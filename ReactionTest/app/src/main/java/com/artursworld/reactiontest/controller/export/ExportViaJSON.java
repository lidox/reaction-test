package com.artursworld.reactiontest.controller.export;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;


import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.artursworld.reactiontest.model.persistence.manager.TrialManager;
import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExportViaJSON implements IExporter  {

    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 4;
    private Activity activity = null;
    public ExportViaJSON(Activity a) {
        this.activity = a;
    }

    @Override
    public void export() {
        final String outputString = getJSONS();
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

    private void export(File directory, String fileName, String jsonString) {
        UtilsRG.info("Exporting user by file name(" + fileName + ")");
        fileName += UtilsRG.audioTimeStamp.format(new Date());
        String dir = directory.getAbsolutePath();
        writeToFile(jsonString, dir, fileName);
        dir = directory.getAbsolutePath() + "/" + fileName + ".json";
        UtilsRG.shareFile(new File(dir), activity, fileName);
    }

    private void writeToFile(String data, String dir, String fileName) {
        try {
            File root = new File(dir);
            File gpxfile = new File(root, fileName+".json");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            UtilsRG.info("write: ");
            UtilsRG.info(data);
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            UtilsRG.error("Exception File write failed: " + e.toString());
        }
    }

    private String getJSONS() {
        UtilsRG.info("exporting to JSON...");
        JSONArray jsonRootArray = new JSONArray();
        try {
            List<MedicalUser> userList = new MedicalUserManager(activity).getAllMedicalUsers();
            for (int k = 0; k < userList.size(); k++) {
                JSONObject jsonUserObj = new JSONObject();
                JSONArray gamesArray = new JSONArray();

                String operationIssue = new OperationIssueManager(activity).getAllOperationIssuesByMedicoId(userList.get(k).getMedicalId()).get(0).getDisplayName();
                List<ReactionGame> gameList = new ReactionGameManager(activity).getAllReactionGameList(operationIssue, "ASC");
                for (int i = 0; i < gameList.size(); i++) {
                    JSONObject gameJSONObj = new JSONObject();
                    ReactionGame game = gameList.get(i);
                    gameJSONObj.put("datetime", game.getCreationDateFormatted());
                    gameJSONObj.put("type", game.getTestType().name());
                    List<Integer> timesList = new TrialManager(activity).getAllReactionTimesList(game.getCreationDateFormatted());
                    gameJSONObj.put("times", timesList);
                    gamesArray.put(i, gameJSONObj);
                }
                jsonUserObj.put("name", userList.get(k).getMedicalId());
                jsonUserObj.put("age", userList.get(k).getAge());
                jsonUserObj.put("gender", userList.get(k).getGender());
                jsonUserObj.put("games", gamesArray);

                jsonRootArray.put(k, jsonUserObj);

            }
            UtilsRG.info(jsonRootArray.toString());
            }catch(Exception e){
                UtilsRG.error("Exception: " + e.getLocalizedMessage());
            }
        return jsonRootArray.toString();
    }
}
