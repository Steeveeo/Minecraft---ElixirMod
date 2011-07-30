package me.steeveeo.ElixirMod;

import me.steeveeo.ElixirMod.Potions.ElixirModPotion;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

public class ElixirModPlayerListener extends PlayerListener {

	//Plugin Constructor
	public ElixirMod plugin;
	public ElixirModPlayerListener(ElixirMod instance)
	{ 
		plugin = instance;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Action action = event.getAction();
		
		//Right click, use bowl
		if((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && player.getItemInHand().getTypeId() == 281)
		{
			//Make sure this interact was deliberately meant for potions;
			//check to see if the block clicked was not interactable.
			if(action == Action.RIGHT_CLICK_BLOCK)
			{
				Material[] blacklist = new Material[]{
					Material.BED,
					Material.BURNING_FURNACE,
					Material.CAKE_BLOCK,
					Material.CHEST,
					Material.DIODE_BLOCK_ON,
					Material.DIODE_BLOCK_OFF,
					Material.DISPENSER,
					Material.FURNACE,
					Material.JUKEBOX,
					Material.LEVER,
					Material.SIGN, //For Essentials and Sign Trading stuff
					Material.STONE_BUTTON,
					Material.TRAP_DOOR,
					Material.WALL_SIGN, //Again, for Sign-using stuff compatability
					Material.WOOD_DOOR,
					Material.WOODEN_DOOR,
					Material.WORKBENCH
				};
				Block clicked = event.getClickedBlock();
				Material blockMat = clicked.getType();
				
				for(int ii = 0; ii < blacklist.length; ii++)
				{
					//Did we find that this block is interactable?
					if(blockMat == blacklist[ii])
					{
						return;
					}
				}
			}
			
			//Loop through until we find an "Alias" item, then run that script.
			//--Current Alias Items:
			//  Cactus Rum:					Cactus (81)
			//  Elixir of Haste:			Sugar (353)
			//  Elixir of Obsidian Skin:	Obsidian (49)
			//  Wheatseed Tea:				Seeds (295)
			//	Elixir of Featherfall:		Feathers (288)
			//	Elixir of Firetouch:		Netherrack (87)
			//	Diver's Ale:				GlowstoneDust (348)
			
			ItemStack[] inventory = player.getInventory().getContents();
			for(ItemStack item : inventory)
			{
				boolean potionUsed = false;
				if(item != null)
				{
					//Loop through each active potion, see which item this belongs to
					for(int index = 0; index < plugin.ElixirMod_PotionController.PotionList.length; index++)
					{
						ElixirModPotion thisPotion = plugin.ElixirMod_PotionController.PotionList[index];
						//Is this potion active?
						if(!thisPotion.getEnabled() || !plugin.permissionHandler.has(player, thisPotion.getPermNode()))
						{
							continue;
						}
						
						//Proper Item?
						int[][] Recipe = thisPotion.getRecipe();
						if(item.getTypeId() == Recipe[0][0] && item.getDurability() == Recipe[0][1])
						{
							//Check if all requirements are met
							boolean hasIngredients = true;
							for(int ii = 0; ii < Recipe.length; ii++)
							{
								int typeid = Recipe[ii][0];
								int datavalue = Recipe[ii][1];
								int num = Recipe[ii][2];
								if(!util.hasItemAmount(player, num, typeid, datavalue))
								{
									hasIngredients = false;
									break;
								}
							}
							
							//Take all ingredients and grant buff
							if(hasIngredients)
							{
								for(int ii = 0; ii < Recipe.length; ii++)
								{
									//Item Requirement Data
									int typeid = Recipe[ii][0];
									int datavalue = Recipe[ii][1];
									int num = Recipe[ii][2];
									
									util.takeItemAmount(player, num, typeid, datavalue);
								}
								
								//Activate
								thisPotion.playerDrink(player);

								//Notify
								String printMsg =  thisPotion.getSwigMessage();
								if(!printMsg.isEmpty())
								{
									player.sendMessage(ChatColor.DARK_GRAY + "**" + printMsg + "**");
								}
								
								potionUsed = true;
								break;
							}
						}
					}
				}
				
				//Done here?
				if(potionUsed)
				{
					break;
				}
			}
		}
	}
	
}

