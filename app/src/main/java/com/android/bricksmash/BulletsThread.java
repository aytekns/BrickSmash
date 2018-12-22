package com.android.bricksmash;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class BulletsThread extends Thread
{
    private BulletsView m_oBulletsView;

    public BulletsThread(BulletsView oBulletsView)
    {
        m_oBulletsView = oBulletsView;
    }

    @Override
    public void run()
    {
        while(true)
        {
            CopyOnWriteArrayList<Bullet> lstBulletList = m_oBulletsView.getBulletList();
            Iterator<Bullet> it = lstBulletList.iterator();
            while(it.hasNext())
            {
                Bullet oBlt = it.next();
                oBlt.move();
            }
            m_oBulletsView.postInvalidate();

            try
            {
                sleep(3);
                m_oBulletsView.clearRemovedBullets();
            }
            catch(InterruptedException ie)
            {
                ie.printStackTrace();
                break;
            }
         }
    }
}
