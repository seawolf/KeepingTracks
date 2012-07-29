package com.seawolfsanctuary.tmt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FoursquareCheckinActivity extends ListActivity {

	private static boolean venuesUpdated = false;
	private static ArrayList<String> venues = new ArrayList<String>();
	private static ArrayList<String> venueIDs = new ArrayList<String>();

	private static LocationManager locationManager = null;
	private static Location location = null;

	private static Location objLocation() {
		System.out.println("Starting location services...");

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);

		String provider = locationManager.getBestProvider(criteria, true);
		Location location = locationManager.getLastKnownLocation(provider);

		return location;
	}

	private static String askFoursquareForVenuesAt(Location location)
			throws ClientProtocolException, IOException {
		// initialize
		InputStream is = null;
		String result = "";
		String url = "https://api.foursquare.com/v2/venues/search?v=20120728&oauth_token="
				+ Helpers.readAccessToken()
				+ "&ll="
				+ location.getLatitude()
				+ "," + location.getLongitude();

		// http get
		System.out.println("Contacting Foursquare...");
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		is = entity.getContent();

		// convert response to string
		System.out.println("Recieving response...");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"utf-8"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		result = sb.toString();

		return result;
	}

	private JSONArray parseFoursquareSearchResponse(String result)
			throws JSONException {
		JSONArray returnedVenues = null;

		System.out.println("Parsing response...");
		JSONObject jArray = new JSONObject(result);

		System.out.println("Selecting response...");
		JSONObject response = jArray.getJSONObject("response");
		returnedVenues = response.getJSONArray("venues");

		return returnedVenues;
	}

	private boolean updateVenuesFromSearch(JSONArray returnedVenues)
			throws JSONException {
		boolean success = false;

		venues.clear();
		venueIDs.clear();

		System.out.println("Parsing venues...");
		for (int i = 0; i < returnedVenues.length(); i++) {
			JSONObject e = returnedVenues.getJSONObject(i);
			String venueName = e.getString("name");

			String venueCategory = "Uncategorised";
			JSONArray venueCategories = e.getJSONArray("categories");
			for (int cc = 0; cc < venueCategories.length(); cc++) {
				JSONObject c = venueCategories.getJSONObject(cc);
				String categoryPrimary = c.getString("primary");
				if (categoryPrimary == "true") {
					venueCategory = c.getString("name");
				}
			}

			System.out.println("Adding venue: " + venueName);
			venues.add(venueCategory + ": " + venueName);
			venueIDs.add(e.getString("id"));
			success = true;
		}

		return success;
	}

	private void searchForVenues() {
		try {

			// Get user's location:

			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			location = objLocation();

			venuesUpdated = false;
			String result = askFoursquareForVenuesAt(location);
			JSONArray returnedVenues = parseFoursquareSearchResponse(result);
			venuesUpdated = updateVenuesFromSearch(returnedVenues);

		} catch (ClientProtocolException e) {
			Toast.makeText(getApplicationContext(),
					"Unable to connect to the Internet.", Toast.LENGTH_SHORT)
					.show();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(),
					"Unable to connect to the Internet.", Toast.LENGTH_SHORT)
					.show();
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(),
					"Foursquare sent me something I couldn't understand.",
					Toast.LENGTH_SHORT).show();
		}
	}

	private static String tryToCheckinTo(String venueID, String broadcast)
			throws ClientProtocolException, IOException {
		// initialize
		InputStream is = null;
		String result = "";
		String url = "https://api.foursquare.com/v2/checkins/add?v=20120728&oauth_token="
				+ Helpers.readAccessToken()
				+ "&venueId="
				+ venueID
				+ "&ll="
				+ location.getLatitude()
				+ ","
				+ location.getLongitude()
				+ "&broadcast=" + broadcast;

		System.out.println("URL: " + url);

		// http post
		System.out.println("Contacting Foursquare...");
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		HttpResponse response = httpclient.execute(httpPost);
		HttpEntity entity = response.getEntity();
		is = entity.getContent();

		// convert response to string
		System.out.println("Recieving response...");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"utf-8"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		result = sb.toString();

		System.out.println(result);

		return result;
	}

	private int parseFoursquareCheckinResponse(String result)
			throws JSONException {
		int returnedStatus = -1;

		System.out.println("Parsing response...");
		JSONObject response = new JSONObject(result);

		System.out.println("Selecting response...");
		JSONObject meta = response.getJSONObject("meta");

		returnedStatus = meta.getInt("code");

		return returnedStatus;
	}

	private boolean wasCheckinSuccessful(int returnedStatus)
			throws JSONException {
		boolean success = false;

		System.out.println("Parsing checkin response: "
				+ Integer.toString(returnedStatus));
		success = (returnedStatus == 200);

		return success;
	}

	private boolean checkIntoVenue(String venueID) {
		boolean success = false;

		try {

			venuesUpdated = false;
			String result = tryToCheckinTo(venueID, "private");
			int returnedStatus = parseFoursquareCheckinResponse(result);
			success = wasCheckinSuccessful(returnedStatus);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return success;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.foursquare_context_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.foursquare_deauthenticate:
			if (Helpers.removeAccessToken() == true) {
				FoursquareCheckinActivity.this.finish();
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
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Ask Foursquare for venues
		searchForVenues();

		if (venuesUpdated) {
			Toast.makeText(getApplicationContext(), "Updated venues.",
					Toast.LENGTH_SHORT).show();
		}

		setContentView(R.layout.foursquare_checkin_activity);
		setListAdapter(new ArrayAdapter<String>(this,
				R.layout.foursquare_checkin_venue, venues));

		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				boolean checkinSuccessful = checkIntoVenue(venueIDs
						.get(position));
				if (checkinSuccessful == true) {
					Toast.makeText(getApplicationContext(),
							"Checked into " + venues.get(position) + "!",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(
							getApplicationContext(),
							"Could not check into " + venues.get(position)
									+ ".", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void removeFoursquareAuthentication() {
		Helpers.removeAccessToken();
	}
}
