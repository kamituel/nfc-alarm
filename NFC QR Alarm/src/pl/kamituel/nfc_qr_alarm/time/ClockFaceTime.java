package pl.kamituel.nfc_qr_alarm.time;

public class ClockFaceTime {
	private Time mTime;
	
	public ClockFaceTime(Time time) {
		mTime = time;
	}
	
	public static Time fromAngle(double angle, boolean amTime) {
		long msec = (long)((angle / 360f) * 12 * Time.HOUR + (amTime ? 0 : 12 * Time.HOUR));
		return Time.makeAbsolute(msec);
	}
	
	public double getAngle() {
		return 360d * (mTime.getTimeFromMidnight() % (12 * Time.HOUR)) / (double) (12 * Time.HOUR); 
	}
}
