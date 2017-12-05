package com.artursworld.reactiontest.controller.analysis.outlierdetection;


import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import com.artursworld.reactiontest.controller.util.UtilsRG;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Evaluation Performance Test
 */
public class OutlierDetectionEvaluationTest extends InstrumentationTestCase {


    private double[] dataPoints = {};
    private RenamingDelegatingContext context;

    // config
    private int[] dataSizes = {100, 1000, 2000, 4000, 6000, 8000, 10000, 12000};
    private IOutlierDetector[] techniques = {new ChebyshevInequality(), new MarkovInequality(), new MedianAbsoluteDeviation(), new SnEstimator()};

    private int size = 100;
    private int minValue = 200;
    private int maxValue = 5000;

    @BeforeClass
    public void setUp() {
        context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
    }

    @AfterClass
    public void tearDown() {
        dataPoints = null;
    }

    @Test
    public void testOutlierDetectionPerformance() {
        StringBuilder message = new StringBuilder();
        double valueToCheck = 5000;

        for (IOutlierDetector detector : techniques) {
            List<Long> result = new ArrayList<>();

            for (Integer size : dataSizes) {
                dataPoints = getRandomDataWithOutliers(minValue, maxValue, 10, size);
                long detectionTime = getDetectionTime(valueToCheck, detector);
                result.add(detectionTime);
            }

            for (int i = 0; i < result.size(); i++) {
                message.append("\"" + result.get(i) + "\"");
                if (i != result.size() - 1) {
                    message.append(", ");
                }
            }

            UtilsRG.error(detector.getClass().getSimpleName() + ": " + message);

        }

        assertTrue(!message.equals(""));
    }

    @Test
    public void testOutlierDetectionMemory() {
        StringBuilder message = new StringBuilder();
        double valueToCheck = 5000;

        for (IOutlierDetector detector : techniques) {
            List<Long> result = new ArrayList<>();

            for (Integer size : dataSizes) {
                dataPoints = getRandomDataWithOutliers(minValue, maxValue, 10, size);
                long detectionTime = getDetectionMemory(valueToCheck, detector);
                result.add(detectionTime);
            }

            for (int i = 0; i < result.size(); i++) {
                message.append("\"" + result.get(i) + "\"");
                if (i != result.size() - 1) {
                    message.append(", ");
                }
            }

            UtilsRG.error("memory test: " + detector.getClass().getSimpleName() + ": " + message);
            message = new StringBuilder();
        }

        assertTrue(!message.equals(""));
    }

    /**
     * Get memory usage after running outlier detection method
     *
     * @param valueToCheck the value to check
     * @param detector     the outlier detection technique to use
     * @return the used memory in byte
     */
    private long getDetectionMemory(double valueToCheck, IOutlierDetector detector) {

        // get memory before testing
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        UtilsRG.debug("Used Memory before" + usedMemoryBefore);

        boolean out = detector.isOutlier(valueToCheck, dataPoints);

        // get memory after testing
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        UtilsRG.debug("Memory increased:" + (usedMemoryAfter - usedMemoryBefore));

        return usedMemoryAfter;
    }


    /**
     * Get duration time for running outlier detection method
     *
     * @param valueToCheck the value to check
     * @param detector     the outlier detection technique to use
     * @return the duration the method need to be executed
     */
    private long getDetectionTime(double valueToCheck, IOutlierDetector detector) {
        long startTime = System.nanoTime();

        detector.isOutlier(valueToCheck, dataPoints);

        long stopTime = System.nanoTime();

        return TimeUnit.NANOSECONDS.toNanos(stopTime - startTime);
    }


    /**
     * Get random data points
     *
     * @param minValue              min value
     * @param maxValue              max value
     * @param outlierRatePercentage outlier rate in percentage
     * @param dataSize              the data size
     * @return an array of random data
     */
    private double[] getRandomDataWithOutliers(int minValue, int maxValue, int outlierRatePercentage, int dataSize) {
        double[] dataPoints = new double[dataSize];
        int randomValue;

        outlierRatePercentage = outlierRatePercentage / 10;

        for (int i = 0; i < dataSize; i++) {


            boolean isInThreeSigma = UtilsRG.getRandomNumberInRange(0, 10) > outlierRatePercentage;
            if (isInThreeSigma) {
                randomValue = UtilsRG.getRandomNumberInRange(minValue, maxValue / 10);
            } else {
                randomValue = UtilsRG.getRandomNumberInRange(minValue, maxValue);
            }

            dataPoints[i] = randomValue;
        }

        return dataPoints;
    }

}
