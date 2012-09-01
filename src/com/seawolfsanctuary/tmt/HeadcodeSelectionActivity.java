package com.seawolfsanctuary.tmt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HeadcodeSelectionActivity extends Activity {

	/** Called when the activity is first created. */
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.headcode_selection_activity);

		WebView webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient());

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

		String url = "http://trains.im/location/" + fromStation + "/" + year
				+ "/" + month + "/" + day + "/" + section + "/" + toStation;

		webview.loadUrl(url);
	}

}
