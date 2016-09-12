package com.artursworld.reactiontest.controller.export;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.ListView;

import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.InOpEvent;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.Medicament;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.manager.InOpEventManager;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.model.persistence.manager.MedicamentManager;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.opencsv.CSVWriter;

import junit.framework.Test;

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
        StringBuilder r = new StringBuilder();
        String COMMA = ",";
        String EQUALS = ":";
        String MARKS = "\"";
        String BEGIN = "[{";
        String END = "}]";

        MedicalUser user = new MedicalUserManager(activity).getUserByMedicoId(userId);
        r.append("{");
        r.append(MARKS + "medicalId" + MARKS + EQUALS + MARKS + user.getMedicalId() +MARKS + COMMA);
        r.append(MARKS + "operations" + MARKS + EQUALS + BEGIN); // operations
        List<OperationIssue> operationIssueList = new OperationIssueManager(activity).getAllOperationIssuesByMedicoId(userId);
        if (operationIssueList != null) {
            for (int j = 0; j < operationIssueList.size() ; j++) {
                r.append(MARKS + "medicaments" + MARKS + EQUALS + BEGIN); // medicaments
                String operationIssue = operationIssueList.get(j).getDisplayName();
                List<Medicament> medicamentList = new MedicamentManager(activity).getMedicamentList(operationIssue, "ASC");
                if(medicamentList != null){
                    for(int i = 0; i < medicamentList.size() ; i++){
                        r.append(medicamentList.get(i).toJSON());
                        if(i != medicamentList.size() -1){
                            r.append(COMMA);
                        }
                    }
                }
                r.append(END + COMMA); // medicaments



                //List<InOpEvent> eventList = new InOpEventManager(activity).getInOpEventListByOperationIssue(operationIssue.getDisplayName(), "ASC");
                //double averagePreOperationValue = new ReactionGameManager(activity).getFilteredReactionGames(operationIssue.getDisplayName(), Type.getGameType(Type.GameTypes.GoGame), Type.getTestType(Type.TestTypes.PreOperation), "AVG");
                //double averageInOperationValue = new ReactionGameManager(activity).getFilteredReactionGames(operationIssue.getDisplayName(), Type.getGameType(Type.GameTypes.GoGame), Type.getTestType(Type.TestTypes.InOperation), "AVG");
                //double averagePostOperationValue = new ReactionGameManager(activity).getFilteredReactionGames(operationIssue.getDisplayName(), Type.getGameType(Type.GameTypes.GoGame), Type.getTestType(Type.TestTypes.PostOperation), "AVG");
                if(j != operationIssueList.size() -1){
                    r.append(COMMA);
                }
            }

        }
        r.append(END); // operations
        r.append("}");
        String[] userAsString = { r.toString() };
        //String[] userAsString = {user.getMedicalId(), user.getGender() + "", user.getBirthDateAsString(), user.getAge() + "", user.getBmi() + "", operationIssueString};
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
