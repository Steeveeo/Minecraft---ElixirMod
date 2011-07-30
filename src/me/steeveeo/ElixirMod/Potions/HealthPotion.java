package me.steeveeo.ElixirMod.Potions;

import me.steeveeo.ElixirMod.ElixirModFileConfig;
import me.steeveeo.ElixirMod.util;
import me.steeveeo.ElixirMod.ElixirModToxicityController.PotionEntry;

import org.bukkit.entity.Player;

public class HealthPotion extends ElixirModPotion
{	
	//Potion Info
	public boolean Enabled = true;
	public String Name = "Health Potion";
	public String PermissionNode = "elixirmod.health";
	
	//Modifiers
	public int toxicityPerDrink = 20;
	public int ticksPerDrink = 15;
	public int tickLength = 1;
	public int healthRecovery = 1;
	public String RecipeString = "39*1,40*1,335*1";
	public int[][] Recipe;
	
	//Take a Drink messages
	String[] swigMessages = new String[]{
		"You slowly regain your strength.",
		"The medicine tastes horrid, but your injuries heal.",
		"That was nasty, but your bones begin to mend.",
		"Your wounds begin to close, your pain fades.",
		"The Health Potion is extremely bitter."
	};
	
	//It Wore Off messages
	String[] soberMessages = new String[]{
		"The Health Potion wears off.",
		"You are feeling a lot better than before.",
		"Your pain is gone, you are left with a sour taste.",
		"You feel restored.",
		"The potion finishes, and you feel strong again."
	};
	
	public void giveBuff(PotionEntry entry)
	{
		entry.HasBuff.put(Name, true);
		if(entry.BuffTicks.get(Name) != null)
		{
			entry.BuffTicks.put(Name, entry.BuffTicks.get(Name)+ticksPerDrink);
		}
		else
		{
			entry.BuffTicks.put(Name, ticksPerDrink);
		}
	}
	
	public void takeBuff(PotionEntry entry)
	{
		entry.HasBuff.put(Name, false);
		entry.BuffTicks.put(Name, 0);
	}

	public void playerDrink(Player player)
	{
		PotionEntry entry = getEntry(player);
		
		//Add toxicity
		entry.modToxicity(toxicityPerDrink);
		
		giveBuff(entry);
	}

	//Restore some health
	public void tick(PotionEntry entry)
	{
		Player player = entry.getPlayer();
		//Can't go above max
		if(player.getHealth() < 20)
		{
			player.setHealth(player.getHealth() + healthRecovery);
		}
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
		return util.randomStringFromList(soberMessages);
	}

	//--End Accessor Funcs--
	
	//------------------
	// CONFIG FILE DATA
	//------------------

	public void configDefaults(ElixirModFileConfig cfg)
	{
		cfg.write("Health Potion.Enable", true);
		cfg.write("Health Potion.toxicityPerDrink", 20);
		cfg.write("Health Potion.ticksPerDrink", 15);
		cfg.write("Health Potion.tickLength", 1);
		cfg.write("Health Potion.Health Per Tick", 1);
		cfg.write("Health Potion.Recipe", RecipeString);
	}

	public void configLoad(ElixirModFileConfig cfg)
	{
		Enabled = cfg.readBoolean("Health Potion.Enable");
		toxicityPerDrink = cfg.readInt("Health Potion.toxicityPerDrink");
		ticksPerDrink = cfg.readInt("Health Potion.ticksPerDrink");
		tickLength = cfg.readInt("Health Potion.tickLength");
		healthRecovery = cfg.readInt("Health Potion.Health Per Tick");
		RecipeString = cfg.readString("Health Potion.Recipe");
	}
	
	//--End Config Data--
}
