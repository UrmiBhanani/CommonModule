package com.androidcommonlibrary.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class NetworkActivity extends Activity {
	public static final String WIFI = "Wi-Fi";
	public static final String ANY = "Any";

	// Whether the display should be refreshed.
	public static boolean refreshDisplay = true;

	// The user's current network preference setting.
	public static String sPref = null;

	// The BroadcastReceiver that tracks network connectivity changes.
	private NetworkReceiver receiver = new NetworkReceiver();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Registers BroadcastReceiver to track network connection changes.
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		receiver = new NetworkReceiver();
		this.registerReceiver(receiver, filter);
	}

	@Override 
	public void onDestroy() {
		super.onDestroy();
		// Unregisters BroadcastReceiver when app is destroyed.
		if (receiver != null) {
			this.unregisterReceiver(receiver);
		}
	}

	// Refreshes the display if the network connection and the
	// pref settings allow it.

	@Override
	public void onStart () {
		super.onStart();  

		// Gets the user's network preference settings
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		// Retrieves a string value for the preferences. The second parameter
		// is the default value to use if a preference value is not found.
		sPref = sharedPrefs.getString("listPref", "Wi-Fi");

	}

	/**
	 *
	 * This BroadcastReceiver intercepts the android.net.ConnectivityManager.CONNECTIVITY_ACTION,
	 * which indicates a connection change. It checks whether the type is TYPE_WIFI.
	 * If it is, it checks whether Wi-Fi is connected and sets the wifiConnected flag in the
	 * main activity accordingly.
	 *
	 */
	public class NetworkReceiver extends BroadcastReceiver {   

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager conn =  (ConnectivityManager)
					context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = conn.getActiveNetworkInfo();

			// Checks the user prefs and the network connection. Based on the result, decides whether
			// to refresh the display or keep the current display.
			// If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
			if (WIFI.equals(sPref) && networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				// If device has its Wi-Fi connection, sets refreshDisplay
				// to true. This causes the display to be refreshed when the user
				// returns to the app.
				refreshDisplay = true;
				Toast.makeText(context, "wifi_connected", Toast.LENGTH_SHORT).show();

				// If the setting is ANY network and there is a network connection
				// (which by process of elimination would be mobile), sets refreshDisplay to true.
			} else if (ANY.equals(sPref) && networkInfo != null) {
				refreshDisplay = true;

				// Otherwise, the app can't download content--either because there is no network
				// connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there 
				// is no Wi-Fi connection.
				// Sets refreshDisplay to false.
			} else {
				refreshDisplay = false;
				Toast.makeText(context, "lost_connection", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
