package fun.guruqu.portal.beacon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitUtil;

import fun.guruqu.portal.MessageUtil;
import fun.guruqu.portal.PortalBeaconPlugin;
import fun.guruqu.portal.economy.EconomyCore;
import fun.guruqu.portal.structures.BlockManager;
import fun.guruqu.portal.structures.StrippedLocation;
import fun.guruqu.portal.structures.StructureBuiltEvent;


public class BeaconListener implements Listener {
	BeaconManager beaconManager;
	BlockManager blockManager;
	PortalBeaconPlugin plugin;

	public BeaconListener(BeaconManager beaconManager,
			BlockManager blockManager, PortalBeaconPlugin plugin) {
		this.beaconManager = beaconManager;
		this.plugin = plugin;
		this.blockManager = blockManager;
		isNameChanging = new HashMap<>();
		isPriceChanging = new HashMap<>();
	}

	public static final Material beacon_namechange = Material.BOOK_AND_QUILL;
	public static final Material beacon_pricechange = Material.DIAMOND;

	HashMap<String, BeaconProfile> isNameChanging, isPriceChanging;

	@EventHandler
	public void onUserLogin(PlayerLoginEvent event) {
		String name = event.getPlayer().getName();
		for (String guid : beaconManager.getPlayerProfile(name).ownBeacon) {
			beaconManager.informBeaconUsage(guid);
		}
	}

	class Teleport implements Runnable {
		BeaconUseEvent event;
		float speed;

		public Teleport(BeaconUseEvent event,float speed) {
			this.event = event;
			this.speed=speed;
		}

		public void run() {
			if(speed==0)
				speed=1;
			Location loc = event.beacon.getLocation().getRealLocation(
					plugin.getServer());
			Location curLoc = event.getPlayer().getLocation().clone();
			event.getPlayer().setWalkSpeed((float)speed);
			event.getPlayer().teleport(loc);
			event.getPlayer().removePotionEffect(PotionEffectType.CONFUSION);

			World world = plugin.getServer().getWorld(
					event.beacon.getLocation().world);

			event.getPlayer().sendMessage(MessageUtil.getMessage("warning.teleport_success"));
			world.strikeLightningEffect(curLoc);
			loc.getWorld().strikeLightningEffect(loc);
		}
	}

