package com.artursworld.reactiontest.controller.analysis.outlierdetection;


import com.artursworld.reactiontest.controller.util.UtilsRG;

import org.apache.commons.math3.stat.descriptive.rank.Median;

public class MedianAbsoluteDeviation implements IOutlierDetector{

    private double scalingFactor = 1.4826;

    /**
     * Check if given value is an outlier in the data points using MAD
     *
     * @param valueToCheck the given values to check
     * @param dataPoints   the data points
     * @return true if value is an outlier. Otherwise false.
     */
    public boolean isOutlier(double valueToCheck, double[] dataPoints) {
        double value = getOutlierScore(valueToCheck, dataPoints);
        UtilsRG.debug((value) + " outlier score using scaling factor = " + scalingFactor);
        return value >= 3;
    }

    /**
     * Get outlier score using value to check and historical data
     *
     * @param valueToCheck the given values to check
     * @param dataPoints   the data points
     * @return outlier score using value to check and historical data
     */
    public double getOutlierScore(double valueToCheck, double[] dataPoints) {
        double median = new Median().evaluate(dataPoints);
        double mad = getMedianAbsoluteDeviation(dataPoints, median);

        double value = Math.abs((valueToCheck - median) / mad * scalingFactor);

        UtilsRG.debug(Math.floor(median) + " median");
        UtilsRG.debug(mad + " mad");
        UtilsRG.debug((value) + " value");
        return value;
    }


    /**
     * Calculates the Median Absolute Deviation
     *
     * @param dataPoints the data points for the calculation
     * @param median     the median of the data points
     * @return the Median Absolute Deviation
     */
    private double getMedianAbsoluteDeviation(double[] dataPoints, double median) {
        double[] absoluteDeviations = new double[dataPoints.length];
        for (int i = 0; i < dataPoints.length; i++) {
            absoluteDeviations[i] = Math.abs(dataPoints[i] - median);
        }
        return new Median().evaluate(absoluteDeviations);
    }

}
