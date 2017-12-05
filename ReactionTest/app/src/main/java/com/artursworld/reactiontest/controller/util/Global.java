package com.artursworld.reactiontest.controller.util;


import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;

import com.artursworld.reactiontest.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides global access to values from everywhere
 */
public class Global {

    /**
     * Reads boolean from shared preferences by key id
     *
     * @param keyId the key id
     * @return the boolean for the key id. If no values saved 'false' will be returned
     */
    private static boolean getBooleanByKey(int keyId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());
        String key = App.getAppContext().getResources().getString(keyId);
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * Writes boolean valueToSave into shared preferences
     *
     * @param keyId       the key id
     * @param valueToSave the boolean value to be saved
     * @return Returns true if the new value is successfully written to persistent storage.
     */
    private static boolean putBoolean(final String keyId, final boolean valueToSave) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(App.getAppContext()).edit();
        prefs.putBoolean(keyId, valueToSave);
        return prefs.commit();
    }

    /**
     * Reads string from shared preferences by key id
     *
     * @param keyId the key id
     * @return the string for the key id. If no values saved " " will be returned
     */
    private static String getStringByKey(int keyId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());
        String key = App.getAppContext().getResources().getString(keyId);
        return sharedPreferences.getString(key, " ");
    }

    /**
     * Writes String valueToSave into shared preferences
     *
     * @param keyId       the key id
     * @param valueToSave the string value to be saved
     * @return Returns true if the new value is successfully written to persistent storage.
     */
    private static boolean putString(final int keyId, final String valueToSave) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(App.getAppContext()).edit();
        prefs.putString(Strings.getStringByRId(keyId), valueToSave);
        return prefs.commit();
    }


    public static boolean setHasToDisplayAwakeSurvey(boolean hasToDisplayAwakeSurvey) {
        UtilsRG.info("setHasToDisplayAwakeSurvey(" + hasToDisplayAwakeSurvey + ")");
        return putBoolean(Strings.getStringByRId(R.string.c_show_awake_survey), hasToDisplayAwakeSurvey);
    }

    public static boolean hasToDisplayAwakeSurvey() {
        return getBooleanByKey(R.string.c_show_awake_survey);
    }

    /**
     * Get the selected key template scenario for go game reaction test
     *
     * @return selected template scenario
     */
    public static String getTemplateScenarioKeySelected() {
        String stringByKey = getStringByKey(R.string.c_template_scenario);

        boolean templateNotSetYet = stringByKey == null || stringByKey.trim().equals("");
        if (templateNotSetYet)
            return Strings.getStringByRId(R.string.scenario_colors_only);

        return stringByKey;
    }

    /**
     * Get the Ids of the selected scenario using settings preference
     *
     * @return
     */
    public static int[] getImageIdsBySelectedScenario() {
        Integer selectedScenarioId = getSelectedScenarioId();
        TypedArray scenarioImages = App.getAppContext().getResources().obtainTypedArray(selectedScenarioId);
        int len = scenarioImages.length();
        int[] imageIds = new int[len];
        for (int i = 0; i < len; i++)
            imageIds[i] = scenarioImages.getResourceId(i, 0);
        scenarioImages.recycle();
        return imageIds;
    }

    /**
     * Get the id of the selected scenario template
     *
     * @return id of the selected scenario template
     */
    private static Integer getSelectedScenarioId() {
        Map<String, Integer> mapping = new HashMap<>();

        if (mapping.isEmpty()) {
            String[] scenarioTemplateKeys = App.getAppContext().getResources().getStringArray(R.array.templates_to_select_keys);
            TypedArray scenarioTemplateIds = App.getAppContext().getResources().obtainTypedArray(R.array.templates_to_select_values);

            for (int i = 0; i < scenarioTemplateKeys.length; i++) {
                mapping.put(scenarioTemplateKeys[i], scenarioTemplateIds.getResourceId(i, 0));
            }
        }
        String selectedScenarioKey = getTemplateScenarioKeySelected();

        if(!mapping.containsKey(selectedScenarioKey))
            return mapping.get(Strings.getStringByRId(R.string.scenario_colors_only));

        return mapping.get(selectedScenarioKey);
    }

    /**
     * Get the try / measure count per reaction game
     *
     * @return the try count per game
     */
    public static int getTryCountPerGame() {
        return getIntegerByKey(R.string.c_go_game_tries_per_game, 5);
    }

    /**
     * Sets the try / measure count per reaction game
     *
     * @param valueToSet the new value to  write to the persistent storage
     * @return Returns true if the new value is successfully written to persistent storage.
     */
    public static boolean setTryCountPerGame(int valueToSet) {
        return putInt(Strings.getStringByRId(R.string.c_go_game_tries_per_game), valueToSet);
    }

    /**
     * Get reaction test count per operation
     *
     * @return the reaction test count per operation
     */
    public static int getReactionTestCountPerOperation() {
        return getIntegerByKey(R.string.c_reaction_test_count_per_operation, 5);
    }

    /**
     * Sets the reaction test count per operation
     *
     * @param valueToSet the new value to  write to the persistent storage
     * @return Returns true if the new 'reaction test count per operation' is successfully
     * written to persistent storage.
     */
    public static boolean setReactionTestCountPerOperation(int valueToSet) {
        return putInt(Strings.getStringByRId(R.string.c_reaction_test_count_per_operation), valueToSet);
    }

    /**
     * Reads integer from shared preferences by key id
     *
     * @param keyId the key id
     * @return the integer for the key id. If no values saved 'defaultInt' will be returned
     */
    private static int getIntegerByKey(int keyId, int defaultInt) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());
        String key = App.getAppContext().getResources().getString(keyId);
        return sharedPreferences.getInt(key, defaultInt);
    }

    /**
     * Writes Integer valueToSave into shared preferences
     *
     * @param keyId       the key id
     * @param valueToSave the Integer value to be saved
     * @return Returns true if the new value is successfully written to persistent storage.
     */
    private static boolean putInt(final String keyId, final int valueToSave) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(App.getAppContext()).edit();
        prefs.putInt(keyId, valueToSave);
        return prefs.commit();
    }

    public static void setSelectedUser(String selectedUser) {
        putString(R.string.c_selected_user_id, selectedUser);
    }

    public static String getSelectedUser() {
        return getStringByKey(R.string.c_selected_user_id);
    }
}
