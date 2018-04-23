package com.artursworld.reactiontest.model.persistence.manager;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.importer.ImportCSV;
import com.artursworld.reactiontest.controller.util.App;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.EntityDbManager;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Manages reaction game database issue
 */
public class ReactionGameManager extends EntityDbManager {

    private static final String WHERE_ID_EQUALS = DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE + " =?";
    private String tableName = DBContracts.ReactionGame.TABLE_NAME;


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
                insertReactionGameByOperationIssueName(creationDateId, gameType, testType, operationIssueName);
                return null;
            }
        }.execute();
    }

    public void insertReactionGameByOperationIssueName(String creationDateId, String gameType, String testType, String operationIssueName) {
        ContentValues values = new ContentValues();
        values.put(DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE, creationDateId);
        values.put(DBContracts.ReactionGame.COLUMN_NAME_UPDATE_DATE, creationDateId);//UtilsRG.dateFormat.format(creationDateId)
        values.put(DBContracts.ReactionGame.COLUMN_NAME_AVERAGE_REACTION_TIME, -1);
        values.put(DBContracts.ReactionGame.COLUMN_NAME_DURATION, -1);
        values.put(DBContracts.ReactionGame.COLUMN_NAME_INVALID_TRIAL_COUNT, -1);
        values.put(DBContracts.ReactionGame.COLUMN_NAME_GAME_TYPE, gameType);
        values.put(DBContracts.ReactionGame.COLUMN_NAME_REACTIONTEST_TYPE, testType);
        values.put(DBContracts.ReactionGame.COLUMN_NAME_OPERATION_ISSUE_NAME, operationIssueName);
        values.put(DBContracts.ReactionGame.COLUMN_NAME_BRAIN_TEMPERATURE, 0);

        try {
            database.insertOrThrow(DBContracts.ReactionGame.TABLE_NAME, null, values);
            UtilsRG.info("new reaction game created successfully for operationIssue: " + operationIssueName);
        } catch (Exception e) {
            UtilsRG.error("Could not insert reactionGame into db for operationIssue(" + operationIssueName + ")" + e.getLocalizedMessage());
            e.printStackTrace();
        }
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
                getColumns(), WHERE_CLAUSE, null, null, null, sortOrder);

        games = getReactionGameListByCursor(games, cursor);
        UtilsRG.info(games.size() + ". Games has been found for operationIssue: " + operationIssue + " and testType: " + testType + " and gameType: " + gameType);
        UtilsRG.info(games.toString());

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return games;
    }


    public List<ReactionGame> getAllReactionGameList(String operationIssue, String sortingOrder) {
        List<ReactionGame> games = new ArrayList<>();

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
                        DBContracts.ReactionGame.COLUMN_NAME_PATIENTS_ALERTNESS_FACTOR,
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
            game.setPatientsAlertnessFactor(cursor.getInt(6));
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
     *
     * @param reactionGameId the id (creation timeStamp)
     * @return the number of rows affected if a whereClause is passed in, 0 otherwise. To remove all rows and get a count pass "1" as the whereClause.
     */
    public int deleteById(String reactionGameId) {
        int resultCode = 0;

        // validation
        if (reactionGameId == null) return resultCode;
        if (reactionGameId.trim().equals("")) return resultCode;


        String WHERE_CLAUSE = DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE + " =?";
        //String id = UtilsRG.dateFormat.format(reactionGameId);
        try {
            resultCode = database.delete(
                    DBContracts.ReactionGame.TABLE_NAME, WHERE_CLAUSE, new String[]{reactionGameId});

            UtilsRG.info("Reaction game to delete from database: " + reactionGameId);
        } catch (Exception e) {
            UtilsRG.error("Exception! Could not delete reactiongame from database: " + reactionGameId + " " + e.getLocalizedMessage());
        }
        return resultCode;
    }

    /**
     * Updates the patients alertness factor
     *
     * @param reactionGameId          the reaction game creation date (its ID)
     * @param patientsAlertnessFactor the alertness factor of the patient
     */
    public void updatePatientsAlertness(String reactionGameId, int patientsAlertnessFactor) {
        UtilsRG.info("start to update patients alertness. value = " + patientsAlertnessFactor);
        Date creationDateId = null;
        try {
            // get reaction game
            creationDateId = UtilsRG.dateFormat.parse(reactionGameId);
            ReactionGame game = getReactionGameByDate(creationDateId);
            UtilsRG.info("reaction game to update has been found: " + game);

            // set new value for patients alertness
            game.setPatientsAlertnessFactor(patientsAlertnessFactor);

            // check if update has been successful via database
            long gamesUpdatedCount = update(game);
            UtilsRG.info("affected games =  " + gamesUpdatedCount + " . game = " + game);
        } catch (ParseException e) {
            UtilsRG.error("Could not update patients alertness. Error: " + e.getLocalizedMessage());
        }
    }


    public void updateBrainTemperature(String reactionGameId, Double temperature) {
        UtilsRG.info("Start to update patients brain temperature. value = " + temperature);
        try {
            // get reaction game
            ReactionGame game = getReactionGameByDate(UtilsRG.dateFormat.parse(reactionGameId));
            UtilsRG.info("reaction game to update has been found: " + game);

            // set new value for patients alertness
            game.setBrainTemperature(temperature);

            // check if update has been successful via database
            long gamesUpdatedCount = update(game);
            UtilsRG.info("Affected updated games =  " + gamesUpdatedCount + ". game = " + game);
        } catch (ParseException e) {
            UtilsRG.error("Could not update patients brain temperature. Error: " + e.getLocalizedMessage());
        }
    }


    /**
     * Updates a reaction game using database
     *
     * @param rtGame the reaction game to update
     * @return the number of rows affected
     */
    public long update(ReactionGame rtGame) {
        long rowsAffected = -1;

        if (rtGame.getCreationDate() == null) {
            UtilsRG.error("Cannot update rtGame: " + rtGame);
            return -1;
        }

        try {
            ContentValues contentValues = getReactionGameContentValues(rtGame);
            String[] WHERE_ARGS = new String[]{UtilsRG.dateFormat.format(rtGame.getCreationDate())};
            rowsAffected = database.update(tableName, contentValues, WHERE_ID_EQUALS, WHERE_ARGS);
            Log.i(tableName, rtGame + " has been updated. Rows affected: " + rowsAffected);
        } catch (Exception e) {
            Log.e(tableName, "Exception! Could not update the rtGame data(" + rtGame + ") " + " " + e.getLocalizedMessage());
        }
        return rowsAffected;
    }


    /**
     * Get content values by reaction game
     *
     * @param rtGame the reaction game to get values from
     * @return the content values of the reaction game
     */
    private ContentValues getReactionGameContentValues(ReactionGame rtGame) {
        ContentValues values = new ContentValues();

        if (rtGame.getUpdateDate() != null)
            values.put(DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE, UtilsRG.dateFormat.format(rtGame.getUpdateDate()));

        if (rtGame.getDuration() > 0)
            values.put(DBContracts.ReactionGame.COLUMN_NAME_DURATION, rtGame.getDuration());

        if (rtGame.getOperationIssueID() != null)
            values.put(DBContracts.ReactionGame.COLUMN_NAME_OPERATION_ISSUE_NAME, rtGame.getOperationIssueID());

        if (rtGame.getAverageReactionTime() > 0)
            values.put(DBContracts.ReactionGame.COLUMN_NAME_AVERAGE_REACTION_TIME, rtGame.getAverageReactionTime());

        if (rtGame.getGameType() != null)
            values.put(DBContracts.ReactionGame.COLUMN_NAME_GAME_TYPE, rtGame.getGameType().name());

        if (rtGame.getTestType() != null)
            values.put(DBContracts.ReactionGame.COLUMN_NAME_REACTIONTEST_TYPE, rtGame.getTestType().name());

        if (rtGame.getPatientsAlertnessFactor() > 0)
            values.put(DBContracts.ReactionGame.COLUMN_NAME_PATIENTS_ALERTNESS_FACTOR, rtGame.getPatientsAlertnessFactor());

        if (rtGame.getBrainTemperature() > 0)
            values.put(DBContracts.ReactionGame.COLUMN_NAME_BRAIN_TEMPERATURE, rtGame.getBrainTemperature());

        /*
        if(rtGame.getReactionTimesArray() != null)
            values.put(DBContracts.ReactionGame.COLUMN_NAME_REACTION_TIME_MEASURES, rtGame.getReactionTimesArray());
            */

        return values;
    }

    /**
     * Get the reaction game database columns
     *
     * @return the reaction game database columns
     */
    private String[] getColumns() {
        return new String[]{
                DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE,
                DBContracts.ReactionGame.COLUMN_NAME_UPDATE_DATE,
                DBContracts.ReactionGame.COLUMN_NAME_DURATION,
                DBContracts.ReactionGame.COLUMN_NAME_OPERATION_ISSUE_NAME,
                DBContracts.ReactionGame.COLUMN_NAME_AVERAGE_REACTION_TIME,
                DBContracts.ReactionGame.COLUMN_NAME_GAME_TYPE,
                DBContracts.ReactionGame.COLUMN_NAME_REACTIONTEST_TYPE,
                DBContracts.ReactionGame.COLUMN_NAME_PATIENTS_ALERTNESS_FACTOR,
                DBContracts.ReactionGame.COLUMN_NAME_BRAIN_TEMPERATURE,
        };
    }

    /**
     * Get all meta dates by a creation date
     *
     * @param creationDateId the creation date of the meta data
     * @return a list of all meta dates by creation date
     */
    @NonNull
    public ReactionGame getReactionGameByDate(Date creationDateId) {
        List<ReactionGame> reactionGamesList = new ArrayList<>();

        if (creationDateId == null) {
            return null;
        }

        Cursor cursor = database.query(tableName,
                getColumns(),
                DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE + " LIKE '" + UtilsRG.dateFormat.format(creationDateId) + "'",
                null, null, null, null);

        reactionGamesList = getReactionGameListByCursor(reactionGamesList, cursor);

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return reactionGamesList.get(0);
    }


    private List<ReactionGame>  getReactionGameListByCursor(List<ReactionGame> reactionGamesList, Cursor cursor) {
        while (cursor.moveToNext()) {
            ReactionGame game = null;
            try {
                game = new ReactionGame();
                game.setCreationDate(UtilsRG.dateFormat.parse(cursor.getString(0)));
            } catch (Exception e) {
                UtilsRG.error("Failed to getReactionGameByDate(" + cursor.getString(0) + ")!" + e.getLocalizedMessage());
            }
            try {
                game.setUpdateDate(UtilsRG.dateFormat.parse(cursor.getString(1)));
            } catch (Exception e) {
                UtilsRG.error(e.getLocalizedMessage());
            }
            try {
                game.setDuration(cursor.getDouble(2));
            } catch (Exception e) {
                UtilsRG.error(e.getLocalizedMessage());
            }
            try {
                game.setOperationIssueID(cursor.getString(3));
            } catch (Exception e) {
                UtilsRG.error(e.getLocalizedMessage());
            }
            try {
                game.setAverageReactionTime(cursor.getFloat(4));
            } catch (Exception e) {
                UtilsRG.error(e.getLocalizedMessage());
            }
            try {
                game.setGameType(Type.getGameType(cursor.getString(5)));
            } catch (Exception e) {
                UtilsRG.error(e.getLocalizedMessage());
            }
            try {
                game.setTestType(Type.getTestType(cursor.getString(6)));
            } catch (Exception e) {
                UtilsRG.error(e.getLocalizedMessage());
            }
            try {
                game.setPatientsAlertnessFactor(cursor.getInt(7));
            } catch (Exception e) {
                UtilsRG.error(e.getLocalizedMessage());
            }
            try {
                game.setBrainTemperature(cursor.getDouble(8));
            } catch (Exception e) {
                UtilsRG.error(e.getLocalizedMessage());
            }
            /*
            try {
                game.setReactionTimes(cursor.getBlob(8));
            } catch (Exception e) {
                UtilsRG.error(e.getLocalizedMessage());
            }
            */
            reactionGamesList.add(game);
        }
        return reactionGamesList;
    }

    /**
     * Get all reaction game records
     *
     * @return all reaction game records in the database. The String[] contains follwoing information:
     * 1. medicalId,
     * 2. age,
     * 3. gender,
     * 4. RT timestamp,
     * 5. Test Type e.g. pre-operation,
     * 6. patients alertness,
     * 7...n. reaction times
     */
    public List<String[]> getAllReactionGameRecords() {

        List<String[]> dataToReturn = new ArrayList<>();

        try {
            Context context = App.getAppContext();

            // all users in a list
            List<MedicalUser> userList = new MedicalUserManager(context).getAllMedicalUsers();

            // for each user
            for (int userIndex = 0; userIndex < userList.size(); userIndex++) {

                String medicalId = userList.get(userIndex).getMedicalId();
                List<OperationIssue> operationList = new OperationIssueManager(context).getAllOperationIssuesByMedicoId(medicalId);

                // for each operation of a user
                for (int operationIndex = 0; operationIndex < operationList.size(); operationIndex++) {
                    String operationIssue = operationList.get(operationIndex).getDisplayName();

                    List<ReactionGame> gameList = new ReactionGameManager(context).getAllReactionGameList(operationIssue, "ASC");

                    // for each reaction game of an operation
                    for (int gameIndex = 0; gameIndex < gameList.size(); gameIndex++) {

                        ReactionGame game = gameList.get(gameIndex);

                        // get all reaction times of a reaction game
                        List<Integer> timesList = new TrialManager(context).getAllReactionTimesList(game.getCreationDateFormatted());

                        // name, age, gender, type, datetime, time1, time2, time3, time4, time5
                        int initialValueCount = 6;
                        String[] reactionGameRecords = new String[initialValueCount + timesList.size()];

                        reactionGameRecords[0] = medicalId;
                        reactionGameRecords[1] = Integer.toString(userList.get(userIndex).getAge());
                        reactionGameRecords[2] = userList.get(userIndex).getGender().toString();
                        reactionGameRecords[3] = game.getCreationDateFormatted();
                        reactionGameRecords[4] = game.getTestType().toString();
                        reactionGameRecords[5] = Integer.toString(game.getPatientsAlertnessFactor());

                        // for each reaction time within a test
                        for (int k = 0; k < timesList.size(); k++) {
                            int rawReactionTime = timesList.get(k);
                            String reactionTime = Integer.toString(rawReactionTime);
                            reactionGameRecords[k + initialValueCount] = reactionTime;
                        }

                        UtilsRG.debug("single record: " + Arrays.toString(reactionGameRecords));
                        dataToReturn.add(reactionGameRecords);

                    }
                }
            }
        } catch (Exception e) {
            UtilsRG.error("Could not read database records. Exception: " + e.getLocalizedMessage());
        }

        return dataToReturn;
    }

    /**
     * Get all reaction game median data points in percentage by database and csv.
     *
     * @param userId      the file to read from
     * @param seasonality the seasonal value to use. It is the reaction game count
     *                    per operation.
     * @return the reaction game median data points in percentage by the database.
     */
    public double[] getAllReactionTimesInPercentageByUser(String userId, int seasonality) {
        Map<String, List<String[]>> reactionGameMap = getReactionGameMapOfSingleUser(userId);
        double[] timeSeriesRTData = ImportCSV.getReactionGameMedianArray(reactionGameMap, seasonality);
        return ImportCSV.getReactionGameMedianPerformancesInPercentage(timeSeriesRTData, seasonality);
    }

    /**
     * Get reaction games of the specified medical user
     *
     * @param userId the selected user
     * @return a map containing all reaction games of the given medical user
     */
    @NonNull
    private Map<String, List<String[]>> getReactionGameMapOfSingleUser(String userId) {
        List<String[]> reactionGameRecords = new ReactionGameManager(App.getAppContext()).getAllReactionGameRecords();
        Map<String, List<String[]>> timeSeriesDataMap = ImportCSV.getTimeSeriesDataMapByUser(reactionGameRecords);
        List<String[]> list = timeSeriesDataMap.get(userId);
        Map<String, List<String[]>> dataMap = new HashMap<>();
        dataMap.put(userId, list);
        return dataMap;
    }

    /**
     * Get all reaction game median data points in percentage by database and csv.
     *
     * @param csvFile     the file to read from
     * @param seasonality the seasonal value to use. It is the reaction game count
     *                    per operation.
     * @return the reaction game median data points in percentage by the database.
     */
    public double[] getAllReactionTimesInPercentage(String csvFile, int seasonality) {
        List<String[]> dbData = new ReactionGameManager(App.getAppContext()).getAllReactionGameRecords();
        List<String[]> csvData = ImportCSV.readCsv(App.getAppContext(), csvFile);

        // combine both lists
        List<String[]> combinedList = new ArrayList<>(dbData.size() + csvData.size());
        combinedList.addAll(csvData);
        combinedList.addAll(dbData);

        // sort list by timestamp
        Collections.sort(combinedList, new Comparator<String[]>() {
            @Override
            public int compare(String[] game1, String[] game2) {
                return game1[3].compareTo(game2[3]);
            }
        });

        return ImportCSV.getReactionGameMedianPerformancesInPercentageByReactionGameRecords(seasonality, combinedList);
    }
}
