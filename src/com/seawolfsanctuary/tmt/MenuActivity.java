package com.seawolfsanctuary.tmt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MenuActivity extends Activity {
	public static final String PREFS_NAME = "TMT";

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_context_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.exit:
			MenuActivity.this.finish();
			return true;
		default:
			return true;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		if (settings.getString("last", "") != "") {
			showOKPopUp("Last Session",
					settings.getString("last", "(no last entry found)")
							.replace("You selected:", ""));
		}

		setContentView(R.layout.menu_activity);
	}

	public void startAddActivity(View v) {
		Intent intent = new Intent(this, AddActivity.class);
		startActivity(intent);
	}

	public void startListSavedActivity(View v) {
		Intent intent = new Intent(this, ListSavedActivity.class);
		startActivity(intent);
	}

	private void showOKPopUp(String title, String content) {
		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		helpBuilder.setTitle(title);
		helpBuilder.setMessage(content);
		helpBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing but close the dialog
					}
				});
		AlertDialog helpDialog = helpBuilder.create();
		helpDialog.show();
	}

}
