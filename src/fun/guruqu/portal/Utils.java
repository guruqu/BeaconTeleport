package fun.guruqu.portal;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sk89q.worldedit.CuboidClipboard;

public class Utils {

	public static void setName(ItemStack item, String name, String... lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		java.util.Vector<String> vlore = new java.util.Vector<>();
		for (String li : lore)
			vlore.add(li);
		meta.setLore(vlore);
		item.setItemMeta(meta);
	}

	public static String encode(String str) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			sb.append("§" + str.charAt(i));
		}
		return sb.toString();
	}

	public static String decode(String str) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			if (i % 2 == 0) {
				if (str.charAt(i) != '§')
					return null;
				else {
					continue;
				}
			}
			sb.append(str.charAt(i));
		}
		return sb.toString();
	}
}
