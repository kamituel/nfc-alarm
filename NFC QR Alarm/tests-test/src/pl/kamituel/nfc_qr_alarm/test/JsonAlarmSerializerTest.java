package pl.kamituel.nfc_qr_alarm.test;

import pl.kamituel.nfc_qr_alarm.alarm.Alarm;
import pl.kamituel.nfc_qr_alarm.time.Time;
import pl.kamituel.nfc_qr_alarm.tools.JsonSerializer;
import android.test.AndroidTestCase;

public class JsonAlarmSerializerTest extends AndroidTestCase {
	public void testToJson() {
		Alarm a1 = new Alarm(Time.makeAbsolute(Time.HOUR), false);
		String value = JsonSerializer.toJson(a1);
		boolean isValid = value.matches("^\\{.*\\}$")
				&& value.matches(".*\"enabled\":\\s*?false.*")
				&& value.matches(".*\"msec_since_midnight\":\\s*?3600000.*");

		assertTrue("Invalid: <<" + value + ">>", isValid);
	}
	
	public void testFromJson() {
		Time t1 = Time.makeAbsolute(5);
		String json = "{\"enabled\":true,\"time\":" + JsonSerializer.toJson(t1) + "}";
		
		Alarm expected = new Alarm(t1, true);
		Alarm value = JsonSerializer.fromJson(json, Alarm.class);
		
		assertEquals(expected, value);
	}
	
	/*public void testFromJsonThrowsWhenInvalidEnabledParamName() {
		Time t1 = Time.makeAbsolute(5);
		String json = "{\"ena2bled\":true,\"time\":" + JsonSerializer.toJson(t1) + "}";
		
		try {
			JsonSerializer.fromJson(json, Alarm.class);
		} catch (InvalidParameterException e) {
			assertTrue(true);
			return;
		}
		
		assertTrue("Exception expected for <<" + json + ">>", false);
	}
	
	public void testFromJsonThrowsWhenInvalidEnabledParamValue() {
		Time t1 = Time.makeAbsolute(5);
		String json = "{\"ena2bled\":5,\"time\":" + JsonSerializer.toJson(t1) + "}";
		
		try {
			JsonSerializer.fromJson(json, Alarm.class);
		} catch (InvalidParameterException e) {
			assertTrue(true);
			return;
		}
		
		assertTrue("Exception expected for <<" + json + ">>", false);
	}
	
	public void testFromJsonThrowsWhenInvalidSyntax() {
		Time t1 = Time.makeAbsolute(5);
		// Missing <<">> near "enabled"
		String json = "{enabled\":true,\"time\":" + JsonSerializer.toJson(t1) + "}";
		
		try {
			JsonSerializer.fromJson(json, Alarm.class);
		} catch (InvalidParameterException e) {
			assertTrue(true);
			return;
		}
		
		assertTrue("Exception expected for <<" + json + ">>", false);
	}
	
	public void testFromJsonThrowsWhenTimeMissing() {
		String json = "{\"enabled\":true}";
		
		try {
			JsonSerializer.fromJson(json, Alarm.class);
		} catch (InvalidParameterException e) {
			assertTrue(true);
			return;
		}
		
		assertTrue("Exception expected for <<" + json + ">>", false);
	}*/
}
