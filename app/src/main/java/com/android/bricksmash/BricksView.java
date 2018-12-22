package com.android.bricksmash;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

public class BricksView extends View 
{
	private int 	m_nBricksCount;
	private Brick 	m_oBrick[];
	private Bitmap	m_oBrickBitmap[];
	private boolean m_bFirstTime;
	
	public final static int	BRICK_BLUE 		= 0;
	public final static int	BRICK_GREEN 	= 1;
	public final static int	BRICK_MAGENTA	= 2;
	public final static int	BRICK_ORANGE	= 3;
	public final static int	BRICK_PURPLE	= 4;
	public final static int	BRICK_RED		= 5;
	public final static int	BRICK_TURQUIES	= 6;
	public final static int BRICK_BLUPURPLE	= 7;
	public final static int	BRICK_YELLOW	= 8;
	public final static int	BRICK_BLUGREEN	= 9;
	public final static int BRICK_SOLID		= 10;
		
	public final static int	NUM_OF_BRICK_COLORS = 11;
	
	// For custom view, needed by infrastructure
	public BricksView(Context context)
	{
		super(context);
		
		// Create default line of 5 bricks
		int i, nXPos;
		m_bFirstTime = true;
		m_nBricksCount = 5;
		m_oBrick = new Brick[m_nBricksCount];
		for (i=0, nXPos=45; i<m_nBricksCount; nXPos = m_oBrick[i++].getRight()+10)
			m_oBrick[i] = new Brick(context, nXPos, 60, 600, BRICK_BLUE, 1, -1, false);
		
		// Create Brick Bitmaps
		createBrickBitmaps();
	}
	
	public BricksView(Context context, Brick game_bricks[]) 
	{
		super(context);

		m_bFirstTime = true;
		m_nBricksCount = game_bricks.length;
		m_oBrick = new Brick[m_nBricksCount];
		System.arraycopy(game_bricks, 0, m_oBrick, 0, game_bricks.length);

		// Create Brick Bitmaps
		createBrickBitmaps();
	}
	
	private void createBrickBitmaps()
	{
		m_oBrickBitmap 					= new Bitmap[NUM_OF_BRICK_COLORS];
		m_oBrickBitmap[BRICK_BLUE] 		= BitmapFactory.decodeResource(getResources(), R.drawable.brick1_blu);
		m_oBrickBitmap[BRICK_GREEN] 	= BitmapFactory.decodeResource(getResources(), R.drawable.brick1_gre);
		m_oBrickBitmap[BRICK_MAGENTA] 	= BitmapFactory.decodeResource(getResources(), R.drawable.brick1_mag);
		m_oBrickBitmap[BRICK_ORANGE] 	= BitmapFactory.decodeResource(getResources(), R.drawable.brick1_ora);
		m_oBrickBitmap[BRICK_PURPLE] 	= BitmapFactory.decodeResource(getResources(), R.drawable.brick1_pur);
		m_oBrickBitmap[BRICK_RED] 		= BitmapFactory.decodeResource(getResources(), R.drawable.brick1_red);
		m_oBrickBitmap[BRICK_TURQUIES] 	= BitmapFactory.decodeResource(getResources(), R.drawable.brick1_tur);
		m_oBrickBitmap[BRICK_BLUPURPLE] = BitmapFactory.decodeResource(getResources(), R.drawable.brick1_bpu);
		m_oBrickBitmap[BRICK_YELLOW] 	= BitmapFactory.decodeResource(getResources(), R.drawable.brick1_yel);
		m_oBrickBitmap[BRICK_BLUGREEN] 	= BitmapFactory.decodeResource(getResources(), R.drawable.brick1_bgr);
		m_oBrickBitmap[BRICK_SOLID] 	= BitmapFactory.decodeResource(getResources(), R.drawable.brick1_solid);
	}
	
	// Give access to set base boundaries
	public void setBrickBoundaries(int nMinH)
	{
		for (int i=0; i<m_nBricksCount; i++)
			m_oBrick[i].setBoundaries(nMinH);
	}
	
	@Override
	public void onDraw(Canvas canvas) 
	{
		for (int i=0; i<m_nBricksCount; i++)
			if (m_oBrick[i].isVisible())
			{
				if (m_oBrick[i].getStrength() >= 0)
					canvas.drawBitmap(m_oBrickBitmap[m_oBrick[i].getColor()], m_oBrick[i].getX(), m_oBrick[i].getY(), null);
				else
					canvas.drawBitmap(m_oBrickBitmap[BRICK_SOLID], m_oBrick[i].getX(), m_oBrick[i].getY(), null);
			}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		if (m_bFirstTime)
		{
			int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

			// Base needs to know boundaries to set destination right
			setBrickBoundaries(parentHeight-100);
			m_bFirstTime = false;
		}
	}
}
