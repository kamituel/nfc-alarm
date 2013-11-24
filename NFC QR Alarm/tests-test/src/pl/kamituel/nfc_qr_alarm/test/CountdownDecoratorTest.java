package pl.kamituel.nfc_qr_alarm.test;
import pl.kamituel.nfc_qr_alarm.alarm.Alarm;
import pl.kamituel.nfc_qr_alarm.time.CountdownDecorator;
import pl.kamituel.nfc_qr_alarm.time.Time;
import android.test.AndroidTestCase;


public class CountdownDecoratorTest extends AndroidTestCase {
	public void testGetHours() {
		Time time = Time.makeRelative(3 * Time.HOUR + 5 * Time.MINUTE);
		Alarm a1 = new Alarm(time, true);
		CountdownDecorator decorator = new CountdownDecorator(a1);
		
		assertEquals(3, decorator.getHours());
	}
	
	public void testGetMinutes() {
		Time time = Time.makeRelative(3 * Time.HOUR + 5 * Time.MINUTE + 5 * Time.SECOND);
		Alarm a1 = new Alarm(time, true);
		CountdownDecorator decorator = new CountdownDecorator(a1);
		
		assertEquals(5, decorator.getMinutes());
	}
}
