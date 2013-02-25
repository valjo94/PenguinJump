package org.example.projectunknown;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

public class ProjectUnknown extends Activity implements OnClickListener
{

	private static final String TAG = ProjectUnknown.class.getSimpleName();

	Intent p;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// requesting to turn the title OFF and making it full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_project_unknown);
		Log.d(TAG, "Main Menu view added");

		// GAME MENU BUTTON LISTENERS
		View newButton = this.findViewById(R.id.new_button);
		newButton.setOnClickListener(this);

		View continueButton = this.findViewById(R.id.options_button);
		continueButton.setOnClickListener(this);

		View scoreButton = this.findViewById(R.id.score_button);
		scoreButton.setOnClickListener(this);

		View aboutButton = this.findViewById(R.id.about_button);
		aboutButton.setOnClickListener(this);

		View exitButton = this.findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, R.string.pause_title);
		menu.add(0, 2, 0, R.string.resume_title);
		menu.add(0, 3, 0, R.string.main_menu_label);
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
			case 3:
				// TODO
				Log.d(TAG, "Clicked on Main Menu button.");
				// MainGamePanel.thread.stop();
				// setContentView(R.layout.activity_project_unknown);
				return true;
		}
		return false;
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

	// GAME MENU SWITCH
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.new_button:
				Log.d(TAG, "Clicked on New Game button.");
				openNewGameDialog();
				break;
			// TODO
			case R.id.options_button:
				Log.d(TAG, "Clicked on Options button.");
				break;
			case R.id.score_button:
				Log.d(TAG, "Clicked on HighScore button.");
				openHighscoresDialog();
				break;
			case R.id.about_button:
				Log.d(TAG, "Clicked on About button.");
				Intent i = new Intent(this, About.class);
				startActivity(i);
				break;
			case R.id.exit_button:
				Log.d(TAG, "Clicked on Exit button.");
				finish();
				break;
		}
	}

	private void openHighscoresDialog()
	{

		new AlertDialog.Builder(this).setTitle(R.string.high_score_label)
										.setMessage(String.valueOf(getPreferences()))
										.show();
	}

	/** Ask the user what theme of the level they want */
	private void openNewGameDialog()
	{
		new AlertDialog.Builder(this).setTitle(R.string.new_game_title)
										.setItems(R.array.themes, new DialogInterface.OnClickListener()
										{

											public void onClick(DialogInterface dialoginterface, int i)
											{
												startGame(i);
											}

										})
										.show();

	}

	protected void pauseGame()
	{
		MainThread.gameState = GameStates.PAUSED;
	}

	protected void resumeGame()
	{
		MainThread.gameState = GameStates.RUNNING;
	}

	/** Start a new game with the given theme of the level */
	private void startGame(int i)
	{
		Log.d(TAG, "clicked on " + i);
		// Start game here...

		setContentView(new MainGamePanel(this));
		Log.d(TAG, "MainGamePanel");

	}

	@Override
	protected void onDestroy()
	{
		Log.d(TAG, "Destroying...");
		super.onDestroy();
	}

	@Override
	protected void onStop()
	{
		Log.d(TAG, "Stopping...");
		super.onStop();
	}

	// TODO
	public void setPreferences()
	{
		// setting preferences
		SharedPreferences prefs = this.getSharedPreferences("ProjectUnknown", Context.MODE_PRIVATE);
		int score = prefs.getInt("highscore", 0);
		if (score < MainGamePanel.score)
		{
			Editor editor = prefs.edit();
			editor.putInt("highscore", MainGamePanel.score);
			editor.commit();
		}

	}

	public int getPreferences()
	{
		// getting preferences
		SharedPreferences prefs = this.getSharedPreferences("ProjectUnknown", Context.MODE_PRIVATE);
		int score = prefs.getInt("highscore", 0); // 0 is the default value
		return score;
	}

}
