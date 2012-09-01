package com.seawolfsanctuary.tmt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;

public class HeadcodeSelectionActivity extends ListActivity {

	/** Called when the activity is first created. */
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.headcode_selection_activity);
		registerForContextMenu(getListView());

		ListView lv = getListView();

		String fromStation = "";
		String toStation = "";
		String hour = "";
		String minute = "";
		String year = "";
		String month = "";
		String day = "";
		Integer pageDurationHours = 2;

		String section = Integer
				.toString((Integer.parseInt(hour) / pageDurationHours));
		if (section.indexOf(".") != -1) {
			section = section.substring(0, section.indexOf("."));
		}

	}
}
