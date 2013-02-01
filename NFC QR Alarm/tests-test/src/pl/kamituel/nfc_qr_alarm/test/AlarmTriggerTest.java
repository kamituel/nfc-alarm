package pl.kamituel.nfc_qr_alarm.test;

import java.util.Calendar;
import java.util.Locale;

import pl.kamituel.nfc_qr_alarm.AlarmMgmt;
import pl.kamituel.nfc_qr_alarm.AlarmTrigger;
import pl.kamituel.nfc_qr_alarm.TimeUtils;
import pl.kamituel.nfc_qr_alarm.WakeUpService;
import android.content.Intent;
import android.test.ServiceTestCase;

public class AlarmTriggerTest extends ServiceTestCase<AlarmTrigger> {

	public AlarmTriggerTest () {
		super(AlarmTrigger.class);
	}
	
	public AlarmTriggerTest(Class<AlarmTrigger> serviceClass) {
		super(serviceClass);
	}
	
	public void testOneAlarm () throws InterruptedException {
		AlarmMgmt am = new AlarmMgmt(getContext());
		
		Calendar ac = Calendar.getInstance(Locale.getDefault());
		int time = ac.get(Calendar.HOUR_OF_DAY)*TimeUtils.HOUR
				+ ac.get(Calendar.MINUTE)*TimeUtils.MINUTE
				+ ac.get(Calendar.SECOND)
				+ 5;
		am.addAlarm(TestTools.getTime(time, true), false);
		am.persist();
		
		assertFalse(WakeUpService.isRunning(getContext()));
		
		Intent i = new Intent(getContext(), AlarmTrigger.class);
		startService(i);
		
		Thread.sleep(1*1000);
		
		assertTrue(WakeUpService.isRunning(getContext()));
		
		AlarmTrigger2Test.sendCommand(getContext(), WakeUpService.CMD_STOP_ALARM);
		Thread.sleep(3000);
	}
}
