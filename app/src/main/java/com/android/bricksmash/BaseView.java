package com.android.bricksmash;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public class BaseView extends View 
{
	private Base 	m_oBase;
	private Bitmap	m_oBaseBitmap[];
	private boolean m_bFirstTime;

	// For custom view, needed by infrastructure
	public BaseView(Context context)
	{
		super(context);
		
		// Create a ball with default position and boundaries
		m_oBase = new Base(context,480);
		m_bFirstTime = true;

		// Create Base Bitmaps
		createBaseBitmaps();
	}
	
	// Will be used in this application
	public BaseView(Context context, Base new_base) 
	{
		super(context);
		
		m_oBase = new_base;
		m_bFirstTime = true;

		// Create Base Bitmaps
		createBaseBitmaps();
	}

	private void createBaseBitmaps()
	{
		m_oBaseBitmap 						= new Bitmap[Base.NUM_OF_BASE_TYPES];
		m_oBaseBitmap[Base.BASE_NORMAL] 	= BitmapFactory.decodeResource(getResources(), R.drawable.base_normal);
		m_oBaseBitmap[Base.BASE_NORMAL_S] 	= BitmapFactory.decodeResource(getResources(), R.drawable.base_normal_shooting);
		m_oBaseBitmap[Base.BASE_MINI] 		= BitmapFactory.decodeResource(getResources(), R.drawable.base_mini);
        m_oBaseBitmap[Base.BASE_MINI_S]		= BitmapFactory.decodeResource(getResources(), R.drawable.base_mini_shooting);
		m_oBaseBitmap[Base.BASE_MEGA] 		= BitmapFactory.decodeResource(getResources(), R.drawable.base_mega);
        m_oBaseBitmap[Base.BASE_MEGA_S] 	= BitmapFactory.decodeResource(getResources(), R.drawable.base_mega_shooting);
	}

	// Give access to set base boundaries
	public void setBaseBoundaries(int nMaxW)
	{
		m_oBase.setBoundaries(nMaxW);
	}
	
	// Give access to set base destination
	public void setBaseDestination(int nDest)
	{
		m_oBase.setDestination(nDest);
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		// Draw the base
		int nYPos = m_oBase.getY();
        int nXPos = m_oBase.getX();
        int nBaseType = m_oBase.getBaseType();
		if (nBaseType == Base.BASE_NORMAL_S || nBaseType == Base.BASE_MINI_S || nBaseType == Base.BASE_MEGA_S)
			nYPos -= (m_oBase.getH()* 1.75 - m_oBase.getH());

        canvas.drawBitmap(m_oBaseBitmap[m_oBase.getBaseType()], nXPos, nYPos, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		if (m_bFirstTime)
		{
			int parentWidth = MeasureSpec.getSize(widthMeasureSpec);

			// Base needs to know boundaries to set destination right
			setBaseBoundaries(parentWidth);
			m_bFirstTime = false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int nAction = event.getActionMasked();
		switch(nAction)
		{
			case MotionEvent.ACTION_DOWN:
				if (GameLogics.Instance().isMoveAllowed())
					setBaseDestination((int) event.getX());
				break;

			case MotionEvent.ACTION_MOVE:
				setBaseDestination((int)event.getX());
				break;

			default:
				break;
		}
		return true;
	}
}
