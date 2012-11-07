package pl.kamituel.nfc_qr_alarm;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class PrefHelper {
	private final static String TAG = PrefHelper.class.getSimpleName();
	
	private SharedPreferences mPrefs = null;
	
	private final static long ALARM_TIME_DEFAULT = 3600 * 7;
	private final static String PREF_ALARM_TIME = "alarm_time";
	private final static String PREF_ALARM_ON = "alarm_on";
	private final static String PREF_TAG_ID = "tag_id";
	private final static String PREF_ALARM_TIME_OF_DAY = "alarm_time_of_day";
	
	public PrefHelper (Context ctx) {
		assert(ctx!=null);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
	}
	
	public void saveTag (String tag) {
		SharedPreferences.Editor prefsEdit = mPrefs.edit();
		prefsEdit.putString(PREF_TAG_ID, tag);
		prefsEdit.commit();	
	}
	
	public String getTag () {
		return mPrefs.getString(PREF_TAG_ID, null);
	}
	
	public void setAlarmOn (boolean on) {
		SharedPreferences.Editor prefsEdit = mPrefs.edit();
		prefsEdit.putBoolean(PREF_ALARM_ON, on);
		prefsEdit.commit();
	}
	
	public boolean getAlarmOn () {
		return mPrefs.getBoolean(PREF_ALARM_ON, false);
	}
	
	public void setAlarmTime (long time) {
		SharedPreferences.Editor prefsEdit = mPrefs.edit();
		prefsEdit.putLong(PREF_ALARM_TIME, time);
		prefsEdit.commit();
	}
	
	public long getAlarmTime () {
		return mPrefs.getLong(PREF_ALARM_TIME, ALARM_TIME_DEFAULT);
	}
	
	public void setAlarmInTheMorning (boolean am) {
		SharedPreferences.Editor prefsEdit = mPrefs.edit();
		prefsEdit.putBoolean(PREF_ALARM_TIME_OF_DAY, am);
		prefsEdit.commit();
	}
	
	public boolean getAlarmInTheMorning () {
		return mPrefs.getBoolean(PREF_ALARM_TIME_OF_DAY, true);
	}
	
	public long getAlarmTimeCorrect() {
		long sec = getAlarmTime();
		if ( !getAlarmInTheMorning() ) sec += 12 * 3600;
		return sec;
	}
	
	public Calendar getAlarmDueIn () {
		int nowSecSinceMidnight = TimeUtils.getSecondsFromMidnight(Calendar.getInstance());
		
		Calendar alarm = Calendar.getInstance();
		if ( nowSecSinceMidnight > getAlarmTimeCorrect() ) {
			Log.d(TAG, "Alarm for tommorow");
			alarm.add(Calendar.DAY_OF_YEAR, 1);
		} else {
			Log.d(TAG, "Alarm for today");
		}
		
		alarm.set(Calendar.HOUR_OF_DAY, 0);
		alarm.set(Calendar.MINUTE, 0);
		alarm.set(Calendar.SECOND, 0);
		
		alarm.add(Calendar.SECOND, (int)getAlarmTimeCorrect());

		return alarm;
	}
	
	public void clearAll () {
		SharedPreferences.Editor prefsEdit = mPrefs.edit();
		prefsEdit.clear();
		prefsEdit.commit();
	}
}
