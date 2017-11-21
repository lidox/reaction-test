package com.artursworld.reactiontest.model.persistence.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.analysis.outlierdetection.SnEstimator;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.importer.ImportCSV;
import com.artursworld.reactiontest.controller.util.App;
import com.artursworld.reactiontest.controller.util.Lists;
import com.artursworld.reactiontest.controller.util.Strings;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.EntityDbManager;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.view.statistics.ReactionTimeLineChartWithForecast;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.util.DoubleArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
* Manages trials via database
*/
public class TrialManager extends EntityDbManager {

    public TrialManager(Context context) {
        super(context);
    }

    /**
     * Inserts a trial to reaction a reaction game by reaction game id async
     */
    public void insertTrialtoReactionGameAsync(final String reactionGameCreationTime, final boolean isValid, final double reactionTime) {
        if (reactionTime > 0) {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    ContentValues values = new ContentValues();
                    values.put(DBContracts.TrialTable.CREATION_DATE, UtilsRG.dateFormat.format(new Date()));
                    values.put(DBContracts.TrialTable.IS_VALID, (isValid) ? 1 : 0);
                    values.put(DBContracts.TrialTable.REACTION_TIME, reactionTime);
                    values.put(DBContracts.TrialTable.PK_REACTIONGAME_CREATION_DATE, reactionGameCreationTime);
                    try {
                        database.insertOrThrow(DBContracts.TrialTable.TABLE_NAME, null, values);
                        UtilsRG.info("New trial added to reaction game(" + reactionGameCreationTime + ") with reaction time(" + reactionTime + ") successfully");
                    } catch (Exception e) {
                        UtilsRG.error("Could not insert trial into db for reaction game(" + reactionGameCreationTime + ")" + e.getLocalizedMessage());
                    }
                    return null;
                }
            }.execute();
        }
    }

    /**
     * returns filtered value e.g. AVG for a reaction game
     */
    public double getFilteredReactionTimeByReactionGameId(String reactionGameId, String filter, boolean isValid) {
        Cursor cursor = null;
        try {
            cursor = getCursorByFilter(reactionGameId, filter, isValid, cursor);

            cursor.moveToFirst();
            UtilsRG.info("Get via database the filtered(" + filter + ") and only valid trails reaction time  by reactionGameId(" + reactionGameId + ")= " + cursor.getDouble(0));
            return cursor.getDouble(0);
        } catch (Exception e) {
            UtilsRG.error("could not get filtered reaction time. " + e.getLocalizedMessage());
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
     * Get the cursor by given filter
     *
     * @param reactionGameId the id of the reaction test game
     * @param filter         the filter for database
     * @param isValid        is valid reaction test
     * @param cursor         the initial cursor
     * @return the cursor containing records
     */
    private Cursor getCursorByFilter(String reactionGameId, String filter, boolean isValid, Cursor cursor) {
        String WHERE_CLAUSE = null;
        if (reactionGameId != null) {
            WHERE_CLAUSE = DBContracts.TrialTable.PK_REACTIONGAME_CREATION_DATE + " like '" + reactionGameId + "' ";
            WHERE_CLAUSE += "AND " + DBContracts.TrialTable.IS_VALID + " = " + ((isValid) ? 1 : 0);
        }
        cursor = database.query(DBContracts.TrialTable.TABLE_NAME,
                new String[]{filter + "(" + DBContracts.TrialTable.REACTION_TIME + ")"},
                WHERE_CLAUSE,
                null, null, null, null);
        return cursor;
    }

    public List<Integer> getAllReactionTimesList(String reactionGameId) {
        List<Integer> times = new ArrayList<Integer>();
        Cursor cursor = null;
        try {
            String WHERE_CLAUSE = null;
            if (reactionGameId != null) {
                WHERE_CLAUSE = DBContracts.TrialTable.PK_REACTIONGAME_CREATION_DATE + " like '" + reactionGameId + "' ";
                WHERE_CLAUSE += "AND " + DBContracts.TrialTable.IS_VALID + " = " + ((true) ? 1 : 0);
            }
            cursor = database.query(DBContracts.TrialTable.TABLE_NAME,
                    new String[]{DBContracts.TrialTable.REACTION_TIME},
                    WHERE_CLAUSE,
                    null, null, null, null);

            while (cursor.moveToNext()) {
                float reactionTime = cursor.getFloat(0);
                int roundedInt = Math.round(reactionTime * 1000);
                times.add(roundedInt);
            }
        } catch (Exception e) {
            UtilsRG.error("lol: " + e.getLocalizedMessage());
        } finally {
            try {
                if (cursor != null)
                    cursor.close();
            } catch (Exception e) {
                UtilsRG.error("Error" + e.getLocalizedMessage());
            }
        }
        return times;
    }

    /**
     * Get reaction game count by id
     *
     * @param reactionGameId the id of the reaction game
     * @param isValid        is valid reaction game
     * @return the reaction game count
     */
    public int getReactionGameCount(String reactionGameId, boolean isValid) {
        Cursor cursor = getCursorByFilter(reactionGameId, "COUNT", isValid, null);
        int result = 0;
        while (cursor.moveToNext())
            result = cursor.getInt(0);

        return result;
    }

    public double getReactionGameMedian(String reactionGameId, boolean isValid) {
        Cursor cursor = getCursorByFilter(reactionGameId, "", isValid, null);
        double[] reactionTimes = new double[cursor.getCount()];
        int index = 0;
        while (cursor.moveToNext()) {
            reactionTimes[index] = cursor.getDouble(0);
            index++;
        }
        return new Median().evaluate(reactionTimes);
    }

    /**
     * Calculates the outlier rating for a specific reaction game
     *
     * @param reactionGameId the reaction game Id to calculate the rating for
     * @return the outlier rating for the given reaction game
     */
    public String getOutlierRating(String reactionGameId, double[] historicReactionTimeData) {

        // reaction times of a single reaction game
        double[] reactionTimeArray = getReactionTimesById(reactionGameId);

        // selected median, because it represents a good value of a reaction game
        double valueToCheck = new Median().evaluate(reactionTimeArray);


        double snScore = SnEstimator.getSnScore(valueToCheck, historicReactionTimeData);
        UtilsRG.info("sn_score = " + snScore);

        String ratingToDisplay = "-";

        if (isInRange(snScore, 0, 1))
            ratingToDisplay = Strings.getStringByRId(R.string.good);

        else if (isInRange(snScore, 1, 2))
            ratingToDisplay = Strings.getStringByRId(R.string.ok);

        else if (isInRange(snScore, 2, 3))
            ratingToDisplay = Strings.getStringByRId(R.string.bad);

        else if (isInRange(snScore, 3, Integer.MAX_VALUE))
            ratingToDisplay = Strings.getStringByRId(R.string.critical);

        // in case the reaction time is too good
        double usersPreOperationalMedian = getUsersMedianByTestType(Type.TestTypes.PreOperation);
        UtilsRG.debug("historical pre-operational median = " + usersPreOperationalMedian);
        boolean valueToCheckIsBetterThanMedian = snScore > 1 && valueToCheck <= usersPreOperationalMedian;
        if (valueToCheckIsBetterThanMedian) {
            ratingToDisplay = Strings.getStringByRId(R.string.good);
        }

        return ratingToDisplay;
    }

    private boolean isInRange(double valueToCheck, int min, int max) {
        return Math.min(min, max) <= valueToCheck && Math.max(min, max) >= valueToCheck;
    }

    /**
     * Get all reaction times of a specific reaction game
     *
     * @param reactionGameId the reaction game id
     * @return all reaction times of a reaction game
     */
    private double[] getReactionTimesById(String reactionGameId) {
        List<Integer> list = getAllReactionTimesList(reactionGameId);
        double[] resultArray = new double[list.size()];
        for (int index = 0; index < list.size(); index++) {
            resultArray[index] = list.get(index);
        }
        return resultArray;
    }

    /**
     * Calculate the median of the current selected operation issue (user) by the specified test type (e.g. pre-operation)
     *
     * @param testType the test type of the reaction game (e.g. pre-operation)
     * @return the median of the current selected operation issue (user) by the specified test type (e.g. pre-operation)
     */
    public static double getUsersMedianByTestType(Type.TestTypes testType) {
        // get operation issue
        String selectedOperationIssue = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, App.getAppContext());

        // get all games
        List<ReactionGame> gameList = new ReactionGameManager(App.getAppContext()).getAllReactionGameList(selectedOperationIssue, "ASC");

        // filter games
        List<ReactionGame> preOpGameList = ReactionTimeLineChartWithForecast.getReactionGamesFilteredByGameType(gameList, testType);

        return ReactionTimeLineChartWithForecast.getPreOpMedian(preOpGameList);
    }

    public double[] getAllReactionTimesFromDB() {

        List<Double> dataToReturn = new ArrayList<>();
        Context context = App.getAppContext();

        try {

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

                        // for each reaction time within a test
                        for (int k = 0; k < timesList.size(); k++) {
                            Double rawReactionTime = timesList.get(k).doubleValue() / 1000.;
                            dataToReturn.add(rawReactionTime);
                        }
                    }
                }
            }
        } catch (Exception e) {
            UtilsRG.error("Could not read database records. Exception: " + e.getLocalizedMessage());
        }

        return Lists.getArray(dataToReturn);
    }
}
