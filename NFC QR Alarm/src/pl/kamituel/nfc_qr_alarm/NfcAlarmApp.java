package pl.kamituel.nfc_qr_alarm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Environment;
import android.util.Log;

public class NfcAlarmApp extends Application {
	private final static String TAG = NfcAlarmApp.class.getSimpleName();
	
	private static Context mCtx;

	@Override
	public void onCreate() {
		super.onCreate();
		mCtx = this;
		
		Log.d(TAG, "Dump logcat: " + getResources().getBoolean(R.bool.logcat_monitor));
		if (getResources().getBoolean(R.bool.logcat_monitor)) {
			monitorLogcat();
		}
	}

	public static Context getContext () {
		return mCtx;
	}
	
	public static boolean hasFlag (int settingId) {
		if (settingId == R.bool.debug) {
			return inDebug();
		} else {
			return inDebug() && mCtx.getResources().getBoolean(settingId);
		}
	}
	
	public static boolean inDebug () {
		return mCtx.getResources().getBoolean(R.bool.debug);
	}
	
	private void monitorLogcat () {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Process logcat = Runtime.getRuntime().exec("logcat -v time");
					BufferedReader logsReader = new BufferedReader(
							new InputStreamReader(logcat.getInputStream()));

					File sdcard = Environment.getExternalStorageDirectory();
					File dump = new File(sdcard, mCtx.getResources().getString(R.string.logcat_dump_file));

					if (!dump.exists()) {
						dump.createNewFile();
					}

					BufferedWriter dumpWriter = new BufferedWriter(new FileWriter(dump));

					String line = null;
					while ((line = logsReader.readLine()) != null) {
						dumpWriter.write(line);
						dumpWriter.write("\n");
						dumpWriter.flush();
					}

					dumpWriter.close();
				} catch (NotFoundException e) {
					Log.e(TAG, "Could not dump logcat to sdcard", e);
				} catch (IOException e) {
					Log.e(TAG, "Coukd not dump logcat to sdcard", e);
				}
			}
		}).start();
	}
}
