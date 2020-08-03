package com.dpcsa.compon.single;

import android.content.Context;
import android.content.SharedPreferences;

import com.dpcsa.compon.tools.Constants;

public class ComponPrefTool {
    private static final String PREFERENCES_NAME = "simple_app_prefs";
    //    private static final String TUTORIAL = "tutorial";
    private static final String AUTH = "auth";
    private static final String USER_KEY = "user_key";
    private static final String COOKIE = "cookie";
    private static final String TOKEN = "token";
    private static final String PUSH_TOKEN = "push_token";
    private static final String STATUS_COLOR = "STATUS_COLOR";
    private static final String LOCALE = "locale";
//    public static String SMPL_PUSH_TYPE = "SMPL_PUSH_TYPE";
    private static final String UPDATE_DB_DATE = "UpdateDBDate",
            SPLASH_SCREEN = "SPLASH_SCREEN",
            SPLASH_NAME_SCREEN = "SPLASH_NAME_SCREEN",
            PROFILE = "PROFILE";

    public void setUpdateDBDate(String value) {
        getEditor().putString(UPDATE_DB_DATE, value).commit();
    }

    public String getUpdateDBDate() {
        return getSharedPreferences().getString(UPDATE_DB_DATE, "");
    }

    public void setLocale(String value) {
        getEditor().putString(LOCALE, value).commit();
    }

    public String getLocale() {
        return getSharedPreferences().getString(LOCALE, "");
    }

    public void setStatusBarColor(int value) {
        getEditor().putInt(STATUS_COLOR, value).commit();
    }

    public int getStatusBarColor() {
        return getSharedPreferences().getInt(STATUS_COLOR, 0);
    }

    public void setNameBoolean(String name, boolean value) {
        getEditor().putBoolean(name, value).commit();
    }

    public void setNameString(String name, String value) {
        getEditor().putString(name, value).commit();
    }

    public void setSplashNameScreen(String value) {
        getEditor().putString(SPLASH_NAME_SCREEN, value).commit();
    }

    public String getSplashNameScreen() {
        return getSharedPreferences().getString(SPLASH_NAME_SCREEN, "");
    }

    public void setPushType(String value) {
        getEditor().putString(Constants.PUSH_TYPE, value).commit();
    }

    public String getPushData() {
        return getSharedPreferences().getString(Constants.PUSH_DATA, "");
    }

    public void setPushData(String value) {
        getEditor().putString(Constants.PUSH_DATA, value).commit();
    }

    public String getPushType() {
        return getSharedPreferences().getString(Constants.PUSH_TYPE, "");
    }

    public void setSplashScreen(int value) {
        getEditor().putInt(SPLASH_SCREEN, value).commit();
    }

    public int getSplashScreen() {
        return getSharedPreferences().getInt(SPLASH_SCREEN, 0);
    }

    public boolean getNameBoolean(String name) {
        return getSharedPreferences().getBoolean(name, false);
    }

    public String getNameString(String name) {
        return getSharedPreferences().getString(name, "");
    }

    public void setNameInt(String name, int value) {
        getEditor().putInt(name, value).commit();
    }

    public int getNameInt(String name, int def) {
        return getSharedPreferences().getInt(name, def);
    }

//    public void setTutorial(boolean value) {
//        getEditor().putBoolean(TUTORIAL, value).commit();
//    }
//
//    public boolean getTutorial() {
//        return getSharedPreferences().getBoolean(TUTORIAL, false);
//    }

    public void setAuth(boolean value) {
        getEditor().putBoolean(AUTH, value).commit();
    }

    public boolean getAuth() {
        return getSharedPreferences().getBoolean(AUTH, false);
    }

    public void setPushToken(String token) {
        getEditor().putString(PUSH_TOKEN, token).commit();
    }

    public String getPushToken() {
        return getSharedPreferences().getString(PUSH_TOKEN, "");
    }





    public void setSessionToken(String token) {
        getEditor().putString(TOKEN, token).commit();
    }

    public String getSessionToken() {
        return getSharedPreferences().getString(TOKEN, "");
    }

    public void setProfile(String value) {
        getEditor().putString(PROFILE, value).commit();
    }

    public String getProfile() {
        return getSharedPreferences().getString(PROFILE, "{}");
    }

    public void setSessionCookie(String cookie) {
        getEditor().putString(COOKIE, cookie).commit();
    }

    public String getSessionCookie() {
        return getSharedPreferences().getString(COOKIE, null);
    }

    public void setUserKey(String user_key) {
        getEditor().putString(USER_KEY, user_key).commit();
    }

    public String getUserKey() {
        return getSharedPreferences().getString(USER_KEY, "");
    }

    //  *************************************************

    private Context context;

    public ComponPrefTool(Context context) {
        this.context = context;
    }

    private SharedPreferences.Editor getEditor() {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
