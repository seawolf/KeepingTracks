package com.seawolfsanctuary.tmt.stats;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
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
import com.androidplot.xy.XYStepMode;
import com.seawolfsanctuary.tmt.R;
import com.seawolfsanctuary.tmt.database.Journey;

public class JourneysByMonth extends Activity {
	private XYPlot mySimpleXYPlot;
	private String[] months = new String[] { "J", "F", "M", "A", "M", "J", "J",
			"A", "S", "O", "N", "D" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats_journeys_month);
		mySimpleXYPlot = (XYPlot) findViewById(R.id.xy_JourneysMonth);

		Integer[] series1Integers = new Integer[] { null, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0 };
		ArrayList<Number> series1Numbers = new ArrayList<Number>();
		int i = 0;
		Journey db_journeys = new Journey(this);
		db_journeys.open();
		Cursor c = db_journeys.getAllStatsJourneys();
		if (c.moveToFirst()) {
			do {
				i = i + 1;
				series1Integers[c.getInt(3)] = series1Integers[c.getInt(3)] + 1;
			} while (c.moveToNext());
		}
		db_journeys.close();

		for (int j = 1; j <= 12; j++) {
			series1Numbers.add(series1Integers[j]);
		}

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

		mySimpleXYPlot.setRangeLabel("");
		mySimpleXYPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 2);
		mySimpleXYPlot.setRangeValueFormat(new DecimalFormat("#"));

		mySimpleXYPlot.setDomainLabel("");
		mySimpleXYPlot.setDomainStep(XYStepMode.SUBDIVIDE, 12);
		mySimpleXYPlot.setDomainValueFormat(new Format() {
			@Override
			public StringBuffer format(Object object, StringBuffer buffer,
					FieldPosition field) {
				int pos = (int) Math.round((Double) object);
				StringBuffer result = new StringBuffer(months[pos]);
				return result;
			}

			@Override
			public Object parseObject(String string, ParsePosition position) {
				return null;
			}
		});

		mySimpleXYPlot.setTitle(getApplicationContext().getString(
				R.string.stats_jouneys_month));
	}
}
