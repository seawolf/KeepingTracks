package com.seawolfsanctuary.keepingtracks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class Helpers {

	public static final String dataDirectoryPath = Environment
			.getExternalStorageDirectory().toString()
			+ "/Android/data/com.seawolfsanctuary.keepingtracks";

	public static final String dataDirectoryURI = Environment
			.getExternalStorageDirectory().toURI()
			+ "/Android/data/com.seawolfsanctuary.keepingtracks";

	public static final String exportDirectoryPath = Environment
			.getExternalStorageDirectory().toString();

	public static final String classInfoPhotosURI = "http://dl.dropbox.com/u/6413248/KeepingTracks/class_photos/";
	public static final String classInfoThumbsURI = "http://dl.dropbox.com/u/6413248/KeepingTracks/class_photos/thumbs/";

	public static final String foursquareClientID = "http://keepingtracks.seawolfsanctuary.com/app/foursquare/client_id";
	public static final String foursquareClientSecret = "http://keepingtracks.seawolfsanctuary.com/app/foursquare/client_secret";
	public static final String foursquareRedirectURI = "http://keepingtracks.seawolfsanctuary.com/app/foursquare/redirect_uri";

	public static void hideKeyboard(View view) {
		try {
			InputMethodManager imm = (InputMethodManager) view.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		} catch (Exception e) {
			// couldn't close keyboards
		}
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
		return String.format("%" + width + "s", s).replace(" ", "0");
	}

	public static String rightPad(String s, int width) {
		return String.format("%-" + width + "s", s).replace(" ", "0");
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
			System.out.println("Error reading access token: " + e.getMessage());
		}

		return accessToken;
	}

	public static boolean writeAccessToken(String accessToken) {
		boolean success = false;
		try {
			new File(dataDirectoryPath).mkdirs();

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
			System.out.println("Error writing access token: " + e.getMessage());
		}

		return success;
	}

	public static boolean removeAccessToken() {
		boolean success = false;

		try {
			File f = new File(dataDirectoryPath + "/access_token.txt");
			success = f.delete();
		} catch (Exception e) {
			System.out
					.println("Error removing access token: " + e.getMessage());
		}

		return success;
	}

	public static String trimCodeFromStation(String station, Context c) {
		if (station.length() > 4) {
			if (station.substring(0, 4).matches("([A-Z]){3} ")) {
				station = station.substring(4);
			}
		} else if (station.length() == 0) {
			station = c.getString(R.string.none);
		}
		return station;
	}

	public static String trimNameFromStation(String station, Context c) {
		if (station.length() > 3) {
			if (station.substring(0, 3).matches("([A-Z]){3}")) {
				station = station.substring(0, 3);
			}
		} else if (station.length() == 0) {
			station = c.getString(R.string.none);
		}
		return station;
	}

	public static String nameAndCodeFromStation(String station, Context c) {
		if (station.length() > 4) {
			if (station.substring(0, 4).matches("([A-Z]){3} ")) {
				station = trimCodeFromStation(station, c) + " ("
						+ trimNameFromStation(station, c) + ")";
			}
		} else if (station.length() == 0) {
			station = c.getString(R.string.none);
		}
		return station;
	}

	/* returns [best code, best display of station in text field] */
	public static String[] codeAndStationFromCode(String input,
			String[] completions, Context c) {
		String[] bestGuess = new String[] { input.toUpperCase(), input };

		input = input.toUpperCase();
		for (int i = 0; i < completions.length; i++) {
			String codeAndStation = completions[i];

			if (input.equals(Helpers.trimNameFromStation(codeAndStation, c))) {
				bestGuess[0] = input;
				bestGuess[1] = codeAndStation;
			}
		}

		return bestGuess;
	}

	/* returns [best code, best display of station in text field] */
	public static String[] codeAndStationFromStation(String input,
			String[] completions, Context c) {
		String[] bestGuess = new String[] { input.toUpperCase(), input };

		input = input.toUpperCase();
		for (int i = completions.length - 1; i >= 0; i--) {
			String codeAndStation = completions[i];
			if (input.equals(Helpers.trimCodeFromStation(codeAndStation, c)
					.toUpperCase())) {
				bestGuess[0] = input;
				bestGuess[1] = codeAndStation;
			}
		}

		return bestGuess;
	}

	public static String trimCategoryFromPlace(String placeName, Context c) {
		if (placeName.length() > 0 && placeName.contains(": ")) {
			String category = placeName.split(":")[0];
			placeName = placeName.replace(category + ": ", "");
		}
		return placeName;
	}

	public static Bundle saveCurrentJourney(Bundle previousBundle, Activity src) {
		Bundle journey = new Bundle();

		// Static

		if (previousBundle.containsKey("editing")) {
			journey.putBoolean("editing", previousBundle.getBoolean("editing"));
		}

		if (previousBundle.containsKey("id")) {
			journey.putInt("id", previousBundle.getInt("id"));
		}

		// Possibly Updated in View

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

		CheckBox chk_DetailClass = (CheckBox) src
				.findViewById(R.id.chk_DetailClass);
		TextView txt_DetailClass = (TextView) src
				.findViewById(R.id.txt_DetailClass);
		CheckBox chk_DetailHeadcode = (CheckBox) src
				.findViewById(R.id.chk_DetailHeadcode);
		TextView txt_DetailHeadcode = (TextView) src
				.findViewById(R.id.txt_DetailHeadcode);
		CheckBox chk_DetailUseForStats = (CheckBox) src
				.findViewById(R.id.chk_DetailUseForStats);
		journey.putBoolean("detail_class_enabled", chk_DetailClass.isChecked());
		journey.putString("detail_class", txt_DetailClass.getText().toString());
		journey.putBoolean("detail_headcode_checked",
				chk_DetailHeadcode.isChecked());
		journey.putString("detail_headcode", txt_DetailHeadcode.getText()
				.toString());
		journey.putBoolean("detail_use_for_stats",
				chk_DetailUseForStats.isChecked());

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
		if (journey != null) {
			if (journey.containsKey("from_stn")) {
				if (journey.getString("from_stn").length() > 0) {
					AutoCompleteTextView actv_FromSearch = (AutoCompleteTextView) dest
							.findViewById(R.id.actv_FromSearch);
					actv_FromSearch.setText(journey.getString("from_stn"));
				}
			}

			if (journey.containsKey("from_date_year")) {
				if (journey.getInt("from_date_year")
						+ journey.getInt("from_date_month")
						+ journey.getInt("from_date_day") > 0) {
					DatePicker dp_FromDate = (DatePicker) dest
							.findViewById(R.id.dp_FromDate);
					dp_FromDate.init(journey.getInt("from_date_year"),
							journey.getInt("from_date_month") - 1,
							journey.getInt("from_date_day"), null);
				}
			}

			if (journey.containsKey("from_time_hour")) {
				if (journey.getInt("from_time_hour")
						+ journey.getInt("from_time_minute") > 0) {
					TimePicker tp_FromTime = (TimePicker) dest
							.findViewById(R.id.tp_FromTime);
					tp_FromTime
							.setCurrentHour(journey.getInt("from_time_hour"));
					tp_FromTime.setCurrentMinute(journey
							.getInt("from_time_minute"));
				}
			}

			if (journey.containsKey("detail_class_checked")) {
				CheckBox chk_DetailClass = (CheckBox) dest
						.findViewById(R.id.chk_DetailClass);
				chk_DetailClass.setChecked(journey
						.getBoolean("detail_class_checked"));

				TextView txt_DetailClass = (TextView) dest
						.findViewById(R.id.txt_DetailClass);
				txt_DetailClass.setEnabled(journey
						.getBoolean("detail_class_checked"));
			}

			if (journey.containsKey("detail_class")) {
				if (journey.getString("detail_class").length() > 0) {
					TextView txt_DetailClass = (TextView) dest
							.findViewById(R.id.txt_DetailClass);
					txt_DetailClass.setText(journey.getString("detail_class"));
				}
			}

			if (journey.containsKey("detail_headcode_checked")) {
				CheckBox chk_DetailHeadcode = (CheckBox) dest
						.findViewById(R.id.chk_DetailHeadcode);
				chk_DetailHeadcode.setChecked(journey
						.getBoolean("detail_headcode_checked"));

				TextView txt_DetailHeadcode = (TextView) dest
						.findViewById(R.id.txt_DetailHeadcode);
				txt_DetailHeadcode.setEnabled(journey
						.getBoolean("detail_headcode_checked"));
			}

			if (journey.containsKey("detail_headcode")) {
				if (journey.getString("detail_headcode").length() > 0) {
					TextView txt_DetailHeadcode = (TextView) dest
							.findViewById(R.id.txt_DetailHeadcode);
					txt_DetailHeadcode.setText(journey
							.getString("detail_headcode"));
				}
			}
			if (journey.containsKey("detail_use_for_stats")) {
				CheckBox chk_DetailHeadcode = (CheckBox) dest
						.findViewById(R.id.chk_DetailUseForStats);
				chk_DetailHeadcode.setChecked(journey
						.getBoolean("detail_use_for_stats"));
			}

			if (journey.containsKey("to_stn")) {
				if (journey.getString("to_stn").length() > 0) {
					AutoCompleteTextView actv_ToSearch = (AutoCompleteTextView) dest
							.findViewById(R.id.actv_ToSearch);
					actv_ToSearch.setText(journey.getString("to_stn"));
				}
			}

			if (journey.containsKey("to_date_year")) {
				if (journey.getInt("to_date_year")
						+ journey.getInt("to_date_month")
						+ journey.getInt("to_date_day") > 0) {
					DatePicker dp_ToDate = (DatePicker) dest
							.findViewById(R.id.dp_ToDate);
					dp_ToDate.init(journey.getInt("to_date_year"),
							journey.getInt("to_date_month") - 1,
							journey.getInt("to_date_day"), null);
				}
			}

			if (journey.containsKey("to_time_hour")) {
				if (journey.getInt("to_time_hour")
						+ journey.getInt("to_time_minute") > 0) {

					TimePicker tp_ToTime = (TimePicker) dest
							.findViewById(R.id.tp_ToTime);
					tp_ToTime.setCurrentHour(journey.getInt("to_time_hour"));
					tp_ToTime
							.setCurrentMinute(journey.getInt("to_time_minute"));
				}
			}
		}
	}

	public static boolean isLocationEnabledNetwork(Context c) {
		LocationManager lm = (LocationManager) c
				.getSystemService(Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	public static boolean isLocationEnabledGPS(Context c) {
		LocationManager lm = (LocationManager) c
				.getSystemService(Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	public static String guageSizeToName(String guage) {
		String result = guage;

		String[] definitions = new String[] { "broad", "standard", "medium",
				"metre", "narrow", "minumum" };
		Hashtable<String, String> relationships = new Hashtable<String, String>();
		relationships.put("1435", definitions[0]);

		if (guage == "") {
			result = "(unknown)";
		}
		if (relationships.containsKey(guage)) {
			result = relationships.get(guage);
		}

		return result;
	}

	public static File dirAt(String path, boolean removeIfExists)
			throws IOException {
		File d = new File(path);

		if (removeIfExists == true && d.exists()) {
			d.delete();
		}

		d.mkdirs();

		return d;
	}

	public static File fileAt(String path, String filename,
			boolean removeIfExists) throws IOException {
		File d = dirAt(path, removeIfExists);
		File f = new File(d.getPath() + "/" + filename);

		if (removeIfExists == true && f.exists()) {
			f.delete();
		}

		if (!f.exists()) {
			f.createNewFile();
		}

		return f;
	}

	public static String fetchData(String url) throws Exception {
		System.out.println("Fetching data from:");
		System.out.println(url);
		InputStream inputStream = null;
		String result = "";
		Exception error = null;

		try {
			DefaultHttpClient httpclient = new DefaultHttpClient(
					new BasicHttpParams());
			HttpGet httpget = new HttpGet(new URL(url).toURI());
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();
		} catch (Exception e) {
			error = e;
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (Exception squish) {
				//
			}
		}

		if (error != null) {
			throw error;
		}

		return result;
	}
}
