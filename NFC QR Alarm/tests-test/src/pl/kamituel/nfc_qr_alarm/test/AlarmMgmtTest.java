package pl.kamituel.nfc_qr_alarm.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import pl.kamituel.nfc_qr_alarm.AlarmMgmt;
import pl.kamituel.nfc_qr_alarm.PrefHelper;
import pl.kamituel.nfc_qr_alarm.alarm.Alarm;
import pl.kamituel.nfc_qr_alarm.alarm.AlarmList;
import pl.kamituel.nfc_qr_alarm.time.Time;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import android.test.mock.MockContentResolver;

public class AlarmMgmtTest extends AndroidTestCase {
	private IsolatedContext mCtx;
	
	public void setUp() {
		mCtx = new IsolatedContext(new MockContentResolver(), getContext());
	}
	
	public void testPersist() {		
		AlarmMgmt m1 = new AlarmMgmt(mCtx);
		m1.getAlarms().add(new Alarm(Time.makeAbsolute(5), true));
		m1.persist();
		
		PrefHelper pref = new PrefHelper(mCtx);
		
		assertNotNull(pref.getAlarms());
	}
	
	public void testRestore() {		
		PrefHelper pref = new PrefHelper(mCtx);
		pref.saveAlarms("{\"alarms\":[{\"enabled\": false, \"time\": {\"msec_since_midnight\": 5}}]}");
		
		AlarmMgmt m1 = new AlarmMgmt(mCtx);
		m1.restore();
		
		assertEquals(1, m1.getAlarms().size());
		assertEquals(false, m1.getAlarms().get(0).getEnabled());
		assertEquals(5, m1.getAlarms().get(0).getTime().getAbsolute());
	}
	
	public void testTryToRecoverFromOldVersion() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		String json = "[{\"seconds\": 23400, \"enabled\": false}]";
		AlarmMgmt m1 = new AlarmMgmt(mCtx);
		
		Method target = AlarmMgmt.class.getDeclaredMethod("tryToRecoverFromOldVersion", String.class);
		target.setAccessible(true);
		AlarmList value = (AlarmList) target.invoke(m1, json);
		
		AlarmList expected = new AlarmList();
		expected.addAlarm(new Alarm(Time.makeAbsolute(23400 * 1000), false));
		
		assertEquals(expected, value);
	}

}
