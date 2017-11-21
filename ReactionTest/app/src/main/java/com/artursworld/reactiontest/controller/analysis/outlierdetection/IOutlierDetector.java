package com.artursworld.reactiontest.controller.analysis.outlierdetection;

public interface IOutlierDetector {

    boolean isOutlier(double valueToCheck, double[] dataPoints);

}
