package fun.guruqu.portal.structures;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;

public class MatchClipBoard_ArtisticVersion {
	CuboidClipboard clipBoard;

	public CuboidClipboard getClipBoard() {
		return clipBoard;
	}

	HashMap<Integer, List<int[]>> blockSet;
	List<int[][]> allowTransform;
	
	static int[][] mult(int[][] m1, int[][] m2) {
		int m3[][] = new int[4][4];

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 4; k++) {
					m3[i][j] += m1[i][k] * m2[k][j];
				}
			}
		}
		return m3;
	}
	
	static int[] apply(int[] v,int[][] m){
		int[] r = new int[3];
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				r[i]+=v[j]*m[i][j];
			}
		}
		return r;
	}
	
	public static void main(String s[]){
		
	}
	
	public static int QuickSin(int angle) {
		angle = ((angle % 360) + 360) % 360;
		switch (angle) {
		case 0:
			return 0;
		case 90:
			return 1;
		case 180:
			return 0;
		default:
			return -1;
		}
	}

	public static int QuickCos(int angle) {
		angle = ((angle % 360) + 360) % 360;
		switch (angle) {
		case 0:
			return 1;
		case 90:
			return 0;
		case 180:
			return -1;
		default:
			return 0;
		}
	}
	
	static final int[][][] rotate = new int[][][]{
			{
				{1,	0, 0, 0},
				{0, 1, 0, 0},
				{0, 0, 1, 0},
				{0, 0, 0, 1}
			},
			{
				{0, 0,-1, 0},
				{0, 1, 0, 0},
				{1, 0, 0, 0},
				{0, 0, 0, 1}
			},
			{
				{-1,0, 0, 0},
				{0, 1, 0, 0},
				{0, 0,-1, 0},
				{0, 0, 0, 1}
			},
			{
				{0,	0, 1, 0},
				{0, 1, 0, 0},
				{-1,0, 0, 0},
				{0, 0, 0, 1}
			}
	};
	
	static final int[][][] flip = new int[][][]{
			{
				{1,	0, 0, 0},
				{0, 1, 0, 0},
				{0, 0, 1, 0},
				{0, 0, 0, 1}
			},
			{
				{-1,0, 0, 0},
				{0, 1, 0, 0},
				{0, 0, 1, 0},
				{0, 0, 0, 1}
			},
			{
				{-1,0, 0, 0},
				{0, 1, 0, 0},
				{0, 0, -1,0},
				{0, 0, 0, 1}
			},
			{
				{1,	0, 0, 0},
				{0, 1, 0, 0},
				{0, 0, -1,0},
				{0, 0, 0, 1}
			},
	};
	
	static int[][] translate(int x,int y,int z){
		int[][] m = new int[][]{
			{1,	0, 0, x},
			{0, 1, 0, y},
			{0, 0, 1, z},
			{0, 0, 0, 1}
		};
		return m;
	}
	
	public MatchClipBoard_ArtisticVersion(CuboidClipboard clipBoard, boolean allowFlip) {
		this.clipBoard = clipBoard;
		
	}

	public boolean Match(World world, int[][] transform) {
		Vector size = clipBoard.getSize();
		for (int x = 0; x < size.getBlockX(); x++)
			for (int y = 0; y < size.getBlockY(); y++)
				for (int z = 0; z < size.getBlockZ(); z++) {
					int[] np = apply(new int[] { x, y, z, 1 }, transform);
					if (world.getBlockAt(np[0], np[1], np[2]).getTypeId() != clipBoard
							.getBlock(new Vector(x, y, z)).getId())
						return false;
				}

		return true;
	}
	
	public void place(World world,int[][] transform){
		Vector size = clipBoard.getSize();
				
		
		for (int x = 0; x < size.getBlockX(); x++)
			for (int y = 0; y < size.getBlockY(); y++)
				for (int z = 0; z < size.getBlockZ(); z++) {
					int[] np = apply(new int[] { x, y, z, 1 }, transform);
					
				}
	}
	
	public int[][] Match(World world, Location position) {
		int id = world.getBlockAt(position).getTypeId();
		if (!blockSet.containsKey(id))
			return null;
		List<int[]> cpos = blockSet.get(id);
		for (int[] ci : cpos) {
			for(int i=0;i<rotate.length;i++){
				for(int j=0;j<flip.length;j++){
					int[][] transform=
						mult(
						mult(
						mult(
							translate(-ci[0], -ci[1], -ci[2]), 
							rotate[i]),
							flip[j]),
							translate(position.getBlockX(),
									position.getBlockY(), 
									position.getBlockZ()));
					if(Match(world,transform)){
						// Find lower left corner
						return transform;
					}
				}
			}
			
		}
		return null;
	}
}
