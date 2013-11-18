package pl.kamituel.nfc_qr_alarm.test;

import java.security.InvalidParameterException;

import pl.kamituel.nfc_qr_alarm.alarm.Alarm;
import pl.kamituel.nfc_qr_alarm.alarm.JsonAlarmSerializer;
import pl.kamituel.nfc_qr_alarm.time.JsonTimeSerializer;
import pl.kamituel.nfc_qr_alarm.time.Time;
import android.test.AndroidTestCase;

public class JsonAlarmSerializerTest extends AndroidTestCase {
	public void testToJson() {
		Alarm a1 = new Alarm(Time.makeAbsolute(Time.HOUR), false);
		String value = new JsonAlarmSerializer(a1).toJson();
		String expected = "{\"enabled\":false,\"time\":" + new JsonTimeSerializer(a1.getTime()).toJson() + "}";
		assertEquals(expected, value);
	}
	
	public void testFromJson() {
		Time t1 = Time.makeAbsolute(5);
		String json = "{\"enabled\":true,\"time\":" + new JsonTimeSerializer(t1).toJson() + "}";
		
		Alarm expected = new Alarm(t1, true);
		Alarm value = JsonAlarmSerializer.fromJson(json);
		
		assertEquals(expected.getEnabled(), value.getEnabled());
		assertEquals(expected.getTime().getTimeFromMidnight(), value.getTime().getTimeFromMidnight());
	}
	
	public void testFromJsonThrowsWhenInvalidEnabledParamName() {
		Time t1 = Time.makeAbsolute(5);
		String json = "{\"ena2bled\":true,\"time\":" + new JsonTimeSerializer(t1).toJson() + "}";
		
		try {
			JsonAlarmSerializer.fromJson(json);
		} catch (InvalidParameterException e) {
			assertTrue(true);
			return;
		}
		
		assertTrue("Exception expected", false);
	}
	
	public void testFromJsonThrowsWhenInvalidEnabledParamValue() {
		Time t1 = Time.makeAbsolute(5);
		String json = "{\"ena2bled\":5,\"time\":" + new JsonTimeSerializer(t1).toJson() + "}";
		
		try {
			JsonAlarmSerializer.fromJson(json);
		} catch (InvalidParameterException e) {
			assertTrue(true);
			return;
		}
		
		assertTrue("Exception expected", false);
	}
	
	public void testFromJsonThrowsWhenInvalidSyntax() {
		Time t1 = Time.makeAbsolute(5);
		// Missing <<">> near "enabled"
		String json = "{enabled\":5,\"time\":" + new JsonTimeSerializer(t1).toJson() + "}";
		
		try {
			JsonAlarmSerializer.fromJson(json);
		} catch (InvalidParameterException e) {
			assertTrue(true);
			return;
		}
		
		assertTrue("Exception expected", false);
	}
	
	public void testFromJsonThrowsWhenTimeMissing() {
		String json = "{\"enabled\":true}";
		
		try {
			JsonAlarmSerializer.fromJson(json);
		} catch (InvalidParameterException e) {
			assertTrue(true);
			return;
		}
		
		assertTrue("Exception expected", false);
	}
}
