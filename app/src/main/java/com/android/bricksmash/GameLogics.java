package com.android.bricksmash;

import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;


/*
 * This class is responsible for all the logics of the game. It continuously checks the
 * ball position in relevance to the base, changes ball direction angle and speed if needed,
 * disqualifies the user if it misses the ball, freezes the game and keeps count of lives.
 */
public class GameLogics 
{
	// Maximum number of game levels
	public final static int MAX_LEVEL_NUMBER = 12;
	
	// Period of time (in secs) to release ball
	public final static int SECS_TO_RELEASE_BALL = 10;
	
	// Period of time (in secs) to increase ball speed
	public final static int SECS_TO_SPEED_UP_BALL = 40;
	
	// When game stopped (onStop) user have 2 min to resume it
	public final static int SECS_TO_KILL_APP = 120;

	// Starting level
	private final static int START_LEVEL = 1;
	
	// Singleton design
	private static GameLogics m_Instance;
	
	// Application Context
	private Context m_oAppContext;

	// Application main view
	private MainAppView m_oMainView;

	// Game balls view
	private BallsView m_oBallsView;
	
	// Game base
	private Base m_oBase;
	
	// Game bricks
	private int	m_nBricksLeft;		// Number of visible bricks left
	private Brick[] m_oBrick;			// Bricks array

	// Game packs view
	private PacksView m_oPacksView;

	// Game bullets view
	private BulletsView m_oBulletsView;

	// Message view
	private MsgView m_oMsgView;

	// Freeze the game if a paddle misses the ball
	private boolean m_bIsMoveAllowed;
	private boolean m_bIsGamePaused;
	
	// Screen dimensions
	private int m_nMaxWidth;
	private int m_nMaxHeight;
	
	// User's life
	private int m_nLife;
	
	// Current level
	private int m_nCurrentLevel;
	
	// Timer ability (in the form of handler and runnable)
	private final Handler m_oTimerHandler;
	private final Runnable m_oTimerTaskRunnable;
	
	// Timer counters
	private float m_fTimerRepCounter;
	private float m_fTimerEndCounter;

	// 1 if device uses software navigation keys, 0 otherwise
	private int m_nHasSWKeys = -1;

	// Private constructor - singleton design
	private GameLogics()
	{
		m_bIsMoveAllowed = false;
		m_nLife = 3;
		m_nCurrentLevel = START_LEVEL;
		
		m_oTimerHandler = new Handler();
		m_oTimerTaskRunnable = new Runnable() {
			@Override
			public void run() {
				// Timer task job
				timerTaskJob();
				
				// Re-Trigger the timer for 0.5 second
				m_oTimerHandler.postDelayed(this, 500);
			}
		};
	}
	
