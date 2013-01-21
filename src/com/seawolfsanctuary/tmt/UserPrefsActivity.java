package com.seawolfsanctuary.tmt;

import java.io.File;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class UserPrefsActivity extends Activity {

	CheckBox chk_CompleteFromStn;
	CheckBox chk_CompleteToStn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_prefs_activity);

		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		System.out.println("Loaded " + settings.getAll().size()
				+ " saved preferences.");

		chk_CompleteFromStn = (CheckBox) findViewById(R.id.chk_CompleteFromStation);
		chk_CompleteFromStn.setChecked(settings.getBoolean(
				"CompleteFromStation", true));

		chk_CompleteToStn = (CheckBox) findViewById(R.id.chk_CompleteToStation);
		chk_CompleteToStn.setChecked(settings.getBoolean("CompleteToStation",
				true));
	}

	@Override
	protected void onStop() {
		super.onStop();

		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();

		chk_CompleteFromStn = (CheckBox) findViewById(R.id.chk_CompleteFromStation);
		editor.putBoolean("CompleteFromStation",
				chk_CompleteFromStn.isChecked());

		chk_CompleteToStn = (CheckBox) findViewById(R.id.chk_CompleteToStation);
		editor.putBoolean("CompleteToStation", chk_CompleteToStn.isChecked());

		editor.commit();
	}

	public void clearCrashReports(View v) {
		File appFiles = getFilesDir(); // actually directory
		File[] listOfFiles = appFiles.listFiles();
		System.out.println("Found " + listOfFiles.length + " file(s) in "
				+ appFiles.getAbsolutePath());

		if (listOfFiles.length == 0) {
			Toast.makeText(getBaseContext(), "No previous reports found.",
					Toast.LENGTH_SHORT).show();
		} else {
			String fileInList;
			int filesRemoved = 0;
			for (File file : listOfFiles) {
				if (file.isFile()) {
					fileInList = file.getName();
					if (fileInList.endsWith(".stacktrace")) {
						System.out.println("Removing: " + fileInList);
						file.delete();
						filesRemoved += 1;
					}
				}
			}
			Toast.makeText(getBaseContext(),
					"" + filesRemoved + " previous reports removed.",
					Toast.LENGTH_SHORT).show();
		}
	}
}