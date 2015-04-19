package backcab.RandomTP;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PreTP {
	
	private RandomTP rtp;
	private PluginFile config;

	protected void start(RandomTP rtp, Player p, TeleportType type) {
		this.rtp = rtp;
		config = rtp.config();
		
		//check world
		if(type.equals(TeleportType.SELF) && !validWorld(p.getWorld().getName())){
			rtp.file(p.getName() + ": invalid world: " + p.getWorld().getName());
			p.sendMessage(ChatColor.RED + "RandomTP is not available in this world");
			return;
		}
		
		if(type.equals(TeleportType.SELF) && !validPosition(p.getLocation(), p.isFlying())){
			rtp.file(p.getName() + ": invalid location");
			p.sendMessage(ChatColor.RED + "Cannot teleport from this location. Please be sure you are not falling, jumping, or swimming");
			return;
		}
			
		sendTP(type, p.getName());
		
	}
	
	private boolean validWorld(String world){
		
		List<String> worlds = config.getConfig().getStringList("valid_worlds");
		for(int i = 0; i < worlds.size(); i++){
			if(worlds.get(i).startsWith("$")){
				worlds.set(i, worlds.get(i).substring(1));
			}
		}
		
		rtp.file("valid worlds: " + worlds);
		rtp.file("current world: " + world);
		
		if(worlds.contains(world)){
			return true;
		}
		
		return false;
	}
	
	private boolean validPosition(Location loc, boolean flying){
		if(config.getConfig().getBoolean("anticheat") == true && 
			(loc.getBlock().getType().equals(Material.STATIONARY_LAVA) || 
			loc.getBlock().getType().equals(Material.STATIONARY_WATER) || 
		    (loc.subtract(0, 1, 0).getBlock().getType().equals(Material.AIR) && flying == false))){
			
			return false;
		}
		
		return true;
	}

	private void sendTP(TeleportType type, String name){
		
		boolean rand = (Boolean)parse("random_world", Boolean.FALSE, "Invalid value for random_world. Defaulting to false.");
		List<String> worlds = config.getConfig().getStringList("valid_worlds");
		
		int maxX = (Integer)parse("radius.max_X", 1000, "Invalid value for max_X. Defaulting to 1000");
		int maxZ = (Integer)parse("radius.max_Z", 1000, "Invalid value for max_Z. Defaulting to 1000");
		int minX = (Integer)parse("radius.min_X", 0, "Invalid value for min_X. Defaulting to 0");
		int minZ = (Integer)parse("radius.min_Z", 0, "Invalid value for min_Z. Defaulting to 0");
		
		boolean message = (Boolean)parse("send_message_on_tp", Boolean.FALSE, "Invalid value for send_message_on_tp");
		
		double price = (Double)parse("price", 0.0, "Invalid value for price. Defaulting to 0.0");
		int cooldown = (Integer)parse("cooldown", 0, "Invalid value for cooldown. Defaulting to 0");
		
		String section = type.toString().toLowerCase();
		boolean priceEnabled = (Boolean)parse(section + ".price", Boolean.FALSE, "Invalid value for " + section + ".price. Defaulting to false");
		boolean cooldownEnabled = (Boolean)parse(section + ".cooldown", Boolean.FALSE, "Invalid value for " + section + ".cooldown. Defaulting to false");
		
		List<String> biomes = config.getConfig().getStringList("biomes");
		List<String> blocks = config.getConfig().getStringList("blocks");
		
		boolean usingTowny = (Boolean)parse("towny", Boolean.FALSE, "Invalid value for towny. Defaulting to false.");
		boolean usingFactions = (Boolean)parse("factions", Boolean.FALSE, "Invalid value for factions. Defaulting to false.");
		boolean usingWG = (Boolean)parse("worldguard", Boolean.FALSE, "Invalid value for worldguard. Defaulting to false.");
		boolean usingWB = (Boolean)parse("worldborder", Boolean.FALSE, "Invalid value for worldborder. Defaulting to false.");
		
		Task t = new Task(rand, worlds, maxX, maxZ, minX, minZ, message, price, cooldown, priceEnabled, cooldownEnabled, biomes, blocks, name, usingTowny, usingFactions, usingWG, usingWB);
		
		int id = Bukkit.getScheduler().runTaskTimer(rtp, t, 0, 1).getTaskId();
		
		t.setID(id);
	}
	
	private Object parse(String s, Object o, String warning){
		String thing = config.getConfig().getString(s);
		
		try{
			Integer i = Integer.parseInt(thing);
			if(o instanceof Integer){
				return i;
			}
		} catch (Exception e){}
		
		try{
			Double d = Double.parseDouble(thing);
			if(o instanceof Double){
				return d;
			}
		} catch (Exception e){}
		
		if(thing.equalsIgnoreCase("true")){
			return true; 
		}
		else if(thing.equalsIgnoreCase("false")){
			return false;
		}
		
		rtp.log(Level.SEVERE, warning);
		rtp.file(warning);
		
		config.getConfig().set(s, o);
		try {
			config.save();
		} catch (IOException e) {}
		
		return o;
	}
}
