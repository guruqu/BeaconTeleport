package fun.guruqu.portal.beacon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.meta.FireworkMeta;


import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitUtil;

import fun.guruqu.portal.MessageUtil;
import fun.guruqu.portal.PortalBeaconPlugin;
import fun.guruqu.portal.structures.BlockManager;
import fun.guruqu.portal.structures.StrippedLocation;
import fun.guruqu.portal.structures.StructureBuiltEvent;

public class BeaconManager {

	PortalBeaconPlugin plugin;
	BlockManager blockManager;
	HashMap<String, UserProfile> playerProfiles;
	HashMap<String, BeaconProfile> beaconProfile;

	int beaconSalt = 0;

	public UserProfile getPlayerProfile(String name) {
		UserProfile p = playerProfiles.get(name);
		if (p == null) {
			p = new UserProfile();
			p.setName(name);
			playerProfiles.put(name, p);
		}
		return p;
	}

	public void updateBeaconStatus(String guid) {
		BeaconProfile profile = beaconProfile.get(guid);
		List<StrippedLocation> location = blockManager
				.findAllBlockWithKey("beacon_sign_" + guid);
		if (profile == null || location == null)
			return;

		for (StrippedLocation li : location) {
			World world = plugin.getServer().getWorld(li.world);
			if (world == null)
				continue;

			Block block = li.getRealLocation(world).getBlock();
			if(block==null)
				continue;
			
			BlockState state = block.getState();
			if (state != null && state instanceof Sign) {
				Sign sign = (Sign) state;
				sign.setLine(0, MessageUtil.getMessage("beacon_sign.name", profile.getName()));
				sign.setLine(1, MessageUtil.getMessage("beacon_sign.owner", profile.getOwner()));
				sign.setLine(2, MessageUtil.getMessage("beacon_sign.cost", ""+profile.getCost()));
				sign.setLine(3,  MessageUtil.getMessage("beacon_sign.used_times",""+profile.getUsed()));
				sign.update();
			}
		}
	}

	public String getGuid() {
		return "" + (System.currentTimeMillis() + beaconSalt++);
	}

	public BeaconManager(BlockManager blockManager, PortalBeaconPlugin plugin) {
		this.plugin = plugin;
		this.blockManager = blockManager;
		playerProfiles = new HashMap<>();
		beaconProfile = new HashMap<>();
	}

	public PortalBeaconPlugin getPlugin() {
		return plugin;
	}

	public BlockManager getBlockManager() {
		return blockManager;
	}

	public HashMap<String, UserProfile> getUserProfiles() {
		return playerProfiles;
	}

	public HashMap<String, BeaconProfile> getBeaconProfile() {
		return beaconProfile;
	}

	public static final String saveFile = "beacon.json";

	public boolean saveState() {
		try {
			JsonWriter writer = new JsonWriter(new FileOutputStream(new File(
					plugin.getDataFolder(), saveFile)));
			writer.write(playerProfiles);
			writer.write(beaconProfile);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean informBeaconUsage(String guid) {
		BeaconProfile bf = beaconProfile.get(guid);
		Player player = plugin.getServer().getPlayer(bf.getOwner());
		if (player == null || !player.isOnline())
			return false;
		double earned = (bf.totalEarned - bf.lastInformedEarning);
		if (earned == 0)
			return false;
		player.sendMessage(MessageUtil.getMessage("report.earning",
				bf.getName(), "" + earned));
		bf.lastInformedEarning = bf.totalEarned;
		return true;

	}

	public boolean loadState() {
		try {
			File file = new File(plugin.getDataFolder(), saveFile);
			if (!file.exists())
				return false;

			JsonReader reader = new JsonReader(new FileInputStream(file));
			playerProfiles = (HashMap<String, UserProfile>) reader.readObject();
			beaconProfile = (HashMap<String, BeaconProfile>) reader
					.readObject();

			for (BeaconProfile pi : beaconProfile.values()) {
				this.updateBeaconStatus(pi.getGuid());
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
