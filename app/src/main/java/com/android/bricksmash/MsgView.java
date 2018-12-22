package com.android.bricksmash;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

public class MsgView extends View 
{
	private Bitmap	m_oPauseBitmap;
	private Point   m_oScrSize;
	
	// For custom view, needed by infrastructure
	public MsgView(Context context, Point scr_size)
	{
		super(context);
		m_oScrSize = scr_size;
		
		// Load message bitmaps
		m_oPauseBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.game_pause);
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		if (GameLogics.Instance().isGamePaused())
		{
			// Draw pause message if game is paused
			float fLeft = m_oScrSize.x/2 - m_oPauseBitmap.getWidth()/2;
			float fTop  = m_oScrSize.y/2 - m_oPauseBitmap.getHeight()/2;
			canvas.drawBitmap(m_oPauseBitmap, fLeft, fTop, null);
		}
	}
}
