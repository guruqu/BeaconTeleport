package fun.guruqu.portal;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.MetadataValue;


import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitUtil;

import fun.guruqu.portal.beacon.BeaconManager;
import fun.guruqu.portal.beacon.BeaconProfile;
import fun.guruqu.portal.beacon.BeaconUseEvent;
import fun.guruqu.portal.beacon.UserProfile;
import fun.guruqu.portal.structures.StrippedLocation;

public class DebugListener implements Listener {

	PortalBeaconPlugin plugin;

	public DebugListener(PortalBeaconPlugin plugin) {
		this.plugin = plugin;
	}


	@EventHandler
	public void onBlockDamage(BlockDamageEvent blockEvent) {
		plugin.getLogger().info(blockEvent.toString());
		final Player player = blockEvent.getPlayer();
		Block block = blockEvent.getBlock();

		List<MetadataValue> values = block.getMetadata("lastplayer");
		if (values == null)
			values = new Vector<>();

		player.sendMessage("Last Player hit this block: " + values.toString());

		MetadataValue newValue = new LazyMetadataValue(plugin,
				new Callable<Object>() {

					@Override
					public String call() throws Exception {
						return player.getName();
					}
				});
		block.setMetadata("lastplayer", newValue);
	}

}
