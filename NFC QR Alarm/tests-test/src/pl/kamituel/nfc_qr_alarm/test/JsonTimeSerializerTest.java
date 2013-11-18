package pl.kamituel.nfc_qr_alarm.test;

import java.security.InvalidParameterException;

import pl.kamituel.nfc_qr_alarm.time.JsonTimeSerializer;
import pl.kamituel.nfc_qr_alarm.time.Time;
import android.test.AndroidTestCase;

public class JsonTimeSerializerTest extends AndroidTestCase {
	public void testToJson() {
		Time t1 = Time.makeAbsolute(13 * Time.HOUR + 12 * Time.MINUTE + 15 * Time.SECOND);
		String expected = "{\"seconds_since_midnight\":47535000}";
		JsonTimeSerializer serializer = new JsonTimeSerializer(t1);
		
		assertEquals(expected, serializer.toJson());
	}
	
	public void testFromJson() {
		String json = "{\"seconds_since_midnight\":47535000}";
		Time t1 = JsonTimeSerializer.fromJson(json);
		Time expected = Time.makeAbsolute(47535000);
		
		assertEquals(expected.getTimeFromMidnight(), t1.getTimeFromMidnight());
	}
	
	public void testThrowsWhenInvalidSyntax() {
		// Missing closing bracket
		String json = "{\"seconds_since_midnight\":47535000";
		
		try {
			JsonTimeSerializer.fromJson(json);
		} catch (InvalidParameterException e) {
			assertTrue(true);
			return;
		}
		
		assertTrue("Exception expected", false);
	}

	public void testThrowsWhenInvalidNumber() {
		String json = "{\"seconds_since_midnight\":47535d000}";

		try {
			JsonTimeSerializer.fromJson(json);
		} catch (InvalidParameterException e) {
			assertTrue(true);
			return;
		}

		assertTrue("Exception expected", false);
	}
}
