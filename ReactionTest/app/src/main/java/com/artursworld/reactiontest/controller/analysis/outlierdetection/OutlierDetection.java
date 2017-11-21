package com.artursworld.reactiontest.controller.analysis.outlierdetection;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

/**
 * Provides methods for anomaly/outlier detection using time series data
 */
public class OutlierDetection {


    /**
     * Check if given value is an outlier in the time series data using z-score
     *
     * @param valueToCheck   the given values to check
     * @param timeSeriesData the time series data
     * @return true if value is an outlier. Otherwise false.
     */
    public static boolean isOutlierByZScore(double valueToCheck, double[] timeSeriesData) {
        return new ZScore().isOutlier(valueToCheck, timeSeriesData);
    }


    /**
     * Check if given value is an outlier in the time series data using chebyshev inequality
     *
     * @param valueToCheck   the given values to check
     * @param timeSeriesData the time series data
     * @return true if value is an outlier. Otherwise false.
     * @throws Exception
     */
    public static boolean isOutlierByChebyshev(double valueToCheck, double[] timeSeriesData) throws Exception {
        return new ChebyshevInequality().isOutlier(valueToCheck, timeSeriesData);
    }

    /**
     * Check if given value is an outlier in the time series data using markovs inequality
     *
     * @param valueToCheck   the given values to check
     * @param timeSeriesData the time series data
     * @return true if value is an outlier. Otherwise false.
     * @throws Exception
     */
    public static boolean isOutlierByMarkov(double valueToCheck, double[] timeSeriesData) throws Exception {
        return new MarkovInequality().isOutlier(valueToCheck, timeSeriesData);
    }

    /**
     * Get reaction time data by resource Id
     *
     * @param context     the context
     * @param rtResourceId the id of the resource containing the reaction time data
     * @return reaction time data
     */
    public static double[] getReactionTimeData(Context context, int rtResourceId) {
        Resources resources = context.getResources();

        int[] intArray = resources.getIntArray(rtResourceId);

        double[] result = new double[intArray.length];

        for(int i = 0; i < intArray.length; i++){
            result[i] = intArray[i];
        }

        return result;
    }
}
