package pl.kamituel.nfc_qr_alarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;


public class AlarmMgmt implements AlarmDataProvider, AlarmTime.Observer, OnSharedPreferenceChangeListener {
	private final static String TAG = AlarmMgmt.class.getSimpleName();
	
	private final List<AlarmTime> mAlarms = new ArrayList<AlarmTime>();
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
	}
}
