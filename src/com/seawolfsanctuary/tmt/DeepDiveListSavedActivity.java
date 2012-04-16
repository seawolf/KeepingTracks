package com.seawolfsanctuary.tmt;

import java.util.ArrayList;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class DeepDiveListSavedActivity extends ExpandableListActivity {

	ArrayList<String> entries = new ArrayList<String>();
	ArrayList<String> names = new ArrayList<String>();
	ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

	String[] presentedNames = Helpers.arrayListToArray(names);
	String[][] presentedData = Helpers.multiArrayListToArray(data);

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new DeepDiveListSavedAdapter());
		registerForContextMenu(getExpandableListView());
	}

	class DeepDiveListSavedAdapter extends BaseExpandableListAdapter {
		public Object getChild(int groupPosition, int childPosition) {
			return presentedData[groupPosition][childPosition];
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			return presentedData[groupPosition].length;
		}

		public TextView getGenericView() {
			// Layout parameters for the ExpandableListView
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 64);

			TextView textView = new TextView(DeepDiveListSavedActivity.this);
			textView.setLayoutParams(lp);
			// Centre the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			textView.setPadding(36, 0, 0, 0);
			return textView;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setText(getChild(groupPosition, childPosition).toString());
			return textView;
		}

		public Object getGroup(int groupPosition) {
			return presentedNames[groupPosition];
		}

		public int getGroupCount() {
			return presentedNames.length;
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setText(getGroup(groupPosition).toString());
			return textView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public boolean hasStableIds() {
			return true;
		}

	}

}