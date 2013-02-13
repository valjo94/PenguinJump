package org.example.projectunknown;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

public class ProjectUnknown extends Activity implements OnClickListener {

	private static final String TAG = ProjectUnknown.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// requesting to turn the title OFF and making it full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
								WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_project_unknown);
		Log.d(TAG, "Main Menu view added");
		
		//TODO
		
		// GAME MENU BUTTON LISTENERS
		View continueButton = this.findViewById(R.id.continue_button);
		continueButton.setOnClickListener(this);
		
		View newButton = this.findViewById(R.id.new_button);
		newButton.setOnClickListener(this);
		
		View scoreButton = this.findViewById(R.id.score_button);
		scoreButton.setOnClickListener(this);
		
		View aboutButton = this.findViewById(R.id.about_button);
		aboutButton.setOnClickListener(this);
		
		View exitButton = this.findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		super.onCreateOptionsMenu(menu);
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.activity_project_unknown, menu);
//		return true;
//	}
	
	// GAME MENU SWITCH
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.continue_button:
				break;
				
			case R.id.new_button:
				openNewGameDialog();
				break;
				
			case R.id.score_button:				
				break;
				
			case R.id.about_button:
				Intent i = new Intent(this, About.class);
				startActivity(i);
				break;
				
			case R.id.exit_button:
				finish();
				break;
		}
	}
	
	/** Ask the user what theme of the level they want */
	private void openNewGameDialog() {
		
		new AlertDialog.Builder(this).setTitle(R.string.new_game_title).setItems(R.array.themes,
			new DialogInterface.OnClickListener() {
		
			public void onClick(DialogInterface dialoginterface,int i) {
				startGame(i);
			}
			
		}).show();
	
	}
	
	/** Start a new game with the given theme of the level */
	private void startGame(int i) {
		Log.d(TAG, "clicked on " + i);
		// Start game here...
		
		setContentView(R.layout.start_game);
		Log.d(TAG, "start_game layout");
		
		setContentView(new MainGamePanel(this));
		Log.d(TAG, "MainGamePanel");

	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "Destroying...");
		super.onDestroy();
	}
	
	@Override
	protected void onStop() {
		Log.d(TAG, "Stopping...");
		super.onStop();
	}
		

}
