package fun.guruqu.portal.structures;

import java.util.List;
import java.util.Vector;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.FireworkMeta;


import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitUtil;

import fun.guruqu.portal.PortalBeaconPlugin;

public class BlockStructureListener implements Listener {
	BlockManager blockManager;
	PortalBeaconPlugin plugin;

	public BlockStructureListener(BlockManager blockManager,
			PortalBeaconPlugin plugin) {
		this.plugin = plugin;
		this.blockManager = blockManager;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent blockEvent) {
	//	plugin.getLogger().info(blockEvent.toString());
		final Player player = blockEvent.getPlayer();
		Block block = blockEvent.getBlock();

		Block wb = player.getWorld().getBlockAt(block.getLocation());
//		System.out.println(wb);

		for (MatchClipBoard mi : plugin.getStructureTrigger()) {
			StructureBuiltEvent value = mi.Match(player.getWorld(),
					wb.getLocation());
			if (value != null) {
				value.lastBlockEvent = blockEvent;
				plugin.getServer().getPluginManager().callEvent(value);
			}
		}
	}
}
