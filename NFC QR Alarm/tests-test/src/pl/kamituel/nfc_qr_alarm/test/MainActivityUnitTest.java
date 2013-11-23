package pl.kamituel.nfc_qr_alarm.test;

import pl.kamituel.nfc_qr_alarm.MainActivity;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityUnitTestCase;

public class MainActivityUnitTest extends ActivityUnitTestCase<MainActivity> {
	private Context mCtx;
	
	public MainActivityUnitTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		mCtx = getInstrumentation().getTargetContext();
		Intent intent = new Intent(mCtx, MainActivity.class);
		startActivity(intent, null, null);
	}

	
	// TODO: test launch two times in a row
}
