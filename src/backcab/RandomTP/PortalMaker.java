/*  1:   */ package backcab.RandomTP;
/*  2:   */ 
/*  3:   */ import java.util.HashMap;
/*  4:   */ import java.util.UUID;
/*  5:   */ import org.bukkit.util.Vector;
/*  6:   */ 
/*  7:   */ public class PortalMaker
/*  8:   */ {
/*  9:   */   private static HashMap<UUID, PortalMaker> map;
/* 10:12 */   private Vector pos1 = null;
/* 11:13 */   private Vector pos2 = null;
/* 12:   */   
/* 13:   */   protected PortalMaker(UUID id)
/* 14:   */   {
/* 15:16 */     map.put(id, this);
/* 16:   */   }
/* 17:   */   
/* 18:   */   protected static void init()
/* 19:   */   {
/* 20:20 */     map = new HashMap();
/* 21:   */   }
/* 22:   */   
/* 23:   */   protected static PortalMaker getMaker(UUID id)
/* 24:   */   {
/* 25:24 */     return (PortalMaker)map.get(id);
/* 26:   */   }
/* 27:   */   
/* 28:   */   protected void setPos1(Vector l)
/* 29:   */   {
/* 30:28 */     this.pos1 = l;
/* 31:   */   }
/* 32:   */   
/* 33:   */   protected void setPos2(Vector l)
/* 34:   */   {
/* 35:32 */     this.pos2 = l;
/* 36:   */   }
/* 37:   */   
/* 38:   */   protected Vector getPos1()
/* 39:   */   {
/* 40:36 */     return this.pos1;
/* 41:   */   }
/* 42:   */   
/* 43:   */   protected Vector getPos2()
/* 44:   */   {
/* 45:40 */     return this.pos2;
/* 46:   */   }
/* 47:   */ }


/* Location:           C:\Users\David\Desktop\RandomTP.jar
 * Qualified Name:     backcab.RandomTP.PortalMaker
 * JD-Core Version:    0.7.0.1
 */