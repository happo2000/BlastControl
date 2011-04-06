package com.bukkit.happo2000.BlastControl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;

public class BlastConfiguration 
{
    public static final String 	CONFIGUATION_FILE_OLD			= "blastcontrol.cfg";
    
    public static final String 	CONFIGUATION_FILE_DIRECTORY		= "plugins/BlastControl"; // No last slash
    public static final String 	CONFIGUATION_FILE_SETTINGS		= "settings.properties";
    public static final String 	CONFIGUATION_FILE_PERMISSIONS	= "user.properties";
    
    // Saved Configuration
    private boolean						bPluginEnabled			= true;
    private EnumCreeperSetting 			eCreeperSetting			= EnumCreeperSetting.LIMITED_WITH_FANGS;
	private int 						nBlastLimit				= 54;	  // 54th level
    private int							nBlastTriggerLimit		= 10000;  // 10 seconds
    private float						fBlastYield				= 0.3f;   // 30%
    private int							nBlastRadius			= 4;      // 4 blocks
    private int							nBlastLinkLimit			= 15;	  // 15 TNT links ( 16 TNT detonated )
    
    // State Configuration
    private boolean						bIsPermissionsEnabled	= false;
    private HashMap<String, Boolean>	permissionsMap			= null;
    
    public void loadConfiguationOld()
    {
        File 	file 	= new File(CONFIGUATION_FILE_OLD);
        
        if (file.exists())
        {
            try 
			{
                Properties 	configFile 	= new Properties();
	    		InputStream is 			= new FileInputStream(file);
	    		
	        	configFile.load(is);
	
	        	is.close();

	        	if (configFile.getProperty("Enabled") != null)
	        		this.bPluginEnabled 	= Boolean.parseBoolean(configFile.getProperty("Enabled"));
	        	
	        	if (configFile.getProperty("BlastLimit") != null)
	        		this.nBlastLimit 		= Integer.parseInt(configFile.getProperty("BlastLimit"));
	        	
	        	if (configFile.getProperty("BlastTriggerLimit") != null)
	        		this.nBlastTriggerLimit = Integer.parseInt(configFile.getProperty("BlastTriggerLimit"));
	        	
	        	if (configFile.getProperty("CreeperSetting") != null)
	        		this.eCreeperSetting	= BlastHelper.parseCreeperSetting(configFile.getProperty("CreeperSetting"));
	        	
	        	if (configFile.getProperty("BlastRadius") != null)
	        		this.nBlastRadius 		= Integer.parseInt(configFile.getProperty("BlastRadius"));

	        	if (configFile.getProperty("BlastYield") != null)
	        		this.fBlastYield 		= Float.parseFloat(configFile.getProperty("BlastYield"));

	        	if (configFile.getProperty("BlastLinkLimit") != null)
	        		this.nBlastLinkLimit 	= Integer.parseInt(configFile.getProperty("BlastLinkLimit"));
			} 
			catch (Exception e)
			{
			}
			
			file.delete();
        }
    }
    
    public void loadConfiguation()
    {
        File 	file 	= new File(CONFIGUATION_FILE_DIRECTORY + "/" + CONFIGUATION_FILE_SETTINGS);
        
        if (!file.exists())
        {
        	loadConfiguationOld();
        	saveConfiguation();
        }
        	
    	try 
		{
            Properties 		configFile 	= new Properties();
    		InputStream 	is 			= new FileInputStream(file);
    		
        	configFile.load(is);

        	is.close();

        	if (configFile.getProperty("Enabled") != null)
        		this.bPluginEnabled 	= Boolean.parseBoolean(configFile.getProperty("Enabled"));
        	
        	if (configFile.getProperty("BlastLimit") != null)
        		this.nBlastLimit 		= Integer.parseInt(configFile.getProperty("BlastLimit"));
        	
        	if (configFile.getProperty("BlastTriggerLimit") != null)
        		this.nBlastTriggerLimit = Integer.parseInt(configFile.getProperty("BlastTriggerLimit"));
        	
        	if (configFile.getProperty("CreeperSetting") != null)
        		this.eCreeperSetting	= BlastHelper.parseCreeperSetting(configFile.getProperty("CreeperSetting"));
        	
        	if (configFile.getProperty("BlastRadius") != null)
        		this.nBlastRadius 		= Integer.parseInt(configFile.getProperty("BlastRadius"));

        	if (configFile.getProperty("BlastYield") != null)
        		this.fBlastYield 		= Float.parseFloat(configFile.getProperty("BlastYield"));

        	if (configFile.getProperty("BlastLinkLimit") != null)
        		this.nBlastLinkLimit	= Integer.parseInt(configFile.getProperty("BlastLinkLimit"));
		} 
		catch (Exception e)
		{
			System.out.println("[BlastControl] Unable to load configuation file.");
		}
    }
    
