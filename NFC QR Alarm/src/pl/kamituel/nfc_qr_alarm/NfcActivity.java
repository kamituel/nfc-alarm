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
	
	//public final static String NFC_BROADCAST = "pl.kamituel.nfc_qr_alarm.tag";
	
	protected abstract void tagReceived (String tagId);
	private AlertDialog mNoNfcDialog = null;
	
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if ( !isNfcEnabled() ) {
			mNoNfcDialog = buildNoNfcWarning();
			mNoNfcDialog.show();
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
		
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		nfcAdapter.disableForegroundDispatch(this);
		
		Log.d(TAG, "onPause(): NFC listen off");
	}

	@Override
	protected void onResume() {
		super.onResume();

//		Log.d(TAG, "Setting up NFC");
//		
//		Intent i = new Intent();
//		i.setAction(NFC_BROADCAST);
//		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);
//		
//		IntentFilter intF = new IntentFilter();
//		intF.addAction(NFC_BROADCAST);
//		registerReceiver(new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				Log.d(TAG, "asdasdasdasd");
//				Iterator<String> it = intent.getExtras().keySet().iterator();
//				while ( it.hasNext() ) Log.d(TAG, "  extr: "+it.next());
//			}
//		}, intF);
		
		Intent i = new Intent(this, getClass());
		i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		//new Intent(this,
                //WakeUpActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if ( nfcAdapter == null ) { 
			Log.e(TAG, "NFC adapter null. NFC listen off"); 
			return; 
		}
		nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
		
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
