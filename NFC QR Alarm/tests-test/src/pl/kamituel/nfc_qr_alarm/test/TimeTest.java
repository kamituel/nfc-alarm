package pl.kamituel.nfc_qr_alarm.test;

import pl.kamituel.nfc_qr_alarm.time.OnTimeChangedListener;
import pl.kamituel.nfc_qr_alarm.time.Time;

public class TimeTest extends DeltaTestCase {

	public void testGetTimeFromMidnight() {
		Time t1 = Time.makeAbsolute(5000);
		assertEquals(5000, t1.getTimeFromMidnight());
	}
	
	public void testGetCountdownInFuture() {
		Time t = Time.makeRelative(5000);		
		long value = t.getAlarmCountdown();
		long expected = 5000;
		
		assertTrue("Out of range: " + value + ", expected: " + expected, delta(expected, value));
	}
	
	public void testGetCountdownInFuture2() {
		Time t = Time.makeRelative(72 * Time.HOUR + 35 * Time.MINUTE);
		long value = t.getAlarmCountdown();
		long expected = 35 * Time.MINUTE;
		
		assertTrue("Out of range: " + value + ", expected: " + expected, delta(expected, value));
	}
	
	public void testgetCountdownInPast() {
		Time t = Time.makeRelative(-5000);
		long value = t.getAlarmCountdown();
		long expected = 24 * Time.HOUR - 5000;
		
		assertTrue("Out of range: " + value + ", expected: " + expected, delta(expected, value));
	}
	
	public void testToggleAmPm() {
		Time t1 = Time.makeRelative(0);
		helpTestToggleAmPm(t1);
	}
	
	public void testToggleAmPm2() {
		Time t1 = Time.makeRelative(12 * Time.HOUR);
		helpTestToggleAmPm(t1);
	}
	
	public void testValidCurrentTimeInListener() {
		final Time t1 = Time.makeAbsolute(Time.HOUR);
		t1.addOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(Time time, Time oldTime) {
				assertEquals(time, t1);
				assertEquals(time.getTimeFromMidnight(), 13 * Time.HOUR);
			}
		});
		
		t1.toggleAmPm();
	}
	
	public void testValidOldTimeInListener() {
		Time t1 = Time.makeRelative(0);
		final long timeFromMidnight = t1.getTimeFromMidnight();
		
		t1.addOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(Time time, Time oldTime) {
				assertEquals(oldTime.getTimeFromMidnight(), timeFromMidnight);
			}
		});
		
		t1.toggleAmPm();
	}
	
	public void testIsMorning() {
		Time t1 = Time.makeAbsolute(Time.HOUR);
		assertTrue(t1.isMorning());
	}
	
	public void testIsMorning2() {
		Time t1 = Time.makeAbsolute(13 * Time.HOUR);
		assertFalse(t1.isMorning());
	}
	
	public void testIsMorning3() {
		Time t1 = Time.makeAbsolute(0);
		assertTrue(t1.isMorning());
	}
	
	public void testIsMorning4() {
		Time t1 = Time.makeAbsolute(12 * Time.HOUR);
		assertFalse(t1.isMorning());
	}
	
	private void helpTestToggleAmPm(Time t1) {
		Time t2 = t1.clone();
		t2.toggleAmPm();
		
		long c1 = t1.getAlarmCountdown();
		long c2 = t2.getAlarmCountdown();
		
		long expected = 12 * Time.HOUR;
		long value = c1 > c2 ? c1 - c2 : c2 - c1;
		
		assertTrue("Out of range: " + value + ", expected: " + expected, delta(expected, value));
	}
}