package com.projects.activities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.config.Config;
import com.libraries.usersession.UserAccessSession;
import com.libraries.utilities.MGUtilities;
import com.apps.gasfinder.R;


public class SettingsActivity extends PreferenceActivity {
    private static String appVersion;
    private static Context context;

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        context = getApplicationContext();
        AppBarLayout bar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
            root.addView(bar, 0);
        } else {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);
            root.removeAllViews();
            bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);

            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }else{
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);
            root.addView(content);
            root.addView(bar);
        }

        Toolbar Tbar = (Toolbar) bar.getChildAt(0);
        Tbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = pInfo.versionName;
        } catch(PackageManager.NameNotFoundException e) {
            appVersion = "unknown";
        }

        setupSimplePreferencesScreen();
    }

    @SuppressWarnings("deprecation")
    private void setupSimplePreferencesScreen() {
        addPreferencesFromResource(R.xml.preference_settings);

        EditTextPreference prefRadius = (EditTextPreference)findPreference("notifications_radius");

        float distance = UserAccessSession.getInstance(context).getFilterDistance();
        String val = String.valueOf(Config.MAX_RADIUS_IN_KM);
        if(distance > 0) {
            val = String.valueOf(distance);
        }
        prefRadius.setText(val);

        bindPreferenceSummaryToValue(findPreference("notifications_radius"));
        Preference app_version = findPreference("application_version");
        setPreferenceSummary(app_version, appVersion);
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            }
            else if(preference instanceof EditTextPreference){
                if(preference.getKey().compareTo("notifications_radius") == 0) {
                    String valStr = String.format("%s %s", stringValue,
                            MGUtilities.getStringFromResource(context, R.string.km));

                    preference.setSummary(valStr);

                    float max = UserAccessSession.getInstance(context).getFilterDistanceMax();
                    String maxTitle = String.format("%s (%s %.2f %s)",
                            MGUtilities.getStringFromResource(context, R.string.radius_nearby),
                            MGUtilities.getStringFromResource(context, R.string.max),
                            max,
                            MGUtilities.getStringFromResource(context, R.string.km));

                    preference.setTitle(maxTitle);

                    UserAccessSession.getInstance(context).setFilterDistance(Float.parseFloat(stringValue));
                }
                else {
                    preference.setSummary(stringValue);
                }
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        // Trigger the listener immediately with the preference's
        // current value.

        float distance = UserAccessSession.getInstance(context).getFilterDistance();
        String val = PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), String.valueOf(Config.MAX_RADIUS_IN_KM));

        if(distance > 0) {
            val = String.valueOf(distance);
        }

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, val);
    }

    private static void setPreferenceSummary(Preference preference, String value) {
        preference.setSummary(value);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        // Allow super to try and create a view first
        final View result = super.onCreateView(name, context, attrs);
        if (result != null) {
            return result;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // If we're running pre-L, we need to 'inject' our tint aware Views in place of the
            // standard framework versions
            switch (name) {
                case "EditText":
                    return new AppCompatEditText(this, attrs);
                case "Spinner":
                    return new AppCompatSpinner(this, attrs);
                case "CheckBox":
                    return new AppCompatCheckBox(this, attrs);
                case "RadioButton":
                    return new AppCompatRadioButton(this, attrs);
                case "CheckedTextView":
                    return new AppCompatCheckedTextView(this, attrs);
            }
        }

        return null;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
