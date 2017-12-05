package com.artursworld.reactiontest.controller.util;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ListsTest {

    @Test
    public void test1() {
        double[] d1 = {422.};
        double[] d2 = {232.};
        double[] result = Lists.combine(d1, d2);
        assertTrue(result.length == 2);
    }

    @Test
    public void test2() {
        List<Double> list = new ArrayList<>();
        list.add(234.);
        double[] result = Lists.getArray(list);
        assertTrue(result.length == 1);
    }
}
