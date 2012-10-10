package com.seawolfsanctuary.tmt.stats;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import com.androidplot.series.XYSeries;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.seawolfsanctuary.tmt.R;
import com.seawolfsanctuary.tmt.database.Journey;

public class JourneysByMonth extends Activity {
	private XYPlot mySimpleXYPlot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats_journeys_month);
		mySimpleXYPlot = (XYPlot) findViewById(R.id.xy_JourneysMonth);
		// Create a couple arrays of y-values to plot:
		Integer[] series1Integers = new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0 };
		ArrayList<Number> series1Numbers = new ArrayList<Number>();
		int i = 0;
		Journey db_journeys = new Journey(this);
		db_journeys.open();
		Cursor c = db_journeys.getAllJourneys();
		if (c.moveToFirst()) {
			do {
				i = i + 1;
				series1Integers[c.getInt(3)] = series1Integers[c.getInt(3)] + 1;
			} while (c.moveToNext());
		}
		db_journeys.close();

		for (int j = 0; j < 13; j++) {
			series1Numbers.add(series1Integers[j]);
		}
		series1Numbers.remove(0);

		// Turn the above arrays into XYSeries':
		XYSeries series1 = new SimpleXYSeries(series1Numbers,
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use
				// the element index as
				// the x value
				"Month"); // Set the display title of the series

		// Create a formatter to use for drawing a series using
		// LineAndPointRenderer:
		LineAndPointFormatter series1Format = new LineAndPointFormatter(
				Color.rgb(0, 200, 0), // line color
				Color.rgb(0, 100, 0), // point color
				Color.rgb(0, 200, 0)); // fill color

		BarFormatter series1BarFormat = new BarFormatter(Color.rgb(0, 200, 0),
				Color.rgb(0, 100, 0));

		// add a new series' to the xyplot:
		mySimpleXYPlot.addSeries(series1, series1BarFormat);

		// reduce the number of range labels
		// mySimpleXYPlot.setTicksPerRangeLabel(3);

		// by default, AndroidPlot displays developer guides to aid in laying
		// out your plot.
		// To get rid of them call disableAllMarkup():
		mySimpleXYPlot.disableAllMarkup();
	}
}
