package me.steeveeo.ElixirMod.Potions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;
import me.steeveeo.ElixirMod.ElixirMod;
import me.steeveeo.ElixirMod.ElixirModFileConfig;
import me.steeveeo.ElixirMod.util;
import me.steeveeo.ElixirMod.ElixirModToxicityController.PotionEntry;

public class Frosttouch extends ElixirModPotion
{
	//Constructor
	public Frosttouch()
	{
		//Setup event hooks
		PluginManager pm = getPlugin().pluginManager;
		PlayerListener playerListener = new Frosttouch_playerListener();
		EntityListener entityListener = new Frosttouch_entityListener();
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Highest, getPlugin());
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Highest, getPlugin());
	
		//Init Freeze control loop timer
		getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(getPlugin(),new Frosttouch_freezeController(),0,1);
	}
	
	//Potion Info
	public boolean Enabled = true;
	public String Name = "Elixir of Frosttouch";
	public String PermissionNode = "elixirmod.frosttouch";
	
	//Modifiers
	public int toxicityPerDrink = 50;
	public int ticksPerDrink = 90;
	public int tickLength = 1;
	public int stunLength = 3;
	public int stunChance = 75;
	public boolean unarmedOnly = false;
	public boolean canFreezePlayers = true;
	public String RecipeString = "80*4,326*1";
	public int[][] Recipe;

	private Map<Entity, Integer> FrozenTicks = new HashMap<Entity, Integer>();
	private Map<Entity, Location> FrozenPos = new HashMap<Entity, Location>();
	
	//Take a Drink messages
	String[] swigMessages = new String[]{
		"Your hands stiffen with cold.",
		"Ice crystals form like gloves on your hands.",
		"Jagged ice juts from your knuckles.",
		"A cloud of frost surrounds your palms.",
		"Your hands are numb from cold."
	};
	
	//It Wore Off messages
	String[] soberMessages = new String[]{
		"Your fingers tingle as they warm up.",
		"Your gloves of ice melt and drip away.",
		"The ice from your knuckles melts away.",
		"Feeling returns to your fingertips.",
		"Your hands drip as the ice melts."
	};
	
	//Hit a player, tell them what happened
	String[] hitMessages = new String[]{
		"Ice forms around your feet, you can't move!",
		"Your legs are frozen solid!",
		"A sheet of ice has trapped your feet!",
		"Cold rushes through you, you're stuck!",
		"A deathly cold surrounds you, your legs are frozen!"
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

	//Ticks down frozen entities
	public void tick(PotionEntry entry)
	{
		Set<Entity> keys = FrozenTicks.keySet();
		for(Entity ent : keys)
		{
			//If this has an entry, tick down counter
			if(FrozenTicks.containsKey(ent))
			{
				if(FrozenTicks.get(ent) > 0)
				{
					FrozenTicks.put(ent, FrozenTicks.get(ent) - 1);
				}
			}
		}
	}
	
	//-------------
	// EVENT HOOKS
	//-------------
	
	public class Frosttouch_playerListener extends PlayerListener
	{
		public void onPlayerMove(PlayerMoveEvent event)
		{
			Entity player = (Entity)event.getPlayer();
			
			//If frozen, cancel movement
			if(FrozenTicks.containsKey(player) && FrozenTicks.get(player) > 0)
			{
				event.setCancelled(true);
			}
		}
	}
	
	public class Frosttouch_entityListener extends EntityListener
	{
		//Damage Hook
		public void onEntityDamage(EntityDamageEvent event)
		{
			//No point in running if this has been stopped
			if(event.isCancelled())
			{
				return;
			}
			
			//Check if Firetouch user hit another entity
			ElixirMod plugin = getPlugin();
			if(event instanceof EntityDamageByEntityEvent)
			{
				Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
				Entity damagee = event.getEntity();
				
				//Check if damager is a player
				if(damager instanceof Player)
				{
					Player player = (Player) damager;
					PotionEntry entry = plugin.BuffList.get(player);
					
					//See if that player has Firetouch
					if(entry != null)
					{
						if(entry.HasBuff.get(Name) != null && entry.HasBuff.get(Name))
						{
							//Check if we can freeze with any damage, else unarmed (air) only
							boolean canStun = false;
							if(unarmedOnly)
							{
								if(((Player) damager).getItemInHand().getTypeId() == 0)
								{
									canStun = true;
								}
							}
							else
							{
								canStun = true;
							}
							
							if(canStun)
							{
								//If target is already frozens, ignore, else freeze
								if(FrozenTicks.get(damagee) == null || FrozenTicks.get(damagee) <= 0)
								{
									//Chance ignite
									int chance = 1 + (int)(Math.random() * (100 - 1));
									if(chance <= stunChance)
									{
										if(!(damagee instanceof Player) || canFreezePlayers)
										{
											FrozenTicks.put(damagee, stunLength);
											FrozenPos.put(damagee, damagee.getLocation());
											
											//If this is a player, report that they are stunned
											if(damagee instanceof Player)
											{
												((Player)damagee).sendMessage(ChatColor.AQUA + util.randomStringFromList(hitMessages));
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	//--End Event Hooks--
	
	//-------------------
	// FREEZE CONTROLLER
	//-------------------
	
	private class Frosttouch_freezeController implements Runnable
	{
		//Make sure all frozen mobs are stuck in place
		public void run()
		{
			Set<Entity> keys = FrozenTicks.keySet();
			for(Entity ent : keys)
			{
				//If this has an entry, freeze
				if(FrozenTicks.containsKey(ent) && FrozenTicks.get(ent) > 0)
				{
					//If moved, move back
					if(!FrozenPos.get(ent).equals(ent.getLocation()))
					{
						ent.teleport(FrozenPos.get(ent));
					}
				}
			}
		}
	}
	
	//--End Freeze Controller--
	
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
        cfg.write("Elixir of Frosttouch.Enable", true);
        cfg.write("Elixir of Frosttouch.toxicityPerDrink", 50);
        cfg.write("Elixir of Frosttouch.ticksPerDrink", 90);
        cfg.write("Elixir of Frosttouch.tickLength", 1);
        cfg.write("Elixir of Frosttouch.stunLength", 3);
        cfg.write("Elixir of Frosttouch.stunChance", 75);
        cfg.write("Elixir of Frosttouch.Unarmed Only", false);
        cfg.write("Elixir of Frosttouch.Freeze Players", true);
        cfg.write("Elixir of Frosttouch.Recipe", RecipeString);
	}

	public void configLoad(ElixirModFileConfig cfg)
	{
        Enabled = cfg.readBoolean("Elixir of Frosttouch.Enable");
        toxicityPerDrink = cfg.readInt("Elixir of Frosttouch.toxicityPerDrink");
        ticksPerDrink = cfg.readInt("Elixir of Frosttouch.ticksPerDrink");
        tickLength = cfg.readInt("Elixir of Frosttouch.tickLength");
        stunLength = cfg.readInt("Elixir of Frosttouch.stunLength");
        stunChance = cfg.readInt("Elixir of Frosttouch.stunChance");
        unarmedOnly = cfg.readBoolean("Elixir of Frosttouch.Unarmed Only");
        canFreezePlayers = cfg.readBoolean("Elixir of Frosttouch.Freeze Players");
        RecipeString = cfg.readString("Elixir of Frosttouch.Recipe");
	}
	
	//--End Config Data--
}
