package pl.kamituel.nfc_qr_alarm.alarm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import pl.kamituel.nfc_qr_alarm.time.Time;

public class Alarm {
	@Expose @SerializedName("time")
	private Time mTime;
	
	@Expose @SerializedName("enabled")
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

	@Override
	public boolean equals(Object o) {
		if (o instanceof Alarm) {
			Alarm oAlarm = (Alarm) o;
			return oAlarm.mEnabled == mEnabled && oAlarm.getTime().equals(getTime());
		} else {
			return false;
		}
	}
}
