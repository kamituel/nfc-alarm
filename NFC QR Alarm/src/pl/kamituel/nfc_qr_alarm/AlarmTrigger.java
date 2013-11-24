package pl.kamituel.nfc_qr_alarm;

import java.util.Calendar;
import java.util.GregorianCalendar;

import pl.kamituel.nfc_qr_alarm.alarm.Alarm;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmTrigger {
	private final static String TAG = AlarmTrigger.class.getSimpleName();
	
	/* Not currently used by PendingIntent API */
	private final static int REQUEST_CODE = 12345;
	
	private Context mCtx;
	private AlarmManager mAlarmManager;
	private Alarm mAlarm;
		
	public AlarmTrigger (Context ctx, Alarm alarm) {
		mCtx = ctx;
		mAlarmManager = (AlarmManager) mCtx.getSystemService(Activity.ALARM_SERVICE);
		mAlarm = alarm;
	}

	/*
	 * Schedules AlarmTrigger to wake up in 'millis' ms.
	 */
	public void schedule () {
		long millis = mAlarm.getCountdown();
		Log.d(TAG, "schedule(): Schedule myself to run in " + millis + " ms");
		
		Calendar alarmTime = new GregorianCalendar();
		alarmTime.add(Calendar.MILLISECOND, (int) millis);

		mAlarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), getPendingIntent());
	}
	
	public void cancel () {
		Log.d(TAG, "cancel()");
		
		mAlarmManager.cancel(getPendingIntent());
	}
	
	private PendingIntent getPendingIntent () {
		Intent intent = new Intent(mCtx, WakeUpService.class);
		intent.putExtra(WakeUpService.COMMAND, WakeUpService.CMD_START_ALARM);
		
		return PendingIntent.getService(
				mCtx, 
				REQUEST_CODE, 
				intent, 
				PendingIntent.FLAG_ONE_SHOT);
	}

}
