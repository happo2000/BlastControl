package com.bukkit.happo2000.BlastControl;

import java.util.Calendar;

public class ChunkMetadata 
{
	private int				nLimit			=	BlastLimit.DISABLED;
	private long			nLastUpdate		= 	0;
	private	int				nRadius			= 	0;
	private float			fYield			= 	0.0f;
	private boolean			bLinkLimited	=   false;
	private	int				nStartId		= 	0;
	
    public ChunkMetadata(int nBlastLimit, int nBlastRadius, float fBlastYield, boolean bLinkLimited, int nStartId)
    {
    	this.nLimit 		= nBlastLimit;
    	this.nRadius 		= nBlastRadius;
    	this.fYield 		= fBlastYield;
    	this.bLinkLimited 	= bLinkLimited;
    	this.nStartId 		= nStartId;
    }

    public ChunkMetadata()
    {
    	this(BlastLimit.DISABLED, 0, 0, false, 0);
    }

	public void setBlastStatus(int nBlastLimit) 
	{
		this.nLimit 	= nBlastLimit;
	}

	public int getBlastStatus() 
	{
		return nLimit;
	}
	
	public void touchUpdate()
	{
		this.nLastUpdate = Calendar.getInstance().getTimeInMillis(); 
	}
	
	public long getLastUpdate()
	{
		return nLastUpdate;
	}
	
	public boolean isValid(long nTime)
	{
		return nLastUpdate > nTime;
	}
	
	public float getBlastYield()
	{
		return fYield;
	}
	
	public void setBlastYield(float fBlastYield)
	{
		this.fYield = fBlastYield;
	}

	public int getBlastRadius()
	{
		return nRadius;
	}

	public void setBlastRadius(int nBlastRadius)
	{
		this.nRadius = nBlastRadius;
	}

	public void setLinkLimited(boolean bLinkLimited) 
	{
		this.bLinkLimited = bLinkLimited;
	}

	public boolean isLinkLimited() 
	{
		return bLinkLimited;
	}

	public void setStartedByPlayerId(int nStartId) 
	{
		this.nStartId = nStartId;
	}

	public int getStartedByPlayerId() 
	{
		return nStartId;
	}
}
