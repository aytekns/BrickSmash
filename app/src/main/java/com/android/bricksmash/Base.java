package com.android.bricksmash;

import android.content.Context;
import android.content.res.Resources;


public class Base extends MovableObj
{
	// Game context
	private Context m_oContext;
	
	// Resources object
	private Resources m_oResources;
	
	// Base adds dimensions to properties of movable object
	private int m_nWidth;
	private int m_nHeight;
	
	// Base destination
	private int m_nDest;
	
	// Moving Boundaries
	private int m_nMaxWidth;

    // Base Type
    private int m_nBaseType;

	// Base types:
	public final static int BASE_NORMAL            	= 0;
	public final static int BASE_NORMAL_S 			= 1;
	public final static int BASE_MINI				= 2;
	public final static int BASE_MINI_S				= 3;
	public final static int BASE_MEGA				= 4;
	public final static int BASE_MEGA_S				= 5;

    public final static int NUM_OF_BASE_TYPES       = 6;

	public Base(Context oContext, int nMaxW)
	{
		m_oContext		= oContext;
		m_oResources	= m_oContext.getResources();

        m_nBaseType = BASE_NORMAL;
		m_nCoordX 	= getRestartX();
		m_nCoordY 	= getRestartY();
		m_nWidth	= getDefaultW();
		m_nHeight	= getDefaultH();
		m_nMaxWidth	= nMaxW;
		
		// X & Y are the upper left corner of the base. Destination is the middle of
		// the base in the X axis
		m_nDest = m_nCoordX + getDefaultW()/2;
	}
	
	@Override
	public synchronized void restart()
	{
        m_nBaseType = BASE_NORMAL;
        m_nCoordX 	= getRestartX();
        m_nCoordY 	= getRestartY();
        m_nWidth	= getDefaultW();
        m_nHeight	= getDefaultH();

		// X & Y are the upper left corner of the base. Destination is the middle of
		// the base in the X axis
		m_nDest = m_nCoordX + getDefaultW()/2;
	}
	
	public synchronized void setBoundaries(int nMaxW)
	{
		m_nMaxWidth = nMaxW;
	}
	
	public synchronized void setDestination(int nDest)
	{
		m_nDest = nDest;
	}
	
	@Override
	public synchronized void move()
	{
		if (!GameLogics.Instance().isGamePaused())
		{
			// Check to see if base reached destination. If not move a step toward it
			int nDestX = m_nDest - m_nWidth/2;
			
			// Check new destination with boundaries
			if (nDestX < 0)
				nDestX = 0;
			else if (nDestX + m_nWidth > m_nMaxWidth)
				nDestX = m_nMaxWidth - m_nWidth;
			
			// if destination X is the current position of the base, no need to move
			if (nDestX == m_nCoordX)
				return;
			
			// OK, need to move.
			m_nCoordX = nDestX < m_nCoordX ? m_nCoordX-getSpeed() : m_nCoordX+getSpeed();
		}
		
		// Notify game logics about base movement
		GameLogics.Instance().onBaseMovement();
	}

	public synchronized void setBaseType(int nBaseType)
	{
		switch (nBaseType)
		{
			case BASE_NORMAL:
			case BASE_NORMAL_S:
				m_nWidth = getDefaultW();
                if (m_nBaseType == BASE_MINI || m_nBaseType == BASE_MINI_S)
                    m_nCoordX -= (getDefaultW() / 6);
                else if (m_nBaseType == BASE_MEGA || m_nBaseType == BASE_MEGA_S)
                    m_nCoordX += (getDefaultW() * 5 / 18);
				break;

            case BASE_MINI:
			case BASE_MINI_S:
                m_nWidth = getDefaultW() * 2 / 3;
                if (m_nBaseType == BASE_NORMAL || m_nBaseType == BASE_NORMAL_S)
                    m_nCoordX += (getDefaultW() / 6);
                else if (m_nBaseType == BASE_MEGA || m_nBaseType == BASE_MEGA_S)
                    m_nCoordX += (getDefaultW() * 4 / 9);
                break;

            case BASE_MEGA:
			case BASE_MEGA_S:
                m_nWidth = getDefaultW() * 14 / 9;
                if (m_nBaseType == BASE_NORMAL || m_nBaseType == BASE_NORMAL_S)
                    m_nCoordX -= (getDefaultW() * 5 / 18);
                break;
		}
        if (m_nCoordX < 0)
            m_nCoordX = 0;
        if (getRight() > m_nMaxWidth)
            m_nCoordX = m_nMaxWidth - m_nWidth;
        m_nBaseType = nBaseType;
	}

    public int getBaseType()            { return m_nBaseType;                                           }
	public int getDefaultW()			{ return m_oResources.getDimensionPixelSize(R.dimen.Base_W);   	}
	public int getDefaultH()			{ return m_oResources.getDimensionPixelSize(R.dimen.Base_H);   	}
	public int getRestartX()			{ return m_oResources.getDimensionPixelOffset(R.dimen.Base_X); 	}
	public int getRestartY()
	{
		if (GameLogics.Instance().deviceUsesSWKeys(m_oContext))
			return m_oResources.getDimensionPixelOffset(R.dimen.Base_Y);
		else
			return m_oResources.getDimensionPixelOffset(R.dimen.Base_Y) +
				   m_oResources.getDimensionPixelOffset(R.dimen.sw_keys_height);
	}
	public int getSpeed()				{ return m_oResources.getInteger(R.integer.Base_Speed);			}

	public synchronized int getW() 		{ return m_nWidth;  			                                }
	public synchronized int getH() 		{ return m_nHeight; 			                                }


	@Override
	public synchronized int getLeft()	{ return m_nCoordX; 			                                }
	@Override
	public synchronized int getRight()	{ return m_nCoordX + m_nWidth; 	                                }
	@Override
	public synchronized int getTop()	{ return m_nCoordY; 			                                }
	@Override
	public synchronized int getBottom()	{ return m_nCoordY + m_nHeight;                                 }
}
