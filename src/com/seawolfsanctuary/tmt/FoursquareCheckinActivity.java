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
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FoursquareCheckinActivity extends ListActivity {

	private static final int LOOKUP_LIFETIME = 1000 * 30;
	private static boolean initialLookup = false;
	private static boolean venuesUpdated = false;
	private static ArrayList<String> venues = new ArrayList<String>();
	private static ArrayList<String> venueIDs = new ArrayList<String>();

	private static int locationUpdateStatus = 0;
	private static LocationManager locationManager = null;
	private static Location location = null;
	// Define a listener that responds to location updates
	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location newLocation) {
			// Called when a new location is found
			Toast.makeText(
					getApplicationContext(),
					"New Location from " + newLocation.getProvider() + " at "
							+ newLocation.getTime(), Toast.LENGTH_SHORT).show();

			if (initialLookup || isBetterLocation(newLocation, location)) {

				String message = "...";
				switch (locationUpdateStatus) {
				case -2:
					message = "Not worthy of an update.";
					break;
				case -1:
					message = "Significantly older.";
					break;
				case 0:
					message = "Current location unknown. Please switch on GPS.";
					break;
				case 1:
					message = "Significantly newer.";
					break;
				case 2:
					message = "More accurate.";
					break;
				case 3:
					message = "Significantly newer and not less accurate.";
					break;
				case 4:
					message = "Signifcantly newer, not significantly less accurate, from same provider.";
					break;
				default:
					message = "Current location unknown. Please switch on GPS.";
					break;
				}

				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_SHORT).show();

				initialLookup = false;
				location = newLocation;
				new SearchVenuesTask().execute(newLocation);
			}

			if (venuesUpdated) {
				Toast.makeText(getApplicationContext(), "Updated venues.",
						Toast.LENGTH_SHORT).show();
			}

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	};

	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			locationUpdateStatus = 0;
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > LOOKUP_LIFETIME;
		boolean isSignificantlyOlder = timeDelta < -LOOKUP_LIFETIME;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			locationUpdateStatus = 1;
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			locationUpdateStatus = -1;
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			locationUpdateStatus = 2;
			return true;
		} else if (isSignificantlyNewer && !isLessAccurate) {
			locationUpdateStatus = 3;
			return true;
		} else if (isSignificantlyNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			locationUpdateStatus = 4;
			return true;
		}

		locationUpdateStatus = -2;
		return false;
	}

	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	private boolean searchForVenues() throws ClientProtocolException,
			IOException, JSONException {
		System.out.println("Searching for venues...");
		boolean success = false;

		venuesUpdated = false;
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
		HttpResponse httpResponse = httpclient.execute(httpGet);
		HttpEntity entity = httpResponse.getEntity();
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

		JSONArray returnedVenues = null;

		System.out.println("Parsing response...");
		JSONObject jArray = new JSONObject(result);

		JSONObject response = jArray.getJSONObject("response");
		returnedVenues = response.getJSONArray("venues");

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
		}

		success = true;

		return success;
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
			System.out.println("Unknown action: " + item.getItemId());
			return true;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.foursquare_checkin_activity);
		setListAdapter(new ArrayAdapter<String>(this,
				R.layout.foursquare_checkin_venue, venues));

		initialLookup = true;
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// Get user's location:
		System.out.println("Starting location services...");
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = locationManager.getBestProvider(criteria, true);
		Toast.makeText(getApplicationContext(),
				"Updated location based on " + provider, Toast.LENGTH_SHORT)
				.show();

		// Register the listener with the Location Manager to receive updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				5000, 0, locationListener);

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

	private class SearchVenuesTask extends AsyncTask<Location, Void, Location> {
		/**
		 * The system calls this to perform work in a worker thread and delivers
		 * it the parameters given to AsyncTask.execute()
		 */
		@Override
		protected Location doInBackground(Location... newLocations) {
			try {

				searchForVenues();

			} catch (ClientProtocolException e) {
				System.out.println("Unable to connect to the Internet.");
			} catch (IOException e) {
				System.out.println("Unable to connect to the Internet.");
			} catch (JSONException e) {
				System.out
						.println("Foursquare sent me something I couldn't understand.");
			}

			return newLocations[0];
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		protected void onPostExecute(Location newLocation) {

			Toast.makeText(getApplicationContext(), "Updated venues.",
					Toast.LENGTH_SHORT).show();
			ListView lst_Venues = getListView();
			ListAdapter lst_Venues_Adaptor = lst_Venues.getAdapter();
			((BaseAdapter) lst_Venues_Adaptor).notifyDataSetChanged();

		}
	}
}
