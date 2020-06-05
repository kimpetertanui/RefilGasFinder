package com.libraries.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.restaurantfinder.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MGUtilities {

	public static boolean hasConnection(Context c) {
		
	    ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

	    NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    if (wifiNetwork != null && wifiNetwork.isConnected()) {
	      return true;
	    }

	    NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	    if (mobileNetwork != null && mobileNetwork.isConnected()) {
	      return true;
	    }

	    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
	    if (activeNetwork != null && activeNetwork.isConnected()) {
	      return true;
	    }

	    return false;
	}

	public static String getDeviceID(Context context) {
		String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		return md5(android_id).toUpperCase();
	}

	public static void dismissKeyboard(Context context, EditText editText) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	public static void showAlertView(Activity act, int resIdTitle, int resIdMessage) {
		AlertDialog.Builder alert = new AlertDialog.Builder(act);
		alert.setTitle(resIdTitle);
		alert.setMessage(resIdMessage);
		alert.setPositiveButton(act.getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
		alert.create();
		alert.show();
	}

	public static String md5(final String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static boolean isLocationEnabled(Context context) {
		LocationManager service = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		boolean isEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
				service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		return isEnabled;
	}

	public static void showNotifier(Activity c, int offsetY, int stringId) {
		LayoutInflater inf = (LayoutInflater) c.getLayoutInflater();
		View v = inf.inflate(R.layout.notifier_results, (ViewGroup) c.findViewById(R.id.root));
		v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		TextView tvNotifier = (TextView) v.findViewById(R.id.tvNotifier);
		tvNotifier.setText(stringId);

		Toast toast = new Toast(c);
		toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, offsetY);
		toast.setView(v);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}

	public static String getStringFromResource(Context c, int resid) {
		return c.getResources().getString(resid);
	}
}
