package com.android.bricksmash;

import android.media.AudioManager;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;


@SuppressLint("DefaultLocale")
public class MainActivity extends Activity 
{
	private MainAppView 	m_oMainView;
	private BallsThread 	m_oBallsAnimThread;
	private BaseThread 		m_oBaseAnimThread;
	private BricksThread	m_oBricksAnimThread;
    private PacksThread     m_oPacksAnimThread;
	private BulletsThread	m_oBulletsAnimThread;

	private boolean m_bIsCreated;

	public MainActivity()
	{
		Log.i("Aytek BS", "Main Activity constructor");
		m_bIsCreated = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		Log.i("Aytek BS", "Main: onCreate");
		super.onCreate(savedInstanceState);
		if (m_bIsCreated)
			return;

		m_bIsCreated = true;
		Log.i("Aytek BS", "First onCreate");

		// Set volume keys to control music volume
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// Keep screen on while in game
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		// Keep screen in portrait orientation
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// Get Screen Width and Height
		Point oDispEnd = new Point(0,0);
		getWindowManager().getDefaultDisplay().getSize(oDispEnd);
		
		// Create BallView (balls are created in view and during the game)
		BallsView oBallsView = new BallsView(this);
		
		// Create Base and BaseView
		Base		oBase = new Base(this, oDispEnd.x);
		BaseView	oBaseView = new BaseView(this, oBase);
		
		int nStartXPos = getResources().getDimensionPixelOffset(R.dimen.bricks_start_pos_x);
		int nStartYPos = getResources().getDimensionPixelOffset(R.dimen.bricks_start_pos_y);
		int nMatrixH = getResources().getInteger(R.integer.Bricks_Matrix_Height);
		int nMatrixW = getResources().getInteger(R.integer.Bricks_Matrix_Width);
		
		// Create Bricks and BricksView.
		int nLastIndx = 0;
		Brick oBricks[] = new Brick[getResources().getInteger(R.integer.Total_Bricks_Count)];
		for (int j=0, nYPos=nStartYPos; j<nMatrixH; nYPos = oBricks[nLastIndx].getBottom(), j++)
		{
			for (int i=0, nXPos=nStartXPos; i<nMatrixW; nXPos = oBricks[nLastIndx].getRight(), i++)
			{
				// Currently create bricks with right position but default properties.
				// Properties are initialized later by game logics manager
				nLastIndx = i + j*nMatrixW;
				oBricks[nLastIndx] = new Brick(this, nXPos, nYPos, oDispEnd.y-100, 0, 1, -1, false);
			}
		}
		BricksView 	oBricksView = new BricksView(this, oBricks);

        // Create Pack View (packs are created during the game)
        PacksView oPacksView = new PacksView(this);

		// Create Bullets View (bullets are created during the game)
		BulletsView oBulletsView = new BulletsView(this);

		MsgView oMsgView = new MsgView(this, oDispEnd);
		
		// Create application view
		m_oMainView = new MainAppView(this);
		m_oMainView.addView(oBallsView);
		m_oMainView.addView(oBaseView);
		m_oMainView.addView(oBricksView);
        m_oMainView.addView(oPacksView);
		m_oMainView.addView(oBulletsView);
		m_oMainView.addView(oMsgView);
		
		// Set Content View (not using the activity XML content)
		setContentView(m_oMainView);

		// Initialize action bar
		ActionBar actionBar = getActionBar();
        if (actionBar != null)
        {
            actionBar.setCustomView(R.layout.life_view);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        }
	    findViewById(R.id.player_life4).setVisibility(View.INVISIBLE);
	    findViewById(R.id.player_life5).setVisibility(View.INVISIBLE);
	    findViewById(R.id.player_life6).setVisibility(View.INVISIBLE);
	    
	    // Initialize Game Sounds
	    GameSounds.Instance().addSound(GameSounds.BASE_HIT,  		R.raw.base_hit,   		getBaseContext());
	    GameSounds.Instance().addSound(GameSounds.SPARK_HIT, 		R.raw.spark_hit,  		getBaseContext());
	    GameSounds.Instance().addSound(GameSounds.BRICK_HIT, 		R.raw.brick_hit,  		getBaseContext());
	    GameSounds.Instance().addSound(GameSounds.GRND_HIT,  		R.raw.ground_hit, 		getBaseContext());
	    GameSounds.Instance().addSound(GameSounds.WALL_HIT,  		R.raw.wall_hit,   		getBaseContext());
		GameSounds.Instance().addSound(GameSounds.EXPLODE_BALL,		R.raw.explode_ball, 	getBaseContext());
		GameSounds.Instance().addSound(GameSounds.EXPLODE_BRICK,	R.raw.explode_brick,	getBaseContext());
		GameSounds.Instance().addSound(GameSounds.EXTRA_LIFE,		R.raw.extra_life,		getBaseContext());
		GameSounds.Instance().addSound(GameSounds.KILL_BASE,		R.raw.kill_base,		getBaseContext());
		GameSounds.Instance().addSound(GameSounds.POWERUP,			R.raw.powerup,			getBaseContext());
		GameSounds.Instance().addSound(GameSounds.GOOD_POWERUP,		R.raw.good_powerup,		getBaseContext());
		GameSounds.Instance().addSound(GameSounds.BAD_POWERUP,		R.raw.bad_powerup,		getBaseContext());
		GameSounds.Instance().addSound(GameSounds.NEXT_LEVEL,		R.raw.next_level,		getBaseContext());
		GameSounds.Instance().addSound(GameSounds.SHOOTING_BULLET,  R.raw.shooting_bullet,	getBaseContext());
		GameSounds.Instance().addSound(GameSounds.EXPAND_BASE,  	R.raw.expand_base,		getBaseContext());
		GameSounds.Instance().addSound(GameSounds.SHRINK_BASE,  	R.raw.shrink_base,		getBaseContext());
		GameSounds.Instance().addSound(GameSounds.METAL_BRICK,  	R.raw.metal_brick,		getBaseContext());
	    
        // Initialize the game logic manager
	    GameLogics.Instance().setApplicationContext(getApplicationContext());
		GameLogics.Instance().setGameMainView(m_oMainView);
        GameLogics.Instance().setGameBallsView(oBallsView);
        GameLogics.Instance().setGameBase(oBase);
        GameLogics.Instance().setGameBricks(oBricks);
        GameLogics.Instance().setGamePacksView(oPacksView);
		GameLogics.Instance().setGameBulletsView(oBulletsView);
        GameLogics.Instance().setScreenDimensions(oDispEnd.x, oDispEnd.y);
		GameLogics.Instance().setGameMsgView(oMsgView);
        GameLogics.Instance().resetGame();

		// Create and run the thread in which ball animation is implemented
		m_oBallsAnimThread = new BallsThread(oBallsView);
		m_oBallsAnimThread.start();
		
		// Create and run the thread in which base animation is implemented
		m_oBaseAnimThread = new BaseThread(oBase, oBaseView);
		m_oBaseAnimThread.start();
		
		// Create and run the thread in which bricks animation is implemented
		m_oBricksAnimThread = new BricksThread(oBricks, oBricksView);
		m_oBricksAnimThread.start();

        // Create and run the thread in which packs animation is implemented
        m_oPacksAnimThread = new PacksThread(oPacksView);
        m_oPacksAnimThread.start();

		// Create and run the thread in which bullets animation is implemented
		m_oBulletsAnimThread = new BulletsThread(oBulletsView);
		m_oBulletsAnimThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle selection
		switch(item.getItemId())
		{
			case R.id.action_exit:
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
				return true;
				
			case R.id.action_about:
                GameLogics.Instance().setGamePaused(true);
				showVersionInfo();
				return true;
				
			case R.id.action_restart:
				GameLogics.Instance().resetGame();
				return true;
				
			case R.id.action_pause:
				GameLogics.Instance().togglePause();
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
    public void onBackPressed() 
	{
		//super.onBackPressed();
		Log.i("Aytek BS", "Main: onBackPressed");
		GameLogics.Instance().togglePause();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		Log.i("Aytek BS", "Main: onStart");
		GameLogics.Instance().autoDistract(false);
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		Log.i("Aytek BS", "Main: onStop");
		GameLogics.Instance().setGamePaused(true);
		GameLogics.Instance().autoDistract(true);
	}
	
	@Override
	protected void onRestart()
	{
		super.onRestart();
		Log.i("Aytek BS", "Main: onRestart");
		GameLogics.Instance().autoDistract(false);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		Log.i("Aytek BS", "Main: onPause");
		GameLogics.Instance().autoDistract(true);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		Log.i("Aytek BS", "Main: onResume");
		GameLogics.Instance().autoDistract(false);
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Log.i("Aytek BS", "Main: onDestroy");
		android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
	}
	
		
	//+-----------------------+
	//| [IC] Brick  Smash!    |
	//| --------------------- |
	//| Author:	Nisim Aytek   |
	//| Version:	X.XX      |
	//| --------------------- |
	//|          OK           |
	//+-----------------------+
	private void showVersionInfo()
	{		
		// Version alert dialog box
		AlertDialog.Builder versionInfo = new AlertDialog.Builder(this);
        String versionName = BuildConfig.VERSION_NAME;
		
		// Version info
		versionInfo.setTitle(R.string.app_name);
		versionInfo.setIcon(R.drawable.ic_brick_smash);
		versionInfo.setMessage("Author:\t\tNisim Aytek\nVersion:\t\t" + versionName);
		
		// Set OK button
		OnClickListener okButtonHandler = new DialogInterface.OnClickListener()
		{			
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		};
		versionInfo.setNeutralButton("OK", okButtonHandler);

		// Prevent from dialog to be dismissed when clicking outside or pressing back
        versionInfo.setCancelable(false);

		// Show dialog
		versionInfo.show();
	}
}
