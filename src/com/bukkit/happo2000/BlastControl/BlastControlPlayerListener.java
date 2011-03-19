package com.bukkit.happo2000.BlastControl;

import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;

public class BlastControlPlayerListener extends PlayerListener 
{
    private final BlastControl plugin;

    public BlastControlPlayerListener(BlastControl instance) 
    {
        plugin = instance;
    }
    
    public void onPlayerQuit(PlayerEvent event) 
    {
    	plugin.removeFromReclaim(event.getPlayer().getEntityId());
    }
    
    public void onPlayerKick(PlayerKickEvent event) 
    {
    	plugin.removeFromReclaim(event.getPlayer().getEntityId());
    }
}
