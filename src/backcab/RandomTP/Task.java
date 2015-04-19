package backcab.RandomTP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.massivecraft.massivecore.ps.PS;

public class Task implements Runnable{
	
	private boolean rand, message, priceEnabled, cooldownEnabled, 
					usingTowny, usingFactions, usingWG, usingWB;
	private List<String> worldList, biomes, blocks;
	private int maxX, maxZ, minX, minZ, cooldown;
	private double price;
	
	private String id;
	
	private static HashMap<String, Integer> players;
	private static HashMap<String, Long> cooldowns;
	private static RandomTP rtp;

	protected Task(boolean rand, List<String> worlds, int maxX, int maxZ,
			int minX, int minZ, boolean message, double price, int cooldown,
			boolean priceEnabled, boolean cooldownEnabled, List<String> biomes, 
			List<String> blocks, String id, boolean usingTowny, boolean usingFactions, 
			boolean usingWG, boolean usingWB) {
		
		//cancel the existing task for this user if one exist
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
		players = new HashMap<String, Integer>();
		cooldowns = new HashMap<String, Long>();
		Task.rtp = rtp;
	}
	
	protected void setID(int taskId){
		players.put(id, taskId);
	}

	protected static void cancel(String name) {
		if(players.containsKey(name)){
			Bukkit.getScheduler().cancelTask(players.get(name));
			players.remove(name);
		}
	}

	@Override
	public void run() {
		Player p = Bukkit.getServer().getPlayer(id);
		
		Location loc = findLocation(p.getWorld());
		
		//location check (keep trying till loc found)
		if(loc == null){
			return;
		}
		
		//cooldown check (cancel if cooldown)
		if(cooldownEnabled && cooldowns.containsKey(id)){
			long time = System.currentTimeMillis() - cooldowns.get(id);
			int seconds = (int)(time/1000.0);
			
			rtp.file(p.getName() + ": time (s): " + seconds + "   " + time);
			
			if(seconds >= cooldown){
				cooldowns.remove(id);
			} else {
				p.sendMessage(ChatColor.GOLD + "You must wait another " + ChatColor.RED + (cooldown - seconds) + " seconds " + 
						      ChatColor.GOLD + "to randomly teleport again.");
				cancel(id);
				return;
			}
		}
		
		//price check and take (cancel if can't afford)
		if(Bukkit.getServer().getPluginManager().isPluginEnabled("Vault") && priceEnabled && price != 0){
			net.milkbowl.vault.economy.Economy econ = null;
			RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
			if(rsp != null){
				econ = rsp.getProvider();
			}
			
			net.milkbowl.vault.economy.EconomyResponse.ResponseType type = econ.withdrawPlayer(p, price).type;
			
			if(!type.equals(net.milkbowl.vault.economy.EconomyResponse.ResponseType.SUCCESS)){
				p.sendMessage(ChatColor.GOLD + "Not enough money. Random teleports cost $" + price);
				rtp.file(p.getName() + " tried to teleport. Not enough money");
				cancel(id);
				return;
			}
		}
		
		//cooldown add
		if(!rtp.checkPermission(p, "randomtp.cdexempt", null) && cooldown != 0 && cooldownEnabled){
			rtp.file("Adding cooldown for " + p.getName());
			cooldowns.put(p.getName(), System.currentTimeMillis());
		}
		
		//force load chunk so player doesn't spawn in wall
		loc.getChunk().load(true);
		
		//message check and send
		if(message){
			p.sendMessage(getMessage(loc));
		}
		
		//teleport the player
		p.teleport(loc);
		//cancel the event (prevents checking again)
		cancel(id);
		//remove the player from the map
		players.remove(id);
	}

