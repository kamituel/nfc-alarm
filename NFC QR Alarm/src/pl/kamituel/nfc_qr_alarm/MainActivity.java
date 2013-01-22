package pl.kamituel.nfc_qr_alarm;

import java.util.Calendar;
import java.util.Date;

import pl.kamituel.nfc_qr_alarm.ClockSurfaceView.AlarmListener;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

public class MainActivity extends Activity implements OnGlobalLayoutListener, AlarmListener, OnItemSelectedListener {
	private final static String TAG = MainActivity.class.getSimpleName();
	private ClockSurfaceView mClock = null;
	private TextView mTime = null;
	private PrefHelper mPrefHelper = null;
	private Spinner mTimeOfDay = null;
	
	private static final boolean DEVELOPER_MODE = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if ( DEVELOPER_MODE ) setDeveloperMode();
		
		logDeviceInfo();
		
		mPrefHelper = new PrefHelper(getApplicationContext());
		if ( mPrefHelper.getTag() == null ) {
			Intent i = new Intent(getApplicationContext(), RegisterTagActivity.class);
			startActivity(i);
			finish();
			return;
		}
		
		setContentView(R.layout.main_layout);

        mClock = (ClockSurfaceView) findViewById(R.id.clock);
        mTime = (TextView) findViewById(R.id.timeTV);

        mClock.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mClock.setAlarmListener(this);
        
        mTime.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Toast.makeText(getApplicationContext(), R.string.use_red_arrow_to_set_alarm, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
        
        mTimeOfDay = (Spinner) findViewById(R.id.timeofdayList);
        ArrayAdapter<CharSequence> timeOfDayData = ArrayAdapter.createFromResource(this, R.array.time_of_day, R.layout.time_of_day_spinner);
        timeOfDayData.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTimeOfDay.setAdapter(timeOfDayData);
        mTimeOfDay.setOnItemSelectedListener(this);
        

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume()");
		
		if ( WakeUpService.isRunning(getApplicationContext()) ) {
			Log.d(TAG, "started, but alarm is ringing, so switching to correct view");
			Intent startApp = new Intent(this, WakeUpActivity.class);
			startApp.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(startApp);
			finish();
		}
		
		refreshAlarmUi();
		
		long alarmTime = mPrefHelper.getAlarmTimeCorrect();
        Date alarm = TimeUtils.getTimeFromSecondsFromMidnight((int)alarmTime);
        mTime.setText(TimeUtils.getTimeFormatted(alarm));
        
        mTimeOfDay.setSelection(mPrefHelper.getAlarmInTheMorning() ? 0 : 1);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		EasyTracker.getInstance().activityStop(this);
	}	

	public void setAlarm (View v) {
		Log.d(TAG, "Setting an alarm or disabling it");
		
		mPrefHelper.setAlarmOn(!mPrefHelper.getAlarmOn());
		refreshAlarmUi();

		int command = mPrefHelper.getAlarmOn() ? WakeUpService.CMD_START_ALARM : WakeUpService.CMD_EMPTY;
		
		Intent intent = new Intent(this, WakeUpService.class);
		intent.putExtra(WakeUpService.COMMAND, command);
		PendingIntent pendingIntent = PendingIntent.getService(
				this, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		Calendar c = mPrefHelper.getAlarmDueIn();// getAlarmTime((int)mPrefHelper.getAlarmTimeCorrect());
		
		if ( Utils.TEST_ALARM_TIME ) {
			c = Calendar.getInstance();
			c.add(Calendar.SECOND, 5);
		}

		Log.d(TAG, "Alarm set to "+TimeUtils.toStr(c.getTime()));

		AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
	}

	@Override
	public void onGlobalLayout() {
        long alarmTime = mPrefHelper.getAlarmTime();
        
        float angle = ((float)alarmTime)/(12*3600)*360;
        Log.d(TAG, "Restoring alarm time to "+alarmTime+". Angle is "+angle);
        mClock.setAngle(angle);
	}

	@Override
	public void onAlarmChanged(int secondsTill12) {
		Date alarm = TimeUtils.getTimeFromSecondsFromMidnight(secondsTill12);
		mTime.setText(TimeUtils.getTimeFormatted(alarm));

		//mPrefHelper.setAlarmTime(secondsTill12);
		mAlarmTimeCached = secondsTill12;
	}
	
	private Handler mTimeOfDaySpinnerHandler = new Handler();
	
	@Override
	public void onTimeOfDayChanged() {
		Log.d(TAG, "TIme of day of alarm changed.");
		final boolean am = !(mTimeOfDay.getSelectedItemPosition() == 0);
		
		mTimeOfDaySpinnerHandler.post(new Runnable() {
			@Override
			public void run() {
				mTimeOfDay.setSelection(am ? 0 : 1, false);		
			}
		});
		
		mPrefHelper.setAlarmInTheMorning(am);
	}
	
	private int mAlarmTimeCached = -1;
	@Override
	public void commit() {
		Log.d(TAG, "Commig alarm settings");
		mPrefHelper.setAlarmTime(mAlarmTimeCached);
	}
	
	private void refreshAlarmUi () {
		Button enableBtn = (Button) findViewById(R.id.enableAlarmBtn);
		
		if ( mPrefHelper.getAlarmOn() ) {
			Log.d(TAG, "refreshAlarmUi alarm scheduled");
			enableBtn.setText(R.string.disable_alarm);
		} else {
			Log.d(TAG, "refreshAlarmUi alarm not scheduled");
			enableBtn.setText(R.string.enable_alarm);
		}
		
		mClock.setAlarmTimeOrNone(mPrefHelper.getAlarmOn() ? mPrefHelper.getAlarmDueIn() : null);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.reset:
			Log.d(TAG, "Clearing preferences");
			
			mPrefHelper.clearAll();
			finish();	
			break;
		case R.id.attribution:
			Log.d(TAG, "Displaying attribution info");
			
			showAboutDialog();
			
			break;
//		case R.id.grab_screenshot:
//			Log.d(TAG, "Grabbing screenshot");
//			
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					mClock.grabScreenshot();
//				}
//			}).start();
//			
//			
//			break;
		}
		
		return true;
	}
	
	private void logDeviceInfo () {
	    Display display = getWindowManager().getDefaultDisplay();
	    DisplayMetrics outMetrics = new DisplayMetrics ();
	    display.getMetrics(outMetrics);

	    float density  = getResources().getDisplayMetrics().density;
	    float dpH = outMetrics.heightPixels / density;
	    float dpW  = outMetrics.widthPixels / density;
	    
	    Log.d(TAG, String.format("DPI: %f, width[dp]: %.1f, height[dp]: %.1f", density, dpW, dpH));
	}


	private void showAboutDialog () {
		String author = getResources().getString(R.string.author);
		String music = getResources().getString(R.string.music);
		SpannableString msg = new SpannableString("\n"+author+": kamituel\n\n"+music+": \"Coffe\", Josh Woodward, http://joshwoodward.com/\n");
		Linkify.addLinks(msg, Linkify.WEB_URLS);
		
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(getResources().getString(R.string.attribution));
		b.setMessage(msg);
		b.setPositiveButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		AlertDialog d = b.create();
		d.show();
		
		((TextView)d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
	}


	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
		boolean am = (pos == 0);
		Log.d(TAG, "Setting alarm time of day to "+am);
		mPrefHelper.setAlarmInTheMorning(am);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		Log.w(TAG, "nothing selected in time of day spinner");
	}

	private void setDeveloperMode () {
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

}
