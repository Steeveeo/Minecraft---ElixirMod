package me.steeveeo.ElixirMod;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class ElixirModPlayerListener extends PlayerListener {

	//Plugin Constructor
	public ElixirMod plugin; public ElixirModPlayerListener(ElixirMod instance)
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
			//Cactus Rum Check
			if(util.hasItemAmount(player, 16, 81) && plugin.permissionHandler.has(player, "elixirmod.cactusrum"))
			{
				util.takeItemAmount(player, 16, 81);
				plugin.ElixirMod_PotionController.CactusRum_playerDrink(player);
			}
			
			//Elixir of Haste Check
			else if(util.hasItemAmount(player, 16, 353) && plugin.permissionHandler.has(player, "elixirmod.haste"))
			{
				util.takeItemAmount(player, 16, 353);
				plugin.ElixirMod_PotionController.Haste_playerDrink(player);
			}
		}
	}
	
	//Haste Stuffs
	private static double defaultSpeed = 1.8;
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		
		//Can this person speed?
		if(plugin.hasteEnabled.get(player))
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
	
	//New Joiner
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		
		//Default Values
		plugin.hasteEnabled.put(player, false);
	}
	
}

