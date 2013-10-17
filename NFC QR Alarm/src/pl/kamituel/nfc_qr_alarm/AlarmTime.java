package pl.kamituel.nfc_qr_alarm;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.kamituel.nfc_qr_alarm.tools.TaggableObject;

public class AlarmTime extends TaggableObject implements Comparable<AlarmTime> {
	@SuppressWarnings("unused")
	private final static String TAG = AlarmTime.class.getSimpleName();
	
	private Integer mSeconds = null;
	private Boolean mEnabled = null;
	
	private final HashSet<Observer> mObservers = new HashSet<Observer>();
	
	private final static Pattern JSON_SECONDS_PATTERN = Pattern.compile(".*seconds\":\\s*(\\d+).*");
	private final static Pattern JSON_ENABLED_PATTERN = Pattern.compile(".*enabled\":\\s*(true|false).*");
	
	private final static int TIME_RESOLUTION = 5*TimeUtils.MINUTE;
	
	public AlarmTime () {

	}
	
	public int get () {
		return mSeconds;
	}
	
	public long getAbsolute () {
		int nowSecSinceMidnight = TimeUtils.getSecondsFromMidnight(Calendar.getInstance(TimeZone.getDefault()));
		
		Calendar alarm = Calendar.getInstance();
		if ( nowSecSinceMidnight > get() ) {
			alarm.add(Calendar.DAY_OF_YEAR, 1);
		}
		
		alarm.set(Calendar.HOUR_OF_DAY, 0);
		alarm.set(Calendar.MINUTE, 0);
		alarm.set(Calendar.SECOND, 0);
		
		alarm.add(Calendar.SECOND, get());
		
		return alarm.getTimeInMillis()/1000;
	}
	
	public int getCountdown () {
		long alarm = getAbsolute();
		long now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis()/1000;
		return (int)(alarm-now);
	}
	
	public int getCountdownHours () {
		return getCountdown()/TimeUtils.HOUR;
	}
	
	public int getCountdownMinutes () {
		return (getCountdown()-getCountdownHours()*TimeUtils.HOUR)/TimeUtils.MINUTE;
	}
	
	public void set (int seconds) {
		Integer prevSeconds = mSeconds;
		Boolean prevIsMorning = mSeconds == null ? true : isMorning();
		mSeconds = seconds;
		normalize();
		
		if ( prevSeconds == null || prevSeconds.intValue() != get() ) {
			boolean timeOfDayChanged = (isMorning() != prevIsMorning || prevSeconds == null) ? true : false;
			//Log.d(TAG, "pAM:"+prevIsMorning+", AM:"+isMorning()+", pT:"+prevSeconds+", T:"+get()+", AM->PM:"+timeOfDayChanged);
			Iterator<Observer> oIt = mObservers.iterator();
			while ( oIt.hasNext() ) oIt.next().timeChanged(this, get(), timeOfDayChanged);
		}
	}
	
	public void add (int seconds) {
		set(get()+seconds);
	}
	
	public void setEnabled (boolean enabled) {
		Boolean prevEnabled = mEnabled;
		mEnabled = enabled;
		
		if ( prevEnabled == null || prevEnabled.booleanValue() != getEnabled() ) {
			Iterator<Observer> oIt = mObservers.iterator();
			while ( oIt.hasNext() ) oIt.next().statusChanged(this, getEnabled());
		}
	}
	
	public boolean getEnabled () {
		return mEnabled;
	}
	
	public boolean isMorning () {
		return (mSeconds < TimeUtils.TWELVE_HOUR);
	}
	
	private void normalize () {
		mSeconds = mSeconds % (24*TimeUtils.HOUR);
		
		//if ( !Utils.RUNS_IN_EMULATOR ) {
		//	mSeconds = mSeconds - (mSeconds % TIME_RESOLUTION);
		//}
	}
	
	public void addObserver (Observer o) {
		mObservers.add(o);
	}
	
	public void removeObserver (Observer o) {
		mObservers.remove(o);
	}
	
	public static interface Observer {
		public void timeChanged (AlarmTime alarm, int newSeconds, boolean timeOfDayChanged);
		public void statusChanged (AlarmTime alarm, boolean isEnabled);
	}
	
	public String serialize () {
		return String.format(Locale.getDefault(), "{\"seconds\": %d, \"enabled\": %s}", get(), getEnabled() ? "true" : "false");
	}
	
	public static AlarmTime deserialize (String json) throws InvalidParameterException {
		AlarmTime am = new AlarmTime();
		
		Matcher secondsM = JSON_SECONDS_PATTERN.matcher(json);
		if ( !secondsM.matches() ) throw new InvalidParameterException("JSON invalid. 'seconds' parameter missing (#1)");
		
		String secondsS = secondsM.group(1);
		if ( secondsS == null ) throw new InvalidParameterException("JSON invalid. 'seconds' parameter missing (#2)");
		
		try {
			am.set(Integer.parseInt(secondsS));
		} catch (NumberFormatException e) {
			throw new InvalidParameterException("JSON invalid. 'seconds' parameter not a number");
		}
		
		Matcher enabledM = JSON_ENABLED_PATTERN.matcher(json);
		if ( !enabledM.matches() ) throw new InvalidParameterException("JSON invalid. 'enabled' parameter missing (#1)");
		
		String enabledS = enabledM.group(1);
		if ( enabledS == null ) throw new InvalidParameterException("JSON invalid. 'enabled' parameter missing (#2)");
		
		try {
			am.setEnabled(Boolean.parseBoolean(enabledS));
		} catch (Exception e) {
			throw new InvalidParameterException("JSON invalid. 'enabled' parameter not a 'true' nor 'false'");
		}
		
		return am;
	}

	@Override
	public String toString() {
		return String.format(Locale.US, "Alarm: %d (%dH:%dM) %s countdown:%d", 
				get(), get()/TimeUtils.HOUR, get()%TimeUtils.HOUR, getEnabled() ? "enabled" : "disabled", getCountdown());
	}

	@Override
	public int compareTo(AlarmTime b) {
		if ( getEnabled() && !b.getEnabled() ) return -1;
		if ( !getEnabled() && b.getEnabled() ) return 1;
		if ( getCountdown() < b.getCountdown() ) return -1;
		if ( getCountdown() > b.getCountdown() ) return 1;
		return 0;
	}
}
