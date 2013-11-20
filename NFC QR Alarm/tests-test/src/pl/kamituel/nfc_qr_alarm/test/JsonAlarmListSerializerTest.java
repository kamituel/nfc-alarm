package pl.kamituel.nfc_qr_alarm.test;

import pl.kamituel.nfc_qr_alarm.alarm.Alarm;
import pl.kamituel.nfc_qr_alarm.alarm.AlarmList;
import pl.kamituel.nfc_qr_alarm.time.Time;
import pl.kamituel.nfc_qr_alarm.tools.JsonSerializer;
import pl.kamituel.nfc_qr_alarm.tools.JsonSerializer.InvalidJsonException;
import android.test.AndroidTestCase;

public class JsonAlarmListSerializerTest extends AndroidTestCase {
	public void testToJson() {
		AlarmList l1 = new AlarmList();
		Alarm a1 = new Alarm(Time.makeAbsolute(5), true);
		Alarm a2 = new Alarm(Time.makeAbsolute(23), false);
		l1.addAlarm(a1);
		l1.addAlarm(a2);
		
		String value = JsonSerializer.toJson(l1);
		boolean isValid = value.matches("^\\{.*\\}$")
				&& value.matches(".*\"alarms\":\\s*\\[.*")
				&& value.matches(".*\\[\\s*\\{\\s*\".*");
		
		assertTrue("Invalid: <<" + value + ">>", isValid);
	}
	
	public void testFromJson() throws InvalidJsonException {
		Alarm a1 = new Alarm(Time.makeAbsolute(5), true);
		Alarm a2 = new Alarm(Time.makeAbsolute(23), false);
		String json = "{\"alarms\":[" + JsonSerializer.toJson(a1) + "," 
				+ JsonSerializer.toJson(a2) + "]}";
		
		AlarmList expected = new AlarmList();
		expected.addAlarm(a1);
		expected.addAlarm(a2);
		AlarmList value = JsonSerializer.fromJson(json, AlarmList.class);
		
		assertEquals(expected, value);
	}
	
	public void testEqualsTrue() {
		AlarmList l1 = new AlarmList();
		l1.addAlarm(new Alarm(Time.makeAbsolute(5), true));
		l1.addAlarm(new Alarm(Time.makeAbsolute(6), false));
		
		AlarmList l2 = new AlarmList();
		l2.addAlarm(new Alarm(Time.makeAbsolute(5), true));
		l2.addAlarm(new Alarm(Time.makeAbsolute(6), false));
		
		assertEquals(l1, l2);
	}
	
	public void testEqualsFalse() {
		AlarmList l1 = new AlarmList();
		l1.addAlarm(new Alarm(Time.makeAbsolute(5), true));
		l1.addAlarm(new Alarm(Time.makeAbsolute(6), false));
		
		AlarmList l2 = new AlarmList();
		l2.addAlarm(new Alarm(Time.makeAbsolute(5), true));
		l2.addAlarm(new Alarm(Time.makeAbsolute(6), true));  // diff here
		
		assertFalse("Equal while should not be", l1.equals(l2));
	}
}
