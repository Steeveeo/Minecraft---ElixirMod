package me.steeveeo.ElixirMod.Potions;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;

import me.steeveeo.ElixirMod.ElixirModFileConfig;
import me.steeveeo.ElixirMod.util;
import me.steeveeo.ElixirMod.ElixirModToxicityController.PotionEntry;

public class DiversAle extends ElixirModPotion
{
	//Constructor
	public DiversAle()
	{
		//Setup event hooks
		PluginManager pm = getPlugin().pluginManager;
		PlayerListener playerListener = new DiversAle_playerListener();
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Highest, getPlugin());
	}
	
	//Potion Info
	public boolean Enabled = true;
	public String Name = "Diver's Ale";
	public String PermissionNode = "elixirmod.diversale";
	
	//Modifiers
	public int toxicityPerDrink = 30;
	public int ticksPerDrink = 50;
	public int tickLength = 6;
	public double maxAirMult = 5;
	public int defaultAirTicks = 300;
	public String RecipeString = "348*8,38*1";
	public int[][] Recipe;
	
	//Take a Drink messages
	static String[] swigMessages = new String[]{
		"Your lungs itch as the Diver's Ale takes effect.",
		"You feel as if you could hold your breath for hours.",
		"The Diver's Ale gives you Olympic strength lungs.",
		"You feel like swimming for miles.",
		"You feel the need to swim to great depths."
	};
	
	//It Wore Off messages
	static String[] soberMessages = new String[]{
		"Your lungs contract as the Diver's Ale wears off.",
		"The Diver's Ale wears off.",
		"You feel a need to return to shore.",
		"Your love of dry land returns.",
		"You feel light headed from the lack of extra air."
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
		
		//Set max air
		double maxAir = defaultAirTicks * maxAirMult;
		entry.getPlayer().setMaximumAir((int) maxAir);
	}
	
	public void takeBuff(PotionEntry entry)
	{
		entry.HasBuff.put(Name, false);
		entry.BuffTicks.put(Name, 0);
		
		//Return max air to default
		entry.getPlayer().setMaximumAir(defaultAirTicks);
	}

	public void playerDrink(Player player)
	{
		PotionEntry entry = getEntry(player);
		
		//Add toxicity
		entry.modToxicity(toxicityPerDrink);
		
		giveBuff(entry);
	}

	//This potion does nothing over time
	public void tick(PotionEntry entry){}
	
	//-------------
	// EVENT HOOKS
	//-------------
	
	public class DiversAle_playerListener extends PlayerListener
	{
		public void onPlayerMove(PlayerMoveEvent event)
		{
			Player player = event.getPlayer();
			PotionEntry entry = getPlugin().BuffList.get(player);
			
			//If this player has the buff, extend their max breath,
			//else default.
			//Doing this constantly because sometimes it doesn't take
			//in the initial use for whatever reason.
			if(entry != null)
			{
				if(entry.HasBuff.get(Name) != null && entry.HasBuff.get(Name))
				{
					//Set max air
					double maxAir = defaultAirTicks * maxAirMult;
					entry.getPlayer().setMaximumAir((int) maxAir);
				}
				else
				{
					//Set max air
					entry.getPlayer().setMaximumAir(defaultAirTicks);
				}
			}
		}
	}
	
	//--End Event Hooks--
	
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
        cfg.write("Divers Ale.Enable", true);
        cfg.write("Divers Ale.toxicityPerDrink", 30);
        cfg.write("Divers Ale.ticksPerDrink", 50);
        cfg.write("Divers Ale.tickLength", 6);
        cfg.write("Divers Ale.defaultAirTicks", 300);
        cfg.write("Divers Ale.maxAirMult", 5);
        cfg.write("Divers Ale.Recipe", RecipeString);
	}

	public void configLoad(ElixirModFileConfig cfg)
	{
        Enabled = cfg.readBoolean("Divers Ale.Enable");
        toxicityPerDrink = cfg.readInt("Divers Ale.toxicityPerDrink");
        ticksPerDrink = cfg.readInt("Divers Ale.ticksPerDrink");
        tickLength = cfg.readInt("Divers Ale.tickLength");
        defaultAirTicks = cfg.readInt("Divers Ale.defaultAirTicks");
        maxAirMult = cfg.readDouble("Divers Ale.maxAirMult");
        RecipeString = cfg.readString("Divers Ale.Recipe");
	}
	
	//--End Config Data--
	
}
