package pl.kamituel.nfc_qr_alarm.test;

import pl.kamituel.nfc_qr_alarm.AlarmTrigger;
import pl.kamituel.nfc_qr_alarm.WakeUpActivity;
import pl.kamituel.nfc_qr_alarm.alarm.Alarm;
import pl.kamituel.nfc_qr_alarm.time.Time;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.UiThreadTest;

public class AlarmTriggerTest extends InstrumentationTestCase {
	private Context mCtx;
	private ActivityMonitor mWakeUpMonitor;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		mCtx = getInstrumentation().getTargetContext();
		mWakeUpMonitor = getInstrumentation().addMonitor(WakeUpActivity.class.getName(), null, false);
	}

	@UiThreadTest
	public void testTriggersAlarm() {			
		Alarm a1 = new Alarm(Time.makeRelative(10 * Time.SECOND), true);
		AlarmTrigger trigger = new AlarmTrigger(mCtx, a1);
		trigger.schedule();
		
		WakeUpActivity activity = (WakeUpActivity) getInstrumentation()
				.waitForMonitorWithTimeout(mWakeUpMonitor, 60);
		
		assertNotNull("WakeUp activity not started", activity);
	}
}
