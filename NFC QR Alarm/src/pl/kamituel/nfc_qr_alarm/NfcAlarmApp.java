package pl.kamituel.nfc_qr_alarm;

import android.app.Application;
import android.content.Context;

public class NfcAlarmApp extends Application {
	private static Context mCtx;

	@Override
	public void onCreate() {
		super.onCreate();
		mCtx = this;
	}

	public static Context getContext () {
		return mCtx;
	}
	
	public static boolean hasFlag (int settingId) {
		if (settingId == R.bool.debug) {
			return inDebug();
		} else {
			return inDebug() && mCtx.getResources().getBoolean(settingId);
		}
	}
	
	public static boolean inDebug () {
		return mCtx.getResources().getBoolean(R.bool.debug);
	}
}
