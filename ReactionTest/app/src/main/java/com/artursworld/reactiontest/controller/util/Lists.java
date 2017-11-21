package com.artursworld.reactiontest.controller.util;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;


public class Lists {

    /**
     * Converts a list of doubles to a double array
     *
     * @param listToConvert the list to convert
     * @return a double array
     */
    public static double[] getArray(List<Double> listToConvert) {
        double[] target = new double[listToConvert.size()];
        for (int i = 0; i < target.length; i++) {
            target[i] = listToConvert.get(i);
        }
        return target;
    }

    /**
     * Combines double[] arrays
     *
     * @param first  the first array to combine
     * @param second the second array to combine
     * @return combined double[] arrays
     */
    public static double[] combine(double[] first, double[] second) {
        List<Double> both = new ArrayList<>(first.length + second.length);
        for (double item : first) {
            both.add(item);
        }
        for (double item : second) {
            both.add(item);
        }
        Double[] doubles = both.toArray(new Double[both.size()]);

        return ArrayUtils.toPrimitive(doubles);
    }
}
