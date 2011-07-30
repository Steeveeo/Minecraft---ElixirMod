package me.steeveeo.ElixirMod.Potions;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import me.steeveeo.ElixirMod.ElixirModFileConfig;
import me.steeveeo.ElixirMod.util;
import me.steeveeo.ElixirMod.ElixirModToxicityController.PotionEntry;

public class Haste extends ElixirModPotion
{
	public Haste()
	{
		//Setup event hooks
		PluginManager pm = getPlugin().pluginManager;
		PlayerListener playerListener = new Haste_playerListener();
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Highest, getPlugin());
	}
	
	//Potion Info
	public boolean Enabled = true;
	public String Name = "Elixir of Haste";
	public String PermissionNode = "elixirmod.haste";
	
	//Modifiers
	public int toxicityPerDrink = 20;
	public int ticksPerDrink = 10;
	public int tickLength = 6;
	public String RecipeString = "353*16";
	public int[][] Recipe;
	
	//Take a Drink messages
	String[] swigMessages = new String[]{
		"You feel light on your feet."
	};
	
	//It Wore Off messages
	String[] soberMessages = new String[]{
		"Your legs begin to feel sluggish."
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
	
	public class Haste_playerListener extends PlayerListener
	{
		private double defaultSpeed = 1.8;
		public void onPlayerMove(PlayerMoveEvent event)
		{
			Player player = event.getPlayer();
			PotionEntry entry = getPlugin().BuffList.get(player);
			
			//TODO BukkitContrib version of this to run smoother.
			
			//Run this only if Haste is actually enabled
			if(Enabled)
			{
				//Can this person speed?
				if(player != null && entry != null && entry.HasBuff.get(Name) != null && entry.HasBuff.get(Name))
				{
					//Unless crouched, speed the hell up
					if(!player.isSneaking())
					{
						//Make sure to only speed up if on solid ground
						int material = player.getWorld().getBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ()).getTypeId();
						if(material != 0 && material != 8 && material != 9 && material != 10 && material != 11 && material != 30 && material != 65 && material != 88)
						{
							//Vector dir = player.getLocation().getDirection().multiply(defaultSpeed).setY(0.1);
							Vector dir = player.getLocation().getDirection().setY(0); //player.getVelocity();
							Vector veloc = player.getVelocity();
							dir.setX(Math.max(Math.min((dir.getX() * defaultSpeed)+(veloc.getX()*0.3), 2), -2));
							dir.setZ(Math.max(Math.min((dir.getZ() * defaultSpeed)+(veloc.getZ()*0.3), 2), -2));
							dir.setY(veloc.getY());
							player.setVelocity(dir);
						}
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
        cfg.write("Elixir of Haste.Enable", true);
        cfg.write("Elixir of Haste.toxicityPerDrink", 20);
        cfg.write("Elixir of Haste.ticksPerDrink", 10);
        cfg.write("Elixir of Haste.tickLength", 6);
        cfg.write("Elixir of Haste.Recipe", RecipeString);
	}

	public void configLoad(ElixirModFileConfig cfg)
	{
        Enabled = cfg.readBoolean("Elixir of Haste.Enable");
        toxicityPerDrink = cfg.readInt("Elixir of Haste.toxicityPerDrink");
        ticksPerDrink = cfg.readInt("Elixir of Haste.ticksPerDrink");
        tickLength = cfg.readInt("Elixir of Haste.tickLength");
        RecipeString = cfg.readString("Elixir of Haste.Recipe");
	}
	
	//--End Config Data--

}
