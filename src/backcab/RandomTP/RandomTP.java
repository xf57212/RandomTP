/*   1:    */ package backcab.RandomTP;
/*   2:    */ 
/*   3:    */ import java.io.BufferedWriter;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileWriter;
/*   6:    */ import java.io.IOException;
/*   7:    */ import java.io.PrintWriter;
/*   8:    */ import java.util.Set;
/*   9:    */ import java.util.logging.Level;
/*  10:    */ import java.util.logging.Logger;
/*  11:    */ import org.bukkit.Bukkit;
/*  12:    */ import org.bukkit.ChatColor;
/*  13:    */ import org.bukkit.Server;
/*  14:    */ import org.bukkit.command.CommandSender;
/*  15:    */ import org.bukkit.command.PluginCommand;
/*  16:    */ import org.bukkit.configuration.ConfigurationSection;
/*  17:    */ import org.bukkit.configuration.file.YamlConfiguration;
/*  18:    */ import org.bukkit.plugin.PluginDescriptionFile;
/*  19:    */ import org.bukkit.plugin.PluginManager;
/*  20:    */ import org.bukkit.plugin.java.JavaPlugin;
/*  21:    */ import org.bukkit.util.Vector;
/*  22:    */ 
/*  23:    */ public class RandomTP
/*  24:    */   extends JavaPlugin
/*  25:    */ {
/*  26:    */   private PluginFile config;
/*  27:    */   private PluginFile portals;
/*  28:    */   
/*  29:    */   public void onEnable()
/*  30:    */   {
/*  31: 24 */     this.config = new PluginFile(this, "config", true);
/*  32: 25 */     this.portals = new PluginFile(this, "portals", false);
/*  33:    */     
/*  34: 27 */     file("Server Version: " + getServer().getVersion());
/*  35: 28 */     file("RandomTP Version: " + getDescription().getVersion());
/*  36:    */     
/*  37: 30 */     PortalMaker.init();
/*  38: 31 */     Task.init(this);
/*  39:    */     
/*  40: 33 */     Bukkit.getPluginManager().registerEvents(new Events(this), this);
/*  41: 34 */     getCommand("randomtp").setExecutor(new TPCommand(this));
/*  42:    */     
/*  43: 36 */     log(Level.INFO, "Checking/Fixing format of portals.yml");
/*  44: 37 */     updatePortals();
/*  45:    */   }
/*  46:    */   
/*  47:    */   private void updatePortals()
/*  48:    */   {
/*  49: 41 */     Set<String> portalList = this.portals.getConfig().getKeys(false);
/*  50: 42 */     for (String portal : portalList) {
/*  51: 43 */       if (!this.portals.getConfig().contains(portal + ".world"))
/*  52:    */       {
/*  53: 44 */         String[] s = this.portals.getConfig().getString(portal).split(":");
/*  54: 45 */         String world = s[0];
/*  55: 46 */         Vector v1 = new Vector(Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3]));
/*  56: 47 */         Vector v2 = new Vector(Integer.parseInt(s[4]), Integer.parseInt(s[5]), Integer.parseInt(s[6]));
/*  57: 48 */         String material = s[7];
/*  58:    */         
/*  59:    */ 
/*  60: 51 */         this.portals.getConfig().set(portal, null);
/*  61:    */         
/*  62: 53 */         ConfigurationSection sect = this.portals.getConfig().createSection(portal);
/*  63: 54 */         sect.set("world", world);
/*  64: 55 */         sect.set("v1", v1);
/*  65: 56 */         sect.set("v2", v2);
/*  66: 57 */         sect.set("material", material);
/*  67:    */       }
/*  68:    */     }
/*  69:    */     try
/*  70:    */     {
/*  71: 62 */       this.portals.save();
/*  72:    */     }
/*  73:    */     catch (IOException localIOException) {}
/*  74:    */   }
/*  75:    */   
/*  76:    */   protected void log(Level level, String message)
/*  77:    */   {
/*  78: 67 */     getLogger().log(level, message);
/*  79:    */   }
/*  80:    */   
/*  81:    */   protected void file(String message)
/*  82:    */   {
/*  83: 71 */     if (!this.config.getConfig().getBoolean("debug")) {
/*  84: 72 */       return;
/*  85:    */     }
/*  86: 75 */     File debug = new File(getDataFolder(), "debug.log");
/*  87: 76 */     if (!debug.exists()) {
/*  88:    */       try
/*  89:    */       {
/*  90: 78 */         debug.createNewFile();
/*  91:    */       }
/*  92:    */       catch (IOException localIOException) {}
/*  93:    */     }
/*  94:    */     try
/*  95:    */     {
/*  96: 83 */       PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(debug, true)));
/*  97: 84 */       out.println(message);
/*  98: 85 */       out.close();
/*  99:    */     }
/* 100:    */     catch (IOException localIOException1) {}
/* 101:    */   }
/* 102:    */   
/* 103:    */   protected boolean checkPermission(CommandSender sender, String perm, String message)
/* 104:    */   {
/* 105: 90 */     if (!sender.hasPermission(perm))
/* 106:    */     {
/* 107: 91 */       file(sender.getName() + " does not have " + perm);
/* 108: 92 */       if (message != null) {
/* 109: 93 */         sender.sendMessage(ChatColor.RED + message);
/* 110:    */       }
/* 111: 95 */       return false;
/* 112:    */     }
/* 113: 98 */     return true;
/* 114:    */   }
/* 115:    */   
/* 116:    */   protected PluginFile config()
/* 117:    */   {
/* 118:102 */     return this.config;
/* 119:    */   }
/* 120:    */   
/* 121:    */   protected PluginFile portals()
/* 122:    */   {
/* 123:106 */     return this.portals;
/* 124:    */   }
/* 125:    */ }


/* Location:           C:\Users\David\Desktop\RandomTP.jar
 * Qualified Name:     backcab.RandomTP.RandomTP
 * JD-Core Version:    0.7.0.1
 */