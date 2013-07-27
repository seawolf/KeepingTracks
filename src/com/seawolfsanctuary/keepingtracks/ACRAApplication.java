package com.seawolfsanctuary.keepingtracks;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(formKey = "", mailTo = "bugs@seawolfsanctuary.com", mode = ReportingInteractionMode.NOTIFICATION, resNotifTickerText = R.string.crash_notif_title, resNotifTitle = R.string.crash_notif_title, resNotifText = R.string.crash_notif_text, resNotifIcon = android.R.drawable.stat_notify_error, resDialogText = R.string.crash_dialog_text, resDialogIcon = android.R.drawable.ic_dialog_info, resDialogTitle = R.string.crash_dialog_title, resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, resDialogOkToast = R.string.crash_dialog_ok_toast)
public class ACRAApplication extends org.holoeverywhere.app.Application {
	@Override
	public void onCreate() {
		super.onCreate();
		ACRA.init(this);
	}
}
