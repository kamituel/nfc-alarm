package pl.kamituel.nfc_qr_alarm.test;

import java.util.Calendar;
import java.util.Locale;

import pl.kamituel.nfc_qr_alarm.AlarmMgmt;
import pl.kamituel.nfc_qr_alarm.AlarmTrigger;
import pl.kamituel.nfc_qr_alarm.TimeUtils;
import pl.kamituel.nfc_qr_alarm.WakeUpService;
import android.content.Context;
import android.content.Intent;
import android.test.ServiceTestCase;
import android.util.Log;

public class AlarmTrigger2Test extends ServiceTestCase<AlarmTrigger> {
	public static final String TAG = AlarmTrigger2Test.class.getSimpleName();
	
	public AlarmTrigger2Test () {
		super(AlarmTrigger.class);
	}
	
	public AlarmTrigger2Test(Class<AlarmTrigger> serviceClass) {
		super(serviceClass);
	}
	
	public void testMultipleAlarms () throws InterruptedException {
		AlarmMgmt am = new AlarmMgmt(getContext());
		
		Calendar ac = Calendar.getInstance(Locale.getDefault());
		int time = ac.get(Calendar.HOUR_OF_DAY)*TimeUtils.HOUR
				+ ac.get(Calendar.MINUTE)*TimeUtils.MINUTE
				+ ac.get(Calendar.SECOND)
				+ 5;
		am.addAlarm(TestTools.getTime(time, true), false);
		am.addAlarm(TestTools.getTime(time+65, true), false);
		am.persist();
		
		assertFalse(WakeUpService.isRunning(getContext()));
		
		Intent i = new Intent(getContext(), AlarmTrigger.class);
		startService(i);
		
		Thread.sleep(1*1000);
		assertTrue(WakeUpService.isRunning(getContext()));
		
		sendCommand(getContext(), WakeUpService.CMD_STOP_ALARM);
		Thread.sleep(3000);
		assertFalse(WakeUpService.isRunning(getContext()));
		
		Thread.sleep(68*1000);
		assertTrue(WakeUpService.isRunning(getContext()));
		
		sendCommand(getContext(), WakeUpService.CMD_STOP_ALARM);
		Thread.sleep(3000);
	}
	
	public static void sendCommand (Context ctx, int cmd) {
		Log.d(TAG, "Sending command: "+cmd);
		Intent i = new Intent(ctx, WakeUpService.class);
		i.putExtra(WakeUpService.COMMAND, cmd);
		ctx.startService(i);
	}
}
