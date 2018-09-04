package com.artursworld.reactiontest.controller.util;


import java.text.DecimalFormat;

/**
 * Provides string utilities
 */
public class Strings {

    /**
     * Get the string by resource id
     * @param R_ID the resource id
     * @return the string by resource id
     */
    public static String getStringByRId(int R_ID) {
        return App.getAppContext().getResources().getString(R_ID);
    }

    public static String shortenPercentage(double percentageValue) {
        String ret = String.valueOf(percentageValue);
        try {

            DecimalFormat formatter = new DecimalFormat("#");

            ret = (formatter.format(percentageValue));
        } catch (Exception e) {
            UtilsRG.error("Could not parse reaction time = " + percentageValue);
        }

        return ret;
    }
}
