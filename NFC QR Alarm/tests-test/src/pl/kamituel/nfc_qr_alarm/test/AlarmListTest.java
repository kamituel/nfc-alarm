package pl.kamituel.nfc_qr_alarm.test;

import pl.kamituel.nfc_qr_alarm.alarm.Alarm;
import pl.kamituel.nfc_qr_alarm.alarm.AlarmList;
import android.test.AndroidTestCase;

public class AlarmListTest extends AndroidTestCase {
	public void testAlarmListEmpty() {
		AlarmList l1 = new AlarmList();
		assertEquals(0, l1.getAlarms().size());
	}
	
	public void testAddAlarm() {
		AlarmList l1 = new AlarmList();
		l1.addAlarm(new Alarm(null, true));
		
		assertEquals(1, l1.getAlarms().size());
	}
}
