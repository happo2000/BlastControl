package com.bukkit.happo2000.BlastControl;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
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
		LivingEntity		mobTarget 	= mobCreeper.getTarget();

		Location  	lCreeper = mobCreeper.getLocation();
		Vector 	  	vCreeper = new Vector(lCreeper.getX(), lCreeper.getY(), lCreeper.getZ());
		
		Location  	lTarget = mobTarget.getLocation();
		Vector		vTarget = new Vector(lTarget.getX(), lTarget.getY(), lTarget.getZ());
		
		if (vTarget.isInSphere(vCreeper, 3))
			mobTarget.damage(1, mobCreeper);
    }

    @Override
    public void onExplosionPrime(ExplosionPrimeEvent event) 
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

    	if (blastConfig.isPluginEnabled() && (!event.isCancelled()))
		{
    		event.setYield(blastConfig.getBlastYield());
		
			if (event.getEntity() instanceof TNTPrimed)
	    	{
	    		Chunk blastChunk = event.getEntity().getWorld().getChunkAt(event.getEntity().getLocation());
	
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
			else if (event.getEntity() instanceof Creeper)
			{
				switch (blastConfig.getCreeperSetting())
				{
				case LIMITED_ONLY_ENTITY:
	    			if (event.getEntity().getLocation().getBlockY() <= blastConfig.getBlastLimit())
	    				break;
				case DISABLED_ONLY_ENTITY:
					event.setCancelled(true);

					// Make explosion noise
					CraftWorld world = (CraftWorld)event.getEntity().getWorld();
					
					world.getHandle().a(null, event.getLocation().getX(), event.getLocation().getY(), event.getLocation().getZ(), 0);
				}
			}
    	}
    }
    
    /*@Override
    public void onEntityDamage(EntityDamageEvent event) 
    {
    	BlastConfiguration blastConfig = plugin.getBlastConfiguration();

    	if (blastConfig.isPluginEnabled() && (!event.isCancelled()) && event.getEntity() instanceof Player)
		{
	    	switch (event.getCause())
	    	{
	    	case BLOCK_EXPLOSION:
	    	case ENTITY_EXPLOSION:
	    		event.setCancelled(plugin.CheckPermission((Player)event.getEntity(), BlastControl.PERMISSION_IMMUNE));
	    	}
		}
    }*/
}

