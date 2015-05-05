package backcab.RandomTP;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class PluginFile {
	private YamlConfiguration file;
	private File f;
	
	private RandomTP p;
	private String name;
	
	protected PluginFile(RandomTP p, String name, boolean copy){
		f = new File(p.getDataFolder().toString() + "/" + name + ".yml");
		if(!f.exists()){
			if(copy){
				p.saveResource(name + ".yml", false);
			} else {
				try {
					f.createNewFile();
				} catch (IOException e) {}
			}
		}
		reload();
		try{
			update();
		} catch(Exception e) {}
	}
	
	protected ConfigurationSection get(String path){
		return file.getConfigurationSection(path);
	}
	
	protected YamlConfiguration getConfig(){
		return file;
	}
	
	protected void save() throws IOException{
		file.save(f);
		reload();
	}
	
	protected void reload(){
		file = YamlConfiguration.loadConfiguration(f);
	}
	
	protected void update() throws IOException{
		p.saveResource(name + ".yml", false);
		YamlConfiguration temp = YamlConfiguration.loadConfiguration(f);
		for(String s: file.getKeys(true)){
			temp.set(s, file.get(s));
		}
		save();
	}
}
