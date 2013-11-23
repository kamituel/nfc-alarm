package pl.kamituel.nfc_qr_alarm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.kamituel.nfc_qr_alarm.alarm.Alarm;
import pl.kamituel.nfc_qr_alarm.alarm.AlarmList;
import pl.kamituel.nfc_qr_alarm.time.Time;
import pl.kamituel.nfc_qr_alarm.tools.JsonSerializer;
import pl.kamituel.nfc_qr_alarm.tools.JsonSerializer.InvalidJsonException;
import android.content.Context;
import android.util.Log;

public class AlarmMgmt {
	private final static String TAG = AlarmMgmt.class.getSimpleName();

	private AlarmList mAlarms;
	private PrefHelper mPrefHelper;
	
	private int mSelectedAlarm;
	
	public AlarmMgmt(Context ctx) {
		mPrefHelper = new PrefHelper(ctx);
		mAlarms = new AlarmList();
		mSelectedAlarm = 0;
	}
	
	public void selectAlarm(int index) {
		if (index < 0 || index >= mAlarms.getAlarms().size()) {
			throw new IndexOutOfBoundsException("Requested alarm index " + index 
					+ " but only " + (mAlarms.getAlarms().size() - 1) + " alarms defined");
		}
		
		mSelectedAlarm = index;
	}
	
	public Alarm getAlarm() {
		return mAlarms.getAlarms().get(mSelectedAlarm);
	}
	
	public int getAlarmCount() {
		return mAlarms.getAlarms().size();
	}
	
	public void addAlarm(Alarm alarm) {
		mAlarms.addAlarm(alarm);
	}
	
	public void persist() {		
		String alarmsJson = JsonSerializer.toJson(mAlarms);
		mPrefHelper.saveAlarms(alarmsJson);
		
		Log.d(TAG, "Alarm list saved. " + mAlarms.getAlarms().size() + " alarms.");
	}
	
	public void restore() {		
		String alarmsJson = mPrefHelper.getAlarms();
		Log.d(TAG, "Restoring alarms from JSON: <<" + alarmsJson + ">>");
		
		try {
			mAlarms = JsonSerializer.fromJson(alarmsJson, AlarmList.class);
		} catch (InvalidJsonException e) {
			mAlarms = tryToRecoverFromOldVersion(alarmsJson);
			
			if (mAlarms != null) {
				persist();
			}
		} finally {
			if (mAlarms == null) {
				mAlarms = new AlarmList();
			}
		}
	}
	
	/**
	 * Recovers from the old JSON format, i.e:
	 *   [{"seconds": 23400, "enabled": false}]
	 *   
	 * Note that even though it's an array, only one element inside is expected.
	 * 
	 * @param json
	 * @return
	 */
	@Deprecated
	private AlarmList tryToRecoverFromOldVersion (String json) {
		try {
			Matcher enabledMatcher = Pattern.compile(".*\"enabled\":\\s*(true|false).*").matcher(json);
			Matcher secondsMatcher = Pattern.compile(".*\"seconds\":\\s*(\\d+).*").matcher(json);
			
			if (enabledMatcher.matches() && secondsMatcher.matches()) {
				boolean enabled = Boolean.parseBoolean(enabledMatcher.group(1));
				long seconds = Long.parseLong(secondsMatcher.group(1));
				
				Log.d(TAG, "Recovered alarm enabled=" + enabled + ", seconds=" + seconds);
				
				AlarmList list = new AlarmList();
				list.addAlarm(new Alarm(Time.makeAbsolute(seconds * 1000), enabled));
				
				return list;
			} else {
				Log.d(TAG, "Could not recover alarm from JSON (#1): <<" + json + ">>");
				return null;
			}
		} catch (Exception e) {
			Log.d(TAG, "Could not recover alarm from JSON (#2): <<" + json + ">>");
			return null;
		}
	}
}
