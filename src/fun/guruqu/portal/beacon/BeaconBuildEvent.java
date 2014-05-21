package fun.guruqu.portal.beacon;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fun.guruqu.portal.structures.StrippedLocation;
import fun.guruqu.portal.structures.StructureBuiltEvent;


public class BeaconBuildEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	StructureBuiltEvent buildEvent;
	String beaconGuid, owner;
	StrippedLocation location;

	public StrippedLocation getLocation() {
		return location;
	}

	public void setLocation(StrippedLocation location) {
		this.location = location;
	}

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

	public void setBuildEvent(StructureBuiltEvent buildEvent) {
		this.buildEvent = buildEvent;
	}

	public StructureBuiltEvent getBuildEvent() {
		return buildEvent;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
