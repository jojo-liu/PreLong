package com.longlongago.login.model;

import android.content.SharedPreferences;

import com.longlongago.openvcall.LLAApplication;

/**
 * Created by Jojo on 24/07/2017.
 */

public class UserPreference {
    private static SharedPreferences mUserPreferences;
    private static final String USER_PREFERENCE = "user_preference";

    public static SharedPreferences ensureIntializePreference() {
        if (mUserPreferences == null) {
            mUserPreferences = LLAApplication.getInstance().getSharedPreferences(USER_PREFERENCE, 0);
        }
        return mUserPreferences;
    }

    public static void save(String key, String value) {
        SharedPreferences.Editor editor = ensureIntializePreference().edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String read(String key, String defaultvalue) {
        return ensureIntializePreference().getString(key, defaultvalue);
    }

    public static void remove(String key) {
        SharedPreferences.Editor editor = ensureIntializePreference().edit();
        editor.remove(key);
        editor.commit();
    }
}
