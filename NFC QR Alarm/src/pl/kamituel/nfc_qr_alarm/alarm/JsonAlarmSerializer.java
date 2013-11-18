package pl.kamituel.nfc_qr_alarm.alarm;

import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.kamituel.nfc_qr_alarm.time.JsonTimeSerializer;
import pl.kamituel.nfc_qr_alarm.time.Time;

public class JsonAlarmSerializer {
	private final static Pattern JSON_ALARM = Pattern.compile("^\\{\"enabled\":(true|false),\"time\":(.*)\\}$");
	private Alarm mAlarm;
	
	public JsonAlarmSerializer(Alarm alarm) {
		mAlarm = alarm;
	}
	
	public String toJson() {
		StringBuilder res = new StringBuilder();
		
		res.append("{");
		res.append("\"enabled\":"); res.append(mAlarm.isEnabled()); res.append(",");
		res.append("\"time\":"); res.append(new JsonTimeSerializer(mAlarm.getTime()).toJson());
		res.append("}");
		
		return res.toString();
	}
	
	public static Alarm fromJson(String json) {
		Matcher alarmMatcher = JSON_ALARM.matcher(json);
		if (!alarmMatcher.matches()) {
			throw new InvalidParameterException("JSON: " + json + " invalid.");
		}
		
		boolean enabled;
		try {
			enabled = Boolean.parseBoolean(alarmMatcher.group(1));
		} catch (Exception e) {
			throw new InvalidParameterException("JSON: " + json + " invalid. Enabled parameter invalid.");
		}
		
		Time time;
		try {
			time = JsonTimeSerializer.fromJson(alarmMatcher.group(2));
		} catch (Exception e) {
			throw new InvalidParameterException("JSON: " + json + " invalid. Time invalid.");
		}
		
		return new Alarm(time, enabled);
	}
}
