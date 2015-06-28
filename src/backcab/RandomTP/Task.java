/*   1:    */ package backcab.RandomTP;
/*   2:    */ 
/*   3:    */ import com.massivecraft.factions.entity.BoardColl;
/*   4:    */ import com.massivecraft.factions.entity.Faction;
/*   5:    */ import com.massivecraft.massivecore.ps.PS;
/*   6:    */ import com.palmergames.bukkit.towny.object.TownBlock;
/*   7:    */ import com.palmergames.bukkit.towny.object.TownyUniverse;
/*   8:    */ import com.sk89q.worldedit.bukkit.BukkitUtil;
/*   9:    */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*  10:    */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*  11:    */ import com.wimbli.WorldBorder.BorderData;
/*  12:    */ import com.wimbli.WorldBorder.Config;
/*  13:    */ import java.util.ArrayList;
/*  14:    */ import java.util.HashMap;
/*  15:    */ import java.util.List;
/*  16:    */ import java.util.UUID;
/*  17:    */ import net.milkbowl.vault.economy.Economy;
/*  18:    */ import net.milkbowl.vault.economy.EconomyResponse;
/*  19:    */ import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
/*  20:    */ import org.bukkit.Bukkit;
/*  21:    */ import org.bukkit.ChatColor;
/*  22:    */ import org.bukkit.Chunk;
/*  23:    */ import org.bukkit.Location;
/*  24:    */ import org.bukkit.Material;
/*  25:    */ import org.bukkit.Server;
/*  26:    */ import org.bukkit.World;
/*  27:    */ import org.bukkit.block.Biome;
/*  28:    */ import org.bukkit.block.Block;
/*  29:    */ import org.bukkit.entity.Player;
/*  30:    */ import org.bukkit.plugin.PluginManager;
/*  31:    */ import org.bukkit.plugin.RegisteredServiceProvider;
/*  32:    */ import org.bukkit.plugin.ServicesManager;
/*  33:    */ import org.bukkit.scheduler.BukkitScheduler;
/*  34:    */ 
/*  35:    */ public class Task
/*  36:    */   implements Runnable
/*  37:    */ {
/*  38:    */   private boolean rand;
/*  39:    */   private boolean message;
/*  40:    */   private boolean priceEnabled;
/*  41:    */   private boolean cooldownEnabled;
/*  42:    */   private boolean usingTowny;
/*  43:    */   private boolean usingFactions;
/*  44:    */   private boolean usingWG;
/*  45:    */   private boolean usingWB;
/*  46:    */   private List<String> worldList;
/*  47:    */   private List<String> biomes;
/*  48:    */   private List<String> blocks;
/*  49:    */   private int maxX;
/*  50:    */   private int maxZ;
/*  51:    */   private int minX;
/*  52:    */   private int minZ;
/*  53:    */   private int cooldown;
/*  54:    */   private double price;
/*  55:    */   private UUID id;
/*  56:    */   private static HashMap<UUID, Integer> players;
/*  57:    */   private static HashMap<UUID, Long> cooldowns;
/*  58:    */   private static RandomTP rtp;
/*  59:    */   
/*  60:    */   protected Task(boolean rand, List<String> worlds, int maxX, int maxZ, int minX, int minZ, boolean message, double price, int cooldown, boolean priceEnabled, boolean cooldownEnabled, List<String> biomes, List<String> blocks, UUID id, boolean usingTowny, boolean usingFactions, boolean usingWG, boolean usingWB)
/*  61:    */   {
/*  62: 42 */     cancel(id);
/*  63:    */     
/*  64: 44 */     this.rand = rand;
/*  65: 45 */     this.worldList = worlds;
/*  66: 46 */     this.maxX = maxX;
/*  67: 47 */     this.maxZ = maxZ;
/*  68: 48 */     this.minX = minX;
/*  69: 49 */     this.minZ = minZ;
/*  70: 50 */     this.message = message;
/*  71: 51 */     this.price = price;
/*  72: 52 */     this.cooldown = cooldown;
/*  73: 53 */     this.priceEnabled = priceEnabled;
/*  74: 54 */     this.cooldownEnabled = cooldownEnabled;
/*  75: 55 */     this.biomes = biomes;
/*  76: 56 */     this.blocks = blocks;
/*  77: 57 */     this.id = id;
/*  78: 58 */     this.usingTowny = usingTowny;
/*  79: 59 */     this.usingFactions = usingFactions;
/*  80: 60 */     this.usingWG = usingWG;
/*  81: 61 */     this.usingWB = usingWB;
/*  82:    */     
/*  83: 63 */     rtp.file("Initialized task for " + Bukkit.getPlayer(id).getName() + 
/*  84: 64 */       "\n  rand: " + rand + 
/*  85: 65 */       "\n  worlds: " + worlds + 
/*  86: 66 */       "\n  max X: " + maxX + 
/*  87: 67 */       "\n  max Z: " + maxZ + 
/*  88: 68 */       "\n  min X: " + minX + 
/*  89: 69 */       "\n  min Z: " + minZ + 
/*  90: 70 */       "\n  message: " + message + 
/*  91: 71 */       "\n price: " + price + 
/*  92: 72 */       "\n cooldown: " + cooldown + 
/*  93: 73 */       "\n priceEnabled: " + priceEnabled + 
/*  94: 74 */       "\n cooldownEnabled: " + cooldownEnabled + 
/*  95: 75 */       "\n biomes: " + biomes + 
/*  96: 76 */       "\n blocks: " + blocks + 
/*  97: 77 */       "\n id: " + id + 
/*  98: 78 */       "\n usingWB: " + usingWB);
/*  99:    */   }
/* 100:    */   
/* 101:    */   protected static void init(RandomTP rtp)
/* 102:    */   {
/* 103: 82 */     players = new HashMap();
/* 104: 83 */     cooldowns = new HashMap();
/* 105: 84 */     rtp = rtp;
/* 106:    */   }
/* 107:    */   
/* 108:    */   protected void setID(int taskId)
/* 109:    */   {
/* 110: 88 */     players.put(this.id, Integer.valueOf(taskId));
/* 111:    */   }
/* 112:    */   
/* 113:    */   protected static void cancel(UUID uuid)
/* 114:    */   {
/* 115: 92 */     if (players.containsKey(uuid))
/* 116:    */     {
/* 117: 93 */       Bukkit.getScheduler().cancelTask(((Integer)players.get(uuid)).intValue());
/* 118: 94 */       players.remove(uuid);
/* 119:    */     }
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void run()
/* 123:    */   {
/* 124:100 */     Player p = Bukkit.getServer().getPlayer(this.id);
/* 125:    */     
/* 126:102 */     Location loc = findLocation(p.getWorld());
/* 127:105 */     if (loc == null) {
/* 128:106 */       return;
/* 129:    */     }
/* 130:110 */     if ((this.cooldownEnabled) && (cooldowns.containsKey(this.id)))
/* 131:    */     {
/* 132:111 */       long time = System.currentTimeMillis() - ((Long)cooldowns.get(this.id)).longValue();
/* 133:112 */       int seconds = (int)(time / 1000.0D);
/* 134:    */       
/* 135:114 */       rtp.file(p.getName() + ": time (s): " + seconds + "   " + time);
/* 136:116 */       if (seconds >= this.cooldown)
/* 137:    */       {
/* 138:117 */         cooldowns.remove(this.id);
/* 139:    */       }
/* 140:    */       else
/* 141:    */       {
/* 142:119 */         p.sendMessage(ChatColor.GOLD + "You must wait another " + ChatColor.RED + (this.cooldown - seconds) + " seconds " + 
/* 143:120 */           ChatColor.GOLD + "to randomly teleport again.");
/* 144:121 */         cancel(this.id);
/* 145:122 */         return;
/* 146:    */       }
/* 147:    */     }
/* 148:127 */     if ((Bukkit.getServer().getPluginManager().isPluginEnabled("Vault")) && (this.priceEnabled) && (this.price != 0.0D))
/* 149:    */     {
/* 150:128 */       Economy econ = null;
/* 151:129 */       RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
/* 152:130 */       if (rsp != null) {
/* 153:131 */         econ = (Economy)rsp.getProvider();
/* 154:    */       }
/* 155:134 */       EconomyResponse.ResponseType type = econ.withdrawPlayer(p, this.price).type;
/* 156:136 */       if (!type.equals(EconomyResponse.ResponseType.SUCCESS))
/* 157:    */       {
/* 158:137 */         p.sendMessage(ChatColor.GOLD + "Not enough money. Random teleports cost $" + this.price);
/* 159:138 */         rtp.file(p.getName() + " tried to teleport. Not enough money");
/* 160:139 */         cancel(this.id);
/* 161:140 */         return;
/* 162:    */       }
/* 163:    */     }
/* 164:145 */     if ((!rtp.checkPermission(p, "randomtp.cdexempt", null)) && (this.cooldown != 0) && (this.cooldownEnabled))
/* 165:    */     {
/* 166:146 */       rtp.file("Adding cooldown for " + p.getName());
/* 167:147 */       cooldowns.put(p.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
/* 168:    */     }
/* 169:151 */     loc.getChunk().load(true);
/* 170:154 */     if (this.message) {
/* 171:155 */       p.sendMessage(getMessage(loc));
/* 172:    */     }
/* 173:159 */     p.teleport(loc);
/* 174:    */     
/* 175:161 */     cancel(this.id);
/* 176:    */     
/* 177:163 */     players.remove(this.id);
/* 178:    */   }
/* 179:    */   
/* 180:    */   private Location findLocation(World w)
/* 181:    */   {
/* 182:167 */     World world = null;
/* 183:170 */     if (this.rand)
/* 184:    */     {
/* 185:171 */       ArrayList<World> worlds = new ArrayList();
/* 186:172 */       for (String sworld : this.worldList) {
/* 187:173 */         if (!sworld.startsWith("$")) {
/* 188:174 */           worlds.add(Bukkit.getServer().getWorld(sworld));
/* 189:    */         }
/* 190:    */       }
/* 191:178 */       world = (World)worlds.get((int)(Math.random() * worlds.size()));
/* 192:    */     }
/* 193:    */     else
/* 194:    */     {
/* 195:180 */       world = w;
/* 196:    */     }
/* 197:184 */     rtp.file("Attempting to find safe location in " + world.getName() + " for " + Bukkit.getPlayer(this.id).getName());
/* 198:    */     
/* 199:186 */     Location spawn = world.getSpawnLocation();
/* 200:189 */     if ((this.usingWB) && (Bukkit.getPluginManager().isPluginEnabled("WorldBorder")))
/* 201:    */     {
/* 202:190 */       BorderData bd = Config.Border(world.getName());
/* 203:191 */       this.maxX = bd.getRadiusX();
/* 204:192 */       this.maxZ = bd.getRadiusZ();
/* 205:    */     }
/* 206:196 */     int x = (int)(Math.random() * this.maxX);
/* 207:197 */     int z = (int)(Math.random() * this.maxZ);
/* 208:200 */     while ((x < this.minX) && (z < this.minZ))
/* 209:    */     {
/* 210:201 */       double r = Math.random();
/* 211:202 */       if (r < 0.5D) {
/* 212:203 */         x++;
/* 213:    */       } else {
/* 214:205 */         z++;
/* 215:    */       }
/* 216:    */     }
/* 217:209 */     if (Math.random() > 0.5D) {
/* 218:210 */       x *= -1;
/* 219:    */     }
/* 220:212 */     if (Math.random() > 0.5D) {
/* 221:213 */       z *= -1;
/* 222:    */     }
/* 223:217 */     x += spawn.getBlockX();
/* 224:218 */     z += spawn.getBlockZ();
/* 225:    */     
/* 226:    */ 
/* 227:221 */     Block b = world.getHighestBlockAt(x, z).getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();
/* 228:222 */     rtp.file("Testing block " + b.getType().toString() + " in biome " + b.getBiome().toString() + " at " + b.getLocation());
/* 229:223 */     if ((this.biomes.contains(b.getBiome().name())) || (this.blocks.contains(b.getType().name())))
/* 230:    */     {
/* 231:224 */       b.getChunk().unload(true, true);
/* 232:225 */       return null;
/* 233:    */     }
/* 234:229 */     Location loc = b.getLocation().add(0.5D, 1.0D, 0.5D);
/* 235:232 */     if ((this.usingWB) && (Bukkit.getPluginManager().isPluginEnabled("WorldBorder")))
/* 236:    */     {
/* 237:233 */       rtp.file("Checking if location is inside border");
/* 238:234 */       BorderData bd = Config.Border(world.getName());
/* 239:235 */       if (!bd.insideBorder(loc))
/* 240:    */       {
/* 241:236 */         rtp.file("Outside of worldborder");
/* 242:237 */         b.getChunk().unload(true, true);
/* 243:238 */         return null;
/* 244:    */       }
/* 245:    */     }
/* 246:242 */     rtp.file("Checking if location is claimed");
/* 247:243 */     if (claimCheck(loc))
/* 248:    */     {
/* 249:244 */       rtp.file("In claimed area.");
/* 250:245 */       b.getChunk().unload(true, true);
/* 251:246 */       return null;
/* 252:    */     }
/* 253:249 */     rtp.file("Safe location found at " + loc);
/* 254:250 */     return loc;
/* 255:    */   }
/* 256:    */   
/* 257:    */   private boolean claimCheck(Location loc)
/* 258:    */   {
/* 259:255 */     PluginManager pm = rtp.getServer().getPluginManager();
/* 260:258 */     if ((this.usingTowny) && (pm.isPluginEnabled("Towny")))
/* 261:    */     {
/* 262:259 */       TownBlock tb = TownyUniverse.getTownBlock(loc);
/* 263:260 */       if (tb != null) {
/* 264:261 */         return true;
/* 265:    */       }
/* 266:    */     }
/* 267:266 */     if ((this.usingFactions) && (pm.isPluginEnabled("MassiveCore")) && (pm.isPluginEnabled("Factions")))
/* 268:    */     {
/* 269:267 */       PS ps = PS.valueOf(loc).getChunk(true);
/* 270:268 */       Faction f = BoardColl.get().getFactionAt(ps);
/* 271:270 */       if (!f.getId().equals("none")) {
/* 272:271 */         return true;
/* 273:    */       }
/* 274:    */     }
/* 275:276 */     if ((this.usingWG) && (pm.isPluginEnabled("WorldGuard")))
/* 276:    */     {
/* 277:277 */       RegionManager rm = WorldGuardPlugin.inst().getRegionManager(loc.getWorld());
/* 278:278 */       if (rm != null)
/* 279:    */       {
/* 280:279 */         List<String> list = rm.getApplicableRegionsIDs(BukkitUtil.toVector(loc));
/* 281:280 */         if ((list != null) && (list.size() != 0)) {
/* 282:281 */           return true;
/* 283:    */         }
/* 284:    */       }
/* 285:    */     }
/* 286:286 */     return false;
/* 287:    */   }
/* 288:    */   
/* 289:    */   private String getMessage(Location loc)
/* 290:    */   {
/* 291:290 */     Location spawn = loc.getWorld().getSpawnLocation();
/* 292:291 */     double x = loc.getX() - spawn.getX();
/* 293:292 */     double y = loc.getZ() - spawn.getZ();
/* 294:293 */     double distance = Math.sqrt(Math.pow(x, 2.0D) + Math.pow(y, 2.0D));
/* 295:294 */     String direction = "";
/* 296:295 */     double slope = 0.0D;
/* 297:297 */     if ((x == 0.0D) && (y < 0.0D)) {
/* 298:298 */       direction = "North";
/* 299:300 */     } else if ((x == 0.0D) && (y > 0.0D)) {
/* 300:301 */       direction = "South";
/* 301:303 */     } else if ((y == 0.0D) && (x > 0.0D)) {
/* 302:304 */       direction = "East";
/* 303:306 */     } else if ((y == 0.0D) && (x < 0.0D)) {
/* 304:307 */       direction = "West";
/* 305:    */     } else {
/* 306:310 */       slope = Math.toDegrees(Math.atan(y / x));
/* 307:    */     }
/* 308:313 */     if (x > 0.0D)
/* 309:    */     {
/* 310:314 */       if ((slope > -90.0D) && (slope <= -67.5D)) {
/* 311:315 */         direction = "North";
/* 312:317 */       } else if ((slope > -67.5D) && (slope <= -22.5D)) {
/* 313:318 */         direction = "Northeast";
/* 314:320 */       } else if ((slope > -22.5D) && (slope <= 22.5D)) {
/* 315:321 */         direction = "East";
/* 316:323 */       } else if ((slope > 22.5D) && (slope <= 67.5D)) {
/* 317:324 */         direction = "Southeast";
/* 318:326 */       } else if ((slope > 67.5D) && (slope <= 90.0D)) {
/* 319:327 */         direction = "South";
/* 320:    */       }
/* 321:    */     }
/* 322:330 */     else if ((slope > -90.0D) && (slope <= -67.5D)) {
/* 323:331 */       direction = "South";
/* 324:333 */     } else if ((slope > -67.5D) && (slope <= -22.5D)) {
/* 325:334 */       direction = "Southwest";
/* 326:336 */     } else if ((slope > -22.5D) && (slope <= 22.5D)) {
/* 327:337 */       direction = "West";
/* 328:339 */     } else if ((slope > 22.5D) && (slope <= 67.5D)) {
/* 329:340 */       direction = "Northwest";
/* 330:342 */     } else if ((slope > 67.5D) && (slope <= 90.0D)) {
/* 331:343 */       direction = "North";
/* 332:    */     }
/* 333:347 */     return 
/* 334:348 */       ChatColor.GOLD + "You have been teleported " + ChatColor.RED + String.format("%.2f", new Object[] { Double.valueOf(distance) }) + " blocks " + direction + ChatColor.GOLD + " of " + ChatColor.RED + loc.getWorld().getName() + "'s" + ChatColor.GOLD + " spawn.";
/* 335:    */   }
/* 336:    */ }


/* Location:           C:\Users\David\Desktop\RandomTP.jar
 * Qualified Name:     backcab.RandomTP.Task
 * JD-Core Version:    0.7.0.1
 */