package com.artursworld.reactiontest.controller.analysis.forecast;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.GlobalDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class TripleExponentialSmoothing {

    /**
     * Computes the average of trend averages across seasons
     * (the most common practice).
     *
     * @param series       the series data
     * @param seasonLength the seasons length
     * @return the initial trend used for Triple Exponential Smoothing
     */
    public static double getInitialTrend(double[] series, int seasonLength) {
        double sum = 0.0;
        for (int i = 0; i < seasonLength; i++) {
            sum += (series[i + seasonLength] - series[i]) / seasonLength;
        }
        return sum / seasonLength;
    }

    /**
     * Briefly, computes the average level for every observed season,
     * divides every observed value by the average for the season it’s in
     * and finally averages each of these numbers across our observed seasons.
     *
     * @param series       the series data
     * @param seasonLength the seasons length
     * @return the initial seasonal components for each season
     * @see <a href="http://www.itl.nist.gov/div898/handbook/pmc/section4/pmc435.htm">More details</a>
     */
    public static List<Double> getInitialSeasonalComponents(double[] series, int seasonLength) {
        List<Double> seasonals = new ArrayList<>();
        List<Double> seasonAverages = new ArrayList<>();
        int seasonCount = series.length / seasonLength;

        // compute averages for each season
        for (int j = 0; j < seasonCount; j++) {
            double sum = 0.0;
            for (int i = seasonLength * j; i < seasonLength * j + seasonLength; i++) {
                sum += series[i];
            }
            seasonAverages.add(sum / seasonLength);
        }

        // compute initial values: sum of the subtraction of each value within a season
        // and the average of the corresponding season
        for (int i = 0; i < seasonLength; i++) {
            double sumOfValsOverAvg = 0.0;
            for (int j = 0; j < seasonCount; j++) {
                sumOfValsOverAvg += series[seasonLength * j + i] - seasonAverages.get(j);
            }
            seasonals.add(sumOfValsOverAvg / seasonCount);
        }
        return seasonals;
    }


    /**
     * Predicts values using the triple exponential smoothing additive method.
     *
     * @param series          the series data
     * @param seasonLength    the seasons length
     * @param alpha           the smoothing factor (or coefficient)
     * @param beta            the trend factor (or coefficient)
     * @param gamma           the smoothing season factor (or coefficient)
     * @param predictionCount the amount of predictions to calculate
     * @return a list of calculated values containing <code>predictionCount</code>
     * predictions at the end of the list. The complete list contains also smoothed values at the beginning.
     */
    public static List<Double> getSmoothedDataPointsWithPredictions(double[] series, int seasonLength, double alpha, double beta, double gamma, int predictionCount) {
        List<Double> result = new ArrayList<>();
        List<Double> seasonals = getInitialSeasonalComponents(series, seasonLength);
        double smooth = 0.0, last_smooth, trend = 0.0;
        int m;

        for (int i = 0; i < series.length + predictionCount; i++) {


            if (i == 0) {
                // initial values
                smooth = series[0];
                trend = getInitialTrend(series, seasonLength);
                result.add(series[0]);
                continue;
            }
            if (i >= series.length) {
                //  we are forecasting
                m = i - series.length + 1;
                result.add((smooth + m * trend) + seasonals.get(i % seasonLength));
            } else {
                double lastObservedValue = series[i];
                last_smooth = smooth;
                smooth = alpha * (lastObservedValue - seasonals.get(i % seasonLength)) + (1 - alpha) * (smooth + trend);

                // Uncomment to user wikipedia's formula
                //double lastTrend = trend; // wiki
                trend = beta * (smooth - last_smooth) + (1 - beta) * trend;
                //seasonals.set(i % seasonLength, gamma * (lastObservedValue - last_smooth - lastTrend) + (1 - gamma) * seasonals.get(i % seasonLength)); // wiki

                seasonals.set(i % seasonLength, gamma * (lastObservedValue - smooth) + (1 - gamma) * seasonals.get(i % seasonLength));
                result.add(smooth + trend + seasonals.get(i % seasonLength));
            }
        }
        return result;
    }

    /**
     * Predicts values using the triple exponential smoothing additive method.
     *
     * @param dataPoints           the series data points
     * @param seasonLength         the seasons length
     * @param alpha                the smoothing factor (or coefficient)
     * @param beta                 the trend factor (or coefficient)
     * @param gamma                the smoothing season factor (or coefficient)
     * @param valuesToPredictCount the amount of predictions to calculate
     * @return a list of calculated values containing <code>predictionCount</code>
     * predictions at the end of the list.
     */
    public static List<Double> getPredictions(double[] dataPoints, int seasonLength, double alpha, double beta, double gamma, int valuesToPredictCount) {
        boolean hasEnoughDataPointsForAPrediction = dataPoints.length / seasonLength >= 2;

        if(!hasEnoughDataPointsForAPrediction){
            // throw new Exception("Holt Winters Prediction needs at least 2 seasons to be able to predict");
        }

        int dataPointsCount = dataPoints.length;
        List<Double> smoothedValuesWithPredictedValuesList = getSmoothedDataPointsWithPredictions(dataPoints, seasonLength, alpha, beta, gamma, valuesToPredictCount);
        List<Double> predictedValues = new ArrayList<>();
        for (int i = dataPointsCount; i < smoothedValuesWithPredictedValuesList.size(); i++) {
            Double predictedValue = smoothedValuesWithPredictedValuesList.get(i);
            predictedValues.add(predictedValue);
        }
        return predictedValues;
    }


}
