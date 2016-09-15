package com.artursworld.reactiontest.controller.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.GameStatus;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.model.persistence.strictmode.StrictModeApplication;
import com.artursworld.reactiontest.view.games.StartGameSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
    public static SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("dd.MM");
    public static SimpleDateFormat audioTimeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");

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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        int restoredNumber = prefs.getInt(key, defaultValue);
        UtilsRG.info("get global value by key(key=" + key + ", value=" + restoredNumber + ")");
        return restoredNumber;
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

    public static void beepDevice(int duration) {
        try {
            UtilsRG.info("Device is beeping for");
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, duration);
        } catch (Exception e) {
            UtilsRG.info("device could not make a beep");
        }
    }

    public static void vibrateDevice(long vibrationDurationOnCountDownFinish, Activity activity) {
        if (activity != null) {
            Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            if (v.hasVibrator()) {
                v.vibrate(vibrationDurationOnCountDownFinish);
                UtilsRG.info("vibrating...");
            } else {
                UtilsRG.info("Device does not have vibration support");
            }
        }
    }

    /**
     * Share file via chooser
     *
     * @param file the file to share
     */
    public static void shareFile(File file, Activity activity, String medId) {
        Uri u1 = null;
        u1 = Uri.fromFile(file);

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        String subject = activity.getResources().getString(R.string.app_name) + " " + activity.getResources().getString(R.string.results) + " "+medId;
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
        sendIntent.setType("text/html");
        activity.startActivity(Intent.createChooser(sendIntent, activity.getResources().getString(R.string.share_using)));
    }

    /**
     * Requests permissions
     * @param activity
     * @param permissions
     * @param requestCode
     * @return true if permissions already set. Otherwise false.
     */
    public static boolean requestPermission(Activity activity, String[] permissions, int requestCode) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
            UtilsRG.info("Permissions:" + Arrays.toString(permissions) + " has been set by user.");
            return true;
        } else {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);//new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE}REQUEST_CODE_RECORD_AUDIO);
            return false;
        }
    }

    /**
     * Check if permission is granted
     * @param activity
     * @param permission the permission e.g. Manifest.permission.RECORD_AUDIO
     * @return
     */
    public static boolean permissionAllowed(Activity activity, String permission) {
        int result = ContextCompat.checkSelfPermission(activity, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else{
            return false;
        }
    }

    public static void startInstalledAppDetailsActivity(Activity activity) {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + activity.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        activity.startActivity(i);
    }
}


