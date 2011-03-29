package com.bukkit.happo2000.BlastControl;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ExplosionPrimedEvent;
import org.bukkit.util.Vector;

public class BlastControlEntityListener extends EntityListener 
{
    private final BlastControl plugin;
    
    class PrimedEventHandler
    {
    	private Entity 		entity;
    	private float 		fRadius;
		private boolean 	bCancelled;
    	
    	public PrimedEventHandler(Entity entity, float fRadius, boolean bCancelled)
    	{
    		this.entity 	= entity;
    		this.fRadius 	= fRadius;
    		this.bCancelled = false;
    	}
    	
    	public Entity 		getEntity() 						{ return entity; };
    	public float 		getRadius() 						{ return fRadius; };
    	public void			setRadius(float fRadius)			{ this.fRadius = fRadius; };
    	public boolean 		isCancelled() 						{ return bCancelled; };
    	public void			setCancelled(boolean bCancelled)	{ this.bCancelled = bCancelled; };
    }
    
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
		
		if (vTarget.isInSphere(vCreeper, 4))
			mobTarget.damage(1, mobCreeper);
    }

    public void onExplosionPrimed(ExplosionPrimedEvent event) // Old and broken
    {
    	BlastConfiguration blastConfig = plugin.getBlastConfiguration();
    	
    	if (blastConfig.isPluginEnabled() && !event.isCancelled())
    	{
        	PrimedEventHandler primeEventHandler = new PrimedEventHandler(event.getEntity(), event.getRadius(), event.isCancelled());
    		
        	onExplosionIntermediate(primeEventHandler);
        	
        	event.setRadius(primeEventHandler.getRadius());
        	event.setCancelled(primeEventHandler.isCancelled());
    	}
    }

    public void onExplosionPrime(ExplosionPrimeEvent event) // New hotness 
    {
    	BlastConfiguration blastConfig = plugin.getBlastConfiguration();
    	
    	if (blastConfig.isPluginEnabled() && !event.isCancelled())
    	{
        	PrimedEventHandler primeEventHandler = new PrimedEventHandler(event.getEntity(), event.getRadius(), event.isCancelled());
        	
        	onExplosionIntermediate(primeEventHandler);
    		
        	event.setRadius(primeEventHandler.getRadius());
        	event.setCancelled(primeEventHandler.isCancelled());
    	}
    }
    
    public void onExplosionIntermediate(PrimedEventHandler event)
    {
    	BlastConfiguration blastConfig = plugin.getBlastConfiguration();
    	
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
}