	@EventHandler
	public void onBeaconUse(BeaconUseEvent event) {
		EconomyCore economy = plugin.getEconomy();
		if (economy.isValid()) {
			String playerName = event.getPlayer().getName();
			String owner = event.beacon.getOwner();
			boolean transfer = economy.transfer(playerName, owner, event
					.getBeacon().getCost());

			if (!owner.equals(event.getPlayer().getName())
					&& event.getBeacon().getCost() > 0 && !transfer) {
				event.player.sendMessage(MessageUtil.getMessage(
						"warning.not_enough_money", ""
								+ event.getBeacon().getCost()));
				return;
			}
		}
		event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100000, 5));
		event.beacon.used++;
		event.beacon.totalEarned += event.beacon.cost;
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Teleport(event,event.getPlayer().getWalkSpeed()), 100);
		event.getPlayer().sendMessage(MessageUtil.getMessage("warning.teleport_starting"));
		event.getPlayer().setWalkSpeed(0.05f);
		beaconManager.updateBeaconStatus(event.beacon.getGuid());
		beaconManager.informBeaconUsage(event.getBeacon().getGuid());
	}

	@EventHandler
	public void onBeaconChangeChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		if (isNameChanging.containsKey(player.getName())) {
			String newName = event.getMessage();
			if (newName.length() > 10) {
				player.sendMessage(MessageUtil.getMessage(
						"warning.name_too_long", "" + 10));
			} else {
				isNameChanging.get(player.getName()).setName(newName);
				beaconManager.updateBeaconStatus(isNameChanging.get(
						player.getName()).getGuid());
				beaconManager.saveState();
				player.sendMessage(MessageUtil.getMessage(
						"warning.name_changed", newName));
			}
			isNameChanging.remove(player.getName());
		}
		if (isPriceChanging.containsKey(player.getName())) {
			String newPrice = event.getMessage();
			try {
				int price = Integer.parseInt(newPrice);
				if (price < 0)
					throw new RuntimeException();
				isPriceChanging.get(player.getName()).setCost(price);
				beaconManager.updateBeaconStatus(isPriceChanging.get(
						player.getName()).getGuid());
				beaconManager.saveState();
				player.sendMessage(MessageUtil.getMessage(
						"warning.price_changed", "" + price));
			} catch (Exception e) {
				player.sendMessage(MessageUtil.getMessage(
						"warning.price_change_failed", "" + newPrice));
			}

			isPriceChanging.remove(player.getName());
		}

	}

	@EventHandler
	public void onBeaconChange(PlayerInteractEvent event) {
		BlockFace blockFace = event.getBlockFace();
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		if (block == null) {
			return;
		}

		Map<String, Object> meta = blockManager.getBlockMeta(block
				.getLocation());
		if (meta == null)
			return;

		if (meta.get("beaconGuid") == null)
			return;
		String beaconGuid = meta.get("beaconGuid").toString();

		BeaconProfile beaconProfile = beaconManager.getBeaconProfile().get(
				beaconGuid);
		if (beaconProfile == null)
			return;
		if (!player.getName().equals(beaconProfile.owner) && !player.isOp()) {
			player.sendMessage(MessageUtil.getMessage(
					"warning.not_allowed_touch", beaconProfile.getOwner()));
			return;
		}

		if (player.getItemInHand() == null)
			return;

		if (player.getItemInHand().getType() == beacon_namechange) {
			// name change
			player.sendMessage(MessageUtil.getMessage("report.input_name",
					beaconProfile.getName()));
			isNameChanging.put(player.getName(), beaconProfile);
		} else if (player.getItemInHand().getType() == beacon_pricechange) {
			player.sendMessage(MessageUtil.getMessage("report.input_price",
					beaconProfile.getName()));
			isPriceChanging.put(player.getName(), beaconProfile);
		}
		beaconManager.updateBeaconStatus(beaconProfile.getGuid());
	}

	@EventHandler
	public void onBeaconBuilt(BeaconBuildEvent event) {
		Player player = event.getBuildEvent().getLastBlockEvent().getPlayer();

		BeaconProfile profile = new BeaconProfile();
		profile.setCost(0);
		profile.setGuid(event.getBeaconGuid());
		profile.setLocation(event.getLocation());
		profile.setName(MessageUtil.getMessage("beacon_sign.default_name",
				player.getName()));
		profile.setOwner(player.getName());
		profile.setPrivateOwn(false);

		beaconManager.getBeaconProfile().put(profile.getGuid(), profile);
		beaconManager.getPlayerProfile(player.getName()).ownBeacon.add(profile
				.getGuid());
		beaconManager.updateBeaconStatus(profile.getGuid());
		beaconManager.saveState();
	}

	@EventHandler
	public void onBeaconDestroy(BeaconBreakEvent event) {
		Player player = event.getBlockBreakEvent().getPlayer();

		BeaconProfile profile = beaconManager.getBeaconProfile().get(
				event.getBeaconGuid());
		if (profile == null)
			return;

		beaconManager.getBeaconProfile().remove(event.getBeaconGuid());
		UserProfile playerProfile = beaconManager.getPlayerProfile(player
				.getName());
		playerProfile.ownBeacon.remove(event.getBeaconGuid());
		
		for(UserProfile pi : beaconManager.playerProfiles.values()){
			pi.knownBeacon.remove(event.getBeaconGuid());
		}
		beaconManager.saveState();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBeaconBlockDestroy(BlockBreakEvent event) {
		if (event.isCancelled())
			return;
		Location loc = event.getBlock().getLocation();
		if (blockManager.getBlockMeta(loc) == null
				|| !blockManager.getBlockMeta(loc).containsKey("beacon"))
			return;
		// None of my bussiness

		String owner = (String) blockManager.getBlockMeta(loc).get(
				"beaconOwner");
		String guid = (String) blockManager.getBlockMeta(loc).get("beaconGuid");
		if (event.getPlayer() == null) {
			// wtf natural evetn??
			event.setCancelled(true);
			return;
		}

		if (!event.getPlayer().isOp()
				&& !event.getPlayer().getName().equals(owner)) {
			event.getPlayer().sendMessage(
					MessageUtil.getMessage("warning.not_allowed_touch", owner));
			event.setCancelled(true);
			return;
		}

		// legit
		List<StrippedLocation> beaconBlocks = blockManager
				.findAllBlockWithKey("beacon_" + guid);
		World thisWorld = event.getPlayer().getWorld();

		Vector<StrippedLocation> removeList = new Vector<>();
		for (StrippedLocation si : beaconBlocks) {
			Block block = thisWorld.getBlockAt(si.x, si.y, si.z);
			if (block.getType() == Material.SIGN
					|| block.getType() == Material.SIGN_POST
					|| block.getType() == Material.WALL_SIGN) {
				// block.setType(Material.AIR);
			} else {
				block.setType(Material.STONE);
			}

			removeList.add(si);
		}
		for (StrippedLocation oi : removeList) {
			blockManager.clearBlock(oi);
		}
		event.setCancelled(true);

		BeaconBreakEvent beaconEvent = new BeaconBreakEvent();
		beaconEvent.beaconGuid = guid;
		beaconEvent.owner = owner;
		beaconEvent.blockBreakEvent = event;

		plugin.getServer().getPluginManager().callEvent(beaconEvent);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBeaconStructure(StructureBuiltEvent event) {
		try {
			Player player = event.getLastBlockEvent().getPlayer();
			if (event.isCancelled()) {
				player.sendMessage("Structure Built, but was canceled!");
				return;
			}
			if (!event.getMatchedClipBoard().getName().equals("tp_beacon_orig")) {
				// None of my bussiness
				return;
			}

			// Check beacon permission and limits
			UserProfile userProfile = beaconManager.getPlayerProfile(player
					.getName());
			if (userProfile.ownBeacon.size() >= userProfile.beaconLimit) {
				player.sendMessage(MessageUtil.getMessage(
						"warning.beacon_over_budget", ""
								+ userProfile.ownBeacon.size()));
				return;
			}

			// check if within another beacon's protect range
			if (beaconManager.isBeaconInRange(event.getLastBlockEvent().getBlock()
					.getLocation(), 100)) 
			{
				player.sendMessage(MessageUtil.getMessage(
						"warning.beacon_too_close", ""));
				return;
			}


			// finally it works!
			Firework firework = player.getWorld().spawn(player.getLocation(),
					Firework.class);

			FireworkMeta meta = firework.getFireworkMeta();
			meta.addEffects(FireworkEffect.builder()
					.withColor(Color.RED, Color.PURPLE, Color.WHITE)
					.with(Type.BALL_LARGE).flicker(true).trail(true).build());
			meta.setPower(0);
			firework.setFireworkMeta(meta);

			Location lastBlock = event.getLastBlockEvent().getBlock()
					.getLocation();
			com.sk89q.worldedit.Vector offset = event.getOffset();

			LocalWorld world = BukkitUtil.getLocalWorld(player.getWorld());

			EditSession session = WorldEdit.getInstance()
					.getEditSessionFactory().getEditSession(world, 1000);

			com.sk89q.worldedit.Vector placeLoc = new com.sk89q.worldedit.Vector(
					lastBlock.getX() - offset.getBlockX(), lastBlock.getY()
							- offset.getBlockY(), lastBlock.getZ()
							- offset.getBlockZ());

			CuboidClipboard activeBeacon = plugin.getStructureLibrary()
					.get("tp_beacon_active").getClipBoard()
					.get(event.getTransformIndex()).clipBoard;

			activeBeacon.place(session, placeLoc, false);

			Location placeWorldLoc = new Location(player.getWorld(),
					placeLoc.getBlockX(), placeLoc.getBlockY(),
					placeLoc.getBlockZ());

			String guid = beaconManager.getGuid();
			blockManager.batchAddKey(activeBeacon, placeWorldLoc, true,
					"beacon", "");
			blockManager.batchAddKey(activeBeacon, placeWorldLoc, true,
					"beacon_" + guid, "");
			blockManager.batchAddKey(activeBeacon, placeWorldLoc, true,
					"beaconOwner", player.getName());
			blockManager.batchAddKey(activeBeacon, placeWorldLoc, true,
					"beaconGuid", guid);

			HashSet<Integer> sign = new HashSet<>();
			sign.add(Material.SIGN.getId());
			sign.add(Material.WALL_SIGN.getId());
			sign.add(Material.SIGN_POST.getId());
			blockManager.batchAddKey(activeBeacon, placeWorldLoc, null, sign,
					"beacon_sign_" + guid, "");
			blockManager.saveState();

			BeaconBuildEvent beaconEvent = new BeaconBuildEvent();
			beaconEvent.beaconGuid = guid;
			beaconEvent.owner = player.getName();
			beaconEvent.buildEvent = event;
			beaconEvent.location = new StrippedLocation();
			beaconEvent.location.x = placeLoc.getBlockX() + 1;
			beaconEvent.location.y = placeLoc.getBlockY() + 5;
			beaconEvent.location.z = placeLoc.getBlockZ() + 1;
			beaconEvent.location.world = player.getWorld().getName();

			plugin.getServer().getPluginManager().callEvent(beaconEvent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
