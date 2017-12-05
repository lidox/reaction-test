package com.artursworld.reactiontest.controller.export;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.artursworld.reactiontest.model.persistence.manager.TrialManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class ExportViaJSON implements IExporter {

    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 4;
    private Activity activity = null;
    private Context context = null;

    public ExportViaJSON(Activity a) {
        this.activity = a;
    }

    public ExportViaJSON(Context c) {
        this.context = c;
    }

    /**
     * Export database as JSON
     */
    @Override
    public void export() {
        Context c = null;
        if (activity != null)
            c = activity.getApplicationContext();
        else
            c = this.context;

        final String outputString = getJSONString(c);
        new AsyncTask<Void, Void, File>() {

            @Override
            protected File doInBackground(Void... params) {
                return android.os.Environment.getExternalStorageDirectory();
            }

            @Override
            protected void onPostExecute(File directory) {
                export(directory, "reaction-data", outputString);
                super.onPostExecute(directory);
            }
        }.execute();
    }

    /**
     * Export database as JSON, saved file and open Share File Dialog
     *
     * @param directory  the directory to save the JSON file
     * @param fileName   the JSON's file name
     * @param jsonString the JSON content
     */
    private void export(File directory, String fileName, String jsonString) {
        UtilsRG.info("Exporting user by file name(" + fileName + ")");
        fileName += UtilsRG.audioTimeStamp.format(new Date());
        String dir = directory.getAbsolutePath();
        writeToFile(jsonString, dir, fileName);
        dir = directory.getAbsolutePath() + "/" + fileName + ".json";
        UtilsRG.shareFile(new File(dir), activity, fileName);
    }

    /**
     * Writes a string into a file
     *
     * @param data     the JSON's content
     * @param dir      the directory to save the JSON file
     * @param fileName the JSON's file name
     */
    private void writeToFile(String data, String dir, String fileName) {
        try {
            File root = new File(dir);
            File gpxfile = new File(root, fileName + ".json");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            UtilsRG.info("write: ");
            UtilsRG.info(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            UtilsRG.error("Exception File write failed: " + e.toString());
        }
    }

    /**
     * @return the full JSON string containing all user data
     */
    public static String getJSONString(Context context) {
        UtilsRG.info("exporting to JSON...");
        JSONArray jsonRootArray = new JSONArray();
        try {
            List<MedicalUser> userList = new MedicalUserManager(context).getAllMedicalUsers();
            for (int k = 0; k < userList.size(); k++) {
                JSONObject jsonUserObj = new JSONObject();
                JSONArray gamesArray = new JSONArray();

                String medicalId = userList.get(k).getMedicalId();
                List<OperationIssue> operationList = new OperationIssueManager(context).getAllOperationIssuesByMedicoId(medicalId);

                for (int q = 0; q < operationList.size(); q++) {
                    String operationIssue = operationList.get(q).getDisplayName();

                    List<ReactionGame> gameList = new ReactionGameManager(context).getAllReactionGameList(operationIssue, "ASC");
                    for (int i = 0; i < gameList.size(); i++) {
                        JSONObject gameJSONObj = new JSONObject();
                        ReactionGame game = gameList.get(i);
                        gameJSONObj.put("datetime", game.getCreationDateFormatted());
                        gameJSONObj.put("type", game.getTestType().name());
                        gameJSONObj.put(ExportKey.PATIENTS_AWAKE_ALERTNESS, game.getPatientsAlertnessFactor());
                        List<Integer> timesList = new TrialManager(context).getAllReactionTimesList(game.getCreationDateFormatted());
                        gameJSONObj.put("times", new JSONArray(timesList));
                        gamesArray.put(i, gameJSONObj);
                    }
                }
                jsonUserObj.put("name", userList.get(k).getMedicalId());
                jsonUserObj.put("age", userList.get(k).getAge());
                jsonUserObj.put("gender", userList.get(k).getGender());
                jsonUserObj.put("games", gamesArray);

                jsonRootArray.put(k, jsonUserObj);

            }
            UtilsRG.info(jsonRootArray.toString());
        } catch (Exception e) {
            UtilsRG.error("Exception: " + e.getLocalizedMessage());
        }
        return jsonRootArray.toString();
    }
}