	public void timerTaskJob()
	{
		int nBaseType = m_oBase.getBaseType();
        if (nBaseType == Base.BASE_NORMAL_S || nBaseType == Base.BASE_MINI_S || nBaseType == Base.BASE_MEGA_S)
            // Create 2 new fire bullets
            createNewBullets();

		if (m_fTimerRepCounter <= SECS_TO_SPEED_UP_BALL)
			m_fTimerRepCounter += 0.5F;

		if (m_fTimerRepCounter == SECS_TO_RELEASE_BALL)
			setMoveAllowed(true);

		if (m_fTimerRepCounter == SECS_TO_SPEED_UP_BALL)
			m_oBallsView.setHighSpeed(true);

		// Check to see if I need to count seconds to kill activity
		if (m_fTimerEndCounter >= 0)
			m_fTimerEndCounter += 0.5F;
		
		if (m_fTimerEndCounter >= SECS_TO_KILL_APP)
		{
			Log.i("Aytek BS", "Kill Timeout. Exiting.");
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}

    private void createNewBullets()
    {
        int nBltH = m_oAppContext.getResources().getDimensionPixelSize(R.dimen.Bullet_H);
        int nBltW = m_oAppContext.getResources().getDimensionPixelSize(R.dimen.Bullet_W);

        m_oBulletsView.addNewBullet(m_oBase.getLeft(), m_oBase.getTop()-nBltH);
        m_oBulletsView.addNewBullet(m_oBase.getRight()-nBltW, m_oBase.getTop()-nBltH);

        GameSounds.Instance().play(GameSounds.SHOOTING_BULLET, false);
    }

    public void resetGame()
	{
		m_bIsMoveAllowed = false;
		m_bIsGamePaused = false;
		m_nLife = 3;
		m_oMainView.postInvalidate();
        restartGameElements();
		m_nCurrentLevel = START_LEVEL;
		readGameLevel(m_nCurrentLevel);
		m_fTimerRepCounter = 0;
		m_fTimerEndCounter = -1;
		
		// Re-trigger timer. Handler runs every 0.5 second
        m_oTimerHandler.removeCallbacks(m_oTimerTaskRunnable);
		m_oTimerHandler.postDelayed(m_oTimerTaskRunnable, 500);
	}

	// Returns the only instance of this object in the game - singleton design
	public static synchronized GameLogics Instance()
	{
		if (m_Instance == null)
			m_Instance = new GameLogics();
		return m_Instance;
	}
	
	public void onBallMovement(Ball oBall)
	{
        if (!oBall.isActive())
            return;

		if (m_bIsMoveAllowed && !m_bIsGamePaused)
		{
			if (checkBallWall(oBall))
				return;

			checkBallBricks(oBall);
			checkBallBase(oBall);
		}
	}
	
	public void onBaseMovement()
	{
		if (m_bIsMoveAllowed)
		{
            // Check every ball in game with base
            for (Ball oBall : m_oBallsView.getBallList())
			{
                checkBallBase(oBall);
            }
		}
		else
        {
            // In this case there should be only one ball in the game.
            // Get it and move ball with base
			if (!m_oBallsView.getBallList().isEmpty())
			{
				Ball oBall = m_oBallsView.getBallList().get(0);
				oBall.setHorPosition(m_oBase.getX() + m_oBase.getW() / 2);
			}
        }
	}

	public void onPackMovement(Pack oPack)
	{
		if (checkPackFloor(oPack))
			return;

		checkPackBase(oPack);
	}

	public void onBulletMovement(Bullet oBullet)
	{
		if (oBullet.isUsed())
			return;

		if (checkBulletTop(oBullet))
			return;

		checkBulletBricks(oBullet);
	}

	private boolean checkBulletTop(Bullet oBullet)
	{
		if (oBullet.getTop() <= 0)
		{
			m_oBulletsView.removeBullet(oBullet);
			return true;
		}
		return false;
	}

	private void checkBulletBricks(Bullet oBullet)
	{
		int nBulletT = oBullet.getTop();
		int nBulletL = oBullet.getLeft();
        int nTotalBricks = m_oAppContext.getResources().getInteger(R.integer.Total_Bricks_Count);

        for (int i=0; i<nTotalBricks; i++)
        {
            // Disregard invisible bricks
            if (!m_oBrick[i].isVisible())
                continue;

            int nBrckB = m_oBrick[i].getBottom();
            int nBrckR = m_oBrick[i].getRight();
            int nBrckL = m_oBrick[i].getLeft();

            // Check if bullet is inline with brick
            if (nBulletL >= nBrckL && nBulletL <= nBrckR )
            {
                if (nBulletT <= nBrckB)
                {
                    // Bullet hits brick. Make brick invisible and remove bullet
                    if (m_oBrick[i].getContent() >= 0)
                    {
                        if (m_oBrick[i].getStrength() > 0 || m_oBallsView.isThroughBrick())
                        {
                            GameSounds.Instance().play(GameSounds.POWERUP, false);
                            m_oPacksView.addNewPack(m_oBrick[i].getX(), m_oBrick[i].getY(), m_oBrick[i].getContent(), 1, m_nMaxWidth);
                        }
                        else
                            GameSounds.Instance().play(GameSounds.METAL_BRICK, false);
                    }
                    else if (m_oBrick[i].getStrength() < 0)
                        GameSounds.Instance().play(GameSounds.METAL_BRICK, false);
                    else
                        GameSounds.Instance().play(GameSounds.BRICK_HIT, false);
                    if (!m_oBallsView.isThroughBrick())
                        m_oBulletsView.removeBullet(oBullet);
                    m_nBricksLeft -= m_oBrick[i].ballHit(false, m_oBallsView.isThroughBrick());
                    if (m_nBricksLeft <= 0)
                    {
                        nextLevel();
                        return;
                    }

                    // No need to continue checking bricks
                    return;
                }
            }
        }
	}

	private boolean checkPackFloor(Pack oPack)
	{
		if (oPack.getBottom() >= m_nMaxHeight)
		{
			m_oPacksView.removePack(oPack);
			return true;
		}
		return false;
	}

	private void checkPackBase(Pack oPack)
	{
        Iterator<Ball> it;

		if (!oPack.isUsed() &&
             oPack.getBottom() >= m_oBase.getTop()   &&
			 oPack.getRight()  >  m_oBase.getLeft()  &&
			 oPack.getLeft()   <  m_oBase.getRight() &&
             oPack.getTop() <= m_oBase.getBottom() )
		{
			// Pack is touching base. Use pack content and remove pack
			// TODO: Complete implementation of change of base/game and remove pack
			switch (oPack.getContent())
			{
				case Pack.PACK_CONT_EXTRA_LIFE:
					GameSounds.Instance().play(GameSounds.EXTRA_LIFE, false);
					m_nLife++;
					m_oMainView.postInvalidate();
					break;

				case Pack.PACK_CONT_SHOOTING_BASE:
					GameSounds.Instance().play(GameSounds.GOOD_POWERUP, false);
					if (m_oBase.getBaseType() == Base.BASE_NORMAL)
						m_oBase.setBaseType(Base.BASE_NORMAL_S);
					else if (m_oBase.getBaseType() == Base.BASE_MINI)
						m_oBase.setBaseType(Base.BASE_MINI_S);
					else if (m_oBase.getBaseType() == Base.BASE_MEGA)
						m_oBase.setBaseType(Base.BASE_MEGA_S);
					break;

				case Pack.PACK_CONT_SLOW_BALL:
					// Slow down the ball and reset time count for it
                    GameSounds.Instance().play(GameSounds.GOOD_POWERUP, false);
					m_oBallsView.setHighSpeed(false);
					m_fTimerRepCounter = 0;
					break;

                case Pack.PACK_CONT_FIRE_BALL:
                    GameSounds.Instance().play(GameSounds.GOOD_POWERUP, false);
                    m_oBallsView.setBallType(BallsView.BallTypeEnum.BALL_TYPE_FIRE);
                    break;

				case Pack.PACK_CONT_WRAP_LEVEL:
					GameSounds.Instance().play(GameSounds.NEXT_LEVEL, false);
					nextLevel();
					break;

                case Pack.PACK_CONT_ZAP_BRICKS:
                    GameSounds.Instance().play(GameSounds.GOOD_POWERUP, false);
                    zapBricks();
                    break;

                case Pack.PACK_CONT_THROUGH_BRICK:
                    GameSounds.Instance().play(GameSounds.GOOD_POWERUP, false);
                    m_oBallsView.setThroughBrick(true);
                    break;

				case Pack.PACK_CONT_EXPAND_BASE:
					GameSounds.Instance().play(GameSounds.EXPAND_BASE, false);
					if (m_oBase.getBaseType() == Base.BASE_MINI)
						m_oBase.setBaseType(Base.BASE_NORMAL);
					else if (m_oBase.getBaseType() == Base.BASE_MINI_S)
						m_oBase.setBaseType(Base.BASE_NORMAL_S);
					else if (m_oBase.getBaseType() == Base.BASE_NORMAL)
						m_oBase.setBaseType(Base.BASE_MEGA);
					else if (m_oBase.getBaseType() == Base.BASE_NORMAL_S)
						m_oBase.setBaseType(Base.BASE_MEGA_S);
					break;

				case Pack.PACK_CONT_SHRINK_BASE:
					GameSounds.Instance().play(GameSounds.SHRINK_BASE, false);
					if (m_oBase.getBaseType() == Base.BASE_MEGA)
						m_oBase.setBaseType(Base.BASE_NORMAL);
					else if (m_oBase.getBaseType() == Base.BASE_MEGA_S)
						m_oBase.setBaseType(Base.BASE_NORMAL_S);
					else if (m_oBase.getBaseType() == Base.BASE_NORMAL)
						m_oBase.setBaseType(Base.BASE_MINI);
					else if (m_oBase.getBaseType() == Base.BASE_NORMAL_S)
						m_oBase.setBaseType(Base.BASE_MINI_S);
					break;

                case Pack.PACK_CONT_MEGA_BALL:
                    GameSounds.Instance().play(GameSounds.GOOD_POWERUP, false);
                    m_oBallsView.setBallType(BallsView.BallTypeEnum.BALL_TYPE_MEGA);
                    break;

                case Pack.PACK_CONT_SPLIT_BALL:
                    GameSounds.Instance().play(GameSounds.GOOD_POWERUP, false);
                    // For each ball in the game create another one that moves in the same vertical
                    // direction but in opposite horizontal angle
                    it = m_oBallsView.getBallList().iterator();
                    while(it.hasNext())
                    {
                        Ball oBall = it.next();
                        if (oBall.isActive())
                            m_oBallsView.addNewBall(oBall.getX(), oBall.getY(), oBall.getMovingAngleX(), oBall.getMovingAngleY(), !oBall.isMovingRight(), oBall.isMovingDown());
                    }
                    break;

                case Pack.PACK_CONT_EIGHT_BALL:
                    GameSounds.Instance().play(GameSounds.GOOD_POWERUP, false);
                    // For each ball in the game create 8 balls moving in an expanding circle away from current ball
                    // Current ball can be removed
                    it = m_oBallsView.getBallList().iterator();
                    while(it.hasNext())
                    {
                        Ball oBall = it.next();
                        if (oBall.isActive())
                        {
                            m_oBallsView.addNewBall(oBall.getX(), oBall.getY(), oBall.getLowAngle(), oBall.getHighAngle(), true, false);    // right low, up high
                            m_oBallsView.addNewBall(oBall.getX(), oBall.getY(), oBall.getHighAngle(), oBall.getLowAngle(), true, false);    // right high, up low
                            m_oBallsView.addNewBall(oBall.getX(), oBall.getY(), oBall.getLowAngle(), oBall.getHighAngle(), false, false);   // left low, up high
                            m_oBallsView.addNewBall(oBall.getX(), oBall.getY(), oBall.getHighAngle(), oBall.getLowAngle(), false, false);   // left high, up low
                            m_oBallsView.addNewBall(oBall.getX(), oBall.getY(), oBall.getLowAngle(), oBall.getHighAngle(), true, true);     // right low, down high
                            m_oBallsView.addNewBall(oBall.getX(), oBall.getY(), oBall.getHighAngle(), oBall.getLowAngle(), true, true);     // right low, down high
                            m_oBallsView.addNewBall(oBall.getX(), oBall.getY(), oBall.getLowAngle(), oBall.getHighAngle(), false, true);    // left low, down high
                            m_oBallsView.addNewBall(oBall.getX(), oBall.getY(), oBall.getHighAngle(), oBall.getLowAngle(), false, true);    // left low, down high
                            m_oBallsView.removeBall(oBall);
                        }
                    }
                    break;

				case Pack.PACK_CONT_KILL_BASE:
					GameSounds.Instance().play(GameSounds.KILL_BASE, false);
                    restartGameElements();
					m_nLife--;
					m_oMainView.postInvalidate();
					if (m_nLife < 0)
						// Game over. Stop timer
						m_oTimerHandler.removeCallbacks(m_oTimerTaskRunnable);
					m_bIsMoveAllowed = false;
					m_fTimerRepCounter = 0;
					break;

				case Pack.PACK_CONT_FAST_BALL:
					GameSounds.Instance().play(GameSounds.BAD_POWERUP, false);
					m_oBallsView.setHighSpeed(true);
					break;

				case Pack.PACK_CONT_MINI_BASE:
					GameSounds.Instance().play(GameSounds.SHRINK_BASE, false);
					if (m_oBase.getBaseType() == Base.BASE_NORMAL || m_oBase.getBaseType() == Base.BASE_MEGA)
						m_oBase.setBaseType(Base.BASE_MINI);
					else if (m_oBase.getBaseType() == Base.BASE_NORMAL_S || m_oBase.getBaseType() == Base.BASE_MEGA_S)
						m_oBase.setBaseType(Base.BASE_MINI_S);
					break;

                case Pack.PACK_CONT_MINI_BALL:
                    GameSounds.Instance().play(GameSounds.BAD_POWERUP, false);
                    m_oBallsView.setBallType(BallsView.BallTypeEnum.BALL_TYPE_MINI);
                    break;
            }

			m_oPacksView.removePack(oPack);
		}
	}
	
	private boolean checkBallWall(Ball oBall)
	{
		int ballX = oBall.getX();
		int ballY = oBall.getY();
		int ballR = oBall.getR();
		
		// Check boundaries. If reached, reverse direction
		
		// Right wall
		if (ballX > (m_nMaxWidth-ballR))
		{
			oBall.bounceLeft();
			GameSounds.Instance().play(GameSounds.WALL_HIT, false);
			return false;
		}

		// Left wall
		if (ballX < ballR)
		{
			oBall.bounceRight();
			GameSounds.Instance().play(GameSounds.WALL_HIT, false);
			return false;
		}
		
		// Upper wall
		if (ballY < ballR)
		{
			oBall.bounceDown();
			GameSounds.Instance().play(GameSounds.WALL_HIT, false);
			return false;
		}
		
		// Bottom wall - user failure
		if (ballY > (m_nMaxHeight-ballR))
		{
			GameSounds.Instance().play(GameSounds.GRND_HIT, false);
            m_oBallsView.removeBall(oBall);
            if (m_oBallsView.getNumOfGameBalls() <= 0)
            {
                restartGameElements();
                m_nLife--;
				m_oMainView.postInvalidate();
                if (m_nLife < 0)
                    // Game over. Stop timer
                    m_oTimerHandler.removeCallbacks(m_oTimerTaskRunnable);
                m_bIsMoveAllowed = false;
                m_fTimerRepCounter = 0;
                return true;
            }
		}		
		return false;
	}
	
	private void checkBallBricks(Ball oBall)
	{
		int nBallT = oBall.getTop();
		int nBallB = oBall.getBottom();
		int nBallR = oBall.getRight();
		int nBallL = oBall.getLeft();
		int nBallX = oBall.getX();		// Ball Center in X axis
		int nBallY = oBall.getY();		// Ball Center in Y axis
        int nTotalBricks = m_oAppContext.getResources().getInteger(R.integer.Total_Bricks_Count);

		for (int i=0; i<nTotalBricks; i++)
		{
			// Disregard invisible bricks
			if (!m_oBrick[i].isVisible())
				continue;
			
			int nBrckT = m_oBrick[i].getTop();
			int nBrckB = m_oBrick[i].getBottom();
			int nBrckR = m_oBrick[i].getRight();
			int nBrckL = m_oBrick[i].getLeft();
			
			// Check ball-brick horizontally
			if ( nBallT <= nBrckB && nBallT >= nBrckT ||
				 nBallB <= nBrckB && nBallB >= nBrckT  )
			{
				// Ball in the same level of brick
				if (nBallX >= nBrckL && nBallX <= nBrckR)
				{
					// Ball hit brick.
                    playBallHitSound(i, oBall);

                    // Change ball direction unless it has 'through brick' capability
                    if (!m_oBallsView.isThroughBrick())
					    oBall.changeVerDirection();

                    // Remove brick if it lost all strength
                    ballHitsBrick(i);
					if (m_nBricksLeft <= 0)
					{
						nextLevel();
						return;
					}

					// No need to continue checking for this brick
					continue;
				}
			}
			
			// Check ball-brick vertically
			if ( nBallR >= nBrckL && nBallR <= nBrckR ||
				 nBallL >= nBrckL && nBallL <= nBrckR  )
			{
				// Ball in the same line of brick
				if (nBallY >= nBrckT && nBallY <= nBrckB)
				{
					// Ball hit brick.
                    playBallHitSound(i, oBall);

                    // Change ball direction unless it has 'through brick' capability
                    if (!m_oBallsView.isThroughBrick())
                        oBall.changeHorDirection();

                    // Remove brick if it lost all strength
                    ballHitsBrick(i);
					if (m_nBricksLeft <= 0)
					{
						nextLevel();
						return;
					}
                }
			}
		}
	}

    private void playBallHitSound(int nBrickIndx, Ball oBall)
    {
        if (m_oBrick[nBrickIndx].getContent() >= 0)
        {
            if (m_oBrick[nBrickIndx].getStrength() > 0 || m_oBallsView.getBallType() == BallsView.BallTypeEnum.BALL_TYPE_FIRE || m_oBallsView.isThroughBrick())
            {
				if (m_oBrick[nBrickIndx].getStrength() == 1 || m_oBallsView.getBallType() == BallsView.BallTypeEnum.BALL_TYPE_FIRE || m_oBallsView.isThroughBrick())
				{
					GameSounds.Instance().play(GameSounds.POWERUP, false);
					m_oPacksView.addNewPack(m_oBrick[nBrickIndx].getX(), m_oBrick[nBrickIndx].getY(), m_oBrick[nBrickIndx].getContent(), oBall.isMovingRight() ? 1 : -1, m_nMaxWidth);
				}
				else
					GameSounds.Instance().play(GameSounds.METAL_BRICK, false);
            }
            else if (m_oBrick[nBrickIndx].getStrength() < 0)
                GameSounds.Instance().play(GameSounds.METAL_BRICK, false);
        }
        else if (m_oBallsView.getBallType() == BallsView.BallTypeEnum.BALL_TYPE_FIRE)
            GameSounds.Instance().play(GameSounds.EXPLODE_BRICK, false);
        else if (m_oBrick[nBrickIndx].getStrength() < 0 || m_oBrick[nBrickIndx].getStrength() > 1)
            GameSounds.Instance().play(GameSounds.METAL_BRICK, false);
        else
            GameSounds.Instance().play(GameSounds.BRICK_HIT, false);
    }

    private void ballHitsBrick(int nBrickIndx)
    {
        boolean bFB = (m_oBallsView.getBallType() == BallsView.BallTypeEnum.BALL_TYPE_FIRE);
        int nMatrixH = m_oAppContext.getResources().getInteger(R.integer.Bricks_Matrix_Height);
        int nMatrixW = m_oAppContext.getResources().getInteger(R.integer.Bricks_Matrix_Width);
        int nTotalBricks = m_oAppContext.getResources().getInteger(R.integer.Total_Bricks_Count);
        int nIndx;
        boolean bTB = m_oBallsView.isThroughBrick();

        // Brick itself is hit
        m_nBricksLeft -= m_oBrick[nBrickIndx].ballHit(bFB, bTB);

        if (bFB)
        {
            try
            {
                if (nBrickIndx % nMatrixW != 0)                                             // Check that brick is not first in row
                {
                    nIndx = nBrickIndx - 1;
					if (m_oBrick[nIndx].isVisible() && m_oBrick[nIndx].getContent() >= 0)
                        m_oPacksView.addNewPack(m_oBrick[nIndx].getX(), m_oBrick[nIndx].getY(), m_oBrick[nIndx].getContent(), 1, m_nMaxWidth);
                    m_nBricksLeft -= m_oBrick[nIndx].ballHit(true, bTB);                    // Brick to the left

                    if (nBrickIndx >= nMatrixW)
                    {
                        nIndx = nBrickIndx - nMatrixW - 1;
                        if (m_oBrick[nIndx].isVisible() && m_oBrick[nIndx].getContent() >= 0)
                            m_oPacksView.addNewPack(m_oBrick[nIndx].getX(), m_oBrick[nIndx].getY(), m_oBrick[nIndx].getContent(), 1, m_nMaxWidth);
                        m_nBricksLeft -= m_oBrick[nIndx].ballHit(true, bTB);                // Brick to the left and above
                    }
                }

                if (nBrickIndx >= nMatrixW)                                                 // Check that brick is not in first row
                {
                    nIndx = nBrickIndx - nMatrixW;
                    if (m_oBrick[nIndx].isVisible() && m_oBrick[nIndx].getContent() >= 0)
                        m_oPacksView.addNewPack(m_oBrick[nIndx].getX(), m_oBrick[nIndx].getY(), m_oBrick[nIndx].getContent(), 1, m_nMaxWidth);
                    m_nBricksLeft -= m_oBrick[nIndx].ballHit(true, bTB);                    // Brick above

                    if (((nBrickIndx + 1) % nMatrixW) != 0)
                    {
                        nIndx = nBrickIndx - nMatrixW + 1;
                        if (m_oBrick[nIndx].isVisible() && m_oBrick[nIndx].getContent() >= 0)
                            m_oPacksView.addNewPack(m_oBrick[nIndx].getX(), m_oBrick[nIndx].getY(), m_oBrick[nIndx].getContent(), 1, m_nMaxWidth);
                        m_nBricksLeft -= m_oBrick[nIndx].ballHit(true, bTB);                // Brick above and to the right
                    }
                }

                if (((nBrickIndx + 1) % nMatrixW) != 0)                                     // Check that brick is not last in row
                {
                    nIndx = nBrickIndx + 1;
                    if (m_oBrick[nIndx].isVisible() && m_oBrick[nIndx].getContent() >= 0)
                        m_oPacksView.addNewPack(m_oBrick[nIndx].getX(), m_oBrick[nIndx].getY(), m_oBrick[nIndx].getContent(), 1, m_nMaxWidth);
                    m_nBricksLeft -= m_oBrick[nIndx].ballHit(true, bTB);                    // Brick to the right

                    if (nBrickIndx < (nTotalBricks - nMatrixW))
                    {
                        nIndx = nBrickIndx + nMatrixW + 1;
                        if (m_oBrick[nIndx].isVisible() && m_oBrick[nIndx].getContent() >= 0)
                            m_oPacksView.addNewPack(m_oBrick[nIndx].getX(), m_oBrick[nIndx].getY(), m_oBrick[nIndx].getContent(), 1, m_nMaxWidth);
                        m_nBricksLeft -= m_oBrick[nIndx].ballHit(true, bTB);                // Brick to the right and below
                    }
                }

                if (nBrickIndx < (nTotalBricks - nMatrixW))                                 // Check that brick is not in last row
                {
                    nIndx = nBrickIndx + nMatrixW;
                    if (m_oBrick[nIndx].isVisible() && m_oBrick[nIndx].getContent() >= 0)
                        m_oPacksView.addNewPack(m_oBrick[nIndx].getX(), m_oBrick[nIndx].getY(), m_oBrick[nIndx].getContent(), 1, m_nMaxWidth);
                    m_nBricksLeft -= m_oBrick[nIndx].ballHit(true, bTB);                    // Brick below

                    if ((nBrickIndx % nMatrixW) != 0)
                    {
                        nIndx = nBrickIndx + nMatrixW - 1;
                        if (m_oBrick[nIndx].isVisible() && m_oBrick[nIndx].getContent() >= 0)
                            m_oPacksView.addNewPack(m_oBrick[nIndx].getX(), m_oBrick[nIndx].getY(), m_oBrick[nIndx].getContent(), 1, m_nMaxWidth);
                        m_nBricksLeft -= m_oBrick[nIndx].ballHit(true, bTB);                // Brick below and to the left
                    }
                }
            }
            catch (ArrayIndexOutOfBoundsException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
    }
	
	private enum BaseHitZoneEnum
	{
		HIT_ZONE_EDGE_L,
		HIT_ZONE_INB_L,
		HIT_ZONE_MID,
		HIT_ZONE_INB_R,
		HIT_ZONE_EDGE_R,
		HIT_ZONE_NONE
	}
	
	private void checkBallBase(Ball oBall)
	{
		if ( oBall.getBottom() >= m_oBase.getTop() &&
			 oBall.getTop()  < m_oBase.getBottom() )
		{
			// Ball and base are at the same line. Check if ball is hitting
			// the base			
			if ( oBall.getRight() > m_oBase.getLeft() &&
				 oBall.getLeft() < m_oBase.getRight() &&
				 oBall.isMovingDown())
			{
				BaseHitZoneEnum eHitZ = getBallHitZone(oBall);
				switch(eHitZ)
				{
					case HIT_ZONE_MID:
						oBall.setMovingAngleX(oBall.getLowAngle());
						oBall.setMovingAngleY(oBall.getHighAngle());
						break;
						
					case HIT_ZONE_INB_L:
						if (oBall.isMovingRight())	// If ball is moving right, change direction
							oBall.changeHorDirection();
						oBall.setMovingAngleX(oBall.getAvgAngle());
						oBall.setMovingAngleY(oBall.getAvgAngle());
						break;
						
					case HIT_ZONE_INB_R:
						if (!oBall.isMovingRight())	// If ball is moving left, change direction
							oBall.changeHorDirection();
						oBall.setMovingAngleX(oBall.getAvgAngle());
						oBall.setMovingAngleY(oBall.getAvgAngle());
						break;
						
					case HIT_ZONE_EDGE_L:
						if (oBall.isMovingRight())	// If ball is moving right, change direction
							oBall.changeHorDirection();
						oBall.setMovingAngleX(oBall.getHighAngle());
						oBall.setMovingAngleY(oBall.getLowAngle());
						break;
						
					case HIT_ZONE_EDGE_R:
						if (!oBall.isMovingRight())	// If ball is moving left, change direction
							oBall.changeHorDirection();
						oBall.setMovingAngleX(oBall.getHighAngle());
						oBall.setMovingAngleY(oBall.getLowAngle());
						break;
						
					default:
						break;
				}
				oBall.bounceUp();
				if (m_oBallsView.isHighSpeed())
					GameSounds.Instance().play(GameSounds.SPARK_HIT, false);
				else
					GameSounds.Instance().play(GameSounds.BASE_HIT, false);
			}
		}
	}
	
	private BaseHitZoneEnum getBallHitZone(Ball oBall)
	{
		int zoneW = m_oBase.getW()/5;
		int ballX = oBall.getX();
		int baseL = m_oBase.getLeft();
		int baseR = m_oBase.getRight();
		
		if (baseL <= ballX && ballX < baseL+zoneW)
			return BaseHitZoneEnum.HIT_ZONE_EDGE_L;
		
		if (baseL+zoneW <= ballX && ballX < baseL+2*zoneW)
			return BaseHitZoneEnum.HIT_ZONE_INB_L;
		
		if (baseL+2*zoneW <= ballX && ballX < baseR-2*zoneW)
			return BaseHitZoneEnum.HIT_ZONE_MID;
		
		if (baseR-2*zoneW <= ballX && ballX < baseR-zoneW)
			return BaseHitZoneEnum.HIT_ZONE_INB_R;
		
		if (baseR-zoneW <= ballX && ballX <= baseR)
			return BaseHitZoneEnum.HIT_ZONE_EDGE_R;
		
		return BaseHitZoneEnum.HIT_ZONE_NONE;
	}

    private void zapBricks()
    {
        int nTotalBricks = m_oAppContext.getResources().getInteger(R.integer.Total_Bricks_Count);

        for (int i = 0; i < nTotalBricks; i++)
        {
            // Disregard invisible bricks
            if (!m_oBrick[i].isVisible())
                continue;

            if (m_oBrick[i].getStrength() < 0)
                m_nBricksLeft++;

            m_oBrick[i].setStrength(1);
        }
    }
	
	private void nextLevel()
	{
        restartGameElements();
		m_bIsMoveAllowed = false;
		if (m_nCurrentLevel < MAX_LEVEL_NUMBER)
		{
			readGameLevel(++m_nCurrentLevel);
			m_fTimerRepCounter = 0;
		}
		else
		{
			m_nCurrentLevel++;	// Will cause game to freeze
			m_oTimerHandler.removeCallbacks(m_oTimerTaskRunnable);
		}
	}
	
	private void readGameLevel(int nLevel)
	{
		String strFileName;
		if (nLevel < 10)
			strFileName = String.format(Locale.US, "Level00%d.xml", nLevel);
		else if (nLevel < 100)
			strFileName = String.format(Locale.US, "Level0%d.xml", nLevel);
		else
			strFileName = String.format(Locale.US, "Level%d.xml", nLevel);
		
		List<BrickXmlData> lstBrickList;
		try
		{
			lstBrickList = XmlLevelParser.readLevel(strFileName, m_oAppContext);
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
			return;
		}
		
		// Initialize bricks properties
        int nMatrixH = m_oAppContext.getResources().getInteger(R.integer.Bricks_Matrix_Height);
        int nMatrixW = m_oAppContext.getResources().getInteger(R.integer.Bricks_Matrix_Width);
        m_nBricksLeft = 0;
		for (int j=0; j<nMatrixH; j++)
		{
			for (int i=0; i<nMatrixW; i++)
			{
				int nLastIndx = i + j*nMatrixW;
				BrickXmlData brick_data = lstBrickList.get(nLastIndx);
				m_oBrick[nLastIndx].setBrickProperties(brick_data.getColor(), brick_data.getStrength(), brick_data.getContent(), brick_data.isExplosive());
				if (brick_data.getStrength() > 0)
					m_nBricksLeft++;
			}
		}
	}

    // Reset all game elements
    public void restartGameElements()
    {
        m_oBase.restart();
        m_oBallsView.restart();
        m_oPacksView.restart();
        m_oBulletsView.restart();
    }

	// Returns if movement is allowed or not.
	public boolean isMoveAllowed() { return m_bIsMoveAllowed; }
	
	// Returns if game is paused or not
	public boolean isGamePaused() { return m_bIsGamePaused; }
	
	// Toggles game-paused property
	public void togglePause()
	{
		m_bIsGamePaused = !m_bIsGamePaused;
		if (m_bIsGamePaused)
			m_oTimerHandler.removeCallbacks(m_oTimerTaskRunnable);
		else
			m_oTimerHandler.postDelayed(m_oTimerTaskRunnable, 500);

		m_oMsgView.postInvalidate();
	}
	
	// Sets the game pauses state
	public void setGamePaused(boolean bPause)
    {
        m_bIsGamePaused = bPause;
		if (m_bIsGamePaused)
			m_oTimerHandler.removeCallbacks(m_oTimerTaskRunnable);
		else
			m_oTimerHandler.postDelayed(m_oTimerTaskRunnable, 500);

		m_oMsgView.postInvalidate();
    }
	
	// Starts "Auto distract sequence"...
	public void autoDistract(boolean bAD) {  m_fTimerEndCounter = bAD ? 0 : -1; }
	
	// Returns user life
	public int getUserLife() { return m_nLife; }
	
	// Sets if Movement is allowed
	public void setMoveAllowed(boolean bMove) { if (m_nCurrentLevel <= MAX_LEVEL_NUMBER) m_bIsMoveAllowed = bMove; }

	// Sets the game main view. Game will not work without initializing this member
	public void setGameMainView(MainAppView oMainView) { m_oMainView = oMainView; }

	// Sets the game balls view. Game will not work without initializing this member
	public void setGameBallsView(BallsView oBallsView) { m_oBallsView = oBallsView; }
	
	// Sets the game base. Game will not work without initializing this member
	public void setGameBase(Base base) { m_oBase = base; }
	
	// Sets the game bricks. Game will not work without initializing this member
	public void setGameBricks(Brick[] brick)
    {
		m_oBrick = new Brick[m_oAppContext.getResources().getInteger(R.integer.Total_Bricks_Count)];
		System.arraycopy(brick, 0, m_oBrick, 0, brick.length);
	}

	// Sets the game packs view. Game will not work without initializing this member
	public void setGamePacksView(PacksView oPacksView) { m_oPacksView = oPacksView; }

	// Sets the game bullets view. Game will not work without initializing this member
	public void setGameBulletsView(BulletsView oBulletsView) { m_oBulletsView = oBulletsView; }

	// Sets the game message view. Game will not work without initializing this member
	public void setGameMsgView(MsgView oMsgView) { m_oMsgView = oMsgView; }

	// Sets the screen dimensions. Game will not work without initializing
	// these parameters
	public void setScreenDimensions(int maxWidth, int maxHeight)
	{
		m_nMaxWidth = maxWidth;
		m_nMaxHeight = maxHeight;
	}
	
	public void setApplicationContext(Context context) { m_oAppContext = context; }

	public boolean deviceUsesSWKeys(Context ctx)
	{
		if (m_nHasSWKeys < 0)
		{
			Display d = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			DisplayMetrics realDisplayMetrics = new DisplayMetrics();
			d.getRealMetrics(realDisplayMetrics);

			int nRealHeight = realDisplayMetrics.heightPixels;
			int nRealWidth = realDisplayMetrics.widthPixels;

			DisplayMetrics displayMetrics = new DisplayMetrics();
			d.getMetrics(displayMetrics);

			int nDisplayHeight = displayMetrics.heightPixels;
			int nDisplayWidth = displayMetrics.widthPixels;

			m_nHasSWKeys = ((nRealWidth > nDisplayWidth) || (nRealHeight > nDisplayHeight)) ? 1 : 0;
		}

		return (m_nHasSWKeys == 1);
	}
}
