/*   1:    */ package backcab.RandomTP;
/*   2:    */ 
/*   3:    */ import java.util.Set;
/*   4:    */ import org.bukkit.Bukkit;
/*   5:    */ import org.bukkit.ChatColor;
/*   6:    */ import org.bukkit.Location;
/*   7:    */ import org.bukkit.Material;
/*   8:    */ import org.bukkit.block.Block;
/*   9:    */ import org.bukkit.block.Sign;
/*  10:    */ import org.bukkit.configuration.ConfigurationSection;
/*  11:    */ import org.bukkit.configuration.file.YamlConfiguration;
/*  12:    */ import org.bukkit.entity.Player;
/*  13:    */ import org.bukkit.event.EventHandler;
/*  14:    */ import org.bukkit.event.Listener;
/*  15:    */ import org.bukkit.event.block.Action;
/*  16:    */ import org.bukkit.event.block.BlockPhysicsEvent;
/*  17:    */ import org.bukkit.event.block.SignChangeEvent;
/*  18:    */ import org.bukkit.event.player.PlayerInteractEvent;
/*  19:    */ import org.bukkit.event.player.PlayerMoveEvent;
/*  20:    */ import org.bukkit.event.player.PlayerQuitEvent;
/*  21:    */ import org.bukkit.inventory.ItemStack;
/*  22:    */ import org.bukkit.plugin.PluginManager;
/*  23:    */ import org.bukkit.util.Vector;
/*  24:    */ 
/*  25:    */ public class Events
/*  26:    */   implements Listener
/*  27:    */ {
/*  28:    */   private RandomTP rtp;
/*  29:    */   
/*  30:    */   protected Events(RandomTP rtp)
/*  31:    */   {
/*  32: 28 */     this.rtp = rtp;
/*  33:    */   }
/*  34:    */   
/*  35:    */   @EventHandler
/*  36:    */   protected void logoff(PlayerQuitEvent event)
/*  37:    */   {
/*  38: 33 */     Task.cancel(event.getPlayer().getUniqueId());
/*  39:    */   }
/*  40:    */   
/*  41:    */   @EventHandler
/*  42:    */   protected void signClick(PlayerInteractEvent event)
/*  43:    */   {
/*  44: 38 */     Player p = event.getPlayer();
/*  45: 40 */     if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
/*  46: 41 */       return;
/*  47:    */     }
/*  48: 44 */     Block b = event.getClickedBlock();
/*  49: 46 */     if ((!b.getType().equals(Material.SIGN_POST)) && (!b.getType().equals(Material.WALL_SIGN))) {
/*  50: 47 */       return;
/*  51:    */     }
/*  52: 50 */     Sign s = (Sign)b.getState();
/*  53:    */     
/*  54: 52 */     String line = ChatColor.stripColor(s.getLine(0));
/*  55: 54 */     if (!line.equalsIgnoreCase("[randomtp]")) {
/*  56: 55 */       return;
/*  57:    */     }
/*  58: 58 */     if (!this.rtp.checkPermission(p, "randomtp.sign.use", "You do not have permisson to use this sign.")) {
/*  59: 59 */       return;
/*  60:    */     }
/*  61: 62 */     this.rtp.file(p.getName() + ": successfully used sign");
/*  62:    */     
/*  63: 64 */     new PreTP().start(this.rtp, p, TeleportType.SIGN);
/*  64:    */   }
/*  65:    */   
/*  66:    */   @EventHandler
/*  67:    */   protected void makeSign(SignChangeEvent event)
/*  68:    */   {
/*  69: 69 */     String line = event.getLine(0);
/*  70: 70 */     line = ChatColor.stripColor(line);
/*  71:    */     
/*  72: 72 */     Player p = event.getPlayer();
/*  73: 74 */     if (!line.equalsIgnoreCase("[randomtp]"))
/*  74:    */     {
/*  75: 75 */       this.rtp.file(p.getName() + ": " + line);
/*  76: 76 */       return;
/*  77:    */     }
/*  78: 79 */     if (!this.rtp.checkPermission(p, "randomtp.sign.make", null)) {
/*  79: 80 */       return;
/*  80:    */     }
/*  81: 83 */     this.rtp.file(p.getName() + ": successfully made sign");
/*  82:    */     
/*  83: 85 */     event.setLine(0, ChatColor.DARK_BLUE + "[RandomTP]");
/*  84:    */   }
/*  85:    */   
/*  86:    */   @EventHandler
/*  87:    */   protected void playerMove(PlayerMoveEvent event)
/*  88:    */   {
/*  89: 91 */     if (!event.getPlayer().hasPermission("randomtp.portal.use")) {
/*  90: 92 */       return;
/*  91:    */     }
/*  92: 95 */     Location to = event.getTo();
/*  93: 96 */     Location from = event.getFrom();
/*  94: 99 */     if (to.getBlock().getLocation().equals(from.getBlock().getLocation())) {
/*  95:100 */       return;
/*  96:    */     }
/*  97:103 */     if (inPortal(to.toVector()))
/*  98:    */     {
/*  99:104 */       this.rtp.file(event.getPlayer().getName() + ": successfully used portal");
/* 100:105 */       new PreTP().start(this.rtp, event.getPlayer(), TeleportType.PORTAL);
/* 101:106 */       return;
/* 102:    */     }
/* 103:    */   }
/* 104:    */   
/* 105:    */   @EventHandler
/* 106:    */   protected void onClick(PlayerInteractEvent event)
/* 107:    */   {
/* 108:112 */     if (!event.getPlayer().getItemInHand().getType().equals(Material.WOOD_AXE)) {
/* 109:113 */       return;
/* 110:    */     }
/* 111:116 */     if (!this.rtp.checkPermission(event.getPlayer(), "randomtp.portal.make", null)) {
/* 112:117 */       return;
/* 113:    */     }
/* 114:120 */     PortalMaker pm = PortalMaker.getMaker(event.getPlayer().getUniqueId());
/* 115:122 */     if (pm == null) {
/* 116:123 */       pm = new PortalMaker(event.getPlayer().getUniqueId());
/* 117:    */     }
/* 118:126 */     if (event.getAction().equals(Action.LEFT_CLICK_BLOCK))
/* 119:    */     {
/* 120:127 */       event.setCancelled(true);
/* 121:128 */       Location loc = event.getClickedBlock().getLocation();
/* 122:129 */       pm.setPos1(loc.toVector());
/* 123:131 */       if (!Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
/* 124:132 */         event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Position 1 set to " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ".");
/* 125:    */       }
/* 126:    */     }
/* 127:136 */     if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
/* 128:    */     {
/* 129:137 */       event.setCancelled(true);
/* 130:138 */       Location loc = event.getClickedBlock().getLocation();
/* 131:139 */       pm.setPos2(loc.toVector());
/* 132:141 */       if (!Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
/* 133:142 */         event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Position 2 set to " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ".");
/* 134:    */       }
/* 135:    */     }
/* 136:    */   }
/* 137:    */   
/* 138:    */   @EventHandler
/* 139:    */   protected void preventUpdate(BlockPhysicsEvent event)
/* 140:    */   {
/* 141:149 */     if (!this.rtp.config().getConfig().getBoolean("preventPortalUpdate")) {
/* 142:150 */       return;
/* 143:    */     }
/* 144:153 */     Location loc = event.getBlock().getLocation();
/* 145:155 */     if (inPortal(loc.toVector())) {
/* 146:156 */       event.setCancelled(true);
/* 147:    */     }
/* 148:    */   }
/* 149:    */   
/* 150:    */   private boolean inPortal(Vector v)
/* 151:    */   {
/* 152:161 */     Set<String> portals = this.rtp.portals().getConfig().getKeys(false);
/* 153:163 */     for (String portal : portals)
/* 154:    */     {
/* 155:164 */       ConfigurationSection sect = this.rtp.portals().get(portal);
/* 156:    */       
/* 157:166 */       Vector v1 = sect.getVector("v1");
/* 158:167 */       Vector v2 = sect.getVector("v2");
/* 159:    */       
/* 160:169 */       Vector max = Vector.getMaximum(v1, v2);
/* 161:170 */       Vector min = Vector.getMinimum(v1, v2);
/* 162:    */       
/* 163:172 */       v.setX(v.getBlockX());
/* 164:173 */       v.setY(v.getBlockY());
/* 165:174 */       v.setZ(v.getBlockZ());
/* 166:176 */       if (v.isInAABB(min, max)) {
/* 167:177 */         return true;
/* 168:    */       }
/* 169:    */     }
/* 170:188 */     return false;
/* 171:    */   }
/* 172:    */ }


/* Location:           C:\Users\David\Desktop\RandomTP.jar
 * Qualified Name:     backcab.RandomTP.Events
 * JD-Core Version:    0.7.0.1
 */