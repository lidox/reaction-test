package com.artursworld.reactiontest.controller.importer;


import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import com.artursworld.reactiontest.controller.util.Global;
import com.artursworld.reactiontest.controller.util.UtilsRG;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertTrue;

public class ImportCSVTest extends InstrumentationTestCase {

    private RenamingDelegatingContext context;
    private ImportCSV importer;
    private String csvFile = "reaction-data-july-2017.csv";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
        importer = new ImportCSV();
    }

    @Test
    public void testReadCSV1() {
        List<String[]> data = importer.readCsv(context, csvFile);
        assertTrue(data != null);
    }

    @Test
    public void testTimeSeriesDataMap1() {
        List<String[]> data = importer.readCsv(context, csvFile);
        Map<String, List<String[]>> timeSeriesDataMap = importer.getTimeSeriesDataMapByUser(data);
        assertTrue(timeSeriesDataMap != null);
    }

    @Test
    public void testTimeSeriesDataMap2() {
        List<String[]> data = importer.readCsv(context, csvFile);
        Map<String, List<String[]>> timeSeriesDataMap = importer.getTimeSeriesDataMapByUser(data);
        Map<String, List<String[]>> filteredTimeSeriesDataMap = importer.getValidTimeSeriesDataMap(timeSeriesDataMap, Global.getTryCountPerGame());
        assertTrue(filteredTimeSeriesDataMap != null);
    }

    @Test
    public void testTimeSeriesDataMap3() {
        Global.setTryCountPerGame(3);
        List<String[]> data = importer.readCsv(context, csvFile);
        Map<String, List<String[]>> timeSeriesDataMap = importer.getTimeSeriesDataMapByUser(data);
        Map<String, List<String[]>> validTimeSeriesDataMap = importer.getValidTimeSeriesDataMap(timeSeriesDataMap, Global.getTryCountPerGame());
        double[] timeSeriesData = importer.getReactionGameMedianArray(validTimeSeriesDataMap, Global.getTryCountPerGame());
        assertTrue((timeSeriesData.length % (Global.getTryCountPerGame() + 1)) == 0);
    }

    @Test
    public void testTimeSeriesDataMap4() {
        Global.setTryCountPerGame(3);
        List<String[]> data = importer.readCsv(context, csvFile);
        Map<String, List<String[]>> timeSeriesDataMap = importer.getTimeSeriesDataMapByUser(data);
        Map<String, List<String[]>> validTimeSeriesDataMap = importer.getValidTimeSeriesDataMap(timeSeriesDataMap, Global.getTryCountPerGame());
        double[] timeSeriesRTData = importer.getReactionGameMedianArray(validTimeSeriesDataMap, Global.getTryCountPerGame());
        double[] timeSeriesPercentageData = importer.getReactionGameMedianPerformancesInPercentage(timeSeriesRTData, Global.getTryCountPerGame());
        assertTrue((timeSeriesPercentageData.length % (Global.getTryCountPerGame())) == 0);
    }

    @Test
    public void testTimeSeriesDataMap5() {
        Global.setReactionTestCountPerOperation(3);
        double[] timeSeriesPercentageData = importer.getReactionTimeDataInPercentageByCSV(csvFile, Global.getReactionTestCountPerOperation());
        assertTrue((timeSeriesPercentageData.length % (Global.getReactionTestCountPerOperation())) == 0);
    }

    @Test
    public void testTimeSeriesDataSorting1() {
        List<String[]> data = importer.readCsv(context, csvFile);

        Collections.sort(data, new Comparator<String[]>()
        {
            @Override
            public int compare(String[] game1, String[] game2)
            {
                return game1[3].compareTo(game2[3]);
            }
        });


        assertTrue(data != null);
    }

}
