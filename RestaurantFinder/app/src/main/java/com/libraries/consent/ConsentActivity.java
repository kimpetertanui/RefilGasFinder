package com.libraries.consent;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.config.UIConfig;
import com.libraries.utilities.MGUtilities;
import com.apps.restaurantfinder.MainActivity;
import com.apps.restaurantfinder.R;

public class ConsentActivity extends AppCompatActivity implements View.OnClickListener{

    LockableViewPager viewPager;
    FloatingActionButton fabLeft, fabRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.consent_activity);
        setTitle(R.string.consent);

        viewPager = (LockableViewPager) findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for(int x = 0; x < UIConfig.CONSENT_SCREENS.length; x++) {
            Bundle b = new Bundle();
            Consent consent = UIConfig.CONSENT_SCREENS[x];
            b.putSerializable("consent", consent);
            ConsentFragment f1 = new ConsentFragment();
            f1.setArguments(b);
            adapter.addFragment(f1, MGUtilities.getStringFromResource(this, consent.getTitleResId()));
        }

        viewPager.setSwipeLocked(true);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(UIConfig.CONSENT_SCREENS.length);

        viewPager.addOnPageChangeListener(new LockableViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position > 0) {
                    fabLeft.setVisibility(View.VISIBLE);
                    fabRight.setImageResource(R.drawable.ic_chevron_right);
                }
                check(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fabLeft = findViewById(R.id.fabLeft);
        fabLeft.setOnClickListener(this);
        fabLeft.setVisibility(View.GONE);

        fabRight = findViewById(R.id.fabRight);
        fabRight.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabLeft:
                if(viewPager.getCurrentItem() > 0)
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                break;
            case R.id.fabRight:

                ConsentFragment fragment = (ConsentFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                if(!fragment.isConsentChecked()) {
                    Snackbar.make(fabLeft, R.string.require_consent, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(viewPager.getCurrentItem() < UIConfig.CONSENT_SCREENS.length - 1)
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);

                else if(viewPager.getCurrentItem() == UIConfig.CONSENT_SCREENS.length - 1) {
                    boolean isEnabled = true;
                    for (int x = 0; x < UIConfig.CONSENT_SCREENS.length; x++) {
                        ConsentFragment fragment1 = (ConsentFragment) viewPager.getAdapter().instantiateItem(viewPager, x);
                        if (!fragment1.isConsentChecked()) {
                            isEnabled = false;
                            break;
                        }
                    }

                    if(isEnabled) {
                        ConsentSession.getInstance(this).showGDPR(false);
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                break;
        }
    }

    public void check(int position) {
        if(position == UIConfig.CONSENT_SCREENS.length - 1) {
            boolean isEnabled = true;
            for(int x = 0; x < UIConfig.CONSENT_SCREENS.length; x++) {
                ConsentFragment fragment = (ConsentFragment) viewPager.getAdapter().instantiateItem(viewPager, x);
                if(!fragment.isConsentChecked()) {
                    isEnabled = false;
                    break;
                }
            }

            if(!isEnabled) {
                fabRight.setImageResource(R.drawable.ic_chevron_right);
            }
            else {
                fabRight.setImageResource(R.drawable.ic_done_all);
            }


        }

    }
}
