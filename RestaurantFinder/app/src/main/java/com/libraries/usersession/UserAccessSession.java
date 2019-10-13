package com.libraries.usersession;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.config.Config;

public class UserAccessSession {

	private SharedPreferences sharedPref;
	private Editor editor;

	private static final String FILTER_RADIUS_DISTANCE 		= "FILTER_RADIUS_DISTANCE";
	private static final String FILTER_RADIUS_DISTANCE_MAX 		= "FILTER_RADIUS_DISTANCE_MAX";


	private static final String SHARED = "UserAccessSession_Preferences";
	private static UserAccessSession instance;
	
	public static UserAccessSession getInstance(Context context) {
		if(instance == null)
			instance = new UserAccessSession(context);
		return instance;
	}

	public UserAccessSession(Context context) {
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
		editor = sharedPref.edit();
	}

	public void setFilterDistance(float radius) {
		editor.putFloat(FILTER_RADIUS_DISTANCE, radius);
		editor.commit();
	}

	public float getFilterDistance() {
		return  sharedPref.getFloat(FILTER_RADIUS_DISTANCE, 0);
	}

	public void setFilterDistanceMax(float radius) {
		editor.putFloat(FILTER_RADIUS_DISTANCE_MAX, radius);
		editor.commit();
	}

	public float getFilterDistanceMax() {
		return  sharedPref.getFloat(FILTER_RADIUS_DISTANCE_MAX, Config.MAX_RADIUS_IN_KM);
	}
}
