package org.example.projectunknown.gameactivities;

import org.example.projectunknown.R;
import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;

public class Options extends Activity implements OnClickListener
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.options);

		View musicChBx = this.findViewById(R.id.music_button);
		musicChBx.setOnClickListener(this);

		View soundsChBx = this.findViewById(R.id.sounds_button);
		soundsChBx.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{

		switch (v.getId())
		{
			case R.id.music_button:
				if (ProjectUnknown.prefMusic.getBoolean("MUSIC", false) == true)
				{
					Editor editor = ProjectUnknown.prefMusic.edit();
					editor.putBoolean("MUSIC", false);
					editor.commit();
					System.out.println("MUSIC == FALSE");
				}
				else if (ProjectUnknown.prefMusic.getBoolean("MUSIC", false) == false)
				{
					Editor editor = ProjectUnknown.prefMusic.edit();
					editor.putBoolean("MUSIC", true);
					editor.commit();
					System.out.println("MUSIC == TRUE");
				}
				break;

			case R.id.sounds_button:
				if (ProjectUnknown.prefMusic.getBoolean("SOUNDS", false) == true)
				{
					Editor editor = ProjectUnknown.prefMusic.edit();
					editor.putBoolean("SOUNDS", false);
					editor.commit();
					System.out.println("SOUNDS == FALSE");
				}
				else if (ProjectUnknown.prefMusic.getBoolean("SOUNDS", false) == false)
				{
					Editor editor = ProjectUnknown.prefMusic.edit();
					editor.putBoolean("SOUNDS", true);
					editor.commit();
					System.out.println("SOUNDS == TRUE");
				}
				break;
		}
	}
}