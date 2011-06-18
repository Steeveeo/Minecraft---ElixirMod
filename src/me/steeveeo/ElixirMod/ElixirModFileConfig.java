package me.steeveeo.ElixirMod;

import java.io.File;
import java.util.List;

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
    private void write(String root, Object x){
        Configuration config = load();
        config.setProperty(root, x);
        config.save();
    }
    private Boolean readBoolean(String root){
        Configuration config = load();
        return config.getBoolean(root, true);
    }
    @SuppressWarnings("unused")
	private Double readDouble(String root){
        Configuration config = load();
        return config.getDouble(root, 0);
    }
    private int readInt(String root){
        Configuration config = load();
        return config.getInt(root, 0);
    }
    @SuppressWarnings("unused")
    private List<String> readStringList(String root){
        Configuration config = load();
        return config.getKeys(root);
    }
    @SuppressWarnings("unused")
    private String readString(String root){
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
        plugin.log.info("[ElixirMod] Generating Config File...");
        
        write(plugin.enabledstartup, true);
        
        //Cactus Rum
        write("Cactus Rum.baseDamage",6);
        write("Cactus Rum.toxicityPerDamage",6);
        write("Cactus Rum.toxicityPerDrink",30);
        write("Cactus Rum.ticksPerDrink",60);
        write("Cactus Rum.tickLength",3);
        
        //Elixir of Haste
        write("Elixir of Haste.baseDamage",0);
        write("Elixir of Haste.Minimum Toxicity for Damage",50);
        write("Elixir of Haste.toxicityPerDamage",15);
        write("Elixir of Haste.toxicityPerDrink",20);
        write("Elixir of Haste.ticksPerDrink",10);
        write("Elixir of Haste.tickLength",6);
        
        loadkeys();
    }
    private void loadkeys()
    {
    	plugin.enabled = readBoolean(plugin.enabledstartup);
        
        //Cactus Rum
		plugin.ElixirMod_PotionController.CactusRum_baseDamage = readInt("Cactus Rum.baseDamage");
		plugin.ElixirMod_PotionController.CactusRum_toxicityPerDamage = readInt("Cactus Rum.toxicityPerDamage");
		plugin.ElixirMod_PotionController.CactusRum_toxicityPerDrink = readInt("Cactus Rum.toxicityPerDrink");
		plugin.ElixirMod_PotionController.CactusRum_ticksPerDrink = readInt("Cactus Rum.ticksPerDrink");
		plugin.ElixirMod_PotionController.CactusRum_tickLength = readInt("Cactus Rum.tickLength");
		
		//Elixir of Haste
		plugin.ElixirMod_PotionController.Haste_baseDamage = readInt("Elixir of Haste.baseDamage");
		plugin.ElixirMod_PotionController.Haste_toxicityMinForDamage = readInt("Elixir of Haste.Minimum Toxicity for Damage");
		plugin.ElixirMod_PotionController.Haste_toxicityPerDamage = readInt("Elixir of Haste.toxicityPerDamage");
		plugin.ElixirMod_PotionController.Haste_toxicityPerDrink = readInt("Elixir of Haste.toxicityPerDrink");
		plugin.ElixirMod_PotionController.Haste_ticksPerDrink = readInt("Elixir of Haste.ticksPerDrink");
		plugin.ElixirMod_PotionController.Haste_tickLength = readInt("Elixir of Haste.tickLength");
		
    }
}
