package pl.kamituel.nfc_qr_alarm.alarm;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import pl.kamituel.nfc_qr_alarm.time.Time;
import pl.kamituel.nfc_qr_alarm.time.TimeHelperUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Alarm {
	@Expose @SerializedName("time")
	private Time mTime;
	
	@Expose @SerializedName("enabled")
	private boolean mEnabled;
	
	private List<OnAlarmStateChangedListener> mListeners;
	
	private Alarm() {
		mListeners = new LinkedList<OnAlarmStateChangedListener>();
	}
	
	public Alarm(Time time, boolean enabled) {
		this();
		mTime = time;
		mEnabled = enabled;
	}
	
	public void addListener(OnAlarmStateChangedListener listener) {
		mListeners.add(listener);
	}
	
	public boolean isEnabled() {
		return mEnabled;
	}
	
	public void setEnabled(boolean enabled) {
		mEnabled = enabled;
		notifyListeners();
	}
	
	public boolean getEnabled() {
		return mEnabled;
	}
	
	public Time getTime() {
		return mTime;
	}

	public long getCountdown() {
		return getCountdown(Calendar.getInstance());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Alarm) {
			Alarm oAlarm = (Alarm) o;
			return oAlarm.mEnabled == mEnabled && oAlarm.getTime().equals(getTime());
		} else {
			return false;
		}
	}
	
	private long getCountdown(Calendar reference) {
		long ref = TimeHelperUtils.millisSinceMidnight(reference);
		long diff = mTime.getAbsolute() - ref;
		
		// If ie. ref=19.00 and val=17.00,
		// diff will be negative, because alarm is set
		// to ring tommorow, not today.
		if (diff < 0) {
			diff = 24 * Time.HOUR - (-diff);
		}
		
		return diff;
	}
	
	private void notifyListeners() {
		Iterator<OnAlarmStateChangedListener> listenersIt = mListeners.iterator();
		while (listenersIt.hasNext()) {
			listenersIt.next().onAlarmStateChanged(this, getEnabled());
		}
	}
}
