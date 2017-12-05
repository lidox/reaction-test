package com.artursworld.reactiontest.controller.analysis.outlierdetection;


import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccuracyMeasureTest extends InstrumentationTestCase {


    private RenamingDelegatingContext context = null;

    public void testScores1() throws Exception {

        double valueToTest = 500;

        double[] dataPoints = {
                200, 200, 200, 200, 200, 200, 200, 200, 200, 200,
                300, 300, 300, 300, 300, 300, 300, 300, 300, 300,
                250, 250, 250, 250, 250, 250, 250, 250, 250, 250,
        };

        // Markov Inequality
        double outlierScore1 = new MarkovInequality().getOutlierScore(valueToTest, dataPoints);
        UtilsRG.error("outlierScore = " + outlierScore1);
        boolean isOutlier1 = new MarkovInequality().isOutlier(valueToTest, dataPoints);
        assertFalse("Markov Inequality", isOutlier1);

        // Chebyshev Inequality
        double outlierScore2 = new ChebyshevInequality().getOutlierScore(valueToTest, dataPoints);
        UtilsRG.error("outlierScore = " + outlierScore2);
        boolean isOutlier2 = new ChebyshevInequality().isOutlier(valueToTest, dataPoints);
        assertTrue("Chebyshev Inequality", isOutlier2);

        // MAD score
        double outlierScore3 = new MedianAbsoluteDeviation().getOutlierScore(valueToTest, dataPoints) - 3;
        boolean isOutlier3 = new MedianAbsoluteDeviation().isOutlier(valueToTest, dataPoints);
        assertTrue("Median Absolute Deviation", outlierScore3 > 0);

        // S_n score
        double outlierScore4 = new SnEstimator().getSnScore(valueToTest, dataPoints);
        boolean isOutlier4 = new SnEstimator().isOutlier(valueToTest, dataPoints);
        assertTrue("Sn Estimator", outlierScore4 > 0);

        UtilsRG.error("valueToTest = " + valueToTest);
        UtilsRG.error(outlierScore1 + " (outlier=" + isOutlier1 + ") Markov Inequality");
        UtilsRG.error(outlierScore2 + " (outlier=" + isOutlier2 + ") Chebyshev Inequality");
        UtilsRG.error(outlierScore3 + " (outlier=" + isOutlier3 + ") MAD");
        UtilsRG.error(outlierScore4 + " (outlier=" + isOutlier4 + ") S_n");
    }


    public void testScores2() throws Exception {

        double[] valuesToCheck = {538, 762 , 877 , 1000, 1200, 1250, 1300, 2000, 3000, 4805};

        context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");

        double[] dataPoints = OutlierDetection.getReactionTimeData(context, R.array.all_rt_with_outliers_july_2017);

        List<Double> madScores = new ArrayList<>();
        List<Double> snScores = new ArrayList<>();
        List<Double> markovScores = new ArrayList<>();
        List<Double> chebyshevScores = new ArrayList<>();


        for (Double valueToTest : valuesToCheck) {

            // Markov Inequality
            double outlierScore1 = new MarkovInequality().getOutlierScore(valueToTest, dataPoints);
            UtilsRG.error("outlierScore = " + outlierScore1);
            boolean isOutlier1 = new MarkovInequality().isOutlier(valueToTest, dataPoints);

            // Chebyshev Inequality
            double outlierScore2 = new ChebyshevInequality().getOutlierScore(valueToTest, dataPoints);
            UtilsRG.error("outlierScore = " + outlierScore2);
            boolean isOutlier2 = new ChebyshevInequality().isOutlier(valueToTest, dataPoints);

            // MAD score
            double outlierScore3 = new MedianAbsoluteDeviation().getOutlierScore(valueToTest, dataPoints);
            boolean isOutlier3 = new MedianAbsoluteDeviation().isOutlier(valueToTest, dataPoints);

            // S_n score
            double outlierScore4 = new SnEstimator().getSnScore(valueToTest, dataPoints);
            boolean isOutlier4 = new SnEstimator().isOutlier(valueToTest, dataPoints);

            /*
            UtilsRG.error("valueToTest = " + valueToTest);
            UtilsRG.error(outlierScore1 + " (outlier=" + isOutlier1 + ") Markov Inequality");
            UtilsRG.error(outlierScore2 + " (outlier=" + isOutlier2 + ") Chebyshev Inequality");
            UtilsRG.error(outlierScore3 + " (outlier=" + isOutlier3 + ") MAD");
            UtilsRG.error(outlierScore4 + " (outlier=" + isOutlier4 + ") S_n");
            */

            markovScores.add(outlierScore1);
            chebyshevScores.add(outlierScore2);
            madScores.add(outlierScore3);
            snScores.add(outlierScore4);
        }
        UtilsRG.error("markov Scores = " + markovScores.toString());
        UtilsRG.error("chebyshev Scores = " + chebyshevScores.toString());
        UtilsRG.error("MAD Scores = " + madScores.toString());
        UtilsRG.error("sn Scores = " + snScores.toString());

        DescriptiveStatistics statistics = new DescriptiveStatistics(dataPoints);
        UtilsRG.error("median = " + statistics.getPercentile(50));
        UtilsRG.error("mean = " + statistics.getMean());
        UtilsRG.error("min = " + statistics.getMin());
        UtilsRG.error("max = " + statistics.getMax());
        UtilsRG.error("sdtdev = " + statistics.getStandardDeviation());
    }


}
