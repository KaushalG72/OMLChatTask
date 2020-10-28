package com.sunilkumar.omlchattask.util;

import android.content.Context;
import android.content.SharedPreferences;

public final class PreferencesController {

    private PreferencesController() {
    }

    private static final String GENERAL_PREFS_ID = "general_prefs";
    public static final String FORM_AUTH = "form_auth";
    public static final String PASS_FIELD1 = "pass1";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(GENERAL_PREFS_ID, Context.MODE_PRIVATE);
    }

    public static void setFormAuth(Context context, boolean b) {
        getPrefs(context).edit().putBoolean(FORM_AUTH, b).apply();
    }

    public static boolean getFormAuth(Context context) {
        return getPrefs(context).getBoolean(FORM_AUTH, false);
    }

    public static void setPassField1(Context context, String s) {
        getPrefs(context).edit().putString(FORM_AUTH, s).apply();
    }

    public static String getPassField1(Context context) {
        return getPrefs(context).getString(FORM_AUTH, "");
    }
}
