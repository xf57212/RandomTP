package backcab.RandomTP;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

public class Events implements Listener{

	private RandomTP rtp;
	
	protected Events(RandomTP rtp){
		this.rtp = rtp;
	}
	
	@EventHandler
	protected void logoff(PlayerQuitEvent event){
		Task.cancel(event.getPlayer().getName());
	}
	
	@EventHandler
	protected void signClick(PlayerInteractEvent event){
		Player p = event.getPlayer();
		
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			return;
		}
		
		Block b = event.getClickedBlock();
		
		if(!b.getType().equals(Material.SIGN_POST) && !b.getType().equals(Material.WALL_SIGN)){
			return;
		}
		
		Sign s = (Sign)b.getState();
		
		String line = ChatColor.stripColor(s.getLine(0));
		
		if(!line.equalsIgnoreCase("[randomtp]")){
			return;
		}
		
		if(!rtp.checkPermission(p, "randomtp.sign.use", "You do not have permisson to use this sign.")){
			return;
		}
		
		rtp.file(p.getName() + ": successfully used sign");
		
		new PreTP().start(rtp, p, TeleportType.SIGN);
	}
	
	@EventHandler
	protected void makeSign(SignChangeEvent event){
		String line = event.getLine(0);
		line = ChatColor.stripColor(line);
		
		Player p = event.getPlayer();
		
		if(!line.equalsIgnoreCase("[randomtp]")){
			rtp.file(p.getName() + ": " + line);
			return;
		}
		
		if(!rtp.checkPermission(p, "randomtp.sign.make", null)){
			return;
		}
		
		rtp.file(p.getName() + ": successfully made sign");
		
		event.setLine(0, ChatColor.DARK_BLUE + "[RandomTP]");
	}
	
	@EventHandler
	protected void playerMove(PlayerMoveEvent event){
		
		if(!event.getPlayer().hasPermission("randomtp.portal.use")){
			return;
		}
		
		Location to = event.getTo();
		Location from = event.getFrom();
		
		//ignore if block not changed
		if(to.getBlock().getLocation().equals(from.getBlock().getLocation())){
			return;
		}
		
		if(inPortal(to.toVector())){
			rtp.file(event.getPlayer().getName() + ": successfully used portal");
			new PreTP().start(rtp, event.getPlayer(), TeleportType.PORTAL);
			return;
		}
	}
	
	@EventHandler
	protected void onClick(PlayerInteractEvent event){
		if(!event.getPlayer().getItemInHand().getType().equals(Material.WOOD_AXE)){
			return;
		}
		
		if(!rtp.checkPermission(event.getPlayer(), "randomtp.portal.make", null)){
			return;
		}
		
		PortalMaker pm = PortalMaker.getMaker(event.getPlayer().getUniqueId());
		
		if(pm == null){
			pm = new PortalMaker(event.getPlayer().getUniqueId());
		}
		
		if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
			event.setCancelled(true);
			Location loc = event.getClickedBlock().getLocation();
			pm.setPos1(loc.toVector());
			
			if(!Bukkit.getPluginManager().isPluginEnabled("WorldEdit")){
				event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Position 1 set to " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ".");
			}
		}
		
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			event.setCancelled(true);
			Location loc = event.getClickedBlock().getLocation();
			pm.setPos2(loc.toVector());
			
			if(!Bukkit.getPluginManager().isPluginEnabled("WorldEdit")){
				event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Position 2 set to " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ".");
			}
		}
	}
	
	@EventHandler
	protected void preventUpdate(BlockPhysicsEvent event){
		if(!rtp.config().getConfig().getBoolean("preventPortalUpdate")){
			return;
		}
		
		Location loc = event.getBlock().getLocation();
		
		if(inPortal(loc.toVector())){
			event.setCancelled(true);
		}
	}
	
	private boolean inPortal(Vector v){
		Set<String> portals = rtp.portals().getConfig().getKeys(false);
		
		for(String portal: portals){
			ConfigurationSection sect = rtp.portals().get(portal);
			
			Vector v1 = sect.getVector("v1");
			Vector v2 = sect.getVector("v2");
			
			Vector max = Vector.getMaximum(v1, v2);
			Vector min = Vector.getMinimum(v1, v2);
			
			v.setX(v.getBlockX());
			v.setY(v.getBlockY());
			v.setZ(v.getBlockZ());
			
			if(v.isInAABB(min, max)){
				return true;
			}
			
//			if(((x <= x1 && x >= x2) || (x >= x1 && x <= x2)) &&
//			   ((y <= y1 && x >= y2) || (y >= y1 && x <= y2)) &&
//			   ((z <= z1 && x >= z2) || (z >= z1 && x <= z2))){
//				return true;
//			}
			
		}
		
		return false;
	}
}
