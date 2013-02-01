package pl.kamituel.nfc_qr_alarm;

import java.util.List;

public interface AlarmDataProvider {
	/**
	 * Selected alarm is a alarm which is currently displayed
	 * by the UI.
	 * @return
	 */
	public AlarmTime getSelectedAlarm();
	
	/**
	 * 
	 * @return List of alarms sorted by alarm time.
	 */
	public List<AlarmTime> getAlarms();
	
	
	public void addAlarm(AlarmTime alarm, Boolean selected);
	
	
	public void removeAlarm(AlarmTime alarm);
	
	/**
	 * Should be called by an entity which modifies one of the alarms
	 * to indicate that there will be no more changes in the nearest
	 * future, so current state of all the alarms can be saved.
	 */
	public void commit ();
	
	public void addObserver (Observer o);
	
	public void removeObserver (Observer o);
	
	public interface Observer {
		public void onSelectedAlarmChanged (AlarmTime alarm);
	}
}
