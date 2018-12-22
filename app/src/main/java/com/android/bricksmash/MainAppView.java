package com.android.bricksmash;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.Math;

public class MainAppView extends ViewGroup
{
	private boolean  	m_bFirstTime;
	private int 		m_nUserLife;
	
	private float 		m_fLastEventX;
	private float 		m_fLastEventY;
	
	public MainAppView(Context context) 
	{
		super(context);
		m_bFirstTime = true;
		m_nUserLife = GameLogics.Instance().getUserLife();
		setWillNotDraw(false);
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		// Check user life
		int nCurrUserLife = GameLogics.Instance().getUserLife();
		if (m_nUserLife != nCurrUserLife)
		{
			// User life changed. Show User Life message
			m_nUserLife = nCurrUserLife;
			showUserLife();
		}
	}
	
	private void showUserLife()
	{
		if (m_nUserLife < 0)
		{
			AlertDialog.Builder lifeInfo = new AlertDialog.Builder(getContext());
			lifeInfo.setTitle(R.string.app_name);
			lifeInfo.setMessage(R.string.game_over);
		
			// Set Reset button
			DialogInterface.OnClickListener resetButtonHandler = new DialogInterface.OnClickListener() 
			{			
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					GameLogics.Instance().resetGame();
					m_nUserLife = GameLogics.Instance().getUserLife();
					setUserLifeView();
				}
			};		
			lifeInfo.setPositiveButton(R.string.action_reset, resetButtonHandler);
			
			// Set Exit button
			DialogInterface.OnClickListener exitButtonHandler = new DialogInterface.OnClickListener() 
			{			
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
                    android.os.Process.killProcess(android.os.Process.myPid());
					System.exit(0);				
				}
			};		
			lifeInfo.setNegativeButton(R.string.action_exit, exitButtonHandler);

			// Prevent from dialog to be dismissed when clicking outside or pressing back
			lifeInfo.setCancelable(false);

			// Show dialog
			lifeInfo.show();		
		}
		else
			setUserLifeView();
	}
	
	private void setUserLifeView()
	{
		MainActivity oMainAct = (MainActivity)getContext();
		oMainAct.findViewById(R.id.player_life1).setVisibility(View.VISIBLE);
		oMainAct.findViewById(R.id.player_life2).setVisibility(View.VISIBLE);
		oMainAct.findViewById(R.id.player_life3).setVisibility(View.VISIBLE);
		oMainAct.findViewById(R.id.player_life4).setVisibility(View.VISIBLE);
		oMainAct.findViewById(R.id.player_life5).setVisibility(View.VISIBLE);
		oMainAct.findViewById(R.id.player_life6).setVisibility(View.VISIBLE);
		switch(m_nUserLife)
		{
			// No breaks in cases intentionally.
			case 0:
				oMainAct.findViewById(R.id.player_life1).setVisibility(View.INVISIBLE);
			case 1:
				oMainAct.findViewById(R.id.player_life2).setVisibility(View.INVISIBLE);
			case 2:
				oMainAct.findViewById(R.id.player_life3).setVisibility(View.INVISIBLE);
			case 3:
				oMainAct.findViewById(R.id.player_life4).setVisibility(View.INVISIBLE);
			case 4:
				oMainAct.findViewById(R.id.player_life5).setVisibility(View.INVISIBLE);
			case 5:
				oMainAct.findViewById(R.id.player_life6).setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int nCount = getChildCount();
		for (int i = 0; i < nCount; i++)
		{
			View oChild = getChildAt(i);
			oChild.measure(widthMeasureSpec, heightMeasureSpec);
		}

	    if (m_bFirstTime)
	    {
	    	int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	    	int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
	    	
	    	GameLogics.Instance().setScreenDimensions(parentWidth, parentHeight);	    
    		m_bFirstTime = false;
	    }
	}

    @Override
    public boolean onInterceptTouchEvent (MotionEvent event)
    {
        int nAction = event.getActionMasked();
        switch(nAction)
        {
            case MotionEvent.ACTION_DOWN:
                m_fLastEventX = event.getRawX();
                m_fLastEventY = event.getRawY();
                break;

            case MotionEvent.ACTION_UP:
                if (Math.abs(event.getRawX() - m_fLastEventX) < 5.0 &&
                    Math.abs(event.getRawY() - m_fLastEventY) < 5.0 )
                {
                    // Click event
                    performClick();
                }

            default:
                break;
        }
        return false;
    }

	@Override
	public boolean performClick()
	{
		Log.i("Aytek BS", "Main: performClick");
		// In order to release the game the user need to click the screen. Any other
		// action will not do. This gives the user the ability to move the base along
		// the screen to aim before releasing the ball
		GameLogics.Instance().setMoveAllowed(true);
		return super.performClick();
	}

	@Override
	protected void onLayout(boolean bChanged, int l, int t, int r, int b)
	{
		int nCount = getChildCount();
		for (int i = 0; i < nCount; i++)
		{
			View oChild = getChildAt(i);
			oChild.layout(0, 0, oChild.getMeasuredWidth(), oChild.getMeasuredHeight());
		}
	}
}
