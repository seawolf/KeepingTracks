package com.seawolfsanctuary.tmt.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.seawolfsanctuary.tmt.R;
import com.seawolfsanctuary.tmt.database.Journey;

public class ClassesUsed extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats_classes_used);

		ArrayList<String> allClasses = loadSavedEntries(true);
		Hashtable<String, Integer> classesUsed = new Hashtable<String, Integer>();
		for (String journeyClasses : allClasses) {
			for (String classUsed : Journey
					.classesStringToArrayList(journeyClasses)) {
				int count = 0;
				if (classesUsed.keySet().contains(classUsed)) {
					count = classesUsed.get(classUsed);
				}
				classesUsed.put(classUsed, 1 + count);
			}
		}

		ArrayList<String> listContents = new ArrayList<String>();
		for (String classUsed : classesUsed.keySet()) {
			String time = " time";
			if (classesUsed.get(classUsed) > 1) {
				time += "s";
			}
			listContents.add(classUsed + " (" + classesUsed.get(classUsed)
					+ time + ")");
		}
		Collections.sort(listContents);

		setListAdapter(new ArrayAdapter<String>(this,
				R.layout.stats_classes_used_list, listContents));
	}

	private ArrayList<String> loadSavedEntries(boolean showToast) {
		ArrayList<String> allClasses = new ArrayList<String>();

		Journey db_journeys = new Journey(this);
		db_journeys.open();
		Cursor c = db_journeys.getAllJourneys();
		if (c.moveToFirst()) {
			do {
				System.out.println("Reading row #" + c.getInt(0) + "...");
				allClasses.add(c.getString(13));
				;
			} while (c.moveToNext());
		}
		db_journeys.close();

		if (showToast) {
			String msg = "Loaded " + allClasses.size() + " entr"
					+ (allClasses.size() == 1 ? "y" : "ies")
					+ " from database.";
			System.out.println(msg);
			Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
		}

		return allClasses;
	}
}
