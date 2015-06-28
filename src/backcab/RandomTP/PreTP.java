package backcab.RandomTP;
        
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
       
public class PreTP{
	private RandomTP rtp;
	private PluginFile config;
         
	protected void start(RandomTP rtp, Player p, TeleportType type){
		this.rtp = rtp;
		this.config = rtp.config();
		if ((type.equals(TeleportType.SELF)) && (!validWorld(p.getWorld().getName()))){
			rtp.file(p.getName() + ": invalid world: " + p.getWorld().getName());
			p.sendMessage(ChatColor.RED + "RandomTP is not available in this world");
			return;
		}
		if ((type.equals(TeleportType.SELF)) && (!validPosition(p.getLocation(), p.isFlying()))){
			rtp.file(p.getName() + ": invalid location");
			p.sendMessage(ChatColor.RED + "Cannot teleport from this location. Please be sure you are not falling, jumping, or swimming");
			return;
		}
		sendTP(type, p.getUniqueId());
	}
         
	private boolean validWorld(String world){
		List<String> worlds = this.config.getConfig().getStringList("valid_worlds");
		for (int i = 0; i < worlds.size(); i++) {
			if (((String)worlds.get(i)).startsWith("$")) {
				worlds.set(i, ((String)worlds.get(i)).substring(1));
			}
		}
		this.rtp.file("valid worlds: " + worlds);
		this.rtp.file("current world: " + world);
		if (worlds.contains(world)) {
			return true;
		}
		return false;
	}
	
	private boolean validPosition(Location loc, boolean flying){
		if ((this.config.getConfig().getBoolean("anticheat")) && (
			(loc.getBlock().getType().equals(Material.STATIONARY_LAVA)) || 
			(loc.getBlock().getType().equals(Material.STATIONARY_WATER)) || (
			(loc.subtract(0.0D, 1.0D, 0.0D).getBlock().getType().equals(Material.AIR)) && (!flying)))) {
			return false;
		}
		return true;
	}
         
	private void sendTP(TeleportType type, UUID uuid){
		boolean rand = ((Boolean)parse("random_world", Boolean.FALSE, "Invalid value for random_world. Defaulting to false.")).booleanValue();
		List<String> worlds = this.config.getConfig().getStringList("valid_worlds");
           
		int maxX = ((Integer)parse("radius.max_X", Integer.valueOf(1000), "Invalid value for max_X. Defaulting to 1000")).intValue();
		int maxZ = ((Integer)parse("radius.max_Z", Integer.valueOf(1000), "Invalid value for max_Z. Defaulting to 1000")).intValue();
		int minX = ((Integer)parse("radius.min_X", Integer.valueOf(0), "Invalid value for min_X. Defaulting to 0")).intValue();
		int minZ = ((Integer)parse("radius.min_Z", Integer.valueOf(0), "Invalid value for min_Z. Defaulting to 0")).intValue();
           
		boolean message = ((Boolean)parse("send_message_on_tp", Boolean.FALSE, "Invalid value for send_message_on_tp")).booleanValue();
		
		double price = ((Double)parse("price", Double.valueOf(0.0D), "Invalid value for price. Defaulting to 0.0")).doubleValue();
		int cooldown = ((Integer)parse("cooldown", Integer.valueOf(0), "Invalid value for cooldown. Defaulting to 0")).intValue();
		
		String section = type.toString().toLowerCase();
		boolean priceEnabled = ((Boolean)parse(section + ".price", Boolean.FALSE, "Invalid value for " + section + ".price. Defaulting to false")).booleanValue();
		boolean cooldownEnabled = ((Boolean)parse(section + ".cooldown", Boolean.FALSE, "Invalid value for " + section + ".cooldown. Defaulting to false")).booleanValue();
		
		List<String> biomes = this.config.getConfig().getStringList("biomes");
		List<String> blocks = this.config.getConfig().getStringList("blocks");
		
		boolean usingTowny = ((Boolean)parse("towny", Boolean.FALSE, "Invalid value for towny. Defaulting to false.")).booleanValue();
		boolean usingFactions = ((Boolean)parse("factions", Boolean.FALSE, "Invalid value for factions. Defaulting to false.")).booleanValue();
		boolean usingWG = ((Boolean)parse("worldguard", Boolean.FALSE, "Invalid value for worldguard. Defaulting to false.")).booleanValue();
		boolean usingWB = ((Boolean)parse("worldborder", Boolean.FALSE, "Invalid value for worldborder. Defaulting to false.")).booleanValue();
           
		Task t = new Task(rand, worlds, maxX, maxZ, minX, minZ, message, price, cooldown, priceEnabled, cooldownEnabled, biomes, blocks, uuid, usingTowny, usingFactions, usingWG, usingWB);
        
		int id = Bukkit.getScheduler().runTaskTimer(this.rtp, t, 0L, 1L).getTaskId();
           
		t.setID(id);
	}
        
	private Object parse(String s, Object o, String warning){
		String thing = this.config.getConfig().getString(s);
		try{
			Integer i = Integer.valueOf(Integer.parseInt(thing));
			if ((o instanceof Integer)) {
				return i;
			}
		}
		catch (Exception localException){
			try{
				Double d = Double.valueOf(Double.parseDouble(thing));
				if ((o instanceof Double)) {
					return d;
				}
			}
			catch (Exception localException1){
				if (thing.equalsIgnoreCase("true")) {
					return Boolean.valueOf(true);
				}
				if (thing.equalsIgnoreCase("false")) {
					return Boolean.valueOf(false);
				}
				this.rtp.log(Level.SEVERE, warning);
				this.rtp.file(warning);
              
				this.config.getConfig().set(s, o);
				try{
					this.config.save();
				}
				catch (IOException localIOException) {}
			}
		}
		return o;
	}
}