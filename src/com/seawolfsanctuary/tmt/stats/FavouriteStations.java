package com.seawolfsanctuary.tmt.stats;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import com.androidplot.series.XYSeries;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.seawolfsanctuary.tmt.Helpers;
import com.seawolfsanctuary.tmt.R;
import com.seawolfsanctuary.tmt.database.Journey;

public class FavouriteStations extends Activity {
	private XYPlot mySimpleXYPlot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats_favourite_stations);
		mySimpleXYPlot = (XYPlot) findViewById(R.id.xy_FavouriteStations);

		final Hashtable<String, Integer> departures = new Hashtable<String, Integer>();
		final Hashtable<String, Integer> arrivals = new Hashtable<String, Integer>();

		Journey db_journeys = new Journey(this);
		db_journeys.open();
		Cursor c = db_journeys.getAllJourneys();
		// Collect count of departure stations
		if (c.moveToFirst()) {
			do {
				Integer depCount = 0;
				Integer arrCount = 0;

				if (departures.containsKey(c.getString(1))) {
					depCount += departures.get(c.getString(1));
				}
				departures.put(c.getString(1), (depCount + 1));

				if (arrivals.containsKey(c.getString(7))) {
					arrCount += arrivals.get(c.getString(7));
				}
				arrivals.put(c.getString(7), (arrCount + 1));
			} while (c.moveToNext());
		}
		db_journeys.close();

		final Hashtable<String, Integer> merged = new Hashtable<String, Integer>();
		for (Iterator i = departures.keySet().iterator(); i.hasNext();) {
			String depStn = i.next().toString();

			// Use departure count, add arrival count
			Integer total = departures.get(depStn);
			if (arrivals.containsKey(depStn)) {
				total += arrivals.get(depStn);
			}
			merged.put(depStn, total);
		}

		// Add arrival-only counts
		for (Iterator i = arrivals.keySet().iterator(); i.hasNext();) {
			String arrStn = i.next().toString();
			if (!departures.containsKey(arrStn)) {
				merged.put(arrStn, arrivals.get(arrStn));
			}
		}

		// Sort the keys (stations) and match up the values (count)
		final ArrayList<String> sortedKeys = new ArrayList(merged.keySet());
		Collections.sort(sortedKeys);
		Number[] sortedValues = new Number[merged.size()];
		for (int i = 0; i < sortedKeys.size(); i++) {
			sortedValues[i] = merged.get(sortedKeys.get(i));
		}

		// Collect the visit counts / hastable values
		ArrayList<Number> series1Numbers = new ArrayList<Number>();
		for (Iterator iterator = sortedKeys.iterator(); iterator.hasNext();) {
			Object station = (Object) iterator.next();
			series1Numbers.add(merged.get(station));
		}

		// padding, ensure zero at axis
		sortedKeys.add("");
		series1Numbers.add(0);

		XYSeries series1 = new SimpleXYSeries(series1Numbers,
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");

		LineAndPointFormatter series1Format = new LineAndPointFormatter(
				Color.rgb(0, 200, 0), // line color
				Color.rgb(0, 100, 0), // point color
				Color.rgb(0, 200, 0)); // fill color

		BarFormatter series1BarFormat = new BarFormatter(Color.rgb(0, 200, 0),
				Color.rgb(0, 100, 0));

		mySimpleXYPlot.addSeries(series1, series1BarFormat);

		mySimpleXYPlot.getLegendWidget().setVisible(false);
		mySimpleXYPlot.disableAllMarkup();

		mySimpleXYPlot.setRangeLabel("Count");
		mySimpleXYPlot.setRangeStep(XYStepMode.INCREMENT_BY_PIXELS, 15);
		mySimpleXYPlot.setRangeValueFormat(new DecimalFormat("#"));

		mySimpleXYPlot.setDomainLabel("Stn");
		mySimpleXYPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
		mySimpleXYPlot.setDomainValueFormat(new Format() {
			@Override
			public StringBuffer format(Object object, StringBuffer buffer,
					FieldPosition field) {
				int pos = (int) Math.round((Double) object);
				String station = Helpers.trimNameFromStation(sortedKeys
						.get(pos).toString());
				StringBuffer result = new StringBuffer(station);
				return result;
			}

			@Override
			public Object parseObject(String string, ParsePosition position) {
				return null;
			}

		});
	}
}