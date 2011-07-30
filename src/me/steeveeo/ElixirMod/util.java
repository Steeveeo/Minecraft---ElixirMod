package me.steeveeo.ElixirMod;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class util {
	
	//Checks if the player has Num amount of typeid
	public static boolean hasItemAmount(Player player, int num, int typeid, int datavalue)
	{
		ItemStack[] inventory = player.getInventory().getContents();
		int hasAmount = 0;
		for(ItemStack item : inventory){
			if(item != null && item.getTypeId() == typeid)
			{
				if(datavalue == -1 || item.getDurability() == datavalue)
				{
					hasAmount += item.getAmount();
				}
			}
			
			if(hasAmount >= num)
			{
				return true;
			}
		}
		
		//Failed Check
		return false;
	}
	public static boolean hasItemAmount(Player player, int num, int typeid)
	{
		return hasItemAmount(player,num,typeid,-1);
	}
	
	//Removes up to Num amount of typeid from inventory of player.
	@SuppressWarnings("deprecation")
	public static void takeItemAmount(Player player, int num, int typeid, int datavalue)
	{
		ItemStack[] inventory = player.getInventory().getContents();
		int taken = 0;
		for(ItemStack item : inventory)
		{
			if(item != null && item.getTypeId() == typeid)
			{
				//Proper datavlue?
				if(datavalue == -1 || item.getDurability() == datavalue)
				{
					//More than enough in this stack
					if(item.getAmount() > num-taken)
					{
						item.setAmount(item.getAmount() - (num - taken));
						player.getInventory().setContents(inventory);
						player.updateInventory();
						
						break;
					}
					//Not enough in this stack, take it all and move on.
					else
					{
						taken += item.getAmount();
						
						//Is the target item a bucket with stuff in it?
						if(typeid == Material.MILK_BUCKET.getId() ||
						typeid == Material.WATER_BUCKET.getId() ||
						typeid == Material.LAVA_BUCKET.getId())
						{
							//Take the stuff out of the bucket and give it back
							item.setTypeId(Material.BUCKET.getId());
						}
						else
						{
							item.setAmount(0);
							item.setTypeId(0);
						}
						
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
	}
	public static void takeItemAmount(Player player, int num, int typeid)
	{
		takeItemAmount(player,num,typeid,-1);
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
	
	//Break a string into an array by a delimiter
	public static String[] explode(String in, char delim)
	{
		LinkedList<String> out = new LinkedList<String>();
		char[] input = in.toCharArray();
		
		char inChar;
		String entry = "";
		for(int index = 0; index < in.length(); index++)
		{
			inChar = input[index];
			
			//EOF
			if(inChar == '\0')
			{
				break;
			}
			
			//End Line
			if(inChar == delim)
			{
				out.add(entry);
				entry = "";
			}
			//Else add to current entry
			else
			{
				entry = entry + inChar;
			}
		}
		out.add(entry);
		
		//Empty to array
		String[] output = new String[out.size()];
		for(int index = 0; index < out.size(); index++)
		{
			output[index] = out.get(index);
		}
		
		return output;
	}
	
	//Get player(s) by name stub (accepts wildcard '*' for everyone)
	public static List<Player> findPlayerByName(Server server, String name)
	{
		List<Player> targets = new ArrayList<Player>();
		if(name.equals("*"))
		{
			Player[] onlinePlayers = server.getOnlinePlayers();
			for(int ii = 0; ii < onlinePlayers.length; ii++)
			{
				targets.add(onlinePlayers[ii]);
			}
		}
		else
		{
			targets = server.matchPlayer(name);
		}
		
		return targets;
	}
}
