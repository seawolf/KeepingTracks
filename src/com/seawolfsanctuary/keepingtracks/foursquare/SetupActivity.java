package com.seawolfsanctuary.keepingtracks.foursquare;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.AlertDialog.Builder;
import org.holoeverywhere.app.ProgressDialog;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.seawolfsanctuary.keepingtracks.Helpers;
import com.seawolfsanctuary.keepingtracks.MenuActivity;
import com.seawolfsanctuary.keepingtracks.R;

public class SetupActivity extends org.holoeverywhere.app.Activity {

	private static String CLIENT_ID;
	private static String CLIENT_SECRET;
	private static String REDIRECT_URI;
	private WebView wv;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.context_menu_foursquare, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.foursquare_logout:
			if (Helpers.removeAccessToken() == true) {
				SetupActivity.this.finish();
				Toast.makeText(getApplicationContext(),
						getString(R.string.foursquare_logout_success),
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(),
						getString(R.string.foursquare_logout_failure),
						Toast.LENGTH_LONG).show();
			}

			return true;
		default:
			System.out.println("Unkown action: " + item.getItemId());
			return true;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.foursquare_setup_activity);
		MenuActivity.hideLoader();

		wv = (WebView) findViewById(R.id.webview);

		ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog
				.setTitle(getString(R.string.foursquare_setup_prepare_title));
		progressDialog
				.setMessage(getString(R.string.foursquare_setup_prepare_msg));
		progressDialog.setCancelable(true);
		progressDialog.setMax(5);

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog
				.setTitle(getString(R.string.foursquare_setup_failure_title))
				.setMessage(getString(R.string.foursquare_setup_failure_msg))
				.setNeutralButton(getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								SetupActivity.this.finish();
							}
						});

		new DownloadFoursquareSecretsTask(progressDialog, alertDialog)
				.execute();

	}

	private class DownloadFoursquareSecretsTask extends
			AsyncTask<Void, String, String> {
		private ProgressDialog progressDialog;
		private Builder alertDialog;

		public DownloadFoursquareSecretsTask(ProgressDialog dialogFromActivity,
				Builder alertDialogFromActivity) {
			progressDialog = dialogFromActivity;
			alertDialog = alertDialogFromActivity;
		}

		public void onPreExecute() {
			progressDialog.show();
		}

		protected String doInBackground(Void... params) {

			CLIENT_ID = fetchClientID();
			progressDialog.incrementProgressBy(1);

			CLIENT_SECRET = fetchClientSecret();
			progressDialog.incrementProgressBy(1);

			REDIRECT_URI = fetchRedirectURI();
			progressDialog.incrementProgressBy(1);

			if (CLIENT_ID == "" || CLIENT_SECRET == "" || REDIRECT_URI == "") {
				return null;
			} else {
				progressDialog.incrementProgressBy(1);

				String authenticationURL = "https://foursquare.com/oauth2/authenticate"
						+ "?client_id="
						+ CLIENT_ID
						+ "&response_type=token"
						+ "&redirect_uri=" + REDIRECT_URI;

				progressDialog.incrementProgressBy(1);

				return authenticationURL;
			}
		}

		@SuppressLint("SetJavaScriptEnabled")
		protected void onPostExecute(String authenticationURL) {
			progressDialog.dismiss();
			if (authenticationURL == null) {
				alertDialog.show();
			} else {
				// If authentication works, we'll get redirected to a url with a
				// pattern like:
				//
				// http://YOUR_REGISTERED_REDIRECT_URI/#access_token=ACCESS_TOKEN
				//
				// We can override onPageStarted() in the web client and grab
				// the token out.
				wv.getSettings().setJavaScriptEnabled(true);
				wv.setWebViewClient(new WebViewClient() {
					public void onPageStarted(WebView view, String url,
							Bitmap favicon) {

						String fragment = "#access_token=";
						int start = url.indexOf(fragment);
						if (start > -1) {
							if (Helpers.readAccessToken() == "") {
								// Fetch an access token and reload
								String accessToken = url.substring(start
										+ fragment.length(), url.length());
								Helpers.writeAccessToken(accessToken);
								SetupActivity.this.finish();
								Toast.makeText(
										getApplicationContext(),
										getString(R.string.foursquare_setup_success),
										Toast.LENGTH_LONG).show();
							}
						}
					}
				});
				wv.loadUrl(authenticationURL);
				wv.setVisibility(View.VISIBLE);
			}
		}

		private String fetchClientID() {
			String client_id = "";
			try {
				// Create a URL for the desired page
				URL client_id_url = new URL(Helpers.foursquareClientID);

				// Read all the text returned by the server
				BufferedReader client_id_in = new BufferedReader(
						new InputStreamReader(client_id_url.openStream()));

				String str;
				while ((str = client_id_in.readLine()) != null) {
					client_id = str;
				}
				client_id_in.close();
				System.out.println("Fetched Client ID: " + client_id);
			} catch (Exception e) {
				System.err.println("Error fetching Client ID: "
						+ e.getMessage());
				e.printStackTrace();
			}

			return client_id;
		}

		private String fetchClientSecret() {
			String client_secret = "";

			try {
				// Create a URL for the desired page
				URL client_secret_url = new URL(Helpers.foursquareClientSecret);

				// Read all the text returned by the server
				BufferedReader client_secret_in = new BufferedReader(
						new InputStreamReader(client_secret_url.openStream()));
				String str;
				System.out.println("Fetching Client Secret from: "
						+ client_secret_url.toString());
				while ((str = client_secret_in.readLine()) != null) {
					client_secret = str;
				}
				client_secret_in.close();
				System.out.println("Fetched Client secret: " + client_secret);
			} catch (Exception e) {
				System.err.println("Error fetching Client secret: "
						+ e.getMessage());
				e.printStackTrace();
			}

			return client_secret;
		}

		private String fetchRedirectURI() {
			String redirect_uri = "";

			try {
				// Create a URL for the desired page
				URL redirect_uri_url = new URL(Helpers.foursquareRedirectURI);

				// Read all the text returned by the server
				BufferedReader redirect_uri_in = new BufferedReader(
						new InputStreamReader(redirect_uri_url.openStream()));
				String str;
				System.out.println("Fetching Redirect URL from: "
						+ redirect_uri_url.toString());
				while ((str = redirect_uri_in.readLine()) != null) {
					redirect_uri = str;
				}
				redirect_uri_in.close();
				System.out.println("Fetched redirect URL: " + redirect_uri);
			} catch (Exception e) {
				System.err.println("Error fetching redirect URL: "
						+ e.getMessage());
				e.printStackTrace();
			}

			return redirect_uri;
		}
	}
}