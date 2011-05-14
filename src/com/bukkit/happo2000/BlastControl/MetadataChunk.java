package com.bukkit.happo2000.BlastControl;

import java.util.Calendar;

public class MetadataChunk 
{
	private EnumBlastLimit 	blastLimit;
	private long			lastUpdate;
	
    public MetadataChunk(EnumBlastLimit blastLimit)
    {
    	setBlastStatus(blastLimit);
    }

    public MetadataChunk()
    {
    	this(EnumBlastLimit.DISABLED);
    }

	public void setBlastStatus(EnumBlastLimit blastLimit) 
	{
		this.lastUpdate 	= Calendar.getInstance().getTimeInMillis(); 
		this.blastLimit 	= blastLimit;
	}

	public EnumBlastLimit getValidBlastStatus(long nTime) 
	{
		if (lastUpdate > nTime)
			return blastLimit;
		else
			return EnumBlastLimit.DISABLED;
	}

	public EnumBlastLimit getBlastStatus() 
	{
		return blastLimit;
	}
	
	public long getLastUpdate()
	{
		return lastUpdate;
	}
}
