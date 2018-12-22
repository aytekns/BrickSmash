package com.android.bricksmash;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class BallsThread extends Thread
{
	private BallsView m_oBallsView;

	public BallsThread(BallsView oBallsView)
	{
		m_oBallsView = oBallsView;
	}
	
	@Override
	public void run()
	{
		while(true)
		{
            CopyOnWriteArrayList<Ball> lstBallList = m_oBallsView.getBallList();
            Iterator<Ball> it = lstBallList.iterator();
            while(it.hasNext())
            {
                Ball oBall = it.next();
                oBall.move();
            }
			m_oBallsView.postInvalidate();

			try
			{
				if (m_oBallsView.isHighSpeed())
					sleep(3);
				else
					sleep(5);
			}
			catch(InterruptedException ie)
			{
				ie.printStackTrace();
				break;
			}
		}
	}
}
