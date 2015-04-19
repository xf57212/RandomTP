package backcab.RandomTP;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class BuildTask implements Runnable{
	
	private Integer x1;
	private Integer x2;
	private Integer y1;
	private Integer y2;
	private Integer z1;
	private Integer z2;
	private World world;
	
	private int x;
	private int y;
	
	private Material m;
	private byte data;
	private Material old;
	
	private int id;
	
	public BuildTask(Vector v1, Vector v2, String world, Material m, byte data, Material old){
		x1 = Math.min(v1.getBlockX(), v2.getBlockX());
		x2 = Math.max(v1.getBlockX(), v2.getBlockX());
		y1 = Math.min(v1.getBlockY(), v2.getBlockY());
		y2 = Math.max(v1.getBlockY(), v2.getBlockY());
		z1 = Math.min(v1.getBlockZ(), v2.getBlockZ());
		z2 = Math.max(v1.getBlockZ(), v2.getBlockZ());
		this.world = Bukkit.getWorld(world);
		
		x = x1;
		y = y1;
		
		this.m = m;
		this.data = data;
		this.old = old;
	}
	
	@SuppressWarnings("deprecation")
	public void run() {
		for(int z = z1; z <= z2; z++){
			Block b = new Location(world, x, y, z).getBlock();
			if(b.getType().equals(old)){
				b.setType(m, false);
				b.setData(data);
				b.getState().update(true, false);
			}
		}
		y++;
		
		if(y > y2){
			y = y1;
			x++;
		}
		
		if(x > x2){
			cancel();
		}
	}
	
	protected void setID(int id){
		this.id = id;
	}
	
	private void cancel(){
		Bukkit.getScheduler().cancelTask(id);
	}
}
