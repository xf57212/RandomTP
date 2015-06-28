/*   1:    */ package backcab.RandomTP;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.util.List;
/*   5:    */ import java.util.UUID;
/*   6:    */ import java.util.logging.Level;
/*   7:    */ import org.bukkit.Bukkit;
/*   8:    */ import org.bukkit.ChatColor;
/*   9:    */ import org.bukkit.Location;
/*  10:    */ import org.bukkit.Material;
/*  11:    */ import org.bukkit.World;
/*  12:    */ import org.bukkit.block.Block;
/*  13:    */ import org.bukkit.configuration.file.YamlConfiguration;
/*  14:    */ import org.bukkit.entity.Player;
/*  15:    */ import org.bukkit.scheduler.BukkitScheduler;
/*  16:    */ import org.bukkit.scheduler.BukkitTask;
/*  17:    */ 
/*  18:    */ public class PreTP
/*  19:    */ {
/*  20:    */   private RandomTP rtp;
/*  21:    */   private PluginFile config;
/*  22:    */   
/*  23:    */   protected void start(RandomTP rtp, Player p, TeleportType type)
/*  24:    */   {
/*  25: 20 */     this.rtp = rtp;
/*  26: 21 */     this.config = rtp.config();
/*  27: 24 */     if ((type.equals(TeleportType.SELF)) && (!validWorld(p.getWorld().getName())))
/*  28:    */     {
/*  29: 25 */       rtp.file(p.getName() + ": invalid world: " + p.getWorld().getName());
/*  30: 26 */       p.sendMessage(ChatColor.RED + "RandomTP is not available in this world");
/*  31: 27 */       return;
/*  32:    */     }
/*  33: 30 */     if ((type.equals(TeleportType.SELF)) && (!validPosition(p.getLocation(), p.isFlying())))
/*  34:    */     {
/*  35: 31 */       rtp.file(p.getName() + ": invalid location");
/*  36: 32 */       p.sendMessage(ChatColor.RED + "Cannot teleport from this location. Please be sure you are not falling, jumping, or swimming");
/*  37: 33 */       return;
/*  38:    */     }
/*  39: 36 */     sendTP(type, p.getUniqueId());
/*  40:    */   }
/*  41:    */   
/*  42:    */   private boolean validWorld(String world)
/*  43:    */   {
/*  44: 42 */     List<String> worlds = this.config.getConfig().getStringList("valid_worlds");
/*  45: 43 */     for (int i = 0; i < worlds.size(); i++) {
/*  46: 44 */       if (((String)worlds.get(i)).startsWith("$")) {
/*  47: 45 */         worlds.set(i, ((String)worlds.get(i)).substring(1));
/*  48:    */       }
/*  49:    */     }
/*  50: 49 */     this.rtp.file("valid worlds: " + worlds);
/*  51: 50 */     this.rtp.file("current world: " + world);
/*  52: 52 */     if (worlds.contains(world)) {
/*  53: 53 */       return true;
/*  54:    */     }
/*  55: 56 */     return false;
/*  56:    */   }
/*  57:    */   
/*  58:    */   private boolean validPosition(Location loc, boolean flying)
/*  59:    */   {
/*  60: 60 */     if ((this.config.getConfig().getBoolean("anticheat")) && (
/*  61: 61 */       (loc.getBlock().getType().equals(Material.STATIONARY_LAVA)) || 
/*  62: 62 */       (loc.getBlock().getType().equals(Material.STATIONARY_WATER)) || (
/*  63: 63 */       (loc.subtract(0.0D, 1.0D, 0.0D).getBlock().getType().equals(Material.AIR)) && (!flying)))) {
/*  64: 65 */       return false;
/*  65:    */     }
/*  66: 68 */     return true;
/*  67:    */   }
/*  68:    */   
/*  69:    */   private void sendTP(TeleportType type, UUID uuid)
/*  70:    */   {
/*  71: 73 */     boolean rand = ((Boolean)parse("random_world", Boolean.FALSE, "Invalid value for random_world. Defaulting to false.")).booleanValue();
/*  72: 74 */     List<String> worlds = this.config.getConfig().getStringList("valid_worlds");
/*  73:    */     
/*  74: 76 */     int maxX = ((Integer)parse("radius.max_X", Integer.valueOf(1000), "Invalid value for max_X. Defaulting to 1000")).intValue();
/*  75: 77 */     int maxZ = ((Integer)parse("radius.max_Z", Integer.valueOf(1000), "Invalid value for max_Z. Defaulting to 1000")).intValue();
/*  76: 78 */     int minX = ((Integer)parse("radius.min_X", Integer.valueOf(0), "Invalid value for min_X. Defaulting to 0")).intValue();
/*  77: 79 */     int minZ = ((Integer)parse("radius.min_Z", Integer.valueOf(0), "Invalid value for min_Z. Defaulting to 0")).intValue();
/*  78:    */     
/*  79: 81 */     boolean message = ((Boolean)parse("send_message_on_tp", Boolean.FALSE, "Invalid value for send_message_on_tp")).booleanValue();
/*  80:    */     
/*  81: 83 */     double price = ((Double)parse("price", Double.valueOf(0.0D), "Invalid value for price. Defaulting to 0.0")).doubleValue();
/*  82: 84 */     int cooldown = ((Integer)parse("cooldown", Integer.valueOf(0), "Invalid value for cooldown. Defaulting to 0")).intValue();
/*  83:    */     
/*  84: 86 */     String section = type.toString().toLowerCase();
/*  85: 87 */     boolean priceEnabled = ((Boolean)parse(section + ".price", Boolean.FALSE, "Invalid value for " + section + ".price. Defaulting to false")).booleanValue();
/*  86: 88 */     boolean cooldownEnabled = ((Boolean)parse(section + ".cooldown", Boolean.FALSE, "Invalid value for " + section + ".cooldown. Defaulting to false")).booleanValue();
/*  87:    */     
/*  88: 90 */     List<String> biomes = this.config.getConfig().getStringList("biomes");
/*  89: 91 */     List<String> blocks = this.config.getConfig().getStringList("blocks");
/*  90:    */     
/*  91: 93 */     boolean usingTowny = ((Boolean)parse("towny", Boolean.FALSE, "Invalid value for towny. Defaulting to false.")).booleanValue();
/*  92: 94 */     boolean usingFactions = ((Boolean)parse("factions", Boolean.FALSE, "Invalid value for factions. Defaulting to false.")).booleanValue();
/*  93: 95 */     boolean usingWG = ((Boolean)parse("worldguard", Boolean.FALSE, "Invalid value for worldguard. Defaulting to false.")).booleanValue();
/*  94: 96 */     boolean usingWB = ((Boolean)parse("worldborder", Boolean.FALSE, "Invalid value for worldborder. Defaulting to false.")).booleanValue();
/*  95:    */     
/*  96: 98 */     Task t = new Task(rand, worlds, maxX, maxZ, minX, minZ, message, price, cooldown, priceEnabled, cooldownEnabled, biomes, blocks, uuid, usingTowny, usingFactions, usingWG, usingWB);
/*  97:    */     
/*  98:100 */     int id = Bukkit.getScheduler().runTaskTimer(this.rtp, t, 0L, 1L).getTaskId();
/*  99:    */     
/* 100:102 */     t.setID(id);
/* 101:    */   }
/* 102:    */   
/* 103:    */   private Object parse(String s, Object o, String warning)
/* 104:    */   {
/* 105:106 */     String thing = this.config.getConfig().getString(s);
/* 106:    */     try
/* 107:    */     {
/* 108:109 */       Integer i = Integer.valueOf(Integer.parseInt(thing));
/* 109:110 */       if ((o instanceof Integer)) {
/* 110:111 */         return i;
/* 111:    */       }
/* 112:    */     }
/* 113:    */     catch (Exception localException)
/* 114:    */     {
/* 115:    */       try
/* 116:    */       {
/* 117:116 */         Double d = Double.valueOf(Double.parseDouble(thing));
/* 118:117 */         if ((o instanceof Double)) {
/* 119:118 */           return d;
/* 120:    */         }
/* 121:    */       }
/* 122:    */       catch (Exception localException1)
/* 123:    */       {
/* 124:122 */         if (thing.equalsIgnoreCase("true")) {
/* 125:123 */           return Boolean.valueOf(true);
/* 126:    */         }
/* 127:125 */         if (thing.equalsIgnoreCase("false")) {
/* 128:126 */           return Boolean.valueOf(false);
/* 129:    */         }
/* 130:129 */         this.rtp.log(Level.SEVERE, warning);
/* 131:130 */         this.rtp.file(warning);
/* 132:    */         
/* 133:132 */         this.config.getConfig().set(s, o);
/* 134:    */         try
/* 135:    */         {
/* 136:134 */           this.config.save();
/* 137:    */         }
/* 138:    */         catch (IOException localIOException) {}
/* 139:    */       }
/* 140:    */     }
/* 141:137 */     return o;
/* 142:    */   }
/* 143:    */ }


/* Location:           C:\Users\David\Desktop\RandomTP.jar
 * Qualified Name:     backcab.RandomTP.PreTP
 * JD-Core Version:    0.7.0.1
 */