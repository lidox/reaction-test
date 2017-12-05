package com.artursworld.reactiontest.controller.util;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.artursworld.reactiontest.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Holds some global values
 */
public class GlobalDB {

    /**
     * Get the last seasonal deviations
     *
     * @return the last seasonal deviations
     */
    public static double[] getLastSeasonalDeviations() {
        try {
            List seasonalDeviations = getList(R.string.c_last_seasonal_deviations);

            double[] lastSeasonalDeviations = new double[seasonalDeviations.size()];
            for (int i = 0; i < seasonalDeviations.size(); i++) {
                lastSeasonalDeviations[i] = Double.parseDouble(seasonalDeviations.get(i).toString());
            }

            return lastSeasonalDeviations;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Sets the last seasonal deviations
     *
     * @param seasonalDeviations the last seasonal deviations
     */
    public static void setLastSeasonalDeviations(double[] seasonalDeviations) {
        setList(R.string.c_last_seasonal_deviations, new ArrayList(Arrays.asList(seasonalDeviations)));
    }

    /**
     * Save a list of values as json array into shared preferences
     *
     * @param keyId      the name of the preference to modify
     * @param listToSave the new list for the preference.
     */
    private static void setList(int keyId, List listToSave) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(App.getAppContext()).edit();
        JSONArray jsonArray = new JSONArray(listToSave);
        String key = App.getAppContext().getResources().getString(keyId);
        if (key != null && listToSave != null) {
            prefs.putString(key, jsonArray.toString());
            prefs.commit();
            String logMessage = "set global value(key=" + key + ", value=" + listToSave + ")";
            System.out.print(logMessage);
        }
    }

    /**
     * Get the list of values as json array from shared preferences
     *
     * @param keyId the name of the preference to modify
     * @return the saved list within the preference.
     * @throws JSONException if the saved value is not compatible with JSON
     */
    private static List getList(int keyId) throws JSONException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());
        List jsonArrayList = new ArrayList();
        String key = App.getAppContext().getResources().getString(keyId);
        JSONArray jsonArray = (JSONArray) new JSONArray(prefs.getString(key, "[]")).get(0);
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonArrayList.add(jsonArray.get(i));
        }
        return jsonArrayList;
    }

}
