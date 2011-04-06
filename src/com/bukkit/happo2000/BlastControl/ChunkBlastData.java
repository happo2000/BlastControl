package com.bukkit.happo2000.BlastControl;

import java.util.Calendar;
import java.util.HashMap;

public class ChunkBlastData 
{
	private final	HashMap<Integer,Integer>		tntLimitList 	= new HashMap<Integer,Integer>();
	private final	HashMap<Integer,ChunkMetadata>	chunkList 		= new HashMap<Integer,ChunkMetadata>();
	private final 	BlastControl 					plugin;
	
    public ChunkBlastData(BlastControl instance) 
    {
        plugin 		= instance;
	}

	public ChunkMetadata getChunkData(int posX, int posZ)
	{
		long 			nTime 		= Calendar.getInstance().getTimeInMillis() - plugin.getBlastConfiguration().getBlastTriggerLimit();
		ChunkMetadata	chunkData	= chunkList.get(posX & 0xFFFF | posZ << 16);

		if (chunkData == null || !chunkData.isValid(nTime))
		{
			chunkData = findNearestValidChunk(posX, posZ, nTime);
			
			if (chunkData == null || !chunkData.isValid(nTime))
				chunkData = new ChunkMetadata();
		}

		return chunkData;
	}
	
	private ChunkMetadata findNearestValidChunk(int posX, int posZ, long nTime)
	{
		ChunkMetadata	nearChunk	= null;
		int				minX 		= posX - 2;
		int				minZ 		= posZ - 2;
		
		for (int curX = posX + 1; curX > minX; --curX)
		{
			for (int curZ = posZ + 1; curZ > minZ; --curZ)
			{
				ChunkMetadata 	metaChunk 	= chunkList.get(curX & 0xFFFF | curZ << 16);

				if (metaChunk != null && (nearChunk == null || nearChunk.getLastUpdate() < metaChunk.getLastUpdate()))
					nearChunk = metaChunk;
			}
		}
		
		return nearChunk;
	}
	
	public void setChunkData(int posX, int posZ, ChunkMetadata chunkData)
	{
		chunkData.touchUpdate();
		
		chunkList.put(posX & 0xFFFF | posZ << 16, chunkData);
	}
	
	public void removeChunkInfo(int posX, int posZ)
	{
		chunkList.remove(posX & 0xFFFF | posZ << 16);
	}
	
	public void setLinkLimit(int nPlayerId, int nTNTLinkCount)
	{
		tntLimitList.put(nPlayerId, nTNTLinkCount);
	}

	public int getLinkLimit(int nPlayerId)
	{
		Integer value = tntLimitList.get(nPlayerId);
		
		if (value == null)
			return 0;
		else
			return value.intValue();
	}

	public void removeLinkLimit(int nPlayerId)
	{
		tntLimitList.remove(nPlayerId);
	}
}
