package com.seawolfsanctuary.tmt;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

public class UserPrefsActivity extends Activity {
	public static final String PREFS_APP = "ApplicationPrefs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_prefs_activity);

		SharedPreferences settings = getSharedPreferences(PREFS_APP, 0);
	}

	@Override
	protected void onStop() {
		super.onStop();

		SharedPreferences settings = getSharedPreferences(PREFS_APP, 0);
		SharedPreferences.Editor editor = settings.edit();
		// boolean preferenceValue = ... ;
		// editor.putBoolean("preferenceName", preferenceValue);

		editor.commit();
	}
}