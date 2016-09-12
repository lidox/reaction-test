package com.artursworld.reactiontest.controller.export;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Exports a user and it's content via CSV file
 */
public class ExportViaCSV implements IExporter {

    Activity activity = null;
    private String userId = null;

    public ExportViaCSV(Activity a, String userId) {
        this.activity = a;
        this.userId = userId;
    }

    @Override
    public void export() {
        loadDirectoryAndExport(userId);
    }

    /**
     * Exports user content to device and opens share chooser
     *
     * @param userId    the user id to export
     * @param directory the directory where to save file
     * @param fileName  the name of the new file to export
     */
    private void export(String userId, File directory, String fileName) {
        UtilsRG.info("Exporting user by Id(" + userId + ")");
        CSVWriter writer = null;
        String dir = null;
        try {
            // init writer
            dir = directory.getAbsolutePath() + "/" + fileName + ".csv";
            writer = new CSVWriter(new FileWriter(dir));

            List<String[]> data = getUserContentAsList();

            // write to file
            writer.writeAll(data);
            writer.close();
        } catch (IOException e) {
            UtilsRG.error("Could not export file:" + fileName + "! " + e.getLocalizedMessage());
        }

        UtilsRG.shareFile(new File(dir), activity);
    }

    /**
     * Get user content by database
     *
     * @return a list of all content for specified user
     */
    @NonNull
    private List<String[]> getUserContentAsList() {
        //TODO: hier go!
        List<String[]> data = new ArrayList<String[]>();
        data.add(new String[]{"India", "New Delhi"});
        data.add(new String[]{"United States", "Washington D.C"});
        data.add(new String[]{"Germany", "Berlin"});
        return data;
    }

    /**
     * Loads directory async and than exports user via CSV
     *
     * @param userId the user to export
     */
    private void loadDirectoryAndExport(final String userId) {
        new AsyncTask<Void, Void, File>() {

            @Override
            protected File doInBackground(Void... params) {
                return android.os.Environment.getExternalStorageDirectory();
            }

            @Override
            protected void onPostExecute(File directory) {
                export(userId, directory, "data" + userId);
                super.onPostExecute(directory);
            }
        }.execute();
    }
}
