package com.libraries.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by mg on 24/10/16.
 */
public class MGLocationManager extends Activity {

    final int PERMISSION_REMOVE_LOCATION = 8880;
    public final int PERMISSION_REQUEST_LOCATION = 8800;
    OnLocationListenerM mCallbackLocation;
    Activity mActivity;
    public int INTERVAL_IN_MILLIS = 1000;
    public int METERS_RANGE = 10;
    LocationManager locationManager;

    public interface OnLocationListenerM {
        public void onLocationChanged(Location currentLoc);
        public void onLocationRequestDenied();
        public void onLocationGranted();
    }

    public void setOnLocationListenerM(OnLocationListenerM listener) {
        try {
            mCallbackLocation = (OnLocationListenerM) listener;
        } catch (ClassCastException e)  {
            throw new ClassCastException(this.toString() + " must implement OnLocationListenerM");
        }
    }

    public MGLocationManager(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    checkLocationPermission();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if(mCallbackLocation != null)
                        mCallbackLocation.onLocationRequestDenied();
                }
                return;
            }
            case PERMISSION_REMOVE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    removeLocationUpdates();
                }
                return;
            }
        }
    }

    public Location checkLocationPermission() {

        locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        int permissionCheck = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
                return null;
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
                return null;
                // PERMISSION_REQUEST_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        String locationProvide = LocationManager.NETWORK_PROVIDER;
//        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                mActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Config.PERMISSION_REQUEST_LOCATION);
//                return;
//            }
//
//
//        }
//        if ( ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                mActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Config. PERMISSION_REQUEST_LOCATION);
//                return;
//            }
//
//        }

        if(mCallbackLocation != null)
            mCallbackLocation.onLocationGranted();

        locationManager.requestLocationUpdates(locationProvide, INTERVAL_IN_MILLIS, METERS_RANGE, locationListener);
        Location lastLocation = locationManager.getLastKnownLocation(locationProvide);
        return lastLocation;
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.e("MGLocationManager", "Location Updated [" + location.getLatitude() + "," + location.getLongitude() + "]");
            if(mCallbackLocation != null)
                mCallbackLocation.onLocationChanged(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void removeLocationUpdates() {
        int permissionCheck = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REMOVE_LOCATION);
                return;
                // PERMISSION_REQUEST_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        locationManager.removeUpdates(locationListener);
    }
}
