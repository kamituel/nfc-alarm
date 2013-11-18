package pl.kamituel.nfc_qr_alarm.test;

import pl.kamituel.nfc_qr_alarm.AlarmTrigger;
import pl.kamituel.nfc_qr_alarm.WakeUpService;
import android.app.PendingIntent;
import android.content.Intent;
import android.test.AndroidTestCase;

public class AlarmTriggerTest extends AndroidTestCase {
	/*public void testScheduleIn5Sec() {
		AlarmTrigger trigger = new AlarmTrigger(getContext());
		
		int requestCode = 12345; // Same as in AlarmTrigger.java
		int alarmIn = 5000; // 5 sec
		trigger.schedule(alarmIn);
		
		Intent intent = new Intent(getContext(), WakeUpService.class);
		intent.putExtra(WakeUpService.COMMAND, WakeUpService.CMD_START_ALARM);
		boolean alarmUp = (PendingIntent.getService(getContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE)) != null;
		assertEquals(true, alarmUp);
	}*/
}
