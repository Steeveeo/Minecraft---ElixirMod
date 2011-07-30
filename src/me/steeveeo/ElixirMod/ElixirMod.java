package me.steeveeo.ElixirMod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import me.steeveeo.ElixirMod.ElixirModToxicityController.PotionEntry;
import me.steeveeo.ElixirMod.Potions.ElixirModPotion;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class ElixirMod extends JavaPlugin {
	
	//Establish Listeners
	public PluginManager pluginManager;
	private final ElixirModPlayerListener playerListener = new ElixirModPlayerListener(this);

	//Start Logger
	public Logger log = Logger.getLogger("Minecraft");

	//Initialize Drunkenness Timer
	//final Timer ElixirMod_Timer = new Timer(true);
	final ElixirModToxicityController ElixirMod_PotionController = new ElixirModToxicityController(this);
	
	//Start Config File Stuffs
    ElixirModFileConfig config = new ElixirModFileConfig(this);
    
    //Permissions Stuff
    public PermissionHandler permissionHandler;
    private void setupPermissions()
    {
    	Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

		if (this.permissionHandler == null) {
			if (permissionsPlugin != null) {
			    this.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
			} else {
			    log.info("[ElixirMod] Permissions system not detected, defaulting to OP.");
			}
		}
    }

    //Potion Maps
	public Map<Player, PotionEntry> BuffList = new HashMap<Player, PotionEntry>();
	
	//Startup and Shutdown
	public void onEnable()
	{
		pluginManager = this.getServer().getPluginManager();
		
		//Start Timer
		//ElixirMod_Timer.schedule(ElixirMod_PotionController, (long) 5000, (long) 1000);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, ElixirMod_PotionController, 0, 20);
		
		//Load in potions
		ElixirMod_PotionController.recompilePotionList();
		
		//Init Config
		config.configCheck();
		
		//Init Permissions
		setupPermissions();
		
		//Dependencies loaded, parse potion recipes
		ElixirMod_PotionController.parseRecipes();
		
		//Register Listeners
		pluginManager.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
		
		//Startup was good!
		log.info("[ElixirMod] - Version 1.00 Started.");
	} 
	
	public void onDisable()
	{ 
		log.info("[ElixirMod] - Version 1.00 Stopped.");
	}
	
	//-------------------
	// COMMAND PROCESSOR
	//-------------------
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player player = null;
		if(sender instanceof Player)
		{
			player = (Player)sender;
		}
		
		if(label.equalsIgnoreCase("elixir"))
		{
			if(args.length > 0)
			{
				//-----------------
				// COMMAND: GETTOX
				//	/elixir gettox[ <player>]
				//-----------------
				if(args[0].equalsIgnoreCase("getTox"))
				{
					//If we have more than just "getTox", search instead for that player
					if(args.length >= 2)
					{
						//View Others
						if(player == null || permissionHandler.has(player, "elixirmod.command.gettox.others"))
						{
							//Get all possible matches to the input (all on wildcard '*')
							List<Player> targets = util.findPlayerByName(getServer(), args[1]);
							
							//Not found?
							if(targets.size() < 1)
							{
								cmdNotify(player, true, "Player(s) not found.");
								return false;
							}
							//Found someone or multiples, display
							else
							{
								cmdNotify(player, false, "Toxicity for the following player(s):");
								for(Player found : targets)
								{
									PotionEntry entry = BuffList.get(found);
									
									//Package up data and send as line
									String send = "   " + found.getDisplayName() + ": ";
									if(entry != null)
									{
										send += entry.getToxicity();
									}
									else
									{
										send += 0;
									}
									//--Append max
									send += "/" + ElixirMod_PotionController.Toxicity_minToxicityForDamage;
									cmdNotify(player, false, send);
								}
								return true;
							}
						}
						else
						{
							return permsError(player);
						}
					}
					else
					{
						//View Self
						if(player == null || permissionHandler.has(player, "elixirmod.command.gettox"))
						{
							if(player == null)
							{
								return serverError();
							}
							
							PotionEntry entry = BuffList.get(player);
							
							//Package up data and send as line
							String send = "Your toxicity: ";
							if(entry != null)
							{
								send += entry.getToxicity();
							}
							else
							{
								send += 0;
							}
							//--Append max
							send += "/" + ElixirMod_PotionController.Toxicity_minToxicityForDamage;
							cmdNotify(player, false, send);
							return true;
						}
						else
						{
							return permsError(player);
						}
					}
				}
				//--End GetTox--
				
				//-----------------
				// COMMAND: SETTOX
				//	/elixir settox[ <player>] <toxicity>
				//-----------------
				if(args[0].equalsIgnoreCase("setTox"))
				{
					//If we have more than just "setTox", search instead for that player
					if(args.length >= 3)
					{
						//Set other player(s)
						if(player == null || permissionHandler.has(player, "elixirmod.command.settox.others"))
						{
							//Get all possible matches to the input (all on wildcard '*')
							List<Player> targets = util.findPlayerByName(getServer(), args[1]);
							
							//Not found?
							if(targets.size() < 1)
							{
								//Not found error
								cmdNotify(player, true, "Player(s) not found.");
								return false;
							}
							else
							{
								int setTox = 0;
								try
								{
									setTox = Integer.parseInt(args[2]);
									setTox = Math.abs(setTox);
								}
								catch(NumberFormatException e)
								{
									cmdNotify(player, true, "Error: Toxicity must be a number!");
									return false;
								}
								
								for(Player found : targets)
								{
									PotionEntry entry = BuffList.get(found);
									
									//If we found an entry, set
									if(entry != null)
									{
										entry.setToxicity(setTox);
									}
									//Else make a new entry with this toxicity
									else
									{
										entry = new PotionEntry(found);
										entry.setToxicity(setTox);
										entry.commit();
									}
									
									//Notify
									String sendName = "CONSOLE";
									if(player != null)
									{
										sendName = player.getDisplayName();
									}
									found.sendMessage(ChatColor.WHITE + sendName + " set your toxicity to " + setTox);
									cmdNotify(player, false, "Setting " + found.getDisplayName() + "'s Toxicity to " + setTox);
								}
								
								return true;
							}
						}
						else
						{
							return permsError(player);
						}
					}
					//Set Self
					else
					{
						if(player == null || permissionHandler.has(player, "elixirmod.command.settox"))
						{
							if(player == null)
							{
								return serverError();
							}
							
							int setTox = 0;
							try
							{
								setTox = Integer.parseInt(args[1]);
								setTox = Math.abs(setTox);
							}
							catch(NumberFormatException e)
							{
								cmdNotify(player, true, "Error: Toxicity must be a number!");
								return false;
							}
							
							cmdNotify(player, false, "Setting your Toxicity to " + setTox);
							
							//Find this player's PotionEntry
							PotionEntry entry = BuffList.get(player);
							
							//If we found an entry, set
							if(entry != null)
							{
								entry.setToxicity(setTox);
							}
							//Else make a new entry with this toxicity
							else
							{
								entry = new PotionEntry(player);
								entry.setToxicity(setTox);
								entry.commit();
							}
							
							return true;
						}
						else
						{
							return permsError(player);
						}
					}
				}
				//--End SetTox--
				
				//----------------
				// COMMAND: RESET
				//	/elixir reset[ <player>]
				//----------------
				if(args[0].equalsIgnoreCase("reset"))
				{
					//Reset others
					if(args.length >= 2)
					{
						if(player == null || permissionHandler.has(player, "elixirmod.command.reset.others"))
						{
							//Get all possible matches to the input (all on wildcard '*')
							List<Player> targets = util.findPlayerByName(getServer(), args[1]);
							
							//Not found?
							if(targets.size() < 1)
							{
								//Not found error
								cmdNotify(player, true, "Player(s) not found.");
								return false;
							}
							else
							{
								for(Player found : targets)
								{
									PotionEntry entry = BuffList.get(found);
									
									//If found, reset; else just notify to make the user feel better about himself.
									if(entry != null)
									{
										entry.reset();
									}
									
									//Notify
									String sendName = "CONSOLE";
									if(player != null)
									{
										sendName = player.getDisplayName();
									}
									cmdNotify(player, false, "You reset " + found.getDisplayName() + "'s status.");
									found.sendMessage(ChatColor.WHITE + sendName + " reset your status.");
								}
								return true;
							}
						}
						else
						{
							return permsError(player);
						}
					}
					//Reset Self
					else
					{
						if(player == null || permissionHandler.has(player, "elixirmod.command.reset"))
						{
							if(player == null)
							{
								return serverError();
							}
							
							PotionEntry entry = BuffList.get(player);
							
							//If found, reset; else just notify to make the user feel better about himself.
							if(entry != null)
							{
								entry.reset();
							}
							
							cmdNotify(player, false, "You reset your status.");
							
							return true;
						}
						else
						{
							return permsError(player);
						}
					}
				}
				//--End Reset--
				
				//-------------------
				// COMMAND: GIVEBUFF
				//	/elixir givebuff <player> <name>
				//-------------------
				if(args[0].equalsIgnoreCase("givebuff"))
				{
					if(player == null || permissionHandler.has(player, "elixirmod.command.givebuff"))
					{
						if(args.length >= 3)
						{
							//Compile full potion name
							String buffName = "";
							for(int ii = 2; ii < args.length; ii++)
							{
								buffName += args[ii];
								buffName += " ";
							}
							buffName = buffName.trim();
							
							//Get all possible player matches to the input (all on wildcard '*')
							List<Player> targets = util.findPlayerByName(getServer(), args[1]);
							
							//Not found?
							if(targets.size() < 1)
							{
								//Not found error
								cmdNotify(player, true, "Player(s) not found.");
								return false;
							}
							else
							{
								//Find this buff
								ElixirModPotion potion = ElixirMod_PotionController.getPotionByName(buffName);
								if(potion != null)
								{
									for(Player found : targets)
									{
										//Find this player's entry
										PotionEntry entry = BuffList.get(found);

										//If we found an entry, set
										if(entry != null)
										{
											potion.giveBuff(entry);
										}
										//Else make a new entry with this toxicity
										else
										{
											entry = new PotionEntry(found);
											potion.giveBuff(entry);
											entry.commit();
										}

										String sendName = "CONSOLE";
										if(player != null)
										{
											sendName = player.getDisplayName();
										}
										cmdNotify(player, false, "Giving " + found.getDisplayName() + " \"" + potion.getName() + "\".");
										found.sendMessage(ChatColor.WHITE + sendName + " gave you \"" + potion.getName() + "\".");
									}

									return true;
								}
								else
								{
									cmdNotify(player, true, "A potion by that name was not found.");
									return false;
								}
							}
						}
						else
						{
							//Not enough args
							cmdNotify(player, true, "You must specify a player and a potion name.");
							return false;
						}
					}
					else
					{
						return permsError(player);
					}
				}
				//--End GiveBuff--
				
				//------------------
				// COMMAND TAKEBUFF
				//  /elixir takebuff <player> <potionName>
				//------------------
				if(args[0].equalsIgnoreCase("takebuff"))
				{
					if(player == null || permissionHandler.has(player, "elixirmod.command.takebuff"))
					{
						if(args.length >= 3)
						{
							//Compile full potion name
							String buffName = "";
							for(int ii = 2; ii < args.length; ii++)
							{
								buffName += args[ii];
								buffName += " ";
							}
							buffName = buffName.trim();
							
							//Get all possible player matches to the input (all on wildcard '*')
							List<Player> targets = util.findPlayerByName(getServer(), args[1]);
							
							//Not found?
							if(targets.size() < 1)
							{
								//Not found error
								cmdNotify(player, true, "Player(s) not found.");
								return false;
							}
							else
							{
								//Find this buff
								ElixirModPotion potion = ElixirMod_PotionController.getPotionByName(buffName);
								if(potion != null)
								{
									for(Player found : targets)
									{
										//Find this player's entry
										PotionEntry entry = BuffList.get(found);

										//If we found an entry, set
										if(entry != null)
										{
											String sendName = "CONSOLE";
											if(player != null)
											{
												sendName = player.getDisplayName();
											}
											
											//Take the buff, then notify all parties involved
											if(entry.HasBuff.get(potion.getName()) != null && entry.HasBuff.get(potion.getName()))
											{
												potion.takeBuff(entry);
												cmdNotify(player, false, "Taking away " + found.getDisplayName() + "'s \"" + potion.getName() + "\".");
												found.sendMessage(ChatColor.WHITE + sendName + " took away your \"" + potion.getName() + "\".");
											}
										}
									}

									return true;
								}
								else
								{
									cmdNotify(player, true, "A potion by that name was not found.");
									return false;
								}
							}
						}
						else
						{
							//Not enough args
							cmdNotify(player, true, "You must specify a player and a potion name.");
							return false;
						}
					}
					else
					{
						return permsError(player);
					}
				//--End TakeBuff--
				}
			}
			
			//If nothing was detected, drop the usage on them
			showUsage(player);
			return false;
		}
		return false;
	}
	//Usage Text
	private void showUsage(Player player)
	{
		cmdNotify(player, false, "Usage: /elixir <command>");
		cmdNotify(player, false, "Commands are as follows:");
		cmdNotify(player, false, " getTox[ <player>]       (Shows toxicity of Player(s))");
		cmdNotify(player, false, " setTox[ <player>] <tox> (Sets toxicity of Player(s))");
		cmdNotify(player, false, " reset[ <player>]        (Resets state of Player(s))");
		cmdNotify(player, false, " giveBuff <player> <name> (Gives buff by name)");
		cmdNotify(player, false, " takeBuff <player> <name> (Takes away buff by name)");
	}
	//Permissions error
	private boolean permsError(Player player)
	{
		cmdNotify(player, true, "You do not have permission to use this command.");
		return false;
	}
	//Not a Player error
	private boolean serverError()
	{
		cmdNotify(null, true, "This command cannot be run on the server! Choose a player instead.");
		return false;
	}
	//Send a message to the proper player
	private void cmdNotify(Player player, boolean error, String text)
	{
		if(player != null)
		{
			ChatColor color;
			if(error)
			{
				color = ChatColor.RED;
			}
			else
			{
				color = ChatColor.WHITE;
			}
			
			player.sendMessage(color + text);
		}
		else
		{
			if(error)
			{
				log.warning("[ElixirMod] - " + text);
			}
			else
			{
				log.info("[ElixirMod] - " + text);
			}
		}
	}
	
	//--End Command Processor--
	
}