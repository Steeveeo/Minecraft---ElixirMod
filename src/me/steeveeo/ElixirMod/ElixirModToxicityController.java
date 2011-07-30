package me.steeveeo.ElixirMod;

import java.util.HashMap;
import java.util.Map;

import me.steeveeo.ElixirMod.Potions.CactusRum;
import me.steeveeo.ElixirMod.Potions.DiversAle;
import me.steeveeo.ElixirMod.Potions.DragonsDraught;
import me.steeveeo.ElixirMod.Potions.ElixirModPotion;
import me.steeveeo.ElixirMod.Potions.Featherfall;
import me.steeveeo.ElixirMod.Potions.Firetouch;
import me.steeveeo.ElixirMod.Potions.Frosttouch;
import me.steeveeo.ElixirMod.Potions.Haste;
import me.steeveeo.ElixirMod.Potions.HealthPotion;
import me.steeveeo.ElixirMod.Potions.ObbySkin;
import me.steeveeo.ElixirMod.Potions.WheatseedTea;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ElixirModToxicityController implements Runnable {
	
	//Constructor
	public static ElixirMod plugin;
	public ElixirModToxicityController(ElixirMod instance)
	{ 
		plugin = instance;
	}
	
	//Control Variables
	public static PotionEntry[] potionUsers = null;
	public int thisTick = 0;

	//----------------
	// POTION MODULES
	//----------------
	//Include all Potion objects in this!
	public ElixirModPotion[] PotionList;
	public void recompilePotionList()
	{
		PotionList = new ElixirModPotion[]{
				new CactusRum(),
				new Firetouch(),
				new Haste(),
				new ObbySkin(),
				new WheatseedTea(),
				new Featherfall(),
				new DiversAle(),
				new Frosttouch(),
				new HealthPotion(),
				new DragonsDraught()
		};
	};
	//Find a potion by its name
	public ElixirModPotion getPotionByName(String name)
	{
		name = name.toLowerCase();
		for(ElixirModPotion potion : PotionList)
		{
			String potionName = potion.getName().toLowerCase();
			if(potionName.indexOf(name) >= 0)
			{
				return potion;
			}
		}
		
		return null;
	}
	
	//Parse Recipe lists
	public void parseRecipes()
	{
		for(int index = 0; index < PotionList.length; index++)
		{
			ElixirModPotion thisPotion = PotionList[index];
			if(thisPotion != null)
			{
				thisPotion.setRecipe(parseRecipeArray(thisPotion.getRecipeString()));
			}
		}
	}
	private static int[][] parseRecipeArray(String In)
	{
		//Parse into recipe requirements by these syntax:
		// ##Type[:##Data],(...)
		//With that, a recipe for Obsidian Skin would look like:
		//"49*1,334*8" for 1 obsidian and 8 leather. Datavalues are optional.
		
		String[] ingredients = util.explode(In, ',');
		int[][] output = new int[ingredients.length][];
		
		for(int index = 0; index < ingredients.length; index++)
		{
			int[] typeAndAmount = new int[3];
			
			String[] preDataValue = util.explode(ingredients[index], '*');
			String[] ingredient = util.explode(preDataValue[0], ':');
			
			//Interpret Data
			int typeID = Integer.parseInt(ingredient[0]);
			int dataValue = 0;
			if(ingredient.length >= 2)
			{
				dataValue = Integer.parseInt(ingredient[1]);
			}
			int amount = Integer.parseInt(preDataValue[1]);
			
			//Set to array
			typeAndAmount[0] = typeID;
			typeAndAmount[1] = dataValue;
			typeAndAmount[2] = amount;
			
			output[index] = typeAndAmount;
		}
		
		return output;
	}
	
	//-----------------
	// TOXICITY DAMAGE
	//-----------------
	
	//Modifiers
	int Toxicity_minToxicityForDamage = 50;
	int Toxicity_toxicityPerDamage = 20;
	int Toxicity_tickLength = 3;
	
	//Overtoxic Messages
	static String[] Toxicity_sickMessages = new String[]{
		"You feel sick to your stomach.",
		"Something feels off.",
		"Nausea washes over you.",
		"A sense of dread fills your gut."
	};
	
	//--End Toxicity Damage--

	//----------------------
	// PLAYER TRACKER ENTRY
	//----------------------
	public static class PotionEntry
	{
		//Vars
		private
			Player player;
			int toxicity;
		public
			boolean overToxic;
			
		//Potion Buffs
		public Map<String, Boolean> HasBuff = new HashMap<String, Boolean>();
		public Map<String, Integer> BuffTicks = new HashMap<String, Integer>();
		
		//Constructor
		public PotionEntry(Player newPlayer)
		{
			player = newPlayer;
			toxicity = 0;
			overToxic = false;
			
		}
		
		//Hook to player for ease of access
		public void commit()
		{
			//Add
			int index = 0;
			for(PotionEntry user : potionUsers)
			{
				if(user == null)
				{
					potionUsers[index] = this;
					break;
				}
				else
				{
					index++;
				}
			}
			
			plugin.BuffList.put(player,this);
		}
		
		//Clear up all buffs, links, and such
		public void reset()
		{
			//Find self in reference list and nullify
			int index = 0;
			for(PotionEntry user : potionUsers)
			{
				if(user == this)
				{
					potionUsers[index] = null;
					break;
				}
				else
				{
					index++;
				}
			}
			
			//Unhook
			plugin.BuffList.put(player, null);
		}
		
		//Get Toxicity
		public int getToxicity()
		{
			return toxicity;
		}
		
		//Modify Toxicity
		public void modToxicity(int amount)
		{
			toxicity += amount;
			
			//Toxicity cannot go below 0
			if(toxicity < 0)
			{
				toxicity = 0;
			}
		}
		public void setToxicity(int amount)
		{
			if(amount >= 0)
			{
				toxicity = amount;
			}
		}
		
		//Get Player
		public Player getPlayer()
		{
			return player;
		}
	}
	
	//--End Player Tracker--
	
	//---------------------
	// ELIXIR CONTROL LOOP
	//---------------------
	public void run()
	{
		//Init potionUsers array
		//   Note: Doing this here instead of startup because
		//   the server needs to be fully loaded before
		//   server.getMaxPlayers() will return anything but 0. ¬_¬
		if(potionUsers == null)
		{
			int maxPlayers = plugin.getServer().getMaxPlayers();
			potionUsers = new PotionEntry[maxPlayers];
		}
		
		
		thisTick++;
		
		int index = 0;
		for(PotionEntry user : potionUsers)
		{
			//Validity Check
			if(user == null)
			{
				index++;
				continue;
			}
			
			//Disconnected, Missing, or Dead Player
			if(user.getPlayer() == null || user.getPlayer().getHealth() <= 0)
			{
				potionUsers[index] = null;
				
				//If player just died instead of disconnecting, remove their buff
				if(user.getPlayer() != null)
				{
					plugin.BuffList.put(user.getPlayer(), null);
				}
				
				index++;
				continue;
			}
			
			Player player = user.getPlayer();
			
			//Check for damage
			if(thisTick % Toxicity_tickLength == 0)
			{
				if(user.toxicity >= Toxicity_minToxicityForDamage)
				{
					//Warn if over-toxic
					if(!user.overToxic)
					{
						player.sendMessage(ChatColor.DARK_GRAY + "**" + util.randomStringFromList(Toxicity_sickMessages) + "**");
						user.overToxic = true;
					}
					
					//Damage
					int toxicDamage = Math.max(((user.toxicity - Toxicity_minToxicityForDamage) / Toxicity_toxicityPerDamage),1);
					int targetHealth = player.getHealth() - toxicDamage;
					targetHealth = Math.max(targetHealth, 0);
					player.setHealth(targetHealth);
				}
				else if(user.toxicity < Toxicity_minToxicityForDamage)
				{
					user.overToxic = false;
				}
			}
			
			//Tick down Toxicity
			user.modToxicity(-1);
			
			//Exercise Buffs
			for(int ii = 0; ii < PotionList.length; ii++)
			{
				ElixirModPotion thisPotion = PotionList[ii];
				
				//Are we supposed to run now?
				if(thisTick % thisPotion.getTickLength() == 0)
				{
					String buffName = thisPotion.getName();
					
					//Check if this buff is enabled for this player
					if(user.HasBuff.get(buffName) != null && user.HasBuff.get(buffName))
					{
						//Run buff
						thisPotion.tick(user);
					
						//Decrease ticks, check for buff remove
						user.BuffTicks.put(buffName, user.BuffTicks.get(buffName)-1);
						if(user.BuffTicks.get(buffName) <= 0)
						{
							thisPotion.takeBuff(user);
							
							//Notify
							String printMsg =  thisPotion.getSoberMessage();
							if(!printMsg.isEmpty())
							{
								player.sendMessage(ChatColor.GRAY + "**" + printMsg + "**");
							}
						}
					}
				}
			}
			
			index++;
		}
		
	}
	
	//--END ELIXIR CONTROL LOOP--
}
