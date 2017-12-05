package com.artursworld.reactiontest.controller.analysis.outlierdetection;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class SnEstimatorTest {

    // some observed reaction times
    double dataPoints[] = {0.464, 0.443, 0.424, 0.386, 0.367, 0.382, 0.455, 0.410, 0.411, 0.424, 0.338, 0.355, 0.342, 0.324,
            0.354, 0.322, 0.364, 0.375, 1.085, 0.575, 0.597, 0.464, 0.414, 0.408, 1.156, 0.819, 1.156, 1.024, 1.152, 1.103,
            0.431, 0.378, 0.358, 0.382, 0.354, 0.435, 0.386, 0.361, 0.397, 0.362, 0.334, 0.357, 0.344, 0.362, 0.317, 0.331,
            0.199, 0.351, 0.284, 0.343, 0.354, 0.336, 0.280, 0.312, 0.778, 0.723, 0.755, 0.774, 0.759, 0.762, 0.490, 0.400,
            0.364, 0.439, 0.441, 0.673};

    private double[] getDataPoints() {
        double[] timeSeries = new double[dataPoints.length];
        for (int i = 0; i < timeSeries.length; i++) {
            timeSeries[i] = dataPoints[i] * 1000;
        }
        return timeSeries;
    }


    @Test
    public void testSnEstimator1() {
        double[] timeSeries = { 1, 5, 2, 2, 7, 4, 1, 5 };

        // Sn = RousseeuwCrouxSn ( X ) % should give 3 .015
        double s_n = new SnEstimator().getSn(timeSeries);
        boolean isCorrectCalculation = s_n == 3.015;

        assertTrue(isCorrectCalculation);
    }

    @Test
    public void testSnEstimator2() {
        double[] timeSeries = { 1, 5, 2, 2, 7, 4, 1, 5 };
        boolean isOutlier = new SnEstimator().isOutlier(10, timeSeries);
        assertTrue(isOutlier);
    }

    @Test
    public void testSnEstimator3() {
        double[] timeSeries = getDataPoints();
        double reactionTimeToCheck = 630;
        boolean isOutlier = new SnEstimator().isOutlier(reactionTimeToCheck, timeSeries);

        DescriptiveStatistics maths = new DescriptiveStatistics(getDataPoints());
        System.out.println(maths.getMean() + " mean");
        System.out.println(maths.getPercentile(50) + " median");

        assertTrue(isOutlier);
    }

    @Test
    public void testSnEstimatorZeroDivisions() {
        double[] timeSeries = { 0, 0, 0, 1, 1};
        double reactionTimeToCheck = 4.2;
        boolean isOutlier = new SnEstimator().isOutlier(reactionTimeToCheck, timeSeries);
        assertTrue(isOutlier);
    }


}
