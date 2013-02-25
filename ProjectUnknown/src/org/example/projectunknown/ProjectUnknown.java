package org.example.projectunknown;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

public class ProjectUnknown extends Activity implements OnClickListener
{

	private static final String TAG = ProjectUnknown.class.getSimpleName();

	MainGamePanel panel;

	MainThread thread;

	// private Boolean paused;

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
				openPauseGameDialog();
				return true;
			case 2:
				Log.d(TAG, "Clicked on Resume Game button.");
				openResumeGameDialog();
				return true;
			case 3:
				Log.d(TAG, "Clicked on Main Menu button.");
				return true;
		}
		return false;
	}

	// TODO
	@Override
	public void onBackPressed()
	{

		// panel.getThread().setRunning(false);
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

				// panel.getThread().resume();
				// panel.getThread().setRunning(true);
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
										.setItems(R.array.highscores, new DialogInterface.OnClickListener()
										{

											public void onClick(DialogInterface dialoginterface, int i)
											{
												dialoginterface.cancel();
												return;
											}

										})
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

	private void openPauseGameDialog()
	{
		new AlertDialog.Builder(this).setItems(R.array.pause, new DialogInterface.OnClickListener()
		{

			public void onClick(DialogInterface dialoginterface, int p)
			{
				pauseGame(p);
			}

		}).show();
	}

	// TODO
	protected void pauseGame(int p)
	{
		Log.d(TAG, "clicked on " + p);

		// panel.getThread().setRunning(false);

	}

	private void openResumeGameDialog()
	{
		resumeGame();
	}

	// TODO
	protected void resumeGame()
	{
		Log.d(TAG, "clicked on " + "resume game");

		// panel.getThread().resume();
		// panel.getThread().setRunning(true);

	}

	/** Start a new game with the given theme of the level */
	private void startGame(int i)
	{
		Log.d(TAG, "clicked on " + i);
		// Start game here...

		setContentView(R.layout.start_game);
		Log.d(TAG, "start_game layout");

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

}
