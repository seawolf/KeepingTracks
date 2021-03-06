package com.seawolfsanctuary.keepingtracks.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;

import com.seawolfsanctuary.keepingtracks.Helpers;
import com.seawolfsanctuary.keepingtracks.R;
import com.seawolfsanctuary.keepingtracks.database.config.KeepingTracks;
import com.seawolfsanctuary.keepingtracks.database.config.KeepingTracks.DatabaseHelper;

public class UnitClass {
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	private final Context context;

	public static final String DATABASE_TABLE = "unit_class";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_CLASS_NO = "class_no";
	public static final String KEY_NOTES = "notes";

	public UnitClass(Context c) {
		this.context = c;
		DBHelper = new KeepingTracks.DatabaseHelper(context);
	}

	public UnitClass open() throws SQLException {
		System.out.println("Opening database...");
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		System.out.println("Closing " + DATABASE_TABLE + " database...");
		DBHelper.close();
	}

	public Cursor getAllUnitClasses() {
		System.out.println("Fetching all entries...");
		return db.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_CLASS_NO,
				KEY_NOTES }, null, null, null, null, KEY_CLASS_NO);
	}

	public Hashtable<String, String> getAllUnitNotes() {
		System.out.println("Fetching all notes...");
		Hashtable<String, String> notes = new Hashtable<String, String>();
		Cursor c = db
				.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_CLASS_NO,
						KEY_NOTES }, null, null, null, null, KEY_CLASS_NO);
		if (c.moveToFirst()) {
			do {
				String classNo = c.getString(c.getColumnIndex(KEY_CLASS_NO));
				String note = c.getString(c.getColumnIndex(KEY_NOTES));
				if (note != null) {
					notes.put(classNo, note);
				}
			} while (c.moveToNext());
		}
		return notes;
	}

	public long insertUnitNotes(String classNo, String notes) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CLASS_NO, classNo);
		initialValues.put(KEY_NOTES, notes);
		System.out.println("Writing entry...");
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	public boolean deleteUnitNotes(String classNo) {
		System.out.println("Deleting entry " + classNo + "...");
		return db.delete(DATABASE_TABLE, KEY_CLASS_NO + "=" + classNo, null) > 0;
	}

	public Cursor getUnitNotes(String unitNo) throws SQLException {
		System.out.println("Fetching notes for class " + unitNo + "...");
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_CLASS_NO, KEY_NOTES }, KEY_CLASS_NO + "="
				+ unitNo, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public boolean insertOrUpdateUnitNotes(String unitNo, String notes) {
		System.out.println("Updating notes for class " + unitNo + "...");
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(KEY_CLASS_NO, unitNo);
		updatedValues.put(KEY_NOTES, notes);
		int rowId = 0;
		rowId = db.update(DATABASE_TABLE, updatedValues, KEY_CLASS_NO + "="
				+ unitNo, null);
		if (rowId == 0) {
			rowId = (int) this.insertUnitNotes(unitNo, notes);
		}
		return rowId > 0;
	}

	public ArrayList<Boolean> importFromCSV(String dataFile) {
		ArrayList<Boolean> statuses = new ArrayList<Boolean>();
		ArrayList<String> saved_entries = loadSavedEntries(dataFile);
		ArrayList<String[]> parsed_entries = parseEntries(saved_entries);

		UnitClass db_unit_class = new UnitClass(this.context);
		db_unit_class.open();

		for (String[] entry : parsed_entries) {
			try {
				System.out.println("Importing...");
				db_unit_class.insertUnitNotes(entry[0], entry[1]);
				System.out.println("Success!");
				statuses.add(true);
			} catch (Exception e) {
				System.out.println("Error!");
				statuses.add(false);
			}
		}

		db_unit_class.close();
		File f = new File(dataFile);
		f.renameTo(new File(dataFile + ".imported"));

		return statuses;
	}

	public ArrayList<String> loadSavedEntries(String dataFile) {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

			try {
				String line = null;
				ArrayList<String> array = new ArrayList<String>();

				File f = new File(dataFile);
				BufferedReader reader = new BufferedReader(new FileReader(f));

				while ((line = reader.readLine()) != null) {
					String str = new String(line);
					array.add(str);
				}
				reader.close();
				return array;

			} catch (Exception e) {
				System.out.println("Error reading old routes: "
						+ e.getMessage());
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
			Toast.makeText(
					this.context,
					this.context.getString(R.string.notes_parsing_error,
							e.getMessage()), Toast.LENGTH_LONG).show();
		}

		return data;
	}

	public Boolean exportToCSV(String dataFile) {
		boolean mExternalStorageWritable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageWritable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageWritable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need to know is we can neither read nor write
			mExternalStorageWritable = false;
		}

		if (mExternalStorageWritable) {
			try {

				File f = Helpers.fileAt(Helpers.exportDirectoryPath, dataFile,
						true);
				FileWriter writer = new FileWriter(f, true);
				String msep = "\",\"";

				UnitClass db_unit_classes = new UnitClass(context);
				db_unit_classes.open();
				Cursor c = db_unit_classes.getAllUnitClasses();
				if (c.moveToFirst()) {
					do {
						String line = "";
						line = "\"" + c.getString(1) + msep + c.getInt(2)
								+ "\"";

						writer.write(line);
						writer.write(System.getProperty("line.separator"));
					} while (c.moveToNext());
				}
				writer.close();
				db_unit_classes.close();

				return true;

			} catch (Exception e) {
				return false;
			}

		} else {
			return false;
		}
	}
}
