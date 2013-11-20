package pl.kamituel.nfc_qr_alarm;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class WakeUpService extends Service {
	private final static String TAG = WakeUpService.class.getSimpleName();

	private MediaPlayer mPlayer = null;
	private AudioManager mAudioManager = null;

	private boolean mRunning = false;

	public static final String COMMAND = "cmd";
	public static final int CMD_START_ALARM = 1;
	public static final int CMD_STOP_ALARM = 2;
	public static final int CMD_SNOOZE_ALARM = 3;
	public static final int CMD_UNSNOOZE_ALARM = 4;

	private int mAnalyticsSnoozeCount;
	private long mAnalyticsAlarmStartTime;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate()");
	}
	
	private void goIntoForeground () {		
		Intent startApp = new Intent(this, WakeUpActivity.class);
		startApp.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		PendingIntent ci = PendingIntent.getActivity(this, 0, startApp, PendingIntent.FLAG_UPDATE_CURRENT);
		
		 Notification notification = new Notification.Builder(getApplicationContext())
         .setContentTitle(getResources().getString(R.string.welcome_activity_name))
         .setContentText(getResources().getString(R.string.good_morning_sunshine))
         .setSmallIcon(R.drawable.ic_launcher)
         .setContentIntent(ci)
         .getNotification();

		startForeground(17676, notification);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if ( intent.getExtras() == null ) {
			Log.w(TAG, "Intent.getExtras() is null?");
			stopSelf();
			return Service.START_NOT_STICKY;
		}
		
		int command = intent.getExtras().getInt(COMMAND);
		Log.d(TAG, "onStartCommand(): Command: "+command);

		switch ( command ) {
		case CMD_START_ALARM:
			if (mRunning) {
				Log.w(TAG, "WakeUpService started when one instance is already running.");
				return Service.START_STICKY;
			}
			
			mRunning = true;
			goIntoForeground();
			
			analyticsInit();
			
			ensureVolumeUp();
			startPlayback();
			
			AlarmMgmt alarmMgmt = new AlarmMgmt(this);
			alarmMgmt.restore();
			// Valid, until only one alarm supported.
			alarmMgmt.getAlarms().get(0).setEnabled(false);			
			alarmMgmt.persist();
			
			Intent i = new Intent(this, WakeUpActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
		
			break;
		case CMD_STOP_ALARM:
			if (mRunning) {
				stopPlayback();
				mRunning = false;
				analyticsAlarmStopped();
			}
			break;
		case CMD_SNOOZE_ALARM:
			if (mRunning) {
				mPlayer.pause();
				analyticsAlarmSnoozed();
			}
			break;
		case CMD_UNSNOOZE_ALARM:
			if (mRunning) {
				mPlayer.start();
			}
			break;
		default:
			Log.w(TAG, "Unrecognized command "+command);
		}
		
		if (!mRunning) {
			stopSelf();
		}

		return Service.START_NOT_STICKY;
	}

	private void startPlayback () {
		Log.d(TAG, "startPlayback()");
		try {
			mPlayer = new MediaPlayer();
			AssetFileDescriptor fd = getAssets().openFd("JoshWoodward-Coffee.mp3");
			mPlayer.setLooping(true);
			mPlayer.setDataSource(fd.getFileDescriptor());
			mPlayer.prepare();
			
			if (!NfcAlarmApp.hasFlag(R.bool.debug_alarm_muted)) {
				mPlayer.start();
			}
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "", e); 
		} catch (IllegalStateException e) {
			Log.e(TAG, "", e);
		} catch (IOException e) {
			Log.e(TAG, "", e);
		}
	}

	private void stopPlayback () {
		Log.d(TAG, "stopPlayback()");
		
		// It's possible mPlayer == null. This is when
		// stopPlayback() is called twice in a row.
		// First invocation stops player and then calls
		// stopSelf(). Then second invocation operates on
		// new WakeUpService instance, where mPlayer is null.
		if ( mRunning ) {
			mPlayer.stop();
			mPlayer.release();
		}

		stopSelf();
	}

	private void ensureVolumeUp () {
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		new Thread(new Runnable() {
			@Override
			public void run() {
				while ( mRunning ) {
					upVolume();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						Log.e(TAG, "", e);
					}
				}
			}
		}).start();
	}

	private void upVolume () {
		mAudioManager.setStreamVolume(
				AudioManager.STREAM_MUSIC, 
				mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 
				0);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy()");
	}

	public static boolean isRunning(Context ctx) {
	    ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
	    
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (WakeUpService.class.getCanonicalName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	private void analyticsInit() {
		mAnalyticsAlarmStartTime = System.nanoTime() / (1000 * 1000 * 1000);
		mAnalyticsSnoozeCount = 0;
		
		// Technically, this should be reported in MainActivity, upon alarm set.
		Calendar now = Calendar.getInstance(Locale.getDefault());
		long timeSinceMidnightSeconds = now.get(Calendar.HOUR_OF_DAY) * 60 * 60 
				+ now.get(Calendar.MINUTE) * 60;
		MyAnalytics.alarmSet(this, timeSinceMidnightSeconds);
	}
	
	private void analyticsAlarmStopped() {
		MyAnalytics.alarmDisabled(this, System.nanoTime() / (1000 * 1000 * 1000) - mAnalyticsAlarmStartTime);
		MyAnalytics.alarmSnoozeCount(this, mAnalyticsSnoozeCount);
	}
	
	private void analyticsAlarmSnoozed() {
		mAnalyticsSnoozeCount += 1;
	}
}
