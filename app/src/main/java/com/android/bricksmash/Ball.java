package com.android.bricksmash;

import android.content.Context;
import android.content.res.Resources;

public class Ball extends MovableObj {
	// Game context
	private Context m_oContext;

	// Resources object
	private Resources m_oResources;

	// Ball adds radius to properties of movable object
	private int m_nRadius;

	// Ball moving direction and angle
	private boolean m_bMovingRight;
	private boolean m_bMovingDown;
	private int m_nMovingAngleY;    // The step in Y axis
	private int m_nMovingAngleX;    // The step in X axis

	private boolean m_bIsActive;

	public Ball(Context oContext)
	{
		m_oContext = oContext;
		m_oResources = m_oContext.getResources();

		m_nCoordX = getRestartX();
		m_nCoordY = getRestartY();
		m_nRadius = getDefaultR();

		m_nMovingAngleY = getAvgAngle();
		m_nMovingAngleX = getAvgAngle();
		m_bMovingRight = true;
		m_bMovingDown = false;
		m_bIsActive = true;
	}

	public Ball(Context oContext, int nStartX, int nStartY, int nMoveAngleX, int nMoveAngleY, boolean bMovingRight, boolean bMovingDown)
	{
		m_oContext = oContext;
		m_oResources = m_oContext.getResources();

		m_nCoordX = nStartX;
		m_nCoordY = nStartY;
		m_nRadius = getDefaultR();

		m_nMovingAngleY = nMoveAngleY;
		m_nMovingAngleX = nMoveAngleX;
		m_bMovingRight = bMovingRight;
		m_bMovingDown = bMovingDown;
        m_bIsActive = true;
	}
	
	@Override
	public void restart()
	{
		m_nCoordX = getRestartX();
		m_nCoordY = getRestartY();
		m_nMovingAngleY = getAvgAngle();
		m_nMovingAngleX	= getAvgAngle();
		m_bMovingRight = true;
		m_bMovingDown = false;
	}
	
	@Override
	public void move()
	{
		if (!GameLogics.Instance().isGamePaused() && GameLogics.Instance().isMoveAllowed())
		{
			m_nCoordX = m_bMovingRight ? m_nCoordX+m_nMovingAngleX : m_nCoordX-m_nMovingAngleX;
			m_nCoordY = m_bMovingDown  ? m_nCoordY+m_nMovingAngleY : m_nCoordY-m_nMovingAngleY;
		}
		
		// Notify game logic manager about movement
		GameLogics.Instance().onBallMovement(this);
	}
	
	public int getDefaultR()				{ return m_oResources.getDimensionPixelSize(R.dimen.Ball_R);   }
	public int getRestartX()				{ return m_oResources.getDimensionPixelOffset(R.dimen.Ball_X); }
	public int getRestartY()
	{
		if (GameLogics.Instance().deviceUsesSWKeys(m_oContext))
			return m_oResources.getDimensionPixelOffset(R.dimen.Ball_Y);
		else
			return m_oResources.getDimensionPixelOffset(R.dimen.Ball_Y) +
				   m_oResources.getDimensionPixelOffset(R.dimen.sw_keys_height);
	}
	public int getAvgAngle()				{ return m_oResources.getInteger(R.integer.Ball_Avg_Angle);    }
	public int getLowAngle()				{ return m_oResources.getInteger(R.integer.Ball_Low_Angle);    }
	public int getHighAngle()				{ return m_oResources.getInteger(R.integer.Ball_High_Angle);   }


	public void setActive(boolean bActive)	{ m_bIsActive = bActive;			}
	public void setHorPosition(int nPos) 	{ m_nCoordX = nPos;					}
	public void setMovingAngleY(int nSpeed)	{ m_nMovingAngleY = nSpeed; 		}
	public void setMovingAngleX(int nAngle)	{ m_nMovingAngleX = nAngle; 		}
	public void changeHorDirection()
	{
		m_bMovingRight = !m_bMovingRight;
		m_nCoordX = m_bMovingRight ? m_nCoordX+m_nMovingAngleX : m_nCoordX-m_nMovingAngleX;
	}
	public void changeVerDirection()
	{
		m_bMovingDown = !m_bMovingDown;
		m_nCoordY = m_bMovingDown  ? m_nCoordY+m_nMovingAngleY : m_nCoordY-m_nMovingAngleY;
	}
	public void bounceUp()			 		{ m_bMovingDown = false; 			}
	public void bounceDown()				{ m_bMovingDown = true;				}
	public void bounceLeft()				{ m_bMovingRight = false;			}
	public void bounceRight()				{ m_bMovingRight = true;			}

	public boolean isActive()				{ return m_bIsActive;				}
	public boolean isMovingRight()	 		{ return m_bMovingRight; 			}
	public boolean isMovingDown()	 		{ return m_bMovingDown; 			}
	public int getMovingAngleX()			{ return m_nMovingAngleX;			}
	public int getMovingAngleY()			{ return m_nMovingAngleY;			}
	
	public int  getR() 						{ return m_nRadius; 				}
	public void setR(int nRadius)			{ m_nRadius = nRadius;				}

	@Override
	public int getLeft()					{ return m_nCoordX - m_nRadius; 	}
	@Override
	public int getRight()					{ return m_nCoordX + m_nRadius; 	}
	@Override
	public int getTop()						{ return m_nCoordY - m_nRadius; 	}
	@Override
	public int getBottom()					{ return m_nCoordY + m_nRadius; 	}
}
