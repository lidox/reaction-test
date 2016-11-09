package com.artursworld.reactiontest.model.persistence.manager;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.model.persistence.EntityDbManager;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.facebook.rebound.ui.Util;

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
    public void insertReactionGameByOperationIssueNameAsync(final String creationDateId, final String operationIssueName, final String gameType, final String testType) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                ContentValues values = new ContentValues();
                values.put(DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE, creationDateId);
                values.put(DBContracts.ReactionGame.COLUMN_NAME_UPDATE_DATE, UtilsRG.dateFormat.format(new Date()));
                values.put(DBContracts.ReactionGame.COLUMN_NAME_AVERAGE_REACTION_TIME, -1);//TODO: test -10
                values.put(DBContracts.ReactionGame.COLUMN_NAME_DURATION, -1);
                values.put(DBContracts.ReactionGame.COLUMN_NAME_INVALID_TRIAL_COUNT, -1);
                values.put(DBContracts.ReactionGame.COLUMN_NAME_GAME_TYPE, gameType);
                values.put(DBContracts.ReactionGame.COLUMN_NAME_REACTIONTEST_TYPE, testType);
                values.put(DBContracts.ReactionGame.COLUMN_NAME_OPERATION_ISSUE_NAME, operationIssueName);

                try {
                    database.insertOrThrow(DBContracts.ReactionGame.TABLE_NAME, null, values);
                    UtilsRG.info("new reaction game created successfully for operationIssue: " + operationIssueName);
                } catch (Exception e) {
                    UtilsRG.error("Could not insert reactionGame into db for operationIssue(" + operationIssueName + ")" + e.getLocalizedMessage());
                }
                return null;
            }
        }.execute();
    }

    /*
    * Updates average reaction time for a specific reaction game
    */
    public void updateAverageReactionTimeById(String creationDateId, double averageReactionTime) {
        ContentValues valuesToUpdate = new ContentValues();
        valuesToUpdate.put(DBContracts.ReactionGame.COLUMN_NAME_AVERAGE_REACTION_TIME, averageReactionTime);
        try {
            database.update(DBContracts.ReactionGame.TABLE_NAME, valuesToUpdate, DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE + "= ?", new String[]{creationDateId});
            UtilsRG.info("Updated averageReactionTime(" + averageReactionTime + ") for reactionGame with creatioDate[Id](" + creationDateId + ")");
        } catch (Exception e) {
            UtilsRG.error("Exception! Could not update new average ReactionTime! " + e.getLocalizedMessage());
        }
    }

    public void updateInValidTrialCountById(String creationDateId, Integer inValidTrialCount) {
        ContentValues valuesToUpdate = new ContentValues();
        valuesToUpdate.put(DBContracts.ReactionGame.COLUMN_NAME_INVALID_TRIAL_COUNT, inValidTrialCount);
        try {
            database.update(DBContracts.ReactionGame.TABLE_NAME, valuesToUpdate, DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE + "= ?", new String[]{creationDateId});
            UtilsRG.info("Updated inValidTrialCount(" + inValidTrialCount + ") for reactionGame with creatioDate[Id](" + creationDateId + ")");
        } catch (Exception e) {
            UtilsRG.error("Exception! Could not update new in-valid tria count! " + e.getLocalizedMessage());
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
            if (selectedOperationIssue != null) {
                //TODO: use prepared statement
                WHERE_CLAUSE = DBContracts.ReactionGame.COLUMN_NAME_OPERATION_ISSUE_NAME + " like '" + selectedOperationIssue + "' ";
                WHERE_CLAUSE += "AND " + DBContracts.ReactionGame.COLUMN_NAME_GAME_TYPE + " like '" + gameType + "'";
                WHERE_CLAUSE += "AND " + DBContracts.ReactionGame.COLUMN_NAME_REACTIONTEST_TYPE + " like '" + testType + "'";
            }

            cursor = database.query(DBContracts.ReactionGame.TABLE_NAME,
                    new String[]{filter + "(" + DBContracts.ReactionGame.COLUMN_NAME_AVERAGE_REACTION_TIME + ")"},
                    WHERE_CLAUSE,
                    null, null, null, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                UtilsRG.info("getReactionGames() OperationIssue[" + selectedOperationIssue + "], GameType[" + gameType + "], TestType[" + testType + "]=" + cursor.getDouble(0));
                return cursor.getDouble(0);
            }
        } catch (Exception e) {
            UtilsRG.info("\"Exception! Could not getReactionGames() OperationIssue[" + selectedOperationIssue + "], GameType[" + gameType + "], TestType[" + testType + "] " + e.getLocalizedMessage());
        } finally {
            try {
                if (cursor != null)
                    cursor.close();
            } catch (Exception e) {
                UtilsRG.error(e.getLocalizedMessage());
            }
        }
        return -1;
    }

    /**
     * Deletes all reaction games where reaction time less than 0
     *
     * @return the number of rows affected if a whereClause is passed in, 0 otherwise. To remove all rows and get a count pass "1" as the whereClause.
     */
    public int deleteNotFinishedGames() {
        String WHERE_REACTION_TIME_LESS_THAN_ZERO = DBContracts.ReactionGame.COLUMN_NAME_AVERAGE_REACTION_TIME + " < 0";
        int columnCount = database.delete(DBContracts.ReactionGame.TABLE_NAME,
                WHERE_REACTION_TIME_LESS_THAN_ZERO, null);
        UtilsRG.info(columnCount + ". has been deleted, because their reaction time was less than zero");
        return columnCount;
    }

    /**
     * Get failure count of user for the game GoNoGo-Game
     *
     * @param operationIssue
     * @param testType
     * @return
     */
    public float getFailureCount(String operationIssue, String testType) {
        Cursor cursor = null;
        try {
            String WHERE_CLAUSE = null;
            if (operationIssue != null) {
                WHERE_CLAUSE = DBContracts.ReactionGame.COLUMN_NAME_OPERATION_ISSUE_NAME + " like '" + operationIssue + "' ";
                WHERE_CLAUSE += "AND " + DBContracts.ReactionGame.COLUMN_NAME_REACTIONTEST_TYPE + " like '" + testType + "'";
            }

            String filter = "SUM";
            cursor = database.query(DBContracts.ReactionGame.TABLE_NAME,
                    new String[]{filter + "(" + DBContracts.ReactionGame.COLUMN_NAME_INVALID_TRIAL_COUNT + ")"},
                    WHERE_CLAUSE,
                    null, null, null, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                UtilsRG.info("getFailureCount() OperationIssue[" + operationIssue + "], TestType[" + testType + "]=" + cursor.getFloat(0));
                return cursor.getFloat(0);
            }
        } catch (Exception e) {
            UtilsRG.info("\"Exception! Could not getReactionGames() OperationIssue[" + operationIssue + "], TestType[" + testType + "] " + e.getLocalizedMessage());
        } finally {
            try {
                if (cursor != null)
                    cursor.close();
            } catch (Exception e) {
                UtilsRG.error(e.getLocalizedMessage());
            }
        }

        return -1;
    }

    public List<ReactionGame> getReactionGameList(String operationIssue, String gameType, String testType, String sortingOrder) {
        List<ReactionGame> games = new ArrayList<ReactionGame>();

        if (operationIssue == null) {
            UtilsRG.error("cannot get ReactionGameList by operationIssue, because operationIssue = null");
            return null;
        }

        String sortOrder = DBContracts.ReactionGame.COLUMN_NAME_UPDATE_DATE + " " + sortingOrder;
        String WHERE_CLAUSE = DBContracts.ReactionGame.COLUMN_NAME_OPERATION_ISSUE_NAME + " like '" + operationIssue + "' ";
        WHERE_CLAUSE += "AND " + DBContracts.ReactionGame.COLUMN_NAME_GAME_TYPE + " like '" + gameType + "' ";
        WHERE_CLAUSE += "AND " + DBContracts.ReactionGame.COLUMN_NAME_REACTIONTEST_TYPE + " like '" + testType + "' ";
        Cursor cursor = database.query(DBContracts.ReactionGame.TABLE_NAME,
                new String[]{
                        DBContracts.ReactionGame.COLUMN_NAME_AVERAGE_REACTION_TIME,
                        DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE,
                        DBContracts.ReactionGame.COLUMN_NAME_UPDATE_DATE,
                        DBContracts.ReactionGame.COLUMN_NAME_DURATION,
                }, WHERE_CLAUSE, null, null, null, sortOrder);

        while (cursor.moveToNext()) {
            float averageReactionTime = cursor.getFloat(0);
            Date creationDate = null;
            Date updateDate = null;
            double duration = cursor.getDouble(3);

            try {
                creationDate = (UtilsRG.dateFormat.parse(cursor.getString(1)));
            } catch (Exception e) {
                UtilsRG.info("Could not parse the creation date of the game: " + e.getLocalizedMessage());
            }
            try {
                updateDate = (UtilsRG.dateFormat.parse(cursor.getString(2)));
            } catch (Exception e) {
                UtilsRG.info("Could not parse the update of the game: " + e.getLocalizedMessage());
            }

            ReactionGame game = new ReactionGame(operationIssue, gameType, testType);
            game.setAverageReactionTime(averageReactionTime);
            game.setCreationDate(creationDate);
            game.setUpdateDate(updateDate);
            game.setDuration(duration);

            games.add(game);
        }
        UtilsRG.info(games.size() + ". Games has been found for operationIssue: " + operationIssue + " and testType: " + testType + " and gameType: " + gameType);
        UtilsRG.info(games.toString());

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return games;
    }


    public List<ReactionGame> getAllReactionGameList(String operationIssue, String sortingOrder) {
        List<ReactionGame> games = new ArrayList<ReactionGame>();

        if (operationIssue == null) {
            UtilsRG.error("cannot get ReactionGameList by operationIssue, because operationIssue = null");
            return null;
        }

        String sortOrder = DBContracts.ReactionGame.COLUMN_NAME_UPDATE_DATE + " " + sortingOrder;
        String WHERE_CLAUSE = DBContracts.ReactionGame.COLUMN_NAME_OPERATION_ISSUE_NAME + " like '" + operationIssue + "' ";
        Cursor cursor = database.query(DBContracts.ReactionGame.TABLE_NAME,
                new String[]{
                        DBContracts.ReactionGame.COLUMN_NAME_AVERAGE_REACTION_TIME,
                        DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE,
                        DBContracts.ReactionGame.COLUMN_NAME_UPDATE_DATE,
                        DBContracts.ReactionGame.COLUMN_NAME_DURATION,
                        DBContracts.ReactionGame.COLUMN_NAME_GAME_TYPE,
                        DBContracts.ReactionGame.COLUMN_NAME_REACTIONTEST_TYPE,
                }, WHERE_CLAUSE, null, null, null, sortOrder);

        while (cursor.moveToNext()) {
            float averageReactionTime = cursor.getFloat(0);
            Date creationDate = null;
            Date updateDate = null;
            double duration = cursor.getDouble(3);

            try {
                creationDate = (UtilsRG.dateFormat.parse(cursor.getString(1)));
            } catch (Exception e) {
                UtilsRG.info("Could not parse the creation date of the game: " + e.getLocalizedMessage());
            }
            try {
                updateDate = (UtilsRG.dateFormat.parse(cursor.getString(2)));
            } catch (Exception e) {
                UtilsRG.info("Could not parse the update of the game: " + e.getLocalizedMessage());
            }

            ReactionGame game = new ReactionGame();
            game.setOperationIssueID(operationIssue);
            game.setGameType(Type.getGameType(cursor.getString(4)));
            game.setTestType(Type.getTestType(cursor.getString(5)));
            game.setAverageReactionTime(averageReactionTime);
            game.setCreationDate(creationDate);
            game.setUpdateDate(updateDate);
            game.setDuration(duration);

            games.add(game);
        }
        UtilsRG.info(games.size() + ". Games has been found for operationIssue: " + operationIssue);
        UtilsRG.info(games.toString());

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return games;
    }

    //TODO: not implemented yet
    public int delete(ReactionGame reactionGame) {
        return database.delete(DBContracts.ReactionGame.TABLE_NAME,
                WHERE_ID_EQUALS, new String[]{reactionGame.getCreationDateFormatted()});
    }

    /**
     * Deletes the reaction game by id (creation timeStamp)
     * @param reactionGameId the id (creation timeStamp)
     * @return the number of rows affected if a whereClause is passed in, 0 otherwise. To remove all rows and get a count pass "1" as the whereClause.
     */
    public int deleteById(String reactionGameId) {
        int resultCode = 0;

        // validation
        if (reactionGameId == null) return resultCode;
        if(reactionGameId.trim().equals("")) return resultCode;


        String WHERE_CLAUSE = DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE + " =?";
        //String id = UtilsRG.dateFormat.format(reactionGameId);
        try {
            resultCode = database.delete(
                DBContracts.ReactionGame.TABLE_NAME, WHERE_CLAUSE, new String[]{reactionGameId} );

            UtilsRG.info("Reaction game to delete from database: " + reactionGameId);
        } catch (Exception e) {
            UtilsRG.error("Exception! Could not delete reactiongame from database: " + reactionGameId + " " + e.getLocalizedMessage());
        }
        return resultCode;
    }
}
