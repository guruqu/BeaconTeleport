package fun.guruqu.portal.structures;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.CuboidClipboard.FlipDirection;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.schematic.SchematicFormat;

public class MatchClipBoard {
	List<Transformed> clipBoard;
	String name;

	public static class Transformed {
		public CuboidClipboard clipBoard;
		public HashMap<Integer, List<int[]>> blockSet;
	}

	private CuboidClipboard clone(CuboidClipboard original) {
		try {
			File file = File.createTempFile("_cuboidclipboard", "");
			SchematicFormat.MCEDIT.save(original, file);
			CuboidClipboard bi = SchematicFormat.MCEDIT.load(file);
			return bi;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Transformed> getClipBoard() {
		return clipBoard;
	}

	public MatchClipBoard(String name, CuboidClipboard original,
			boolean allowFlip) {
		clipBoard = new java.util.Vector<>();
		this.name = name;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < (!allowFlip ? 1 : 2); j++) {
				Transformed tf = new Transformed();

				CuboidClipboard copy = clone(original);
				copy.rotate2D(i * 90);
				if (j % 2 == 1)
					copy.flip(FlipDirection.NORTH_SOUTH);

				copy.flip(FlipDirection.WEST_EAST);
				copy.flip(FlipDirection.NORTH_SOUTH);
				tf.clipBoard = copy;
				tf.blockSet = new HashMap<>();

				Vector size = copy.getSize();
				for (int y = 0; y < size.getY(); y++)
					for (int x = 0; x < size.getX(); x++)
						for (int z = 0; z < size.getZ(); z++) {
							Vector vec = new Vector(x, y, z);
							BaseBlock block = copy.getBlock(vec);

							List<int[]> list = tf.blockSet.get(block.getId());
							if (list == null) {
								list = new java.util.Vector<>();
								tf.blockSet.put(block.getId(), list);
							}
							list.add(new int[] { x, y, z });
						}
				clipBoard.add(tf);
			}
		}
	}

	public static void main(String s[]) {

	}

	public boolean Match(World world, Transformed clip, Location worldPosition,
			Vector blockOffset) {
		Vector size = clip.clipBoard.getSize();

		int[] delta = new int[] {
				worldPosition.getBlockX() - blockOffset.getBlockX(),
				worldPosition.getBlockY() - blockOffset.getBlockY(),
				worldPosition.getBlockZ() - blockOffset.getBlockZ() };

		boolean match = true;
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println();

		for (int y = 0; y < size.getY(); y++) {
			for (int x = 0; x < size.getX(); x++) {
				for (int z = 0; z < size.getZ(); z++) {
					int worldId = world.getBlockAt(x + delta[0], y + delta[1],
							z + delta[2]).getTypeId();
					int clipId = clip.clipBoard.getBlock(new Vector(x, y, z))
							.getType();

					// Ignore grass
					if (worldId == Material.GRASS.getId())
						worldId = 0;
					
					if (worldId != clipId) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public StructureBuiltEvent Match(World world, Location position) {
		int id = world.getBlockAt(position).getTypeId();
		if (!clipBoard.get(0).blockSet.containsKey(id))
			return null;

		for (int indx = 0; indx < clipBoard.size(); indx++) {
			List<int[]> cpos = clipBoard.get(indx).blockSet.get(id);
			for (int[] ci : cpos) {
				if (Match(world, clipBoard.get(indx), position, new Vector(
						ci[0], ci[1], ci[2]))) {
					StructureBuiltEvent event = new StructureBuiltEvent();
					event.transformIndex = indx;
					event.offset = new Vector(ci[0], ci[1], ci[2]);
					event.matchedClipBoard = this;
					return event;
				}
			}
		}

		return null;
	}
}
