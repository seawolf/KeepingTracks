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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ArrayList<String> venues = new ArrayList<String>();

		venues.add("Here");
		venues.add("There");
		venues.add("Everywhere");
		venues.add("Nowhere");

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