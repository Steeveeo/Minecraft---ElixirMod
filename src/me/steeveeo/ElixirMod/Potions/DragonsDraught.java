package me.steeveeo.ElixirMod.Potions;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.plugin.PluginManager;

import me.steeveeo.ElixirMod.ElixirModFileConfig;
import me.steeveeo.ElixirMod.util;
import me.steeveeo.ElixirMod.ElixirModToxicityController.PotionEntry;

public class DragonsDraught extends ElixirModPotion
{
	public DragonsDraught()
	{
		//Setup event hooks
		PluginManager pm = getPlugin().pluginManager;
		EntityListener entityListener = new DragonsDraught_entityListener();
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Highest, getPlugin());
	}
	
	//Potion Info
	public boolean Enabled = true;
	public String Name = "Dragon's Draught";
	public String PermissionNode = "elixirmod.dragonsdraught";
	
	//Modifiers
	public int toxicityPerDrink = 60;
	public int ticksPerDrink = 50;
	public int tickLength = 6;
	public boolean blockLavaDamage = true;
	public boolean cutFireTicks = false;
	public String RecipeString = "327*2,49*4";
	public int[][] Recipe;
	
	//Take a Drink messages
	static String[] swigMessages = new String[]{
		"You feel cold as your skin blocks out heat.",
		"A protective layer of ash coats your body.",
		"You begin to reek of smoke.",
		"You feel as if you could bathe in lava.",
		"The lava flowing through you is oddly comfortable."
	};
	
	//It Wore Off messages
	static String[] soberMessages = new String[]{
		"You shiver as your core temperature drops back.",
		"The air around you returns to normal.",
		"You crave a cold bath.",
		"You sweat as the Dragon's Draught wears off.",
		"The Dragon's Draught wears off."
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

	//Extinguish any flames on this player
	public void tick(PotionEntry entry)
	{
		Player player = entry.getPlayer();
		
		//If on fire and should not be, extinguish
		if(cutFireTicks)
		{
			if(player.getFireTicks() >= 0)
			{
				player.setFireTicks(-1);
			}
		}
	}
	
	//-------------
	// EVENT HOOKS
	//-------------
	
	public class DragonsDraught_entityListener extends EntityListener
	{
		public void onEntityDamage(EntityDamageEvent event)
		{
			//No use in checking if already cancelled
			if(event.isCancelled())
			{
				return;
			}
			
			//Check if this event's target is a player
			if(event.getEntity() instanceof Player)
			{
				Player player = (Player) event.getEntity();
				PotionEntry entry = getPlugin().BuffList.get(player);
				
				//Check if this player has this buff
				if(entry != null && entry.HasBuff.get(Name) != null && entry.HasBuff.get(Name))
				{
					//Block fire damage
					if(event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK)
					{
						event.setCancelled(true);
					}
					
					//Conditionally block Lava damage
					if(blockLavaDamage)
					{
						if(event.getCause() == DamageCause.LAVA)
						{
							event.setCancelled(true);
						}
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
		cfg.write("Dragons Draught.Enable", true);
		cfg.write("Dragons Draught.toxicityPerDrink", 60);
		cfg.write("Dragons Draught.ticksPerDrink", 50);
		cfg.write("Dragons Draught.tickLength", 6);
		cfg.write("Dragons Draught.Block Lava Damage", true);
		cfg.write("Dragons Draught.Extinguish Fire", false);
		cfg.write("Dragons Draught.Recipe", RecipeString);
	}

	public void configLoad(ElixirModFileConfig cfg)
	{
		Enabled = cfg.readBoolean("Dragons Draught.Enable");
		toxicityPerDrink = cfg.readInt("Dragons Draught.toxicityPerDrink");
		ticksPerDrink = cfg.readInt("Dragons Draught.ticksPerDrink");
		tickLength = cfg.readInt("Dragons Draught.tickLength");
		blockLavaDamage = cfg.readBoolean("Dragons Draught.Block Lava Damage");
		cutFireTicks = cfg.readBoolean("Dragons Draught.Extinguish Fire");
		RecipeString = cfg.readString("Dragons Draught.Recipe");
	}
	
	//--End Config Data--
}
