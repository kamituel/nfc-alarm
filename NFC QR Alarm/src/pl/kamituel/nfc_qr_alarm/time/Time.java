package pl.kamituel.nfc_qr_alarm.time;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * All values are in milliseconds.
 * @author kls
 *
 */
public class Time {
	public final static long SECOND = 1000;
	public final static long MINUTE = 60 * SECOND;
	public final static long HOUR = 60 * MINUTE;
	
	/**
	 * Number of milliseconds since midnight.
	 * Value in range <0; 24*HOUR).
	 */
	@Expose @SerializedName("msec_since_midnight")
	private long mValue;
	
	/**
	 * Array of listeners to changes in this time.
	 */
	private List<OnTimeChangedListener> mListeners;
	
	private Time() {
		mListeners = new LinkedList<OnTimeChangedListener>();
	}
	
	private Time (long value) {
		this();
		mValue = value;
	}
	
	public static Time makeRelative(long msec) {
		Calendar value = Calendar.getInstance();
		value.add(Calendar.MILLISECOND, (int)msec);
		
		return new Time(TimeHelperUtils.millisSinceMidnight(value));
	}
	
	public static Time makeAbsolute(long msec) {
		return new Time(msec);
	}
	
	public void setAbsolute(long value) {
		Time oldTime = clone();
		
		mValue = value;
		normalize();
		
		notifyListeners(oldTime);
	}
	
	public void toggleAmPm() {
		Time oldTime = clone();
		
		mValue += 12 * HOUR;
		normalize();
		
		notifyListeners(oldTime);
	}
	
	public boolean isMorning() {
		return (mValue < 12 * HOUR);
	}
	
	public Time clone() {
		return new Time(mValue);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Time) {
			Time oTime = (Time) o;
			return oTime.mValue == mValue;
		} else {
			return false;
		}
	}

	/*
	 * Returns time from midnight.
	 */
	public long getAbsolute() {
		return mValue;
	}
	
	public void addOnTimeChangedListener(OnTimeChangedListener listener) {
		mListeners.add(listener);
	}
	
	public void notifyListeners(Time oldTime) {
		Iterator<OnTimeChangedListener> listenersIt = mListeners.iterator();
		while (listenersIt.hasNext()) {
			listenersIt.next().onTimeChanged(this, oldTime);
		}
	}
	
	private void normalize() {
		mValue = mValue % (24 * HOUR);
	}
}
