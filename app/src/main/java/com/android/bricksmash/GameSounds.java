package com.android.bricksmash;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class GameSounds 
{
	public final static int	BASE_HIT 		= 1;
	public final static int	BRICK_HIT		= 2;
	public final static int	GRND_HIT		= 3;
	public final static int	WALL_HIT		= 4;
	public final static int SPARK_HIT 		= 5;
	public final static int EXPLODE_BALL	= 6;
	public final static int EXPLODE_BRICK	= 7;
	public final static int EXTRA_LIFE		= 8;
	public final static int KILL_BASE		= 9;
	public final static int POWERUP			= 10;
	public final static int GOOD_POWERUP	= 11;
	public final static int BAD_POWERUP		= 12;
	public final static int NEXT_LEVEL		= 13;
	public final static int SHOOTING_BULLET = 14;
	public final static int EXPAND_BASE		= 15;
	public final static int SHRINK_BASE		= 16;
	public final static int METAL_BRICK		= 17;

	private  SoundPool m_oSoundPool;
	private  HashMap<Integer, Integer> m_oHashMap;
	private static GameSounds m_sInstance = null; 
	
	private GameSounds() 
	{
		m_oSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		m_oHashMap = new HashMap<>();
	}

	public static synchronized GameSounds Instance()
	{
		if (m_sInstance == null)
			m_sInstance = new GameSounds();
		return m_sInstance;
	}
	
	public void addSound(int index, int soundID, Context context) 
	{
		int soundPoolID = m_oSoundPool.load(context, soundID, 1);
		m_oHashMap.put(index, soundPoolID);
	}

	public void play(int index, boolean loop) 
	{
		if (!loop)
			m_oSoundPool.play(m_oHashMap.get(index), 1, 1, 1, 0, 1f);
		else
			m_oSoundPool.play(m_oHashMap.get(index), 1, 1, 1, -1, 1f);
	}

	public void stop(int index) 
	{
		m_oSoundPool.stop(m_oHashMap.get(index));
	}

	public void release() 
	{
		m_oSoundPool.release();
	}
}