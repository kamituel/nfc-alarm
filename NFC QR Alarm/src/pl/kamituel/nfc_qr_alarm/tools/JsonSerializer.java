package pl.kamituel.nfc_qr_alarm.tools;

import com.google.gson.GsonBuilder;

public class JsonSerializer {
	public static String toJson(Object object) {
		return new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.create()
				.toJson(object);
	}
	
	public static <T> T fromJson(String json, Class<T> type) throws InvalidJsonException {
		try {
			return new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.create()
				.fromJson(json, type);
		} catch (Exception e) {
			throw new InvalidJsonException("Invalid JSON document: <<" + json + ">>");
		}
	}
	
	public static class InvalidJsonException extends Exception {
		public InvalidJsonException(String msg) {
			super(msg);
		}
	}
}
