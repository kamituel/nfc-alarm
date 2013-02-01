package pl.kamituel.nfc_qr_alarm;

import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AlarmTrigger extends Service {
	private final static String TAG = AlarmTrigger.class.getSimpleName();

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand(): Looking for alarm which is scheduled to ring next.");
		
		AlarmMgmt alarmMgmt = new AlarmMgmt(getApplicationContext());
		alarmMgmt.restore();
		List<AlarmTime> alarms = alarmMgmt.getAlarms();
		
		if ( alarms.size() == 0 || alarms.get(0).getEnabled() == false ) {
			Log.d(TAG, "onStartCommand(): No alarm found to ring next. Clear any future WakeUpService calls.");
			ensureWakeUpServiceNotCalled();
		} else {
			AlarmTime nextAlarm = alarms.get(0);
			int nextCountdown = nextAlarm.getCountdown();
			
			if ( nextCountdown < TimeUtils.MINUTE ) {
				Log.d(TAG, "onStartCommand(): Found alarm which should ring in "+nextCountdown+" seconds.");
				startAlarmService();
				
				if ( alarms.size() > 1 && alarms.get(1).getEnabled() ) {
					AlarmTime nextNextAlarm = alarms.get(1);
					Log.d(TAG, "onStartCommand(): Next alarm should ring in "+nextNextAlarm.getCountdown()+" seconds");
					setSelf(nextNextAlarm);
				} else {
					Log.d(TAG, "onStartCommand(): There is no alarm to ring after the next one.");
				}
			} else {
				Log.d(TAG, "onStartCommand(): Next alarm should ring in "+nextCountdown+" seconds. Scheduling myself to wakeup then.");
				setSelf(nextAlarm);
			}
		}
		
		Log.d(TAG, "onStartCommand(): I'm done here. Commiting suicide.");
		stopSelf();
		
		return Service.START_STICKY;
	}
	
	private void startAlarmService () {
		Log.d(TAG, "startAlarmService(): Starting alarm service");
		Intent intent = new Intent(this, WakeUpService.class);
		intent.putExtra(WakeUpService.COMMAND, WakeUpService.CMD_START_ALARM);
		
		startService(intent);
	}
	
	private void setSelf (AlarmTime alarm) {
		Log.d(TAG, "setSelf(): Schedule myself to run in "+alarm.getCountdown()+" seconds");
		
		Intent intent = new Intent(this, AlarmTrigger.class);
		PendingIntent pi = PendingIntent.getService(
				getApplicationContext(), 
				12346, 
				intent, 
				PendingIntent.FLAG_CANCEL_CURRENT);
		
		AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, alarm.getAbsolute()*1000, pi);
	}
	
	private void ensureWakeUpServiceNotCalled () {
		Intent intent = new Intent(this, WakeUpService.class);
		intent.putExtra(WakeUpService.COMMAND, WakeUpService.CMD_EMPTY);
		PendingIntent pendingIntent = PendingIntent.getService(
				this, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		Log.d(TAG, "ensureWakeUpServiceNotCalled(): Cancel it now.");
		
		AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, -1, pendingIntent);		
	}

}
