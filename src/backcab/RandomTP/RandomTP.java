package backcab.RandomTP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
      
public class RandomTP extends JavaPlugin{
	private PluginFile config;
	private PluginFile portals;
        
	public void onEnable(){
		this.config = new PluginFile(this, "config", true);
		this.portals = new PluginFile(this, "portals", false);
		
		file("Server Version: " + getServer().getVersion());
		file("RandomTP Version: " + getDescription().getVersion());
		
		PortalMaker.init();
		Task.init(this);
		
		Bukkit.getPluginManager().registerEvents(new Events(this), this);
		getCommand("randomtp").setExecutor(new TPCommand(this));
          
		log(Level.INFO, "Checking/Fixing format of portals.yml");
		updatePortals();
	}
	
	private void updatePortals(){
		Set<String> portalList = this.portals.getConfig().getKeys(false);
		for (String portal : portalList) {
			if (!this.portals.getConfig().contains(portal + ".world")){
				String[] s = this.portals.getConfig().getString(portal).split(":");
				String world = s[0];
				Vector v1 = new Vector(Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3]));
				Vector v2 = new Vector(Integer.parseInt(s[4]), Integer.parseInt(s[5]), Integer.parseInt(s[6]));
				String material = s[7];
				
      
				this.portals.getConfig().set(portal, null);
              
				ConfigurationSection sect = this.portals.getConfig().createSection(portal);
				sect.set("world", world);
				sect.set("v1", v1);
				sect.set("v2", v2);
				sect.set("material", material);
			}
		}
		try{
			this.portals.save();
		}
		catch (IOException localIOException) {}
	}
        
	protected void log(Level level, String message){
		getLogger().log(level, message);
	}
       
	protected void file(String message){
		if (!this.config.getConfig().getBoolean("debug")) {
			return;
		}
		File debug = new File(getDataFolder(), "debug.log");
		if (!debug.exists()) {
			try{
				debug.createNewFile();
			}
			catch (IOException localIOException) {}
		}
		try{
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(debug, true)));
			out.println(message);
			out.close();
		}
		catch (IOException localIOException1) {}
	}
       
	protected boolean checkPermission(CommandSender sender, String perm, String message){
		if (!sender.hasPermission(perm)){
			file(sender.getName() + " does not have " + perm);
			if (message != null) {
				sender.sendMessage(ChatColor.RED + message);
			}
			return false;
		}
		return true;
	}
       
	protected PluginFile config(){
		return this.config;
	}
       
	protected PluginFile portals(){
		return this.portals;
	}
}