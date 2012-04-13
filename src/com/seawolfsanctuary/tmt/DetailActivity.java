package com.seawolfsanctuary.tmt;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class DetailActivity extends Activity {
	CheckBox cb_DetailClass;
	TextView txt_DetailClass;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_detail);
	}

	public void onCheckboxClicked(View v) {
		TextView txt_DetailClass = (TextView) findViewById(R.id.txt_DetailClass);
		txt_DetailClass.setEnabled(((CheckBox) v).isChecked());
	}
}