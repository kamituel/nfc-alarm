package pl.kamituel.nfc_qr_alarm.test;

import android.test.AndroidTestCase;

public class DeltaTestCase extends AndroidTestCase {
	// Tolerane [ms] when comparing time
	private final int mDelta = 2;
	
	protected boolean delta (long expected, long value) {
		return value >= expected - mDelta && value <= expected + mDelta;
	}
}
