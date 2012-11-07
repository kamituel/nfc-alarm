package pl.kamituel.nfc_qr_alarm;

import java.io.IOException;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
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
	public static final int CMD_EMPTY = 0;
	public static final int CMD_START_ALARM = 1;
	public static final int CMD_STOP_ALARM = 2;
	public static final int CMD_SNOOZE_ALARM = 3;
	public static final int CMD_UNSNOOZE_ALARM = 4;
	
	private PrefHelper mPrefHelper = null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		
		mPrefHelper = new PrefHelper(getApplicationContext());
	}
	
	private void goIntoForeground () {
		Intent startApp = new Intent(this, WakeUpActivity.class);
		startApp.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		NotificationManager ns = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent ci = PendingIntent.getActivity(this, 0, startApp, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification n = new Notification(R.drawable.ic_launcher, getResources().getString(R.string.good_morning_sunshine), System.currentTimeMillis());
		n.setLatestEventInfo(this, getResources().getString(R.string.welcome_activity_name), getResources().getString(R.string.press_nfc_to_disable), ci);
		startForeground(
				17676,
				n
				);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int command = intent.getExtras().getInt(COMMAND);
		Log.d(TAG, "Command: "+command);

		switch ( command ) {
		case CMD_START_ALARM:
			if ( !mRunning ) {		
				mRunning = true;
				
				goIntoForeground();
				
				ensureVolumeUp();
				startPlayback();
				
				Intent i = new Intent(this, WakeUpActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			} else Log.i(TAG, "Not starting service - already running");
			
			Log.d(TAG, "Setting alarm disabled");
			mPrefHelper.setAlarmOn(false);
			
			break;
		case CMD_STOP_ALARM:
			mRunning = false;
			stopPlayback();
			break;
		case CMD_SNOOZE_ALARM:
			if ( mRunning ) mPlayer.pause();
			break;
		case CMD_UNSNOOZE_ALARM:
			if ( mRunning ) mPlayer.start();
			break;
		case CMD_EMPTY:
			Log.d(TAG, "Canceled alarm");
			break;
		default:
			Log.w(TAG, "Unrecognized command "+command);
		}

		return Service.START_STICKY;
	}


	private void startPlayback () {
		Log.d(TAG, "startPlayback()");
		try {
			mPlayer = new MediaPlayer();
			AssetFileDescriptor fd = getAssets().openFd("JoshWoodward-Coffee.mp3");
			mPlayer.setLooping(true);
			mPlayer.setDataSource(fd.getFileDescriptor());
			mPlayer.prepare();
			mPlayer.start();
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
		mPlayer.stop();
		mPlayer.release();
		mPlayer = null;

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
	
	public static boolean isRunning(Context ctx) {
	    ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
	    
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (WakeUpService.class.getCanonicalName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

}
