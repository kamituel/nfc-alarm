package pl.kamituel.nfc_qr_alarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefHelper {
	@SuppressWarnings("unused")
	private final static String TAG = PrefHelper.class.getSimpleName();
	
	private SharedPreferences mPrefs = null;
	
	public final static long ALARM_TIME_DEFAULT = 3600 * 7;
	private final static String PREF_TAG_ID = "tag_id";
	public final static String PREF_ALARMS = "alarms";
	
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
	
	public void clearAll () {
		SharedPreferences.Editor prefsEdit = mPrefs.edit();
		prefsEdit.clear();
		prefsEdit.commit();
	}
}
