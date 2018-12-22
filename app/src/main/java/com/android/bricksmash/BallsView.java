package com.android.bricksmash;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

public class BallsView extends View
{
	private CopyOnWriteArrayList<Ball> m_lstBalls;
	private Stack<Ball> m_stkRemovedBalls;
	private Bitmap	m_oBallBitmap[];
	private int 	m_nBitmapIndex;

    // Because removed balls are not immediately removed from array list I need to track
    // the number of balls in game
    private int m_nNumOfGameBalls;

    public enum BallTypeEnum
    {
        BALL_TYPE_NORMAL,
        BALL_TYPE_FIRE,
        BALL_TYPE_MINI,
        BALL_TYPE_MEGA
    }

    // Some ball capabilities
    private boolean m_bHighSpeed;
    private boolean m_bThroughBrick;
    private BallTypeEnum m_eBallType;

	public BallsView(Context context)
	{
		super(context);

        // List to contain active packs
        m_lstBalls = new CopyOnWriteArrayList<>();
        m_stkRemovedBalls = new Stack<>();

        // Create a ball with default position
		Ball oBall = new Ball(context);
		m_lstBalls.add(oBall);
        m_nNumOfGameBalls = 1;

        m_bHighSpeed = false;
        m_bThroughBrick = false;
        m_eBallType = BallTypeEnum.BALL_TYPE_NORMAL;

		// Create Ball Bitmaps
		createBallBitmaps();		
	}
	
	private void createBallBitmaps()
	{
		// Create ball bitmaps
		m_oBallBitmap = new Bitmap[13];
		m_oBallBitmap[0] = BitmapFactory.decodeResource(getResources(), R.drawable.ball01);
		m_oBallBitmap[1] = BitmapFactory.decodeResource(getResources(), R.drawable.ball02);
		m_oBallBitmap[2] = BitmapFactory.decodeResource(getResources(), R.drawable.ball03);
		m_oBallBitmap[3] = BitmapFactory.decodeResource(getResources(), R.drawable.ball04);
		m_oBallBitmap[4] = BitmapFactory.decodeResource(getResources(), R.drawable.ball05);
		m_oBallBitmap[5] = BitmapFactory.decodeResource(getResources(), R.drawable.ball06);
		m_oBallBitmap[6] = BitmapFactory.decodeResource(getResources(), R.drawable.ball07);
		m_oBallBitmap[7] = BitmapFactory.decodeResource(getResources(), R.drawable.ball08);
		m_oBallBitmap[8] = BitmapFactory.decodeResource(getResources(), R.drawable.ball09);
		m_oBallBitmap[9] = BitmapFactory.decodeResource(getResources(), R.drawable.ball10);
		m_oBallBitmap[10] = BitmapFactory.decodeResource(getResources(), R.drawable.ball_normal);
        m_oBallBitmap[11] = BitmapFactory.decodeResource(getResources(), R.drawable.ball_mini);
        m_oBallBitmap[12] = BitmapFactory.decodeResource(getResources(), R.drawable.ball_mega);
		m_nBitmapIndex = 10;
	}

    public synchronized void addNewBall(int nStartX, int nStartY, int nMoveAngleX, int nMoveAngleY, boolean bMovingRight, boolean bMovingDown)
    {
        Ball oBall = new Ball(getContext(), nStartX, nStartY, nMoveAngleX, nMoveAngleY, bMovingRight, bMovingDown);

        // Fix ball radius
        if (m_eBallType == BallTypeEnum.BALL_TYPE_MEGA)
            oBall.setR(oBall.getR()*2);
        if (m_eBallType == BallTypeEnum.BALL_TYPE_MINI)
            oBall.setR(oBall.getR()/2);

        m_lstBalls.add(oBall);
        m_nNumOfGameBalls++;
    }

    public synchronized void removeBall(Ball oBall)
    {
        m_stkRemovedBalls.push(oBall);
        oBall.setActive(false);
        m_nNumOfGameBalls--;
    }

    public synchronized void clearRemovedBalls()
    {
        while(!m_stkRemovedBalls.empty())
        {
            Ball oBall = m_stkRemovedBalls.pop();
            m_lstBalls.remove(oBall);
        }
    }

    public synchronized void restart()
    {
        m_lstBalls.clear();
        m_stkRemovedBalls.clear();

        // There is always one ball in game. Create new one with default values
        Ball oBall = new Ball(getContext());
        m_lstBalls.add(oBall);
        m_nNumOfGameBalls = 1;

        m_bHighSpeed = false;
        m_bThroughBrick = false;
        m_eBallType = BallTypeEnum.BALL_TYPE_NORMAL;
        m_nBitmapIndex = 10;
    }

    @Override
	public void onDraw(Canvas canvas)
	{
        Iterator<Ball> it = m_lstBalls.iterator();
        while(it.hasNext())
        {
            Ball oBall = it.next();
            if (m_eBallType == BallTypeEnum.BALL_TYPE_FIRE)
            {
                if (!GameLogics.Instance().isGamePaused() && GameLogics.Instance().isMoveAllowed())
                    m_nBitmapIndex++;
                if (m_nBitmapIndex >= 10)
                    m_nBitmapIndex = 0;
            }
            canvas.drawBitmap(m_oBallBitmap[m_nBitmapIndex], oBall.getX()-oBall.getR(), oBall.getY()-oBall.getR(), null);
        }
	}

    public CopyOnWriteArrayList<Ball> getBallList()         { return m_lstBalls;                }
    public boolean isHighSpeed()                            { return m_bHighSpeed;              }
    public boolean isThroughBrick()                         { return m_bThroughBrick;           }
    public BallTypeEnum getBallType()                       { return m_eBallType;               }
    public void setHighSpeed(boolean bHighSpeed)            { m_bHighSpeed = bHighSpeed;        }
    public void setThroughBrick(boolean bThroughBrick)      { m_bThroughBrick = bThroughBrick;  }
    public int getNumOfGameBalls()                          { return m_nNumOfGameBalls;         }

    public void setBallType(BallTypeEnum eBallType)
    {
        int nBallR = getContext().getResources().getDimensionPixelSize(R.dimen.Ball_R);
        m_eBallType = eBallType;
        switch (eBallType)
        {
            case BALL_TYPE_MEGA:
                m_nBitmapIndex = 12;
                nBallR *= 2;
                break;

            case BALL_TYPE_MINI:
                m_nBitmapIndex = 11;
                nBallR /= 2;
                break;

            case BALL_TYPE_NORMAL:
                m_nBitmapIndex = 10;
                break;

            default:
                m_nBitmapIndex = 0;
                break;
        }

        for (Ball oBall : m_lstBalls)
            oBall.setR(nBallR);
    }
}
