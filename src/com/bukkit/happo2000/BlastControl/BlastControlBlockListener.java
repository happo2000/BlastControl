package com.bukkit.happo2000.BlastControl;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.inventory.ItemStack;

public class BlastControlBlockListener extends BlockListener 
{
	private final BlastControl plugin;
	
    public BlastControlBlockListener(BlastControl instance) 
    {
        plugin 		= instance;
	}
    
    public void onBlockRightClick(BlockRightClickEvent event) 
    {
    	Block targetBlock = event.getBlock();
    	
    	if (plugin.getBlastConfiguration().isPluginEnabled() && targetBlock.getType() == Material.TNT && plugin.CheckPermission(event.getPlayer(), BlastControl.PERMISSION_RECLAIM))
    	{
    		ItemStack heldItem = event.getPlayer().getItemInHand();
    		
    		switch (heldItem.getType())
    		{
    		case AIR:
            	targetBlock.setType(Material.AIR);
	    		event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.TNT, 1));
    			break;
    		}
    	}    	
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
    	BlastConfiguration blastConfig = plugin.getBlastConfiguration();
    	
    	if (blastConfig.isPluginEnabled() && (!event.isCancelled()) && event.getBlock().getType() == Material.TNT)
    	{
    		Block 		targetBlock 		= event.getBlock();

    		if (plugin.isOnReclaim(event.getPlayer().getEntityId()))
    		{
            	targetBlock.setType(Material.AIR);
        		event.setCancelled(true);

	    		event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.TNT, 1));
    		}
    		else
    		{
	    		boolean 	bCancel 			= true;
	    		boolean		bActivateAboveLimit	= false;
	    		
	        	if (plugin.CheckPermission(event.getPlayer(), BlastControl.PERMISSION_TNT_ALLOWED))
	        	{
	        		bActivateAboveLimit = plugin.CheckPermission(event.getPlayer(), BlastControl.PERMISSION_ACTIVATE_ABOVE_LIMIT);
	        		
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
	        		Chunk blastChunk = event.getPlayer().getWorld().getChunkAt(targetBlock);
	        		
	        		plugin.setChunkStatus(blastChunk.getX(), blastChunk.getZ(), bActivateAboveLimit ? EnumBlastLimit.NO_RESTRICTION: EnumBlastLimit.BELOW_LIMIT_ONLY);
	        	}
	    	}
    	}
    }
}
