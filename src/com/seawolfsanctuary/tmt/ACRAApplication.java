package com.seawolfsanctuary.tmt;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.Intent;

@ReportsCrashes(formKey = "", mailTo = "bugs@seawolfsanctuary.com", mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text, resNotifTickerText = R.string.crash_notif_ticker_text, resNotifTitle = R.string.crash_notif_title, resNotifText = R.string.crash_notif_text, resNotifIcon = android.R.drawable.stat_notify_error, resDialogText = R.string.crash_dialog_text, resDialogIcon = android.R.drawable.ic_dialog_info, resDialogTitle = R.string.crash_dialog_title, resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, resDialogOkToast = R.string.crash_dialog_ok_toast)
public class ACRAApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		ACRA.init(this);
		System.out.println("ACRA initialised. Crashes will be reported.");
		Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}
