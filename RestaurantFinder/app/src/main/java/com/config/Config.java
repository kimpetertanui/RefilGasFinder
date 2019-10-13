package com.config;

import com.apps.gasfinder.R;

/**
 * Created by mg on 20/10/16.
 */
public class Config {

    // You AdMob Banner Unit ID
    public static final String BANNER_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";

    // Set to true if you want to display test ads in emulator
    public static final boolean TEST_ADS_USING_EMULATOR = false;

    // Set to true if you want to display test ads on your testing device
    public static final boolean TEST_ADS_USING_TESTING_DEVICE = false;

    // Add testing device hash
    // It is displayed upon running the app, please check logcat.
    public static final String TESTING_DEVICE_HASH = "79AFFFA212DC44434958839999ADD123";

    // Set to true if you want to display ads in all views.
    public static final boolean WILL_SHOW_ADS = true;

    // Max filter distance in kilometers
    public final static int MAX_RADIUS_IN_KM = 5000;

    // Change this url depending on the name of your web hosting.
    public static String BASE_URL = "http://mangasaurgames.com/apps/mg/restaurantfinder/";

    // Server API KEY
    // This key must match in the Config.php file
    public static final String API_KEY = "45090dcae2aYMK";

    // adjust this depending on the offset of you map info window.
    public final static float MAP_INFO_WINDOW_X_OFFSET = 0.25f;

    // Map zoom level
    public static int MAP_ZOOM_LEVEL = 14;

    // Search Category for all selection
    public final static int CATEGORY_ALL = R.string.all;

    // Default restaurants to fetch to get initial data
    public final static int DEFAULT_RESTAURANTS_COUNT_TO_FIND_DISTANCE = 20;

    // Show always GDPR during startup
    public final static boolean ALWAYS_SHOW_GDPR_CONSENT = false;

    // Show GDPR during startup for EU countries only.
    public final static boolean ALWAYS_SHOW_GDPR_CONSENT_TO_EU_COUNTRIES = false;

    // DO NOT EDIT THIS
    public static String GET_DATA_URL = BASE_URL + "rest/get_data.php";

    // DO NOT EDIT THIS
    public final static int LOADING_DELAY_IN_MILLIS = 500;

    // DO NOT EDIT THIS
    public final static boolean SHOW_LOCATION_COORDINATES_LOG = true;

    // DO NOT EDIT THIS
    public final static double DEBUG_LATITUDE = 37.332331;

    // DO NOT EDIT THIS
    public final static double DEBUG_LONGITUDE= -122.031219;

    // DO NOT EDIT THIS
    public final static boolean DEBUG_LOCATION = false;

    // DO NOT EDIT THIS
    public final static int PERMISSION_REQUEST_LOCATION_SETTINGS = 8888;

    // DO NOT EDIT THIS
    public final static int PERMISSION_REQUEST_CALL = 8889;

    // DO NOT EDIT THIS
    public final static int SPLASH_DELAY_IN_SECONDS = 2;

    // DO NOT EDIT THIS
    public static final Boolean AUTO_ADJUST_DISTANCE = true;
}
