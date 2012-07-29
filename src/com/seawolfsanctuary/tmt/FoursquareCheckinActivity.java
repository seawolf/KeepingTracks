package com.seawolfsanctuary.tmt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FoursquareCheckinActivity extends ListActivity {

	private static final String dataDirectoryPath = Environment
			.getExternalStorageDirectory().toString()
			+ "/Android/data/com.seawolfsanctuary.tmt";

	private static final String dataFilePath = Environment
			.getExternalStorageDirectory().toString()
			+ "/Android/data/com.seawolfsanctuary.tmt/routes.csv";

	private static Location location = null;

	private static boolean venuesUpdated = false;
	private static ArrayList<String> venues = new ArrayList<String>();
	private static ArrayList<String> venueIDs = new ArrayList<String>();

	private LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			System.out.println("Location: " + location);
		}

		@Override
		public void onProviderDisabled(String arg0) {
			System.out.println("Provider disabled: " + arg0);
		}

		@Override
		public void onProviderEnabled(String arg0) {
			System.out.println("Provider enabled: " + arg0);
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			System.out.println("Status Changed: " + arg0 + " | " + arg1);
		}
	};

	private static Location objLocation(LocationManager lm) {
		System.out.println("Starting location services...");

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);

		String provider = lm.getBestProvider(criteria, true);
		Location location = lm.getLastKnownLocation(provider);

		return location;
	}

	private static String askFoursquareForVenuesAt(Location location)
			throws ClientProtocolException, IOException {
		// initialize
		InputStream is = null;
		String result = "";
		String url = "https://api.foursquare.com/v2/venues/search?v=20120728&oauth_token="
				+ readAccessToken()
				+ "&ll="
				+ location.getLatitude()
				+ ","
				+ location.getLongitude();

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get user's location
		location = objLocation((LocationManager) getSystemService(Context.LOCATION_SERVICE));

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
				Toast.makeText(
						getApplicationContext(),
						"You've selected #" + (position + 1) + ": "
								+ ((TextView) view).getText(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}
}