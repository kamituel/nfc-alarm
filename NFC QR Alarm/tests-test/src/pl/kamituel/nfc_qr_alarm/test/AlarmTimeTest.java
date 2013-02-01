package pl.kamituel.nfc_qr_alarm.test;

import pl.kamituel.nfc_qr_alarm.AlarmMgmt;
import pl.kamituel.nfc_qr_alarm.AlarmTime;
import pl.kamituel.nfc_qr_alarm.TimeUtils;
import android.test.AndroidTestCase;

public class AlarmTimeTest extends AndroidTestCase {
	
	public void testSorting () {
		AlarmMgmt am = new AlarmMgmt(getContext());
		
		AlarmTime a1 = TestTools.getTime(6*TimeUtils.HOUR, true);
		AlarmTime a2 = TestTools.getTime(2*TimeUtils.HOUR, false);
		AlarmTime a3 = TestTools.getTime(5*TimeUtils.HOUR, true); 
		
		am.addAlarm(a1, false);
		am.addAlarm(a2, false);
		am.addAlarm(a3, false);
		
		assertEquals(am.getAlarms().size(), 3);
		assertEquals(a3, am.getAlarms().get(0));
		assertEquals(a1, am.getAlarms().get(1));
		assertEquals(a2, am.getAlarms().get(2));
	}


}
