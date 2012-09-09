package com.seawolfsanctuary.tmt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class Helpers {

	public static final String dataDirectoryPath = Environment
			.getExternalStorageDirectory().toString()
			+ "/Android/data/com.seawolfsanctuary.tmt";

	public static final String dataDirectoryURI = "file:///sdcard/Android/data/com.seawolfsanctuary.tmt";

	public static void hideKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static String trimCSVSpeech(String input) {
		if (input.startsWith("" + '"') && input.endsWith("" + '"')) {
			return input.substring(1, input.length() - 1);
		} else {
			return input;
		}
	}

	public static String[] arrayListToArray(ArrayList<String> input) {
		String[] returnedArray = new String[input.size()];
		for (int i = 0; i < input.size(); i++) {
			returnedArray[i] = input.get(i);
		}
		return returnedArray;
	}

	public static String[][] multiArrayListToArray(
			ArrayList<ArrayList<String>> input) {
		String[][] returnedArray = new String[input.size()][];
		for (int i = 0; i < input.size(); i++) {
			returnedArray[i] = arrayListToArray(input.get(i));
		}
		return returnedArray;
	}

	public static String leftPad(String s, int width) {
		return String.format("%" + width + "s", s).replace(' ', '0');
	}

	public static String rightPad(String s, int width) {
		return String.format("%-" + width + "s", s).replace(' ', '0');
	}

	public static String readAccessToken() {
		String accessToken = "";

		try {
			String line = null;
			File f = new File(dataDirectoryPath + "/access_token.txt");

			BufferedReader reader = new BufferedReader(new FileReader(f));

			while ((line = reader.readLine()) != null) {
				accessToken = line;
			}
			reader.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return accessToken;
	}

	public static boolean writeAccessToken(String accessToken) {
		boolean success = false;
		try {

			File f = new File(dataDirectoryPath + "/access_token.txt");

			if (f.exists()) {
				f.delete();
			}

			if (!f.exists()) {
				f.createNewFile();
			}

			FileWriter writer = new FileWriter(f, true);

			writer.write(accessToken);
			writer.close();

			success = true;

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return success;
	}

	public static boolean removeAccessToken() {
		boolean success = false;

		try {
			File f = new File(dataDirectoryPath + "/access_token.txt");
			success = f.delete();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return success;
	}

	public static String trimCodeFromStation(String station) {
		if (station.length() > 4) {
			station = station.substring(4);
		}
		return station;
	}

	public static Bundle saveCurrentJourney(Activity src) {
		Bundle journey = new Bundle();

		AutoCompleteTextView actv_FromSearch = (AutoCompleteTextView) src
				.findViewById(R.id.actv_FromSearch);
		DatePicker dp_FromDate = (DatePicker) src
				.findViewById(R.id.dp_FromDate);
		TimePicker tp_FromTime = (TimePicker) src
				.findViewById(R.id.tp_FromTime);
		journey.putString("from_stn", actv_FromSearch.getText().toString());
		journey.putInt("from_date_day", dp_FromDate.getDayOfMonth());
		journey.putInt("from_date_month", dp_FromDate.getMonth());
		journey.putInt("from_date_year", dp_FromDate.getYear());
		journey.putInt("from_time_hour", tp_FromTime.getCurrentHour());
		journey.putInt("from_time_minute", tp_FromTime.getCurrentMinute());

		TextView txt_DetailClass = (TextView) src
				.findViewById(R.id.txt_DetailClass);
		TextView txt_DetailHeadcode = (TextView) src
				.findViewById(R.id.txt_DetailHeadcode);
		journey.putCharSequence("detail_class", txt_DetailClass.getText());
		journey.putCharSequence("detail_headcode", txt_DetailHeadcode.getText());

		AutoCompleteTextView actv_ToSearch = (AutoCompleteTextView) src
				.findViewById(R.id.actv_ToSearch);
		DatePicker dp_ToDate = (DatePicker) src.findViewById(R.id.dp_ToDate);
		TimePicker tp_ToTime = (TimePicker) src.findViewById(R.id.tp_ToTime);
		journey.putString("to_stn", actv_ToSearch.getText().toString());
		journey.putInt("to_date_day", dp_ToDate.getDayOfMonth());
		journey.putInt("to_date_month", dp_ToDate.getMonth());
		journey.putInt("to_date_year", dp_ToDate.getYear());
		journey.putInt("to_time_hour", tp_ToTime.getCurrentHour());
		journey.putInt("to_time_minute", tp_ToTime.getCurrentMinute());

		return journey;
	}

	public static void loadCurrentJourney(Bundle journey, Activity dest) {
		try {
			AutoCompleteTextView actv_FromSearch = (AutoCompleteTextView) dest
					.findViewById(R.id.actv_FromSearch);
			actv_FromSearch.setText(journey.getString("from_stn"));

			DatePicker dp_FromDate = (DatePicker) dest
					.findViewById(R.id.dp_FromDate);
			dp_FromDate.init(journey.getInt("from_date_year"),
					journey.getInt("from_date_month"),
					journey.getInt("from_date_day"), null);

			TimePicker tp_FromTime = (TimePicker) dest
					.findViewById(R.id.tp_FromTime);
			tp_FromTime.setCurrentHour(journey.getInt("from_time_hour"));
			tp_FromTime.setCurrentMinute(journey.getInt("from_time_minute"));

			TextView txt_DetailClass = (TextView) dest
					.findViewById(R.id.txt_DetailClass);
			txt_DetailClass.setText(journey.getCharSequence("detail_class"));

			TextView txt_DetailHeadcode = (TextView) dest
					.findViewById(R.id.txt_DetailHeadcode);
			txt_DetailHeadcode.setText(journey
					.getCharSequence("detail_headcode"));

			AutoCompleteTextView actv_ToSearch = (AutoCompleteTextView) dest
					.findViewById(R.id.actv_ToSearch);
			actv_ToSearch.setText(journey.getString("to_stn"));

			DatePicker dp_ToDate = (DatePicker) dest
					.findViewById(R.id.dp_ToDate);
			dp_ToDate.init(journey.getInt("to_date_year"),
					journey.getInt("to_date_month"),
					journey.getInt("to_date_day"), null);

			TimePicker tp_ToTime = (TimePicker) dest
					.findViewById(R.id.tp_ToTime);
			tp_ToTime.setCurrentHour(journey.getInt("to_time_hour"));
			tp_ToTime.setCurrentMinute(journey.getInt("to_time_minute"));
		} catch (Exception e) {
		}
	}
}
