package me.steeveeo.ElixirMod.Potions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import me.steeveeo.ElixirMod.ElixirModFileConfig;
import me.steeveeo.ElixirMod.util;
import me.steeveeo.ElixirMod.ElixirModToxicityController.PotionEntry;

public class WheatseedTea extends ElixirModPotion
{
	//Potion Info
	public boolean Enabled = true;
	public String Name = "Wheatseed Tea";
	public String PermissionNode = "elixirmod.wheatseedtea";
	
	//Modifiers
	public double toxicityDivisor = 2;
	public boolean reduceBuffTicks = true;
	public double buffTickDivisor = 1.5;
	public int healthRecovery = 4;
	public String RecipeString = "295*16";
	public int[][] Recipe;

	//Take a Drink messages
	static String[] swigMessages = new String[]{
		"You feel refreshed.",
		"The Wheatseed Tea is surprisingly tasty.",
		"The Wheatseed Tea warms and sooths.",
		"You feel better than before.",
		"The Wheatseed Tea clears your mind."
	};
	
	//This potion does nothing after the initial use
	public void giveBuff(PotionEntry entry){}
	public void takeBuff(PotionEntry entry){}
	public void tick(PotionEntry entry){}

	public void playerDrink(Player player)
	{
		PotionEntry entry = getEntry(player);
		
		//Update entry
		entry.setToxicity((int) (entry.getToxicity() / toxicityDivisor));
		
		//Reduce Buff Ticks for Balance
		if(reduceBuffTicks)
		{
			//Run through all buffTicks entries and divide them
			List<String> keys = new ArrayList<String>(entry.BuffTicks.keySet());
			for (String key: keys)
			{
				getPlugin().log.info("Wheatseed - Found " + key + ": " + entry.BuffTicks.get(key));
				entry.BuffTicks.put(key, ((int) (entry.BuffTicks.get(key) / buffTickDivisor)));
				getPlugin().log.info("Wheatseed - Out: " + entry.BuffTicks.get(key));
			}
		}
		
		//Restore some health
		int newhealth = player.getHealth() + healthRecovery;
		newhealth = Math.min(newhealth, 20);
		newhealth = Math.max(0, newhealth);
		player.setHealth(newhealth);
	}
	
	//----------------
	// ACCESSOR FUNCS
	//----------------
	
	public void setEnabled(boolean on)
	{
		Enabled = on;
	}

	public boolean getEnabled()
	{
		return Enabled;
	}

	public void setName(String newName)
	{
		Name = newName;
	}

	public String getName()
	{
		return Name;
	}

	public void setPermNode(String permNode)
	{
		PermissionNode = permNode;
	}

	public String getPermNode()
	{
		return PermissionNode;
	}

	public void setToxicityPerDrink(int newTox)
	{
		toxicityPerDrink = newTox;
	}

	public int getToxicityPerDrink()
	{
		return toxicityPerDrink;
	}

	public void setTicksPerDrink(int newTicks)
	{
		ticksPerDrink = Math.abs(newTicks);
	}

	public int getTicksPerDrink()
	{
		return ticksPerDrink;
	}

	public void setTickLength(int newLength)
	{
		tickLength = Math.abs(newLength);
	}

	public int getTickLength()
	{
		return tickLength;
	}

	public void setRecipeString(String newString)
	{
		RecipeString = newString;
	}

	public String getRecipeString()
	{
		return RecipeString;
	}

	public void setRecipe(int[][] dataTable)
	{
		Recipe = dataTable;
	}

	public int[][] getRecipe()
	{
		return Recipe;
	}

	public String getSwigMessage()
	{
		return util.randomStringFromList(swigMessages);
	}

	public String getSoberMessage()
	{
		return "";
	}

	//--End Accessor Funcs--
	
	//------------------
	// CONFIG FILE DATA
	//------------------

	public void configDefaults(ElixirModFileConfig cfg)
	{
        cfg.write("Wheatseed Tea.Enable", true);
        cfg.write("Wheatseed Tea.toxicityDivisor", 2);
        cfg.write("Wheatseed Tea.Reduce Buff Ticks", true);
        cfg.write("Wheatseed Tea.buffTickDivisor", 1.5);
        cfg.write("Wheatseed Tea.healthRecovery", 4);
        cfg.write("Wheatseed Tea.Recipe", RecipeString);
	}

	public void configLoad(ElixirModFileConfig cfg)
	{
        Enabled = cfg.readBoolean("Wheatseed Tea.Enable");
        toxicityDivisor = cfg.readDouble("Wheatseed Tea.toxicityDivisor");
        reduceBuffTicks = cfg.readBoolean("Wheatseed Tea.Reduce Buff Ticks");
        buffTickDivisor = cfg.readDouble("Wheatseed Tea.buffTickDivisor");
        healthRecovery = cfg.readInt("Wheatseed Tea.healthRecovery");
        RecipeString = cfg.readString("Wheatseed Tea.Recipe");
	}
	
	//--End Config Data--

}
