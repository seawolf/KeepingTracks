package com.seawolfsanctuary.tmt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class HeadcodeSelectionActivity extends ListActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.headcode_selection_activity);

		ArrayList<String> allJourneys = fetchJourneys();
		String[] journeys = new String[allJourneys.size()];
		for (int i = 0; i < allJourneys.size(); i++) {
			journeys[i] = allJourneys.get(i);
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, journeys);

		getListView().setAdapter(adapter);
	}

	private ArrayList<String> fetchJourneys() {
		ArrayList<String> formattedJourneys = new ArrayList<String>();

		String fromStation = "";
		String toStation = "";
		String hour = "";
		String minute = "";
		String year = "";
		String month = "";
		String day = "";
		Integer pageDurationHours = 2;

		String section = Integer
				.toString((Integer.parseInt(hour) / pageDurationHours));
		if (section.indexOf(".") != -1) {
			section = section.substring(0, section.indexOf("."));
		}

		try {

			URL url = new URL("http://trains.im/departures/" + fromStation
					+ "/" + year + "/" + month + "/" + day + "/" + section);

			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream(), "UTF-8"));

			for (String line; (line = reader.readLine()) != null;) {
				builder.append(line.trim());
			}

			String tableStart = "<table class=\"table table-striped\">";
			String tableEnd = "</table>";
			String tablePart = builder.substring(builder.indexOf(tableStart)
					+ tableStart.length());
			String table = tablePart.substring(0, tablePart.indexOf(tableEnd));

			String bodyStart = "<tbody>";
			String bodyEnd = "</tbody>";
			String bodyPart = table.substring(table.indexOf(bodyStart)
					+ bodyStart.length());
			String body = bodyPart.substring(0, bodyPart.indexOf(bodyEnd));

			String rowStart = "<tr";
			String rowEnd = "</tr>";
			ArrayList<String> rows = new ArrayList<String>();

			String[] rawRows = body.split(rowStart);
			for (int r = 1; r < rawRows.length; r++) {
				String row = rawRows[r];
				String rowPart = table.substring(table.indexOf(rowStart)
						+ rowStart.length());
				row = rowPart.substring(0, rowPart.indexOf(rowEnd));
				rows.add(row);
			}

			ArrayList<ArrayList> journeys = new ArrayList<ArrayList>();

			for (int r = 1; r < rows.size(); r++) {
				String row = rows.get(r);

				// Split into array of cells
				String cellStart = "<td";
				String cellEnd = "</";

				ArrayList<String> cells = new ArrayList<String>();
				String[] rawCells = body.split(cellStart);
				for (int i = 0; i < rawCells.length; i++) {
					cells.add(rawCells[i]);
				}
				cells.remove(0);

				ArrayList<String> journey = new ArrayList<String>();

				// Get cell contents and remove any more HTML tags from inside
				// it
				for (int c = 0; c < cells.size(); c++) {
					String cellPart = cells.get(c);
					String cell = cellPart.substring(cellPart.indexOf(">") + 1,
							cellPart.indexOf(cellEnd));
					cells.set(c, android.text.Html.fromHtml(cell).toString());
				}

				// Pick out elements
				// System.out.println("Headcode: " + cells.get(0));
				// System.out.println("Departure: " + cells.get(1));
				// System.out.println("Destination: " + cells.get(2));
				// System.out.println("Platform: " + cells.get(3));
				// System.out.println("Operator: " + cells.get(4));

				String line = cells.get(0);
				line += ": " + cells.get(1);
				line += " to " + cells.get(2);

				if (cells.get(3).length() > 0) {
					line += " (platform " + cells.get(3) + ")";
				}

				for (int i = 0; i < cells.size(); i++) {
					System.out.println("Adding: " + cells.get(i));
					journey.add(cells.get(i));
				}

				journeys.add(journey);
				formattedJourneys.add(line);
			}

			try {
				reader.close();
			} catch (IOException logOrIgnore) {
				// TODO ignore
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return formattedJourneys;
	}
}
