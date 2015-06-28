/*   1:    */ package backcab.RandomTP;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.util.Set;
/*   5:    */ import org.bukkit.Bukkit;
/*   6:    */ import org.bukkit.ChatColor;
/*   7:    */ import org.bukkit.Material;
/*   8:    */ import org.bukkit.World;
/*   9:    */ import org.bukkit.command.Command;
/*  10:    */ import org.bukkit.command.CommandExecutor;
/*  11:    */ import org.bukkit.command.CommandSender;
/*  12:    */ import org.bukkit.configuration.ConfigurationSection;
/*  13:    */ import org.bukkit.configuration.file.YamlConfiguration;
/*  14:    */ import org.bukkit.entity.Player;
/*  15:    */ import org.bukkit.scheduler.BukkitScheduler;
/*  16:    */ import org.bukkit.scheduler.BukkitTask;
/*  17:    */ import org.bukkit.util.Vector;
/*  18:    */ 
/*  19:    */ public class TPCommand
/*  20:    */   implements CommandExecutor
/*  21:    */ {
/*  22:    */   private RandomTP rtp;
/*  23:    */   
/*  24:    */   protected TPCommand(RandomTP rtp)
/*  25:    */   {
/*  26: 21 */     this.rtp = rtp;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args)
/*  30:    */   {
/*  31: 32 */     PreTP tp = new PreTP();
/*  32: 34 */     if ((args.length >= 1) && (args[0].equalsIgnoreCase("portal")))
/*  33:    */     {
/*  34: 35 */       parsePortalCommand(args, sender);
/*  35: 36 */       return true;
/*  36:    */     }
/*  37: 39 */     if ((args.length == 1) && (args[0].equalsIgnoreCase("reload")) && (this.rtp.checkPermission(sender, "randomtp.reload", "You do not have permission to use this command.")))
/*  38:    */     {
/*  39: 40 */       this.rtp.config().reload();
/*  40: 41 */       sender.sendMessage(ChatColor.GREEN + "RandomTP reloaded.");
/*  41: 42 */       return true;
/*  42:    */     }
/*  43: 46 */     if ((args.length == 0) && (this.rtp.checkPermission(sender, "randomtp.tp", "You do not have permission to use this command.")))
/*  44:    */     {
/*  45: 47 */       if ((sender instanceof Player))
/*  46:    */       {
/*  47: 48 */         this.rtp.file(sender.getName() + ": used /rtp");
/*  48: 49 */         tp.start(this.rtp, (Player)sender, TeleportType.SELF);
/*  49:    */       }
/*  50:    */       else
/*  51:    */       {
/*  52: 51 */         sender.sendMessage("/rtp <player|portal>");
/*  53:    */       }
/*  54: 53 */       return true;
/*  55:    */     }
/*  56: 57 */     if ((args.length == 1) && (this.rtp.checkPermission(sender, "randomtp.other", "You do not have permission to use this command.")))
/*  57:    */     {
/*  58: 59 */       Player p = Bukkit.getPlayer(args[0]);
/*  59: 61 */       if (p == null)
/*  60:    */       {
/*  61: 62 */         sender.sendMessage(ChatColor.RED + "Invalid player. Player is either offline or does not exist");
/*  62: 63 */         return true;
/*  63:    */       }
/*  64: 66 */       this.rtp.file(sender.getName() + ": used /rtp " + p.getName());
/*  65: 67 */       tp.start(this.rtp, p, TeleportType.CMD);
/*  66:    */       
/*  67: 69 */       return true;
/*  68:    */     }
/*  69: 72 */     return false;
/*  70:    */   }
/*  71:    */   
/*  72:    */   private void parsePortalCommand(String[] args, CommandSender sender)
/*  73:    */   {
/*  74: 76 */     YamlConfiguration portals = this.rtp.portals().getConfig();
/*  75: 79 */     if ((args.length == 1) && (this.rtp.checkPermission(sender, "randomtp.portal.info", "You do not have permission to use this command.")))
/*  76:    */     {
/*  77: 80 */       this.rtp.file(sender.getName() + ": get portal names");
/*  78: 81 */       String s = ChatColor.BLUE + "-----Portals-----" + ChatColor.AQUA;
/*  79: 82 */       for (String portal : portals.getKeys(false)) {
/*  80: 83 */         s = s + "\n" + portal;
/*  81:    */       }
/*  82: 85 */       sender.sendMessage(s);
/*  83: 86 */       return;
/*  84:    */     }
/*  85: 89 */     if (args.length == 2)
/*  86:    */     {
/*  87: 90 */       if ((args[1].equalsIgnoreCase("delete")) && (this.rtp.checkPermission(sender, "randomtp.portal.delete", "You do not have permission to use this command.")))
/*  88:    */       {
/*  89: 91 */         this.rtp.file(sender.getName() + ": delete command");
/*  90: 92 */         sender.sendMessage("/rtp portal delete <name>");
/*  91: 93 */         return;
/*  92:    */       }
/*  93: 96 */       if ((args[1].equalsIgnoreCase("create")) && (this.rtp.checkPermission(sender, "randomtp.portal.make", "You do not have permission to use this command.")))
/*  94:    */       {
/*  95: 97 */         sender.sendMessage("/rtp portal create <name> [material]");
/*  96: 98 */         return;
/*  97:    */       }
/*  98:    */     }
/*  99:103 */     if ((args.length == 2) && (this.rtp.checkPermission(sender, "randomtp.portal.info", "You do not have permission to use this command.")))
/* 100:    */     {
/* 101:104 */       if (portals.getKeys(false).contains(args[1]))
/* 102:    */       {
/* 103:105 */         ConfigurationSection sect = portals.getConfigurationSection(args[1]);
/* 104:106 */         String s = ChatColor.BLUE + "-----" + args[1] + "-----" + ChatColor.AQUA;
/* 105:107 */         s = s + "\nworld: " + sect.getString("world");
/* 106:108 */         s = s + "\nvect 1: " + sect.getVector("v1");
/* 107:109 */         s = s + "\nvect 2: " + sect.getVector("v2");
/* 108:110 */         s = s + "\nmaterial: " + sect.getString("material");
/* 109:    */         
/* 110:112 */         sender.sendMessage(s);
/* 111:    */       }
/* 112:    */       else
/* 113:    */       {
/* 114:114 */         sender.sendMessage(ChatColor.RED + "This portal does not exist");
/* 115:    */       }
/* 116:116 */       return;
/* 117:    */     }
/* 118:121 */     if ((args.length == 3) || (args.length == 4))
/* 119:    */     {
/* 120:122 */       if (!(sender instanceof Player))
/* 121:    */       {
/* 122:123 */         sender.sendMessage(ChatColor.RED + "Only a player can use this command.");
/* 123:124 */         return;
/* 124:    */       }
/* 125:127 */       Player p = (Player)sender;
/* 126:129 */       if ((args[1].equalsIgnoreCase("delete")) && (this.rtp.checkPermission(sender, "randomtp.portal.delete", "You do not have permission to use this command.")))
/* 127:    */       {
/* 128:130 */         if (portals.getKeys(false).contains(args[2]))
/* 129:    */         {
/* 130:131 */           ConfigurationSection sect = portals.getConfigurationSection(args[2]);
/* 131:132 */           BuildTask bt = new BuildTask(sect.getVector("v1"), sect.getVector("v2"), sect.getString("world"), Material.AIR, (byte)0, Material.getMaterial(sect.getString("material")));
/* 132:    */           
/* 133:134 */           int id = Bukkit.getScheduler().runTaskTimer(this.rtp, bt, 0L, 1L).getTaskId();
/* 134:135 */           bt.setID(id);
/* 135:    */           
/* 136:137 */           portals.set(args[2], null);
/* 137:    */           try
/* 138:    */           {
/* 139:139 */             this.rtp.portals().save();
/* 140:    */           }
/* 141:    */           catch (IOException localIOException) {}
/* 142:142 */           this.rtp.file(sender.getName() + ": deleted portal " + args[2]);
/* 143:143 */           sender.sendMessage(ChatColor.GREEN + "Portal " + args[2] + " has been deleted.");
/* 144:    */         }
/* 145:    */         else
/* 146:    */         {
/* 147:145 */           this.rtp.file(sender.getName() + ": portal " + args[2] + " does not exist.");
/* 148:146 */           sender.sendMessage(ChatColor.GREEN + "Portal " + args[2] + " does not exist.");
/* 149:    */         }
/* 150:148 */         return;
/* 151:    */       }
/* 152:151 */       if ((args[1].equalsIgnoreCase("create")) && (this.rtp.checkPermission(sender, "randomtp.portal.make", "You do not have permission to use this command.")))
/* 153:    */       {
/* 154:152 */         if (portals.getKeys(false).contains(args[2]))
/* 155:    */         {
/* 156:153 */           this.rtp.file(sender.getName() + ": portal name " + args[2] + " in use.");
/* 157:154 */           sender.sendMessage(ChatColor.RED + "Portal name already in use. Please choose another.");
/* 158:155 */           return;
/* 159:    */         }
/* 160:158 */         PortalMaker pm = PortalMaker.getMaker(p.getUniqueId());
/* 161:160 */         if ((pm == null) || (pm.getPos1() == null) || (pm.getPos2() == null))
/* 162:    */         {
/* 163:161 */           this.rtp.file(sender.getName() + ": no region selected");
/* 164:162 */           sender.sendMessage(ChatColor.RED + "Please select an area to make a portal.");
/* 165:163 */           return;
/* 166:    */         }
/* 167:166 */         Vector pos1 = pm.getPos1();
/* 168:167 */         Vector pos2 = pm.getPos2();
/* 169:    */         
/* 170:169 */         Material m = null;
/* 171:170 */         byte data = 0;
/* 172:171 */         if (args.length == 4)
/* 173:    */         {
/* 174:172 */           String[] split = args[3].split(":");
/* 175:173 */           m = Material.getMaterial(split[0].toUpperCase());
/* 176:174 */           if (split.length >= 2) {
/* 177:    */             try
/* 178:    */             {
/* 179:176 */               data = Byte.parseByte(split[1]);
/* 180:    */             }
/* 181:    */             catch (Exception localException) {}
/* 182:    */           }
/* 183:    */         }
/* 184:180 */         if (m == null) {
/* 185:181 */           m = Material.AIR;
/* 186:    */         }
/* 187:184 */         ConfigurationSection sect = portals.createSection(args[2]);
/* 188:185 */         sect.set("world", p.getWorld().getName());
/* 189:186 */         sect.set("v1", pos1);
/* 190:187 */         sect.set("v2", pos2);
/* 191:188 */         sect.set("material", m.toString());
/* 192:    */         try
/* 193:    */         {
/* 194:191 */           this.rtp.portals().save();
/* 195:    */         }
/* 196:    */         catch (IOException localIOException1) {}
/* 197:194 */         BuildTask bt = new BuildTask(pos1, pos2, p.getWorld().getName(), m, data, Material.AIR);
/* 198:195 */         int id = Bukkit.getScheduler().runTaskTimer(this.rtp, bt, 0L, 1L).getTaskId();
/* 199:196 */         bt.setID(id);
/* 200:    */         
/* 201:198 */         this.rtp.file(sender.getName() + ": created portal: " + 
/* 202:199 */           "\n  name: " + args[2] + 
/* 203:200 */           "\n  world: " + p.getWorld().getName() + 
/* 204:201 */           "\n  v1: " + pos1.toString() + 
/* 205:202 */           "\n  v2: " + pos2.toString() + 
/* 206:203 */           "\n  material: " + m.toString());
/* 207:204 */         sender.sendMessage(ChatColor.GREEN + "Portal " + args[2] + " has been created.");
/* 208:    */       }
/* 209:    */     }
/* 210:    */   }
/* 211:    */ }


/* Location:           C:\Users\David\Desktop\RandomTP.jar
 * Qualified Name:     backcab.RandomTP.TPCommand
 * JD-Core Version:    0.7.0.1
 */