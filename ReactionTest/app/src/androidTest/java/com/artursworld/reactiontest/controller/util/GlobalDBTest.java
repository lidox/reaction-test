package com.artursworld.reactiontest.controller.util;

import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import com.artursworld.reactiontest.R;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RunWith(AndroidJUnit4.class)
public class GlobalDBTest {

    @Test
    public void testGetLastSeasonalDeviations() {
        double[] deviations = { 1, 2, 3, 4, 5};
        GlobalDB.setLastSeasonalDeviations(deviations);
        double[] resultDeviations = GlobalDB.getLastSeasonalDeviations();
        Assert.assertTrue(resultDeviations != null);
        Assert.assertEquals(Arrays.toString(deviations), Arrays.toString(resultDeviations));
    }

}
