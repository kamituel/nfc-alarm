package pl.kamituel.nfc_qr_alarm.test;

import pl.kamituel.nfc_qr_alarm.AlarmMgmt;
import pl.kamituel.nfc_qr_alarm.AlarmTime;
import pl.kamituel.nfc_qr_alarm.TimeUtils;
import android.test.AndroidTestCase;

public class AlarmTimeTest extends AndroidTestCase {
	
	private static final int M = TimeUtils.MINUTE;
	private static final int H = TimeUtils.HOUR;
	
	/*public void testSorting () {
		AlarmMgmt am = new AlarmMgmt(getContext());
		
		AlarmTime a1 = TestTools.getTime(6*H, true);
		AlarmTime a2 = TestTools.getTime(2*H, false);
		AlarmTime a3 = TestTools.getTime(5*H, true); 
		
		am.addAlarm(a1, false);
		am.addAlarm(a2, false);
		am.addAlarm(a3, false);
		
		assertEquals(am.getAlarms().size(), 3);
		assertTrue(am.getAlarms().get(0).getEnabled());
		assertTrue(am.getAlarms().get(1).getEnabled());
		assertFalse(am.getAlarms().get(2).getEnabled());
		assertTrue(am.getAlarms().get(0).getCountdown() < am.getAlarms().get(1).getCountdown());
	}

	public void testSerialize () {
		AlarmTime a1 = TestTools.getTime(452, true);
		String s1 = a1.serialize();
		AlarmTime a2 = AlarmTime.deserialize(s1);
		String s2 = a2.serialize();
		
		assertTrue(s1.length() > 0);
		assertTrue(s2.length() > 0);
		assertEquals(s1, s2);
		assertEquals(a1.get(), a2.get());
		assertEquals(a1.getEnabled(), a2.getEnabled());
	}
	
	public void testGet () {
		AlarmTime a1 = TestTools.getTime(5*M+10, true);
		assertEquals(5*M, a1.get());
		
		AlarmTime a2 = TestTools.getTime(9*M+59, true);
		assertEquals(5*M, a2.get());
		
		AlarmTime a3 = TestTools.getTime(10*M, true);
		assertEquals(10*M, a3.get());
	}
	
	public void testSet () {
		AlarmTime a1 = TestTools.getTime(0, true);
		a1.set(5*M+20);
		assertEquals(5*M, a1.get());
	}
	
	public void testSetOverflow () {
		AlarmTime a1 = TestTools.getTime(30*H+75, true);
		assertEquals(6*H, a1.get());
	}
	
	public void testAdd () {
		AlarmTime a1 = TestTools.getTime(5*M+78, true);
		a1.add(3*M);
		assertEquals(5*M, a1.get());
		
		a1.add(7*M);
		assertEquals(10*M, a1.get());
	}

	public void testAddOverflow () {
		AlarmTime a1 = TestTools.getTime(20*H+12, true);
		a1.add(6*H);
		assertEquals(2*H, a1.get());
	}*/
}
