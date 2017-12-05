package com.artursworld.reactiontest.controller.analysis.outlierdetection;


import com.artursworld.reactiontest.controller.analysis.forecast.TripleExponentialSmoothing;
import com.artursworld.reactiontest.controller.util.GlobalDB;
import com.artursworld.reactiontest.controller.util.UtilsRG;

import java.util.List;

/**
 * Provides methods to calculate prediction intervals (confidence bands) introduced by Brulag
 */
public class PredictionInterval {

    /**
     * Sensible values are between 2 and 3
     */
    private static final double delta = 3;

    /**
     * The prediction interval measures deviation for each time point in the
     * seasonal cycle: It's a weighted average absolute deviation updated via
     * triple exponential smoothing.
     *
     * @param dataPoints     the series data points
     * @param seasonLength   the seasons length
     * @param alpha          the smoothing factor (or coefficient)
     * @param beta           the trend factor (or coefficient)
     * @param gamma          the smoothing season factor (or coefficient)
     * @param predictedValue the predicted value using triple exponential smoothing
     * @param observedValue  the observed value
     * @return the deviation
     */
    public static Double getDeviation(double[] dataPoints, int seasonLength, double alpha, double beta, double gamma, double predictedValue, double observedValue) throws Exception {
        double[] seasonalDeviations = GlobalDB.getLastSeasonalDeviations();

        if (seasonalDeviations == null) {
            List<Double> smoothedDataPoints = TripleExponentialSmoothing.getSmoothedDataPointsWithPredictions(dataPoints, seasonLength, alpha, beta, gamma, 1);
            seasonalDeviations = new double[seasonLength];
            double newDeviationSum = 0;
            for (int i = 0; i < seasonLength; i++) {
                double newDeviation = getNewDeviation(gamma, smoothedDataPoints.get(i), dataPoints[i], 0);
                seasonalDeviations[i] = newDeviation;
                newDeviationSum += newDeviation;
            }
            seasonalDeviations[0] = newDeviationSum / (seasonalDeviations.length - 1);
        }
        if(seasonalDeviations.length != seasonLength){
            throw new Exception("Cannot calculate prediction interval, because last deviation array size != seanson legth");
        }

        int seasonalDeviationIndex = dataPoints.length % seasonLength;
        double lastObservedSeasonalDeviation = seasonalDeviations[seasonalDeviationIndex];

        // update new deviation
        double newDeviation = getNewDeviation(gamma, predictedValue, observedValue, lastObservedSeasonalDeviation);
        seasonalDeviations[seasonalDeviationIndex] = newDeviation;
        GlobalDB.setLastSeasonalDeviations(seasonalDeviations);

        return lastObservedSeasonalDeviation;
    }

    /**
     * @param gamma                 the smoothing season factor (or coefficient)
     * @param predictedValue        the predicted value using triple exponential smoothing
     * @param observedValue         the observed value
     * @param lastObservedDeviation the last observed deviation
     * @return the new deviation
     */
    private static double getNewDeviation(double gamma, double predictedValue, double observedValue, double lastObservedDeviation) {
        return gamma * Math.abs(observedValue - predictedValue) + (1 - gamma) * lastObservedDeviation;
    }

    /***
     * Check if observed value of time series falls out the prediction interval
     * @param dataPoints     the series data points
     * @param seasonLength   the seasons length
     * @param alpha          the smoothing factor (or coefficient)
     * @param beta           the trend factor (or coefficient)
     * @param gamma          the smoothing season factor (or coefficient)
     * @param predictedValue the predicted value using triple exponential smoothing
     * @param observedValue  the observed value
     * @return True if observed value is an outlier, otherwise false
     */
    public static boolean isOutlier(double[] dataPoints, int seasonLength, double alpha, double beta, double gamma, double predictedValue, double observedValue) {
        try {
            double deviation = getDeviation(dataPoints, seasonLength, alpha, beta, gamma, predictedValue, observedValue);
            double min = predictedValue - (deviation * delta);
            double max = predictedValue + (deviation * delta);

            boolean isOutlier = !(observedValue > min && observedValue < max);
            System.out.println();
            System.out.println(Math.floor(deviation) + " deviation");
            System.out.println(Math.floor(predictedValue) + " predicted");
            System.out.println(observedValue + " observed");
            System.out.println(Math.floor(min) + " min");
            System.out.println(Math.floor(max) + " max");

            return isOutlier;
        }
        catch (Exception e){
            UtilsRG.error(e.getLocalizedMessage());
            return false;
        }
    }

}
