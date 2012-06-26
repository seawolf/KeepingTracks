package com.seawolfsanctuary.tmt;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Helpers {

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
}
