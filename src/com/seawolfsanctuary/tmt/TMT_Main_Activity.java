package com.seawolfsanctuary.tmt;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class TMT_Main_Activity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Reusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, FromActivity.class);

        // Initialise a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("from").setIndicator("From").setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, DetailActivity.class);
        spec = tabHost.newTabSpec("detail").setIndicator("Detail").setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, ToActivity.class);
        spec = tabHost.newTabSpec("to").setIndicator("To").setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);

    }
}