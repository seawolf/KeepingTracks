package com.seawolfsanctuary.keepingtracks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.seawolfsanctuary.keepingtracks.database.UnitClass;

public class DataFileActivity extends Activity {

	private Bundle template;
	ArrayList<String> names;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.data_file_context_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.download:
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog
					.setTitle(getString(R.string.data_file_download_title));
			progressDialog
					.setMessage(getString(R.string.data_file_download_text));
			progressDialog.setCancelable(true);
			new DownloadBundleTask(progressDialog).execute();
		default:
			return true;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		template = getIntent().getExtras();
		if (template == null) {
			template = new Bundle();
		}

		setContentView(R.layout.data_file_activity);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(this));

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Toast.makeText(DataFileActivity.this,
						"Class: " + names.get(position), Toast.LENGTH_SHORT)
						.show();
			}
		});

		MenuActivity.hideLoader();
	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ArrayList<String> entries = loadDataFile(true);
		public ArrayList<String[]> data = parseEntries(entries);

		public ImageAdapter(Context c) {
			mContext = c;
			names = new ArrayList<String>(getNames(data));
		}

		public int getCount() {
			return entries.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);
			} else {
				imageView = (ImageView) convertView;
			}

			String classNo = names.get(position);
			if (checkForPhoto(classNo)) {
				imageView.setImageDrawable(loadPhoto(classNo));
			} else {
				imageView.setImageResource(R.drawable.data_file_unknown);
			}
			return imageView;
		}

		private ArrayList<String> getNames(ArrayList<String[]> data) {
			ArrayList<String> names = new ArrayList<String>();
			for (int i = 0; i < data.size(); i++) {
				String[] entry = data.get(i);
				String name = entry[0];
				names.add(name);
			}
			return names;
		}

		private boolean checkForPhoto(String classNo) {
			try {
				File f = new File(Helpers.dataDirectoryPath
						+ "/class_photos/thumbs/", classNo);
				return f.exists();
			} catch (Exception e) {
				return false;
			}
		}

		private Drawable loadPhoto(String classNo) {
			Drawable d = null;
			try {
				File f = new File(Helpers.dataDirectoryPath
						+ "/class_photos/thumbs/", classNo);
				Drawable p = Drawable.createFromPath(f.getPath());
				d = p;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			return d;
		}

		private String[] readCSV(String filename) {
			String[] array = {};

			try {
				InputStream input;
				input = getAssets().open(filename);
				int size = input.available();
				byte[] buffer = new byte[size];

				input.read(buffer);
				input.close();
				array = new String(buffer).split("\n");

			} catch (Exception e) {
			}

			return array;
		}

		private ArrayList<String> loadDataFile(boolean showToast) {

			try {
				ArrayList<String> array = new ArrayList<String>();

				String[] classInfo = readCSV("classes.csv");
				for (String infoLine : classInfo) {
					array.add(infoLine);
				}

				return array;

			} catch (Exception e) {
				System.out.println(e.getMessage());
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
				System.out.println(e.getMessage());
			}

			return data;
		}

		private ArrayList<String> parseOperators(String operatorString) {
			ArrayList<String> operators = new ArrayList<String>();
			if (operatorString.length() > 0) {
				String[] pipedOperators = operatorString.split("[|]");
				for (String operator : pipedOperators) {
					operators.add(operator);
				}
			} else {
				operators.add(getString(R.string.data_file_none));
			}
			return operators;
		}

		private Hashtable<String, String> getUnitClassNotes() {
			UnitClass db_unitClass = new UnitClass(getApplicationContext());
			db_unitClass.open();
			Hashtable<String, String> notes = db_unitClass.getAllUnitNotes();
			db_unitClass.close();
			return notes;
		}
	}

	private class DownloadBundleTask extends AsyncTask<Void, String, Boolean> {
		private ProgressDialog progressDialog;
		private String downloadingError = getString(R.string.data_file_download_error);

		public DownloadBundleTask(ProgressDialog dialogFromActivity) {
			progressDialog = dialogFromActivity;
		}

		public void onPreExecute() {
			progressDialog.show();
		}

		protected Boolean doInBackground(Void... params) {
			ImageAdapter adapter = new ImageAdapter(getApplicationContext());
			ArrayList<String> entries = adapter.loadDataFile(true);
			ArrayList<String[]> data = adapter.parseEntries(entries);

			progressDialog.setMax(data.size() * 2);

			boolean downloadedThumbs = false;
			boolean downloadedPhotos = false;

			try {
				URL bundleDownloadURL = new URL(Helpers.classInfoThumbsURI);
				File targetDir = Helpers.dirAt(Helpers.dataDirectoryPath
						+ "/class_photos/thumbs", false);

				for (int i = 0; i < data.size(); i++) {
					String[] d = data.get(i);
					String destination = d[0];

					publishProgress("thumbnail", destination);

					URL photoDownloadURL = new URL(bundleDownloadURL
							+ destination);
					HttpURLConnection c = (HttpURLConnection) photoDownloadURL
							.openConnection();
					c.setRequestMethod("GET");
					c.setDoOutput(true);
					c.connect();

					try {
						File target = Helpers.fileAt(Helpers.dataDirectoryPath
								+ "/class_photos/thumbs/", destination, true);

						FileOutputStream f = new FileOutputStream(target);
						InputStream in = c.getInputStream();
						byte[] buffer = new byte[1024];
						int len1 = 0;
						while ((len1 = in.read(buffer)) > 0) {
							f.write(buffer, 0, len1);
						}
						f.close();
						c.disconnect();
					} catch (Exception e) {
						System.err.println("Download of class " + destination
								+ " thumbnail failed.\n" + e.getMessage());
						e.printStackTrace();
						try {
							File f = Helpers.fileAt(Helpers.dataDirectoryPath
									+ "/class_photos/thumbs/", destination,
									false);
							f.delete();
						} catch (Exception x) {
							// meh
						}
					}

					progressDialog.incrementProgressBy(1);
				}

				downloadedThumbs = true;

				bundleDownloadURL = new URL(Helpers.classInfoPhotosURI);

				targetDir = Helpers.dirAt(Helpers.dataDirectoryPath
						+ "/class_photos", false);
				for (int i = 0; i < data.size(); i++) {
					String[] d = data.get(i);
					String destination = d[0];

					publishProgress("photo", destination);

					URL photoDownloadURL = new URL(bundleDownloadURL
							+ destination);
					HttpURLConnection c = (HttpURLConnection) photoDownloadURL
							.openConnection();
					c.setRequestMethod("GET");
					c.setDoOutput(true);
					c.connect();

					File target = Helpers.fileAt(Helpers.dataDirectoryPath
							+ "/class_photos/", destination, true);

					try {
						FileOutputStream f = new FileOutputStream(
								Helpers.dataDirectoryPath + "/class_photos/"
										+ destination);
						InputStream in = c.getInputStream();
						byte[] buffer = new byte[1024];
						int len1 = 0;
						while ((len1 = in.read(buffer)) > 0) {
							f.write(buffer, 0, len1);
						}
						f.close();
						c.disconnect();
					} catch (Exception e) {
						System.err.println("Download of class " + destination
								+ " photo failed.\n" + e.getMessage());
						e.printStackTrace();
						try {
							File f = Helpers.fileAt(Helpers.dataDirectoryPath
									+ "/class_photos/", destination, false);
							f.delete();
						} catch (Exception x) {
							// meh
						}
					}

					progressDialog.incrementProgressBy(1);
				}

				downloadedPhotos = true;

			} catch (Exception e) {
				downloadingError = e.getLocalizedMessage();
				e.printStackTrace();
			}

			return (downloadedThumbs && downloadedPhotos);
		}

		protected void onProgressUpdate(String... progress) {
			String class_no = progress[1];
			String photo_type = progress[0].substring(0, 1).toUpperCase()
					+ progress[0].substring(1);

			progressDialog
					.setMessage(getString(R.string.data_file_download_progress,
							photo_type, class_no));
		}

		protected void onPostExecute(Boolean success) {
			progressDialog.dismiss();

			Intent intent = new Intent(getApplicationContext(),
					DataFileActivity.class);
			DataFileActivity.this.finish();
			startActivity(intent);

			if (success) {
				Toast.makeText(getApplicationContext(),
						getString(R.string.data_file_download_complete),
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(
						getApplicationContext(),
						getString(R.string.data_file_download_error) + "\n"
								+ downloadingError, Toast.LENGTH_LONG).show();
			}
		}

	}

}