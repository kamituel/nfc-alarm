package pl.kamituel.nfc_qr_alarm.time;

public class ClockFaceTime {
	private Time mTime;
	
	public ClockFaceTime(Time time) {
		mTime = time;
	}
	
	public static Time fromAngle(double angle, boolean amTime) {
		return Time.makeAbsolute(millisecondsFromAngle(angle, amTime));
	}
	
	public static long millisecondsFromAngle(double angle, boolean amTime) {
		return (long)((angle / 360f) * 12 * Time.HOUR + (amTime ? 0 : 12 * Time.HOUR));
	}
	
	public double getAngle() {
		return angleFromMillis(mTime.getAbsolute());
	}
	
	public static double angleFromMillis(long millis) {
		return 360d * (millis % (12 * Time.HOUR)) / (double) (12 * Time.HOUR);
	}
}
