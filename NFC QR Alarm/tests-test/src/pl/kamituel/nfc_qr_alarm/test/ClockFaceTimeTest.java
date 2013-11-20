package pl.kamituel.nfc_qr_alarm.test;

import pl.kamituel.nfc_qr_alarm.time.ClockFaceTime;
import pl.kamituel.nfc_qr_alarm.time.Time;

public class ClockFaceTimeTest extends DeltaTestCase {	
	public void testFromAngle() {
		double angle = 5.12d;
		boolean amTime = true;
		Time t1 = ClockFaceTime.fromAngle(angle, amTime);
		
		long value = t1.getAbsolute();
		long expected = 614400l;
		
		assertTrue("Out of range: " + value + ", expected: " + expected, delta(expected, value));
	}
	
	public void testSecondsFromAngle() {
		double angle = 264d;
		boolean amTime = false;
		
		long value = ClockFaceTime.millisecondsFromAngle(angle, amTime);
		long expected = 74880000l;
		
		assertEquals(expected, value);
	}
	
	public void testToAngle() {
		Time t1 = Time.makeAbsolute(9 * Time.HOUR);
		double value = new ClockFaceTime(t1).getAngle();
		double expected = 270d;
		
		assertEquals(expected, value);
	}
}
