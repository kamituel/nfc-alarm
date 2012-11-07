package pl.kamituel.nfc_qr_alarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
	private final static String TAG = TimeUtils.class.getSimpleName();
	private final static int MINUTE = 60;
	private final static int HOUR = 60 * MINUTE;
	
	public static String getTimeFormatted (Date d) {
		return new SimpleDateFormat("h:mm").format(d);
	}
	
	public static int getSecondsFromMidnight (double angle) {
		return (int)((angle/360) * (12*HOUR));
	}
	
	public static int getAngleFromSecondsFromMidnight (int secondsTill12) {
		return (int)(360f * secondsTill12 / (12*HOUR));
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
	
	public static String toStr (Date d) {
		return new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z").format(d);
	}
	
	
	/*public static double getAngleFromTime (Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		
		return 360 * ((float)c.get(Calendar.HOUR)*60*60+c.get(Calendar.MINUTE)*60)/(12*60*60);
	}*/
	
	/*public static Date sanitizeDate (Date d) {
	Calendar c = Calendar.getInstance();
	c.setTime(d);
	
	
	int minute = c.get(Calendar.MINUTE);
	//if ( minute % 5 <= 2 ) c.set(Calendar.MINUTE, minute - (minute%5));
	//else c.set(Calendar.MINUTE, (int)Math.floor(minute/5)+5 );
	c.set(Calendar.MINUTE, minute - (minute%5));

	//Log.d(TAG, "minute before "+minute+" and after "+c.get(Calendar.MINUTE));
	
	
	return c.getTime();
}*/
}
