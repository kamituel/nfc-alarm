package pl.kamituel.nfc_qr_alarm.alarm;

public interface OnAlarmStateChangedListener {
	public void onAlarmStateChanged(Alarm alarm, boolean enabled);
}
