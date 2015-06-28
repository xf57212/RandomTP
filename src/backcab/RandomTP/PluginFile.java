package backcab.RandomTP;
      
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
      
public class PluginFile{
	private YamlConfiguration file;
	private File f;
       
	protected PluginFile(RandomTP p, String name, boolean copy){
		this.f = new File(p.getDataFolder().toString() + "/" + name + ".yml");
		if (!this.f.exists()) {
			if (copy) {
				p.saveResource(name + ".yml", false);
			} else {
				try{
					this.f.createNewFile();
				}
				catch (IOException localIOException) {}
			}
		}
		this.file = YamlConfiguration.loadConfiguration(this.f);
	}
       
	protected ConfigurationSection get(String path){
		return this.file.getConfigurationSection(path);
	}
       
	protected YamlConfiguration getConfig(){
		return this.file;
	}
	
	protected void save() throws IOException{
		this.file.save(this.f);
		this.file = YamlConfiguration.loadConfiguration(this.f);
	}
       
	protected void reload(){
		this.file = YamlConfiguration.loadConfiguration(this.f);
	}
}