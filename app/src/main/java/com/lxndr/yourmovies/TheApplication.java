package com.lxndr.yourmovies;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public final class TheApplication extends Application {

	private static TheApplication mApplication;


	public void setSortCriteria(@Config.SortCriteria int conf) {
		SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
				getString(R.string.pref_my_preferences_bundle), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(getString(R.string.pref_sort_criteria_key), conf);
		editor.commit();
	}

	public @Config.SortCriteria int getSortCriteria() {
		SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
				getString(R.string.pref_my_preferences_bundle), Context.MODE_PRIVATE);
		int v = sharedPreferences.getInt(getString(R.string.pref_sort_criteria_key), Config.SORT_BY_POPULARITY);
		switch (v) {
			case 1 : return Config.SORT_BY_TOP_RATED;
			case 2 : return Config.SORT_BY_FAVOURITES;
			default : return Config.SORT_BY_POPULARITY;
		}
	}

	public static synchronized TheApplication getInstance() {
		return mApplication;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
	}
}
