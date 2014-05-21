package fun.guruqu.portal;

import java.util.List;

import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;


import com.avaje.ebeaninternal.server.deploy.BeanDescriptor.EntityType;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.schematic.SchematicFormat;

import fun.guruqu.portal.beacon.BeaconBreakEvent;
import fun.guruqu.portal.beacon.BeaconManager;
import fun.guruqu.portal.beacon.BeaconProfile;
import fun.guruqu.portal.beacon.UserProfile;
import fun.guruqu.portal.structures.StrippedLocation;

public class DebugCommand implements CommandExecutor {

	private PortalBeaconPlugin plugin;

	public DebugCommand(PortalBeaconPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!(sender instanceof Player)) {
			plugin.getLogger().warning("Debug command need player");
			return false;
		}

		Player player = (Player) sender;
		if(!player.isOp())
			return false;
		
		plugin.getLogger().info(command.toString());

		if (args[0].equals("box")) {
		}

		if (args[0].equals("sign")) {
		}
		if (args[0].equals("fire")) {
			Firework firework = player.getWorld().spawn(player.getLocation(),
					Firework.class);

			FireworkMeta meta = firework.getFireworkMeta();
			meta.addEffects(FireworkEffect.builder()
					.withColor(Color.RED, Color.PURPLE, Color.WHITE)
					.with(Type.BALL_LARGE).flicker(true).trail(true).build());
			meta.setPower(0);
			firework.setFireworkMeta(meta);
		}
		if (args[0].equals("load")) {
			plugin.blockManager.loadState();
		}
		if (args[0].equals("meta")) {
			ItemStack stack = player.getItemInHand();
			if (stack != null) {
				Utils.setName(stack, TeleportBookListener.portalBookTag, " - Very useful!");
			}
		}
		if(args[0].equals("adminbook")){
			UserProfile adminProfile = plugin.beaconManager.getPlayerProfile(player.getName());
			
			if(args.length>=2&&args[1].equals("clear")){
				adminProfile.getKnownBeacon().clear();
			}else if(args.length>=2&&args[1].equals("all")){
				adminProfile.setBookSize(54);
				for(String user : plugin.getBeaconManager().getUserProfiles().keySet()){
					UserProfile userProfile = plugin.beaconManager.getPlayerProfile(user);
					for(String guid: userProfile.getOwnBeacon()){
						adminProfile.getKnownBeacon().add(guid);
					}
				}
			}else{
				adminProfile.setBookSize(54);
				String pname="";
				if(args.length>=2){
					pname=args[1];
					if(plugin.beaconManager.getUserProfiles().containsKey(pname)){
						UserProfile userProfile = plugin.beaconManager.getPlayerProfile(pname);
						for(String guid: userProfile.getOwnBeacon()){
							adminProfile.getKnownBeacon().add(guid);
						}
					}else{
						player.sendMessage("Cannot find such user with beacon");
					}
				}
			}
		}
		if (args[0].equals("beacon")) {
			plugin.beaconManager.getPlayerProfile(player.getName())
					.setBeaconLimit(5);
		}
		if (args[0].equals("clip")) {
			LocalWorld world = BukkitUtil.getLocalWorld(player.getWorld());

			EditSession session = WorldEdit.getInstance()
					.getEditSessionFactory().getEditSession(world, 1000);
			Location loc = player.getLocation();
			try {
				plugin.getStructureLibrary().get("tp_beacon_orig")
						.getClipBoard().get(0).clipBoard.place(session,
						new Vector(loc.getX(), loc.getY(), loc.getZ()), true);
			} catch (MaxChangedBlocksException e) {
				e.printStackTrace();
				player.sendMessage(e.getMessage());
				return false;
			}
		}
		if (args[0].equals("clear")) {
			String name = player.getName();
			if (args.length >= 2)
				name = args[1];

			UserProfile userProfile = plugin.beaconManager
					.getPlayerProfile(name);
			if (userProfile != null) {
				java.util.Vector<String> guids = new java.util.Vector<>(
						userProfile.getOwnBeacon());

				for (String guid : guids) {
					StrippedLocation beaconBlock = plugin.getBlockManager()
							.findAllBlockWithKey("beacon_" + guid).get(0);
					World world = plugin.getServer()
							.getWorld(beaconBlock.world);
					Block block = world.getBlockAt(beaconBlock.x,
							beaconBlock.y, beaconBlock.z);

					BlockBreakEvent bk = new BlockBreakEvent(block, player);
					plugin.getServer().getPluginManager()
							.callEvent(bk);
				}
			}

		}

		return true;
	}
}
