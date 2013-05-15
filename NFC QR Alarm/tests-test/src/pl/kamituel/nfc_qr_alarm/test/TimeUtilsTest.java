package pl.kamituel.nfc_qr_alarm.test;

import junit.framework.TestCase;
import pl.kamituel.nfc_qr_alarm.TimeUtils;

public class TimeUtilsTest extends TestCase {
	public void testAngleToSeconds () {
		assertEquals(600, TimeUtils.angleToSeconds(5f, true));
		assertEquals(43200+600, TimeUtils.angleToSeconds(5f, false));
	}
	
	public void testSecondsToAngle () {
		assertEquals(5f, TimeUtils.secondsToAngle(600));
		assertEquals(5f, TimeUtils.secondsToAngle(43200+600));
	}
}
