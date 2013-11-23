package pl.kamituel.nfc_qr_alarm.test;

import java.lang.reflect.Field;
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
		m1.addAlarm(new Alarm(Time.makeAbsolute(5), true));
		m1.persist();
		
		PrefHelper pref = new PrefHelper(mCtx);
		
		assertNotNull(pref.getAlarms());
	}
	
	public void testRestore() {		
		PrefHelper pref = new PrefHelper(mCtx);
		pref.saveAlarms("{\"alarms\":[{\"enabled\": false, \"time\": {\"msec_since_midnight\": 5}}]}");
		
		AlarmMgmt m1 = new AlarmMgmt(mCtx);
		m1.restore();
		
		assertEquals(1, m1.getAlarmCount());
		assertEquals(false, m1.getAlarm().getEnabled());
		assertEquals(5, m1.getAlarm().getTime().getAbsolute());
	}
	
	public void testRestoreAlarmTimeHasListenersListInitialized() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		PrefHelper pref = new PrefHelper(mCtx);
		pref.saveAlarms("{\"alarms\":[{\"enabled\": false, \"time\": {\"msec_since_midnight\": 5}}]}");
		
		AlarmMgmt m1 = new AlarmMgmt(mCtx);
		m1.restore();
		
		Field timeListeners = Time.class.getDeclaredField("mListeners");
		timeListeners.setAccessible(true);
		Object value = timeListeners.get(m1.getAlarm().getTime());
		
		assertNotNull(value);
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
	
	public void testSelectAlarm() {
		AlarmMgmt m1 = new AlarmMgmt(mCtx);
		Alarm a1 = new Alarm(Time.makeAbsolute(0), false);
		Alarm a2 = new Alarm(Time.makeAbsolute(0), false);
		m1.addAlarm(a1);
		m1.addAlarm(a2);
		
		m1.selectAlarm(1);
		
		assertSame(a2, m1.getAlarm());
	}
	
	public void testSelectAlarmInvalidIndex() {
		AlarmMgmt m1 = new AlarmMgmt(mCtx);
		Alarm a1 = new Alarm(Time.makeAbsolute(0), false);
		Alarm a2 = new Alarm(Time.makeAbsolute(0), false);
		m1.addAlarm(a1);
		m1.addAlarm(a2);
		
		boolean thrown = false;
		try {
			m1.selectAlarm(2);
		} catch (IndexOutOfBoundsException e) {
			thrown = true;
		}
		
		assertTrue("Should have thrown", thrown);
	}

	public void testGetAlarmCount() {
		AlarmMgmt m1 = new AlarmMgmt(mCtx);
		Alarm a1 = new Alarm(Time.makeAbsolute(0), false);
		Alarm a2 = new Alarm(Time.makeAbsolute(0), false);
		m1.addAlarm(a1);
		m1.addAlarm(a2);
		
		int expected = 2;
		int value = m1.getAlarmCount();
		
		assertEquals(expected, value);
	}
	
	public void testAddAlarm() {
		AlarmMgmt m1 = new AlarmMgmt(mCtx);
		Alarm a1 = new Alarm(Time.makeAbsolute(0), false);
		m1.addAlarm(a1);

		assertSame(m1.getAlarm(), a1);
	}
}
