package com.artursworld.reactiontest.controller.export;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.InOpEvent;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.Medicament;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.persistence.manager.InOpEventManager;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.model.persistence.manager.MedicamentManager;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
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

    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 3;

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

        } catch (IOException e) {
            UtilsRG.error("Could not export file:" + fileName + "! " + e.getLocalizedMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    UtilsRG.error(e.getLocalizedMessage());
                }
            }

        }


        UtilsRG.shareFile(new File(dir), activity, userId);
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
        r.append(MARKS + "medicalId" + MARKS + EQUALS + MARKS + user.getMedicalId() + MARKS + COMMA);
        r.append(MARKS + "operations" + MARKS + EQUALS + BEGIN);
        List<OperationIssue> operationIssueList = new OperationIssueManager(activity).getAllOperationIssuesByMedicoId(userId);
        if (operationIssueList != null) {
            for (int j = 0; j < operationIssueList.size(); j++) {

                if (operationIssueList.size() > 0) {
                    r.append(MARKS + "medicaments" + MARKS + EQUALS + BEGIN); // medicaments
                    String operationIssue = operationIssueList.get(j).getDisplayName();
                    List<Medicament> medicamentList = new MedicamentManager(activity).getMedicamentList(operationIssue, "ASC");
                    if (medicamentList != null) {
                        for (int i = 0; i < medicamentList.size(); i++) {
                            r.append(medicamentList.get(i).toJSON());
                            if (i != medicamentList.size() - 1) {
                                r.append(COMMA);
                            }
                        }
                    }
                    r.append(END + COMMA); // medicaments

                    List<InOpEvent> eventList = new InOpEventManager(activity).getInOpEventListByOperationIssue(operationIssue, "ASC");
                    if (eventList != null && eventList.size() > 0) {
                        r.append(MARKS + "events" + MARKS + EQUALS + BEGIN); // events

                        for (int k = 0; k < eventList.size(); k++) {
                            r.append(eventList.get(k).toJSON());
                            if (k != eventList.size() - 1) {
                                r.append(COMMA);
                            }
                        }
                        r.append(END + COMMA); // events
                    }

                    //games beginning
                    double avgPreOp = new ReactionGameManager(activity).getFilteredReactionGames(operationIssue, Type.getGameType(Type.GameTypes.GoGame), Type.getTestType(Type.TestTypes.PreOperation), "AVG");
                    double avgInOp = new ReactionGameManager(activity).getFilteredReactionGames(operationIssue, Type.getGameType(Type.GameTypes.GoGame), Type.getTestType(Type.TestTypes.InOperation), "AVG");
                    double avgPostOp = new ReactionGameManager(activity).getFilteredReactionGames(operationIssue, Type.getGameType(Type.GameTypes.GoGame), Type.getTestType(Type.TestTypes.PostOperation), "AVG");
                    r.append(MARKS + "games" + MARKS + EQUALS + BEGIN);
                    r.append(MARKS + "game1" + MARKS + EQUALS + BEGIN);
                    r.append(MARKS + "gametype" + MARKS + EQUALS + MARKS + "go-game" + MARKS + COMMA);
                    r.append(MARKS + "testtype" + MARKS + EQUALS + MARKS + "pre-operation" + MARKS + COMMA);
                    r.append(MARKS + "average" + MARKS + EQUALS + MARKS + avgPreOp + MARKS);
                    r.append(END + COMMA);
                    r.append(MARKS + "game2" + MARKS + EQUALS + BEGIN);
                    r.append(MARKS + "gametype" + MARKS + EQUALS + MARKS + "go-game" + MARKS + COMMA);
                    r.append(MARKS + "testtype" + MARKS + EQUALS + MARKS + "in-operation" + MARKS + COMMA);
                    r.append(MARKS + "average" + MARKS + EQUALS + MARKS + avgInOp + MARKS);
                    r.append(END + COMMA);
                    r.append(MARKS + "game3" + MARKS + EQUALS + BEGIN);
                    r.append(MARKS + "gametype" + MARKS + EQUALS + MARKS + "go-game" + MARKS + COMMA);
                    r.append(MARKS + "testtype" + MARKS + EQUALS + MARKS + "post-operation" + MARKS + COMMA);
                    r.append(MARKS + "average" + MARKS + EQUALS + MARKS + avgPostOp + MARKS);
                    r.append(END);
                    r.append(END);
                    // games ending
                }//operationIssue

                if (j != operationIssueList.size() - 1) {
                    r.append(COMMA);
                }
            }

        }
        r.append(END); // operations
        r.append("}");
        String[] userAsString = {r.toString()};
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
