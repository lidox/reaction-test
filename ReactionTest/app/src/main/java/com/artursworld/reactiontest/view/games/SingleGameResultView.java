package com.artursworld.reactiontest.view.games;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.artursworld.reactiontest.model.persistence.manager.TrialManager;
import com.artursworld.reactiontest.view.LauncherView;

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
        initBestReactionTimeAsync();
        initAverageReactionTimeAsync();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // delete old value
        UtilsRG.putString(UtilsRG.REACTION_GAME_ID, null, this);
    }

    /**
     * on click the finish button --> go back to launcher
     */
    public void onFinishBtnClick(View view) {
        UtilsRG.info("User clicked: finish game");
        Intent intent = new Intent(this, LauncherView.class);
        startActivity(intent);
    }

    /**
     * On click the retry button --> go back to the activity before the game starts
     */
    public void onNewTryBtnClick(View view) {
        UtilsRG.info("User does a second try");
        Intent intent = new Intent(this, StartGameSettings.class);
        startActivity(intent);
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
        String minimumfilter = "MIN";
        new TrialManager.getFilteredReactionTimeByReactionGameIdAsync(new TrialManager.AsyncResponse() {

            @Override
            public void getFilteredReactionTimeByReactionGameIdAsync(double reactionTime) {
                UtilsRG.info("Best Reaction Time was:" + reactionTime + " s");
                TextView bestReactionTimeText = (TextView) findViewById(R.id.single_game_result_view_best_reaction_time_text);
                if (bestReactionTimeText != null) {
                    bestReactionTimeText.setText(reactionTime + " s");
                }
            }
        }, getApplicationContext()).execute(reactionGameId, minimumfilter);
    }

    /**
     * Get the average reactiontime of the game and display result i a text view
     */
    private void initAverageReactionTimeAsync() {
        String averagefilter = "AVG";
        new TrialManager.getFilteredReactionTimeByReactionGameIdAsync(new TrialManager.AsyncResponse() {

            @Override
            public void getFilteredReactionTimeByReactionGameIdAsync(final double reactionTime) {
                UtilsRG.info("Average Reaction Time was:" + reactionTime + " s");
                TextView averageReactionTimeText = (TextView) findViewById(R.id.single_game_result_view_average_reaction_time_text);
                if (averageReactionTimeText != null) {
                    String reationTimeText = reactionTime + "";
                    if (reationTimeText.length() > (decimalPlacesCount + 1)) {
                        reationTimeText = reationTimeText.substring(0, (decimalPlacesCount + 2));
                    }
                    averageReactionTimeText.setText(reationTimeText + " s");
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... unusedParams) {
                            new ReactionGameManager(getApplicationContext()).updateAverageReactionTimeById(reactionGameId, reactionTime);
                            return null;
                        }
                    }.execute();

                }

            }

        }, getApplicationContext()).execute(reactionGameId, averagefilter);
    }
}
