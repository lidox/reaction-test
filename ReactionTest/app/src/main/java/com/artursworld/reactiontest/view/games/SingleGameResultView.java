package com.artursworld.reactiontest.view.games;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.persistence.manager.TrialManager;

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
        initReactionTimesAsync();
    }

    public void onFinishBtnClick(View view){
        UtilsRG.info("User finished game");
    }

    public void onRetryBtnClick(View view){
        UtilsRG.info("User does a second try");
    }

    private void getGameSettingsByIntent() {
        medicalUserId = getIntentMessage(StartGameSettings.EXTRA_MEDICAL_USER_ID);
        operationIssueName = getIntentMessage(StartGameSettings.EXTRA_OPERATION_ISSUE_NAME);
        testType = getIntentMessage(StartGameSettings.EXTRA_TEST_TYPE);
        gameType = getIntentMessage(StartGameSettings.EXTRA_GAME_TYPE);
        reactionGameId = getIntentMessage(StartGameSettings.EXTRA_REACTION_GAME_ID);
        UtilsRG.info(SingleGameResultView.class.getSimpleName() +" received user("+medicalUserId+") with operation name("
                +operationIssueName+"). Test type="+testType+ ", GameType="+gameType+",ReactionGameId="+reactionGameId);
    }

    private String getIntentMessage(String messageKey){
        Intent intent = getIntent();
        String message = intent.getStringExtra(messageKey);
        return message;
    }

    private void initReactionTimesAsync() {
        new TrialManager.getBestReactionTimeByReactionGameIdAsync(new TrialManager.AsyncResponse(){

            @Override
            public void getBestReactionTimeByReactionGameIdAsync(double bestReactionTime) {
                UtilsRG.info("Best Reaction Time was:" + bestReactionTime + " s");
            }

        }, getApplicationContext()).execute(reactionGameId);
    }

}
