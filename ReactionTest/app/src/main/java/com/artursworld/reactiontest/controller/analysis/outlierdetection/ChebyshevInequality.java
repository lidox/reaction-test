package com.artursworld.reactiontest.controller.analysis.outlierdetection;


import com.artursworld.reactiontest.controller.util.UtilsRG;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Provides Chebyshev's Inequality method to find outliers for non-normally distributed data sets
 */
public class ChebyshevInequality implements IOutlierDetector {

    // threshold to determine the minimum of values within a data set must lie in. 90% = 0.9
    private double probabilityThreshold = 0.90;

    /**
     * Check if given value is an outlier in the data set using Chebyshev's Inequality
     *
     * @param valueToCheck   the given values to check
     * @param timeSeriesData the time series data
     * @return true if value is an outlier. Otherwise false.
     */
    public boolean isOutlier(double valueToCheck, double[] timeSeriesData) {
        double outlierScore = getOutlierScore(valueToCheck, timeSeriesData);
        return isOutlierByChebyshevsInequality(valueToCheck, outlierScore);
    }

    /**
     * Calculates the outlier score using value to be checked and time series data
     *
     * @param valueToCheck   the given values to check
     * @param timeSeriesData the time series data
     * @return an outlier score
     * @throws Exception
     */
    public double getOutlierScore(double valueToCheck, double[] timeSeriesData) {
        // get mean and standard deviation
        DescriptiveStatistics statistics = new DescriptiveStatistics(timeSeriesData);
        double sampleMean = statistics.getMean();
        double sampleStdDev = statistics.getStandardDeviation();

        // get k (number of standard deviations away from the mean)
        double k = getK(probabilityThreshold);

        validateChebyshev(valueToCheck, sampleMean, sampleStdDev, k);

        double acceptableDeviation = k * sampleStdDev;
        double min = sampleMean - acceptableDeviation;
        double max = sampleMean + acceptableDeviation;
        double currentDeviation = Math.abs(valueToCheck - sampleMean);

        double outlierScore = currentDeviation / acceptableDeviation;
        UtilsRG.debug("outlierScore = " + outlierScore);
        UtilsRG.debug(Math.floor(acceptableDeviation) + " acceptableDeviation");
        UtilsRG.debug(valueToCheck + " observed");
        UtilsRG.debug(Math.floor(min) + " min");
        UtilsRG.debug(Math.floor(max) + " max");
        return outlierScore;
    }

    /**
     * Calculates the k (number of standard deviations away from the mean)
     * for the specified probability
     *
     * @param probability the probability that a minimum of just 'probability' percent
     *                    of values within a data set must lie within k standard deviations of the mean.
     * @return the number of standard deviations away from the mean for the given probability.
     */
    public double getK(double probability) {

        if (Math.abs(probability) > 1) {
            return 0;
        }

        double k = Math.sqrt(1. / (1. - probability));
        return k;
    }


    /**
     * Check if given value is an outlier
     *
     * @param valueToCheck the value to check
     * @param outlierScore the score of the outlier
     * @return true if value is an outlier. Otherwise false.
     */
    private boolean isOutlierByChebyshevsInequality(double valueToCheck, double outlierScore) {

        // value in range of min and max
        // boolean isOutlier = !((valueToCheck > min) && (valueToCheck < max));
        boolean isOutlier = outlierScore > 1.;

        if (isOutlier) {
            return true;
        }

        return false;
    }

    /**
     * Check the condition to be able to use Chebyshev's Inequality Theorem
     *
     * @param valueToCheck the given values to check
     * @param sampleMean   the mean of the data set
     * @param sampleStdDev the standard deviation of the data set
     * @param k            the number of standard deviations away from the mean
     */
    private void validateChebyshev(double valueToCheck, double sampleMean, double sampleStdDev, double k) {
        // standard deviations more than 1;
        if (sampleStdDev <= 1) {
            UtilsRG.error("Chebyshev's Inequality does not work, because stdDev < 1");
        }

        double zScore = new ZScore().getZScore(valueToCheck, sampleMean, sampleStdDev);
        // as long as the z score’s absolute value is less than or equal to k
        if (k >= Math.abs(zScore)) {
            //throw new Exception("Chebyshev's Inequality wont work, because k < z-score");
        }

    }

}
