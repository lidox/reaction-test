package com.artursworld.reactiontest.controller.analysis.outlierdetection;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChebyshevInequalityTest {

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
    public void testChebyshevsInequality1() throws Exception {
        double[] timeSeries = getDataPoints();
        double reactionTimeToCheck = 1254;
        boolean outlierByChebyshev = new ChebyshevInequality().isOutlier(reactionTimeToCheck, timeSeries);
        assertTrue(outlierByChebyshev);
    }

    @Test
    public void testMarkovInequality1() throws Exception {
        double[] timeSeries = getDataPoints();
        double reactionTimeToCheck = 2554;
        boolean isOutlier = new MarkovInequality().isOutlier(reactionTimeToCheck, timeSeries);
        assertTrue(isOutlier);
    }

    @Test
    public void testChebyshevsInequality2() throws Exception {
        double[] timeSeries = getDataPoints();
        double reactionTimeToCheck = 1300;
        boolean outlierByChebyshev = new ChebyshevInequality().isOutlier(reactionTimeToCheck, timeSeries);
        assertTrue(outlierByChebyshev);
    }

    @Test
    public void testMedianAbsoluteDeviation1() throws Exception {
        double[] timeSeries = getDataPoints();
        double reactionTimeToCheck = 495;
        boolean outlier = new MedianAbsoluteDeviation().isOutlier(reactionTimeToCheck, timeSeries);
        assertTrue(outlier);
    }

    @Test
    public void testMedianAbsoluteDeviation2() throws Exception {
        double[] timeSeries = getDataPoints();
        double reactionTimeToCheck = 180;
        boolean outlier = new MedianAbsoluteDeviation().isOutlier(reactionTimeToCheck, timeSeries);
        assertTrue(outlier);
    }

    @Test
    public void testChebyshevsInequality3() throws Exception {
        double[] timeSeries = getDataPoints();
        double reactionTimeToCheck = 600;
        boolean outlierByChebyshev = new ChebyshevInequality().isOutlier(reactionTimeToCheck, timeSeries);
        assertFalse(outlierByChebyshev);
    }

}
