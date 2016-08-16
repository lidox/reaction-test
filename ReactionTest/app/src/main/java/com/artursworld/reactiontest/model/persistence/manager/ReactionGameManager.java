package com.artursworld.reactiontest.model.persistence.manager;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.artursworld.reactiontest.model.persistence.EntityDbManager;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.controller.util.UtilsRG;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
* Manages reaction game database issue
*/
public class ReactionGameManager extends EntityDbManager {

    private static final String WHERE_ID_EQUALS = DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE + " =?";

    public ReactionGameManager(Context context) {
        super(context);
    }

    /*
    * Insert a reaction game for specific operation async
    */
    public void insertReactionGameByOperationIssueNameAsync(final String ceationDateId, final String operationIssueName, final String gameType, final String testType){
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                ContentValues values = new ContentValues();
                values.put(DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE, ceationDateId);
                values.put(DBContracts.ReactionGame.COLUMN_NAME_UPDATE_DATE, UtilsRG.dateFormat.format(new Date()));
                values.put(DBContracts.ReactionGame.COLUMN_NAME_AVERAGE_REACTION_TIME, -1);
                values.put(DBContracts.ReactionGame.COLUMN_NAME_DURATION, -1);
                values.put(DBContracts.ReactionGame.COLUMN_NAME_GAME_TYPE, gameType);
                values.put(DBContracts.ReactionGame.COLUMN_NAME_REACTIONTEST_TYPE, testType);
                values.put(DBContracts.ReactionGame.COLUMN_NAME_OPERATION_ISSUE_NAME, operationIssueName);

                try {
                    database.insertOrThrow(DBContracts.ReactionGame.TABLE_NAME, null, values);
                    UtilsRG.info("new reaction game created successfully for operationIssue: " +operationIssueName);
                }
                catch (Exception e){
                    UtilsRG.error("Could not insert reactionGame into db for operationIssue("+operationIssueName +")" + e.getLocalizedMessage());
                }
                return null;
            }
        }.execute();
    }

    /*
    * Updates average reaction time for a specific reaction game
    */
    public void updateAverageReactionTimeById(String creationDateId, double averageReactionTime){
        ContentValues valuesToUpdate = new ContentValues();
        valuesToUpdate.put(DBContracts.ReactionGame.COLUMN_NAME_AVERAGE_REACTION_TIME, averageReactionTime);
        try {
            database.update(DBContracts.ReactionGame.TABLE_NAME, valuesToUpdate, DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE + "= ?", new String[] {creationDateId});
            UtilsRG.info("Updated averageReactionTime("+averageReactionTime+") for reactionGame with creatioDate[Id]("+creationDateId+")");
        }
        catch (Exception e){
            UtilsRG.error("Exception! Could not update new average ReactionTime! " + e.getLocalizedMessage());
        }
    }

    /*
    * get reaction game by the filters: game type, test type, operation issue and db function like AVG
    */
    public double getFilteredReactionGames(String selectedOperationIssue, String gameType, String testType, String filter) {
        List<ReactionGame> reactionGameList = new ArrayList<ReactionGame>();
        Cursor cursor = null;
        try {
            String WHERE_CLAUSE = null;
            if(selectedOperationIssue != null){
                //TODO: use prepared statement
                WHERE_CLAUSE = DBContracts.ReactionGame.COLUMN_NAME_OPERATION_ISSUE_NAME + " like '" + selectedOperationIssue + "' ";
                WHERE_CLAUSE += "AND "+DBContracts.ReactionGame.COLUMN_NAME_GAME_TYPE + " like '" + gameType + "'" ;
                WHERE_CLAUSE += "AND "+DBContracts.ReactionGame.COLUMN_NAME_REACTIONTEST_TYPE + " like '" + testType + "'" ;
            }

            cursor = database.query(DBContracts.ReactionGame.TABLE_NAME,
                    new String[]{filter+"(" + DBContracts.ReactionGame.COLUMN_NAME_AVERAGE_REACTION_TIME + ")"},
                    WHERE_CLAUSE,
                    null, null, null, null);

            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                UtilsRG.info("getReactionGames() OperationIssue[" + selectedOperationIssue + "], GameType["+gameType+"], TestType["+testType+"]="+cursor.getDouble(0));
                return  cursor.getDouble(0);
            }
        } catch (Exception e) {
            UtilsRG.info("\"Exception! Could not getReactionGames() OperationIssue[" + selectedOperationIssue + "], GameType["+gameType+"], TestType["+testType+"] " +e.getLocalizedMessage());
        }
        finally {
            try {
                if (cursor != null)
                    cursor.close();
            }
            catch (Exception e){
                UtilsRG.error(e.getLocalizedMessage());
            }
        }
        return -1;
    }

    //TODO: not implemented and used yet
    public List<ReactionGame> getReactionGamesByMedicalUser(MedicalUser medicalUser) {
        List<ReactionGame> reactionGameList = new ArrayList<ReactionGame>();
        /*try {
            Cursor cursor = database.query(DBContracts.ReactionGame.TABLE_NAME,
                    new String[] { DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE,
                            DBContracts.ReactionGame.COLUMN_NAME_DURATION,

                            DBContracts.ReactionGame.COLUMN_NAME_HITS,
                            DBContracts.ReactionGame.COLUMN_NAME_MISSES,
                            DBContracts.ReactionGame.COLUMN_NAME_REACTION_TYPE,
                            DBContracts.ReactionGame.COLUMN_NAME_MEDICAL_ID

                    },

                    DBContracts.ReactionGame.COLUMN_NAME_MEDICAL_ID + "=\"" +medicalUser.getMedicalId()+"\"",
                    null, null, null, null);

            while (cursor.moveToNext()) {
                ReactionGame reactionGame = new ReactionGame();
                try {
                    reactionGame.setCreationDate(UtilsRG.dateFormat.parse(cursor.getString(0)));
                } catch (Exception e) {
                    reactionGame.setCreationDate(null);
                }
                reactionGame.setDuration(cursor.getDouble(1));
                reactionGame.setHits(cursor.getInt(2));
                reactionGame.setMisses(cursor.getInt(3));
                reactionGame.setReationType(cursor.getString(4));
                reactionGame.setMedicalUser(medicalUser);
                reactionGameList.add(reactionGame);
            }
        }
        catch (Exception e){
            UtilsRG.log.error("Failure at method getReactionGamesByMedicalUser(meduser: " + medicalUser.getMedicalId());
            UtilsRG.log.error(e.getLocalizedMessage());
        }*/
        return reactionGameList;
    }

    // TODO: Not needed and implemented yet
    public List<ReactionGame> getAllReactionGames() {
        List<ReactionGame> reactionGameList = new ArrayList<ReactionGame>();
        /*try {
            Cursor cursor = database.query(DBContracts.ReactionGame.TABLE_NAME,
                    new String[] { DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE,
                            DBContracts.ReactionGame.COLUMN_NAME_DURATION,
                            DBContracts.ReactionGame.COLUMN_NAME_HITS,
                            DBContracts.ReactionGame.COLUMN_NAME_MISSES,
                            DBContracts.ReactionGame.COLUMN_NAME_REACTION_TYPE,
                            DBContracts.ReactionGame.COLUMN_NAME_MEDICAL_ID
                    },
                    null, null, null, null, null);

            while (cursor.moveToNext()) {
                ReactionGame reactionGame = new ReactionGame();
                reactionGame.setCreationDate(UtilsRG.dateFormat.parse(cursor.getString(0)));
                reactionGame.setCreationDate(null);
                reactionGame.setDuration(cursor.getDouble(1));
                reactionGame.setHits(cursor.getInt(2));
                reactionGame.setMisses(cursor.getInt(3));
                reactionGame.setReationType(cursor.getString(4));
                //reactionGame.setMedicalUser(cursor.getString(5));
                reactionGameList.add(reactionGame);
            }
        }
        catch (Exception e){
            UtilsRG.log.error(e.getLocalizedMessage());
        }*/
        return reactionGameList;
    }

    //TODO: not implemented yet
    public int delete(ReactionGame reactionGame) {
        return database.delete(DBContracts.ReactionGame.TABLE_NAME,
                WHERE_ID_EQUALS, new String[] { reactionGame.getCreationDateFormatted() });
    }

}
