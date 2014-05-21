package fun.guruqu.portal.structures;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockPlaceEvent;

import com.sk89q.worldedit.Vector;

public class StructureBuiltEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	int transformIndex;
	MatchClipBoard matchedClipBoard;
	Vector offset;
	BlockPlaceEvent lastBlockEvent;

	public StructureBuiltEvent() {
	}

	public int getTransformIndex() {
		return transformIndex;
	}

	public MatchClipBoard getMatchedClipBoard() {
		return matchedClipBoard;
	}

	public Vector getOffset() {
		return offset;
	}

	public BlockPlaceEvent getLastBlockEvent() {
		return lastBlockEvent;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	boolean cancel;

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
