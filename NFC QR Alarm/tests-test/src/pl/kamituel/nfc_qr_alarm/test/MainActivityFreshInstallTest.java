package pl.kamituel.nfc_qr_alarm.test;

import pl.kamituel.nfc_qr_alarm.MainActivity;
import pl.kamituel.nfc_qr_alarm.PrefHelper;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Test fresh install, but with one tag already added
 * (to skip RegisterNewTagActivity phase).
 *
 */
public class MainActivityFreshInstallTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	public MainActivityFreshInstallTest() {
		super(MainActivity.class);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(true);
		
		// Make sure there is no tag, so MainActivity should launch
		// RegisterNewTagActivity first.
		PrefHelper prefHelper = new PrefHelper(getInstrumentation().getTargetContext());
		prefHelper.clearAll();
		prefHelper.saveTag("123");
	}
	
	@UiThreadTest
	public void testAlarmSetOnDefaultTime() {
		int timeTextViewId = pl.kamituel.nfc_qr_alarm.R.id.timeTV;
		int amSpinner = pl.kamituel.nfc_qr_alarm.R.id.timeofdayList;
		TextView time = (TextView) getActivity().findViewById(timeTextViewId);
		Spinner amPm = (Spinner) getActivity().findViewById(amSpinner);
		
		String timeValue = time.getText().toString();
		long amValue = amPm.getSelectedItemId();
		
		int hour = getActivity().getResources().getInteger(pl.kamituel.nfc_qr_alarm.R.integer.alarm_default_hour);
		int minute = getActivity().getResources().getInteger(pl.kamituel.nfc_qr_alarm.R.integer.alarm_default_minute);
		String timeExpected = String.format("%02d:%02d", hour, minute);
		
		long expectedAmValue = 0;
	
		assertEquals(timeExpected, timeValue);
		assertEquals(expectedAmValue, amValue);
	}
}
