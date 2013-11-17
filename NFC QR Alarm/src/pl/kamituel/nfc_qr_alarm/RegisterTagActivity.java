package pl.kamituel.nfc_qr_alarm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.analytics.tracking.android.EasyTracker;

public class RegisterTagActivity extends NfcActivity {
	private final static String TAG = RegisterTagActivity.class.getSimpleName();
	private final Handler mHandler = new Handler();
	private PrefHelper mPrefHelper = null;
	private ViewFlipper mViewFlipper = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.register_tag_layout);
		mPrefHelper = new PrefHelper(getApplicationContext());

		mViewFlipper = (ViewFlipper) findViewById(R.id.introFlipper);
		mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
		mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
		
		if ( Utils.RUNS_IN_EMULATOR ) {
			TextView t = (TextView) findViewById(R.id.rtl_2_next_after_nfc_scan);
			t.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPrefHelper.saveTag("0");
					mViewFlipper.showNext();
				}
			});
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		Log.d(TAG, "restoer instance state()");
		int displayedChild = savedInstanceState.getInt("displayed_child"); 
		if ( displayedChild > 0 ) {
			while ( mViewFlipper.getDisplayedChild() < displayedChild ) {
				mViewFlipper.showNext();
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		Log.d(TAG, "save instance state()");
		outState.putInt("displayed_child", mViewFlipper.getDisplayedChild());
	}

	@Override
	protected void tagReceived (String id) {
		Log.d(TAG, "Saving tag: "+id);

		mPrefHelper.saveTag(id);		

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				while ( mViewFlipper.getDisplayedChild() < 2 ) {
					mViewFlipper.showNext();
				}
			}
		});
	}

	public void goToTagDiscovery (View v) {
		Log.d(TAG, "Flipping to tag discovery");		
		
		mViewFlipper.setInAnimation(this, R.anim.in_left);
		mViewFlipper.setOutAnimation(this, R.anim.out_left);
		mViewFlipper.showNext();
	}

	public void goToApp (View v) {
		Log.d(TAG, "Switching to app activity");
		Intent i = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(i);
		finish();
	}

	public void goToIntro (View v) {		
		mViewFlipper.setInAnimation(this, R.anim.in_right);
		mViewFlipper.setOutAnimation(this, R.anim.out_right);
		mViewFlipper.showPrevious();
	}

	public void goToScanAnotherTag (View v) {
		mPrefHelper.saveTag(null);
		
		mViewFlipper.setInAnimation(this, R.anim.in_right);
		mViewFlipper.setOutAnimation(this, R.anim.out_right);
		mViewFlipper.showPrevious();
	}

	@Override
	public void onBackPressed() {
		switch ( mViewFlipper.getDisplayedChild() ) {
		case 2: goToScanAnotherTag(null); break;
		case 1: goToIntro(null); break;
		case 0: finish(); break;
		}
	}


}
