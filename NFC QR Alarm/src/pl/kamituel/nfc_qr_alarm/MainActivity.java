package pl.kamituel.nfc_qr_alarm;

import java.util.Iterator;

import pl.kamituel.nfc_qr_alarm.alarm.Alarm;
import pl.kamituel.nfc_qr_alarm.alarm.OnAlarmStateChangedListener;
import pl.kamituel.nfc_qr_alarm.time.OnTimeChangedListener;
import pl.kamituel.nfc_qr_alarm.time.Time;
import pl.kamituel.nfc_qr_alarm.time.TimeHumanReadable;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
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

public class MainActivity extends Activity implements OnGlobalLayoutListener, OnItemSelectedListener {
	private final static String TAG = MainActivity.class.getSimpleName();
	
	private ClockSurfaceView mClock = null;
	private TextView mTime = null;
	private PrefHelper mPrefHelper = null;
	private Spinner mTimeOfDay = null;
	
	private AlarmMgmt mAlarmMgmt = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");
		
		Utils.initDeveloperTools(this);
		
		mAlarmMgmt = new AlarmMgmt(this);
		mPrefHelper = new PrefHelper(this);
	
		setContentView(R.layout.main_layout);
		buildActivityUi();
	}
	
	private void registerFirstTag () {
		Intent i = new Intent(getApplicationContext(), RegisterTagActivity.class);
		startActivity(i);
		finish();
	}
	
	private void manageTags () {
		Intent i = new Intent(getApplicationContext(), TagManageActivity.class);
		startActivity(i);
	}
	
	private void buildActivityUi () {
        mClock = (ClockSurfaceView) findViewById(R.id.clock);
        //mClock.setAlarmDataProvider(mAlarmMgmt);
        mTime = (TextView) findViewById(R.id.timeTV);

        mClock.getViewTreeObserver().addOnGlobalLayoutListener(this);
        
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
        
        if (NfcAlarmApp.hasFlag(R.bool.debug)) {
        	View top = getWindow().getDecorView().findViewById(android.R.id.content);
        	top.setBackgroundColor(Color.RED);
        }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume()");
		
		/**
		 * If there is no NFC tag (ex. first launch), 
		 * open RegisterTagActivity and finish.
		 */
		if ( mPrefHelper.getTags() == null || mPrefHelper.getTags().size() == 0) {
			registerFirstTag();
			return;
		} else {
			Log.d(TAG, "Registered tags: " + mPrefHelper.getTags().size());
			Iterator<String> tags = mPrefHelper.getTags().iterator();
			while (tags.hasNext()) {
				Log.d(TAG, "  " + tags.next());
			}
		}
		
		if (WakeUpService.isRunning(getApplicationContext())) {
			Log.d(TAG, "onResume(): Started, but alarm is ringing, so switching to correct view");
			Intent startApp = new Intent(this, WakeUpActivity.class);
			startApp.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(startApp);
			finish();
		}
		
		mAlarmMgmt.restore();
		if ( mAlarmMgmt.getAlarmCount() == 0 ) {
			Log.d(TAG, "Adding default alarm.");
			Alarm defaultAlarm = new Alarm(Time.makeAbsolute(getDefaultAlarmTime()), false);
			mAlarmMgmt.addAlarm(defaultAlarm);
			mAlarmMgmt.persist();
		}
		//mAlarmMgmt.getSelectedAlarm().addObserver(this);
		/*mClock.setAlarmTime(getSelectedAlarm().getTime());
		mClock.setAlarmEnabled(getSelectedAlarm().getEnabled());
		mClock.addAlarmTimeChangedListener(this);*/
		Log.d(TAG, "Selected alarm: " + mAlarmMgmt.getAlarm() + " - " + mAlarmMgmt.getAlarm().getTime());
		mClock.setAlarm(getSelectedAlarm());
		
		getSelectedAlarm().getTime().addOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(Time time, Time oldTime) {
				refreshInterfaceTime();
				
				if (oldTime.isMorning() != time.isMorning()) {
					refreshInterfaceTimeOfDay();
				}
			}
		});
		
		getSelectedAlarm().addListener(new OnAlarmStateChangedListener() {
			@Override
			public void onAlarmStateChanged(Alarm alarm, boolean enabled) {
				refreshInterfaceEnabled();
			}
		});
		
		refreshInterfaceTime();
		refreshInterfaceTimeOfDay();
		refreshInterfaceEnabled();
	}

	private Alarm getSelectedAlarm() {
		return mAlarmMgmt.getAlarm();
	}

	public void setAlarm (View v) {
		Log.d(TAG, "setAlarm(): Setting an alarm or disabling it");

		boolean isAlarmOn = !getSelectedAlarm().getEnabled();
		getSelectedAlarm().setEnabled(isAlarmOn);
		mAlarmMgmt.persist();
		
		AlarmTrigger trigger;
		if (NfcAlarmApp.hasFlag(R.bool.debug_alarm_in_5_sec)) {
			Alarm fake = new Alarm(Time.makeRelative(5 * Time.SECOND), true);
			trigger = new AlarmTrigger(fake);
		} else {
			trigger = new AlarmTrigger(getSelectedAlarm());
		}
		
		if (isAlarmOn) {
			trigger.schedule();
		} else {
			trigger.cancel();
		}
	}

	@Override
	public void onGlobalLayout() {
        mClock.forceRepaint();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.attribution:
			Log.d(TAG, "Displaying attribution info");
			showAboutDialog();
			break;
		case R.id.manageTags:
			Log.d(TAG, "Registerin new, additional tag");
			manageTags();
			break;
		}
		
		return true;
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
		Time selectedAlarmTime = getSelectedAlarm().getTime();
		boolean morning = (pos == 0);
		if ( (selectedAlarmTime.isMorning() && !morning) || (!selectedAlarmTime.isMorning() && morning) ) {
			selectedAlarmTime.toggleAmPm();
		}
		mAlarmMgmt.persist();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		Log.w(TAG, "nothing selected in time of day spinner");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause()");
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart()");
		
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop()");
		
		EasyTracker.getInstance(this).activityStop(this);
	}
	
	private void refreshInterfaceTime () {
		Log.d(TAG, "Refreshing");
		TimeHumanReadable time = new TimeHumanReadable(getSelectedAlarm().getTime());
		mTime.setText(time.toString());
	}
	
	private void refreshInterfaceTimeOfDay () {
		mTimeOfDay.setSelection(getSelectedAlarm().getTime().isMorning() ? 0 : 1);
	}
	
	private void refreshInterfaceEnabled () {
		Button enableBtn = (Button) findViewById(R.id.enableAlarmBtn);
		
		if ( getSelectedAlarm().getEnabled() ) {
			Log.d(TAG, "refreshIntrefaceEnabled(): Alarm enabled");
			enableBtn.setText(R.string.disable_alarm);
			mTimeOfDay.setEnabled(false);
		} else {
			Log.d(TAG, "refreshIntrefaceEnabled(): Alarm disabled");
			enableBtn.setText(R.string.enable_alarm);
			mTimeOfDay.setEnabled(true);
		}
	}
	
	private long getDefaultAlarmTime() {
		return Time.HOUR * getResources().getInteger(R.integer.alarm_default_hour)
				+ Time.MINUTE * getResources().getInteger(R.integer.alarm_default_minute);
	}
	
	// TODO: not working now
/*
	@Override
	public void timeChanged(final AlarmTime alarm, int newSeconds,
			boolean timeOfDayChanged) {
		if ( alarm.equals(mAlarmMgmt.getSelectedAlarm()) ) {
			//Log.d(TAG, "Selected alarm change. Updating time. Time of day changed: "+timeOfDayChanged);
			
			refreshInterfaceTime();
			
			if ( timeOfDayChanged ) {
				Log.d(TAG, "Changing time of day spinner");
				mTimeOfDaySpinnerHandler.post(new Runnable() {
					@Override
					public void run() {
						mTimeOfDay.setSelection(alarm.isMorning() ? 0 : 1, false);		
					}
				});
			}
		}
	}
*/
	
	// TODO: not working now
	/*
	@Override
	public void statusChanged(AlarmTime alarm, boolean isEnabled) {
		refreshInterfaceEnabled();
	}*/
}
