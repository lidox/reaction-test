package com.artursworld.reactiontest.controller.analysis.outlierdetection;


import com.artursworld.reactiontest.controller.analysis.forecast.NelderMeadOptimizer;
import com.artursworld.reactiontest.controller.analysis.forecast.TripleExponentialSmoothing;
import com.artursworld.reactiontest.controller.util.GlobalDB;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class OutlierDetectionTest {


    // some observed seasonal data
    double[] dataPoints = {
            141, 53, 78, 137, 182, 161, 177,
            164, 70, 67, 129, 187, 161, 136,
            167, 57, 61, 159, 158, 152, 169,
            181, 65, 60, 146, 186, 180, 181,
            167, 70, 62, 170, 193, 167, 176,
            149, 69, 68, 168, 181, 200, 179,
            181, 83, 72, 157, 188, 193, 173,
            184, 61, 59, 158, 158, 143, 208,
            172, 82, 86, 158, 194, 193, 159
            /*
            162, 68, 62, 157, 162, 187, 184,
            183, 70, 73, 145, 185, 169, 177,
            164, 78, 79, 163, 180, 161, 187,
            170, 60, 74, 138, 169, 138, 157
            */
    };

    @Test
    public void testOutlierDetectionByZScore() {
        double reactionTimeToCheck = 300;
        boolean isOutlier = OutlierDetection.isOutlierByZScore(reactionTimeToCheck, dataPoints);
        assertTrue(isOutlier);
    }

    @Test
    public void testChebyshevsInequality() throws Exception {
        double reactionTimeToCheck = 300;
        boolean isOutlier = OutlierDetection.isOutlierByChebyshev(reactionTimeToCheck, dataPoints);
        assertTrue(isOutlier);
    }

    @Test
    public void testMarkovsInequality() throws Exception {
        double reactionTimeToCheck = 300;
        boolean isOutlier = OutlierDetection.isOutlierByMarkov(reactionTimeToCheck, dataPoints);
        assertTrue(isOutlier);
    }

    @Test
    public void testBrutlagsPredcitonInterval() throws Exception {
        GlobalDB.setLastSeasonalDeviations(null);
        double reactionTimeToCheck = 220;
        int seasonLength = 7;
        NelderMeadOptimizer.Parameters op = NelderMeadOptimizer.optimize(dataPoints, seasonLength);
        double predictedValue = TripleExponentialSmoothing.getPredictions(dataPoints, seasonLength, op.getAlpha(), op.getBeta(), op.getGamma(), 1).get(0);
        boolean isOutlier = PredictionInterval.isOutlier(dataPoints, seasonLength, op.getAlpha(), op.getBeta(), op.getGamma(), predictedValue, reactionTimeToCheck);
        assertTrue(isOutlier);
    }

    @Test
    public void testBrutlagsPredcitonInterval2() throws Exception {
        List<Boolean> checkList = new ArrayList<>();
        int seasonLength = 7;

        double[] newObservedValues = {162, 68, 62, 157, 162, 187, 184,
                183, 70, 73, 145, 185, 169, 177,
                164, 78, 79, 163, 180, 161, 187,
                170, 60, 74, 138, 169, 138, 157};
        for (int i = 0; i < newObservedValues.length; i++) {
            NelderMeadOptimizer.Parameters op = NelderMeadOptimizer.optimize(dataPoints, seasonLength);
            double predictedValue = TripleExponentialSmoothing.getPredictions(dataPoints, seasonLength, op.getAlpha(), op.getBeta(), op.getGamma(), 1).get(0);
            boolean isOutlier = PredictionInterval.isOutlier(dataPoints, seasonLength, op.getAlpha(), op.getBeta(), op.getGamma(), predictedValue, newObservedValues[i]);
            dataPoints = Arrays.copyOf(dataPoints, dataPoints.length + 1);
            dataPoints[dataPoints.length - 1] = newObservedValues[i];
            checkList.add(isOutlier);
        }
        assertTrue(checkList.contains(true));
    }

    @Test
    public void testBrutlagsPredcitonInterval3() throws Exception {
        GlobalDB.setLastSeasonalDeviations(null);
        double[] dataPointsArray = {
                30, 21, 29, 31, 40, 48, 53, 47, 37, 39, 31, 29,
                17, 9, 20, 24, 27, 35, 41, 38, 27, 31, 27, 26,
                21, 13, 21, 18, 33, 35, 40, 36, 22, 24, 21, 20,
                17, 14, 17, 19, 26, 29, 40, 31, 20, 24, 18, 26};

        List<Boolean> checkList = new ArrayList<>();
        int seasonLength = 12;

        double[] newObservedValues = {
                17, 9, 17, 21, 28, 32, 46, 33, 23, 28, 22, 27,
                18, 8, 17, 21, 31, 34, 44, 38, 31, 30, 26, 32
        };

        for (int i = 0; i < newObservedValues.length; i++) {
            NelderMeadOptimizer.Parameters op = NelderMeadOptimizer.optimize(dataPoints, seasonLength);
            double predictedValue = TripleExponentialSmoothing.getPredictions(dataPoints, seasonLength, op.getAlpha(), op.getBeta(), op.getGamma(), 1).get(0);
            boolean isOutlier = PredictionInterval.isOutlier(dataPoints, seasonLength, op.getAlpha(), op.getBeta(), op.getGamma(), predictedValue, newObservedValues[i]);
            dataPoints = Arrays.copyOf(dataPoints, dataPoints.length + 1);
            dataPoints[dataPoints.length - 1] = newObservedValues[i];
            checkList.add(isOutlier);
        }
        assertTrue(checkList.contains(true));
    }


}
