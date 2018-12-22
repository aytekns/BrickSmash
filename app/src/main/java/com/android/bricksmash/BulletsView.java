package com.android.bricksmash;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

public class BulletsView extends View
{
    private CopyOnWriteArrayList<Bullet> m_lstActiveBullets;
    private Stack<Bullet> m_stkRemovedBullets;
    private Bitmap m_oBulletBitmap;

    public BulletsView(Context context)
    {
        super(context);

        // List to contain active packs
        m_lstActiveBullets = new CopyOnWriteArrayList<>();
        m_stkRemovedBullets = new Stack<>();

        // Load Bullet Bitmap
        m_oBulletBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fire_bullet);
    }

    public synchronized void addNewBullet(int nStartX, int nStartY)
    {
        Bullet oPack = new Bullet(getContext(), nStartX, nStartY);
        m_lstActiveBullets.add(oPack);
    }

    public synchronized void removeBullet(Bullet oBullet)
    {
        m_stkRemovedBullets.push(oBullet);
        oBullet.setUsed(true);
    }

    public synchronized void clearRemovedBullets()
    {
        while(!m_stkRemovedBullets.empty())
        {
            Bullet oBullet = m_stkRemovedBullets.pop();
            m_lstActiveBullets.remove(oBullet);
        }
    }

    public synchronized void restart()
    {
        m_lstActiveBullets.clear();
        m_stkRemovedBullets.clear();
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        Iterator<Bullet> it = m_lstActiveBullets.iterator();
        while(it.hasNext())
        {
            Bullet oBlt = it.next();
            canvas.drawBitmap(m_oBulletBitmap, oBlt.getX(), oBlt.getY(), null);
        }
    }

    public CopyOnWriteArrayList<Bullet> getBulletList() { return m_lstActiveBullets; }
}
