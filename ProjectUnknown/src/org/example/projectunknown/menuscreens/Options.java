package org.example.projectunknown.menuscreens;

import org.example.projectunknown.R;
import org.example.projectunknown.R.layout;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.WindowManager;

public class Options extends PreferenceActivity
{

	// private static final String TAG = ProjectUnknown.class.getSimpleName();
	private static final String OPT_MUSIC = "music";

	private static final boolean OPT_MUSIC_DEF = true;

	private static final String OPT_SOUNDS = "sounds";

	private static final boolean OPT_SOUNDS_DEF = true;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		addPreferencesFromResource(R.layout.options);
	}

	// TODO CheckBoxPreference
	// final Preference pref = (Preference) findPreference("music");
	// pRref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	//
	// public boolean onPreferenceClick(Preference preference) {
	// otherPref.setSelectable(false);
	// Toast.makeText(getBaseContext(), "Some text", Toast.LENGTH_SHORT).show();
	// return true;
	// }
	// });
	//
	/** Get the current value of the music option */
	public static boolean getMusic(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_MUSIC, OPT_MUSIC_DEF);
	}

	/** Get the current value of the hints option */
	public static boolean getHints(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_SOUNDS, OPT_SOUNDS_DEF);
	}

}