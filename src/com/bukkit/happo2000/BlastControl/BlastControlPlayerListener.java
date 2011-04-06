package com.bukkit.happo2000.BlastControl;

import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class BlastControlPlayerListener extends PlayerListener 
{
    private final BlastControl plugin;

    public BlastControlPlayerListener(BlastControl instance) 
    {
        plugin = instance;
    }
    
    public void onPlayerQuit(PlayerQuitEvent event) 
    {
    	plugin.removeFromReclaim(event.getPlayer().getEntityId());
    	plugin.getBlastChunkInfo().removeLinkLimit(event.getPlayer().getEntityId());
    }
    
    public void onPlayerKick(PlayerKickEvent event) 
    {
    	plugin.removeFromReclaim(event.getPlayer().getEntityId());
    	plugin.getBlastChunkInfo().removeLinkLimit(event.getPlayer().getEntityId());
    }
}
