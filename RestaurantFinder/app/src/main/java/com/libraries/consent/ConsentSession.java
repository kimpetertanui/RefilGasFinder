package com.libraries.consent;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ConsentSession {

	private SharedPreferences sharedPref;
	private Editor editor;
	private static final String GDPR 		= "GDPR_12345";

	private static final String SHARED = "GDPR_Preferences";
	private static ConsentSession instance;

	public static ConsentSession getInstance(Context context) {
		if(instance == null)
			instance = new ConsentSession(context);
		return instance;
	}

	public ConsentSession(Context context) {
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
		editor = sharedPref.edit();
	}

	public void showGDPR(boolean show) {
		editor.putBoolean(GDPR, show);
		editor.commit();
	}

	public boolean getShowGDPR() {
		return  sharedPref.getBoolean(GDPR, true);
	}
}
