package pl.kamituel.nfc_qr_alarm.time;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;

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
	private long mValue;
	
	/**
	 * Array of listeners to changes in this time.
	 */
	private LinkedList<OnTimeChangedListener> mListeners = new LinkedList<OnTimeChangedListener>();
	
	private Time (long value) {
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

	public long getAlarmCountdown() {
		return getAlarmCountdown(Calendar.getInstance());
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
	
	public long getTimeFromMidnight() {
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

	private long getAlarmCountdown(Calendar reference) {
		long ref = TimeHelperUtils.millisSinceMidnight(reference);
		long diff = mValue - ref;
		
		// If ie. ref=19.00 and val=17.00,
		// diff will be negative, because alarm is set
		// to ring tommorow, not today.
		if (diff < 0) {
			diff = 24 * HOUR - (-diff);
		}
		
		return diff;
	}
	
	private void normalize() {
		mValue = mValue % (24 * HOUR);
	}
}
