package com.bukkit.happo2000.BlastControl;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.ExplosionPrimedEvent;
import org.bukkit.util.Vector;

public class BlastControlEntityListener extends EntityListener 
{
    private final BlastControl plugin;

    public BlastControlEntityListener(BlastControl instance) 
    {
        plugin = instance;
    }
    
    public void creeperAttackTarget(Creeper mobCreeper)
    {
		LivingEntity	mobTarget 	= mobCreeper.getTarget();

		Location  	lCreeper = mobCreeper.getLocation();
		Vector 	  	vCreeper = new Vector(lCreeper.getX(), lCreeper.getY(), lCreeper.getZ());
		
		Location  	lTarget = mobTarget.getLocation();
		Vector		vTarget = new Vector(lTarget.getX(), lTarget.getY(), lTarget.getZ());
		
		if (vTarget.isInSphere(vCreeper, 5))
			mobTarget.damage(1, mobCreeper);
    }

    @Override
    public void onExplosionPrimed(ExplosionPrimedEvent event) 
    {
    	BlastConfiguration blastConfig = plugin.getBlastConfiguration();
    	
    	if (blastConfig.isPluginEnabled() && !event.isCancelled())
    	{
	    	if (blastConfig.getCreeperSetting() != EnumCreeperSetting.ENABLED && event.getEntity() instanceof Creeper)
	    	{
	    		switch (blastConfig.getCreeperSetting())
	    		{
	    			case DISABLED_WITH_DESPAWN:
		    			event.getEntity().remove();
		    			event.setCancelled(true);
	    				break;
	    			case DISABLED_WITH_FANGS:
	    				creeperAttackTarget((Creeper)event.getEntity());
		    			event.setCancelled(true);
		    			break;
		    		case DISABLED:
		    			event.setCancelled(true);
		    			break;
		    		case LIMITED_WITH_DESPAWN:
	    				if (event.getEntity().getLocation().getBlockY() > blastConfig.getBlastLimit())
	    				{
	    					creeperAttackTarget((Creeper)event.getEntity());
	    					event.setCancelled(true);
	    				}
	    				break;
		    		case LIMITED_WITH_FANGS:
	    				if (event.getEntity().getLocation().getBlockY() > blastConfig.getBlastLimit())
	    				{
	    					creeperAttackTarget((Creeper)event.getEntity());
	    					event.setCancelled(true);
	    				}
	    				break;
	    			case LIMITED:
	    				if (event.getEntity().getLocation().getBlockY() > blastConfig.getBlastLimit())
	    					event.setCancelled(true);
		    			break;
	    		}
	    	}
	    	else if (event.getEntity() instanceof TNTPrimed)
	    	{
	    		Chunk blastChunk = event.getEntity().getWorld().getChunkAt(event.getEntity().getLocation());
	    		
	    		event.setRadius(blastConfig.getBlastRadius());
	    		
	    		switch (plugin.getBlastStatus(blastChunk.getX(), blastChunk.getZ()))
	    		{
	    		case BELOW_LIMIT_ONLY:
	    			if (event.getEntity().getLocation().getBlockY() <= blastConfig.getBlastLimit())
	    				break;
	    		case DISABLED:
	    			event.getEntity().remove();
	    			event.setCancelled(true);
	    		}
	    	}
    	}
    }
    
    @Override
    public void onEntityExplode(EntityExplodeEvent event) 
    {
    	BlastConfiguration blastConfig = plugin.getBlastConfiguration();

		if (blastConfig.isPluginEnabled() && (!event.isCancelled()) && event.getEntity() instanceof TNTPrimed)
    	{
    		Chunk blastChunk = event.getEntity().getWorld().getChunkAt(event.getEntity().getLocation());

    		event.setYield(blastConfig.getBlastYield());
    		
    		switch (plugin.getBlastStatus(blastChunk.getX(), blastChunk.getZ()))
    		{
    		case BELOW_LIMIT_ONLY:
    			if (event.getEntity().getLocation().getBlockY() <= blastConfig.getBlastLimit())
    				break;
    		case DISABLED:
    			event.getEntity().remove();
    			event.setCancelled(true);
    		}
    	}
    }
}

