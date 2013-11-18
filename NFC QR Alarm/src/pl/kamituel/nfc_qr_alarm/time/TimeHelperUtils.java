package pl.kamituel.nfc_qr_alarm.time;

import java.util.Calendar;

public class TimeHelperUtils {
	public static long millisSinceMidnight(Calendar calendar) {
		Calendar copy = Calendar.getInstance();
		copy.setTimeInMillis(calendar.getTimeInMillis());
		copy.set(Calendar.HOUR_OF_DAY, 0);
		copy.set(Calendar.MINUTE, 0);
		copy.set(Calendar.SECOND, 0);
		copy.set(Calendar.MILLISECOND, 0);
		
		return calendar.getTimeInMillis() - copy.getTimeInMillis();
	}
}
