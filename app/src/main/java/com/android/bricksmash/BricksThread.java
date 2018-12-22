package com.android.bricksmash;

public class BricksThread extends Thread 
{
	private int m_nBricksCount;
	private Brick m_oBrick[];
	private BricksView m_oBricksView;
	
	public BricksThread(Brick game_bricks[], BricksView oBricksView)
	{
		m_nBricksCount = game_bricks.length;
		m_oBrick = new Brick[m_nBricksCount];
		System.arraycopy(game_bricks, 0, m_oBrick, 0, game_bricks.length);
		m_oBricksView = oBricksView;
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			if (GameLogics.Instance().isMoveAllowed())
				for (int i=0; i<m_nBricksCount; i++)
					m_oBrick[i].move();
			m_oBricksView.postInvalidate();
			
			try
			{
				sleep(20);
			}
			catch(InterruptedException ie)
			{
				ie.printStackTrace();
				break;
			}
		}
	}
}
