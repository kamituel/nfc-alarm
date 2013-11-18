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
		return (int)((mTime.getAlarmCountdown() - getHours() * Time.HOUR) / Time.MINUTE);
	}
}
