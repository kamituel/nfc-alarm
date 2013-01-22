package pl.kamituel.nfc_qr_alarm;

import android.os.Build;
import android.util.Log;

public class Utils {
	private final static String TAG = Utils.class.getSimpleName();
	public static boolean RUNS_IN_EMULATOR;
	public static final boolean TEST_ALARM_TIME = true;
	
	static {
		Log.d(TAG, "Build.PRODUCT="+Build.PRODUCT);
		RUNS_IN_EMULATOR = ("google_sdk".equals(Build.PRODUCT) || "sdk_x86".equals(Build.PRODUCT) || "sdk".equals(Build.PRODUCT));
	}
}
