package com.seawolfsanctuary.tmt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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
}
