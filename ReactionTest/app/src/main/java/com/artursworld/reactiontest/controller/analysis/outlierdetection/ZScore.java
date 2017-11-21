package com.artursworld.reactiontest.controller.analysis.outlierdetection;


import com.artursworld.reactiontest.controller.util.UtilsRG;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Provides z-score method to find outliers for non-normally distributed data sets
 */
public class ZScore {

    // threshold to determine an outlier
    private int threshold = 3;

    /**
     * Check if given value is an outlier in the time series data using z-score
     *
     * @param valueToCheck   the given values to check
     * @param timeSeriesData the time series data
     * @return true if value is an outlier. Otherwise false.
     */
    public boolean isOutlier(double valueToCheck, double[] timeSeriesData) {

        // get mean and standard deviation
        DescriptiveStatistics statistics = new DescriptiveStatistics(timeSeriesData);
        double sampleMean = statistics.getMean();
        double sampleStdDev = statistics.getStandardDeviation();

        // get z-score
        double zScore = getZScore(valueToCheck, sampleMean, sampleStdDev);

        // check if z-score is anomalous
        return isOutlier(zScore);
    }

    /**
     * Calculated the z-score for a data point
     *
     * @param dataPoint         the data point
     * @param mean              the mean of the time series data
     * @param standardDeviation the standard deviation of the time series data
     * @return If standard deviation equals 0 -> 0 will be returned. Otherwise the z-score
     */
    public double getZScore(double dataPoint, double mean, double standardDeviation) {

        if (standardDeviation == 0) {
            UtilsRG.info("Cannot calculate z-score, because standard deviation equals 0.");
            return 0;
        }

        double zScore = (dataPoint - mean) / standardDeviation;
        UtilsRG.info("calculated z-score = " + zScore + " for dataPoint = " + dataPoint);
        return zScore;
    }

    /**
     * Check if z-score is higher than the threshold
     *
     * @param zScore the z-score of the data point
     * @return True if z-score is considered as outlier, otherwise false
     */
    private boolean isOutlier(double zScore) {

        boolean isOutlier = Math.abs(zScore) >= threshold;

        UtilsRG.info("z-score = " + Math.abs(zScore) + " and threshold = " + threshold);

        if (isOutlier)
            return true;
        else
            return false;
    }


}
