package com.android.bricksmash;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

public class PacksView extends View
{
    private CopyOnWriteArrayList<Pack> m_lstActivePacks;
    private Stack<Pack> m_stkRemovedPacks;
    private Bitmap m_oPackBitmap[];

    public PacksView(Context context)
    {
        super(context);

        // List to contain active packs
        m_lstActivePacks = new CopyOnWriteArrayList<>();
        m_stkRemovedPacks = new Stack<>();

        // Create Brick Bitmaps
        createPackBitmaps();
    }

    private void createPackBitmaps()
    {
        m_oPackBitmap = new Bitmap[Pack.NUM_OF_PACK_CONTENT_TYPES];

        m_oPackBitmap[Pack.PACK_CONT_EXTRA_LIFE       ] = BitmapFactory.decodeResource(getResources(), R.drawable.extra_life);
        m_oPackBitmap[Pack.PACK_CONT_SET_OFF_EXPLODE  ] = BitmapFactory.decodeResource(getResources(), R.drawable.setoff_explode);
        m_oPackBitmap[Pack.PACK_CONT_SHOOTING_BASE    ] = BitmapFactory.decodeResource(getResources(), R.drawable.shooting_base);
        m_oPackBitmap[Pack.PACK_CONT_GRAB_BASE        ] = BitmapFactory.decodeResource(getResources(), R.drawable.grab_base);
        m_oPackBitmap[Pack.PACK_CONT_SLOW_BALL        ] = BitmapFactory.decodeResource(getResources(), R.drawable.slow_ball);
        m_oPackBitmap[Pack.PACK_CONT_FIRE_BALL        ] = BitmapFactory.decodeResource(getResources(), R.drawable.fire_ball);
        m_oPackBitmap[Pack.PACK_CONT_WRAP_LEVEL       ] = BitmapFactory.decodeResource(getResources(), R.drawable.wrap_level);
        m_oPackBitmap[Pack.PACK_CONT_ZAP_BRICKS       ] = BitmapFactory.decodeResource(getResources(), R.drawable.zap_bricks);
        m_oPackBitmap[Pack.PACK_CONT_MULTIPLY_EXPLODE ] = BitmapFactory.decodeResource(getResources(), R.drawable.multiply_explode);
        m_oPackBitmap[Pack.PACK_CONT_THROUGH_BRICK    ] = BitmapFactory.decodeResource(getResources(), R.drawable.through_brick);
        m_oPackBitmap[Pack.PACK_CONT_EXPAND_BASE      ] = BitmapFactory.decodeResource(getResources(), R.drawable.expand_base);
        m_oPackBitmap[Pack.PACK_CONT_SHRINK_BASE      ] = BitmapFactory.decodeResource(getResources(), R.drawable.shrink_base);
        m_oPackBitmap[Pack.PACK_CONT_MEGA_BALL        ] = BitmapFactory.decodeResource(getResources(), R.drawable.mega_ball);
        m_oPackBitmap[Pack.PACK_CONT_SPLIT_BALL       ] = BitmapFactory.decodeResource(getResources(), R.drawable.split_ball);
        m_oPackBitmap[Pack.PACK_CONT_EIGHT_BALL       ] = BitmapFactory.decodeResource(getResources(), R.drawable.eight_ball);
        m_oPackBitmap[Pack.PACK_CONT_KILL_BASE        ] = BitmapFactory.decodeResource(getResources(), R.drawable.kill_base);
        m_oPackBitmap[Pack.PACK_CONT_FAST_BALL        ] = BitmapFactory.decodeResource(getResources(), R.drawable.fast_ball);
        m_oPackBitmap[Pack.PACK_CONT_MINI_BASE        ] = BitmapFactory.decodeResource(getResources(), R.drawable.mini_base);
        m_oPackBitmap[Pack.PACK_CONT_MINI_BALL        ] = BitmapFactory.decodeResource(getResources(), R.drawable.mini_ball);
        m_oPackBitmap[Pack.PACK_CONT_FALLING_BRICKS   ] = BitmapFactory.decodeResource(getResources(), R.drawable.falling_bricks);
    }

    public synchronized void addNewPack(int nStartX, int nStartY, int nContent, int nDirection, int nMaxW)
    {
        Pack oPack = new Pack(getContext(), nStartX, nStartY, nContent, nDirection, nMaxW);
        m_lstActivePacks.add(oPack);
    }

    public synchronized void removePack(Pack oPack)
    {
        m_stkRemovedPacks.push(oPack);
        oPack.setUsed(true);
    }

    public synchronized void clearRemovedPacks()
    {
        while(!m_stkRemovedPacks.empty())
        {
            Pack oPack = m_stkRemovedPacks.pop();
            m_lstActivePacks.remove(oPack);
        }
    }

    public synchronized void restart()
    {
        m_lstActivePacks.clear();
        m_stkRemovedPacks.clear();
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        Iterator<Pack> it = m_lstActivePacks.iterator();
        while(it.hasNext())
        {
            Pack oPack = it.next();
            canvas.drawBitmap(m_oPackBitmap[oPack.getContent()], oPack.getX(), oPack.getY(), null);
        }
    }

    public CopyOnWriteArrayList<Pack> getPackList() { return m_lstActivePacks; }
}
