package pl.kamituel.nfc_qr_alarm;

import java.util.List;
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
	
	public AlarmMgmt(Context ctx) {
		mPrefHelper = new PrefHelper(ctx);
		mAlarms = new AlarmList();
	}
	
	public void persist() {		
		String alarmsJson = JsonSerializer.toJson(mAlarms);
		mPrefHelper.saveAlarms(alarmsJson);
		
		Log.d(TAG, "Alarm list saved. " + mAlarms.getAlarms().size() + " alarms.");
	}
	
	public void restore() {		
		String alarmsJson = mPrefHelper.getAlarms();
		
		try {
			mAlarms = JsonSerializer.fromJson(alarmsJson, AlarmList.class);
		} catch (InvalidJsonException e) {
			mAlarms = tryToRecoverFromOldVersion(alarmsJson);
			
			if (mAlarms != null) {
				persist();
			} else {
				mAlarms = new AlarmList();
			}
		}
	}
	
	public List<Alarm> getAlarms() {
		return mAlarms.getAlarms();
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
	
	/*private final List<AlarmTime> mAlarms = new ArrayList<AlarmTime>();
	private Context mCtx = null;
	
	private final static String TAG_SELECTED = "selected";
	
	private final Set<Observer> mObservers = new HashSet<Observer>();
	
	public AlarmMgmt (Context ctx) {
		mCtx = ctx;
		PreferenceManager.getDefaultSharedPreferences(mCtx).registerOnSharedPreferenceChangeListener(this);
	}
	
	public void persist () {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mCtx);
		StringBuilder serialized = new StringBuilder();
		Iterator<AlarmTime> alarmsIt = mAlarms.iterator();
		
		serialized.append("[");
		
		while ( alarmsIt.hasNext() ) {
			serialized.append(alarmsIt.next().serialize());
			if ( alarmsIt.hasNext() ) serialized.append(",");
		}
		
		serialized.append("]");
		
		Editor prefEdit = pref.edit();
		prefEdit.putString(PrefHelper.PREF_ALARMS, serialized.toString());
		prefEdit.commit();
		
		Log.d(TAG, "Done persisting alarms: <<"+serialized.toString()+">>");
	}
	
	public void restore () {
		mAlarms.clear();
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mCtx);
		String alarmsS = pref.getString(PrefHelper.PREF_ALARMS, null);
		
		Log.d(TAG, "Serialized alarms loaded from preferences: "+alarmsS);
		if ( alarmsS == null ) {
			Log.w(TAG, "No serialized alarms loaded from preferences. Not restoring");
			return;
		}
		
		alarmsS = alarmsS.replaceAll("^\\[", "").replaceAll("\\]$", "");
		
		//TODO: JSONify - split it correctly
		String [] alarmsA = alarmsS.split("\\},\\{");
		for ( String a : alarmsA ) {
			Log.d(TAG, "Found alarm <<"+a+">>");
			AlarmTime alarm = AlarmTime.deserialize(a);
			alarm.addObserver(this);
			addAlarm(alarm, false);
		}
	}
	
	@Override
	public List<AlarmTime> getAlarms () {
		// TODO: Not very efficient, I know. But, due to the 
		// low number of alarms, I consider it to be okay for now.
		Collections.sort(mAlarms);
		return mAlarms;
	}
	
	@Override
	public void addAlarm (AlarmTime alarm, Boolean selected) {
		if ( selected || mAlarms.size() == 0 ) {
			unselectAllAlarms();
			alarm.addTag(TAG_SELECTED, Boolean.TRUE);
		}
		mAlarms.add(alarm);
	}
	
	@Override
	public void removeAlarm (AlarmTime alarm) {
		// TODO: remove. Be careful with 'selected' alarm.
		
	}
	
	@Override
	public AlarmTime getSelectedAlarm() {
		for ( AlarmTime alarm : mAlarms ) {
			if ( alarm.getTag(TAG_SELECTED) != null && alarm.getTag(TAG_SELECTED).equals(Boolean.TRUE) ) {
				return alarm;
			}
		}
		Log.e(TAG, "No selected alarm? That should not be possible.");
		return null;
	}
	
	private void unselectAllAlarms () {
		for ( AlarmTime alarm : mAlarms ) {
			alarm.addTag(TAG_SELECTED, Boolean.FALSE);
		}
	}

	@Override
	public void timeChanged(AlarmTime alarm, int newSeconds, boolean timeOfDayChanged) {
		//Log.d(TAG, "timeChanged(): "+alarm.toString()+", timeOfDayChanged: "+timeOfDayChanged);
	}

	@Override
	public void statusChanged(AlarmTime alarm, boolean isEnabled) {
		Log.d(TAG, "statusChanged(): "+alarm.toString());
	}
	
	@Override
	public void commit () {
		persist();
	}

	@Override
	public void addObserver(Observer o) {
		mObservers.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		mObservers.remove(o);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		Log.d(TAG, "SharedPreference changed: "+arg1);
	}*/
}
