package com.artursworld.reactiontest.controller.util;

import java.util.Arrays;
import java.util.List;

public class Statistics {
    double[] data;
    int size;

    public Statistics(double[] data) {
        this.data = data;
        size = data.length;
    }

    public Statistics(List<Double> data){
        this.size = data.size();
        double[] dataArray = new double[data.size()];
        for (int i = 0;i <data.size(); i++) {
            dataArray[i] = data.get(i);
        }
        this.data = dataArray;
    }

    public double getMean() {
        double sum = 0.0;
        for (double a : data)
            sum += a;
        return sum / size;
    }

    double getVariance() {
        double mean = getMean();
        double temp = 0;
        for (double a : data)
            temp += (a - mean) * (a - mean);
        return temp / size;
    }

    public double getStdDev() {
        return Math.sqrt(getVariance());
    }

    public double getMedian() {
        Arrays.sort(data);

        if (data.length % 2 == 0) {
            return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
        } else {
            return data[data.length / 2];
        }
    }

    public static float getPercentageComparedWithPreOpValue(float preOpAvgReactionTime, float averageReactionInOpForSingleTest){
        return (preOpAvgReactionTime / averageReactionInOpForSingleTest) * 100;
    }
}
