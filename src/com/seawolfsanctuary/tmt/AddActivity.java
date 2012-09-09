package com.seawolfsanctuary.tmt;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddActivity extends TabActivity {

	Bundle template = new Bundle();

	TextView txt_FromSearch;
	DatePicker dp_FromDate;
	TimePicker tp_FromTime;
	AutoCompleteTextView actv_FromSearch;

	CheckBox chk_DetailClass;
	TextView txt_DetailClass;
	CheckBox chk_DetailHeadcode;
	TextView txt_DetailHeadcode;

	TextView txt_ToSearch;
	DatePicker dp_ToDate;
	TimePicker tp_ToTime;
	AutoCompleteTextView actv_ToSearch;

	TextView txt_Summary;
	CheckBox chk_Checkin;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.list:
			Intent intent = new Intent(this, ListSavedActivity.class);
			AddActivity.this.finish();
			startActivity(intent);
			return true;
		default:
			return true;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Link array of completions
		String[] completions = read_csv("stations.lst");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, completions);

		setContentView(R.layout.add_activity);

		TabHost mTabHost = getTabHost();

		mTabHost.addTab(mTabHost.newTabSpec("tc_From").setIndicator("From")
				.setContent(R.id.tc_From));
		mTabHost.addTab(mTabHost.newTabSpec("tc_Detail").setIndicator("Detail")
				.setContent(R.id.tc_Detail));
		mTabHost.addTab(mTabHost.newTabSpec("tc_To").setIndicator("To")
				.setContent(R.id.tc_To));
		mTabHost.addTab(mTabHost.newTabSpec("tc_Summary")
				.setIndicator("Summary").setContent(R.id.tc_Summary));

		mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabID) {
				template = Helpers.saveCurrentJourney(AddActivity.this);

				if (tabID == "tc_Detail") {
					if (template.containsKey("detail_class")) {
						txt_DetailClass = (TextView) findViewById(R.id.txt_DetailClass);
						txt_DetailClass.setText(template
								.getCharSequence("detail_class"));
					}
				}

				if (tabID == "tc_Summary") {
					updateText();
					txt_Summary = (TextView) findViewById(R.id.txt_Summary);
					chk_Checkin = (CheckBox) findViewById(R.id.chk_Checkin);
					actv_FromSearch = (AutoCompleteTextView) findViewById(R.id.actv_FromSearch);
					actv_ToSearch = (AutoCompleteTextView) findViewById(R.id.actv_ToSearch);

					if (Helpers.readAccessToken().length() > 0) {
						chk_Checkin.setEnabled(true);
					}

					chk_Checkin.setChecked(false);
					chk_Checkin.setEnabled(false);
					if (actv_FromSearch.getText().toString().length() > 0
							|| actv_ToSearch.getText().toString().length() > 0) {
						chk_Checkin.setEnabled(true);
						chk_Checkin.setChecked(true);
					}

				}
			}
		});

		mTabHost.setCurrentTab(0);

		actv_FromSearch = (AutoCompleteTextView) findViewById(R.id.actv_FromSearch);
		actv_ToSearch = (AutoCompleteTextView) findViewById(R.id.actv_ToSearch);

		actv_FromSearch.setAdapter(adapter);
		actv_FromSearch.setThreshold(2);
		actv_FromSearch
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						updateText();
						Helpers.hideKeyboard(actv_FromSearch);
					}
				});

		actv_ToSearch.setAdapter(adapter);
		actv_ToSearch.setThreshold(2);
		actv_ToSearch
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						updateText();
						Helpers.hideKeyboard(actv_ToSearch);
					}
				});

		template = getIntent().getExtras();
		Helpers.loadCurrentJourney(template, AddActivity.this);
	}

	private String[] read_csv(String filename) {
		String[] array = {};

		try {
			InputStream input;
			input = getAssets().open(filename);
			int size = input.available();
			byte[] buffer = new byte[size];

			input.read(buffer);
			input.close();
			array = new String(buffer).split("\n");

			Toast.makeText(getBaseContext(), "Stations loaded.",
					Toast.LENGTH_SHORT).show();

		} catch (Exception e) {
			String error_msg = "Error reading station list!";

			actv_FromSearch = (AutoCompleteTextView) findViewById(R.id.actv_FromSearch);
			actv_FromSearch.setText(error_msg);
			actv_FromSearch.setError(error_msg);
			actv_FromSearch.setEnabled(false);

			actv_ToSearch = (AutoCompleteTextView) findViewById(R.id.actv_ToSearch);
			actv_ToSearch.setText(error_msg);
			actv_ToSearch.setError(error_msg);
			actv_ToSearch.setEnabled(false);
		}

		return array;
	}

	private void updateText() {
		actv_FromSearch = (AutoCompleteTextView) findViewById(R.id.actv_FromSearch);
		dp_FromDate = (DatePicker) findViewById(R.id.dp_FromDate);
		tp_FromTime = (TimePicker) findViewById(R.id.tp_FromTime);

		txt_DetailClass = (TextView) findViewById(R.id.txt_DetailClass);
		txt_DetailHeadcode = (TextView) findViewById(R.id.txt_DetailHeadcode);

		actv_ToSearch = (AutoCompleteTextView) findViewById(R.id.actv_ToSearch);
		dp_ToDate = (DatePicker) findViewById(R.id.dp_ToDate);
		tp_ToTime = (TimePicker) findViewById(R.id.tp_ToTime);

		txt_Summary = (TextView) findViewById(R.id.txt_Summary);

		txt_Summary.setText("From:\t"
				+ Helpers.trimCodeFromStation(actv_FromSearch.getText()
						.toString())
				+ "\nOn:\t\t"
				+ dp_FromDate.getDayOfMonth()
				+ "/"
				+ (dp_FromDate.getMonth() + 1)
				+ "/"
				+ dp_FromDate.getYear()
				+ "\nAt:\t\t"
				+ tp_FromTime.getCurrentHour()
				+ ":"
				+ tp_FromTime.getCurrentMinute()
				+

				"\n\nTo:\t\t"
				+ Helpers.trimCodeFromStation(actv_ToSearch.getText()
						.toString()) + "\nOn:\t\t" + dp_ToDate.getDayOfMonth()
				+ "/" + (dp_ToDate.getMonth() + 1) + "/" + dp_ToDate.getYear()
				+ "\nAt:\t\t" + tp_ToTime.getCurrentHour() + ":"
				+ tp_ToTime.getCurrentMinute() +

				"\n\nWith:\t" + txt_DetailClass.getText() + "\nAs:\t\t"
				+ txt_DetailHeadcode.getText());
	}

	public void onClassCheckboxClicked(View view) {
		CheckBox chk_DetailClass = (CheckBox) findViewById(R.id.chk_DetailClass);
		TextView txt_DetailClass = (TextView) findViewById(R.id.txt_DetailClass);
		txt_DetailClass.setEnabled(((CheckBox) chk_DetailClass).isChecked());

		Helpers.hideKeyboard(view);
	}

	public void onHeadcodeCheckboxClicked(View view) {
		CheckBox chk_DetailHeadcode = (CheckBox) findViewById(R.id.chk_DetailHeadcode);
		TextView txt_DetailHeadcode = (TextView) findViewById(R.id.txt_DetailHeadcode);
		txt_DetailHeadcode.setEnabled(((CheckBox) chk_DetailHeadcode)
				.isChecked());

		Helpers.hideKeyboard(view);
	}

	public boolean writeEntry(View view) {
		actv_FromSearch = (AutoCompleteTextView) findViewById(R.id.actv_FromSearch);
		dp_FromDate = (DatePicker) findViewById(R.id.dp_FromDate);
		tp_FromTime = (TimePicker) findViewById(R.id.tp_FromTime);

		txt_DetailClass = (TextView) findViewById(R.id.txt_DetailClass);
		txt_DetailHeadcode = (TextView) findViewById(R.id.txt_DetailHeadcode);

		actv_ToSearch = (AutoCompleteTextView) findViewById(R.id.actv_ToSearch);
		dp_ToDate = (DatePicker) findViewById(R.id.dp_ToDate);
		tp_ToTime = (TimePicker) findViewById(R.id.tp_ToTime);

		boolean mExternalStorageWritable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageWritable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageWritable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need to know is we can neither read nor write
			mExternalStorageWritable = false;
		}

		if (mExternalStorageWritable) {
			try {
				File f = new File(Helpers.dataDirectoryPath + "/routes.csv");

				if (!f.exists()) {
					f.createNewFile();
				}

				FileWriter writer = new FileWriter(f, true);

				String msep = "\",\"";
				String line = "";
				line = "\"" + actv_FromSearch.getText().toString() + msep
						+ dp_FromDate.getDayOfMonth() + msep
						+ (dp_FromDate.getMonth() + 1) + msep
						+ dp_FromDate.getYear() + msep
						+ tp_FromTime.getCurrentHour() + msep
						+ tp_FromTime.getCurrentMinute() + msep
						+ actv_ToSearch.getText().toString() + msep
						+ dp_ToDate.getDayOfMonth() + msep
						+ (dp_ToDate.getMonth() + 1) + msep
						+ dp_ToDate.getYear() + msep
						+ tp_ToTime.getCurrentHour() + msep
						+ tp_ToTime.getCurrentMinute() + msep
						+ txt_DetailClass.getText() + msep
						+ txt_DetailHeadcode.getText() + "\"";

				writer.write(line);
				writer.write(System.getProperty("line.separator"));
				writer.close();

				Toast.makeText(getBaseContext(), "Entry saved.",
						Toast.LENGTH_SHORT).show();

				chk_Checkin = (CheckBox) findViewById(R.id.chk_Checkin);
				if (chk_Checkin.isChecked()) {
					Bundle details = new Bundle();
					details.putString("from_stn", Helpers
							.trimCodeFromStation(actv_FromSearch.getText()
									.toString()));
					details.putString("to_stn", Helpers
							.trimCodeFromStation(actv_ToSearch.getText()
									.toString()));
					details.putString("detail_class", txt_DetailClass.getText()
							.toString());
					details.putString("detail_headcode", txt_DetailHeadcode
							.getText().toString());

					AddActivity.this.finish();
					Intent intent = new Intent(this, ListSavedActivity.class);
					startActivity(intent);

					foursquareCheckin(details);

				} else {
					AddActivity.this.finish();
					Intent intent = new Intent(this, ListSavedActivity.class);
					startActivity(intent);
				}

				return true;

			} catch (Exception e) {
				Toast.makeText(getBaseContext(), "Error: " + e.getMessage(),
						Toast.LENGTH_LONG).show();

				return false;
			}

		} else {
			return false;
		}
	}

	public void startClassInfoActivity(View view) {
		template = Helpers.saveCurrentJourney(AddActivity.this);
		if (template == null) {
			template = new Bundle();
		}

		Intent intent = new Intent(this, ClassInfoActivity.class);
		intent.putExtras(template);
		startActivity(intent);
	}

	private void foursquareCheckin(Bundle journey) {
		Intent intent = new Intent(this, FoursquareCheckinActivity.class);
		intent.putExtras(journey);
		startActivity(intent);
	}
}