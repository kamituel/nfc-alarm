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
		return 360d * (mTime.getAbsolute() % (12 * Time.HOUR)) / (double) (12 * Time.HOUR); 
	}
}
