package com.artursworld.reactiontest.controller.maths;


import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;

import de.lmu.ifi.dbs.elki.math.statistics.distribution.ExponentiallyModifiedGaussianDistribution;

public class ExGaussianTest {

    @Test
    public void test1() {
        ExponentiallyModifiedGaussianDistribution distribution = new ExponentiallyModifiedGaussianDistribution(0,0,0);
        System.out.println("so: " + distribution.toString());
        double test = distribution.pdf(3,3,3,3);
        System.out.println(test);
    }

    @Test
    public void testOutlierUsing3SigmaRule(){

        // some observed data points
        double dataPoints[] = {0.464,0.443,0.424,0.386,0.367,0.382,0.455,0.410,0.411,0.424,0.338,0.355,0.342,0.324,
                0.354,0.322,0.364,0.375,1.085,0.575,0.597,0.464,0.414,0.408,1.156,0.819,1.156,1.024,1.152,1.103,
                0.431,0.378,0.358,0.382,0.354,0.435,0.386,0.361,0.397,0.362,0.334,0.357,0.344,0.362,0.317,0.331,
                0.199,0.351,0.284,0.343,0.354,0.336,0.280,0.312,0.778,0.723,0.755,0.774,0.759,0.762,0.490,0.400,
                0.364,0.439,0.441,0.673};

        DescriptiveStatistics maths = new DescriptiveStatistics(dataPoints);
        double sampleMean = maths.getMean();
        double sampleStdDev = maths.getStandardDeviation();
        double sampleSkev = (Math.abs(sampleMean - maths.getPercentile(50)) / sampleStdDev);

        // parameter estimation using method of moments ex-gaussian distribution
        double mean = sampleMean - sampleStdDev * Math.pow(sampleSkev/2., 1./3) ;
        double stdDev = Math.sqrt(sampleStdDev*(1 - Math.pow(sampleSkev/2., 2./3)));
        double tau = sampleStdDev * (Math.pow(sampleSkev/2., 1./3));
        double lambda = 1 / tau;

        ExponentiallyModifiedGaussianDistribution exGaussian = new ExponentiallyModifiedGaussianDistribution(mean, stdDev, lambda);
        isOutlier(exGaussian.getStddev(), 1.2);
    }

    private boolean isOutlier(double stddev, double reactionTime) {
        // there is only 0.3 % chance a value fall in the 3th-sigma (3σ).
        double threeSigma = 3  * stddev;
        if(reactionTime >= threeSigma){
            System.out.println(reactionTime +" is an outlier!");
            return true;
        }
        else {
            System.out.println(reactionTime +" is good!");
            return false;
        }
    }
}