    public void saveConfiguation()
    {
    	File		directory	= new File(CONFIGUATION_FILE_DIRECTORY);
        File 		file 		= new File(CONFIGUATION_FILE_DIRECTORY + "/" + CONFIGUATION_FILE_SETTINGS);
        Properties 	configFile 	= new Properties();
        
        if (!directory.exists())
        	directory.mkdirs();
    
    	configFile.setProperty("Enabled", Boolean.toString(this.bPluginEnabled));
    	configFile.setProperty("CreeperSetting", BlastHelper.getConfigFriendlyName(this.eCreeperSetting));
    	configFile.setProperty("BlastLimit", Integer.toString(this.nBlastLimit));
    	configFile.setProperty("BlastTriggerLimit", Integer.toString(this.nBlastTriggerLimit));
    	configFile.setProperty("BlastYield", Float.toString(this.fBlastYield));
    	configFile.setProperty("BlastRadius", Integer.toString(this.nBlastRadius));
    	configFile.setProperty("BlastLinkLimit", Integer.toString(this.nBlastLinkLimit));
		
    	try 
		{
    		OutputStream os = new FileOutputStream(file);
    		
        	configFile.store(os, " BlastControl Configuation");

        	os.flush();
        	os.close();
		} 
		catch (IOException e)
		{
			System.out.println("[BlastControl] Unable to save configuation file.");
		}
		
		configFile.clear();
    }
    
