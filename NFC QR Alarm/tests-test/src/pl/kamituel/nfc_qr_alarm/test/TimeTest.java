package pl.kamituel.nfc_qr_alarm.test;

import pl.kamituel.nfc_qr_alarm.time.OnTimeChangedListener;
import pl.kamituel.nfc_qr_alarm.time.Time;

public class TimeTest extends DeltaTestCase {

	public void testGetTimeFromMidnight() {
		Time t1 = Time.makeAbsolute(5000);
		assertEquals(5000, t1.getAbsolute());
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
				assertEquals(time.getAbsolute(), 13 * Time.HOUR);
			}
		});
		
		t1.toggleAmPm();
	}
	
	public void testValidOldTimeInListenerForTogglePm() {
		Time t1 = Time.makeRelative(0);
		final long timeFromMidnight = t1.getAbsolute();
		
		t1.addOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(Time time, Time oldTime) {
				assertEquals(oldTime, Time.makeAbsolute(timeFromMidnight));
			}
		});
		
		t1.toggleAmPm();
	}
	
	public void testValidOldTimeInListenerForSetAbsolute() {
		Time t1 = Time.makeRelative(0);
		final long timeFromMidnight = t1.getAbsolute();
		
		t1.addOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(Time time, Time oldTime) {
				assertEquals(oldTime, Time.makeAbsolute(timeFromMidnight));
			}
		});
		
		t1.setAbsolute(5);
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
	
	public void testEqualsTrue() {
		Time t1 = Time.makeAbsolute(541);
		Time t2 = Time.makeAbsolute(541);
		assertEquals(t1, t2);
	}
	
	public void testEqualsFalse() {
		Time t1 = Time.makeAbsolute(541);
		Time t2 = Time.makeAbsolute(542);
		assertFalse("Are equal, shouldn't", t1.equals(t2));
	}
	
	public void testSet() {
		Time t1 = Time.makeAbsolute(5);
		t1.setAbsolute(654);
		assertEquals(654, t1.getAbsolute());
	}
	
	private void helpTestToggleAmPm(Time t1) {
		Time t2 = t1.clone();
		t2.toggleAmPm();
		
		long c1 = t1.getAbsolute();
		long c2 = t2.getAbsolute();
		
		long expected = 12 * Time.HOUR;
		long value = c1 > c2 ? c1 - c2 : c2 - c1;
		
		assertTrue("Out of range: " + value + ", expected: " + expected, delta(expected, value));
	}
}
