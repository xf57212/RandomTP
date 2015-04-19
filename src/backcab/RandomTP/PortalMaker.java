package backcab.RandomTP;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.util.Vector;

public class PortalMaker {

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
		return map.get(id);
	}
	
	protected void setPos1(Vector l){
		pos1 = l;
	}
	
	protected void setPos2(Vector l){
		pos2 = l;
	}
	
	protected Vector getPos1(){
		return pos1;
	}
	
	protected Vector getPos2(){
		return pos2;
	}
}
