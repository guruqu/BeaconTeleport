package fun.guruqu.portal.beacon;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BeaconUseEvent extends Event {
	Player player;
	BeaconProfile beacon;
	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public BeaconProfile getBeacon() {
		return beacon;
	}

	public void setBeacon(BeaconProfile beacon) {
		this.beacon = beacon;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
