package com.seawolfsanctuary.tmt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddActivity extends TabActivity {

	TabHost mTabHost;

	TextView txt_FromSearch;
	DatePicker dp_FromDate;
	TimePicker tp_FromTime;
	AutoCompleteTextView actv_FromSearch;

	CheckBox chk_DetailClass;
	TextView txt_DetailClass;
	CheckBox chk_DetailHeadcode;
	TextView txt_DetailHeadcode;

	TextView txt_ToSearch;
	DatePicker dp_ToDate;
	TimePicker tp_ToTime;
	AutoCompleteTextView actv_ToSearch;

	TextView txt_Summary;

	private ProgressDialog dialog;

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
		case R.id.list:
			Intent intent = new Intent(this, ListSavedActivity.class);
			AddActivity.this.finish();
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

		// Link array of completions
		String[] completions = read_csv("stations.lst");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, completions);

		setContentView(R.layout.add_activity);

		mTabHost = getTabHost();

		mTabHost.addTab(mTabHost.newTabSpec("tc_From").setIndicator("From")
				.setContent(R.id.tc_From));
		mTabHost.addTab(mTabHost.newTabSpec("tc_Detail").setIndicator("Detail")
				.setContent(R.id.tc_Detail));
		mTabHost.addTab(mTabHost.newTabSpec("tc_To").setIndicator("To")
				.setContent(R.id.tc_To));
		mTabHost.addTab(mTabHost.newTabSpec("tc_Summary")
				.setIndicator("Summary").setContent(R.id.tc_Summary));

		mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabID) {
				// if detail then update 'from', try 'to'
				if (tabID == "tc_Summary") {
					updateText();
				}
			}
		});

		mTabHost.setCurrentTab(0);

		actv_FromSearch = (AutoCompleteTextView) findViewById(R.id.actv_FromSearch);
		actv_ToSearch = (AutoCompleteTextView) findViewById(R.id.actv_ToSearch);

		TextWatcher tw_FromToTextChanged = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				updateTyped();
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};
		OnItemClickListener cl_FromToClickListener = new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				updateText();
				updateTyped();
				Helpers.hideKeyboard(view);
			}
		};

		actv_FromSearch.setAdapter(adapter);
		actv_FromSearch.setThreshold(2);
		actv_FromSearch.addTextChangedListener(tw_FromToTextChanged);
		actv_FromSearch.setOnItemClickListener(cl_FromToClickListener);

		actv_ToSearch.setAdapter(adapter);
		actv_ToSearch.setThreshold(2);
		actv_ToSearch.addTextChangedListener(tw_FromToTextChanged);
		actv_ToSearch.setOnItemClickListener(cl_FromToClickListener);
	}

	private String[] read_csv(String filename) {
		String[] array = {};

		try {
			InputStream input;
			input = getAssets().open(filename);
			int size = input.available();
			byte[] buffer = new byte[size];

			input.read(buffer);
			input.close();
			array = new String(buffer).split("\n");

			Toast.makeText(getBaseContext(), "Stations loaded.",
					Toast.LENGTH_SHORT).show();

		} catch (Exception e) {
			String error_msg = "Error reading station list!";

			actv_FromSearch = (AutoCompleteTextView) findViewById(R.id.actv_FromSearch);
			actv_FromSearch.setText(error_msg);
			actv_FromSearch.setError(error_msg);
			actv_FromSearch.setEnabled(false);

			actv_ToSearch = (AutoCompleteTextView) findViewById(R.id.actv_ToSearch);
			actv_ToSearch.setText(error_msg);
			actv_ToSearch.setError(error_msg);
			actv_ToSearch.setEnabled(false);
		}

		return array;
	}

	private void updateText() {
		actv_FromSearch = (AutoCompleteTextView) findViewById(R.id.actv_FromSearch);
		dp_FromDate = (DatePicker) findViewById(R.id.dp_FromDate);
		tp_FromTime = (TimePicker) findViewById(R.id.tp_FromTime);

		txt_DetailClass = (TextView) findViewById(R.id.txt_DetailClass);
		txt_DetailHeadcode = (TextView) findViewById(R.id.txt_DetailHeadcode);

		actv_ToSearch = (AutoCompleteTextView) findViewById(R.id.actv_ToSearch);
		dp_ToDate = (DatePicker) findViewById(R.id.dp_ToDate);
		tp_ToTime = (TimePicker) findViewById(R.id.tp_ToTime);

		txt_Summary = (TextView) findViewById(R.id.txt_Summary);

		txt_Summary.setText("You selected:" + "\nFrom:"
				+ actv_FromSearch.getText().toString() + "\nOn: "
				+ dp_FromDate.getDayOfMonth() + "/" + dp_FromDate.getMonth()
				+ "/" + dp_FromDate.getYear() + "\nAt: "
				+ tp_FromTime.getCurrentHour() + ":"
				+ tp_FromTime.getCurrentMinute() +

				"\nTo:" + actv_ToSearch.getText().toString() + "\nOn: "
				+ dp_ToDate.getDayOfMonth() + "/" + dp_ToDate.getMonth() + "/"
				+ dp_ToDate.getYear() + "\nAt: " + tp_ToTime.getCurrentHour()
				+ ":" + tp_ToTime.getCurrentMinute() +

				"\nWith: " + txt_DetailClass.getText() +

				"\nAs: " + txt_DetailHeadcode.getText());
	}

	private void updateTyped() {

		String fromStation = "";
		if (actv_FromSearch.getText().length() > 2) {
			fromStation = actv_FromSearch.getText().toString().substring(0, 3);
		}
		String toStation = "";
		if (actv_ToSearch.getText().length() > 2) {
			toStation = actv_ToSearch.getText().toString().substring(0, 3);
		}
	}

	public void onClassCheckboxClicked(View view) {
		CheckBox chk_DetailClass = (CheckBox) findViewById(R.id.chk_DetailClass);
		TextView txt_DetailClass = (TextView) findViewById(R.id.txt_DetailClass);
		txt_DetailClass.setEnabled(((CheckBox) chk_DetailClass).isChecked());

		Helpers.hideKeyboard(view);
	}

	public void onHeadcodeCheckboxClicked(View view) {
		CheckBox chk_DetailHeadcode = (CheckBox) findViewById(R.id.chk_DetailHeadcode);
		TextView txt_DetailHeadcode = (TextView) findViewById(R.id.txt_DetailHeadcode);
		txt_DetailHeadcode.setEnabled(((CheckBox) chk_DetailHeadcode)
				.isChecked());

		Helpers.hideKeyboard(view);
	}

	public boolean writeEntry(View view) {
		actv_FromSearch = (AutoCompleteTextView) findViewById(R.id.actv_FromSearch);
		dp_FromDate = (DatePicker) findViewById(R.id.dp_FromDate);
		tp_FromTime = (TimePicker) findViewById(R.id.tp_FromTime);

		txt_DetailClass = (TextView) findViewById(R.id.txt_DetailClass);
		txt_DetailHeadcode = (TextView) findViewById(R.id.txt_DetailHeadcode);

		actv_ToSearch = (AutoCompleteTextView) findViewById(R.id.actv_ToSearch);
		dp_ToDate = (DatePicker) findViewById(R.id.dp_ToDate);
		tp_ToTime = (TimePicker) findViewById(R.id.tp_ToTime);

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
				File f = new File(dataFilePath);

				if (!f.exists()) {
					f.createNewFile();
				}

				FileWriter writer = new FileWriter(f, true);

				String msep = "\",\"";
				String line = "";
				line = "\"" + actv_FromSearch.getText().toString() + msep
						+ dp_FromDate.getDayOfMonth() + msep
						+ dp_FromDate.getMonth() + msep + dp_FromDate.getYear()
						+ msep + tp_FromTime.getCurrentHour() + msep
						+ tp_FromTime.getCurrentMinute() + msep
						+ actv_ToSearch.getText().toString() + msep
						+ dp_ToDate.getDayOfMonth() + msep
						+ dp_ToDate.getMonth() + msep + dp_ToDate.getYear()
						+ msep + tp_ToTime.getCurrentHour() + msep
						+ tp_ToTime.getCurrentMinute() + msep
						+ txt_DetailClass.getText() + msep
						+ txt_DetailHeadcode.getText() + "\"";

				writer.write(line);
				writer.write(System.getProperty("line.separator"));
				writer.close();

				Toast.makeText(getBaseContext(), "Entry saved.",
						Toast.LENGTH_SHORT).show();

				AddActivity.this.finish();
				Intent intent = new Intent(this, ListSavedActivity.class);
				startActivity(intent);

				return true;

			} catch (Exception e) {
				Toast.makeText(getBaseContext(), "Error: " + e.getMessage(),
						Toast.LENGTH_LONG).show();

				return false;
			}

		} else {
			return false;
		}
	}

	public void startClassInfoActivity(View view) {
		Intent intent = new Intent(this, ClassInfoActivity.class);
		startActivity(intent);
	}

	public void startHeadcodeSelectionActivity(View view) {
		dialog = ProgressDialog.show(AddActivity.this,
				"Downloading Departures",
				"Downloading departure board. Please wait...", true);

		String from = "";
		if (actv_FromSearch.getText().toString().length() > 2) {
			from = actv_FromSearch.getText().toString().substring(0, 3);
		}
		String to = "";
		if (actv_ToSearch.getText().toString().length() > 2) {
			from = actv_ToSearch.getText().toString().substring(0, 3);
		}
		String month = "";
		if (("" + dp_FromDate.getMonth()).length() > 0) {
			month = "" + (dp_FromDate.getMonth() + 1);
		}

		String[] journeyDetails = { from, to,
				tp_FromTime.getCurrentHour().toString(),
				tp_FromTime.getCurrentMinute().toString(),
				"" + dp_FromDate.getYear(), month,
				"" + dp_FromDate.getDayOfMonth() };

		new DownloadJourneysTask().execute(journeyDetails);
	}

	private class DownloadJourneysTask extends
			AsyncTask<String[], Void, ArrayList<String>> {

		/**
		 * The system calls this to perform work in a worker thread and delivers
		 * it the parameters given to AsyncTask.execute()
		 */
		protected ArrayList<String> doInBackground(String[]... journeysDetails) {
			ArrayList<String> formattedJourneys = new ArrayList<String>();

			String[] journeyDetails = journeysDetails[0];
			String fromStation = journeyDetails[0];
			String toStation = journeyDetails[1];
			String hour = journeyDetails[2];
			String minute = journeyDetails[3];
			String year = journeyDetails[4];
			String month = journeyDetails[5];
			String day = journeyDetails[6];

			Integer pageDurationHours = 2;

			String section = Integer
					.toString((Integer.parseInt(hour) / pageDurationHours));
			if (section.indexOf(".") != -1) {
				section = section.substring(0, section.indexOf("."));
			}

			try {
				System.out.println("From: " + fromStation);
				System.out.println("JourneyDetails: "
						+ journeyDetails.toString());

				URL url = new URL("http://trains.im/departures/" + fromStation
						+ "/" + year + "/" + month + "/" + day + "/" + section);
				System.out.println("URL: " + url.toString());

				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(url.openStream(), "UTF-8"));

				for (String line; (line = reader.readLine()) != null;) {
					builder.append(line.trim());
				}

				String tableStart = "<table class=\"table table-striped\">";
				String tableEnd = "</table>";
				String tablePart = builder.substring(builder
						.indexOf(tableStart) + tableStart.length());
				System.out.println(tablePart);
				String table = tablePart.substring(0,
						tablePart.indexOf(tableEnd));

				String bodyStart = "<tbody>";
				String bodyEnd = "</tbody>";
				String bodyPart = table.substring(table.indexOf(bodyStart)
						+ bodyStart.length());
				String body = bodyPart.substring(0, bodyPart.indexOf(bodyEnd));

				String rowStart = "<tr";
				String rowEnd = "</tr>";
				ArrayList<String> rows = new ArrayList<String>();

				String[] rawRows = body.split(Pattern.quote(rowStart));
				for (int r = 1; r < rawRows.length; r++) {
					String row = rawRows[r];
					rows.add(row);
				}

				ArrayList<ArrayList> journeys = new ArrayList<ArrayList>();

				for (int r = 1; r < rows.size(); r++) {
					String row = rows.get(r);

					// Split into array of cells
					String cellStart = "<td";
					String cellEnd = "</";

					ArrayList<String> cells = new ArrayList<String>();
					String[] rawCells = row.split(Pattern.quote(cellStart));
					for (int i = 0; i < rawCells.length; i++) {
						cells.add(rawCells[i]);
					}
					cells.remove(0);

					ArrayList<String> journey = new ArrayList<String>();

					// Get cell contents and remove any more HTML tags from
					// inside
					for (int c = 0; c < cells.size(); c++) {
						String cellPart = cells.get(c);
						String cell = cellPart.substring(
								cellPart.indexOf(">") + 1,
								cellPart.indexOf(cellEnd));
						cells.set(c, android.text.Html.fromHtml(cell)
								.toString());
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

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		protected void onPostExecute(ArrayList<String> resultList) {
			dialog.dismiss();

			if (resultList.size() > 0) {

				String[] tempResults = new String[resultList.size()];
				for (int i = 0; i < resultList.size(); i++) {
					tempResults[i] = resultList.get(i);
				}

				final String[] results = tempResults;

				AlertDialog.Builder builder = new AlertDialog.Builder(
						AddActivity.this);
				builder.setTitle("Select Journey");
				builder.setSingleChoiceItems(results, -1,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								txt_DetailHeadcode.setText(results[item]
										.substring(0,
												results[item].indexOf(":")));
								dialog.dismiss();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			} else {
				Toast.makeText(getBaseContext(),
						"Download failed. Check your Internet connection.",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
