package com.artursworld.reactiontest.controller.analysis.outlierdetection;


import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 * Uses the S_n estimator introduced by Rousseeuw and Croux to detect outliers.
 * The straightforward algorithm for computing is used and need 0(n^2) computation time.
 * However, Croux and Rousseeuw have constructed an 0( n log n )-time algorithm for Sn.
 */


public class SnEstimator implements IOutlierDetector {

    private static double scalingFactor = 1.1926;
    private static double outlierThreshold = 3;

    /**
     * Check if given value is an outlier within the data points
     *
     * @param valueToCheck   the observed value to check
     * @param dataPoints the data points
     * @return true if value is an outlier. Otherwise false.
     */
    public boolean isOutlier(double valueToCheck, double[] dataPoints) {

        double outlierScore = getSnScore(valueToCheck, dataPoints);

        return outlierScore >= outlierThreshold;
    }

    public static double getSnScore(double valueToCheck, double[] dataPoints){

        // median distance of 'valueToCheck' from all other points
        double[] medianDistanceArray = getMedianDistances(dataPoints, valueToCheck);
        double medianDistance = new Median().evaluate(medianDistanceArray);

        //times the median distance of every point from every other point:
        double s_n = getSn(dataPoints);

        double snScore = medianDistance / s_n;

        System.out.println(medianDistance + " median distance");
        System.out.println(s_n + " s_n");
        System.out.println(snScore + " S_n Score");

        return snScore;
    }


    /**
     * Compute the measure of scale 'Sn', from Rousseeuw and Croux (1993).
     *
     * @param dataPoints the data points for the calculation
     * @return the measure of scale 'Sn'
     */
    public static double getSn(double [] dataPoints){

        // get number of elements
        int n = dataPoints.length;

        double constant = getConstant(n);

        // median distance of every point from every other point
        double[] medianDistances = getMedianDistances(dataPoints, Double.MIN_VALUE);

        double median = new Median().evaluate(medianDistances);

        if(median < 1){
            median = 1;
        }

        return scalingFactor * constant * median;
    }

    /**
     * Compute a constant depending on data point count
     *
     * @param n the data point count
     * @return a constant depending on data point count
     */
    private static double getConstant(int n) {
        //  a constant depending on data point count
        double constant;

        // Set constant: bias correction factor for finite sample size
        if(n < 9){
            double[] biasCorrectionFactors = { 0.743, 1.851, 0.954, 1.351, 0.993, 1.198, 1.005, 1.131 };
            constant = biasCorrectionFactors[n - 1];
        }
        // n is odd
        else if(n % 2 == 0){
            constant = n / (n - 0.9 );
        }
        // % n is even
        else {
            constant = 1;
        }
        return constant;
    }

    /**
     * Calculates the median distance of valueToCheck from all other points. In case valueToCheck
     * equals 'Double.MIN_VALUE' it calculates the median distance of every point from every other point
     *
     * @param dataPoints the data points
     * @param valueToCheck   the observed value to check
     * @return the median distances
     */
    private static double[] getMedianDistances(double[] dataPoints, double valueToCheck) {
        int n = dataPoints.length;

        // compute median difference for each element
        double[] medianDistances = new double[n];
        for(int i= 0; i < n; i++) {

            double[] medianDifferences =  new double[n -1];
            for(int index = 0; index < n-1; index++){
                if(index == i)
                    continue;

                double dataPoint = dataPoints[i];
                if(valueToCheck != Double.MIN_VALUE){
                    dataPoint = valueToCheck;
                }

                medianDifferences[index] = Math.abs(dataPoint - dataPoints[index]);
            }
            medianDistances[i] = new Median().evaluate(medianDifferences);
        }
        return medianDistances;
    }

}
