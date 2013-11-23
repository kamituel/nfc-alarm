package pl.kamituel.nfc_qr_alarm.time;

import java.util.Locale;

public class TimeHumanReadable {
	private Time mTime;
	
	public TimeHumanReadable(Time time) {
		mTime = time;
	}
	
	public String toString() {
		long hour = mTime.getAbsolute() / Time.HOUR;
		long minute = (mTime.getAbsolute() - hour * Time.HOUR) / Time.MINUTE;
	
		// TODO: return 12/24 time depending on locale
		// http://stackoverflow.com/a/14182381/782609
		hour %= 12;

		return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
	}
}
