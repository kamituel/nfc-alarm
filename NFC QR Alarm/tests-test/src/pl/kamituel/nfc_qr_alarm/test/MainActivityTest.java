package pl.kamituel.nfc_qr_alarm.test;

import java.lang.reflect.Field;

import pl.kamituel.nfc_qr_alarm.AlarmMgmt;
import pl.kamituel.nfc_qr_alarm.MainActivity;
import pl.kamituel.nfc_qr_alarm.PrefHelper;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Button;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity mActivity;
	private AlarmMgmt mAlarmMgmt;
	
	public MainActivityTest() {
		super(MainActivity.class);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(true);
		
		// Setup one tag, so each test will go straight to MainActivity,
		// and not to RegisterNewTagActivity.
		PrefHelper prefHelper = new PrefHelper(getInstrumentation().getTargetContext());
		prefHelper.clearAll();
		prefHelper.saveTag("123");
		
		mActivity = (MainActivity) getActivity();
		
		Field alarmMgmtField = mActivity.getClass().getDeclaredField("mAlarmMgmt");
		alarmMgmtField.setAccessible(true);
		mAlarmMgmt = (AlarmMgmt) alarmMgmtField.get(mActivity);
	}
	
	@UiThreadTest
	public void testAlarmSettingsEnabledWhenSet() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		int buttonId = pl.kamituel.nfc_qr_alarm.R.id.enableAlarmBtn;
		Button enable = (Button) getActivity().findViewById(buttonId);
		enable.performClick();
		
		boolean value = mAlarmMgmt.getAlarm().getEnabled();
		boolean expected = true;
		
		assertEquals(expected, value);
	}
	
	@UiThreadTest
	public void testAlarmDisabledOnStartup() {
		boolean value = mAlarmMgmt.getAlarm().getEnabled();
		boolean expected = false;
		
		assertEquals(expected, value);
	}
	
	@UiThreadTest
	public void testAlarmTimeSetCorrectlyUsingClock() {
		assertTrue("Do it with monkeyrunner?", false);
	}
}
