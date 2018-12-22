package com.android.bricksmash;


import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class PacksThread extends Thread
{
    private PacksView m_oPacksView;

    public PacksThread(PacksView oPacksView)
    {
        m_oPacksView = oPacksView;
    }

    @Override
    public void run()
    {
        while(true)
        {
            CopyOnWriteArrayList<Pack> lstPackList = m_oPacksView.getPackList();
            Iterator<Pack> it = lstPackList.iterator();
            while(it.hasNext())
            {
                Pack oPack = it.next();
                oPack.move();
            }
            m_oPacksView.postInvalidate();

            try
            {
                sleep(5);
                m_oPacksView.clearRemovedPacks();
            }
            catch(InterruptedException ie)
            {
                ie.printStackTrace();
                break;
            }
        }
    }
}
