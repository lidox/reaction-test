package com.artursworld.reactiontest.controller.export;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExportJson2CSV implements IExporter {

    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 7;
    private Activity activity = null;

    public ExportJson2CSV(Activity a) {
        this.activity = a;
    }

    /**
     * Exports and shares all records as CSV
     */
    @Override
    public void export() {
        Context context = null;

        if (activity != null)
            context = activity.getApplicationContext();

        final String outputString = ExportViaJSON.getJSONString(context);

        new AsyncTask<Void, Void, File>() {

            @Override
            protected File doInBackground(Void... params) {
                return android.os.Environment.getExternalStorageDirectory();
            }

            @Override
            protected void onPostExecute(File directory) {
                try {
                    List<String[]> exportableCSVList = getExportableCSVList(outputString);
                    String fileNameToCreate = "reaction-data" + new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date());
                    export(directory, fileNameToCreate, exportableCSVList);
                } catch (Exception e) {
                    UtilsRG.error("Cannot parse data: " + e.getLocalizedMessage());
                }

                super.onPostExecute(directory);
            }
        }.execute();
    }

    /**
     * Exports user content to device and opens share chooser
     *
     * @param directory the directory where to save file
     * @param fileName  the name of the new file to export
     */
    private void export(File directory, String fileName, List<String[]> dataToExport) {
        String dir = exportAsCSV(directory, fileName, dataToExport);
        UtilsRG.shareFile(new File(dir), activity, "all users");
    }


    /**
     * Exports a new file as CSV
     *
     * @param directory    the directory to export in
     * @param fileName     the name of the file to export
     * @param dataToExport the list to export containing all data
     * @return the absolute directory path of the file
     */
    @NonNull
    public static String exportAsCSV(File directory, String fileName, List<String[]> dataToExport) {
        UtilsRG.info("Exporting all users");
        CSVWriter writer = null;
        String dir = null;
        try {
            // init writer
            dir = directory.getAbsolutePath() + "/" + fileName + ".csv";
            writer = new CSVWriter(new FileWriter(dir));

            // write to file
            writer.writeAll(dataToExport);
            writer.close();
        } catch (IOException e) {
            UtilsRG.error("Could not export file:" + fileName + "! " + e.getLocalizedMessage());
        }
        return dir;
    }

    /**
     * Get the content of a File by file name
     *
     * @param filename the name of the file
     * @return the content of the file as String
     */
    public String getJSONStringByFileName(String filename) {
        String content = null;
        String charsetName = "UTF-8";

        try {
            InputStream is = openFile(filename);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            content = new String(buffer, charsetName);


        } catch (Exception ex) {
            ex.getLocalizedMessage();
        }
        return content;
    }

    /**
     * Opens a file which is placed in src/test/resources
     *
     * @param filename the name of the file
     * @return the input stream
     * @throws IOException throws exception in case the file cannot be opened
     */
    private InputStream openFile(String filename) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream(filename);
    }

    /**
     * Creates an exportable List containing following records.
     * E.g: name, age, gender, type, datetime, time1, time2, time3, time4, time5
     *
     * @param jsonString The JSON-String to parse
     * @return the exportable List
     * @throws JSONException throws Exception in case the Input-JSON cannot be parsed
     */
    public static List<String[]> getExportableCSVList(String jsonString) throws JSONException {
        List<String[]> csvToExport = new ArrayList<>();

        JSONArray inputJsonArray = new JSONArray(jsonString);

        // for each user
        for (int i = 0; i < inputJsonArray.length(); i++) {
            JSONObject userObject = inputJsonArray.getJSONObject(i);
            JSONArray reactionTestArrayPerUser = userObject.getJSONArray("games");

            // for each reaction test
            for (int j = 0; j < reactionTestArrayPerUser.length(); j++) {
                JSONObject reactionTest = reactionTestArrayPerUser.getJSONObject(j);
                JSONArray reactionTimes = reactionTest.getJSONArray("times");

                // single items
                String name = userObject.getString("name").toString();
                String age = new StringBuilder(userObject.getInt("age") + "").toString();
                String gender = userObject.getString("gender").toString();
                String dateTime = reactionTest.getString("datetime");
                String operationType = reactionTest.getString("type");
                String patientsAwakeAlertness = reactionTest.getString("patients-awake-alertness");
                String brainTemperature = String.valueOf(reactionTest.getDouble("temperature"));

                // name, age, gender, type, datetime, time1, time2, time3, time4, time5
                int initialValueCount = 7;
                String[] csvRecordRow = new String[initialValueCount + reactionTimes.length()];

                // set initial row values
                csvRecordRow[0] = name;
                csvRecordRow[1] = age;
                csvRecordRow[2] = gender;
                csvRecordRow[3] = dateTime;
                csvRecordRow[4] = operationType;
                csvRecordRow[5] = patientsAwakeAlertness;
                csvRecordRow[6] = brainTemperature;

                // for each reaction time within a test
                for (int k = 0; k < reactionTimes.length(); k++) {
                    int rawReactionTime = reactionTimes.getInt(k);
                    String reactionTime = new StringBuilder(rawReactionTime + "").toString();
                    reactionTime = getCorrectedReactionTime(rawReactionTime, reactionTime);
                    csvRecordRow[k + initialValueCount] = reactionTime;
                }
                csvToExport.add(csvRecordRow);
            }

        }
        return csvToExport;
    }

    /**
     * Corrects high reaction times which appeared through bad data export and parsing
     *
     * @param rawReactionTime the reaction time as int
     * @param reactionTime    the reaction time as String
     * @return If reaction time is higher than 100.000 ms it will be corrected.
     * Otherwise does nothing.
     */
    private static String getCorrectedReactionTime(int rawReactionTime, String reactionTime) {
        if (rawReactionTime > 100000000) {
            Double value = rawReactionTime / 1000000.;
            reactionTime = new StringBuilder(value.intValue() + "").toString();
        } else if (rawReactionTime > 100000) {
            Double value = rawReactionTime / 1000.;
            reactionTime = new StringBuilder(value.intValue() + "").toString();
        }
        return reactionTime;
    }
}