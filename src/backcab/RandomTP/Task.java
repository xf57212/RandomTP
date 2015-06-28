package backcab.RandomTP;
       
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.ps.PS;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.Config;

public class Task
implements Runnable{
	private boolean rand;
	private boolean message;
	private boolean priceEnabled;
	private boolean cooldownEnabled;
	private boolean usingTowny;
	private boolean usingFactions;
	private boolean usingWG;
	private boolean usingWB;
	private List<String> worldList;
	private List<String> biomes;
	private List<String> blocks;
	private int maxX;
	private int maxZ;
	private int minX;
	private int minZ;
	private int cooldown;
	private double price;
	private UUID id;
	private static HashMap<UUID, Integer> players;
	private static HashMap<UUID, Long> cooldowns;
	private static RandomTP rtp;
	
	protected Task(boolean rand, List<String> worlds, int maxX, int maxZ, int minX, int minZ, boolean message, double price, int cooldown, boolean priceEnabled, boolean cooldownEnabled, List<String> biomes, List<String> blocks, UUID id, boolean usingTowny, boolean usingFactions, boolean usingWG, boolean usingWB){
		cancel(id);
          
		this.rand = rand;
		this.worldList = worlds;
		this.maxX = maxX;
		this.maxZ = maxZ;
		this.minX = minX;
		this.minZ = minZ;
		this.message = message;
		this.price = price;
		this.cooldown = cooldown;
		this.priceEnabled = priceEnabled;
		this.cooldownEnabled = cooldownEnabled;
		this.biomes = biomes;
		this.blocks = blocks;
		this.id = id;
		this.usingTowny = usingTowny;
		this.usingFactions = usingFactions;
		this.usingWG = usingWG;
		this.usingWB = usingWB;
		
		rtp.file("Initialized task for " + Bukkit.getPlayer(id).getName() + 
				"\n  rand: " + rand + 
				"\n  worlds: " + worlds + 
				"\n  max X: " + maxX + 
				"\n  max Z: " + maxZ + 
				"\n  min X: " + minX + 
				"\n  min Z: " + minZ + 
				"\n  message: " + message + 
				"\n price: " + price + 
				"\n cooldown: " + cooldown + 
				"\n priceEnabled: " + priceEnabled + 
				"\n cooldownEnabled: " + cooldownEnabled + 
				"\n biomes: " + biomes + 
				"\n blocks: " + blocks + 
				"\n id: " + id + 
				"\n usingWB: " + usingWB);
	}
       
	protected static void init(RandomTP rtp){
		players = new HashMap<UUID,Integer>();
		cooldowns = new HashMap<UUID,Long>();
		Task.rtp = rtp;
	}
       
	protected void setID(int taskId){
		players.put(this.id, Integer.valueOf(taskId));
	}
	
	protected static void cancel(UUID uuid){
		if (players.containsKey(uuid)){
			Bukkit.getScheduler().cancelTask(((Integer)players.get(uuid)).intValue());
			players.remove(uuid);
		}
	}
       
	public void run(){
		Player p = Bukkit.getServer().getPlayer(this.id);
         
		Location loc = findLocation(p.getWorld());
		if (loc == null) {
			return;
		}
		if ((this.cooldownEnabled) && (cooldowns.containsKey(this.id))){
			long time = System.currentTimeMillis() - ((Long)cooldowns.get(this.id)).longValue();
			int seconds = (int)(time / 1000.0D);
			
			rtp.file(p.getName() + ": time (s): " + seconds + "   " + time);
			if (seconds >= this.cooldown){
				cooldowns.remove(this.id);
			}
			else{
				p.sendMessage(ChatColor.GOLD + "You must wait another " + ChatColor.RED + (this.cooldown - seconds) + " seconds " + 
						ChatColor.GOLD + "to randomly teleport again.");
				cancel(this.id);
				return;
			}
		}
		if ((Bukkit.getServer().getPluginManager().isPluginEnabled("Vault")) && (this.priceEnabled) && (this.price != 0.0D)){
			Economy econ = null;
			RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
			if (rsp != null) {
				econ = (Economy)rsp.getProvider();
			}
			EconomyResponse.ResponseType type = econ.withdrawPlayer(p, this.price).type;
			if (!type.equals(EconomyResponse.ResponseType.SUCCESS)){
				p.sendMessage(ChatColor.GOLD + "Not enough money. Random teleports cost $" + this.price);
				rtp.file(p.getName() + " tried to teleport. Not enough money");
				cancel(this.id);
				return;
			}
		}
		if ((!rtp.checkPermission(p, "randomtp.cdexempt", null)) && (this.cooldown != 0) && (this.cooldownEnabled)){
			rtp.file("Adding cooldown for " + p.getName());
			cooldowns.put(p.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
		}
		loc.getChunk().load(true);
		if (this.message) {
			p.sendMessage(getMessage(loc));
		}
		p.teleport(loc);
		
		cancel(this.id);
		
		players.remove(this.id);
	}

	private Location findLocation(World w){
		World world = null;
		if (this.rand){
			ArrayList<World> worlds = new ArrayList<World>();
			for (String sworld : this.worldList) {
				if (!sworld.startsWith("$")) {
					worlds.add(Bukkit.getServer().getWorld(sworld));
				}
			}
			world = (World)worlds.get((int)(Math.random() * worlds.size()));
		}
		else{
			world = w;
		}
		rtp.file("Attempting to find safe location in " + world.getName() + " for " + Bukkit.getPlayer(this.id).getName());
		
		Location spawn = world.getSpawnLocation();
		if ((this.usingWB) && (Bukkit.getPluginManager().isPluginEnabled("WorldBorder"))){
			BorderData bd = Config.Border(world.getName());
			this.maxX = bd.getRadiusX();
			this.maxZ = bd.getRadiusZ();
		}
		int x = (int)(Math.random() * this.maxX);
		int z = (int)(Math.random() * this.maxZ);
		while ((x < this.minX) && (z < this.minZ)){
			double r = Math.random();
			if (r < 0.5D) {
				x++;
			} else {
				z++;
			}
		}
		if (Math.random() > 0.5D) {
			x *= -1;
		}
		if (Math.random() > 0.5D) {
			z *= -1;
		}
		x += spawn.getBlockX();
		z += spawn.getBlockZ();
				
		Block b = world.getHighestBlockAt(x, z).getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();
		rtp.file("Testing block " + b.getType().toString() + " in biome " + b.getBiome().toString() + " at " + b.getLocation());
		if ((this.biomes.contains(b.getBiome().name())) || (this.blocks.contains(b.getType().name()))){
			b.getChunk().unload(true, true);
			return null;
		}
		Location loc = b.getLocation().add(0.5D, 1.0D, 0.5D);
		if ((this.usingWB) && (Bukkit.getPluginManager().isPluginEnabled("WorldBorder"))){
			rtp.file("Checking if location is inside border");
			BorderData bd = Config.Border(world.getName());
			if (!bd.insideBorder(loc)){
				rtp.file("Outside of worldborder");
				b.getChunk().unload(true, true);
				return null;
			}
		}
		rtp.file("Checking if location is claimed");
		if (claimCheck(loc)){
			rtp.file("In claimed area.");
			b.getChunk().unload(true, true);
			return null;
		}
		rtp.file("Safe location found at " + loc);
		return loc;
	}
	
	private boolean claimCheck(Location loc){
		PluginManager pm = rtp.getServer().getPluginManager();
		if ((this.usingTowny) && (pm.isPluginEnabled("Towny"))){
			TownBlock tb = TownyUniverse.getTownBlock(loc);
			if (tb != null) {
				return true;
			}
		}
		if ((this.usingFactions) && (pm.isPluginEnabled("MassiveCore")) && (pm.isPluginEnabled("Factions"))){
			PS ps = PS.valueOf(loc).getChunk(true);
			Faction f = BoardColl.get().getFactionAt(ps);
			if (!f.getId().equals("none")) {
				return true;
			}
		}
		if ((this.usingWG) && (pm.isPluginEnabled("WorldGuard"))){
			RegionManager rm = WorldGuardPlugin.inst().getRegionManager(loc.getWorld());
			if (rm != null){
				List<String> list = rm.getApplicableRegionsIDs(BukkitUtil.toVector(loc));
				if ((list != null) && (list.size() != 0)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private String getMessage(Location loc){
		Location spawn = loc.getWorld().getSpawnLocation();
		double x = loc.getX() - spawn.getX();
		double y = loc.getZ() - spawn.getZ();
		double distance = Math.sqrt(Math.pow(x, 2.0D) + Math.pow(y, 2.0D));
		String direction = "";
		double slope = 0.0D;
		if ((x == 0.0D) && (y < 0.0D)) {
			direction = "North";
		} else if ((x == 0.0D) && (y > 0.0D)) {
			direction = "South";
		} else if ((y == 0.0D) && (x > 0.0D)) {
			direction = "East";
		} else if ((y == 0.0D) && (x < 0.0D)) {
			direction = "West";
		} else {
			slope = Math.toDegrees(Math.atan(y / x));
		}
		if (x > 0.0D){
			if ((slope > -90.0D) && (slope <= -67.5D)) {
				direction = "North";
			} else if ((slope > -67.5D) && (slope <= -22.5D)) {
				direction = "Northeast";
			} else if ((slope > -22.5D) && (slope <= 22.5D)) {
				direction = "East";
			} else if ((slope > 22.5D) && (slope <= 67.5D)) {
				direction = "Southeast";
			} else if ((slope > 67.5D) && (slope <= 90.0D)) {
				direction = "South";
			}
		}
		else if ((slope > -90.0D) && (slope <= -67.5D)) {
			direction = "South";
		} else if ((slope > -67.5D) && (slope <= -22.5D)) {
			direction = "Southwest";
		} else if ((slope > -22.5D) && (slope <= 22.5D)) {
			direction = "West";
		} else if ((slope > 22.5D) && (slope <= 67.5D)) {
			direction = "Northwest";
		} else if ((slope > 67.5D) && (slope <= 90.0D)) {
			direction = "North";
		}
			return  ChatColor.GOLD + "You have been teleported " + ChatColor.RED + String.format("%.2f", new Object[] { Double.valueOf(distance) }) + " blocks " + direction + ChatColor.GOLD + " of " + ChatColor.RED + loc.getWorld().getName() + "'s" + ChatColor.GOLD + " spawn.";
	}
}