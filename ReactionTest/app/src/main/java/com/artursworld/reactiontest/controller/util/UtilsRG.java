package com.artursworld.reactiontest.controller.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.artursworld.reactiontest.controller.helper.GameStatus;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.view.games.StartGameSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import ch.qos.logback.classic.android.BasicLogcatConfigurator;

import static com.artursworld.reactiontest.controller.helper.Type.*;

/*
* Provides util functions for reaction game app
*/
public class UtilsRG {

    // database date formats
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static SimpleDateFormat germanDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    public static SimpleDateFormat dayAndhourFormat = new SimpleDateFormat("dd.MM HH:mm");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    // shared preference global constants
    public static String PACKAGE = "com.artursworld.reactiontest.controller.util.";
    public static String OPERATION_ISSUE = PACKAGE + "operation_issue";
    public static String MEDICAL_USER = PACKAGE + "selected_medical_user";
    public static String GAME_TYPE = PACKAGE + "selected_game_type";
    public static String TEST_TYPE = PACKAGE + "selected_test_type";
    public static String REACTION_GAME_ID = PACKAGE + "current_reaction_game_id";

    // logger and its logging functions
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
    public static void putString(final String key, final String value, final Activity activity) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... unusedParams) {
                SharedPreferences.Editor editor = activity.getSharedPreferences("CURRENT_STATE", Context.MODE_PRIVATE).edit();
                if (key != null && value != null) {
                    editor.putString(key, value);
                    editor.commit();
                    UtilsRG.info("set global value(key=" + key + ", value=" + value + ")");
                }
                return null;
            }
        }.execute();

    }

    /**
     * Reads value from shared prefences
     *
     * @param key
     * @param activity
     * @return
     */
    public static String getStringByKey(String key, Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("CURRENT_STATE", Context.MODE_PRIVATE);
        String restoredText = prefs.getString(key, null);
        UtilsRG.info("get global value by key(key=" + key + ", value=" + restoredText + ")");
        return restoredText;
    }

    /**
     * Reads value by key from sharedpreferences
     *
     * @param key          the key
     * @param activity     the running activity
     * @param defaultValue the default value to return in case to value for key found
     * @return the value for given key
     */
    public static int getIntByKey(String key, Activity activity, int defaultValue) {
        SharedPreferences prefs = activity.getSharedPreferences("CURRENT_STATE", Context.MODE_PRIVATE);
        return prefs.getInt(key, defaultValue);
    }

    // TODO: add to types enum
    /*
    * Returns supported test types
    */
    public static String[] getTestTypesList(Activity activity) {
        List<TestTypes> testTypeList = Arrays.asList(TestTypes.values());
        String[] ret = new String[testTypeList.size()];
        for (int i = 0; i < testTypeList.size(); i++) {
            ret[i] = Type.getTranslatedType(testTypeList.get(i), activity);
        }
        return ret;
    }

    // TODO: refactor: add to types enum
    /*
    * returns all supported test types
    */
    public static String[] getGameTypesList(Activity activity) {
        List<GameTypes> gameTypeList = Arrays.asList(GameTypes.values());
        String[] ret = new String[gameTypeList.size()];
        for (int i = 0; i < gameTypeList.size(); i++) {
            ret[i] = Type.getTranslatedGameType(gameTypeList.get(i), activity);
        }
        return ret;
    }

    /**
     * Sets the background color used for the games
     *
     * @param activity the current displayed activity
     * @param colorId  the color to display
     */
    public static void setBackgroundColor(Activity activity, int colorId) {
        try {
            int color = ContextCompat.getColor(activity.getApplicationContext(), colorId);
            activity.getWindow().getDecorView().setBackgroundColor(color);
        } catch (Exception e) {
            String message = "Could not set background color.";
            UtilsRG.error(message + "\n" + e.getLocalizedMessage());
        }
    }

    /**
     * Returns a random number in a range
     *
     * @param min the minimum number for the range
     * @param max the maximum number for the range
     * @return Returns a random number in a range
     */
    public static int getRandomNumberInRange(int min, int max) {
        if (min == max) {
            return min;
        }
        Random r = new Random();
        int random = r.nextInt(max - min + 1) + min;
        UtilsRG.info("Generated random number=" + random);
        return random;
    }

}