	private Location findLocation(World w){
		World world = null;
		
		//random world check
		if(rand){
			ArrayList<World> worlds = new ArrayList<World>();
			for(String sworld: worldList){
				if(!sworld.startsWith("$")){
					worlds.add(Bukkit.getServer().getWorld(sworld));
				}
			}
			
			world = worlds.get((int)(Math.random()*worlds.size()));
		} else {
			world = w;
		}
		
		//debug
		rtp.file("Attempting to find safe location in " + world.getName() + " for " + Bukkit.getPlayer(id).getName());
		
		Location spawn = world.getSpawnLocation();
		
		//set maxX and maxZ to worldborder if applicable
		if(usingWB && Bukkit.getPluginManager().isPluginEnabled("WorldBorder")){
			com.wimbli.WorldBorder.BorderData bd = com.wimbli.WorldBorder.Config.Border(world.getName());
			maxX = bd.getRadiusX();
			maxZ = bd.getRadiusZ();
		}
		
		//get random x and z values centered around the spawn point
		int x = (int)(Math.random() * maxX);
		int z = (int)(Math.random() * maxZ);
		
		//check to make sure the coords are at least the minimum distance away
		while(x < minX && z < minZ){
			double r = Math.random();
			if(r < .5){
				x++;
			} else {
				z++;
			}
		}
		
		if(Math.random() > .5){
			x *= -1;
		}
		if(Math.random() > .5){
			z *= -1;
		}
		
		//center the teleport around the world's spawn point
		x += spawn.getBlockX();
		z += spawn.getBlockZ();
	
		//check block and biome
		Block b = world.getHighestBlockAt(x, z).getLocation().subtract(0, 1, 0).getBlock();
		rtp.file("Testing block " + b.getType().toString() + " in biome " + b.getBiome().toString() + " at " + b.getLocation());
		if(biomes.contains(b.getBiome().name()) || blocks.contains(b.getType().name())){
			b.getChunk().unload(true, true);
			return null;
		}
		
		//center the player on the block and put them in the open air above the block (prevents from being spawned in the ground)
		Location loc = b.getLocation().add(0.5, 1, 0.5);
		
		//check if inside border
		if(usingWB && Bukkit.getPluginManager().isPluginEnabled("WorldBorder")){
			rtp.file("Checking if location is inside border");
			com.wimbli.WorldBorder.BorderData bd = com.wimbli.WorldBorder.Config.Border(world.getName());
			if(!bd.insideBorder(loc)){
				rtp.file("Outside of worldborder");
				b.getChunk().unload(true, true);
				return null;
			}
		}
		
		rtp.file("Checking if location is claimed");
		if(claimCheck(loc)){
			rtp.file("In claimed area.");
			b.getChunk().unload(true, true);
			return null;
		}
		
		rtp.file("Safe location found at " + loc);
		return loc;
	}
	
	//return true if claimed
	private boolean claimCheck(Location loc){
		PluginManager pm = rtp.getServer().getPluginManager();
		
		//towny
		if(usingTowny && pm.isPluginEnabled("Towny")){
			com.palmergames.bukkit.towny.object.TownBlock tb = com.palmergames.bukkit.towny.object.TownyUniverse.getTownBlock(loc);
			if(tb != null){
				return true;
			}
		}
		
		//factions
		if(usingFactions && pm.isPluginEnabled("MassiveCore") && pm.isPluginEnabled("Factions")){
			com.massivecraft.massivecore.ps.PS ps = PS.valueOf(loc).getChunk(true);
			com.massivecraft.factions.entity.Faction f = com.massivecraft.factions.entity.BoardColl.get().getFactionAt(ps);

			if(!f.getId().equals("none")){
				return true;
			}
		}
		
		//worldguard
		if(usingWG && pm.isPluginEnabled("WorldGuard")){
			com.sk89q.worldguard.protection.managers.RegionManager rm = com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().getRegionManager(loc.getWorld());
			if(rm != null){
				List<String> list = rm.getApplicableRegionsIDs(com.sk89q.worldedit.bukkit.BukkitUtil.toVector(loc));
				if(list != null && list.size() != 0){
					return true;
				}
			}
		}
		
		return false;
	}

	private String getMessage(Location loc){
		Location spawn = loc.getWorld().getSpawnLocation();
		double x = (loc.getX() - spawn.getX());
		double y = (loc.getZ() - spawn.getZ());
		double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y,2));
		String direction = "";
		double slope = 0;
		
		if(x == 0 && y < 0){
			direction = "North";
		}
		else if(x == 0 && y > 0){
			direction = "South";
		}
		else if(y == 0 && x > 0){
			direction = "East";
		}
		else if(y == 0 && x < 0){
			direction = "West";
		}
		else{
			slope = Math.toDegrees(Math.atan(y/x));
		}
		
		if(x > 0){
			if(slope > -90 && slope <= -67.5){
				direction = "North";
			}
			else if(slope > -67.5 && slope <= -22.5){
				direction = "Northeast";
			}
			else if(slope > -22.5 && slope <= 22.5){
				direction = "East";
			}
			else if(slope > 22.5 && slope <= 67.5){
				direction = "Southeast";
			}
			else if(slope > 67.5 && slope <= 90){
				direction = "South";
			}
		} else {
			if(slope > -90 && slope <= -67.5){
				direction = "South";
			}
			else if(slope > -67.5 && slope <= -22.5){
				direction = "Southwest";
			}
			else if(slope > -22.5 && slope <= 22.5){
				direction = "West";
			}
			else if(slope > 22.5 && slope <= 67.5){
				direction = "Northwest";
			}
			else if(slope > 67.5 && slope <= 90){
				direction = "North";
			}
		}
		
		return ChatColor.GOLD + "You have been teleported " + ChatColor.RED + String.format("%.2f", distance) + " blocks " + direction +
				      ChatColor.GOLD + " of " + ChatColor.RED + loc.getWorld().getName() + "'s" + ChatColor.GOLD + " spawn.";
	}
}
