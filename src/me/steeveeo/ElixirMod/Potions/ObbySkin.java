package me.steeveeo.ElixirMod.Potions;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.plugin.PluginManager;

import me.steeveeo.ElixirMod.ElixirModFileConfig;
import me.steeveeo.ElixirMod.ElixirModToxicityController.PotionEntry;
import me.steeveeo.ElixirMod.util;

public class ObbySkin extends ElixirModPotion
{
	public ObbySkin()
	{
		//Setup event hooks
		PluginManager pm = getPlugin().pluginManager;
		EntityListener entityListener = new Obbyskin_entityListener();
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Highest, getPlugin());
	}
	
	//Potion Info
	public boolean Enabled = true;
	public String Name = "Elixir of Obsidian Skin";
	public String PermissionNode = "elixirmod.obbyskin";
	
	//Modifiers
	public int toxicityPerDrink = 50;
	public int ticksPerDrink = 30;
	public int tickLength = 10;
	public String RecipeString = "49*1,334*8";
	public int[][] Recipe;
	
	//Take a Drink messages
	static String[] swigMessages = new String[]{
		"Your skin thickens.",
		"You feel slightly tanky.",
		"Your skin feels as though it is made of stone.",
		"Your skin itself becomes an extra layer of armor.",
		"A layer of flexible rock covers your skin."
	};
	
	//It Wore Off messages
	static String[] soberMessages = new String[]{
		"You suddenly feel vulnerable.",
		"You return to your normal, squishy self.",
		"Your flesh softens.",
		"Your rock armor crumbles.",
		"The Obsidian Skin loses its effect."
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
	
	public class Obbyskin_entityListener extends EntityListener
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
				
				//Elixir of Obsidian Skin
				if(entry != null && entry.HasBuff.get(Name) != null && entry.HasBuff.get(Name))
				{
					//Halve all damages that have sources.
					//(Note, this means that direct damage through plugins
					//and such are left alone)
					if(event.getCause() != null)
					{
						int damage = event.getDamage();
						damage = (int) (damage*0.5);
						damage = Math.max(damage, 1);
						event.setDamage(damage);
					}
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
        cfg.write("Elixir of Obsidian Skin.Enable", true);
        cfg.write("Elixir of Obsidian Skin.toxicityPerDrink", 50);
        cfg.write("Elixir of Obsidian Skin.ticksPerDrink", 30);
        cfg.write("Elixir of Obsidian Skin.tickLength", 10);
        cfg.write("Elixir of Obsidian Skin.Recipe", RecipeString);
	}

	public void configLoad(ElixirModFileConfig cfg)
	{
        Enabled = cfg.readBoolean("Elixir of Obsidian Skin.Enable");
        toxicityPerDrink = cfg.readInt("Elixir of Obsidian Skin.toxicityPerDrink");
        ticksPerDrink = cfg.readInt("Elixir of Obsidian Skin.ticksPerDrink");
        tickLength = cfg.readInt("Elixir of Obsidian Skin.tickLength");
        RecipeString = cfg.readString("Elixir of Obsidian Skin.Recipe");
	}

	//--End Config Data--
}
