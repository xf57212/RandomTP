package backcab.RandomTP;
        
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
       
public class TPCommand implements CommandExecutor{
	private RandomTP rtp;
         
	protected TPCommand(RandomTP rtp){
		this.rtp = rtp;
	}
         
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args){
		PreTP tp = new PreTP();
		if ((args.length >= 1) && (args[0].equalsIgnoreCase("portal"))){
			parsePortalCommand(args, sender);
			return true;
		}
		if ((args.length == 1) && (args[0].equalsIgnoreCase("reload")) && (this.rtp.checkPermission(sender, "randomtp.reload", "You do not have permission to use this command."))){
			this.rtp.config().reload();
			sender.sendMessage(ChatColor.GREEN + "RandomTP reloaded.");
			return true;
		}
		if ((args.length == 0) && (this.rtp.checkPermission(sender, "randomtp.tp", "You do not have permission to use this command."))){
			if ((sender instanceof Player)){
				this.rtp.file(sender.getName() + ": used /rtp");
				tp.start(this.rtp, (Player)sender, TeleportType.SELF);
			}
			else{
				sender.sendMessage("/rtp <player|portal>");
			}
			return true;
		}
		if ((args.length == 1) && (this.rtp.checkPermission(sender, "randomtp.other", "You do not have permission to use this command."))){
			@SuppressWarnings("deprecation")
			Player p = Bukkit.getPlayer(args[0]);
			if (p == null){
				sender.sendMessage(ChatColor.RED + "Invalid player. Player is either offline or does not exist");
				return true;
			}
			this.rtp.file(sender.getName() + ": used /rtp " + p.getName());
			tp.start(this.rtp, p, TeleportType.CMD);
			
			return true;
		}
		return false;
	}
	
	private void parsePortalCommand(String[] args, CommandSender sender){
		YamlConfiguration portals = this.rtp.portals().getConfig();
		if ((args.length == 1) && (this.rtp.checkPermission(sender, "randomtp.portal.info", "You do not have permission to use this command."))){
			this.rtp.file(sender.getName() + ": get portal names");
			String s = ChatColor.BLUE + "-----Portals-----" + ChatColor.AQUA;
			for (String portal : portals.getKeys(false)) {
				s = s + "\n" + portal;
			}
			sender.sendMessage(s);
			return;
		}
		if (args.length == 2){
			if ((args[1].equalsIgnoreCase("delete")) && (this.rtp.checkPermission(sender, "randomtp.portal.delete", "You do not have permission to use this command."))){
				this.rtp.file(sender.getName() + ": delete command");
				sender.sendMessage("/rtp portal delete <name>");
				return;
			}
			if ((args[1].equalsIgnoreCase("create")) && (this.rtp.checkPermission(sender, "randomtp.portal.make", "You do not have permission to use this command."))){
				sender.sendMessage("/rtp portal create <name> [material]");
				return;
			}
		}
		if ((args.length == 2) && (this.rtp.checkPermission(sender, "randomtp.portal.info", "You do not have permission to use this command."))){
			if (portals.getKeys(false).contains(args[1])){
				ConfigurationSection sect = portals.getConfigurationSection(args[1]);
				String s = ChatColor.BLUE + "-----" + args[1] + "-----" + ChatColor.AQUA;
				s = s + "\nworld: " + sect.getString("world");
				s = s + "\nvect 1: " + sect.getVector("v1");
				s = s + "\nvect 2: " + sect.getVector("v2");
				s = s + "\nmaterial: " + sect.getString("material");
				
				sender.sendMessage(s);
			}
			else{
				sender.sendMessage(ChatColor.RED + "This portal does not exist");
			}
			return;
		}
		if ((args.length == 3) || (args.length == 4)){
			if (!(sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "Only a player can use this command.");
				return;
			}
			Player p = (Player)sender;
			if ((args[1].equalsIgnoreCase("delete")) && (this.rtp.checkPermission(sender, "randomtp.portal.delete", "You do not have permission to use this command."))){
				if (portals.getKeys(false).contains(args[2])){
					ConfigurationSection sect = portals.getConfigurationSection(args[2]);
					BuildTask bt = new BuildTask(sect.getVector("v1"), sect.getVector("v2"), sect.getString("world"), Material.AIR, (byte)0, Material.getMaterial(sect.getString("material")));
					
					int id = Bukkit.getScheduler().runTaskTimer(this.rtp, bt, 0L, 1L).getTaskId();
					bt.setID(id);
					
					portals.set(args[2], null);
					try{
						this.rtp.portals().save();
					}
					catch (IOException localIOException) {}
					this.rtp.file(sender.getName() + ": deleted portal " + args[2]);
					sender.sendMessage(ChatColor.GREEN + "Portal " + args[2] + " has been deleted.");
				}
				else{
					this.rtp.file(sender.getName() + ": portal " + args[2] + " does not exist.");
					sender.sendMessage(ChatColor.GREEN + "Portal " + args[2] + " does not exist.");
				}
				return;
			}
			if ((args[1].equalsIgnoreCase("create")) && (this.rtp.checkPermission(sender, "randomtp.portal.make", "You do not have permission to use this command."))){
				if (portals.getKeys(false).contains(args[2])){
					this.rtp.file(sender.getName() + ": portal name " + args[2] + " in use.");
					sender.sendMessage(ChatColor.RED + "Portal name already in use. Please choose another.");
					return;
				}
				PortalMaker pm = PortalMaker.getMaker(p.getUniqueId());
				if ((pm == null) || (pm.getPos1() == null) || (pm.getPos2() == null)){
					this.rtp.file(sender.getName() + ": no region selected");
					sender.sendMessage(ChatColor.RED + "Please select an area to make a portal.");
					return;
				}
				Vector pos1 = pm.getPos1();
				Vector pos2 = pm.getPos2();
				
				Material m = null;
				byte data = 0;
				if (args.length == 4){
					String[] split = args[3].split(":");
					m = Material.getMaterial(split[0].toUpperCase());
					if (split.length >= 2) {
						try{
							data = Byte.parseByte(split[1]);
						}
						catch (Exception localException) {}
					}
				}
				if (m == null) {
					m = Material.AIR;
				}
				ConfigurationSection sect = portals.createSection(args[2]);
				sect.set("world", p.getWorld().getName());
				sect.set("v1", pos1);
				sect.set("v2", pos2);
				sect.set("material", m.toString());
				try{
					this.rtp.portals().save();
				}
				catch (IOException localIOException1) {}
				BuildTask bt = new BuildTask(pos1, pos2, p.getWorld().getName(), m, data, Material.AIR);
				int id = Bukkit.getScheduler().runTaskTimer(this.rtp, bt, 0L, 1L).getTaskId();
				bt.setID(id);
				
				this.rtp.file(sender.getName() + ": created portal: " + 
						"\n  name: " + args[2] + 
						"\n  world: " + p.getWorld().getName() + 
						"\n  v1: " + pos1.toString() + 
						"\n  v2: " + pos2.toString() + 
						"\n  material: " + m.toString());
				sender.sendMessage(ChatColor.GREEN + "Portal " + args[2] + " has been created.");
			}
		}
	}
}