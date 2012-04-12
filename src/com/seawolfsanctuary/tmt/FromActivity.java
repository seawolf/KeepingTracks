package com.seawolfsanctuary.tmt;

import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;

public class FromActivity extends Activity {

	private String[] read_csv(String filename) {
		String[] array = {};

		try {
			InputStream input;
			input = getAssets().open("stations.lst");
			int size = input.available();
			byte[] buffer = new byte[size];

			input.read(buffer);
			input.close();
			array = new String(buffer).split("\n");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return array;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_from);
	}

}