package com.seawolfsanctuary.tmt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends Activity {

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
		setContentView(R.layout.menu_activity);
	}

	/** Called when the activity is displayed to the user */
	@Override
	public void onStart() {
		super.onStart();

		View vg = findViewById(R.id.MainMenu);
		vg.invalidate();

		Button btn_SetupFoursquare = (Button) findViewById(R.id.btn_Foursquared);
		Button btn_FoursquareVenues = (Button) findViewById(R.id.btn_Foursquare_Venues);

		btn_SetupFoursquare.setVisibility(View.VISIBLE);
		btn_FoursquareVenues.setVisibility(View.VISIBLE);

		if (Helpers.readAccessToken() != "") {
			// hide set-up button
			btn_SetupFoursquare.setVisibility(View.GONE);
		} else {
			// hide check-in button
			btn_FoursquareVenues.setVisibility(View.GONE);
		}
	}

	public void startAddActivity(View v) {
		Intent intent = new Intent(this, AddActivity.class);
		startActivity(intent);
	}

	public void startListSavedActivity(View v) {
		Intent intent = new Intent(this, ListSavedActivity.class);
		startActivity(intent);
	}

	public void startClassInfoActivity(View v) {
		Intent intent = new Intent(this, ClassInfoActivity.class);
		startActivity(intent);
	}

	public void startFoursquareSetupActivity(View v) {
		Intent intent = new Intent(this, FoursquareSetupActivity.class);
		startActivity(intent);
	}

	public void startFoursquareCheckinActivity(View v) {
		Intent intent = new Intent(this, FoursquareCheckinActivity.class);
		startActivity(intent);
	}

}
