package com.artursworld.reactiontest.view.games;

import android.content.Intent;
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

public class SingleGameResultView extends AppCompatActivity {

    private String reactionGameId;
    private String medicalUserId;
    private String operationIssueName;
    private String testType;
    private String gameType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_game_result_view);
        getGameSettingsByIntent();
        initBestReactionTimeAsync();
        initAverageReactionTimeAsync();
    }

    public void onFinishBtnClick(View view) {
        UtilsRG.info("User clicked: finish game");
        Intent intent = new Intent(this, LauncherView.class);
        startActivity(intent);
    }

    public void onRetryBtnClick(View view) {
        UtilsRG.info("User does a second try");
        Intent intent = new Intent(this, StartGameSettings.class);
        startActivity(intent);
    }

    private void getGameSettingsByIntent() {
        medicalUserId = getIntentMessage(StartGameSettings.EXTRA_MEDICAL_USER_ID);
        operationIssueName = getIntentMessage(StartGameSettings.EXTRA_OPERATION_ISSUE_NAME);
        testType = getIntentMessage(StartGameSettings.EXTRA_TEST_TYPE);
        gameType = getIntentMessage(StartGameSettings.EXTRA_GAME_TYPE);
        reactionGameId = getIntentMessage(StartGameSettings.EXTRA_REACTION_GAME_ID);
        UtilsRG.info(SingleGameResultView.class.getSimpleName() + " received user(" + medicalUserId + ") with operation name("
                + operationIssueName + "). Test type=" + testType + ", GameType=" + gameType + ",ReactionGameId=" + reactionGameId);
    }

    private String getIntentMessage(String messageKey) {
        Intent intent = getIntent();
        String message = intent.getStringExtra(messageKey);
        return message;
    }

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


    private void initAverageReactionTimeAsync() {
        String averagefilter = "AVG";
        new TrialManager.getFilteredReactionTimeByReactionGameIdAsync(new TrialManager.AsyncResponse() {

            @Override
            public void getFilteredReactionTimeByReactionGameIdAsync(double reactionTime) {
                UtilsRG.info("Average Reaction Time was:" + reactionTime + " s");
                TextView averageReactionTimeText = (TextView) findViewById(R.id.single_game_result_view_average_reaction_time_text);
                if (averageReactionTimeText != null) {
                    String reationTimeText = reactionTime + "";
                    if (reationTimeText.length() > 4) {
                        reationTimeText = reationTimeText.substring(0, 5);
                    }
                    averageReactionTimeText.setText(reationTimeText + " s");
                    new ReactionGameManager(getApplicationContext()).updateAverageReactionTimeById(reactionGameId, reactionTime);
                }

            }

        }, getApplicationContext()).execute(reactionGameId, averagefilter);
    }

}
