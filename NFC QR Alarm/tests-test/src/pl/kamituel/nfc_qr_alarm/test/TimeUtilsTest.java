package pl.kamituel.nfc_qr_alarm.test;

import java.util.Calendar;

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
	
	public void testGetTimeFromSecondsFromMidnight() {
		int secondsFromMidnight = 5;
		
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		
		now.add(Calendar.SECOND, secondsFromMidnight);

		assertEquals(now.getTimeInMillis(), TimeUtils.getTimeFromSecondsFromMidnight(secondsFromMidnight, true).getTime());
	}
	
	public void testGetSecondsFromMidnight() {
		Calendar c = Calendar.getInstance();
		
		int H = 7;
		int M = 2;
		int S = 34;
		
		c.set(Calendar.HOUR_OF_DAY, H);
		c.set(Calendar.MINUTE, M);
		c.set(Calendar.SECOND, S);
		
		int expected = H * 60 * 60 + M * 60 + S;
		assertEquals(expected, TimeUtils.getSecondsFromMidnight(c));
	}
}
