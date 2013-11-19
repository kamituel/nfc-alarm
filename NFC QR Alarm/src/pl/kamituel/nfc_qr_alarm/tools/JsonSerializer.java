package pl.kamituel.nfc_qr_alarm.tools;

import java.security.InvalidParameterException;

import com.google.gson.GsonBuilder;

public class JsonSerializer {
	public static String toJson(Object object) {
		return new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.create()
				.toJson(object);
	}
	
	public static <T> T fromJson(String json, Class<T> type) {
		try {
			return new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.create()
				.fromJson(json, type);
		} catch (Exception e) {
			throw new InvalidParameterException("Invalid JSON document: <<" + json + ">>");
		}
	}
}
