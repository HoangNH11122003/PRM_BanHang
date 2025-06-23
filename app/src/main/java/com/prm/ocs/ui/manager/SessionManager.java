package com.prm.ocs.ui.manager;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String SESSION_PREFS = "SessionPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUserSession(String userId, String username, int role) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putInt(KEY_ROLE, role);
        editor.apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public int getRole() {
        return sharedPreferences.getInt(KEY_ROLE, -1);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.contains(KEY_USER_ID);
    }
}