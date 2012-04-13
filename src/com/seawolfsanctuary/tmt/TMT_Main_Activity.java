package com.seawolfsanctuary.tmt;

import java.io.InputStream;

import android.app.TabActivity;
import android.content.res.Resources;
import android.os.Bundle;
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

public class TMT_Main_Activity extends TabActivity {

	TextView txt_FromSearch, txt_FromSummary;
	DatePicker dp_FromDate;
	TimePicker tp_FromTime;
	AutoCompleteTextView actv_FromSearch;

	CheckBox cb_DetailClass;
	TextView txt_DetailClass;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Resources res = getResources();
		TabHost mTabHost = getTabHost();

		mTabHost.addTab(mTabHost.newTabSpec("tc_From").setIndicator("From")
				.setContent(R.id.tc_From));
		mTabHost.addTab(mTabHost.newTabSpec("tc_Detail").setIndicator("Detail")
				.setContent(R.id.tc_Detail));
		mTabHost.addTab(mTabHost.newTabSpec("tc_To").setIndicator("To")
				.setContent(R.id.tc_To));

		mTabHost.setCurrentTab(0);

		actv_FromSearch = (AutoCompleteTextView) findViewById(R.id.actv_FromSearch);
		txt_FromSummary = (TextView) findViewById(R.id.txt_FromSummary);
		dp_FromDate = (DatePicker) findViewById(R.id.dp_FromDate);
		tp_FromTime = (TimePicker) findViewById(R.id.tp_FromTime);

		// Link array of completions
		String[] completions = read_csv("stations.lst");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, completions);
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

		} catch (Exception e) {
			String error_msg = "Error reading station list!";
			actv_FromSearch = (AutoCompleteTextView) findViewById(R.id.actv_FromSearch);
			actv_FromSearch.setText(error_msg);
			actv_FromSearch.setError(error_msg);
			actv_FromSearch.setEnabled(false);
		}

		Toast.makeText(getBaseContext(), "Stations loaded.", Toast.LENGTH_SHORT)
				.show();

		return array;
	}

	public void updateText() {
		actv_FromSearch = (AutoCompleteTextView) findViewById(R.id.actv_FromSearch);
		txt_FromSummary = (TextView) findViewById(R.id.txt_FromSummary);
		dp_FromDate = (DatePicker) findViewById(R.id.dp_FromDate);
		tp_FromTime = (TimePicker) findViewById(R.id.tp_FromTime);
		txt_DetailClass = (TextView) findViewById(R.id.txt_DetailClass);

		txt_FromSummary.setText("You selected:" + "\nFrom:"
				+ actv_FromSearch.getText().toString() + "\nOn: "
				+ dp_FromDate.getDayOfMonth() + "/" + dp_FromDate.getMonth()
				+ "/" + dp_FromDate.getYear() + "\nAt: "
				+ tp_FromTime.getCurrentHour() + ":"
				+ tp_FromTime.getCurrentMinute());
	}

	public void onCheckboxClicked(View view) {
		CheckBox cb_DetailClass = (CheckBox) findViewById(R.id.cb_DetailClass);
		TextView txt_DetailClass = (TextView) findViewById(R.id.txt_DetailClass);
		txt_DetailClass.setEnabled(((CheckBox) cb_DetailClass).isChecked());

		Helpers.hideKeyboard(view);
	}
}