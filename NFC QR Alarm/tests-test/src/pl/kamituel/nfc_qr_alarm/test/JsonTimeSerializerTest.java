package pl.kamituel.nfc_qr_alarm.test;

import pl.kamituel.nfc_qr_alarm.time.Time;
import pl.kamituel.nfc_qr_alarm.tools.JsonSerializer;
import pl.kamituel.nfc_qr_alarm.tools.JsonSerializer.InvalidJsonException;
import android.test.AndroidTestCase;

public class JsonTimeSerializerTest extends AndroidTestCase {
	public void testToJson() {
		Time t1 = Time.makeAbsolute(13 * Time.HOUR + 12 * Time.MINUTE + 15 * Time.SECOND);
		String expected = "{\"msec_since_midnight\":47535000}";
		String value = JsonSerializer.toJson(t1);
		
		assertEquals(expected, value);
	}
	
	public void testFromJson() throws InvalidJsonException {
		String json = "{\"msec_since_midnight\":47535000}";
		Time value = JsonSerializer.fromJson(json, Time.class);
		Time expected = Time.makeAbsolute(47535000);
		
		assertEquals(expected, value);
	}
	
	/*public void testThrowsWhenInvalidSyntax() {
		// Missing closing bracket
		String json = "{\"msec_since_midnight\":47535000";
		
		try {
			Time t = JsonSerializer.fromJson(json, Time.class);
			Log.d("xxx", "?????? " + t);
		} catch (InvalidParameterException e) {
			assertTrue(true);
			return;
		}
		
		assertTrue("Exception expected", false);
	}

	public void testThrowsWhenInvalidNumber() {
		String json = "{\"msec_since_midnight\":4753500d0}";

		try {
			JsonSerializer.fromJson(json, Time.class);
		} catch (InvalidParameterException e) {
			assertTrue(true);
			return;
		}

		assertTrue("Exception expected", false);
	}*/
}
