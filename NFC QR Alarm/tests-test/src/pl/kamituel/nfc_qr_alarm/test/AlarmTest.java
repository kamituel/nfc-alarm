package pl.kamituel.nfc_qr_alarm.test;

import pl.kamituel.nfc_qr_alarm.alarm.Alarm;
import pl.kamituel.nfc_qr_alarm.time.Time;
import android.test.AndroidTestCase;

public class AlarmTest extends AndroidTestCase {
	public void testIsEnabledTrueFromConstructor() {
		Alarm a1 = new Alarm(null, true);
		assertTrue(a1.isEnabled());
	}
	
	public void testIsEnabledFalseFromConstructor() {
		Alarm a1 = new Alarm(null, false);
		assertFalse(a1.isEnabled());
	}
	
	public void testSetEnabled() {
		Alarm a1 = new Alarm(null, false);
		a1.setEnabled(true);
		assertTrue(a1.isEnabled());
	}
	
	public void testGetTime() {
		Alarm a1 = new Alarm(Time.makeAbsolute(5), true);
		long expected = 5l;
		assertEquals(expected, a1.getTime().getTimeFromMidnight());
	}
}
