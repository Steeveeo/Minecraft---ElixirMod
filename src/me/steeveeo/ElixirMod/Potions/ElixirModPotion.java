package me.steeveeo.ElixirMod.Potions;

import me.steeveeo.ElixirMod.ElixirMod;
import me.steeveeo.ElixirMod.ElixirModFileConfig;
import me.steeveeo.ElixirMod.ElixirModToxicityController;
import me.steeveeo.ElixirMod.ElixirModToxicityController.PotionEntry;

import org.bukkit.entity.Player;

public abstract class ElixirModPotion
{
	//-----------------------
	// POTION TEMPLATE CLASS
	//-----------------------
	//Default Variables
	//If I could require these, I would, but for now just overwrite them
	public boolean Enabled = true;
	public String Name = "NULL";
	public String PermissionNode = "NULL";
	public int ticksPerDrink = 10;
	public int toxicityPerDrink = 10;
	public int tickLength = 1;
	public String RecipeString = "1*1";
	public int[][] Recipe;
	
	//This function either gets this player's potion status entry
	// or creates a new one if not found.
	public PotionEntry getEntry(Player player)
	{
		//Check if this player already exists in the table
		PotionEntry entry = getPlugin().BuffList.get(player);
		
		//If not, add the new entry to the list
		if(entry == null)
		{
			entry = new PotionEntry(player);
			entry.commit();
		}
		
		return entry;
	}
	
	//Utility function to get the plugin manager.
	//Planned use is for writing action hooks; might
	//have use elsewhere.
	public ElixirMod getPlugin()
	{
		return ElixirModToxicityController.plugin;
	}
	
	//Potion Activity functions
	public abstract void giveBuff(PotionEntry entry);	//Self explanitory
	public abstract void takeBuff(PotionEntry entry);	//Self explanitory
	public abstract void playerDrink(Player player);	//Called on-swig
	public abstract void tick(PotionEntry entry);		//Exercise this potion's buff
	
	//Return Messages (abstract to let the subclasses overwrite this, just copy
	//the commented code below it for recommended use.
	public abstract String getSwigMessage();
	/*
	public String getSwigMessage()
	{
		return util.randomStringFromList(swigMessages);
	}
	*/
	public abstract String getSoberMessage();
	/*
	public String getSoberMessage()
	{
		return util.randomStringFromList(soberMessages);
	}
	*/
	
	//File Config stuff
	public abstract void configDefaults(ElixirModFileConfig cfg);
	public abstract void configLoad(ElixirModFileConfig cfg);
	
	//Variable Accessors
	//--Enabled
	public abstract void setEnabled(boolean on);
	public abstract boolean getEnabled();
	//--Potion Name
	public abstract void setName(String newName);
	public abstract String getName();
	//--Permission Node
	public abstract void setPermNode(String permNode);
	public abstract String getPermNode();
	//--Toxicity Per Drink
	public abstract void setToxicityPerDrink(int newTox);
	public abstract int getToxicityPerDrink();
	//--Ticks Per Drink
	public abstract void setTicksPerDrink(int newTicks);
	public abstract int getTicksPerDrink();
	//--Tick Time
	public abstract void setTickLength(int newLength);
	public abstract int getTickLength();
	//--Recipe String
	public abstract void setRecipeString(String newString);
	public abstract String getRecipeString();
	//--Recipe Data Table
	public abstract void setRecipe(int[][] dataTable);
	public abstract int[][] getRecipe();
}
