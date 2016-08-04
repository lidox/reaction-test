package com.artursworld.reactiontest.controller.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

import ch.qos.logback.classic.android.BasicLogcatConfigurator;

public class UtilsRG {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static SimpleDateFormat germanDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    public static SimpleDateFormat dayAndhourFormat = new SimpleDateFormat("dd.MM HH:mm");
    public static String OPERATION_ISSUE = "operation_issue";
    public static String MEDICAL_USER = "selected_medical_user";

    static {
        BasicLogcatConfigurator.configureDefaultContext();
    }

    public static Logger log = LoggerFactory.getLogger(UtilsRG.class);

    public static void info(String message) {
        log.info(message);
    }

    public static void error(String message) {
        log.error(message);
    }

    /**
     * Writes value into shared preferences
     *
     * @param key
     * @param value
     * @param activity
     */
    public static void putString(String key, String value, Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences("CURRENT_STATE", Context.MODE_PRIVATE).edit();
        if (key != null && value != null) {
            UtilsRG.info("set global value(key="+ key+", value="+value+")");
            editor.putString(key, value);
            editor.commit();
        }
    }

    /**
     * Reads value from shared prefences
     * @param key
     * @param activity
     * @return
     */
    public static String getStringByKey(String key, Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("CURRENT_STATE", Context.MODE_PRIVATE);
        String restoredText = prefs.getString(key, null);
        UtilsRG.info("get global value by key(key="+ key+", value="+restoredText+")");
        return restoredText;
    }

}


