package com.artursworld.reactiontest.controller.export;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.ListView;

import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;
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

    private static final String SEPARATOR = ", ";
    private Activity activity = null;
    private String userId = null;
    private List<String[]> dataToExport = null;

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

            // write to file
            writer.writeAll(dataToExport);
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
    private void initUserContentAsList() {
        dataToExport = new ArrayList<String[]>();
        //TODO: "123", "2015-11-17", "2015-11-18", [{id: 345, date: ...}, {id: 789, date: ...}]
        MedicalUser user = new MedicalUserManager(activity).getUserByMedicoId(userId);

        List<OperationIssue> operationIssueList = new OperationIssueManager(activity).getAllOperationIssuesByMedicoId(userId);
        String operationIssueString = "";
        if (operationIssueList != null) {
            if (operationIssueList.size() > 0) {
                operationIssueString = "[";
                for (OperationIssue issue : operationIssueList) {
                    String op = "{" + issue.getDisplayName() + SEPARATOR + issue.getCreationDate();
                    op += "}";
                    operationIssueString += op;
                }
                operationIssueString += "]";
            }
        }

        String[] userAsString = {user.getMedicalId(), user.getGender() + "", user.getBirthDateAsString(), user.getAge() + "", user.getBmi() + "", operationIssueString};
        dataToExport.add(userAsString);
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
                initUserContentAsList();
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
