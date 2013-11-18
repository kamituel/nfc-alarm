package pl.kamituel.nfc_qr_alarm.time;

import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonTimeSerializer {
	private final static Pattern JSON_TIME = Pattern.compile("^\\{\"seconds_since_midnight\":(\\d+)\\}$");
	private Time mTime;
	
	public JsonTimeSerializer(Time time) {
		mTime = time;
	}
	
	public String toJson() {
		StringBuilder res = new StringBuilder();
		
		res.append("{");
		res.append("\"seconds_since_midnight\":"); 
		res.append(mTime.getTimeFromMidnight());
		res.append("}");
		
		return res.toString();
	}
	
	public static Time fromJson(String json) {
		Matcher timeMatcher = JSON_TIME.matcher(json);
		if (!timeMatcher.matches()) {
			throw new InvalidParameterException("JSON: \"" + json + "\" invalid.");
		}
		
		long secondsSinceMidnight;
		try {
			secondsSinceMidnight = Long.parseLong(timeMatcher.group(1), 10);
		} catch (NumberFormatException e) {
			throw new InvalidParameterException("JSON: \"" + json + "\" invalid. Wrong number.");
		}
		
		return Time.makeAbsolute(secondsSinceMidnight);
	}
}
