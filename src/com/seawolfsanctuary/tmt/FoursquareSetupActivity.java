package com.seawolfsanctuary.tmt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class FoursquareSetupActivity extends Activity {

	public static final String dataDirectoryPath = Environment
			.getExternalStorageDirectory().toString()
			+ "/Android/data/com.seawolfsanctuary.tmt";
	public static final String dataDirectoryURI = "file:///sdcard/Android/data/com.seawolfsanctuary.tmt";

	private static final String CLIENT_ID = fetchClientID();
	private static final String CLIENT_SECRET = fetchClientSecret();
	private static final String REDIRECT_URI = fetchRedirectURI();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.foursquare_context_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.foursquare_deauthenticate:
			if (removeAccessToken() == true) {
				FoursquareSetupActivity.this.finish();
				Toast.makeText(
						getApplicationContext(),
						"You must now re-authenticate with Foursquare to check-in.",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(
						getApplicationContext(),
						"Could not revoke access. Remove this application from your account by visiting the Foursquare website.",
						Toast.LENGTH_LONG).show();
			}

			return true;
		default:
			System.out.println("Unkown action: " + item.getItemId());
			return true;
		}
	}

	/** Called when the activity is first created. */
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.foursquare_setup_activity);

		String authentication_url = "https://foursquare.com/oauth2/authenticate"
				+ "?client_id="
				+ CLIENT_ID
				+ "&response_type=token"
				+ "&redirect_uri=" + REDIRECT_URI;

		// If authentication works, we'll get redirected to a url with a pattern
		// like:
		//
		// http://YOUR_REGISTERED_REDIRECT_URI/#access_token=ACCESS_TOKEN
		//
		// We can override onPageStarted() in the web client and grab the token
		// out.
		WebView webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient() {
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				String fragment = "#access_token=";
				int start = url.indexOf(fragment);
				if (start > -1) {

					if (readAccessToken() == "") {
						// Fetch an access token and reload
						String accessToken = url.substring(
								start + fragment.length(), url.length());
						writeAccessToken(accessToken);
						reloadActivity();
					} else {
						Toast.makeText(FoursquareSetupActivity.this,
								"Saved Token: " + readAccessToken(),
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		webview.loadUrl(authentication_url);

	}

	private static String fetchClientID() {
		String client_id = "";

		try {
			// Create a URL for the desired page
			URL client_id_url = new URL(
					"http://dl.dropbox.com/u/6413248/client_id.txt");

			// Read all the text returned by the server
			BufferedReader client_id_in = new BufferedReader(
					new InputStreamReader(client_id_url.openStream()));
			String str;
			while ((str = client_id_in.readLine()) != null) {
				client_id = str;
			}
			client_id_in.close();
		} catch (Exception e) {
			//
		}

		return client_id;
	}

	private static String fetchClientSecret() {
		String client_secret = "";

		try {
			// Create a URL for the desired page
			URL client_secret_url = new URL(
					"http://dl.dropbox.com/u/6413248/client_secret.txt");

			// Read all the text returned by the server
			BufferedReader client_secret_in = new BufferedReader(
					new InputStreamReader(client_secret_url.openStream()));
			String str;
			while ((str = client_secret_in.readLine()) != null) {
				client_secret = str;
			}
			client_secret_in.close();
		} catch (Exception e) {
			//
		}

		return client_secret;
	}

	private static String fetchRedirectURI() {
		String redirect_uri = "";

		try {
			// Create a URL for the desired page
			URL redirect_uri_url = new URL(
					"http://dl.dropbox.com/u/6413248/redirect_uri.txt");

			// Read all the text returned by the server
			BufferedReader redirect_uri_in = new BufferedReader(
					new InputStreamReader(redirect_uri_url.openStream()));
			String str;
			while ((str = redirect_uri_in.readLine()) != null) {
				redirect_uri = str;
			}
			redirect_uri_in.close();
		} catch (Exception e) {
			//
		}

		return redirect_uri;
	}

	public void authenticate(View v) {
		try {

			String client_id = fetchClientID();
			String client_secret = fetchClientSecret();
			String redirect_uri = fetchRedirectURI();

			System.out.println("Client ID: " + client_id);
			System.out.println("Client Secret: " + client_secret);
			System.out.println("Redirect URI: " + redirect_uri);

			String authentication_url = "https://foursquare.com/oauth2/authenticate?client_id="
					+ client_id
					+ "&response_type=token&redirect_uri="
					+ redirect_uri;

		} catch (Exception e) {
			//
		}
	}

	private boolean writeAccessToken(String accessToken) {
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

			Toast.makeText(FoursquareSetupActivity.this,
					"Saved Token: " + readAccessToken(), Toast.LENGTH_SHORT)
					.show();
			return true;

		} catch (Exception e) {
			System.out.println(e.getMessage());
			Toast.makeText(getBaseContext(), "Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();

			return false;
		}

	}

	public String readAccessToken() {
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

	private static boolean removeAccessToken() {
		boolean success = false;

		try {
			File f = new File(dataDirectoryPath + "/access_token.txt");
			success = f.delete();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return success;
	}

	private void reloadActivity() {
		Intent intent = new Intent(this, FoursquareSetupActivity.class);
		FoursquareSetupActivity.this.finish();
		startActivity(intent);
	}
}