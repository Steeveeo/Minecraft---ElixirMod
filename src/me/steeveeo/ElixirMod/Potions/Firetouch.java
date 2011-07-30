package me.steeveeo.ElixirMod.Potions;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginManager;

import me.steeveeo.ElixirMod.ElixirMod;
import me.steeveeo.ElixirMod.ElixirModFileConfig;
import me.steeveeo.ElixirMod.util;
import me.steeveeo.ElixirMod.ElixirModToxicityController.PotionEntry;

public class Firetouch extends ElixirModPotion
{
	public Firetouch()
	{
		//Setup event hooks
		PluginManager pm = getPlugin().pluginManager;
		PlayerListener playerListener = new Firetouch_playerListener();
		EntityListener entityListener = new Firetouch_entityListener();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Highest, getPlugin());
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Highest, getPlugin());
	}

	//Potion Info
	public boolean Enabled = true;
	public String Name = "Elixir of Firetouch";
	public String PermissionNode = "elixirmod.firetouch";
	
	//Modifiers
	public int toxicityPerDrink = 50;
	public int ticksPerDrink = 15;
	public int tickLength = 6;
	public int igniteLength = 5;
	public int igniteChance = 75;
	public String RecipeString = "87*16";
	public int[][] Recipe;
	public boolean unarmedOnly = false;
	public boolean igniteNetherrack = true;
	
	//Take a Drink messages
	String[] swigMessages = new String[]{
		"Your hands envelop in flame.",
		"An intense heat emanates from your palms.",
		"Fire streams from your knuckles",
		"Your arms burn with the fires of the Nether.",
		"This hand of yours is burning red!"
	};
	
	//It Wore Off messages
	String[] soberMessages = new String[]{
		"The fire in your palms calms to embers.",
		"Your hands cool down.",
		"Your hands begin to feel cold.",
		"Your fists of flame are extinguished.",
		"The fire on your fingers burns out."
	};

	public void playerDrink(Player player)
	{
		PotionEntry entry = getEntry(player);
		
		//Add toxicity
		entry.modToxicity(toxicityPerDrink);
		
		giveBuff(entry);
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

	//This potion does nothing over time
	public void tick(PotionEntry entry){}

	
	//-------------
	// EVENT HOOKS
	//-------------
	
	public class Firetouch_entityListener extends EntityListener
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
							
							//Check if we can ignite with any damage, else unarmed only
							boolean canIgnite = false;
							if(unarmedOnly)
							{
								if(((Player) damager).getItemInHand().getTypeId() == 0)
								{
									canIgnite = true;
								}
							}
							else
							{
								canIgnite = true;
							}
							
							if(canIgnite)
							{
								//If target is on fire, ignore, else ignite
								if(damagee.getFireTicks() <= 0)
								{
									//Chance ignite
									int chance = 1 + (int)(Math.random() * (100 - 1));
									if(chance <= igniteChance)
									{
										damagee.setFireTicks(igniteLength * 20);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public class Firetouch_playerListener extends PlayerListener
	{
		//Hits a netherrack block
		public void onPlayerInteract(PlayerInteractEvent event)
		{
			//Do not run if cancelled
			if(event.isCancelled())
			{
				return;
			}
			
			//Only run if netherrack ignition is enabled
			if(igniteNetherrack)
			{
				//Check to see if player clicked a netherrack block
				if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
				{
					if(event.getClickedBlock().getType() == Material.NETHERRACK)
					{
						//Ignite this block if Firetouch enabled
						Player player = event.getPlayer();
						PotionEntry buff = getPlugin().BuffList.get(player);
						
						if(buff != null)
						{
							if(buff.HasBuff.get(Name) != null && buff.HasBuff.get(Name))
							{
								//Ignite block
								BlockIgniteEvent ignite = new BlockIgniteEvent(event.getClickedBlock(), IgniteCause.FLINT_AND_STEEL, player);
								getPlugin().getServer().getPluginManager().callEvent(ignite);
								if(!ignite.isCancelled())
								{
									Block above = event.getClickedBlock().getRelative(0,1,0);
									if(above.isEmpty())
									{
										above.setType(Material.FIRE);
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
        cfg.write("Elixir of Firetouch.Enable", true);
        cfg.write("Elixir of Firetouch.toxicityPerDrink", 50);
        cfg.write("Elixir of Firetouch.ticksPerDrink", 15);
        cfg.write("Elixir of Firetouch.tickLength", 6);
        cfg.write("Elixir of Firetouch.igniteLength", 5);
        cfg.write("Elixir of Firetouch.igniteChance", 75);
        cfg.write("Elixir of Firetouch.Unarmed Only", false);
        cfg.write("Elixir of Firetouch.Ignite Netherrack", true);
        cfg.write("Elixir of Firetouch.Recipe", RecipeString);
	}

	public void configLoad(ElixirModFileConfig cfg)
	{
        Enabled = cfg.readBoolean("Elixir of Firetouch.Enable");
        toxicityPerDrink = cfg.readInt("Elixir of Firetouch.toxicityPerDrink");
        ticksPerDrink = cfg.readInt("Elixir of Firetouch.ticksPerDrink");
        tickLength = cfg.readInt("Elixir of Firetouch.tickLength");
        igniteLength = cfg.readInt("Elixir of Firetouch.igniteLength");
        igniteChance = cfg.readInt("Elixir of Firetouch.igniteChance");
        unarmedOnly = cfg.readBoolean("Elixir of Firetouch.Unarmed Only");
        igniteNetherrack = cfg.readBoolean("Elixir of Firetouch.Ignite Netherrack");
        RecipeString = cfg.readString("Elixir of Firetouch.Recipe");
	}

	//--End Config Data--
	
}
