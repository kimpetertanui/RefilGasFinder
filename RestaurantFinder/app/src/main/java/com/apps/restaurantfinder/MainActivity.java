package com.apps.restaurantfinder;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.application.MainApplication;
import com.config.Config;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.libraries.utilities.MGUtilities;

import com.projects.activities.SettingsActivity;
import com.projects.fragments.CategoryFragment;
import com.projects.fragments.FavoriteFragment;
import com.projects.fragments.FeaturedFragment;
import com.projects.fragments.GalleryFragment;
import com.projects.fragments.HomeFragment;
import com.projects.fragments.MapFragment;
import com.projects.fragments.SearchFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Fragment currFragment;
    boolean isLoggedPrev, isLoggedCurrent;
    NavigationView navigationView;
    boolean doubleBackToExitPressedOnce = false;
    public static int offsetY = 0;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            offsetY = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        showFragment(new HomeFragment());
        showAds();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.tap_back_again_to_exit, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent i;
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            showFragment(new HomeFragment());
            setTitle(R.string.app_name);
        }
        if (id == R.id.nav_categories) {
            showFragment(new CategoryFragment());
            setTitle(R.string.categories);
        }
        if (id == R.id.nav_featured) {
            showFragment(new FeaturedFragment());
            setTitle(R.string.tab_featured);
        }
        if (id == R.id.nav_favorites) {
            showFragment(new FavoriteFragment());
            setTitle(R.string.favorites);
        }
        if (id == R.id.nav_map) {
            showFragment(new MapFragment());
            setTitle(R.string.tab_map);
        }
        if (id == R.id.nav_galleries) {
            showFragment(new GalleryFragment());
            setTitle(R.string.galleries);
        }
        if (id == R.id.nav_search) {
            showFragment(new SearchFragment());
            setTitle(R.string.search);
        }
        if (id == R.id.nav_settings) {
            i = new Intent( this, SettingsActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showFragment(Fragment fragment) {
        if (currFragment != null && fragment.getClass().equals(currFragment.getClass()))
            return;

        currFragment = fragment;
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        if (fragmentManager == null)
            return;

        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (ft == null)
            return;

        ft.replace(R.id.content_frame, fragment).commitAllowingStateLoss();
    }

    public void showAds() {
        String deviceId = MGUtilities.getDeviceID(getApplicationContext());
        Log.e("DEVICE ID", "------------------------------------------");
        Log.e("DEVICE ID", deviceId);
        Log.e("DEVICE ID", "------------------------------------------");
        FrameLayout frameAds = (FrameLayout) findViewById(R.id.frameAds);
        if (Config.WILL_SHOW_ADS) {
            frameAds.setVisibility(View.VISIBLE);
            if (adView == null) {
                adView = new AdView(this);
                adView.setAdSize(AdSize.SMART_BANNER);
                adView.setAdUnitId(Config.BANNER_UNIT_ID);
                frameAds.addView(adView);

                AdRequest.Builder builder = new AdRequest.Builder();
                if (Config.TEST_ADS_USING_TESTING_DEVICE)
                    builder.addTestDevice(Config.TESTING_DEVICE_HASH);

                if(Config.TEST_ADS_USING_EMULATOR)
                    builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

                AdRequest adRequest = builder.build();
                // Start loading the ad in the background.
                adView.loadAd(adRequest);
            }
        } else {
            frameAds.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        MainApplication app = (MainApplication)getApplication();
        if(app.getMGLocationManager() != null)
            app.getMGLocationManager().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(currFragment instanceof HomeFragment)
//            currFragment.onActivityResult(requestCode, resultCode, data);
    }
}
