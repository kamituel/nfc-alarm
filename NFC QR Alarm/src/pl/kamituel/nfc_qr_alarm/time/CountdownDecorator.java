package pl.kamituel.nfc_qr_alarm.time;

public class CountdownDecorator {
	private Time mTime;
	
	public CountdownDecorator(Time time) {
		mTime = time;
	}
	
	public int getHours() {
		return (int)(mTime.getAlarmCountdown() / Time.HOUR);
	}
	
	public int getMinutes() {
		long hours = mTime.getAlarmCountdown() / Time.HOUR;
		return (int)((mTime.getAlarmCountdown() - hours * Time.HOUR) / Time.MINUTE);
	}
}
