package pl.kamituel.nfc_qr_alarm;

import android.os.Build;

public class Utils {
	public static boolean RUNS_IN_EMULATOR;
	public static final boolean TEST_ALARM_TIME = true;
	
	static {
		RUNS_IN_EMULATOR = ("google_sdk".equals(Build.PRODUCT) || "sdk".equals(Build.PRODUCT));
	}
}
