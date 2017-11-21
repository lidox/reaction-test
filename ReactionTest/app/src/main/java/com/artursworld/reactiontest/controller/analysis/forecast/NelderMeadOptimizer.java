package com.artursworld.reactiontest.controller.analysis.forecast;


import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateFunctionMappingAdapter;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;

import java.util.List;

public class NelderMeadOptimizer {

    // configuration
    private static final double minValueForOptimizedParameters = 0.001;
    private static final double maxValueForOptimizedParameters = 0.99;
    private static final double simplexRelativeThreshold = 0.0001;
    private static final double simplexAbsoluteThreshold = 0.0001;

    private static final double DEFAULT_LEVEL_SMOOTHING = 0.01;
    private static final double DEFAULT_TREND_SMOOTHING = 0.01;
    private static final double DEFAULT_SEASONAL_SMOOTHING = 0.01;

    private static final int MAX_ALLOWED_NUMBER_OF_ITERATION = 1000;
    private static final int MAX_ALLOWED_NUMBER_OF_EVALUATION = 1000;

    /**
     *
     * @param dataPoints the observed data points
     * @param seasonLength the amount of data points per season
     * @return the optimized parameters
     */
    public static Parameters optimize(double[] dataPoints, int seasonLength) {
        MultivariateFunctionMappingAdapter costFunc = getCostFunction(dataPoints, seasonLength);
        double[] initialGuess = getInitialGuess(dataPoints, seasonLength);
        double[] optimizedValues = optimize(initialGuess, costFunc);
        double alpha = optimizedValues[0];
        double beta = optimizedValues[1];
        double gamma = optimizedValues[2];
        return new Parameters(alpha, beta, gamma);
    }

    /**
     * Optimizes parameters using the Nelder-Mead Method
     * @param initialGuess initial guess / state required for Nelder-Mead-Method
     * @param costFunction which defines that the Mean Squared Error has to be minimized
     * @return the optimized values
     */
    private static double[] optimize(double[] initialGuess, MultivariateFunctionMappingAdapter costFunction) {
        double[] result;

        SimplexOptimizer optimizer = new SimplexOptimizer(simplexRelativeThreshold, simplexAbsoluteThreshold);

        PointValuePair unBoundedResult = optimizer.optimize(
                GoalType.MINIMIZE,
                new MaxIter(MAX_ALLOWED_NUMBER_OF_ITERATION),
                new MaxEval(MAX_ALLOWED_NUMBER_OF_EVALUATION),
                new InitialGuess(initialGuess),
                new ObjectiveFunction(costFunction),
                new NelderMeadSimplex(initialGuess.length));

        result = costFunction.unboundedToBounded(unBoundedResult.getPoint());
        return result;
    }


    /**
     * Defines that the Mean Squared Error has to be minimized
     * in order to get optimized / good parameters for alpha, betta and gamma.
     * It also defines the minimum and maximum values for the parameters to optimize.
     * @param dataPoints the data points
     * @param seasonLength the amount of data points per season
     * @return a cost function  {@link MultivariateFunctionMappingAdapter} which
     * defines that the Mean Squared Error has to be minimized
     * in order to get optimized / good parameters for alpha, betta and gamma
     */
    private static MultivariateFunctionMappingAdapter getCostFunction(final double[] dataPoints, final int seasonLength) {

        MultivariateFunction multivariateFunction = new MultivariateFunction() {
            @Override
            public double value(double[] point) {
                double alpha = point[0];
                double beta = point[1];
                double gamma = point[2];

                if (beta >= alpha) {
                    return Double.POSITIVE_INFINITY;
                }

                List<Double> predictedValues = TripleExponentialSmoothing.getSmoothedDataPointsWithPredictions(dataPoints, seasonLength, alpha, beta, gamma, 1);

                predictedValues.remove(predictedValues.size()-1);

                double meanSquaredError = getMeanSquaredError(dataPoints, predictedValues);
                return meanSquaredError;
            }
        };

        double[][] minMax = getMinMaxValues();
        return new MultivariateFunctionMappingAdapter(multivariateFunction, minMax[0], minMax[1]);
    }

    /**
     * Generates an initial guess/state required for Nelder-Mead-Method.
     * @param dataPoints the data points
     * @param seasonLength the amount of data points per season
     * @return array containing initial guess/state required for Nelder-Mead-Method
     */
    public static double[] getInitialGuess(double[] dataPoints, int seasonLength){
        double[] initialGuess = new double[3];
        initialGuess[0] = DEFAULT_LEVEL_SMOOTHING;
        initialGuess[1] = DEFAULT_TREND_SMOOTHING;
        initialGuess[2] = DEFAULT_SEASONAL_SMOOTHING;
        return initialGuess;
    }

    /**
     * Get minimum and maximum values for the parameters alpha (level coefficient),
     * beta (trend coefficient) and gamma (seasonality coefficient)
     * @return array containing all minimum and maximum values for the parameters alpha, beta and gamma
     */
    private static double[][] getMinMaxValues() {
        double[] min = new double[3];
        double[] max = new double[3];
        min[0] = minValueForOptimizedParameters;
        min[1] = minValueForOptimizedParameters;
        min[2] = minValueForOptimizedParameters;

        max[0] = maxValueForOptimizedParameters;
        max[1] = maxValueForOptimizedParameters;
        max[2] = maxValueForOptimizedParameters;
        return new double[][]{min, max};
    }


    /**
     * Compares observed data points from the past and predicted data points
     * in order to calculate the Mean Squared Error (MSE)
     * @param observedData the observed data points from the past
     * @param predictedData the predicted data points
     * @return the Mean Squared Error (MSE)
     */
    public static double getMeanSquaredError(double[] observedData, List<Double> predictedData){
        double sum = 0;

        for(int i=0; i<observedData.length; i++){
            double error = observedData[i] - predictedData.get(i);
            double sumSquaredError = error * error; // SSE
            sum += sumSquaredError;
        }

        return sum / observedData.length;
    }

    /**
     * Holds the parameters alpha (level coefficient), beta (trend coefficient)
     * and gamma (seasonality coefficient) for Triple Exponential Smoothing.
     */
    public static class Parameters {
        public final double alpha;
        public final double beta;
        public final double gamma;

        public Parameters(double alpha, double beta, double gamma) {
            this.alpha = alpha;
            this.beta = beta;
            this.gamma = gamma;
        }

        public double getAlpha() {
            return alpha;
        }

        public double getBeta() {
            return beta;
        }

        public double getGamma() {
            return gamma;
        }
    };
}