    public void loadPermissions()
    {
        File 		file 		= new File(CONFIGUATION_FILE_DIRECTORY + "/" + CONFIGUATION_FILE_PERMISSIONS);

    	if (permissionsMap == null)
    	{
    		permissionsMap = new HashMap<String, Boolean>();
    		
    		// Defaults
	        permissionsMap.put(BlastControl.PERMISSION_LINK_ABOVE_LIMIT, Boolean.TRUE);
	        permissionsMap.put(BlastControl.PERMISSION_PLACE_ABOVE_LIMIT, Boolean.TRUE);
	        permissionsMap.put(BlastControl.PERMISSION_ACTIVATE_ABOVE_LIMIT, Boolean.TRUE);
			permissionsMap.put(BlastControl.PERMISSION_TNT_ALLOWED, Boolean.FALSE);
			permissionsMap.put(BlastControl.PERMISSION_RECLAIM, Boolean.FALSE);
			permissionsMap.put(BlastControl.PERMISSION_SET_LIMIT, Boolean.TRUE);
			permissionsMap.put(BlastControl.PERMISSION_SET_CREEPER, Boolean.TRUE);
			permissionsMap.put(BlastControl.PERMISSION_SET_YIELD, Boolean.TRUE);
			permissionsMap.put(BlastControl.PERMISSION_SET_RADIUS, Boolean.TRUE);
			permissionsMap.put(BlastControl.PERMISSION_ENABLE_DISABLE, Boolean.TRUE);
			permissionsMap.put(BlastControl.PERMISSION_IMMUNE, Boolean.TRUE);
    	}

    	if (file.exists())
    	{
			try 
			{
	            Properties 		configFile 	= new Properties();
	    		InputStream     is 			= new FileInputStream(file);
					
	        	configFile.load(is);
	        	is.close();
	
	        	if (configFile.getProperty(BlastControl.PERMISSION_LINK_ABOVE_LIMIT) != null) 		permissionsMap.put(BlastControl.PERMISSION_LINK_ABOVE_LIMIT, Boolean.parseBoolean(configFile.getProperty(BlastControl.PERMISSION_LINK_ABOVE_LIMIT)));
	        	if (configFile.getProperty(BlastControl.PERMISSION_PLACE_ABOVE_LIMIT) != null) 		permissionsMap.put(BlastControl.PERMISSION_PLACE_ABOVE_LIMIT, Boolean.parseBoolean(configFile.getProperty(BlastControl.PERMISSION_PLACE_ABOVE_LIMIT)));
	        	if (configFile.getProperty(BlastControl.PERMISSION_ACTIVATE_ABOVE_LIMIT) != null) 	permissionsMap.put(BlastControl.PERMISSION_ACTIVATE_ABOVE_LIMIT, Boolean.parseBoolean(configFile.getProperty(BlastControl.PERMISSION_ACTIVATE_ABOVE_LIMIT)));
	        	if (configFile.getProperty(BlastControl.PERMISSION_TNT_ALLOWED) != null) 			permissionsMap.put(BlastControl.PERMISSION_TNT_ALLOWED, Boolean.parseBoolean(configFile.getProperty(BlastControl.PERMISSION_TNT_ALLOWED)));
	        	if (configFile.getProperty(BlastControl.PERMISSION_RECLAIM) != null) 				permissionsMap.put(BlastControl.PERMISSION_RECLAIM, Boolean.parseBoolean(configFile.getProperty(BlastControl.PERMISSION_RECLAIM)));
	        	if (configFile.getProperty(BlastControl.PERMISSION_SET_LIMIT) != null) 				permissionsMap.put(BlastControl.PERMISSION_SET_LIMIT, Boolean.parseBoolean(configFile.getProperty(BlastControl.PERMISSION_SET_LIMIT)));
	        	if (configFile.getProperty(BlastControl.PERMISSION_SET_CREEPER) != null) 			permissionsMap.put(BlastControl.PERMISSION_SET_CREEPER, Boolean.parseBoolean(configFile.getProperty(BlastControl.PERMISSION_SET_CREEPER)));
	        	if (configFile.getProperty(BlastControl.PERMISSION_SET_YIELD) != null) 				permissionsMap.put(BlastControl.PERMISSION_SET_YIELD, Boolean.parseBoolean(configFile.getProperty(BlastControl.PERMISSION_SET_YIELD)));
	        	if (configFile.getProperty(BlastControl.PERMISSION_SET_RADIUS) != null)				permissionsMap.put(BlastControl.PERMISSION_SET_RADIUS, Boolean.parseBoolean(configFile.getProperty(BlastControl.PERMISSION_SET_RADIUS)));
	        	if (configFile.getProperty(BlastControl.PERMISSION_ENABLE_DISABLE) != null) 		permissionsMap.put(BlastControl.PERMISSION_ENABLE_DISABLE, Boolean.parseBoolean(configFile.getProperty(BlastControl.PERMISSION_ENABLE_DISABLE)));
	        	if (configFile.getProperty(BlastControl.PERMISSION_IMMUNE) != null) 				permissionsMap.put(BlastControl.PERMISSION_IMMUNE, Boolean.parseBoolean(configFile.getProperty(BlastControl.PERMISSION_IMMUNE)));
	        	
	        	configFile.clear();
			} 
			catch (Exception e) 
			{
			}
    	}
    	else
    		savePermissions();
    }

