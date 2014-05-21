package fun.guruqu.portal;

import java.io.File;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class MessageUtil {
	private static YamlConfiguration messages;
	private static HashMap<String, LinkedList<String>> player_messages = new HashMap<String, LinkedList<String>>();
	static PortalBeaconPlugin plugin;
	static {
		plugin = PortalBeaconPlugin.instance;
	}

	/**
	 * Loads all the messages from messages.yml
	 */
	public static void loadCfgMessages() {
		// Load messages.yml
		File messageFile = new File(plugin.getDataFolder(), "messages.yml");
		if (!messageFile.exists()) {
			plugin.getLogger().info("Creating messages.yml");
			plugin.saveResource("messages.yml", true);
		}

		// Store it
		messages = YamlConfiguration.loadConfiguration(messageFile);
		messages.options().copyDefaults(true);

		// Load default messages
		InputStream defMessageStream = plugin.getResource("messages.yml");
		YamlConfiguration defMessages = YamlConfiguration
				.loadConfiguration(defMessageStream);
		messages.setDefaults(defMessages);

		// Parse colour codes
		parseColours(messages);
	}

	public static void parseColours(YamlConfiguration config){
		Set<String> keys = config.getKeys(true);

		for(String key : keys){
			String filtered = config.getString(key);
			if(filtered.startsWith("MemorySection")){
				continue;
			}
			filtered = ChatColor.translateAlternateColorCodes('&', filtered);
			config.set(key, filtered);
		}
	}

	public static String getMessage(String loc, String... args) {
		String raw = messages.getString(loc);

		if (raw == null || raw.isEmpty()) {
			return "Invalid message: " + loc;
		}
		if (args == null) {
			return raw;
		}

		for (int i = 0; i < args.length; i++) {
			raw = raw.replace("{" + i + "}", args[i]);
		}
		return raw;
	}
}