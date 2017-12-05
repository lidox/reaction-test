package com.artursworld.reactiontest.controller.export;


import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExportJson2CSVTest {

    private static final String FILENAME = "reaction-data.json";

    @Test
    public void testGetJSONStringByFileName1() {
        ExportJson2CSV export = new ExportJson2CSV(null);
        String jsonString = export.getJSONStringByFileName(FILENAME);
        Assert.assertTrue(jsonString != null);
    }

    @Test
    public void testGetExportableCSV1() throws JSONException {
        ExportJson2CSV exporter = new ExportJson2CSV(null);
        String jsonString = exporter.getJSONStringByFileName(FILENAME);
        List<String[]> dataToExport = exporter.getExportableCSVList(jsonString);
        Assert.assertTrue(dataToExport.size() > 0);
    }

    @Test
    public void testExportData1() throws JSONException, IOException {
        ExportJson2CSV exporter = new ExportJson2CSV(null);
        String jsonString = exporter.getJSONStringByFileName(FILENAME);
        List<String[]> dataToExport = exporter.getExportableCSVList(jsonString);
        File dir = File.createTempFile("test-data", ".csv");
        exporter.exportAsCSV(new File(dir.getParent()), dir.getName(), dataToExport);
        Assert.assertTrue(dataToExport.size() > 0);
    }

}
