package pl.kamituel.nfc_qr_alarm;

import android.app.Activity;
import android.os.Build;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

public class Utils {
	private final static String TAG = Utils.class.getSimpleName();
	public static boolean RUNS_IN_EMULATOR;
	private static final boolean DEVELOPER_MODE = false;
	
	static {
		RUNS_IN_EMULATOR = ("google_sdk".equals(Build.PRODUCT) || "sdk_x86".equals(Build.PRODUCT) || "sdk".equals(Build.PRODUCT));
	}
	
	private static void setDeveloperMode () {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.detectDiskReads()
		.detectDiskWrites()
		.detectAll()   // or .detectAll() for all detectable problems
		.penaltyLog()
		.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		.detectLeakedSqlLiteObjects()
		.detectLeakedClosableObjects()
		.penaltyLog()
		.penaltyDeath()
		.build());
	}
	
	private static void logDeviceInfo (Activity activity) {
	    Display display = activity.getWindowManager().getDefaultDisplay();
	    DisplayMetrics outMetrics = new DisplayMetrics ();
	    display.getMetrics(outMetrics);

	    float density  = activity.getResources().getDisplayMetrics().density;
	    float dpH = outMetrics.heightPixels / density;
	    float dpW  = outMetrics.widthPixels / density;
	    
	    Log.d(TAG, String.format("DPI: %f, width[dp]: %.1f, height[dp]: %.1f", density, dpW, dpH));
	    Log.d(TAG, String.format("Build.PRODUCT=%s",Build.PRODUCT));
	}
	
	public static void initDeveloperTools (Activity activity) {
		logDeviceInfo(activity);
		if ( DEVELOPER_MODE ) {
			Log.w(TAG, "Developer mode enabled.");
			setDeveloperMode();
		}
	}
}
