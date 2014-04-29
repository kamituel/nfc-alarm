package pl.kamituel.nfc_qr_alarm;

import java.util.Locale;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

public class WakeUpActivity extends NfcActivity {
	private static final String TAG = WakeUpActivity.class.getSimpleName();
	private final static int SNOOZE_TIME_SEC = 20;
	
	private WakeLock mWakeLock = null;
	private Handler mSnoozeHandler = new Handler();
	private PrefHelper mPrefHelper = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		setContentView(R.layout.wake_up_activity);
		mPrefHelper = new PrefHelper(this);

		if ( Utils.RUNS_IN_EMULATOR ) {
			disableAlarmWhenTitleClicked();
		}
	}
	
	@Override
	protected void tagReceived(String tagId) {
		Log.d(TAG, "Tag received: "+tagId);

		Set<String> storedTags = mPrefHelper.getTags();

		if ( storedTags.contains(tagId) ) {
			disableAlarmAndQuit();
		} else {
			Toast.makeText(getApplicationContext(), "This is not the correct tag", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		EasyTracker.getInstance(this).activityStart(this);
	}
	
	@Override 
	protected void onResume () {
		super.onResume();
		acquireWakeLock();
	}
	
	@Override
	protected void onPause () {
		super.onPause();
		releaseWakeLock();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		EasyTracker.getInstance(this).activityStop(this);
	}
	
	private void disableAlarmWhenTitleClicked() {
		TextView v = (TextView) findViewById(R.id.goodMorning1TV);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				disableAlarmAndQuit();
			}
		});
	}
	
	private void disableAlarmAndQuit () {
		sendCommand(WakeUpService.CMD_STOP_ALARM);
		
		Intent i = new Intent(getApplicationContext(), MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(i);
		finish();
	}

	public void onSnooze (final View v) {
		final int sleepTime = 50;
		final int iterations = (SNOOZE_TIME_SEC*1000) / sleepTime;
		final ProgressBar snoozePB = (ProgressBar) findViewById(R.id.snoozePB);
		final Button snoozeB = (Button) findViewById(R.id.snoozeBTN);
		
		v.setEnabled(false);
		sendCommand(WakeUpService.CMD_SNOOZE_ALARM);

		Log.d(TAG, "Snooze animation iterations: "+iterations);
		
		snoozePB.setMax(iterations);
		snoozePB.setProgress(iterations);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while ( snoozePB.getProgress() > 0 ) {
					try {
						mSnoozeHandler.post(new Runnable() {
							@Override
							public void run() {
								int progress = snoozePB.getProgress()-1;
								snoozePB.setProgress(progress);
								
								if ( progress <= 0 ) { 
									v.setEnabled(true);
									snoozeB.setText(R.string.snooze);
								} else if (progress % 2 == 0) {
									snoozeB.setText(getSnoozeBtnTime(progress*SNOOZE_TIME_SEC*1000/iterations));
								}
							}
						});
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						Log.e(TAG, "Snooze animation error", e);
					}
				}
				Log.d(TAG, "Snooze animation done");

				sendCommand(WakeUpService.CMD_UNSNOOZE_ALARM);
			}
		}).start();
	}
	
	private String getSnoozeBtnTime (int msecLeft) {
		return String.format(Locale.ENGLISH, "%.1f", msecLeft/1000f);
	}

	
	private void sendCommand (int cmd) {
		Log.d(TAG, "sendCommand "+cmd);
		Intent i = new Intent(this, WakeUpService.class);
		i.putExtra(WakeUpService.COMMAND, cmd);
		startService(i);
	}
	
	private final void acquireWakeLock () {
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TRAININGCOUNTDOWN");
		mWakeLock.acquire();
		
		Log.d(TAG, "Lock acquire");
		
		if (!mWakeLock.isHeld()) {
			Log.e(TAG, "mWakeLock not acquired");
		}
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON 
				         | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
				         //WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				         | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	}
	
	private final void releaseWakeLock () {
		try {
			mWakeLock.release();
		} catch (RuntimeException e) {
			Log.e(TAG, "mWakeLock", e);
		}
		Log.d(TAG, "Log release");
	}

}
