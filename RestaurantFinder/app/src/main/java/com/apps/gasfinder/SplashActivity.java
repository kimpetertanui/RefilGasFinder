package com.apps.gasfinder;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.config.Config;
import com.config.UIConfig;
import com.libraries.asynctask.MGAsyncTaskNoDialog;
import com.libraries.consent.Consent;
import com.libraries.consent.ConsentActivity;
import com.libraries.consent.ConsentParser;
import com.libraries.consent.ConsentSession;
import com.libraries.consent.Region;
import com.libraries.utilities.MGUtilities;
import com.projects.activities.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    MGAsyncTaskNoDialog task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_splash);

        if(Config.ALWAYS_SHOW_GDPR_CONSENT) {
            showGDPR();
        }
        else if(Config.ALWAYS_SHOW_GDPR_CONSENT_TO_EU_COUNTRIES) {
            getData();
        }
        else {
            if(ConsentSession.getInstance(this).getShowGDPR()) {
                getData();
            }
            else {
                showMain();
            }
        }
    }

    private void showMain() {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000 * Config.SPLASH_DELAY_IN_SECONDS);
    }

    public void showGDPR() {
        Intent i = new Intent(this, ConsentActivity.class);
        startActivity(i);
        finish();
    }

    public void getData() {
        if(!MGUtilities.hasConnection(this)) {
            showMain();
            return;
        }

        task = new MGAsyncTaskNoDialog(this);
        task.setMGAsyncTaskListener(new MGAsyncTaskNoDialog.OnMGAsyncTaskListenerNoDialog() {

            Region region;
            boolean isGDPR = false;

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTaskNoDialog asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTaskNoDialog asyncTask) {

            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                if(isGDPR) {
                    showGDPR();
                }
                else {
                    if(region != null)
                        ConsentSession.getInstance(SplashActivity.this).showGDPR(false);

                    showMain();
                }
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                try {
                    ConsentParser parser = new ConsentParser();
                    region = parser.getRegion(Consent.GET_REGION_JSON_URL);
                    if(region != null) {
                        Log.e("REGION",region.getCountry());
                        for(int x = 0; x < UIConfig.GDPR_COUNTRY.length; x++) {
                            if(region.getCountry().compareToIgnoreCase(UIConfig.GDPR_COUNTRY[x]) == 0) {
                                isGDPR = true;
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        task.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(task != null)
            task.cancel(true);
    }
}
