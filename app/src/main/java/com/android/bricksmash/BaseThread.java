package com.android.bricksmash;

public class BaseThread extends Thread 
{
	private Base 		m_oBase;
	private BaseView	m_oBaseView;
	
	public BaseThread(Base oBase, BaseView oBaseView)
	{
		m_oBase 	= oBase;
		m_oBaseView = oBaseView;
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			m_oBase.move();
			m_oBaseView.postInvalidate();
			
			try
			{
				sleep(1);
			}
			catch(InterruptedException ie)
			{
				ie.printStackTrace();
				break;
			}
		}
	}
}
