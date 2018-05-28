package com.androidcommonlibrary.util;

import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.androidcommonlibrary.constants.Constants;

@SuppressLint("NewApi")
public class Pref {

	private static SharedPreferences sharedPreferences = null;


	public static String KEY_Auth_Token ="auth_token";


	public static void openPref(Context context) {

		sharedPreferences = context.getSharedPreferences(Constants.PREF_FILE,
				Context.MODE_PRIVATE);

	}

	public static String getValue(Context context, String key,
			String defaultValue) {
		Pref.openPref(context);
		String result = Pref.sharedPreferences.getString(key, defaultValue);
		Pref.sharedPreferences = null;
		return result;
	}

	public static void setValue(Context context, String key, String value) {
		Pref.openPref(context);
		Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
		prefsPrivateEditor.putString(key, value);
		prefsPrivateEditor.commit();
		prefsPrivateEditor = null;
		Pref.sharedPreferences = null;
	}

	public static boolean getValue(Context context, String key,
			boolean defaultValue) {
		Pref.openPref(context);
		boolean result = Pref.sharedPreferences.getBoolean(key, defaultValue);
		Pref.sharedPreferences = null;
		return result;
	}

	public static void setValue(Context context, String key, boolean value) {
		Pref.openPref(context);
		Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
		prefsPrivateEditor.putBoolean(key, value);
		prefsPrivateEditor.commit();
		prefsPrivateEditor = null;
		Pref.sharedPreferences = null;
	}
	public static int getValue(Context context, String key,
			int defaultValue) {
		Pref.openPref(context);
		int result = Pref.sharedPreferences.getInt(key, defaultValue);
		Pref.sharedPreferences = null;
		return result;
	}

	public static void setValue(Context context, String key, int value) {
		Pref.openPref(context);
		Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
		prefsPrivateEditor.putInt(key, value);
		prefsPrivateEditor.commit();
		prefsPrivateEditor = null;
		Pref.sharedPreferences = null;
	}
	public static void setStringSet(Context _Context, String key,
			Set<String> mSetArray) {

		Pref.openPref(_Context);

		Editor preferenceEditor = Pref.sharedPreferences.edit();
		preferenceEditor.putStringSet(key, mSetArray);
		preferenceEditor.commit();
		preferenceEditor = null;
		Pref.sharedPreferences = null;
	}

	public static Set<String> getStoredPassHistory(Context _Context, String mKey) {

		Pref.openPref(_Context);

		HashSet<String> mSetPassHistory = (HashSet<String>) Pref.sharedPreferences
				.getStringSet(mKey, new HashSet<String>());

		Pref.sharedPreferences = null;

		return mSetPassHistory;
	}
	public static Editor getPrefEditor(Context mContext){
		Pref.openPref(mContext);
		
		Editor mEditor = Pref.sharedPreferences.edit();
		return mEditor;
	}
	public static void clearAllData(Context context){
		Pref.openPref(context);
		Editor mEditor = Pref.sharedPreferences.edit();
		mEditor.clear();
		mEditor.commit();
	}

}
