package pl.kamituel.nfc_qr_alarm.alarm;

import pl.kamituel.nfc_qr_alarm.time.Time;

public class Alarm {
	private Time mTime;
	private boolean mEnabled;
	
	public Alarm(Time time, boolean enabled) {
		mTime = time;
		mEnabled = enabled;
	}
	
	public boolean isEnabled() {
		return mEnabled;
	}
	
	public void setEnabled(boolean enabled) {
		mEnabled = enabled;
	}
	
	public boolean getEnabled() {
		return mEnabled;
	}
	
	public Time getTime() {
		return mTime;
	}
}
