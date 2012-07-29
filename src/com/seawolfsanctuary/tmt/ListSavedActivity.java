package com.seawolfsanctuary.tmt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListSavedActivity extends ExpandableListActivity {

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
		setListAdapter(new ListSavedAdapter());
		registerForContextMenu(getExpandableListView());

		ExpandableListView lv = getExpandableListView();
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int id, long position) {
				new AlertDialog.Builder(view.getContext())
						.setTitle("Delete Entry")
						.setMessage(
								"Are you sure you want to delete this entry?")
						.setPositiveButton("Yes", new OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								deleteEntry(
										(ArrayList<String>) loadSavedEntries(false),
										id);

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

	class ListSavedAdapter extends BaseExpandableListAdapter {

		ArrayList<String> entries = loadSavedEntries(true);
		ArrayList<String[]> data = parseEntries(entries);
		ArrayList<String> names = new ArrayList<String>(getNames(data));

		private String[] presentedNames = Helpers
				.arrayListToArray(getNames(data));
		private String[][] presentedData = Helpers
				.multiArrayListToArray(getData(data));

		private ArrayList<String> getNames(ArrayList<String[]> data) {
			ArrayList<String> names = new ArrayList<String>();
			for (int i = 0; i < data.size(); i++) {
				String[] entry = data.get(i);
				names.add(Helpers.leftPad(entry[1], 2) + "/"
						+ Helpers.leftPad(entry[2], 2) + "/"
						+ Helpers.leftPad(entry[3], 4) + ":\n" + entry[0]
						+ "\n" + entry[6]);
			}
			return names;
		}

		private ArrayList<ArrayList<String>> getData(ArrayList<String[]> entries) {
			ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

			for (int i = 0; i < entries.size(); i++) {
				String[] entry = entries.get(i);
				ArrayList<String> split = new ArrayList<String>();

				split.add("From: " + entry[0] + "\nOn: "
						+ Helpers.leftPad(entry[1], 2) + "/"
						+ Helpers.leftPad(entry[2], 2) + "/"
						+ Helpers.leftPad(entry[3], 2) + "\nAt: "
						+ Helpers.leftPad(entry[4], 2) + ":"
						+ Helpers.leftPad(entry[5], 2));
				split.add("To: " + entry[6] + "\nOn "
						+ Helpers.leftPad(entry[7], 2) + "/"
						+ Helpers.leftPad(entry[8], 2) + "/"
						+ Helpers.leftPad(entry[9], 2) + "\nAt: "
						+ Helpers.leftPad(entry[10], 2) + ":"
						+ Helpers.leftPad(entry[11], 2));

				if (entry[12].length() > 0) {
					split.add("With: " + entry[12]);
				}

				if (entry[13].length() > 0) {
					split.add("As: " + entry[13]);
				}

				data.add(split);
			}

			return data;
		}

		public Object getChild(int groupPosition, int childPosition) {
			return presentedData[groupPosition][childPosition];
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			return presentedData[groupPosition].length;
		}

		public TextView getGenericView() {
			// Layout parameters for the ExpandableListView
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 64);

			TextView textView = new TextView(ListSavedActivity.this);
			textView.setLayoutParams(lp);
			// Centre the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			textView.setPadding(36, 0, 0, 0);
			return textView;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setText(getChild(groupPosition, childPosition).toString());
			return textView;
		}

		public Object getGroup(int groupPosition) {
			return presentedNames[groupPosition];
		}

		public int getGroupCount() {
			return presentedNames.length;
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setText(getGroup(groupPosition).toString());
			return textView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public boolean hasStableIds() {
			return true;
		}

	}

	public ArrayList<String> loadSavedEntries(boolean showToast) {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

			try {
				String line = null;
				ArrayList<String> array = new ArrayList<String>();

				File f = new File(Helpers.dataDirectoryPath + "/routes.csv");
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

	private ArrayList<String[]> parseEntries(ArrayList<String> entries) {
		ArrayList<String[]> data = new ArrayList<String[]>();

		try {
			for (Iterator<String> i = entries.iterator(); i.hasNext();) {
				String str = (String) i.next();
				String[] elements = str.split(",");
				String[] entry = new String[elements.length];

				for (int j = 0; j < entry.length; j++) {
					entry[j] = Helpers.trimCSVSpeech(elements[j]);
				}

				data.add(entry);
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		return data;
	}

	public boolean deleteEntry(ArrayList<String> entries, long long_position) {
		boolean success = false;
		int position = (int) long_position;

		try {
			entries.remove(position);

			File modified = new File(Helpers.dataDirectoryPath
					+ "/routes.csv.new");
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

			File existing = new File(Helpers.dataDirectoryPath + "/routes.csv");
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