    public void savePermissions()
    {
    	File		directory	= new File(CONFIGUATION_FILE_DIRECTORY);
        File 		file 		= new File(CONFIGUATION_FILE_DIRECTORY + "/" + CONFIGUATION_FILE_PERMISSIONS);
        Properties 	configFile 	= new Properties();
        
        if (!directory.exists())
        	directory.mkdirs();
        
    	configFile.setProperty(BlastControl.PERMISSION_LINK_ABOVE_LIMIT, 		permissionsMap.get(BlastControl.PERMISSION_LINK_ABOVE_LIMIT).toString());
    	configFile.setProperty(BlastControl.PERMISSION_PLACE_ABOVE_LIMIT, 		permissionsMap.get(BlastControl.PERMISSION_PLACE_ABOVE_LIMIT).toString());
    	configFile.setProperty(BlastControl.PERMISSION_ACTIVATE_ABOVE_LIMIT, 	permissionsMap.get(BlastControl.PERMISSION_ACTIVATE_ABOVE_LIMIT).toString());
    	configFile.setProperty(BlastControl.PERMISSION_TNT_ALLOWED, 			permissionsMap.get(BlastControl.PERMISSION_TNT_ALLOWED).toString());
    	configFile.setProperty(BlastControl.PERMISSION_RECLAIM, 				permissionsMap.get(BlastControl.PERMISSION_RECLAIM).toString());
    	configFile.setProperty(BlastControl.PERMISSION_SET_LIMIT, 				permissionsMap.get(BlastControl.PERMISSION_SET_LIMIT).toString());
    	configFile.setProperty(BlastControl.PERMISSION_SET_CREEPER, 			permissionsMap.get(BlastControl.PERMISSION_SET_CREEPER).toString());
    	configFile.setProperty(BlastControl.PERMISSION_SET_YIELD, 				permissionsMap.get(BlastControl.PERMISSION_SET_YIELD).toString());
    	configFile.setProperty(BlastControl.PERMISSION_SET_RADIUS, 				permissionsMap.get(BlastControl.PERMISSION_SET_RADIUS).toString());
    	configFile.setProperty(BlastControl.PERMISSION_ENABLE_DISABLE, 			permissionsMap.get(BlastControl.PERMISSION_ENABLE_DISABLE).toString());
    	configFile.setProperty(BlastControl.PERMISSION_IMMUNE, 					permissionsMap.get(BlastControl.PERMISSION_IMMUNE).toString());
    	
		try 
		{
    		OutputStream os = new FileOutputStream(file);
    		
    		configFile.store(os, " BlastControl Permissions - true means ops are required.");
    		
    		os.flush();
    		os.close();
		} 
		catch (Exception e) 
		{
			System.out.println("[BlastControl] Unable to save permissions file.");
		}
		
		configFile.clear();
    }

    public boolean isOpsRequired(String permissionName)
    {
    	return permissionsMap.get(permissionName);
    }
    
    public void setPluginEnabled(boolean bPluginEnabled) 
    {
		this.bPluginEnabled = bPluginEnabled;

		saveConfiguation();
	}

    public void disableSilently() 
    {
		this.bPluginEnabled = false;
	}

    public boolean isPluginEnabled() 
	{
		return bPluginEnabled;
	}    
	
    public EnumCreeperSetting getCreeperSetting() 
    {
		return eCreeperSetting;
	}

	public void setCreeperSetting(EnumCreeperSetting eCreeperSetting) 
	{
		this.eCreeperSetting = eCreeperSetting;

		saveConfiguation();
	}

	public int getBlastLimit() 
	{
		return nBlastLimit;
	}

	public void setBlastLimit(int nBlastLimit) 
	{
		this.nBlastLimit = nBlastLimit;

		saveConfiguation();
	}

	public int getBlastTriggerLimit() 
	{
		return nBlastTriggerLimit;
	}

	public void setBlastTriggerLimit(int nBlastTriggerLimit) 
	{
		this.nBlastTriggerLimit = nBlastTriggerLimit;

		saveConfiguation();
	}

	public float getBlastYield() 
	{
		return fBlastYield;
	}

	public void setBlastYield(float fBlastYield) 
	{
		this.fBlastYield = fBlastYield;

		saveConfiguation();
	}

	public int getBlastRadius() 
	{
		return nBlastRadius;
	}

	public void setBlastRadius(int nBlastRadius) 
	{
		this.nBlastRadius = nBlastRadius;
		
		saveConfiguation();
	}

	public void setPermissionsEnabled(boolean bIsPermissionsEnabled) 
	{
		this.bIsPermissionsEnabled = bIsPermissionsEnabled;
	}

	public boolean isPermissionsEnabled() 
	{
		return bIsPermissionsEnabled;
	}

	public void setBlastLinkLimit(int nBlastLinkLimit) 
	{
		this.nBlastLinkLimit = nBlastLinkLimit;
	}

	public int getBlastLinkLimit() 
	{
		return nBlastLinkLimit;
	}
}
