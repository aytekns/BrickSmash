package com.android.bricksmash;

import android.content.Context;
import android.content.res.Resources;


public class Brick extends MovableObj 
{
	// Game context
	private Context m_oContext;
	
	// Resources object
	private Resources m_oResources;
	
	// Brick dimensions
	private int m_nWidth;
	private int m_nHeight;
	
	// Special Brick properties
	private int m_nStrength;
	private int m_nInitStrength;
	private int m_nContent;
	private int m_nColor;
	private boolean m_bVisible;
	private boolean m_bExplosive;
	private boolean m_bNeedsDrawing;
	
	// Moving Boundaries
	private int m_nMinHeight;

	public Brick(Context oContext, int nStartX, int nStartY, int nMinH, int nColor, int nStrength, int nContent, boolean bExplosive)
	{
		m_oContext		= oContext;
		m_oResources	= m_oContext.getResources();

		m_nCoordX 		= nStartX;
		m_nCoordY 		= nStartY;
		m_nWidth		= getDefaultW();
		m_nHeight		= getDefaultH();
		m_nMinHeight	= nMinH;
		
		m_nStrength 	= nStrength;
		m_nInitStrength = nStrength;
		m_nContent 		= nContent;
		m_nColor 		= nColor;
		m_bExplosive	= bExplosive;

		if (m_nStrength == 0)
			m_bVisible = false;
		else
			m_bVisible = true;		
		m_bNeedsDrawing = true;
	}
	
	public void setBrickProperties(int nColor, int nStrength, int nContent, boolean bExplosive)
	{
		m_nStrength 	= nStrength;
		m_nInitStrength	= nStrength;
		m_nColor		= nColor;
		m_nContent		= nContent;
		m_bExplosive	= bExplosive;

		if (m_nStrength == 0)
			m_bVisible = false;
		else
			m_bVisible = true;
		m_bNeedsDrawing = true;
	}
	
	@Override
	public synchronized void move() 
	{
		// Currently not implemented for bricks.
		// TODO: Implement move for 'falling bricks' pack

		// Do not allow going below minimum height
		//if (m_nCoordY >= m_nMinHeight)
		//	return;
		
	}

	@Override
	public synchronized void restart() 
	{
		// Default values
		m_nStrength 	= m_nInitStrength;
		m_bVisible 		= true;		
		m_bNeedsDrawing = true;
	}
	

	// Returns 1 if brick is not solid and just now became invisible, zero otherwise
    // Solid bricks are not counted and this is why I ignore them
	public synchronized int ballHit(boolean bIsFireBall, boolean bIsThroughBrick)
	{
		if (m_nStrength == 0 || !m_bVisible)	// Brick is invisible
			return 0;

        int nOldStrength = m_nStrength;
		if (bIsFireBall || bIsThroughBrick)		// Fire ball or Through bricks ball. Kills every brick
			m_nStrength = 0;
		else if (m_nStrength > 0)				// Normal brick
			m_nStrength--;
												// Otherwise this is solid brick. Do nothing to strength
		if (m_nStrength == 0)
		{
			m_bVisible = false;
			return (nOldStrength > 0) ? 1 : 0;
		}
		return 0;
	}
			
	public int getDefaultW()							{ return m_oResources.getDimensionPixelSize(R.dimen.Brick_W);   }
	public int getDefaultH()							{ return m_oResources.getDimensionPixelSize(R.dimen.Brick_H);   }

	public synchronized int getW() 						{ return m_nWidth;  			}
	public synchronized int getH() 						{ return m_nHeight; 			}
	
	public synchronized int getStrength() 				{ return m_nStrength; 			}
	public synchronized int getContent() 				{ return m_nContent; 			}
	public synchronized int getColor()					{ return m_nColor;				}
	public synchronized boolean isExplosive()			{ return m_bExplosive;			}
	public synchronized boolean isVisible() 			{ return m_bVisible; 			}
	public synchronized boolean needsDrawing() 			{ return m_bNeedsDrawing;		}

	public synchronized void setStrength(int nStrength) { m_nStrength = nStrength;		}
    public synchronized void setBoundaries(int nMinH)   { m_nMinHeight = nMinH;         }
	public synchronized void setVisible(boolean bVal)	{ m_bVisible = bVal;			}
	public synchronized void needsDrawing(boolean bVal) { m_bNeedsDrawing = bVal; 		}
	
	
	@Override
	public synchronized int getLeft()					{ return m_nCoordX; 			}
	@Override
	public synchronized int getRight()					{ return m_nCoordX + m_nWidth; 	}
	@Override
	public synchronized int getTop()					{ return m_nCoordY; 			}
	@Override
	public synchronized int getBottom()					{ return m_nCoordY + m_nHeight; }
}
