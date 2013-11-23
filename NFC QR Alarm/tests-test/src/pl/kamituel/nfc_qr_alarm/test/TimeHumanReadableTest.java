package pl.kamituel.nfc_qr_alarm.test;
import pl.kamituel.nfc_qr_alarm.time.Time;
import pl.kamituel.nfc_qr_alarm.time.TimeHumanReadable;
import android.test.AndroidTestCase;


public class TimeHumanReadableTest extends AndroidTestCase {
	public void testToString() {
		TimeHumanReadable t1 = new TimeHumanReadable(Time.makeAbsolute(7 * Time.HOUR + 12 * Time.MINUTE));
		
		String expected = "07:12";
		String value = t1.toString();
	
		assertEquals(expected, value);
	}
}
