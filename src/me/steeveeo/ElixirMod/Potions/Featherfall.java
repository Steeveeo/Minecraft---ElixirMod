package me.steeveeo.ElixirMod.Potions;

import me.steeveeo.ElixirMod.ElixirModFileConfig;
import me.steeveeo.ElixirMod.util;
import me.steeveeo.ElixirMod.ElixirModToxicityController.PotionEntry;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.PluginManager;

public class Featherfall extends ElixirModPotion
{
	public Featherfall()
	{
		//Setup event hooks
		PluginManager pm = getPlugin().pluginManager;
		EntityListener entityListener = new Featherfall_entityListener();
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Highest, getPlugin());
	}
	
	//Potion Info
	public boolean Enabled = true;
	public String Name = "Elixir of Featherfall";
	public String PermissionNode = "elixirmod.featherfall";
	
	//Modifiers
	public int toxicityPerDrink = 40;
	public int ticksPerDrink = 20;
	public int tickLength = 6;
	public String RecipeString = "288*16";
	public int[][] Recipe;
	
	//Take a Drink messages
	String[] swigMessages = new String[]{
		"You feel a touch lighter.",
		"Gravity's effect appears to have deminished.",
		"Your legs feel strangely stronger.",
		"Your legs feel like springs.",
		"Gravity loses its grip upon your feet."
	};
	
	//It Wore Off messages
	String[] soberMessages = new String[]{
		"Gravity weighs down upon you once again.",
		"You feel heavy.",
		"Gravity regains its hold upon you.",
		"Your footfalls become heavy.",
		"It may no longer be safe to land."
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

	//This potion does nothing over time
	public void tick(PotionEntry entry){}
	
	
	//-------------
	// EVENT HOOKS
	//-------------
	
	public class Featherfall_entityListener extends EntityListener
	{
		//Damage Hook
		public void onEntityDamage(EntityDamageEvent event)
		{
			//No point in running if this has been stopped
			if(event.isCancelled())
			{
				return;
			}
			
			//Is this a player?
			if(event.getEntity() instanceof Player)
			{
				Player player = (Player)event.getEntity();
				PotionEntry entry = getPlugin().BuffList.get(player);
				
				if(entry != null && entry.HasBuff.get(Name) != null && entry.HasBuff.get(Name))
				{
					//Cancel fall damage
					if(event.getCause() == DamageCause.FALL)
					{
						event.setCancelled(true);
					}
				}
			}
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
        cfg.write("Elixir of Featherfall.Enable", true);
        cfg.write("Elixir of Featherfall.toxicityPerDrink", 40);
        cfg.write("Elixir of Featherfall.ticksPerDrink", 20);
        cfg.write("Elixir of Featherfall.tickLength", 6);
        cfg.write("Elixir of Featherfall.Recipe", RecipeString);
	}

	public void configLoad(ElixirModFileConfig cfg)
	{
        Enabled = cfg.readBoolean("Elixir of Featherfall.Enable");
        toxicityPerDrink = cfg.readInt("Elixir of Featherfall.toxicityPerDrink");
        ticksPerDrink = cfg.readInt("Elixir of Featherfall.ticksPerDrink");
        tickLength = cfg.readInt("Elixir of Featherfall.tickLength");
        RecipeString = cfg.readString("Elixir of Featherfall.Recipe");
	}
	
	//--End Config Data--
}
