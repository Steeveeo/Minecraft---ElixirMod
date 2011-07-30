package me.steeveeo.ElixirMod.Potions;

import me.steeveeo.ElixirMod.ElixirModFileConfig;
import me.steeveeo.ElixirMod.ElixirModToxicityController.PotionEntry;
import me.steeveeo.ElixirMod.util;

import org.bukkit.entity.Player;

public class CactusRum extends ElixirModPotion
{
	//Potion Info
	public boolean Enabled = true;
	public String Name = "Cactus Rum";
	public String PermissionNode = "elixirmod.cactusrum";
	
	//Modifiers
	public int baseDamage = 6;
	public int toxicityPerDamage = 5;
	public int toxicityPerDrink = 55;
	public int ticksPerDrink = 60;
	public int tickLength = 3;
	public String RecipeString = "81*16";
	public int[][] Recipe;
	
	//Party Messages
	String[] swigMessages = new String[]{
		"You feel woozy.",
		"You take a drink.",
		"You take a swig.",
		"The Cactus Rum burns on the way down.",
		"The drink scorches your throat.",
		"Senses of both bravery and nausea wash over you.",
		"Fitting for a drink made of cactus, that stung.",
		"You feel like you just tried to eat a sword.",
		"Your tongue goes numb, your vision blurs slightly.",
		"Your pain fades, as does your reason."
	};
	
	//End of Party Messages
	String[] soberMessages = new String[]{
		"You sober up.",
		"You are now thinking clearly.",
		"The alcoholic fog clears, you have a headache.",
		"Your head hurts like the Nether, but you are sober.",
		"The liquor wears off.",
		"Your vision clears, but your stomachache does not.",
		"Your pain and reasoning come crashing back to you.",
		"Everything returns to its normal, cuboid shape.",
		"Suddenly the blur goes away, everything is clear.",
		"You regain your balance, only to buckle in pain."
	};
	
	//Player took a swig
	public void playerDrink(Player player)
	{
		PotionEntry entry = getEntry(player);
		//Party hard!
		if(entry.getToxicity() > 0)
		{
			int targetHealth = player.getHealth() - (baseDamage+(entry.getToxicity()/toxicityPerDamage));
			targetHealth = Math.max(targetHealth, 0);
			player.setHealth(targetHealth);
		}
		else
		{
			int targetHealth = player.getHealth() - baseDamage;
			targetHealth = Math.max(targetHealth, 0);
			player.setHealth(targetHealth);
		}
		
		//Add toxicity
		entry.modToxicity(toxicityPerDrink);
		
		giveBuff(entry);
	}

	//Exercise Buff
	public void tick(PotionEntry entry)
	{
		Player player = entry.getPlayer();
		//Can't go above max
		if(player.getHealth() < 20)
		{
			player.setHealth(player.getHealth()+1);
		}
	}
	
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
		cfg.write("Cactus Rum.Enable", true);
		cfg.write("Cactus Rum.baseDamage", 6);
		cfg.write("Cactus Rum.toxicityPerDamage", 6);
		cfg.write("Cactus Rum.toxicityPerDrink", 55);
		cfg.write("Cactus Rum.ticksPerDrink", 60);
		cfg.write("Cactus Rum.tickLength", 3);
		cfg.write("Cactus Rum.Recipe", RecipeString);
	}

	public void configLoad(ElixirModFileConfig cfg)
	{
		Enabled = cfg.readBoolean("Cactus Rum.Enable");
		baseDamage = cfg.readInt("Cactus Rum.baseDamage");
		toxicityPerDamage = cfg.readInt("Cactus Rum.toxicityPerDamage");
		toxicityPerDrink = cfg.readInt("Cactus Rum.toxicityPerDrink");
		ticksPerDrink = cfg.readInt("Cactus Rum.ticksPerDrink");
		tickLength = cfg.readInt("Cactus Rum.tickLength");
		RecipeString = cfg.readString("Cactus Rum.Recipe");
	}
	
	//--End Config Data--
}

