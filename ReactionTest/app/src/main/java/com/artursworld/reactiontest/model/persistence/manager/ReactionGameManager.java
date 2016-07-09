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

public class ReactionGameManager extends EntityDbManager {

    private static final String WHERE_ID_EQUALS = DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE + " =?";

    public ReactionGameManager(Context context) {
        super(context);
    }


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

    public int delete(ReactionGame reactionGame) {
        return database.delete(DBContracts.ReactionGame.TABLE_NAME,
                WHERE_ID_EQUALS, new String[] { reactionGame.getCreationDateFormatted() });
    }


}
