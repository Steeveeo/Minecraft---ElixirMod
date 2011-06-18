package me.steeveeo.ElixirMod;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class util {
	
	//Checks if the player has Num amount of typeid
	public static boolean hasItemAmount(Player player, int num, int typeid)
	{
		ItemStack[] inventory = player.getInventory().getContents();
		int hasAmount = 0;
		for(ItemStack item : inventory){
			if(item != null && item.getTypeId() == typeid){
				hasAmount += item.getAmount();
			}
			
			if(hasAmount >= num)
			{
				return true;
			}
		}
		
		//Failed Check
		return false;
	}
	
	//Removes up to Num amount of typeid from inventory of player.
	@SuppressWarnings("deprecation")
	public static void takeItemAmount(Player player, int num, int typeid)
	{
		ItemStack[] inventory = player.getInventory().getContents();
		int taken = 0;
		for(ItemStack item : inventory)
		{
			if(item != null && item.getTypeId() == typeid)
			{
				//More than enough in this stack
				if(item.getAmount() > num-taken)
				{
					item.setAmount(item.getAmount() - (num-taken));
					player.getInventory().setContents(inventory);
					player.updateInventory();
					
					break;
				}
				//Not enough in this stack, take it all and move on.
				else
				{
					taken += item.getAmount();
					item.setAmount(0);
					item.setTypeId(0);
					player.getInventory().setContents(inventory);
					player.updateInventory();
					
					//Does this finish it up?
					if(taken == num)
					{
						break;
					}
				}
			}
		}
	}

	//Grab a random string from a given array.
	public static String randomStringFromList(String[] array)
	{
		String randomString = "";
		
		//Generate random number
		Random generator = new Random();
		int index = generator.nextInt(array.length);
		
		//Get from list
		randomString = array[index];
		
		return randomString;
	}
}
