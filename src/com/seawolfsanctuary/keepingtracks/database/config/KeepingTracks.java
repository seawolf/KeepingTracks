package com.seawolfsanctuary.keepingtracks.database.config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.seawolfsanctuary.keepingtracks.database.Journey;
import com.seawolfsanctuary.keepingtracks.database.UnitClass;

public class KeepingTracks {
	public static final String DATABASE_NAME = "keepingtracks";
	public static final int DATABASE_VERSION = 2; /* updated at each DB change */

	public static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			System.out.println("Creating database...");
			String createJourneys = "create table " + Journey.DATABASE_TABLE
					+ " (" + Journey.KEY_ROWID
					+ " integer primary key autoincrement, "
					+ Journey.KEY_FROM_STATION + " text not null, "
					+ Journey.KEY_FROM_DAY + " int not null, "
					+ Journey.KEY_FROM_MONTH + " int not null, "
					+ Journey.KEY_FROM_YEAR + " int not null, "
					+ Journey.KEY_FROM_HOUR + " int not null, "
					+ Journey.KEY_FROM_MINUTE + " int not null, "

					+ Journey.KEY_TO_STATION + " text not null, "
					+ Journey.KEY_TO_DAY + " int not null, "
					+ Journey.KEY_TO_MONTH + " int not null, "
					+ Journey.KEY_TO_YEAR + " int not null, "
					+ Journey.KEY_TO_HOUR + " int not null, "
					+ Journey.KEY_TO_MINUTE + " int not null, "
					+ Journey.KEY_CLASS + " text not null, "
					+ Journey.KEY_HEADCODE + " text not null, "
					+ Journey.KEY_STATS + " int not null default '1' " + ");";

			String createUnitClasses = "create table "
					+ UnitClass.DATABASE_TABLE + " ("
					+ "_id integer primary key autoincrement, "
					+ UnitClass.KEY_CLASS_NO + " text not null unique, "
					+ UnitClass.KEY_NOTES + " text " + ");";

			db.execSQL(createJourneys);
			db.execSQL(createUnitClasses);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int currentVersion,
				int newestVersion) {
			System.out.println("Upgrading " + DATABASE_NAME + " from version "
					+ currentVersion + " to " + newestVersion + "...");

			if (currentVersion < 2) {
				System.out.println("Adding column: " + Journey.KEY_STATS
						+ "...");
				db.execSQL("ALTER TABLE " + Journey.DATABASE_TABLE
						+ " ADD COLUMN " + Journey.KEY_STATS
						+ " int not null default '1' " + ";");
			}

			System.out.println(DATABASE_VERSION + " is now at version "
					+ newestVersion + "(schema version: " + DATABASE_VERSION
					+ ")");
		}
	}
}