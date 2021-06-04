package com.citrix.mvpntestapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class UrlUtil {
    private static final String SHARED_PREF_NAME = "UrlPreference";
    private static final String SAVED_URL_KEY = "SAVED_URL_KEY";

    public static String getSavedUrl(Context context) {
        return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(SAVED_URL_KEY, "");
    }

    public static void saveUrl(Context context, String url) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(SAVED_URL_KEY, url);
        editor.apply();
    }
}
