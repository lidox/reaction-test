package com.artursworld.reactiontest.view.games;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.InOpEvent;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.model.persistence.manager.InOpEventManager;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.artursworld.reactiontest.model.persistence.manager.TrialManager;
import com.artursworld.reactiontest.view.LauncherView;
import com.github.mikephil.charting.utils.Utils;
import com.github.pavlospt.CircleView;

import java.text.ParseException;
import java.util.Date;

import at.grabner.circleprogress.CircleProgressView;

/**
 * Displays reaction game results after a reaction game (e.g. the average reaction time)
 */
public class SingleGameResultView extends AppCompatActivity {

    // Reaction game attributes
    private String reactionGameId;
    private String medicalUserId;
    private String operationIssueName;
    private String testType;
    private String gameType;

    // settings attributes
    private int decimalPlacesCount = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_game_result_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGameSettingsByIntent();
        //initBestReactionTimeAsync();
        initAverageReactionTimeAsync();
        initGoNoGoGameFailuresAsync();
    }

    private void clearNotFinishedGames() {
        final Activity activity = this;
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                new ReactionGameManager(activity).deleteNotFinishedGames();
                return null;
            }
        }.execute();
    }

    private void initGoNoGoGameFailuresAsync() {
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

    private void updateInValidTrialCountByIdAsync(final Integer inValidTrialCount, final Activity activity) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                new ReactionGameManager(activity).updateInValidTrialCountById(reactionGameId, inValidTrialCount);
                return null;
            }
        }.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // delete old value
        UtilsRG.putString(UtilsRG.REACTION_GAME_ID, null, this);
    }

    //TODO: Use Shared preferences instead?
    /**
     * Sets game attributes by the intents before extras
     */
    private void getGameSettingsByIntent() {
        medicalUserId = getIntentMessage(StartGameSettings.EXTRA_MEDICAL_USER_ID);
        operationIssueName = getIntentMessage(StartGameSettings.EXTRA_OPERATION_ISSUE_NAME);
        testType = getIntentMessage(StartGameSettings.EXTRA_TEST_TYPE);
        gameType = getIntentMessage(StartGameSettings.EXTRA_GAME_TYPE);
        reactionGameId = getIntentMessage(StartGameSettings.EXTRA_REACTION_GAME_ID);
        UtilsRG.info(SingleGameResultView.class.getSimpleName() + " received user(" + medicalUserId + ") with operation name("
                + operationIssueName + "). Test type=" + testType + ", GameType=" + gameType + ",ReactionGameId=" + reactionGameId);

        Activity activity = this;
        if (activity != null) {
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
    }

    /**
     * Helper method to return intent messages
     */
    private String getIntentMessage(String messageKey) {
        Intent intent = getIntent();
        String message = intent.getStringExtra(messageKey);
        return message;
    }

    /**
     * get the minimum reaction time of the game and display result in a textview
     */
    private void initBestReactionTimeAsync() {
        final String minimumFilter = "MIN";

        new AsyncTask<Void, Void, Double>() {
            @Override
            protected Double doInBackground(Void... params) {
                TrialManager db = new TrialManager(getApplicationContext());
                return db.getFilteredReactionTimeByReactionGameId(reactionGameId, minimumFilter, true);
            }

            @Override
            protected void onPostExecute(final Double minReactionTime) {
                super.onPostExecute(minReactionTime);
                UtilsRG.info("Best Reaction Time was:" + minReactionTime + " s");
                //TextView bestReactionTimeText = (TextView) findViewById(R.id.single_game_result_view_best_reaction_time_text);
                //if (bestReactionTimeText != null) {
                //    bestReactionTimeText.setText(minReactionTime + " s");
                //}
            }
        }.execute();
    }

    /**
     * Get the average reactiontime of the game and display result i a text view
     */
    private void initAverageReactionTimeAsync() {
        final String averageFilter = "AVG";

        new AsyncTask<Void, Void, Double>() {
            @Override
            protected Double doInBackground(Void... params) {
                TrialManager db = new TrialManager(getApplicationContext());
                return db.getFilteredReactionTimeByReactionGameId(reactionGameId, averageFilter, true);
            }

            @Override
            protected void onPostExecute(final Double averageReactionTime) {
                super.onPostExecute(averageReactionTime);
                insertAverageReactionTimeAsync(averageReactionTime);
            }
        }.execute();
    }

    /**
     * Insert average reaction time and sets UI elements
     *
     * @param averageReactionTime
     */
    private void insertAverageReactionTimeAsync(final Double averageReactionTime) {
        UtilsRG.info("Average Reaction Time for reactionGame(" + reactionGameId + ") =" + averageReactionTime + " s");
        //TextView averageReactionTimeText = (TextView) findViewById(R.id.single_game_result_view_average_reaction_time_text);
        CircleView averageReactionTimeCircle = (CircleView) findViewById(R.id.circle);

        if (averageReactionTimeCircle != null) {
            String reactionTimeText = averageReactionTime + "";
            if (reactionTimeText.length() > (decimalPlacesCount + 1)) {
                reactionTimeText = reactionTimeText.substring(0, (decimalPlacesCount + 2));
            }
            //averageReactionTimeText.setText(reactionTimeText + " s");
            averageReactionTimeCircle.setTitleText(reactionTimeText + " " + getResources().getString(R.string.seconds));
            averageReactionTimeCircle.setSubtitleText(getResources().getString(R.string.average_reaction_time));
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... unusedParams) {
                    new ReactionGameManager(getApplicationContext()).updateAverageReactionTimeById(reactionGameId, averageReactionTime);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    clearNotFinishedGames();
                }
            }.execute();
        }
    }

    /**
     * on click the finish button --> go back to launcher
     */
    public void onFinishBtnClick(View view) {
        UtilsRG.info("User clicked: finish game");
        finish();
    }

    /**
     * On click the retry button --> go back to the activity before the game starts
     */
    public void onRetryBtnClick(View view) {
        UtilsRG.info("User does a second try");
        Intent intent = new Intent(this, StartGameSettings.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startGameActivityByTypes(medicalUserId, operationIssueName, gameType, testType);
    }

    private void startGameActivityByTypes(String medicalUserId, String operationIssueName, String gameType, String testType) {
        if (gameType != null && testType != null) {
            Intent intent = null;
            if (Type.GameTypes.GoGame.name().equalsIgnoreCase(gameType)) {
                intent = new Intent(this, GoGameView.class);
            } else if (Type.GameTypes.GoNoGoGame.name().equalsIgnoreCase(gameType)) {
                intent = new Intent(this, GoNoGoGameView.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }
    }

    public void onDeleteBtnClick(View view){
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

    private void deleteReactionGameAsync() {
        final Activity activity = this;
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                ReactionGame r = new ReactionGame();
                //r.setCreationDate(reactionGameId);
                UtilsRG.info("delete:" + reactionGameId);
                try {
                    Date reactionGameIdDate = UtilsRG.dateFormat.parse(reactionGameId);
                    r.setCreationDate(reactionGameIdDate);
                } catch (ParseException e) {
                    UtilsRG.error("Could not delete reactionGame:" + r);
                }
                return (int) new ReactionGameManager(activity).delete(r);
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
}
