package com.config;

import com.libraries.consent.Consent;
import com.apps.restaurantfinder.R;

/**
 * Created by mg on 20/10/16.
 */
public class UIConfig {

    public static final int IMAGE_PLACEHOLDER_PROFILE_THUMB = R.mipmap.bg_image_thumb_placeholder;

    public static final int IMAGE_PLACEHOLDER = R.mipmap.bg_image_placeholder;

    public static int BORDER_RADIUS = R.dimen.border_radius;

    public static int BORDER_WIDTH = R.dimen.border_width;

    public static int THEME_COLOR = R.color.colorAccent;

    public static int[] INNER_TAB_TITLE = {
            R.string.sub_tab_details,
            R.string.sub_tab_map,
            R.string.sub_tab_gallery,
    };

    public static int[] SELECTED_INNER_TAB_BG = {
            R.mipmap.inner_tab_left_selected,
            R.mipmap.inner_tab_left_selected,
            R.mipmap.inner_tab_left_selected
    };

    public static int[] UNSELECTED_INNER_TAB_BG = {
            R.mipmap.inner_tab_left,
            R.mipmap.inner_tab_left,
            R.mipmap.inner_tab_left
    };

    public final static Consent[] CONSENT_SCREENS = {
            new Consent(
                    "AGE_16",
                    R.string.age_consent_title,
                    R.string.age_consent_category,
                    R.string.age_consent_what,
                    R.string.age_consent_why_needed,
                    R.string.age_consent_more_information,
                    "https://gdpr-info.eu/art-8-gdpr/"),

            new Consent(
                    "BASIC_APP",
                    R.string.basic_app_consent_title,
                    R.string.basic_app_consent_category,
                    R.string.basic_app_consent_what,
                    R.string.basic_app_consent_why_needed,
                    R.string.basic_app_consent_more_information,
                    "http://example.com/gdpr"),

            new Consent(
                    "ADS",
                    R.string.ads_consent_title,
                    R.string.ads_consent_category,
                    R.string.ads_consent_what,
                    R.string.ads_consent_why_needed,
                    R.string.ads_consent_more_information,
                    "https://firebase.google.com/support/privacy")
    };

    public final static String[] GDPR_COUNTRY = {
            "Austria",
            "Belgium",
            "Bulgaria",
            "Croatia",
            "Republic of Cyprus",
            "Czech Republic",
            "Denmark",
            "Estonia",
            "Finland",
            "France",
            "Germany",
            "Greece",
            "Hungary",
            "Ireland",
            "Italy",
            "Latvia",
            "Lithuania",
            "Luxembourg",
            "Malta",
            "Netherlands",
            "Poland",
            "Portugal",
            "Romania",
            "Slovakia",
            "Slovenia",
            "Spain",
            "Sweden",
            "United Kingdom"
    };
}

