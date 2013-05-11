package com.seawolfsanctuary.keepingtracks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.seawolfsanctuary.keepingtracks.foursquare.CheckinActivity;
import com.seawolfsanctuary.keepingtracks.foursquare.SetupActivity;
import com.seawolfsanctuary.keepingtracks.stats.StatsActivity;

public class MenuActivity extends Activity {

	static ProgressDialog loader;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.website:
			Uri websiteUrl = Uri.parse("" + getText(R.string.website_url));
			Intent launchBrowser = new Intent(Intent.ACTION_VIEW, websiteUrl);
			startActivity(launchBrowser);
			return true;
		case R.id.exit:
			MenuActivity.this.finish();
			return true;
		default:
			return true;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_activity);
	}

	@Override
	public void onResume() {
		super.onResume();
		TextView txtFoursquared = (TextView) findViewById(R.id.txt_Foursquared);
		if (Helpers.readAccessToken() == "") {
			txtFoursquared.setText(R.string.foursquare_setup);
		} else {
			txtFoursquared.setText(R.string.foursquare_checkin);
		}
	}

	public void showLoader() {
		loader = ProgressDialog.show(MenuActivity.this, "", "", true);
	}

	public static void hideLoader() {
		loader.dismiss();
	}

	public void startAddActivity(View v) {
		showLoader();
		Intent intent = new Intent(this, AddActivity.class);
		startActivity(intent);
	}

	public void startListSavedActivity(View v) {
		showLoader();
		Intent intent = new Intent(this, ListSavedActivity.class);
		startActivity(intent);
	}

	public void startDataFileActivity(View v) {
		showLoader();
		Intent intent = new Intent(this, DataFileActivity.class);
		startActivity(intent);
	}

	public void startFoursquareSetupOrCheckinActivity(View v) {
		showLoader();
		if (Helpers.readAccessToken() == "") {
			startFoursquareSetupActivity(v);
		} else {
			startFoursquareCheckinActivity(v);
		}
	}

	private void startFoursquareSetupActivity(View v) {
		Intent intent = new Intent(this, SetupActivity.class);
		startActivity(intent);
	}

	private void startFoursquareCheckinActivity(View v) {
		Intent intent = new Intent(this, CheckinActivity.class);
		startActivity(intent);
	}

	public void startStatsActivity(View v) {
		showLoader();
		Intent intent = new Intent(this, StatsActivity.class);
		startActivity(intent);
	}

	public void startUserPrefsActivity(View v) {
		showLoader();
		Intent intent = new Intent(this, UserPrefsActivity.class);
		startActivity(intent);
	}
}