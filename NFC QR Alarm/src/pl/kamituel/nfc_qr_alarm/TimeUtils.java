package pl.kamituel.nfc_qr_alarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
	@SuppressWarnings("unused")
	private final static String TAG = TimeUtils.class.getSimpleName();
	public final static int MINUTE = 60;
	public final static int HOUR = 60 * MINUTE;
	public final static int TWELVE_HOUR = HOUR * 12;
	
	public static String getTimeFormatted (Date d) {
		return new SimpleDateFormat("h:mm", Locale.getDefault()).format(d);
	}
	
	/**
	 * Given an angle of the hour clock hand, calculates
	 * how many seconds has elapsed since midnight.
	 * @param angle 
	 * @param am Whether it is morning (true) or evening (false).
	 * @return
	 */
	public static int angleToSeconds (float angle, boolean am) {
		return (int)(angle/360f*TWELVE_HOUR) + (am ? 0 : TWELVE_HOUR);
	}
	
	public static float secondsToAngle (int seconds) {
		return 360f * (seconds % TWELVE_HOUR) / (float)TWELVE_HOUR; 
	}
	
	public static Date getTimeFromSecondsFromMidnight (int secondsFromMidnight) {		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		c.add(Calendar.SECOND, secondsFromMidnight);
		
		return c.getTime();
	}
	
	public static int getSecondsFromMidnight (Calendar c) {
		return c.get(Calendar.HOUR_OF_DAY) * HOUR
				+ c.get(Calendar.MINUTE) * MINUTE
				+ c.get(Calendar.SECOND);
	}
}
