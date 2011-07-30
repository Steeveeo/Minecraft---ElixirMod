package me.steeveeo.ElixirMod;

import java.io.File;
import java.util.List;

import me.steeveeo.ElixirMod.Potions.ElixirModPotion;

import org.bukkit.util.config.Configuration;

//Credit for this goes to JayJay110 on the Bukkit Forums!
//http://forums.bukkit.org/threads/tutorial-create-a-configuration-file-with-yaml.15975/
public class ElixirModFileConfig {
	
	private static ElixirMod plugin;
    public ElixirModFileConfig(ElixirMod instance) {
        plugin = instance;
    }

       public String directory = "plugins" + File.separator +"ElixirMod";
       File file = new File(directory + File.separator + "config.yml");


    public void configCheck(){
        new File(directory).mkdir();


        if(!file.exists()){
            try {
                file.createNewFile();
                addDefaults();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {

            loadkeys();
        }
    }
    public void write(String root, Object x){
        Configuration config = load();
        config.setProperty(root, x);
        config.save();
    }
    public Boolean readBoolean(String root){
        Configuration config = load();
        return config.getBoolean(root, true);
    }
	public Double readDouble(String root){
        Configuration config = load();
        return config.getDouble(root, 0);
    }
	public int readInt(String root){
        Configuration config = load();
        return config.getInt(root, 0);
    }
    public List<String> readStringList(String root){
        Configuration config = load();
        return config.getKeys(root);
    }
    public String readString(String root){
        Configuration config = load();
        return config.getString(root);
    }
    private Configuration load(){

        try {
            Configuration config = new Configuration(file);
            config.load();
            return config;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private void addDefaults(){
        plugin.log.info("[ElixirMod] Config File Not Found, Generating...");
        
        //Global Toxicity Stuff
        write("Minimum Toxicity For Damage",50);
        write("Toxicity Per Damage",20);
        write("Toxicity Tick Length",3);
        
        //Call each potion loaded to write configs.
        ElixirModPotion[] PotionList = plugin.ElixirMod_PotionController.PotionList;
        for(int index = 0; index < PotionList.length; index++)
        {
        	ElixirModPotion thisPotion = PotionList[index];
        	if(thisPotion != null)
        	{
        		thisPotion.configDefaults(this);
        	}
        }
        
        loadkeys();
        
        plugin.log.info("[ElixirMod] Config File Generation Complete.");
    }
    private void loadkeys()
    {
        //Global Toxicity Stuff
		plugin.ElixirMod_PotionController.Toxicity_minToxicityForDamage = readInt("Minimum Toxicity For Damage");
		plugin.ElixirMod_PotionController.Toxicity_toxicityPerDamage = readInt("Toxicity Per Damage");
		plugin.ElixirMod_PotionController.Toxicity_tickLength = readInt("Toxicity Tick Length");
		
        //Call each potion loaded to read configs and load in data.
        ElixirModPotion[] PotionList = plugin.ElixirMod_PotionController.PotionList;
        for(int index = 0; index < PotionList.length; index++)
        {
        	ElixirModPotion thisPotion = PotionList[index];
        	if(thisPotion != null)
        	{
        		thisPotion.configLoad(this);
        	}
        }
    }
}
