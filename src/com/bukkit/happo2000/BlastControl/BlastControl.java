package com.bukkit.happo2000.BlastControl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class BlastControl extends JavaPlugin implements CommandExecutor
{
    private final 	BlastControlEntityListener		entityListener  	= new BlastControlEntityListener(this);
    private final 	BlastControlBlockListener 		blockListener   	= new BlastControlBlockListener(this);
    private final	BlastControlWorldListener		worldListener		= new BlastControlWorldListener(this);
    private final	BlastControlPlayerListener		playerListener  	= new BlastControlPlayerListener(this);
    private final	HashMap<Integer,MetadataChunk>	chunkList			= new HashMap<Integer,MetadataChunk>();
    private final	List<Integer>					playerList			= new ArrayList<Integer>();
    private final	BlastConfiguration				blastConfiguration	= new BlastConfiguration();
    private  		PermissionHandler 				permissions;
    
    // Constants
    public static final String 	PERMISSION_PLACE_ABOVE_LIMIT  	= "bc.tnt.abovelimit.place";
    public static final String 	PERMISSION_ACTIVATE_ABOVE_LIMIT	= "bc.tnt.abovelimit.activate";
    public static final String 	PERMISSION_TNT_ALLOWED  		= "bc.tnt.allowed";
    public static final String 	PERMISSION_RECLAIM    			= "bc.tnt.reclaim";
    //public static final String 	PERMISSION_IMMUNE    			= "bc.tnt.immune";
    public static final String 	PERMISSION_SET_LIMIT    		= "bc.set.limit";
    public static final String 	PERMISSION_SET_CREEPER    		= "bc.set.creeper";
    public static final String 	PERMISSION_SET_YIELD    		= "bc.set.yield";
    public static final String 	PERMISSION_SET_RADIUS    		= "bc.set.radius";
    public static final String 	PERMISSION_ENABLE_DISABLE    	= "bc.set.enable";

    public static final String  DISPLAY_PREFIX					= ChatColor.AQUA + "[" + ChatColor.WHITE + "BC" + ChatColor.AQUA + "]" + ChatColor.WHITE + " "; 
    
    public BlastControl() 
    {
        super(); 
    }

    public void onEnable() 
    {
        PluginManager 			pluginManager 		= getServer().getPluginManager();
        Plugin 					permissionsPlugin 	= pluginManager.getPlugin("Permissions");
        PluginDescriptionFile 	pdfFile 			= this.getDescription();

    	blastConfiguration.loadConfiguation();
    	
    	if (permissionsPlugin == null)
        {
        	blastConfiguration.loadPermissions();
        	System.out.println(pdfFile.getName() + " - Permissions plugin not detected.");
        }
        else
        {
            this.permissions = ((Permissions)permissionsPlugin).getHandler();
            blastConfiguration.setPermissionsEnabled(true);
        }        
    	
    	pluginManager.registerEvent(Event.Type.BLOCK_PLACE, 	this.blockListener, 	Event.Priority.High, this);
    	pluginManager.registerEvent(Event.Type.BLOCK_DAMAGE, 	this.blockListener, 	Event.Priority.High, this);

    	pluginManager.registerEvent(Event.Type.ENTITY_EXPLODE, 	this.entityListener, 	Event.Priority.High, this);
    	pluginManager.registerEvent(Event.Type.EXPLOSION_PRIME, this.entityListener, 	Event.Priority.High, this);
    	//pluginManager.registerEvent(Event.Type.ENTITY_DAMAGE, 	this.entityListener, 	Event.Priority.High, this);

    	pluginManager.registerEvent(Event.Type.PLAYER_KICK, 	this.playerListener, 	Event.Priority.High, this);
    	pluginManager.registerEvent(Event.Type.PLAYER_QUIT, 	this.playerListener, 	Event.Priority.High, this);
    	
    	pluginManager.registerEvent(Event.Type.CHUNK_UNLOAD, 	this.worldListener, 	Event.Priority.High, this);

    	getCommand("bc").setExecutor(this);
	        
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public void onDisable() 
    {
    	blastConfiguration.disableSilently();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
    	String[] 	trimmedArgs 	= args;

        if (trimmedArgs.length > 0)
        {
            String  subCommandName	= trimmedArgs[0].toLowerCase();

        	if (subCommandName.equals("status"))
        		showStatus(sender);
        	else if (subCommandName.equals("limit"))
        		setBlastLimit(sender, trimmedArgs);
        	else if (subCommandName.equals("yield"))
        		setBlastYield(sender, trimmedArgs);
        	else if (subCommandName.equals("radius"))
        		setBlastRadius(sender, trimmedArgs);
        	else if (subCommandName.equals("triggerlimit"))
        		setBlastTriggerLimit(sender, trimmedArgs);
        	else if (subCommandName.equals("enable"))
        		setBlastControlEnabled(sender, true);
        	else if (subCommandName.equals("disable"))
        		setBlastControlEnabled(sender, false);
        	else if (subCommandName.equals("creeper"))
        		setCreeperState(sender, trimmedArgs);
        	else if (subCommandName.equals("reclaim"))
        		toggleReclaim(sender);
        	else if ((!blastConfiguration.isPermissionsEnabled()) && subCommandName.equals("reload"))
        		blastConfiguration.loadPermissions();
        	else
        		showHelp(sender);
        }
        else
    		showHelp(sender);
        
        return true;
    }

	public void showHelp(CommandSender sender)
	{
		sender.sendMessage(DISPLAY_PREFIX + "-- BlastControl Help - Summary");
		sender.sendMessage(DISPLAY_PREFIX + "/bc status - Displays current BlastControl settings");

		if (CheckPermission(sender, PERMISSION_RECLAIM))
			sender.sendMessage(DISPLAY_PREFIX + "/bc reclaim - toggles TNT reclaim mode on and off");
		
		if (CheckPermission(sender, PERMISSION_SET_LIMIT))
		{
			sender.sendMessage(DISPLAY_PREFIX + "/bc limit [#] - Sets a new height limit on explosions");
			sender.sendMessage(DISPLAY_PREFIX + "/bc triggerlimit [#] - Sets a new time-to-link limit on explosions");
		}
		
		if (CheckPermission(sender, PERMISSION_SET_YIELD))
			sender.sendMessage(DISPLAY_PREFIX + "/bc yield [%] - Sets a new item destruction percentage");

		if (CheckPermission(sender, PERMISSION_SET_RADIUS))
			sender.sendMessage(DISPLAY_PREFIX + "/bc radius [#] - Sets a new explosion radius");

		if (CheckPermission(sender, PERMISSION_ENABLE_DISABLE))
			sender.sendMessage(DISPLAY_PREFIX + "/bc [enable/disable] - Enable/Disable BlastControl");

		if (CheckPermission(sender, PERMISSION_SET_CREEPER))
			sender.sendMessage(DISPLAY_PREFIX + "/bc creeper [enable/disable/disable-fangs/disable-despawn/disable-entity/limit/limit-fangs/limit-depawn/limit-entity] - Sets different creeper behavior");
		
		if (sender.isOp() && !blastConfiguration.isPermissionsEnabled())
			sender.sendMessage(DISPLAY_PREFIX + "/bc reload - Reloads the user permission file");
	}
	
	public void setBlastControlEnabled(CommandSender sender, boolean bState)
	{
		if (CheckPermission(sender, PERMISSION_ENABLE_DISABLE))
		{
			blastConfiguration.setPluginEnabled(bState);
			
			sender.sendMessage(DISPLAY_PREFIX + "Set Internal Status: " + ChatColor.AQUA + (blastConfiguration.isPluginEnabled() ? "Enabled" : "Disabled"));
		}
		else
			sender.sendMessage(DISPLAY_PREFIX + "Access Denied");
	}
	
	public void setCreeperState(CommandSender sender, String[] args)
	{
		if (CheckPermission(sender, PERMISSION_SET_CREEPER))
		{
			if (args.length >= 2)
			{
				blastConfiguration.setCreeperSetting(BlastHelper.parseCreeperSetting(args[1]));

				sender.sendMessage(DISPLAY_PREFIX + "Creeper Blast : " + ChatColor.AQUA + BlastHelper.getDisplayFriendlyName(blastConfiguration.getCreeperSetting()));
			}
			else
				sender.sendMessage(DISPLAY_PREFIX + "Usage is /bc creeper [enable/disable/disable-fangs/disable-despawn/disable-entity/limit/limit-fangs/limit-depawn/limit-entity]");
		}
		else
			sender.sendMessage(DISPLAY_PREFIX + "Access Denied");
	}
			
	public void setBlastLimit(CommandSender sender, String[] args)
	{
		if (CheckPermission(sender, PERMISSION_SET_LIMIT))
		{
			boolean bParseFailed = true;

			if (args.length >= 2)
			{
				try
				{
					int nLimit = Integer.parseInt(args[1]);
					
					if (nLimit <= 128)
					{
						blastConfiguration.setBlastLimit(nLimit);

						sender.sendMessage(DISPLAY_PREFIX + "New Blast Height: " + ChatColor.AQUA + Integer.toString(blastConfiguration.getBlastLimit()));

						bParseFailed = false;
					}
				}
				catch (NumberFormatException nfe) { /* do nothing */ }
			}
			
			if (bParseFailed)
			{
				sender.sendMessage(DISPLAY_PREFIX + "Usage is /bc limit <newlimit>");
				sender.sendMessage(DISPLAY_PREFIX + "Valid range is 0 to 128");
			}
		}
		else
			sender.sendMessage(DISPLAY_PREFIX + "Access Denied");
	}
	
	public void setBlastYield(CommandSender sender, String[] args) 
	{
		if (CheckPermission(sender, PERMISSION_SET_YIELD))
		{
			boolean bParseFailed = true;

			if (args.length >= 2)
			{
				try
				{
					float fYield = Integer.parseInt(args[1]) / 100.0f;
					
					if (fYield >= 0.0f && fYield <= 1.0f)
					{
						blastConfiguration.setBlastYield(fYield);
	
						sender.sendMessage(DISPLAY_PREFIX + "Blast Yield: " + ChatColor.AQUA + Integer.toString((int)(blastConfiguration.getBlastYield() * 100.0f)) + "%");
	
						bParseFailed = false;
					}
				}
				catch (NumberFormatException nfe) { /* do nothing */ }
			}
			
			if (bParseFailed)
			{
				sender.sendMessage(DISPLAY_PREFIX + "Usage is /bc yield <pct>");
				sender.sendMessage(DISPLAY_PREFIX + "Valid range is 0 to 100.");
			}
		}
		else
			sender.sendMessage(DISPLAY_PREFIX + "Access Denied");
	}

	public void setBlastRadius(CommandSender sender, String[] args) 
	{
		if (CheckPermission(sender, PERMISSION_SET_RADIUS))
		{
			boolean bParseFailed = true;

			if (args.length >= 2)
			{
				try
				{
					int nRadius = Integer.parseInt(args[1]);
					
					if (nRadius >= 0 && nRadius <= 1000)
					{
						blastConfiguration.setBlastRadius(nRadius);
	
						sender.sendMessage(DISPLAY_PREFIX + "Blast Radius: " + ChatColor.AQUA + Integer.toString(blastConfiguration.getBlastRadius()) + ChatColor.WHITE + " blocks.");
	
						bParseFailed = false;
					}
				}
				catch (NumberFormatException nfe) { /* do nothing */ }
			}
			
			if (bParseFailed)
			{
				sender.sendMessage(DISPLAY_PREFIX + "Usage is /bc radius <size>");
				sender.sendMessage(DISPLAY_PREFIX + "Valid range is 0 to 1000.");
			}
		}
		else
			sender.sendMessage(DISPLAY_PREFIX + "Access Denied");
	}
	
	public void setBlastTriggerLimit(CommandSender sender, String[] args)
	{
		if (CheckPermission(sender, PERMISSION_SET_LIMIT))
		{
			boolean bParseFailed = true;

			if (args.length >= 2)
			{
				try
				{
					blastConfiguration.setBlastTriggerLimit(Integer.parseInt(args[1]));

					sender.sendMessage(DISPLAY_PREFIX + "Max Blast Trigger Timing: " + ChatColor.AQUA + Integer.toString(blastConfiguration.getBlastTriggerLimit()) + ChatColor.WHITE + " millis");

					bParseFailed = false;
				}
				catch (NumberFormatException nfe) { /* do nothing */ }
			}
			
			if (bParseFailed)
			{
				sender.sendMessage(DISPLAY_PREFIX + "Usage is /bc triggerlimit <newlimit>");
				sender.sendMessage(DISPLAY_PREFIX + "Trigger limit is in milliseconds");
			}
		}
		else
			sender.sendMessage(DISPLAY_PREFIX + "Access Denied");
	}
	
	public void showStatus(CommandSender sender)
	{
		PluginDescriptionFile pdfFile = this.getDescription();
		
		sender.sendMessage(DISPLAY_PREFIX + " -- " + pdfFile.getName() + " [v" + pdfFile.getVersion() + "] -- Status: " + ChatColor.AQUA + (blastConfiguration.isPluginEnabled() ? "Enabled" : "Disabled"));

		sender.sendMessage(DISPLAY_PREFIX + "Blast Height Limit : " + ChatColor.AQUA + Integer.toString(blastConfiguration.getBlastLimit()) + ChatColor.WHITE + " - Creeper Blast: " + ChatColor.AQUA + BlastHelper.getDisplayFriendlyName(blastConfiguration.getCreeperSetting()));
		sender.sendMessage(DISPLAY_PREFIX + "Blast Trigger Limit : " + ChatColor.AQUA + Integer.toString(blastConfiguration.getBlastTriggerLimit()) + ChatColor.WHITE + " millis");
		sender.sendMessage(DISPLAY_PREFIX + "Blast Radius : " + ChatColor.AQUA + Integer.toString(blastConfiguration.getBlastRadius()) + ChatColor.WHITE + " blocks - Blast Yield: " + ChatColor.AQUA + Integer.toString((int)(blastConfiguration.getBlastYield() * 100.0f)) + "%");
	
		if (blastConfiguration.isPluginEnabled())
		{
			boolean bTNTAllowed 		= CheckPermission(sender, PERMISSION_TNT_ALLOWED);
			boolean bPlaceAboveLimit 	= CheckPermission(sender, PERMISSION_PLACE_ABOVE_LIMIT);
			boolean bActivateAboveLimit	= CheckPermission(sender, PERMISSION_ACTIVATE_ABOVE_LIMIT);

			sender.sendMessage(DISPLAY_PREFIX + "Your status: You are " + (bTNTAllowed ? ChatColor.GREEN + "permitted" : ChatColor.RED + "denied") + ChatColor.WHITE + " the use of TNT.");
			
			if (bTNTAllowed)
			{
				if (!bPlaceAboveLimit)
					sender.sendMessage(DISPLAY_PREFIX + ChatColor.RED + "Restriction" + ChatColor.WHITE + ": You can only place at level " + ChatColor.AQUA + Integer.toString(blastConfiguration.getBlastLimit()) + ChatColor.WHITE + " and below.");

				if (!bActivateAboveLimit)
					sender.sendMessage(DISPLAY_PREFIX + ChatColor.RED + "Restriction" + ChatColor.WHITE + ": You can only activate at level " + ChatColor.AQUA + Integer.toString(blastConfiguration.getBlastLimit()) + ChatColor.WHITE + " and below.");
			}
		}
	}
	
	public EnumBlastLimit getBlastStatus(int posX, int posZ)
	{
		EnumBlastLimit 	blastLimit 	= EnumBlastLimit.DISABLED;
		long 			nTime 		= Calendar.getInstance().getTimeInMillis() - blastConfiguration.getBlastTriggerLimit();

		MetadataChunk 	metaChunk 	= chunkList.get(Integer.valueOf((posX & 0xFFFF) | (posZ << 16)));
		
		if (metaChunk == null || (blastLimit = metaChunk.getValidBlastStatus(nTime)) == EnumBlastLimit.DISABLED)
		{
			int	minX = posX - 2;
			int	minZ = posZ - 2;
			
			for (int curX = posX + 1; curX > minX; --curX)
			{
				for (int curZ = posZ + 1; curZ > minZ; --curZ)
				{
					metaChunk = chunkList.get(Integer.valueOf((curX & 0xFFFF) | (curZ << 16)));

					if (metaChunk != null)
					{
						EnumBlastLimit 	tempBlastLimit  = metaChunk.getValidBlastStatus(nTime);
						
						if (tempBlastLimit != EnumBlastLimit.DISABLED)
						{
							blastLimit = tempBlastLimit;
							
							if (blastLimit == EnumBlastLimit.BELOW_LIMIT_ONLY) // BELOW_LIMIT_ONLY overrides NO_RESTRICTION
							{
								curZ = minZ; // Exit For
								curX = minX; // Exit For
							}
						}
					}
				}
			}

			setChunkStatus(posX, posZ, blastLimit);
		}
		
		return blastLimit;
	}
	
	public void setChunkStatus(int posX, int posZ, EnumBlastLimit status)
	{
		int nChunkID = (posX & 0xFFFF) | (posZ << 16);
		
		MetadataChunk 	metaChunk 	= chunkList.get(Integer.valueOf(nChunkID));
		
		if (metaChunk == null)
			metaChunk = new MetadataChunk(status);
		else
			metaChunk.setBlastStatus(status);
		
		chunkList.put(Integer.valueOf(nChunkID), metaChunk);
	}
	
	public void removeChunkStatus(int posX, int posZ)
	{
		chunkList.remove(Integer.valueOf((posX & 0xFFFF) | (posZ << 16)));
	}
	
	public boolean isOnReclaim(int entityId)
	{
		return playerList.contains(Integer.valueOf(entityId));
	}

	public void toggleReclaim(CommandSender sender)
	{
		if (sender instanceof Player)
		{
			if (CheckPermission(sender, PERMISSION_RECLAIM))
			{
				int entityId = ((Player)sender).getEntityId();
				
				if (isOnReclaim(Integer.valueOf(entityId)))
				{
					playerList.remove(Integer.valueOf(entityId));
					sender.sendMessage(DISPLAY_PREFIX + "TNT Reclaim mode toggled " + ChatColor.AQUA + "OFF");
				}
				else
				{
					playerList.add(Integer.valueOf(entityId));
					sender.sendMessage(DISPLAY_PREFIX + "TNT Reclaim mode toggled " + ChatColor.AQUA + "ON");
				}
			}
			else
				sender.sendMessage(DISPLAY_PREFIX + "Access denied");
		}
		else
			sender.sendMessage(DISPLAY_PREFIX + "Console cannot toggle mode");
	}
	
	public void removeFromReclaim(int entityId)
	{
		playerList.remove(Integer.valueOf(entityId));
	}

    public boolean CheckPermission(CommandSender sender, String permissionName)
    {
		return sender.isOp() || CheckPermission((Player)sender, permissionName);
    }
    
	public boolean CheckPermission(Player player, String permissionName)
	{
		if (blastConfiguration.isPermissionsEnabled())
			return permissions.has(player, permissionName);
		else
			return blastConfiguration.isOpsRequired(permissionName) ? player.isOp() : true;
	}

    public boolean isDebugging(final Player player) 
    {
        return false;
    }
    
    public void setDebugging(final Player player, final boolean value) 
    {
    }

	public BlastConfiguration getBlastConfiguration() 
	{
		return blastConfiguration;
	}
}

