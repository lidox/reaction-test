package com.artursworld.reactiontest.controller.maths;

import com.artursworld.reactiontest.controller.analysis.forecast.NelderMeadOptimizer;
import com.artursworld.reactiontest.controller.analysis.forecast.TripleExponentialSmoothing;
import com.artursworld.reactiontest.controller.analysis.outlierdetection.PredictionInterval;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TripleExponentialSmoothingTest {

    private double[] dataPointsArray = {30, 21, 29, 31, 40, 48, 53, 47, 37, 39, 31, 29, 17, 9, 20, 24, 27, 35, 41, 38,
            27, 31, 27, 26, 21, 13, 21, 18, 33, 35, 40, 36, 22, 24, 21, 20, 17, 14, 17, 19,
            26, 29, 40, 31, 20, 24, 18, 26, 17, 9, 17, 21, 28, 32, 46, 33, 23, 28, 22, 27,
            18, 8, 17, 21, 31, 34, 44, 38, 31, 30, 26, 32};

    @Test
    public void testGetInitialTrend() {
        double initialTrend = TripleExponentialSmoothing.getInitialTrend(dataPointsArray, 12);
        assertEquals("Compare initial trend value with Grishas-Blog values", -0.7847222222222222, initialTrend, 0);
    }

    @Test
    public void testGetInitialSeasonalComponents() {
        List<Double> expectedSeasonalComponents = Arrays.asList(
                -7.4305555555555545, -15.097222222222221, -7.263888888888888, -5.097222222222222,
                3.402777777777778, 8.069444444444445, 16.569444444444446, 9.736111111111112,
                -0.7638888888888887, 1.902777777777778, -3.263888888888889, -0.7638888888888887);
        List<Double> resultSeasonalComponents = TripleExponentialSmoothing.getInitialSeasonalComponents(dataPointsArray, 12);
        assertEquals("Compare seasonal components with Grishas-Blog components", expectedSeasonalComponents, resultSeasonalComponents);
    }

    @Test
    public void testSmoothedValuesByTripleExponentialSmoothing() {
        List<Double> expectedSmoothedValues = Arrays.asList(30.0, 20.34449316666667, 28.410051892109554, 30.438122252647577, 39.466817731253066);

        // parameters
        double alpha = 0.716;
        double beta = 0.029;
        double gamma = 0.993;
        int seasonLength = 12;
        int predictionCount = 1;

        List<Double> resultSmoothedValues = TripleExponentialSmoothing.getSmoothedDataPointsWithPredictions(dataPointsArray, seasonLength, alpha, beta, gamma, predictionCount);
        List<Double> first5resultSmoothedValues = new ArrayList<>(resultSmoothedValues.subList(0, 5));
        assertEquals("Compare smoothed values with Grishas-Blog smoothed values", expectedSmoothedValues, first5resultSmoothedValues);
    }

    @Test
    public void testGetPredictionsByTripleExponentialSmoothing() {

        // parameters from grishas blog article
        double alpha = 0.716;
        double beta = 0.029;
        double gamma = 0.993;
        int seasonLength = 12;
        int predictionCount = 1;


        // get grischas prediction
        List<Double> observedValueListByGrisha = TripleExponentialSmoothing.getPredictions(dataPointsArray, seasonLength, alpha, beta, gamma, predictionCount);

        // calculate optimized parameters
        NelderMeadOptimizer.Parameters optimizedParams = NelderMeadOptimizer.optimize(dataPointsArray, seasonLength);

        System.out.println("alpha = " + alpha + ", beta = " + beta + ", gamma = " + gamma + " (grishas)");
        System.out.print("alpha = ");
        System.out.printf("%.3f", optimizedParams.getAlpha());
        System.out.print(", beta = ");
        System.out.printf("%.3f", optimizedParams.getBeta());
        System.out.print(", gamma = ");
        System.out.printf("%.3f", optimizedParams.getGamma());
        System.out.print(" (optimizer)");
        System.out.println();
        System.out.println();

        // get own prediction
        List<Double> predictedValueList = TripleExponentialSmoothing.getPredictions(
                dataPointsArray,
                seasonLength,
                optimizedParams.getAlpha(),
                optimizedParams.getBeta(),
                optimizedParams.getGamma(),
                predictionCount);

        // compare results
        Double observedValue = observedValueListByGrisha.get(0);
        Double predictedValue = predictedValueList.get(0);
        double meanSquaredError = getMeanSquaredError(observedValue, predictedValue);
        assertTrue("Compare predicted value with Grishas value", meanSquaredError < 10);
    }

    @Test
    public void testGetOptimizedParamsByNelderMeadMethod() {
        double[] dataPoints = {
                30.052513, 19.148496, 25.317692, 27.591437, 32.076456, 23.487961, 28.47594, 35.123753, 36.838485,
                25.007017, 30.72223, 28.693759, 36.640986, 23.824609, 29.311683, 31.770309, 35.177877, 19.775244,
                29.60175, 34.538842, 41.273599, 26.655862, 28.279859, 35.191153, 41.727458, 24.04185, 32.328103,
                37.328708, 46.213153, 29.346326, 36.48291, 42.977719, 48.901525, 31.180221, 37.717881, 40.420211,
                51.206863, 31.887228, 40.978263, 43.772491, 55.558567, 33.850915, 42.076383, 45.642292, 59.76678,
                35.191877, 44.319737, 47.913736
        };

        // random parameters
        double alpha = 0.4;
        double beta = 0.1;
        double gamma = 0.1;
        int seasonLength = 4;
        int valuesToPredictCount = 1;

        List<Double> predictions = TripleExponentialSmoothing.getPredictions(dataPoints, seasonLength, alpha, beta, gamma, valuesToPredictCount);
        assertTrue("Make prediction and check the prediction count", predictions.size() == valuesToPredictCount);
    }

    @Test
    public void testGetOptimizedParamsByNelderMeadMethod2() {
        double[] dataPoints = {141, 53, 78, 137, 182, 161, 177, 164, 70, 67,
                129, 187, 161, 136, 167, 57, 61, 159, 158, 152, 169, 181, 65,
                60, 146, 186, 180, 181, 167, 70, 62, 170, 193, 167, 176, 149,
                69, 68, 168, 181, 200, 179, 181, 83, 72, 157, 188, 193, 173,
                184, 61, 59, 158, 158, 143, 208, 172, 82, 86, 158, 194, 193,
                159, 162, 68, 62, 157, 162, 187, 184, 183, 70, 73, 145, 185,
                169, 177, 164, 78, 79, 163, 180, 161, 187, 170, 60, 74, 138, 169, 138, 157
        };

        long start = System.currentTimeMillis();
        int seasonLength = 7;
        NelderMeadOptimizer.Parameters optimizedParams = NelderMeadOptimizer.optimize(dataPoints, seasonLength);
        System.out.println((System.currentTimeMillis() - start) + " ms Nelder-Mead Optimizer");

        List<Double> predictedValueList = TripleExponentialSmoothing.getPredictions(
                dataPoints,
                seasonLength,
                optimizedParams.getAlpha(),
                optimizedParams.getBeta(),
                optimizedParams.getGamma(),
                1);
        System.out.println((System.currentTimeMillis() - start) + " ms Holt-Winters Forecast");
        System.out.println();
        double predictedValue = predictedValueList.get(0);
        double meanSquaredError = getMeanSquaredError(167., predictedValue);
        assertTrue("Make predictedValue and check the predictedValue count", meanSquaredError < 200);
    }

    @Test
    public void testGetOptimizedParamsByNelderMeadMethod3() {
        // blog data from 30.01.17 - 24.04.17
        double[] dataPoints = {162, 193, 194, 158, 86, 82, 172, 208, 143, 158, 158, 59, 61, 184, 173, 193, 188, 157, 72, 83, 181,
                179, 200, 181, 168, 68, 69, 149, 176, 167, 193, 170, 62, 70, 167, 181, 180, 186, 146, 60, 65, 181, 169, 152, 158, 159,
                61, 57, 167, 136, 161, 187, 129, 67, 70, 164, 177, 161, 182, 137, 78, 53, 141, 167, 173, 177, 154, 55, 46, 177, 135, 126,
                138, 94, 74, 46, 119, 143, 146, 159, 149, 73, 49, 166
        };

        long start = System.currentTimeMillis();
        int seasonLength = 7;

        NelderMeadOptimizer.Parameters optimizedParams = NelderMeadOptimizer.optimize(dataPoints, seasonLength);
        System.out.println((System.currentTimeMillis() - start) + " ms Nelder-Mead Optimizer");

        List<Double> predictedValueList = TripleExponentialSmoothing.getPredictions(
                dataPoints,
                seasonLength,
                optimizedParams.getAlpha(),
                optimizedParams.getBeta(),
                optimizedParams.getGamma(),
                21);
        System.out.println((System.currentTimeMillis() - start) + " ms Holt-Winters Forecast");
        System.out.println();
        double predictedValue = predictedValueList.get(0);
        double observedValue = 158.0;
        double meanSquaredError = getMeanSquaredError(observedValue, predictedValue);
        assertTrue("Make predictedValue and check the predictedValue count", meanSquaredError < 100);
    }

    /**
     * Helper Method for calculating and printing the Mean Squared Error
     *
     * @param observedValue  the observed value
     * @param predictedValue the predicted value
     * @return the Mean Squared Error
     */
    private double getMeanSquaredError(Double observedValue, Double predictedValue) {
        double error = observedValue - predictedValue;
        double meanSequaredError = error * error / 2;

        System.out.println(observedValue + " (observed)");
        System.out.printf("%.2f", predictedValue);
        System.out.print(" (predicted)");
        System.out.println();
        System.out.printf("%.2f", meanSequaredError);
        System.out.print(" MSE");

        System.out.println();
        double percentageError = (Math.abs(100 - ((observedValue / predictedValue) * 100)));
        System.out.printf("%.2f", percentageError);
        System.out.print("% error deviation");
        return meanSequaredError;
    }
}
