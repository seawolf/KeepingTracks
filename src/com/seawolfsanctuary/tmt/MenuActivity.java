package com.seawolfsanctuary.tmt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

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

	public void startAddActivity(View v) {
		Intent intent = new Intent(this, AddActivity.class);
		startActivity(intent);
	}

	public void startListSavedActivity(View v) {
		Intent intent = new Intent(this, ListSavedActivity.class);
		startActivity(intent);
	}

}
