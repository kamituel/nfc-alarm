package pl.kamituel.nfc_qr_alarm.alarm;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AlarmList {
	@Expose @SerializedName("alarms")
	private List<Alarm> mAlarms = new LinkedList<Alarm>();
	
	public void addAlarm(Alarm alarm) {
		mAlarms.add(alarm);
	}
	
	public List<Alarm> getAlarms() {
		return mAlarms;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AlarmList) {
			AlarmList oList = (AlarmList) o;
			
			if (oList.getAlarms().size() != mAlarms.size()) {
				return false;
			}
			
			for (int a = 0; a < mAlarms.size(); a += 1) {
				Alarm oAlarm = oList.getAlarms().get(a);
				Alarm alarm = mAlarms.get(a);
				
				if (!oAlarm.equals(alarm)) {
					return false;
				}
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	
}
