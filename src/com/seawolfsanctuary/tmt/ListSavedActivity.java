package com.seawolfsanctuary.tmt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListSavedActivity extends ListActivity {

	public static final String dataFilePath = Environment
			.getExternalStorageDirectory().toString()
			+ "/Android/data/com.seawolfsanctuary.tmt/routes.csv";

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.add_new:
			Intent intent = new Intent(this, AddActivity.class);
			ListSavedActivity.this.finish();
			startActivity(intent);
			return true;
		default:
			return true;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ArrayList<String> entries = new ArrayList<String>();
		ArrayList<String> names = new ArrayList<String>();
		final Hashtable<String, String> data = new Hashtable<String, String>();

		try {
			entries = loadSavedEntries(true);

			for (Iterator<String> i = entries.iterator(); i.hasNext();) {
				String str = (String) i.next();

				String name = Helpers.trimCSVSpeech(str.split(",")[0]);
				names.add(name);
				data.put(name + "_from_station",
						Helpers.trimCSVSpeech(str.split(",")[0]));
				data.put(name + "_from_day",
						Helpers.trimCSVSpeech(str.split(",")[1]));
				data.put(name + "_from_month",
						Helpers.trimCSVSpeech(str.split(",")[2]));
				data.put(name + "_from_year",
						Helpers.trimCSVSpeech(str.split(",")[3]));
				data.put(name + "_from_hour",
						Helpers.trimCSVSpeech(str.split(",")[4]));
				data.put(name + "_from_minute",
						Helpers.trimCSVSpeech(str.split(",")[5]));
				data.put(name + "_to_station",
						Helpers.trimCSVSpeech(str.split(",")[6]));
				data.put(name + "_to_day",
						Helpers.trimCSVSpeech(str.split(",")[7]));
				data.put(name + "_to_month",
						Helpers.trimCSVSpeech(str.split(",")[8]));
				data.put(name + "_to_year",
						Helpers.trimCSVSpeech(str.split(",")[9]));
				data.put(name + "_to_hour",
						Helpers.trimCSVSpeech(str.split(",")[10]));
				data.put(name + "_to_minute",
						Helpers.trimCSVSpeech(str.split(",")[11]));
				data.put(name + "_class",
						Helpers.trimCSVSpeech(str.split(",")[12]));
			}

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		setListAdapter(new ArrayAdapter<String>(this,
				R.layout.list_saved_activity, names));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(
						getApplicationContext(),
						data.get(((TextView) view).getText() + "_from_station")
								.substring(0, 3)
								+ " -> "
								+ data.get(
										((TextView) view).getText()
												+ "_to_station")
										.substring(0, 3), Toast.LENGTH_SHORT)
						.show();
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int id, final long position) {
				new AlertDialog.Builder(view.getContext())
						.setTitle("Delete Entry")
						.setMessage(
								"Are you sure you want to delete this entry?")
						.setPositiveButton("Yes", new OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								deleteEntry(
										(ArrayList<String>) loadSavedEntries(false),
										position);

								Intent intent = getIntent();
								finish();
								startActivity(intent);
							}
						}).setNegativeButton("No", new OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								// ignore
							}
						}).show();

				return true;

			}
		});

	}

	private ArrayList<String> loadSavedEntries(boolean showToast) {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

			try {
				String line = null;
				ArrayList<String> array = new ArrayList<String>();

				File f = new File(dataFilePath);
				BufferedReader reader = new BufferedReader(new FileReader(f));

				while ((line = reader.readLine()) != null) {
					String str = new String(line);
					array.add(str);
				}
				reader.close();

				if (showToast) {
					Toast.makeText(
							getBaseContext(),
							"Loaded " + array.size() + " entr"
									+ (array.size() == 1 ? "y" : "ies")
									+ " from CSV file.", Toast.LENGTH_SHORT)
							.show();
				}
				return array;

			} catch (Exception e) {
				Toast.makeText(getBaseContext(), "Error: " + e.getMessage(),
						Toast.LENGTH_LONG).show();

				return new ArrayList<String>();
			}

		} else {
			return new ArrayList<String>();
		}
	}

	public boolean deleteEntry(ArrayList<String> entries, long long_position) {
		boolean success = false;
		int position = (int) long_position;

		try {
			entries.remove(position);

			File modified = new File(dataFilePath + ".new");
			if (!modified.exists()) {
				modified.createNewFile();
			}

			FileWriter writer = new FileWriter(modified, true);

			for (Iterator<String> i = entries.iterator(); i.hasNext();) {
				String str = (String) i.next();
				writer.append(str);
				writer.write(System.getProperty("line.separator"));
			}
			writer.close();

			File existing = new File(dataFilePath);
			if (!existing.exists()) {
				existing.createNewFile();
			}

			modified.renameTo(existing);
			modified.delete();

			Toast.makeText(getBaseContext(), "Entry deleted.",
					Toast.LENGTH_SHORT).show();

			success = true;
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), "Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		return success;
	}

}