package org.example.projectunknown.gameactivities;

import org.example.projectunknown.R;
import org.example.projectunknown.media.Music;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

public class ProjectUnknown extends Activity implements OnClickListener
{

	private static final String TAG = ProjectUnknown.class.getSimpleName();

	public static SharedPreferences prefMusic;

	public static SharedPreferences prefSounds;

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

		prefMusic = this.getSharedPreferences("MUSIC", Context.MODE_PRIVATE);
		prefSounds = this.getSharedPreferences("SOUNDS", Context.MODE_PRIVATE);

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
				Intent o = new Intent(this, Options.class);
				startActivity(o);
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
	public void openNewGameDialog()
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

	/** Start a new game with the given theme of the level */
	private void startGame(int i)
	{
		Log.d(TAG, "clicked on " + i);
		Intent p = new Intent(this, Game.class);
		p.putExtra("Theme", i);
		startActivity(p);
	}

	@Override
	protected void onDestroy()
	{
		Log.d(TAG, "Destroying...");
		Music.stop(this);
		super.onDestroy();
	}

	@Override
	protected void onStop()
	{
		Log.d(TAG, "Stopping main activity...");
		super.onStop();
	}

	public int getPreferences()
	{
		SharedPreferences prefs = this.getSharedPreferences("ProjectUnknown", Context.MODE_PRIVATE);
		int score = prefs.getInt("highscore", 0);
		return score;
	}

}
