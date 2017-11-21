package com.artursworld.reactiontest.controller.importer;


import android.content.Context;
import android.content.res.AssetManager;

import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.util.App;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.opencsv.CSVReader;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ImportCSV {

    /**
     * Reads the CSV file a returns the data inside the file
     *
     * @param context     the app context
     * @param csvFilePath the file to read
     * @return the data from the CSV. The String[] contains follwoing information:
     * 1. medicalId,
     * 2. age,
     * 3. gender,
     * 4. RT timestamp,
     * 5. Test Type e.g. pre-operation,
     * 6. patients alertness,
     * 7...n. reaction times
     */
    public static final List<String[]> readCsv(Context context, String csvFilePath) {
        List<String[]> questionList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();

        try {
            InputStream csvStream = assetManager.open(csvFilePath);
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
            CSVReader csvReader = new CSVReader(csvStreamReader);
            String[] line;
            csvReader.readNext();

            while ((line = csvReader.readNext()) != null) {
                questionList.add(line);
            }
        } catch (Exception e) {
            UtilsRG.error(e.getLocalizedMessage());
        }
        return questionList;
    }

    /**
     * Get a map filtered by user
     *
     * @param rawCsvData a list containing reaction time records
     * @return a map filtered by user
     */
    public static Map<String, List<String[]>> getTimeSeriesDataMapByUser(List<String[]> rawCsvData) {
        Map<String, List<String[]>> userMap = new LinkedHashMap<>();

        for (String[] reactionGame : rawCsvData) {

            String name = reactionGame[0];
            String operationType = reactionGame[4];
            String reactionTestTimeStamp = reactionGame[3];

            List<String> reactionTimesTmp = new ArrayList<>();
            if (reactionGame.length > 6)
                reactionTimesTmp.add(reactionGame[6]);
            if (reactionGame.length > 7)
                reactionTimesTmp.add(reactionGame[7]);
            if (reactionGame.length > 8)
                reactionTimesTmp.add(reactionGame[8]);
            if (reactionGame.length > 9)
                reactionTimesTmp.add(reactionGame[9]);
            if (reactionGame.length > 10)
                reactionTimesTmp.add(reactionGame[10]);

            if (!userMap.containsKey(name)) {
                userMap.put(name, new ArrayList<String[]>());
            }

            // add new value
            List<String[]> reactionTimes = userMap.get(name);
            String[] reactionTimesToAdd = new String[reactionTimesTmp.size() + 2];
            reactionTimesToAdd[0] = operationType;
            reactionTimesToAdd[1] = reactionTestTimeStamp;
            for (int i = 0; i < reactionTimesTmp.size(); i++) {
                reactionTimesToAdd[i + 2] = reactionTimesTmp.get(i);
            }
            //String[] reactionTimesToAdd = {operationType, reactionTestTimeStamp, reactionTime1, reactionTime2, reactionTime3, reactionTime4, reactionTime5};
            reactionTimes.add(reactionTimesToAdd);
            userMap.put(name, reactionTimes);
        }

        return userMap;
    }

    /**
     * Filters the data by the conditions which need to be met in order to make forecasts.
     * The condition is to have a minimum of 'minimumOfReactionTestsPerOperation' reaction games within a operation
     * and a single pre operation game.
     *
     * @param data the reaction game data for each patient
     * @return valid reaction game data data for each patient
     */
    public static Map<String, List<String[]>> getValidTimeSeriesDataMap(Map<String, List<String[]>> data, int minimumOfReactionTestsPerOperation) {
        Map<String, List<String[]>> filteredResult = new LinkedHashMap<>();
        for (Map.Entry<String, List<String[]>> csvRow : data.entrySet()) {
            // the user must have at least a minimum of tries per operation
            if (csvRow.getValue().size() > minimumOfReactionTestsPerOperation) {
                // enough reaction times
                if (hasSinglePreAndAMinimumOfTriesPerGameIntraOperationRecords(csvRow.getValue(), minimumOfReactionTestsPerOperation)) {
                    filteredResult.put(csvRow.getKey(), csvRow.getValue());
                }
            }
        }

        return filteredResult;
    }

    /**
     * Check if the list contains at least a single pre operative reaction game and minimal
     * three of intra operative reaction tests. Attention! A single reaction test can contain
     * multiple reaction times!
     *
     * @param reactionGameList a list of reaction games of a single patient
     * @return true if the list contains at least a single pre-operative reaction game and minimal
     * three of intra operative reaction games
     */
    private static boolean hasSinglePreAndAMinimumOfTriesPerGameIntraOperationRecords(List<String[]> reactionGameList, int minimumOfReactionTestTriesPerGame) {
        boolean hasPre = false;
        boolean hasThreeIntra = false;
        int intraCounter = 0;

        for (String[] row : reactionGameList) {
            if (row[0].equals(Type.TestTypes.PreOperation.name())) {
                hasPre = true;
            } else if (row[0].equals(Type.TestTypes.InOperation.name())) {
                intraCounter++;
                hasThreeIntra = (intraCounter >= minimumOfReactionTestTriesPerGame);
            }
        }
        return hasPre && hasThreeIntra;
    }


    /**
     * Get seasonal time series data by setting intra-operation test count.
     *
     * @param timeSeriesDataMap                 a map containing all records per user
     * @param minimumOfReactionTestTriesPerGame the count of intra of reactions to set. The seasonality equals
     *                                          minimumOfReactionTestTriesPerGame + 1 (pre-operation reaction time)
     * @return a time series reaction time array
     */
    public static double[] getReactionGameMedianArray(Map<String, List<String[]>> timeSeriesDataMap, int minimumOfReactionTestTriesPerGame) {
        List<Double> preOperationReactionTimesList = new ArrayList<>();
        List<Double> timeSeriesResultList = new ArrayList<>();

        // for each user
        for (Map.Entry<String, List<String[]>> userRow : timeSeriesDataMap.entrySet()) {

            List<Double> usersReactionMedianList = new ArrayList<>();
            List<String[]> usersGameList = userRow.getValue();

            if (usersGameList == null) {
                continue;
            }

            // for each reaction game of a user
            for (String[] reactionGame : usersGameList) {

                // read game data
                String operationType = reactionGame[0];

                double reactionTime1 = (reactionGame.length > 2) ? getReactionTimeByRecord(reactionGame[2]) : -1;
                double reactionTime2 = (reactionGame.length > 3) ? getReactionTimeByRecord(reactionGame[3]) : -1;
                double reactionTime3 = (reactionGame.length > 4) ? getReactionTimeByRecord(reactionGame[4]) : -1;
                double reactionTime4 = (reactionGame.length > 5) ? getReactionTimeByRecord(reactionGame[5]) : -1;
                double reactionTime5 = (reactionGame.length > 6) ? getReactionTimeByRecord(reactionGame[6]) : -1;

                boolean hasToSkipRecord = operationType.equals(Type.TestTypes.Trial.name()) || operationType.equals(Type.TestTypes.PostOperation.name());
                if (hasToSkipRecord) {
                    // skip analysis if game was a trial or post operation
                    continue;
                } else if (operationType.equals(Type.TestTypes.PreOperation.name())) {
                    // fill pre-opration list
                    preOperationReactionTimesList = addReactionTime(preOperationReactionTimesList, reactionTime1);
                    preOperationReactionTimesList = addReactionTime(preOperationReactionTimesList, reactionTime2);
                    preOperationReactionTimesList = addReactionTime(preOperationReactionTimesList, reactionTime3);
                    preOperationReactionTimesList = addReactionTime(preOperationReactionTimesList, reactionTime4);
                    preOperationReactionTimesList = addReactionTime(preOperationReactionTimesList, reactionTime5);
                } else if (operationType.equals(Type.TestTypes.InOperation.name())) {
                    // get median of in-operation game and fill median list if allowed
                    List<Double> intraOperationReactionTimes = new ArrayList<>();
                    intraOperationReactionTimes = addReactionTime(intraOperationReactionTimes, reactionTime1);
                    intraOperationReactionTimes = addReactionTime(intraOperationReactionTimes, reactionTime2);
                    intraOperationReactionTimes = addReactionTime(intraOperationReactionTimes, reactionTime3);
                    intraOperationReactionTimes = addReactionTime(intraOperationReactionTimes, reactionTime4);
                    intraOperationReactionTimes = addReactionTime(intraOperationReactionTimes, reactionTime5);
                    double median = new DescriptiveStatistics(getDataPointsByList(intraOperationReactionTimes)).getPercentile(50);
                    if (usersReactionMedianList.size() < minimumOfReactionTestTriesPerGame)
                        usersReactionMedianList.add(median);
                }
            } // end of single reaction game

            // get pre-operation median of this game
            double[] dataPoints = getDataPointsByList(preOperationReactionTimesList);
            double preOperationsMedian = new DescriptiveStatistics(dataPoints).getPercentile(50);
            UtilsRG.debug("pre op median: " + preOperationsMedian);

            // add pre-operation median at the beginning of the medians
            usersReactionMedianList.add(0, preOperationsMedian);
            UtilsRG.debug("single Game result: " + usersReactionMedianList.toString());

            // add game medians into final list
            timeSeriesResultList.addAll(usersReactionMedianList);
        }


        // parse final result list to an array
        double[] timeSeries = getDataPointsByList(timeSeriesResultList);

        if (timeSeries == null)
            return new double[]{0.0};

        UtilsRG.debug("time series data: " + Arrays.toString(timeSeries));
        return timeSeries;
    }

    /**
     * Parse List of doubles to an array of doubles
     *
     * @param listOfDoubles the list to parse
     * @return array of doubles
     */
    private static double[] getDataPointsByList(List<Double> listOfDoubles) {
        double[] target = new double[listOfDoubles.size()];
        for (int i = 0; i < target.length; i++) {
            target[i] = listOfDoubles.get(i);
        }
        return target;
    }


    private static List<Double> addReactionTime(List<Double> doubleList, double reactionTime) {
        if (reactionTime != -1)
            doubleList.add(reactionTime);

        return doubleList;
    }

    /**
     * Parse reaction time to a double value
     *
     * @param reactionTimeAsString the value to parse
     * @return the double value reaction time
     */
    private static double getReactionTimeByRecord(String reactionTimeAsString) {
        double valueToReturn = -1;
        try {
            valueToReturn = Double.parseDouble(reactionTimeAsString);
        } catch (Exception e) {
            UtilsRG.info(reactionTimeAsString + " could not be parsed.");
        }
        return valueToReturn;
    }

    /**
     * Get seasonal time series reaction game medians in percentage
     *
     * @param reactionTimeMedians the reaction time medians of each game.
     *                            If seasonalVale equals 3 the given array looks like this: [pre, in, in, in, pre, in, in, in, ...]
     * @param seasonValue         the seasonal value of the data
     * @return the reaction time performances in percentage as a time series data array
     */
    public static double[] getReactionGameMedianPerformancesInPercentage(double[] reactionTimeMedians, int seasonValue) {
        if (reactionTimeMedians == null) {
            UtilsRG.error("there is no data to calculate, because the reaction time medians equals null!");
            return null;
        }

        List<Double> percentageReactionTimesList = new ArrayList<>();

        // every seasonalValue + 1 a new pre-op reaction time appears
        int medianIndex = 0;
        for (int index = 0; index < (reactionTimeMedians.length - 1); index++) {
            if ((index + 1) % (seasonValue + 1) == 0 && index != 0) {
                medianIndex += (seasonValue + 1);
                index = medianIndex;
            }
            double preOpReactionTime = reactionTimeMedians[medianIndex];
            double currentValue = reactionTimeMedians[index + 1];
            double percentageValue = (preOpReactionTime / currentValue) * 100;
            percentageReactionTimesList.add(percentageValue);
        }

        // transform list into array
        return getDataPointsByList(percentageReactionTimesList);
    }

    /**
     * Get the reaction game median data points in percentage by the given CSV file.
     *
     * @param csvFile                       the file to read from
     * @param reactionGameCountPerOperation the seasonal value to use. It is the reaction game count
     *                                      per operation.
     * @return the reaction game median data points in percentage by the given CSV file.
     */
    public static double[] getReactionTimeDataInPercentageByCSV(String csvFile, int reactionGameCountPerOperation) {
        List<String[]> data = readCsv(App.getAppContext(), csvFile);
        return getReactionGameMedianPerformancesInPercentageByReactionGameRecords(reactionGameCountPerOperation, data);
    }

    /**
     * Get the reaction game median performance in percentage by reaction game records
     *
     * @param reactionGameCountPerOperation the reaction game count per operation value.
     *                                      It represents the seasonality of the time series.
     * @param reactionGameRecords           the reaction game records to use
     * @return the reaction game median performance in percentage by reaction game records
     */
    public static double[] getReactionGameMedianPerformancesInPercentageByReactionGameRecords(int reactionGameCountPerOperation, List<String[]> reactionGameRecords) {
        Map<String, List<String[]>> timeSeriesDataMap = getTimeSeriesDataMapByUser(reactionGameRecords);
        Map<String, List<String[]>> validTimeSeriesDataMap = getValidTimeSeriesDataMap(timeSeriesDataMap, reactionGameCountPerOperation);
        double[] timeSeriesRTData = getReactionGameMedianArray(validTimeSeriesDataMap, reactionGameCountPerOperation);
        return getReactionGameMedianPerformancesInPercentage(timeSeriesRTData, reactionGameCountPerOperation);
    }

}
