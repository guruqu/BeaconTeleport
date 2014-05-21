package fun.guruqu.portal.structures;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;


import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.blocks.BaseBlock;

import fun.guruqu.portal.PortalBeaconPlugin;

public class BlockManager {
	PortalBeaconPlugin plugin;
	Map<StrippedLocation, Map<String, Object>> locationToKeyValue;
	Map<String, List<StrippedLocation>> keyToLocation;

	public static final String saveFile = "blockstructure.json";

	public BlockManager(PortalBeaconPlugin plugin) {
		locationToKeyValue = new HashMap<>();
		keyToLocation = new HashMap<>();
		this.plugin = plugin;
		loadState();
	}

	public void batchAddKey(CuboidClipboard board, Location location,
			Set<Integer> exclude, Set<Integer> include, String key, Object value) {
		com.sk89q.worldedit.Vector size = board.getSize();
		for (int y = 0; y < size.getY(); y++) {
			for (int x = 0; x < size.getX(); x++) {
				for (int z = 0; z < size.getZ(); z++) {
					BaseBlock block = board
							.getBlock(new com.sk89q.worldedit.Vector(x, y, z));
					if (include != null) {
						if (!include.contains(block.getId()))
							continue;
					} else {
						if (exclude != null) {
							if (exclude.contains(block.getId()))
								continue;
						}
					}
					setBlockMeta(
							new Location(location.getWorld(), location.getX()
									+ x, location.getY() + y, location.getZ()
									+ z), key, value);
				}
			}
		}
	}

	public void batchAddKey(CuboidClipboard board, Location location,
			boolean ignoreAir, String key, Object value) {
		HashSet<Integer> air = new HashSet<>();
		air.add(0);
		batchAddKey(board, location, air, null, key, value);
	}

	public void setBlockMeta(Location location, String key, Object value) {
		StrippedLocation sLocation = new StrippedLocation(location);
		Map<String, Object> keyValue = locationToKeyValue.get(sLocation);
		if (keyValue == null) {
			keyValue = new HashMap<>();
			locationToKeyValue.put(sLocation, keyValue);
		}
		keyValue.put(key, value);

		List<StrippedLocation> locations = keyToLocation.get(key);
		if (locations == null) {
			locations = new Vector<>();
			keyToLocation.put(key, locations);
		}
		locations.add(sLocation);
	}

	public Map<String, Object> getBlockMeta(Location location) {
		return getBlockMeta(new StrippedLocation(location));
	}

	public Map<String, Object> getBlockMeta(StrippedLocation location) {
		if (locationToKeyValue.containsKey(location))
			return locationToKeyValue.get(location);
		return null;
	}

	public List<StrippedLocation> findAllBlockWithKey(String key) {
		return keyToLocation.get(key);
	}

	public void clearBlock(Location location) {
		clearBlock(new StrippedLocation(location));
	}

	public void clearBlock(StrippedLocation location) {
		Map<String, Object> removed = locationToKeyValue.remove(location);
		for (String key : removed.keySet()) {
			List<StrippedLocation> list = keyToLocation.get(key);
			if (list != null) {
				list.remove(key);
			}
		}
	}

	public boolean saveState() {
		try {
			JsonWriter writer = new JsonWriter(new FileOutputStream(new File(
					plugin.getDataFolder(), saveFile)));
			writer.write(locationToKeyValue);
			writer.write(keyToLocation);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean loadState() {
		try {
			File file = new File(plugin.getDataFolder(), saveFile);
			if (!file.exists())
				return true;
			JsonReader reader = new JsonReader(new FileInputStream(file));
			locationToKeyValue = (Map<StrippedLocation, Map<String, Object>>) reader
					.readObject();
			keyToLocation = (Map<String, List<StrippedLocation>>) reader
					.readObject();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
