package com.artursworld.reactiontest.view.games;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.analysis.outlierdetection.OutlierDetection;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.util.App;
import com.artursworld.reactiontest.controller.util.Lists;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.artursworld.reactiontest.model.persistence.manager.TrialManager;
import com.artursworld.reactiontest.view.dialogs.AwakeSurvey;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Displays reaction game results after each reaction game (e.g. the average reaction time)
 */
public class SingleGameResultView extends AppCompatActivity {

    // Reaction game attributes
    private String reactionGameId;
    private String testType;
    private String gameType;

    private static DecimalFormat decimalFormat = new DecimalFormat("#########0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_game_result_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGameSettingsByIntent();
        deleteGoNoGoGameFailuresAsync();
        showAverageReactionTimeAsync(reactionGameId, this);
        showMedianReactionTimeAsync(R.id.reaction_time_median_title);
        showReactionTimeCountAsync(R.id.reaction_time_tries_title);
        showOutlierRatingAsync(R.id.reaction_time_rating_title);
    }


    @Override
    protected void onStop() {
        super.onStop();
        UtilsRG.putString(UtilsRG.REACTION_GAME_ID, null, this);
    }

    /**
     * Sets game attributes by the intents before extras
     */
    private void getGameSettingsByIntent() {
        String medicalUserId = getIntentMessage(StartGameSettings.EXTRA_MEDICAL_USER_ID);
        String operationIssueName = getIntentMessage(StartGameSettings.EXTRA_OPERATION_ISSUE_NAME);
        testType = getIntentMessage(StartGameSettings.EXTRA_TEST_TYPE);
        gameType = getIntentMessage(StartGameSettings.EXTRA_GAME_TYPE);
        reactionGameId = getIntentMessage(StartGameSettings.EXTRA_REACTION_GAME_ID);
        UtilsRG.info(SingleGameResultView.class.getSimpleName() + " received user(" + medicalUserId + ") with operation name("
                + operationIssueName + "). Test type=" + testType + ", GameType=" + gameType + ",ReactionGameId=" + reactionGameId);

        if (medicalUserId == null) {
            medicalUserId = UtilsRG.getStringByKey(UtilsRG.MEDICAL_USER, this);
        }
        if (operationIssueName == null) {
            operationIssueName = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, this);
        }
        if (gameType == null) {
            gameType = UtilsRG.getStringByKey(UtilsRG.GAME_TYPE, this);
        }
        if (testType == null) {
            testType = UtilsRG.getStringByKey(UtilsRG.TEST_TYPE, this);
        }
        if (reactionGameId == null) {
            reactionGameId = UtilsRG.getStringByKey(UtilsRG.REACTION_GAME_ID, this);
        }
        UtilsRG.info("Received user(" + medicalUserId + "), operation name(" + operationIssueName + ")");
        UtilsRG.info("Test type=" + testType + ", GameType=" + gameType + ", reactionGameId=" + reactionGameId);

    }

    /**
     * Helper method to return intent messages
     */
    private String getIntentMessage(String messageKey) {
        Intent intent = getIntent();
        return intent.getStringExtra(messageKey);
    }

    /**
     * Get the average reactiontime of the game and display result i a text view
     */
    public static void showAverageReactionTimeAsync(final String reactionGameId, final Activity activity) {
        final String averageFilter = "AVG";

        new AsyncTask<Void, Void, Double>() {
            @Override
            protected Double doInBackground(Void... params) {
                TrialManager db = new TrialManager(App.getAppContext());
                return db.getFilteredReactionTimeByReactionGameId(reactionGameId, averageFilter, true);
            }

            @Override
            protected void onPostExecute(final Double averageReactionTime) {
                super.onPostExecute(averageReactionTime);
                insertAverageReactionTimeAsync(averageReactionTime, reactionGameId, activity);
            }
        }.execute();
    }

    /**
     * Insert average reaction time and sets UI elements
     *
     * @param averageReactionTime the average reaction time to display
     */
    private static void insertAverageReactionTimeAsync(final Double averageReactionTime, final String reactionGameId, final Activity activity) {
        UtilsRG.info("Average Reaction Time for reactionGame(" + reactionGameId + ") =" + averageReactionTime + " s");
        TextView averageReactionTimeTitle = (TextView) activity.getWindow().getDecorView().getRootView().findViewById(R.id.reaction_time_title_1);

        if (averageReactionTimeTitle != null) {
            String reactionTimeText = decimalFormat.format(averageReactionTime * 1000) + "";
            int decimalPlacesCount = 3;
            if (reactionTimeText.length() > (decimalPlacesCount + 1)) {
                reactionTimeText = reactionTimeText.substring(0, (decimalPlacesCount + 2));
            }
            averageReactionTimeTitle.setText(reactionTimeText);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... unusedParams) {
                    new ReactionGameManager(App.getAppContext()).updateAverageReactionTimeById(reactionGameId, averageReactionTime);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    clearNotFinishedGames(activity);
                }
            }.execute();
        }
    }

    /**
     * on click the finish button --> go back to launcher
     */
    public void onFinishBtnClick(View view) {
        UtilsRG.info("User clicked: finish game");

        updateBrainTemperatureByEditTextValue(reactionGameId, R.id.add_temperature_id, this);

        new AwakeSurvey(this, reactionGameId).displayAwakeSurvey();
    }

    /**
     * Updates the brain temperature using value from UI edit text
     *
     * @param reactionGameId the reaction game to be used
     * @param editTextId     the edit text id
     * @param activity       the active activity
     */
    private void updateBrainTemperatureByEditTextValue(String reactionGameId, int editTextId, Activity activity) {
        MaterialEditText temperatureTxt = (MaterialEditText) activity.findViewById(editTextId);
        if (temperatureTxt != null) {
            String temperatureValue = temperatureTxt.getText().toString();
            if (temperatureValue != null && !temperatureValue.trim().isEmpty()) {
                try {
                    Double temperature = Double.parseDouble(temperatureValue);
                    new ReactionGameManager(activity).updateBrainTemperature(reactionGameId, temperature);
                } catch (Exception e) {
                    UtilsRG.error("Cannot update temperature because value '" + temperatureValue + "' not parseable. " + e.getLocalizedMessage());
                }
            }
        }
    }

    /**
     * On click the retry button --> go back to the activity before the game starts
     */
    public void onRetryBtnClick(View view) {
        UtilsRG.info("User does a second try");
        Intent intent = new Intent(this, StartGameSettings.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startGameActivityByTypes(gameType, testType);
    }

    /**
     * Opens new game
     *
     * @param gameType the game type
     * @param testType the test type
     */
    private void startGameActivityByTypes(String gameType, String testType) {
        if (gameType != null && testType != null) {
            Intent intent = null;
            if (Type.GameTypes.GoGame.name().equalsIgnoreCase(gameType)) {
                intent = new Intent(this, GoGameView.class);
            } else if (Type.GameTypes.GoNoGoGame.name().equalsIgnoreCase(gameType)) {
                intent = new Intent(this, GoNoGoGameView.class);
            }

            if (intent != null)
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            startActivity(intent);
        }
    }

    /**
     * Open delete Dialog
     *
     * @param view the rootView
     */
    public void onDeleteBtnClick(View view) {
        UtilsRG.info("User wants to delete last try");
        new MaterialDialog.Builder(this)
                .title(R.string.delete_trial)
                .content(R.string.delete_trial_description)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteReactionGameAsync();
                    }
                })
                .show();
    }

    /**
     * Get the meadian reaction time of the game and display result i a text view
     */
    private void showReactionTimeCountAsync(final int resourceId) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                TrialManager db = new TrialManager(getApplicationContext());
                return db.getReactionGameCount(reactionGameId, true) + "";
            }

            @Override
            protected void onPostExecute(final String reactionGameCount) {
                super.onPostExecute(reactionGameCount);
                TextView averageReactionTimeTitle = (TextView) findViewById(resourceId);//R.id.reaction_time_median_title
                if (averageReactionTimeTitle != null) {
                    averageReactionTimeTitle.setText(reactionGameCount);
                }
            }
        }.execute();

    }

    /**
     * Displays the median on the UI
     *
     * @param resId the resource id to display the median
     */
    private void showMedianReactionTimeAsync(final int resId) {
        new AsyncTask<Void, Void, Double>() {
            @Override
            protected Double doInBackground(Void... params) {
                TrialManager db = new TrialManager(getApplicationContext());
                return db.getReactionGameMedian(reactionGameId, true);
            }

            @Override
            protected void onPostExecute(final Double reactionGameMedian) {
                super.onPostExecute(reactionGameMedian);
                String reactionTimeText = decimalFormat.format(reactionGameMedian * 1000) + "";
                TextView textView = (TextView) findViewById(resId);
                if (textView != null) {
                    textView.setText(reactionTimeText);// + " " + getResources().getString(R.string.milliseconds));;
                }
            }
        }.execute();

    }

    /**
     * Deletes go-no-go reaction games with failures
     */
    private void deleteGoNoGoGameFailuresAsync() {
        final Activity activity = this;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return UtilsRG.getStringByKey(UtilsRG.GAME_TYPE, activity);
            }

            @Override
            protected void onPostExecute(String gameTypeString) {
                super.onPostExecute(gameTypeString);
                Type.GameTypes gameType = Type.getGameType(gameTypeString);
                if (gameType == Type.GameTypes.GoNoGoGame) {
                    getFilteredReactionTimeByReactionGameIdAsync();
                }
            }

            private void getFilteredReactionTimeByReactionGameIdAsync() {
                new AsyncTask<Void, Void, Integer>() {

                    @Override
                    protected Integer doInBackground(Void... params) {
                        return (int) new TrialManager(activity).getFilteredReactionTimeByReactionGameId(reactionGameId, "COUNT", false);
                    }

                    @Override
                    protected void onPostExecute(final Integer inValidTrialCount) {
                        super.onPostExecute(inValidTrialCount);
                        UtilsRG.info("found inValidTrialCount = " + inValidTrialCount);

                        updateInValidTrialCountByIdAsync(inValidTrialCount, activity);
                    }
                }.execute();
            }
        }.execute();
    }

    /**
     * Update invalid trials
     *
     * @param inValidTrialCount the invalid count
     * @param activity          the active activity
     */
    private void updateInValidTrialCountByIdAsync(final Integer inValidTrialCount, final Activity activity) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                new ReactionGameManager(activity).updateInValidTrialCountById(reactionGameId, inValidTrialCount);
                return null;
            }
        }.execute();
    }

    /**
     * Deletes the reaction game
     */
    private void deleteReactionGameAsync() {
        final Activity activity = this;
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                ReactionGame r = new ReactionGame();
                UtilsRG.info("delete:" + reactionGameId);
                try {
                    Date reactionGameIdDate = UtilsRG.dateFormat.parse(reactionGameId);
                    r.setCreationDate(reactionGameIdDate);
                } catch (ParseException e) {
                    UtilsRG.error("Could not delete reactionGame:" + r);
                }
                return new ReactionGameManager(activity).delete(r);
            }

            @Override
            protected void onPostExecute(final Integer inValidTrialCount) {
                super.onPostExecute(inValidTrialCount);
                UtilsRG.info("reactionGame delete with result code = " + inValidTrialCount);
                updateInValidTrialCountByIdAsync(inValidTrialCount, activity);
                finish();
            }
        }.execute();
    }

    /**
     * Deletes games which has not been finished
     */
    private static void clearNotFinishedGames(final Activity activity ) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                new ReactionGameManager(activity).deleteNotFinishedGames();
                return null;
            }
        }.execute();
    }

    /**
     * Displays the rating of the reaction game. If contains outliers
     * the rating is bad. Otherwise the rating is good.
     *
     * @param resId the Id of the resource to display the rating on
     */
    private void showOutlierRatingAsync(final int resId) {
        UtilsRG.info("show rating method started...");
        final Activity activity = this;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                TrialManager db = new TrialManager(getApplicationContext());

                double[] historicReactionTimeData = OutlierDetection.getReactionTimeData(activity, R.array.all_rt_with_outliers_july_2017);

                double[] historicReactionTimesFromDB = db.getAllReactionTimesFromDB();

                double[] combinedDBandCSVdata = Lists.combine(historicReactionTimeData, historicReactionTimesFromDB);

                return db.getOutlierRating(reactionGameId, combinedDBandCSVdata);
            }

            @Override
            protected void onPostExecute(final String outlierRating) {
                super.onPostExecute(outlierRating);
                TextView textView = (TextView) findViewById(resId);
                if (textView != null) {
                    textView.setText(outlierRating);
                }
            }
        }.execute();
    }

}
