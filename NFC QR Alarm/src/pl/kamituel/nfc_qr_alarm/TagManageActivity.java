package pl.kamituel.nfc_qr_alarm;

import com.google.analytics.tracking.android.EasyTracker;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TagManageActivity extends NfcActivity implements OnItemClickListener {
	private final static String TAG = TagManageActivity.class.getSimpleName();
	
	private PrefHelper mPrefHelper = null;
	private ArrayAdapter<String> mListAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tag_manage);
		
		mPrefHelper = new PrefHelper(getApplicationContext());
		
		ListView tagList = (ListView) findViewById(R.id.tagList);
		
		mListAdapter = new ArrayAdapter<String>(this, R.layout.tag_list_item, R.id.tagValue);
		tagList.setAdapter(mListAdapter);
		tagList.setOnItemClickListener(this);
		
		refreshList();
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
	protected void tagReceived(String tagId) {
		Log.d(TAG, "New tag: " + tagId);
		
		mPrefHelper.saveTag(tagId);
		refreshList();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String tag = ((TextView) view.findViewById(R.id.tagValue)).getText().toString();
		Log.d(TAG, "Tag clicked: " + tag + ", removing");
		
		if (mPrefHelper.getTags().size() == 1) {
			Toast.makeText(this, getResources().getString(R.string.cant_remove_last_tag), Toast.LENGTH_LONG).show();
		} else {
			mPrefHelper.removeTag(tag);
			refreshList();
		}
	}
	
	private void refreshList () {
		mListAdapter.clear();
		mListAdapter.addAll(mPrefHelper.getTags());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.tag_manage_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.reset:
			Log.d(TAG, "Clearing preferences");
			mPrefHelper.clearAll();
			finish();
			break;
		}
		
		return true;
	}
}
