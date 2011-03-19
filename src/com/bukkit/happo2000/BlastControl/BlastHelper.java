package com.bukkit.happo2000.BlastControl;

import java.util.HashMap;

public class BlastHelper 
{
    public static String getDisplayFriendlyName(EnumCreeperSetting creeperSetting)
    {
		switch (creeperSetting)
		{
			case DISABLED_WITH_DESPAWN  : return "Disabled (Despawn)";
			case DISABLED_WITH_FANGS	: return "Disabled (Fangs)";
			case DISABLED				: return "Disabled";
			case LIMITED_WITH_DESPAWN   : return "Limited (Despawn)";
			default						:
			case LIMITED_WITH_FANGS		: return "Limited (Fangs)";
			case LIMITED				: return "Limited";
			case ENABLED				: return "Enabled";
		}
    }
    
    public static String getConfigFriendlyName(EnumCreeperSetting creeperSetting)
    {
		switch (creeperSetting)
		{
			case DISABLED_WITH_DESPAWN  : return "disable-despawn";
			case DISABLED_WITH_FANGS	: return "disable-fangs";
			case DISABLED				: return "disable";
			case LIMITED_WITH_DESPAWN   : return "limit-depawn";
			default						:
			case LIMITED_WITH_FANGS		: return "limit-fangs";
			case LIMITED				: return "limit";
			case ENABLED				: return "enable";
		}
    }
    
    public static String getEnumFriendlyName(EnumCreeperSetting creeperSetting)
    {
    	return creeperSetting.toString();
    }
    
    public static EnumCreeperSetting parseCreeperSetting(String sCreeperSetting)
    {
    	HashMap<String, EnumCreeperSetting> parseMap 			= new HashMap<String, EnumCreeperSetting>();
    	EnumCreeperSetting 					creeperSetting 		= EnumCreeperSetting.DISABLED;
    	String								sCreeperSettingLow  = sCreeperSetting.toLowerCase();
    	
    	parseMap.put(getConfigFriendlyName(EnumCreeperSetting.ENABLED).toLowerCase()				, EnumCreeperSetting.ENABLED);
    	parseMap.put(getConfigFriendlyName(EnumCreeperSetting.LIMITED).toLowerCase()				, EnumCreeperSetting.LIMITED);    	
    	parseMap.put(getConfigFriendlyName(EnumCreeperSetting.LIMITED_WITH_FANGS).toLowerCase()		, EnumCreeperSetting.LIMITED_WITH_FANGS);    	
    	parseMap.put(getConfigFriendlyName(EnumCreeperSetting.LIMITED_WITH_DESPAWN).toLowerCase()	, EnumCreeperSetting.LIMITED_WITH_DESPAWN);    	
    	parseMap.put(getConfigFriendlyName(EnumCreeperSetting.DISABLED).toLowerCase()				, EnumCreeperSetting.DISABLED);    	
    	parseMap.put(getConfigFriendlyName(EnumCreeperSetting.DISABLED_WITH_FANGS).toLowerCase()	, EnumCreeperSetting.DISABLED_WITH_FANGS);    	
    	parseMap.put(getConfigFriendlyName(EnumCreeperSetting.DISABLED_WITH_DESPAWN).toLowerCase()	, EnumCreeperSetting.DISABLED_WITH_DESPAWN);
    	
    	parseMap.put(getDisplayFriendlyName(EnumCreeperSetting.ENABLED).toLowerCase()				, EnumCreeperSetting.ENABLED);
    	parseMap.put(getDisplayFriendlyName(EnumCreeperSetting.LIMITED).toLowerCase()				, EnumCreeperSetting.LIMITED);    	
    	parseMap.put(getDisplayFriendlyName(EnumCreeperSetting.LIMITED_WITH_FANGS).toLowerCase()	, EnumCreeperSetting.LIMITED_WITH_FANGS);    	
    	parseMap.put(getDisplayFriendlyName(EnumCreeperSetting.LIMITED_WITH_DESPAWN).toLowerCase()	, EnumCreeperSetting.LIMITED_WITH_DESPAWN);    	
    	parseMap.put(getDisplayFriendlyName(EnumCreeperSetting.DISABLED).toLowerCase()				, EnumCreeperSetting.DISABLED);    	
    	parseMap.put(getDisplayFriendlyName(EnumCreeperSetting.DISABLED_WITH_FANGS).toLowerCase()	, EnumCreeperSetting.DISABLED_WITH_FANGS);    	
    	parseMap.put(getDisplayFriendlyName(EnumCreeperSetting.DISABLED_WITH_DESPAWN).toLowerCase()	, EnumCreeperSetting.DISABLED_WITH_DESPAWN);
    	
    	parseMap.put(getEnumFriendlyName(EnumCreeperSetting.ENABLED).toLowerCase()					, EnumCreeperSetting.ENABLED);
    	parseMap.put(getEnumFriendlyName(EnumCreeperSetting.LIMITED).toLowerCase()					, EnumCreeperSetting.LIMITED);    	
    	parseMap.put(getEnumFriendlyName(EnumCreeperSetting.LIMITED_WITH_FANGS).toLowerCase()		, EnumCreeperSetting.LIMITED_WITH_FANGS);    	
    	parseMap.put(getEnumFriendlyName(EnumCreeperSetting.LIMITED_WITH_DESPAWN).toLowerCase()		, EnumCreeperSetting.LIMITED_WITH_DESPAWN);    	
    	parseMap.put(getEnumFriendlyName(EnumCreeperSetting.DISABLED).toLowerCase()					, EnumCreeperSetting.DISABLED);    	
    	parseMap.put(getEnumFriendlyName(EnumCreeperSetting.DISABLED_WITH_FANGS).toLowerCase()		, EnumCreeperSetting.DISABLED_WITH_FANGS);    	
    	parseMap.put(getEnumFriendlyName(EnumCreeperSetting.DISABLED_WITH_DESPAWN).toLowerCase()	, EnumCreeperSetting.DISABLED_WITH_DESPAWN);

    	if (parseMap.containsKey(sCreeperSettingLow))
    		creeperSetting = parseMap.get(sCreeperSettingLow);
    	
    	parseMap.clear();
    		
    	return creeperSetting;
    }
    
    
}
