package com.bukkit.happo2000.BlastControl;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlastControlBlockListener extends BlockListener 
{
	private final BlastControl plugin;
	
    public BlastControlBlockListener(BlastControl instance) 
    {
        plugin 		= instance;
	}
    
    public void onBlockPlace(BlockPlaceEvent event) 
    {
    	BlastConfiguration blastConfig = plugin.getBlastConfiguration();

    	if (blastConfig.isPluginEnabled() && (!event.isCancelled()) && event.getBlock().getType() == Material.TNT)
    	{
    		boolean bCancel = true;
    		
        	if (plugin.CheckPermission(event.getPlayer(), BlastControl.PERMISSION_TNT_ALLOWED))
        	{
        		if (event.getBlock().getY() <= blastConfig.getBlastLimit() || plugin.CheckPermission(event.getPlayer(), BlastControl.PERMISSION_PLACE_ABOVE_LIMIT))
        			bCancel = false;
        		else
        			event.getPlayer().sendMessage(BlastControl.DISPLAY_PREFIX + "You cannot place TNT above level " + ChatColor.AQUA + Integer.toString(blastConfig.getBlastLimit()) + ChatColor.WHITE + ".");
        	}
        	else
	    		event.getPlayer().sendMessage(BlastControl.DISPLAY_PREFIX + "You do not have permissions to use TNT.");
        	
    		event.setCancelled(bCancel);
    	}
    }

    public void onBlockDamage(BlockDamageEvent event) 
    {
    	BlastConfiguration 	blastConfig 	= plugin.getBlastConfiguration();
		
    	if (blastConfig.isPluginEnabled() && (!event.isCancelled()) && event.getBlock().getType() == Material.TNT)
    	{
    		Block 		targetBlock 	= event.getBlock();
    		Player 		player 			= event.getPlayer();

    		if (plugin.isOnReclaim(player.getEntityId()))
    		{
            	targetBlock.setType(Material.AIR);
        		event.setCancelled(true);

        		player.getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.TNT, 1));
    		}
    		else
    		{
	    		boolean 	bCancel 			= true;
	    		boolean		bActivateAboveLimit	= false;

	    		
	        	if (plugin.CheckPermission(player, BlastControl.PERMISSION_TNT_ALLOWED))
	        	{
	        		bActivateAboveLimit = plugin.CheckPermission(player, BlastControl.PERMISSION_ACTIVATE_ABOVE_LIMIT);
	        		
	        		if (targetBlock.getY() <= blastConfig.getBlastLimit() || bActivateAboveLimit)
	        			bCancel = false;
	        		else
	        			event.getPlayer().sendMessage(BlastControl.DISPLAY_PREFIX + "You cannot activate TNT above level " + ChatColor.AQUA + Integer.toString(blastConfig.getBlastLimit()) + ChatColor.WHITE + ".");
	        	}
	        	else
		    		event.getPlayer().sendMessage(BlastControl.DISPLAY_PREFIX + "You do not have permissions to use TNT.");
	    		
	    		if (bCancel)
	        		event.setCancelled(true);
	        	else
	        	{
	        		boolean 		bTNTLinkRestricted		= !plugin.CheckPermission(player, BlastControl.PERMISSION_LINK_ABOVE_LIMIT);
	        		ChunkBlastData	chunkBlastData			= plugin.getBlastChunkInfo();
	        		Chunk 			blastChunk 				= event.getBlock().getChunk();
		    		ChunkMetadata	metaChunk				= new ChunkMetadata(bActivateAboveLimit ? BlastLimit.NO_RESTRICTION : BlastLimit.BELOW_LIMIT_ONLY, 
		    															blastConfig.getBlastRadius(), 
		    															blastConfig.getBlastYield(), 
		    															bTNTLinkRestricted, 
		    															player.getEntityId());
		    		
		    		if (bTNTLinkRestricted)
		    			chunkBlastData.setLinkLimit(player.getEntityId(), blastConfig.getBlastLinkLimit());
	        		
		    		chunkBlastData.setChunkData(blastChunk.getX(), blastChunk.getZ(), metaChunk);
	        	}
	    	}
    	}
    }
}
