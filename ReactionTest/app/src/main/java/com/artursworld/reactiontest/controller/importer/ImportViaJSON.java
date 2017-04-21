package com.artursworld.reactiontest.controller.importer;


import android.content.Context;
import android.os.AsyncTask;

import com.artursworld.reactiontest.controller.helper.Gender;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.artursworld.reactiontest.model.persistence.manager.TrialManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImportViaJSON {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static final String NAME = "name";
    private static final String GENDER = "gender";
    private static final String GAMES = "games";
    private static final String TIMES = "times";
    private static final String TYPE = "type";
    private static final String DATETIME = "datetime";
    private static final String AGE = "age";

    // TODO: maybe this need to be placed in startmenu
    public void importDataToDBbyJSON(JSONArray jObject,final Context context) {
        try {
            UtilsRG.info(ImportViaJSON.class.getSimpleName()+" importDataToDBbyJSON() started");
            final List<JsonUser> userIdList = getUserIdList(jObject);
            insertUsersToDBAsync(context, userIdList);
        }catch (Exception e){
            UtilsRG.error(e.getLocalizedMessage());
        }
    }

    /**
     * Parses a json array to a list of users containing its reaction test by json string
     * @param jArray the json to be parsed
     * @return a list of users containing its reaction test by json string
     */
    public List<JsonUser>  getUserIdList(JSONArray jArray)  {
        List<JsonUser> userIdList = new ArrayList();
        try {

        for (int i = 0; i < jArray.length(); i++) {
            JSONObject row = jArray.getJSONObject(i);
            JsonUser user = new JsonUser();
            user.setAge(row.getInt(AGE));
            user.setGender(row.getString(GENDER));
            user.setName(row.getString(NAME));

            JSONArray gamesArray = row.getJSONArray(GAMES);
            for (int j = 0; j < gamesArray.length(); j++) {
                JSONObject gRow = gamesArray.getJSONObject(j); // j and not i! quit important
                JsonUser.JsonGame game =  user.new JsonGame();
                game.setDatetime(dateFormat.parse(gRow.getString(DATETIME)));
                game.setType(gRow.getString(TYPE));
                JSONArray reactionTimeArray = gRow.getJSONArray(TIMES);
                for (int k = 0; k < reactionTimeArray.length(); k++) {
                    Double time = Double.parseDouble(reactionTimeArray.get(k).toString());
                    game.addTime(time);
                }
                user.addGame(game);
            }
            userIdList.add(user);
            UtilsRG.info("row: " +row);
        }

        }catch (Exception e){
            UtilsRG.error("The input data could not be parsed.");
            UtilsRG.error(e.getLocalizedMessage());
        }
        return userIdList;
    }

    public void insertUsersToDBAsync(final Context activity, final List<JsonUser> userIdList) {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                insertUsersToDB(activity, userIdList);
                return null;
            }
        }.execute();
    }

    public void insertUsersToDB(Context activity, List<JsonUser> userIdList) {
        MedicalUserManager userDB = new MedicalUserManager(activity);
        OperationIssueManager issueDB = new OperationIssueManager(activity);
        ReactionGameManager gameDB = new ReactionGameManager(activity);
        TrialManager rtDB = new TrialManager(activity);

        for(JsonUser user: userIdList){
            MedicalUser medUser = new MedicalUser();
            medUser.setGender(Gender.findByName(user.getGender()));
            medUser.setMedicalId(user.getName());
            medUser.setBmi(0);
            medUser.setBirthDate(user.getBirthDate());
            medUser.setMarkedAsDeleted(false);
            userDB.insert(medUser);

            MedicalUser insertedUser = userDB.getUserByMedicoId(medUser.getMedicalId());
            boolean isCreated = insertedUser.getMedicalId().equals(medUser.getMedicalId());
            if(isCreated){
                String operationName = "generated" + new Date() + ( (int) (Math.random() * 100000000));
                issueDB.insertOperationIssueByMedId(user.getName(), operationName);
                OperationIssue resultOpIssue = issueDB.getAllOperationIssuesByMedicoId(user.getName()).get(0);
                boolean hasCreatedOperationIssue = operationName.equals(resultOpIssue.getDisplayName());
                if(hasCreatedOperationIssue){
                    for(JsonUser.JsonGame game: user.getGAMES()){
                        String creationDateId = UtilsRG.dateFormat.format(game.getDatetime());
                        gameDB.insertReactionGameByOperationIssueName(creationDateId, "GoGame", game.getType(), resultOpIssue.getDisplayName());
                        double sum = 0;
                        for (Double time: game.getTimes()){
                            rtDB.insertTrialtoReactionGameAsync(creationDateId, true, time);
                            sum += time;
                        }
                        gameDB.updateAverageReactionTimeById(creationDateId, sum/game.getTimes().size());
                    }
                    //List<ReactionGame> reactionGameList = new ReactionGameManager(activity).getReactionGameList(operationName, Type.GameTypes.GoGame.name(), Type.TestTypes.InOperation.name(), "ASC");
                    //reactionGameList.size();
                }
            }
        }
    }
}
