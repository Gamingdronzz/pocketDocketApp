package com.example.hp.pocket_docket.shared_preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hp on 25-05-2017.
 */

public class SavedSharedPreference {
    static final String PREF_USER_NAME = "username";
    static final String PREF_NAME = "name";
    static final String PREF_TYPE = "type";
    static final String PREF_CODE = "code";
    static final String PREF_WORK_FLAG = "workflag";
    static final String PREF_CUR_MOD = "currentModule";
    static final String PREF_CUR_PROJECT = "currentProject";
    static final String PREF_CUR_MOD_ID = "currentModuleId";
    static final String PREF_ASC_ID = "associationId";
    static String PREF_IN_TIME = "intime";

    static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setUserName(Context context, String userName) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    public static String getUserName(Context context) {
        return getSharedPreferences(context).getString(PREF_USER_NAME, "");
    }

    public static void setName(Context context, String name) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_NAME, name);
        editor.commit();
    }

    public static String getName(Context context) {
        return getSharedPreferences(context).getString(PREF_NAME, "");
    }


    public static void setType(Context context, String type) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_TYPE, type);
        editor.commit();
    }

    public static String getType(Context context) {
        return getSharedPreferences(context).getString(PREF_TYPE, "4");
    }

    public static void setCode(Context context, String code) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_CODE, code);
        editor.commit();
    }

    public static String getCode(Context context) {
        return getSharedPreferences(context).getString(PREF_CODE, " ");
    }

    public static void setFlag(Context context, boolean flag) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(PREF_WORK_FLAG, flag);
        editor.commit();
    }

    public static boolean getFlag(Context context) {
        return getSharedPreferences(context).getBoolean(PREF_WORK_FLAG, false);
    }

    public static void setCurModule(Context context, String mod) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_CUR_MOD, mod);
        editor.commit();
    }

    public static String getCurModule(Context context) {
        return getSharedPreferences(context).getString(PREF_CUR_MOD, "");
    }

    public static void setCurProject(Context context, String pro) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_CUR_PROJECT, pro);
        editor.commit();
    }

    public static String getCurPoject(Context context) {
        return getSharedPreferences(context).getString(PREF_CUR_PROJECT, "");
    }

    public static void setCurModuleId(Context context, String mod) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_CUR_MOD_ID, mod);
        editor.commit();
    }

    public static String getCurModuleId(Context context) {
        return getSharedPreferences(context).getString(PREF_CUR_MOD_ID, "");
    }

    public static void setInTime(Context context, String time) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_IN_TIME, time);
        editor.commit();
    }

    public static String getInTime(Context context) {
        return getSharedPreferences(context).getString(PREF_IN_TIME, "");
    }

    public static void setAscId(Context context, String id) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_ASC_ID, id);
        editor.commit();
    }

    public static String getAscId(Context context) {
        return getSharedPreferences(context).getString(PREF_ASC_ID, "");
    }

    public static void clearPref(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.commit();
    }


}
