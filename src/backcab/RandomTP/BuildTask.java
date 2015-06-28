/*  1:   */ package backcab.RandomTP;
/*  2:   */ 
/*  3:   */ import org.bukkit.Bukkit;
/*  4:   */ import org.bukkit.Location;
/*  5:   */ import org.bukkit.Material;
/*  6:   */ import org.bukkit.World;
/*  7:   */ import org.bukkit.block.Block;
/*  8:   */ import org.bukkit.block.BlockState;
/*  9:   */ import org.bukkit.scheduler.BukkitScheduler;
/* 10:   */ import org.bukkit.util.Vector;
/* 11:   */ 
/* 12:   */ public class BuildTask
/* 13:   */   implements Runnable
/* 14:   */ {
/* 15:   */   private Integer x1;
/* 16:   */   private Integer x2;
/* 17:   */   private Integer y1;
/* 18:   */   private Integer y2;
/* 19:   */   private Integer z1;
/* 20:   */   private Integer z2;
/* 21:   */   private World world;
/* 22:   */   private int x;
/* 23:   */   private int y;
/* 24:   */   private Material m;
/* 25:   */   private byte data;
/* 26:   */   private Material old;
/* 27:   */   private int id;
/* 28:   */   
/* 29:   */   public BuildTask(Vector v1, Vector v2, String world, Material m, byte data, Material old)
/* 30:   */   {
/* 31:30 */     this.x1 = Integer.valueOf(Math.min(v1.getBlockX(), v2.getBlockX()));
/* 32:31 */     this.x2 = Integer.valueOf(Math.max(v1.getBlockX(), v2.getBlockX()));
/* 33:32 */     this.y1 = Integer.valueOf(Math.min(v1.getBlockY(), v2.getBlockY()));
/* 34:33 */     this.y2 = Integer.valueOf(Math.max(v1.getBlockY(), v2.getBlockY()));
/* 35:34 */     this.z1 = Integer.valueOf(Math.min(v1.getBlockZ(), v2.getBlockZ()));
/* 36:35 */     this.z2 = Integer.valueOf(Math.max(v1.getBlockZ(), v2.getBlockZ()));
/* 37:36 */     this.world = Bukkit.getWorld(world);
/* 38:   */     
/* 39:38 */     this.x = this.x1.intValue();
/* 40:39 */     this.y = this.y1.intValue();
/* 41:   */     
/* 42:41 */     this.m = m;
/* 43:42 */     this.data = data;
/* 44:43 */     this.old = old;
/* 45:   */   }
/* 46:   */   
/* 47:   */   public void run()
/* 48:   */   {
/* 49:48 */     for (int z = this.z1.intValue(); z <= this.z2.intValue(); z++)
/* 50:   */     {
/* 51:49 */       Block b = new Location(this.world, this.x, this.y, z).getBlock();
/* 52:50 */       if (b.getType().equals(this.old))
/* 53:   */       {
/* 54:51 */         b.setType(this.m, false);
/* 55:52 */         b.setData(this.data);
/* 56:53 */         b.getState().update(true, false);
/* 57:   */       }
/* 58:   */     }
/* 59:56 */     this.y += 1;
/* 60:58 */     if (this.y > this.y2.intValue())
/* 61:   */     {
/* 62:59 */       this.y = this.y1.intValue();
/* 63:60 */       this.x += 1;
/* 64:   */     }
/* 65:63 */     if (this.x > this.x2.intValue()) {
/* 66:64 */       cancel();
/* 67:   */     }
/* 68:   */   }
/* 69:   */   
/* 70:   */   protected void setID(int id)
/* 71:   */   {
/* 72:69 */     this.id = id;
/* 73:   */   }
/* 74:   */   
/* 75:   */   private void cancel()
/* 76:   */   {
/* 77:73 */     Bukkit.getScheduler().cancelTask(this.id);
/* 78:   */   }
/* 79:   */ }


/* Location:           C:\Users\David\Desktop\RandomTP.jar
 * Qualified Name:     backcab.RandomTP.BuildTask
 * JD-Core Version:    0.7.0.1
 */