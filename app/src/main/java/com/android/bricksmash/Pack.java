package com.android.bricksmash;

import android.content.Context;
import android.content.res.Resources;

public class Pack extends MovableObj
{
    // Game context
    private Context m_oContext;

    // Resources object
    private Resources m_oResources;

    // Pack dimensions
    private int m_nWidth;
    private int m_nHeight;

    // Movement properties
    private int m_nMovingY;	    // The step in Y axis
    private int m_nMovingX; 	// The step in X axis
    private int m_nCounter;     // For simulating slingshot movement

    // Moving Boundaries
    private int m_nMaxWidth;

    // Pack content
    private int m_nContent;
    private boolean m_bIsUsed;

    // Content types:
    public final static int PACK_CONT_EXTRA_LIFE            = 0;
    public final static int PACK_CONT_SET_OFF_EXPLODE       = 1;
    public final static int PACK_CONT_SHOOTING_BASE         = 2;
    public final static int PACK_CONT_GRAB_BASE             = 3;
    public final static int PACK_CONT_SLOW_BALL             = 4;
    public final static int PACK_CONT_FIRE_BALL             = 5;
    public final static int PACK_CONT_WRAP_LEVEL            = 6;
    public final static int PACK_CONT_ZAP_BRICKS            = 7;
    public final static int PACK_CONT_MULTIPLY_EXPLODE      = 8;
    public final static int PACK_CONT_THROUGH_BRICK         = 9;

    public final static int PACK_CONT_EXPAND_BASE           = 10;
    public final static int PACK_CONT_SHRINK_BASE           = 11;
    public final static int PACK_CONT_MEGA_BALL             = 12;
    public final static int PACK_CONT_SPLIT_BALL            = 13;
    public final static int PACK_CONT_EIGHT_BALL            = 14;

    public final static int PACK_CONT_KILL_BASE             = 15;
    public final static int PACK_CONT_FAST_BALL             = 16;
    public final static int PACK_CONT_MINI_BASE             = 17;
    public final static int PACK_CONT_MINI_BALL             = 18;
    public final static int PACK_CONT_FALLING_BRICKS        = 19;

    public final static int	NUM_OF_PACK_CONTENT_TYPES       = 20;

    public Pack(Context oContext, int nStartX, int nStartY, int nContent, int nDirection, int nMaxW)
    {
        m_oContext		= oContext;
        m_oResources	= m_oContext.getResources();

        m_nMaxWidth     = nMaxW;
        m_nCoordX 		= nStartX;
        m_nCoordY 		= nStartY;
        m_nHeight       = getDefaultH();
        m_nWidth        = getDefaultW();
        m_nMovingX      = 5 * nDirection;  // TBD
        m_nMovingY      = -3;              // TBD
        m_nContent      = nContent;
        m_nCounter      = 20;
        m_bIsUsed       = false;
    }

    @Override
    public synchronized void move()
    {
        if (!GameLogics.Instance().isGamePaused())
        {
            if (m_nCounter > 0 && --m_nCounter == 0)
            {
                m_nMovingX = 0;
                m_nMovingY = 3;
            }

            m_nCoordX += m_nMovingX;
            if (m_nCounter <= 0 && (m_nCoordX < m_nWidth || m_nCoordX > m_nMaxWidth-m_nWidth))
            {
                m_nMovingX = 0;
            }
            m_nCoordY += m_nMovingY;
        }

        // Notify game logic manager about movement
        GameLogics.Instance().onPackMovement(this);
    }

    @Override
    public synchronized void restart()
    {
        // During game restart all packs are deleted. Nothing to do here
    }

    public int getDefaultW()			    { return m_oResources.getDimensionPixelSize(R.dimen.Pack_W);    }
    public int getDefaultH()			    { return m_oResources.getDimensionPixelSize(R.dimen.Pack_H);    }

    public synchronized int getW() 			{ return m_nWidth;  			                                }
    public synchronized int getH() 			{ return m_nHeight; 			                                }
    public synchronized int getContent()    { return m_nContent;                                            }
    public synchronized boolean isUsed()    { return m_bIsUsed;                                             }

    public synchronized void setUsed(boolean bUsed) { m_bIsUsed = bUsed;                                    }

    @Override
    public synchronized int getLeft()       { return m_nCoordX; 		                                    }
    @Override
    public synchronized int getRight()      { return m_nCoordX + m_nWidth;                                  }
    @Override
    public synchronized int getTop()        { return m_nCoordY; 		                                    }
    @Override
    public synchronized int getBottom()     { return m_nCoordY + m_nHeight;                                 }
}
