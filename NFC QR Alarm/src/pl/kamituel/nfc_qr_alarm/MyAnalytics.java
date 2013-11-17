package pl.kamituel.nfc_qr_alarm;

import android.content.Context;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class MyAnalytics {
	private final static String TAG = MyAnalytics.class.getSimpleName();
	
	public static void alarmDisabled(Context ctx, Long alarmRunningTimeSeconds) {
		Log.d(TAG, "alarm-disabled event. Running time [s]: " + alarmRunningTimeSeconds);
		EasyTracker.getInstance(ctx).send(
				MapBuilder.createEvent("alarm3", "disabled", null, alarmRunningTimeSeconds).build());
	}
	
	public static void alarmSnoozeCount(Context ctx, int snoozeCount) {
		Log.d(TAG, "alarm-snooze event. Snooze count: " + snoozeCount);
		EasyTracker.getInstance(ctx).send(
				MapBuilder.createEvent("alarm3", "snooze", null, (long) snoozeCount).build());
	}
	
	public static void alarmSet(Context ctx, Long timeSinceMidnightSeconds) {
		Log.d(TAG, "alarm-set event. Alarm time [sec from midnight] " + timeSinceMidnightSeconds);
		EasyTracker.getInstance(ctx).send(
				MapBuilder.createEvent("alarm3", "set", null, timeSinceMidnightSeconds).build());
	}
}
