/*  1:   */ package backcab.RandomTP;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ import java.io.IOException;
/*  5:   */ import org.bukkit.configuration.ConfigurationSection;
/*  6:   */ import org.bukkit.configuration.file.YamlConfiguration;
/*  7:   */ 
/*  8:   */ public class PluginFile
/*  9:   */ {
/* 10:   */   private YamlConfiguration file;
/* 11:   */   private File f;
/* 12:   */   
/* 13:   */   protected PluginFile(RandomTP p, String name, boolean copy)
/* 14:   */   {
/* 15:14 */     this.f = new File(p.getDataFolder().toString() + "/" + name + ".yml");
/* 16:15 */     if (!this.f.exists()) {
/* 17:16 */       if (copy) {
/* 18:17 */         p.saveResource(name + ".yml", false);
/* 19:   */       } else {
/* 20:   */         try
/* 21:   */         {
/* 22:20 */           this.f.createNewFile();
/* 23:   */         }
/* 24:   */         catch (IOException localIOException) {}
/* 25:   */       }
/* 26:   */     }
/* 27:24 */     this.file = YamlConfiguration.loadConfiguration(this.f);
/* 28:   */   }
/* 29:   */   
/* 30:   */   protected ConfigurationSection get(String path)
/* 31:   */   {
/* 32:28 */     return this.file.getConfigurationSection(path);
/* 33:   */   }
/* 34:   */   
/* 35:   */   protected YamlConfiguration getConfig()
/* 36:   */   {
/* 37:32 */     return this.file;
/* 38:   */   }
/* 39:   */   
/* 40:   */   protected void save()
/* 41:   */     throws IOException
/* 42:   */   {
/* 43:36 */     this.file.save(this.f);
/* 44:37 */     this.file = YamlConfiguration.loadConfiguration(this.f);
/* 45:   */   }
/* 46:   */   
/* 47:   */   protected void reload()
/* 48:   */   {
/* 49:41 */     this.file = YamlConfiguration.loadConfiguration(this.f);
/* 50:   */   }
/* 51:   */ }


/* Location:           C:\Users\David\Desktop\RandomTP.jar
 * Qualified Name:     backcab.RandomTP.PluginFile
 * JD-Core Version:    0.7.0.1
 */