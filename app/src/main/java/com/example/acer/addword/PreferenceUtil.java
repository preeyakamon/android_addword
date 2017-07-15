package com.example.acer.addword;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ACER on 7/15/2017.
 */

public class PreferenceUtil {
    private Context ctx;
    private static SharedPreferences spf;
    private static final String PREFERENCE = "sharePreference";
    private static final String PREF_USER_ID = "userID";
    private static final String PREF_USERNAME = "userName";

    public PreferenceUtil(Context ctx) {
        this.ctx = ctx;
        this.spf = ctx.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
    }

    public static void saveUserIDLogin(int userID) {
        SharedPreferences.Editor edit = spf.edit();
        edit.putInt(PREF_USER_ID, userID);
        edit.commit();
    }

    public static void saveUserLogin(String username) {
        SharedPreferences.Editor editor = spf.edit();
        editor.putString(PREF_USERNAME, username);
        editor.commit();
    }

    public static String getUserLogin() {
        return spf.getString(PREF_USERNAME, null);
    }

    public static void setCurrentLevel(String user) {
        String key = String.format("%s-level", user);
        SharedPreferences.Editor editor = spf.edit();
        editor.putString(key, user);
        editor.commit();
    }

    public static void setCurrentLevel(int level) {
        String key = String.format("%s-level", getUserLogin());
        SharedPreferences.Editor editor = spf.edit();
        editor.putInt(key, level);
        editor.commit();
    }

    public static int getCurrentLevel() {
        String key = String.format("%s-level", getUserLogin());
        return spf.getInt(key, 0);
    }

    public static int getPointByLevel(int level) {
        String key = String.format("%s-point-%d", getUserLogin(), level);
        return spf.getInt(key, 0);
    }

    public static void plusPointByLevel(int level) {
        String key = String.format("%s-point-%d", getUserLogin(), level);
        int point = spf.getInt(key, 0);
        point += 1;
        SharedPreferences.Editor editor = spf.edit();
        editor.putInt(key, point);
        editor.commit();
    }

    public static void addBonusPoint(int level) {
        String key = String.format("%s-bonus", getUserLogin());
        SharedPreferences.Editor editor = spf.edit();
        editor.putInt(key, level);
        editor.commit();
    }

    public static int getBonusPoint() {
        String key = String.format("%s-bonus", getUserLogin());
        return spf.getInt(key, 1);
    }

    public static void clearSession() {
        SharedPreferences.Editor editor = spf.edit();
        editor.clear();
        editor.commit();
    }

}
