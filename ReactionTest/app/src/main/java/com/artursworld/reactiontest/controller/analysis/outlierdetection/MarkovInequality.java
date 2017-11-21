package com.artursworld.reactiontest.controller.analysis.outlierdetection;

import com.artursworld.reactiontest.controller.util.UtilsRG;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;


public class MarkovInequality implements IOutlierDetector {


    // threshold to determine the probability that the observed value appears
    private double probabilityThreshold = 0.20;

    /**
     * Check if given value is an outlier in the data set using Markov's Inequality
     *
     * @param valueToCheck   the given values to check
     * @param timeSeriesData the time series data
     * @return true if value is an outlier. Otherwise false.
     */
    public boolean isOutlier(double valueToCheck, double[] timeSeriesData) {
        double outlierScore = getOutlierScore(valueToCheck, timeSeriesData);

        // check if
        return isOutlierByMarkovsInequality(valueToCheck, outlierScore);
    }


    /**
     * Calculate the probability to be compared with the probability threshold (outlier threshold)
     *
     * @param valueToCheck   the given values to check
     * @param timeSeriesData the time series data
     * @return the probability to be compared with the probability threshold (outlier threshold)
     * @throws Exception In case some conditions are not met, an exception will be thrown
     */
    public double getOutlierScore(double valueToCheck, double[] timeSeriesData) {
        // get mean
        DescriptiveStatistics statistics = new DescriptiveStatistics(timeSeriesData);
        double sampleMean = statistics.getMean();

        // validate
        validateMarkov(valueToCheck, sampleMean);

        // get outlier score
        double probability = sampleMean / valueToCheck;
        double outlierScore = probabilityThreshold / probability;

        return outlierScore;
    }

    /**
     * Check if given value is an outlier
     *
     * @param valueToCheck the value to check
     * @param outlierScore the outlierScore of time series data
     * @return true if value is an outlier. Otherwise false.
     */
    private boolean isOutlierByMarkovsInequality(double valueToCheck, double outlierScore) {

        //boolean isOutlier = outlierScore <= probabilityThreshold;
        boolean isOutlier = outlierScore > 1;

        UtilsRG.debug("outlierScore = " + outlierScore);
        UtilsRG.debug(Math.floor(outlierScore) + " outlierScore");
        UtilsRG.debug(valueToCheck + " observed");
        UtilsRG.debug(outlierScore + " outlierScore");
        UtilsRG.debug(probabilityThreshold + " probabilityThreshold");

        if (isOutlier) {
            return true;
        }

        return false;
    }

    /**
     * Check the condition to be able to use Markov's Inequality Theorem
     *
     * @param valueToCheck the given values to check
     * @param sampleMean   the mean of the data set
     */
    private void validateMarkov(double valueToCheck, double sampleMean) {
        // standard deviations more than 1;
        if (sampleMean < 0) {
            UtilsRG.error("Markov's Inequality does not work, because mean < 1");
        }

        if (valueToCheck < 0) {
            UtilsRG.error("Markov's Inequality does not work, because observed value must be positive < 1");
        }

    }

}
