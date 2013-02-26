package org.example.projectunknown;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class Game extends Activity
{
	private static final String TAG = ProjectUnknown.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// requesting to turn the title OFF and making it full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(new MainGamePanel(this));
		Log.d(TAG, "Game");
	}

	@Override
	public void onBackPressed()
	{
		pauseGame();
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Exit Alert");
		alertDialog.setMessage("Do you really want to exit the Game?");
		alertDialog.setButton("Yes", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				setPreferences(MainGamePanel.score);
				android.os.Process.killProcess(android.os.Process.myPid());
				return;
			}
		});
		alertDialog.setButton2("No", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
				resumeGame();
				return;
			}
		});
		alertDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, R.string.pause_title);
		menu.add(0, 2, 0, R.string.resume_title);
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case 1:
				Log.d(TAG, "Clicked on Pause Game button.");
				pauseGame();
				return true;
			case 2:
				Log.d(TAG, "Clicked on Resume Game button.");
				resumeGame();
				return true;
		}
		return false;
	}

	protected void pauseGame()
	{
		MainThread.gameState = GameStates.PAUSED;
	}

	protected void resumeGame()
	{
		MainThread.gameState = GameStates.RUNNING;
	}

	public void setPreferences(int currentScore)
	{
		SharedPreferences prefs = this.getSharedPreferences("ProjectUnknown", Context.MODE_PRIVATE);
		int score = prefs.getInt("highscore", 0);
		if (score < currentScore)
		{
			Editor editor = prefs.edit();
			editor.putInt("highscore", MainGamePanel.score);
			editor.commit();
		}
	}
}
