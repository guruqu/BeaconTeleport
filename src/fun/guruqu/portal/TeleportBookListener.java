package fun.guruqu.portal;

import java.util.List;
import java.util.Map;
import java.util.Vector;



import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fun.guruqu.portal.beacon.BeaconProfile;
import fun.guruqu.portal.beacon.BeaconUseEvent;
import fun.guruqu.portal.beacon.UserProfile;
import fun.guruqu.portal.structures.StrippedLocation;


public class TeleportBookListener implements Listener {
	PortalBeaconPlugin plugin;

	public TeleportBookListener(PortalBeaconPlugin plugin) {
		this.plugin = plugin;
	}
	

	static String tpbookname = MessageUtil.getMessage("book_name");

	
	@EventHandler(priority = EventPriority.HIGH)
	public void keepBookOnDeath(PlayerDeathEvent event) {
		ItemStack droppedBook = null;
		for (ItemStack i : event.getDrops()) {
			if (portalBookTag.equals(i.getItemMeta().getDisplayName()))
			{
				droppedBook = i;
				break;
			}
		}
		if (droppedBook != null) {
			event.getDrops().remove(droppedBook);

			PlayerInventory inv = event.getEntity().getInventory();
			for (int i = 0; i < inv.getSize(); i++) {
				if (inv.getItem(i) == null) {
					inv.setItem(i, droppedBook);
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onClickPortal(InventoryClickEvent clickEvent) {
		try {
			if (!tpbookname.equals(clickEvent.getInventory().getName())) {
				return;
			}

			ItemStack clicked = clickEvent.getCurrentItem();
			if (clicked != null) {
				for (String si : clicked.getItemMeta().getLore()) {
					String li = Utils.decode(si);
					if (li == null)
						continue;
					Player player = (Player) clickEvent.getWhoClicked();

					if (li.startsWith("tp:")) {
						// Teleport!
						String guid = li.substring(3);
						BeaconProfile profile = plugin.beaconManager
								.getBeaconProfile().get(guid);

						if (profile != null) {
							StrippedLocation loc = profile.getLocation();
							World world = plugin.getServer()
									.getWorld(loc.world);
							// clickEvent.getWhoClicked().teleport(
							// loc.getRealLocation(world));
							BeaconUseEvent bue = new BeaconUseEvent();
							bue.setBeacon(profile);
							bue.setPlayer(player);
							plugin.getServer().getPluginManager()
									.callEvent(bue);

						}

					} else if (li.startsWith("buy")) {
						// Buy slot
						UserProfile profile = plugin.getBeaconManager()
								.getPlayerProfile(player.getName());
						if (profile.getOwnBeacon().size() >= plugin.getConfig().getInt("beacon.maxBeaconLimit"))
							player.sendMessage(MessageUtil
									.getMessage("report.max_beacon_reached"));

						boolean success = plugin.getEconomy()
								.withdraw(
										player.getName(),
										plugin.getBeaconPrice(profile
												.getBeaconLimit()));
						if (!success) {
							player.sendMessage(MessageUtil
									.getMessage("report.cant_afford"));
						} else {
							profile.setBeaconLimit(profile.getBeaconLimit() + 1);
							player.sendMessage(MessageUtil.getMessage(
									"report.buy_success",
									"" + profile.getBeaconLimit()));
							clickEvent.getView().close();
						}
					}
				}
			}
		} catch (Exception e) {
		}
		clickEvent.getView().close();
		clickEvent.setCancelled(true);
	}
	
	public static String portalBookTag ;

	static{
		portalBookTag = MessageUtil.getMessage("book_name")+"§t§a§g";
	}

	@EventHandler
	public void openPortalBook(PlayerInteractEvent event) {
		if (event.getPlayer().getItemInHand() == null
				|| event.getPlayer().getItemInHand().getItemMeta() == null
				|| !portalBookTag.equals(event.getPlayer().getItemInHand()
						.getItemMeta().getDisplayName()))
			return;
		Block block = event.getClickedBlock();

		Player player = event.getPlayer();
		UserProfile profile = plugin.getBeaconManager().getPlayerProfile(
				player.getName());

		if (block == null) {
			Inventory fakeInventory = plugin.getServer().createInventory(null,
					9 + profile.getBookSize(), tpbookname);

			for (int i = 0; i < 9; i++) {
				if (i < profile.getOwnBeacon().size()) {
					ItemStack owned = new ItemStack(Material.ENCHANTED_BOOK, 1);
					String beaconGuid = profile.getOwnBeacon().get(i);
					BeaconProfile beaconProfile = plugin.beaconManager
							.getBeaconProfile().get(beaconGuid);
					Utils.setName(owned, MessageUtil.getMessage(
							"beacon_sign.name", beaconProfile.getName()),
							MessageUtil.getMessage("beacon_sign.owner",
									beaconProfile.getOwner()), MessageUtil
									.getMessage("beacon_sign.cost", ""
											+ beaconProfile.getCost()), Utils
									.encode("tp:" + beaconGuid));

					fakeInventory.setItem(fakeInventory.getSize() - 1 - i,
							owned);

				} else if (i < profile.getBeaconLimit()) {
					ItemStack owned = new ItemStack(Material.PAPER, 1);
					Utils.setName(owned,
							MessageUtil.getMessage("beacon_book.free_slot"));
					fakeInventory.setItem(fakeInventory.getSize() - 1 - i,
							owned);
				} else {
					ItemStack owned = new ItemStack(Material.DIAMOND, 1);
					Utils.setName(
							owned,
							MessageUtil.getMessage("beacon_book.buy_tag", ""
									+ plugin.getBeaconPrice(i)),
							Utils.encode("buy"));
					fakeInventory.setItem(fakeInventory.getSize() - 1 - i,
							owned);
				}
			}

			List<Object> tbr=new Vector<>();
			for (int i = 0; i < profile.getKnownBeacon().size(); i++) {
				String beaconGuid = profile.getKnownBeacon().get(i);
				BeaconProfile beaconProfile = plugin.beaconManager
						.getBeaconProfile().get(beaconGuid);
				if(beaconProfile==null){
					tbr.add(beaconGuid);
					continue;
				}
				ItemStack otherPortal = new ItemStack(Material.BOOK);
				Utils.setName(otherPortal, MessageUtil.getMessage(
						"beacon_sign.name", beaconProfile.getName()),
						MessageUtil.getMessage("beacon_sign.owner",
								beaconProfile.getOwner()), MessageUtil
								.getMessage("beacon_sign.cost", ""
										+ beaconProfile.getCost()), Utils
								.encode("tp:" + beaconGuid));
				fakeInventory.setItem( i,
						otherPortal);
			}
			
			for (Object oi : tbr) {
				profile.getKnownBeacon().remove(oi);
			}
			// §
			InventoryView view = player.openInventory(fakeInventory);
		} else {
			Map<String, Object> meta = plugin.getBlockManager().getBlockMeta(
					block.getLocation());
			if (meta != null && meta.get("beaconGuid") != null) {
				String guid = meta.get("beaconGuid").toString();
				BeaconProfile beacon = plugin.beaconManager.getBeaconProfile().get(guid);
				if (!beacon.getOwner().equals(player.getName())) {
					if (profile.getKnownBeacon().contains(guid)) {
						profile.getKnownBeacon().remove(guid);
						player.sendMessage(MessageUtil.getMessage("warning.removebeacon_success", beacon.getName()));
					} else {
						if(profile.getKnownBeacon().size()>=profile.getBookSize()){
							player.sendMessage(MessageUtil.getMessage("warning.addbeacon_failed"));
						} else {
							profile.getKnownBeacon().add(guid);
							player.sendMessage(MessageUtil.getMessage(
									"warning.addbeacon_success",
									beacon.getName()));
						}
					}
				}
			}
		}
		event.setCancelled(true);

	}

	@EventHandler
	public void onDragPortal(InventoryDragEvent dragEvent) {
		if (!tpbookname.equals(dragEvent.getInventory().getName())) {
			return;
		}
		dragEvent.setCancelled(true);
	}
}
