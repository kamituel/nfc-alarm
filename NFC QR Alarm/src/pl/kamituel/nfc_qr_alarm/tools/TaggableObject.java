package pl.kamituel.nfc_qr_alarm.tools;

import java.util.Hashtable;

public abstract class TaggableObject {
	private Hashtable<String, Object> mTags = new Hashtable<String, Object>();
	
	public final void addTag (String name, Object value) {
		if ( name == null ) return;
		mTags.put(name, value);
	}
	
	public final Object getTag (String name) {
		if ( name == null ) return null;
		return mTags.get(name);
	}
}
