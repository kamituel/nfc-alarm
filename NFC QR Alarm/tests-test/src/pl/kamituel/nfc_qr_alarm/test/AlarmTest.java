package pl.kamituel.nfc_qr_alarm.test;

import pl.kamituel.nfc_qr_alarm.alarm.Alarm;
import pl.kamituel.nfc_qr_alarm.alarm.OnAlarmStateChangedListener;
import pl.kamituel.nfc_qr_alarm.time.Time;

public class AlarmTest extends DeltaTestCase {
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
		assertEquals(expected, a1.getTime().getAbsolute());
	}
	
	public void testGetCountdownInFuture() {
		Alarm a1 = new Alarm(Time.makeRelative(5000), true);		
		long value = a1.getCountdown();
		long expected = 5000;
		
		assertTrue("Out of range: " + value + ", expected: " + expected, delta(expected, value));
	}
	
	public void testGetCountdownInFuture2() {
		Alarm a1 = new Alarm(Time.makeRelative(72 * Time.HOUR + 35 * Time.MINUTE), true);
		long value = a1.getCountdown();
		long expected = 35 * Time.MINUTE;
		
		assertTrue("Out of range: " + value + ", expected: " + expected, delta(expected, value));
	}
	
	public void testGetCountdownInPast() {
		Alarm a1 = new Alarm(Time.makeRelative(-5000), true);
		long value = a1.getCountdown();
		long expected = 24 * Time.HOUR - 5000;
		
		assertTrue("Out of range: " + value + ", expected: " + expected, delta(expected, value));
	}
	
	public void testSetListener() {
		final boolean initEnabled = false;
		final Alarm a1 = new Alarm(Time.makeAbsolute(0), initEnabled);
		a1.addListener(new OnAlarmStateChangedListener() {
			@Override
			public void onAlarmStateChanged(Alarm alarm, boolean enabled) {
				assertEquals(!initEnabled, enabled);
				assertSame(a1, alarm);
			}
		});
		
		a1.setEnabled(true);
	}
}
