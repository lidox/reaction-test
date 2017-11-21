package com.artursworld.reactiontest.controller.analysis.outlierdetection;

import android.support.test.runner.AndroidJUnit4;

import com.artursworld.reactiontest.controller.analysis.forecast.NelderMeadOptimizer;
import com.artursworld.reactiontest.controller.analysis.forecast.TripleExponentialSmoothing;
import com.artursworld.reactiontest.controller.util.GlobalDB;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

@RunWith(AndroidJUnit4.class)
public class PredictionIntervalTest {

    @Test
    public void testGetConfidenceBandsByBrutlag() {
        // seasonal data with outliers
        double[] dataPoints = {
                130226, 144195, 148737, 147616, 138059, 109628, 116151,
                128435, 141547, 146539, 150938, 156039, 126802, 130557,
                138076, 151982, 158885, 165399, 154331, 105174, 126772,
                139088, 154358, 165807, 169929, 179915, 150258, 149545,
                160063, 172011, 175223, 174529, 180065, 150068, 152702,
                145114, 153224, 150827, 158528, 167520, 151203, 140114,
                146807, 165592, 170370, 173480, 177643, 149025, 150183,
                160043, 173546, 176436, 185629, 179790, 152203, 149080,
                157638, 159328, 163411, 174057, 169935, 140505, 134055,
                145880, 159758, 166681, 164674, 155197, 145601, 140019,
                143070, 147385, 160501, 150952, 148079, 129107, 125640,
                125773, 139764, 140677, 135099, 142548, 141937, 128953,
                135471, 145574, 146218, 147949, 142803, 132491, 121560
        };

        GlobalDB.setLastSeasonalDeviations(null);
        int seasonLength = 7;
        NelderMeadOptimizer.Parameters op = NelderMeadOptimizer.optimize(dataPoints, seasonLength);
        double predictedValue = TripleExponentialSmoothing.getPredictions(dataPoints, seasonLength, op.getAlpha(), op.getBeta(), op.getGamma(), 1).get(0);
        double observedValue = 130501;
        boolean isOutlier = PredictionInterval.isOutlier(dataPoints, seasonLength, op.getAlpha(), op.getBeta(), op.getGamma(), predictedValue, observedValue);
        Assert.assertTrue(!isOutlier);
    }
}
