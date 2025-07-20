package com.example.irr_project.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_ROLE = "user_role";
    private static final String KEY_LOGGED_IN = "is_logged_in";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public UserSession(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setUserLoggedIn(String email, String role) {
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
} 