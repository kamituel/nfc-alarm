package pl.kamituel.nfc_qr_alarm;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefHelper {	
	private SharedPreferences mPrefs = null;
	
	//public final static long ALARM_TIME_DEFAULT = 3600 * 7;
	
	@Deprecated
	private final static String PREF_TAG_ID_OLD = "tag_id";
	
	private final static String PREF_TAG = "tag_set";
	private final static String PREF_ALARMS = "alarms";
	
	public PrefHelper (Context ctx) {
		mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		
		if (getTags() == null) {
			// Attempt to migrate from PREF_TAG_ID_OLD (string, single tag)
			// to PREF_TAG (string set, multiple tags)
			String tag = mPrefs.getString(PREF_TAG_ID_OLD, null);
			if (tag != null) {
				saveTag(tag);
				
				SharedPreferences.Editor prefsEdit = mPrefs.edit();
				prefsEdit.remove(PREF_TAG_ID_OLD);
				prefsEdit.commit();
			}
		}
	}
	
	public void saveTag (String tag) {
		Set<String> tags = getTags();
		if (tags == null) {
			tags = new HashSet<String>();
		}
		
		tags.add(tag);
		
		SharedPreferences.Editor prefsEdit = mPrefs.edit();
		prefsEdit.putStringSet(PREF_TAG, tags);
		prefsEdit.commit();	
	}
	
	public void removeTag (String tag) {
		Set<String> tags = getTags();
		tags.remove(tag);
		
		SharedPreferences.Editor prefsEdit = mPrefs.edit();
		prefsEdit.putStringSet(PREF_TAG, tags);
		prefsEdit.commit();
	}
	
	public Set<String> getTags () {
		return mPrefs.getStringSet(PREF_TAG, null);
	}
	
	public void saveAlarms(String alarmsJson) {
		SharedPreferences.Editor prefsEdit = mPrefs.edit();
		prefsEdit.putString(PREF_ALARMS, alarmsJson);
		prefsEdit.commit();
	}
	
	public String getAlarms() {
		return mPrefs.getString(PREF_ALARMS, null);
	}
	
	public void clearAll () {
		SharedPreferences.Editor prefsEdit = mPrefs.edit();
		prefsEdit.clear();
		prefsEdit.commit();
	}
}
