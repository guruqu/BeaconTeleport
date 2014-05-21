package fun.guruqu.portal.structures;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class StrippedLocation {
	public int x, y, z;
	public String world;
	int hash;

	public StrippedLocation(Location location) {
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();

		// UUID ...
		this.world = location.getWorld().getName();
		hash = x * 112327 + y * 112331 + z * 112337 + world.hashCode();
	}

	public StrippedLocation() {
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StrippedLocation))
			return false;
		StrippedLocation so = (StrippedLocation) obj;
		if (so.x != x || so.y != y || so.z != z)
			return false;
		if (!so.world.equals(world))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	public Location getRealLocation(Server server) {
		return new Location(server.getWorld(world), x, y, z);
	}

	public Location getRealLocation(World world) {
		return new Location(world, x, y, z);
	}
}