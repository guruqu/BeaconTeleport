package fun.guruqu.portal.beacon;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;

public class BeaconBreakEvent extends Event {

	
	String beaconGuid, owner;
	BlockBreakEvent blockBreakEvent;


	public String getBeaconGuid() {
		return beaconGuid;
	}


	public void setBeaconGuid(String beaconGuid) {
		this.beaconGuid = beaconGuid;
	}


	public String getOwner() {
		return owner;
	}


	public void setOwner(String owner) {
		this.owner = owner;
	}


	public BlockBreakEvent getBlockBreakEvent() {
		return blockBreakEvent;
	}


	public void setBlockBreakEvent(BlockBreakEvent blockBreakEvent) {
		this.blockBreakEvent = blockBreakEvent;
	}

	private static final HandlerList handlers = new HandlerList();
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
