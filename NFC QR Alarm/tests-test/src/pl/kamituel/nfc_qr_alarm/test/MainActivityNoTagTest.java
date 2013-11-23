package pl.kamituel.nfc_qr_alarm.test;

import java.util.Iterator;
import java.util.Set;

import pl.kamituel.nfc_qr_alarm.MainActivity;
import pl.kamituel.nfc_qr_alarm.PrefHelper;
import pl.kamituel.nfc_qr_alarm.RegisterTagActivity;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

/**
 * Tests for MainActivity: whether it launches RegisterNewTagActivity when
 * it detects there are no NFC tags added by user yet (i.e. user launches
 * app for the first time).
 *
 */
public class MainActivityNoTagTest extends ActivityInstrumentationTestCase2<MainActivity> {
	ActivityMonitor mRegisterNewTagMonitor;
	
	public MainActivityNoTagTest() {
		super(MainActivity.class);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(true);
		
		// Make sure there is no tag, so MainActivity should launch
		// RegisterNewTagActivity first.
		PrefHelper prefHelper = new PrefHelper(getInstrumentation().getTargetContext());
		removeAllTags(prefHelper);
		
		mRegisterNewTagMonitor = getInstrumentation()
				.addMonitor(RegisterTagActivity.class.getName(), null, false);
	}
	
	@UiThreadTest
	public void testLaunchesTagActivityWhenNoTagConfigured() {
		RegisterTagActivity registerTagActivity = (RegisterTagActivity) getInstrumentation()
				.waitForMonitorWithTimeout(mRegisterNewTagMonitor, 60);
		
		assertNotNull(registerTagActivity);
		registerTagActivity.finish();
	}
	
	private void removeAllTags(PrefHelper prefHelper) {
		Set<String> tags = prefHelper.getTags();
		Iterator<String> tagsIt = tags.iterator();
		while (tagsIt.hasNext()) {
			prefHelper.removeTag(tagsIt.next());
		}
	}
}
