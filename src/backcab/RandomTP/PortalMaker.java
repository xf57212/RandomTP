package backcab.RandomTP;
      
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.util.Vector;
      
public class PortalMaker{
	private static HashMap<UUID, PortalMaker> map;
	private Vector pos1 = null;
	private Vector pos2 = null;
       
	protected PortalMaker(UUID id){
		map.put(id, this);
	}
       
	protected static void init(){
		map = new HashMap<UUID, PortalMaker>();
	}
       
	protected static PortalMaker getMaker(UUID id){
		return (PortalMaker)map.get(id);
	}
       
	protected void setPos1(Vector l){
		this.pos1 = l;
	}
       
	protected void setPos2(Vector l){
		this.pos2 = l;
	}
	
	protected Vector getPos1(){
		return this.pos1;
	}
       
	protected Vector getPos2(){
		return this.pos2;
	}
}