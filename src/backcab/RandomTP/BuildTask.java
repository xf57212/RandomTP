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
		//make sure x1,y1,z1 are less than x2,y2,z2
		this.x1 = Integer.valueOf(Math.min(v1.getBlockX(), v2.getBlockX()));
		this.x2 = Integer.valueOf(Math.max(v1.getBlockX(), v2.getBlockX()));
		this.y1 = Integer.valueOf(Math.min(v1.getBlockY(), v2.getBlockY()));
		this.y2 = Integer.valueOf(Math.max(v1.getBlockY(), v2.getBlockY()));
		this.z1 = Integer.valueOf(Math.min(v1.getBlockZ(), v2.getBlockZ()));
		this.z2 = Integer.valueOf(Math.max(v1.getBlockZ(), v2.getBlockZ()));
		this.world = Bukkit.getWorld(world);
		
		this.x = this.x1.intValue();
		this.y = this.y1.intValue();
		
		this.m = m;
		this.data = data;
		this.old = old;
	}	
 
	@SuppressWarnings("deprecation")
	public void run(){
		for (int z = this.z1.intValue(); z <= this.z2.intValue(); z++){
			Block b = new Location(this.world, this.x, this.y, z).getBlock();
			if (b.getType().equals(this.old)){
				b.setType(this.m, false);
				b.setData(this.data);
				b.getState().update(true, false);
			}
		}
		this.y += 1;
		if (this.y > this.y2.intValue()){
			this.y = this.y1.intValue();
			this.x += 1;
		}
		if (this.x > this.x2.intValue()) {
			cancel();
		}
	}

	protected void setID(int id){
		this.id = id;
	}

	private void cancel(){
		Bukkit.getScheduler().cancelTask(this.id);
	}	
}