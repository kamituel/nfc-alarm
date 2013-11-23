package pl.kamituel.nfc_qr_alarm.time;

import pl.kamituel.nfc_qr_alarm.alarm.Alarm;

public class CountdownDecorator {
	private Alarm mAlarm;
	
	public CountdownDecorator(Alarm alarm) {
		mAlarm = alarm;
	}
	
	public int getHours() {
		return (int)(mAlarm.getCountdown() / Time.HOUR);
	}
	
	public int getMinutes() {
		return (int)((mAlarm.getCountdown() - getHours() * Time.HOUR) / Time.MINUTE);
	}
}
