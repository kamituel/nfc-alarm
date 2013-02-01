package pl.kamituel.nfc_qr_alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;

public abstract class NfcActivity extends Activity {
	private final static String TAG = NfcActivity.class.getSimpleName();
	private AlertDialog mNoNfcDialog = null;
	
	protected abstract void tagReceived (String tagId);
	
	@Override
	protected void onStart() {
		super.onStart();

		if ( !Utils.RUNS_IN_EMULATOR ) {
			if ( !isNfcPresent() ) {
				// should never happen as google play store filters for this.
				// but may be useful when installing from APK file.
				alertNoNfc();
			}

			if ( !isNfcEnabled() ) {
				mNoNfcDialog = buildNoNfcWarning();
				mNoNfcDialog.show();
			}
		}
	}


	@Override
	protected void onStop() {
		super.onStop();
		
		if ( mNoNfcDialog != null ) mNoNfcDialog.dismiss();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		if ( !Utils.RUNS_IN_EMULATOR ) {
			NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
			nfcAdapter.disableForegroundDispatch(this);
		}
		
		Log.d(TAG, "onPause(): NFC listen off");
	}

	@Override
	protected void onResume() {
		super.onResume();

		if ( !Utils.RUNS_IN_EMULATOR ) {
			Intent i = new Intent(this, getClass());
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

			NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
			if ( nfcAdapter == null ) { 
				Log.e(TAG, "NFC adapter null. NFC listen off"); 
				return; 
			}
			nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
		}

		Log.d(TAG, "onResume(): NFC listen on");
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(TAG, "NFC intent received "+intent.getAction());
		
		if ( intent.getAction() == null ) return;
		
		if ( intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED) ) {
			Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			
			if ( !tagReceived(tag) ) {
				Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
				if (rawMsgs != null) {
					NdefMessage [] msgs = new NdefMessage[rawMsgs.length];
					for (int i = 0; i < rawMsgs.length; i++) {
						msgs[i] = (NdefMessage) rawMsgs[i];
						Log.d(TAG, "  NDEF msg: "+msgs[i].toString());
					}
				}
			}
			
		} else if ( intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED) ) {
			Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			if ( !tagReceived(tag) ) {
				Log.w(TAG, "No logic here!");
			}
		} else {
			Log.d(TAG, "Some different kind of tag: "+intent.getAction());
		}
	}
	
	private boolean tagReceived (Tag tag) {
		byte [] id = tag.getId();
		Log.d(TAG, "Discovered TAG ID: "+byteArrayToString(tag.getId()));
		
		if ( id.length == 0 ) {
			Log.w(TAG, "Tag with no ID");
			return false;
		}
		
		String idStr = byteArrayToString(id);
		tagReceived(idStr);
		
		return true;
	}
	
	private String byteArrayToString (byte [] arr) {
		StringBuilder sb = new StringBuilder();
		
		for ( byte b : arr ) {
			sb.append(String.format("%X", b));
		}
		
		return sb.toString();
	}

	private boolean isNfcEnabled () {
		NfcManager manager = (NfcManager) this.getSystemService(Context.NFC_SERVICE);
		NfcAdapter adapter = manager.getDefaultAdapter();
		return ( adapter != null && adapter.isEnabled() );
	}
	
	private boolean isNfcPresent () {
		NfcManager manager = (NfcManager) this.getSystemService(Context.NFC_SERVICE);
		return ( manager.getDefaultAdapter() != null );
	}
	
	protected void alertNoNfc () {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(R.string.no_nfc_present_title)
		.setIcon(R.drawable.alerts_and_states_error)
		.setMessage(R.string.no_nfc_present_desc)
		.setNegativeButton(R.string.no_nfc_present_quit, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		}).create().show();
	}
	
	protected AlertDialog buildNoNfcWarning () {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(R.string.no_nfc_d_title)
		.setIcon(R.drawable.alerts_and_states_error)
		.setMessage(R.string.no_nfc_d_desc)
		.setPositiveButton(R.string.no_nfc_open_settings, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				NfcActivity.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
			}
		})
		.setNegativeButton(R.string.no_nfc_close_alarm_clock, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		return b.create();
	}
}
