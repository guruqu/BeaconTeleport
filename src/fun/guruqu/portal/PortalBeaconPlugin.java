package fun.guruqu.portal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;

import fun.guruqu.portal.beacon.BeaconListener;
import fun.guruqu.portal.beacon.BeaconManager;
import fun.guruqu.portal.economy.EconomyCore;
import fun.guruqu.portal.economy.Economy_Vault;
import fun.guruqu.portal.structures.BlockManager;
import fun.guruqu.portal.structures.BlockStructureListener;
import fun.guruqu.portal.structures.MatchClipBoard;

public class PortalBeaconPlugin extends JavaPlugin {
	public static PortalBeaconPlugin instance;

	public PortalBeaconPlugin() {
		instance = this;
	}

	public int getBeaconPrice(int number) {
		List<Integer> price = getConfig().getIntegerList("beacon.slotPrice");
		if(number>=price.size())
			return price.get(price.size());
		return price.get(number);
	}

	@Override
	public void onDisable() {
		blockManager.saveState();
		beaconManager.saveState();
		getLogger().info("Portal Beacon disabled!");
	}

	public EconomyCore getEconomy() {
		return economy;
	}

	@Override
	public void onEnable() {

		PluginManager pm = getServer().getPluginManager();

//		pm.registerEvents(new DebugListener(this), this);
		pm.registerEvents(new BlockStructureListener(blockManager, this), this);
		pm.registerEvents(
				new BeaconListener(beaconManager, blockManager, this), this);
		pm.registerEvents(new TeleportBookListener(this), this);

		getCommand("pb").setExecutor(new DebugCommand(this));
		getLogger().info("Portal Beacon enabled!");
		blockManager.loadState();
		beaconManager.loadState();
		economy = new Economy_Vault();
		
		MessageUtil.loadCfgMessages();
	}

	BlockManager blockManager;
	BeaconManager beaconManager;
	EconomyCore economy;


	public BeaconManager getBeaconManager() {
		return beaconManager;
	}

	HashMap<String, MatchClipBoard> structureLibrary;

	public BlockManager getBlockManager() {
		return blockManager;
	}

	public void setBlockManager(BlockManager blockManager) {
		this.blockManager = blockManager;
	}

	public HashMap<String, MatchClipBoard> getStructureLibrary() {
		return structureLibrary;
	}

	public void setStructureLibrary(
			HashMap<String, MatchClipBoard> structureLibrary) {
		this.structureLibrary = structureLibrary;
	}

	public Vector<MatchClipBoard> getStructureTrigger() {
		return structureTrigger;
	}

	public void setStructureTrigger(Vector<MatchClipBoard> structureTrigger) {
		this.structureTrigger = structureTrigger;
	}

	Vector<MatchClipBoard> structureTrigger;

	private MatchClipBoard loadSchematic(String file, String name) {
		File input = new File(getDataFolder(), file);
		if(!input.exists()){
			saveResource(file, false);
		}

		try {
			CuboidClipboard clipboard;
			clipboard = MCEditSchematicFormat.MCEDIT.load(input);
			MatchClipBoard mc = new MatchClipBoard(name, clipboard, true);
			structureLibrary.put(name, mc);
			return mc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onLoad() {
		getLogger().info("Portal Beacon Loaded");
		saveDefaultConfig();
		reloadConfig();
		structureTrigger = new Vector<>();
		structureLibrary = new HashMap<>();

		loadSchematic(getConfig().getString("beacon.activeSchema"), "tp_beacon_active");
		loadSchematic(getConfig().getString("beacon.craftSchema"), "tp_beacon_orig");

		structureTrigger.add(structureLibrary.get("tp_beacon_orig"));
		
		blockManager = new BlockManager(this);
		beaconManager = new BeaconManager(blockManager, this);
	}
	
	
	/** Reloads QuickShops config */
	@Override
	public void reloadConfig(){
		super.reloadConfig();

		MessageUtil.loadCfgMessages();
	}

}
