package com.bukkit.happo2000.BlastControl;

import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;

public class BlastControlWorldListener extends WorldListener
{
	private final BlastControl plugin;
	
    public BlastControlWorldListener(BlastControl instance) 
    {
        plugin 		= instance;
	}
    
    public void onChunkUnloaded(ChunkUnloadEvent event) 
    {
    	plugin.getBlastChunkInfo().removeChunkInfo(event.getChunk().getX(), event.getChunk().getZ());
    }
}
