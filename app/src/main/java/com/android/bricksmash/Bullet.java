package com.android.bricksmash;

import android.content.Context;
import android.content.res.Resources;

public class Bullet extends MovableObj
{
    // Game context
    private Context m_oContext;

    // Resources object
    private Resources m_oResources;

    // Pack dimensions
    private int m_nWidth;
    private int m_nHeight;

    // Movement properties
    private int m_nMovingY;	    // The step in Y axis (bullet only goes up)

    private boolean m_bIsUsed;

    public Bullet(Context oContext, int nStartX, int nStartY)
    {
        m_oContext		= oContext;
        m_oResources	= m_oContext.getResources();

        m_nCoordX   = nStartX;
        m_nCoordY   = nStartY;
        m_nHeight   = getDefaultH();
        m_nWidth    = getDefaultW();
        m_nMovingY  = 2;
        m_bIsUsed   = false;
    }

    @Override
    public synchronized void move()
    {
        if (!GameLogics.Instance().isGamePaused())
        {
            m_nCoordY -= m_nMovingY;
        }

        // Notify game logic manager about movement
        GameLogics.Instance().onBulletMovement(this);
    }

    @Override
    public synchronized void restart()
    {
        // During game restart all bullets are deleted. Nothing to do here
    }

    public int getDefaultW()			    { return m_oResources.getDimensionPixelSize(R.dimen.Bullet_W);  }
    public int getDefaultH()			    { return m_oResources.getDimensionPixelSize(R.dimen.Bullet_H);  }

    public synchronized int getW() 			{ return m_nWidth;  			                                }
    public synchronized int getH() 			{ return m_nHeight; 			                                }
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
