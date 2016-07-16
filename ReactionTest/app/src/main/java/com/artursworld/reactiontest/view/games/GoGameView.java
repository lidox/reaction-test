package com.artursworld.reactiontest.view.games;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.GameStatus;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.artursworld.reactiontest.model.persistence.manager.TrialManager;

import java.util.Date;
import java.util.Random;

public class GoGameView extends AppCompatActivity {

    private TrialManager trialManager;
    private String reactionGameId;
    private String medicalUserId;
    private String operationIssueName;
    private String testType;
    private String gameType;

    private Activity activity;
    private GameStatus currentGameStatus;
    private TextView countDownText;

    private int countDown_sec = 4;
    private int minWaitTimeBeforeGameStarts_sec = 2;
    private int maxWaitTimeBeforeGameStarts_sec = 4;
    private int usersMaxAcceptedReactionTime_sec = 5;
    private int triesPerGameCount = 3;

    private long startTimeOfGame_millis;
    private long stopTimeOfGame_millis;

    private int tryCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_game);
        getGameSettingsByIntent();

        if(activity == null)
            activity = this;

        if(trialManager == null)
            trialManager = new TrialManager(getApplicationContext());

        if(reactionGameId == null){
            reactionGameId = UtilsRG.dateFormat.format(new Date());
            new ReactionGameManager(getApplicationContext()).insertReactionGameByOperationIssueNameAsync(reactionGameId, operationIssueName, gameType, testType);
        }

        hideActionBar(getSupportActionBar());
        onChangeStatusToWaiting();





        runCountDownBeforeStartGame(this.countDown_sec);
    }

    private void getGameSettingsByIntent() {
        medicalUserId = getIntentMessage(StartGameSettings.EXTRA_MEDICAL_USER_ID);
        operationIssueName = getIntentMessage(StartGameSettings.EXTRA_OPERATION_ISSUE_NAME);
        testType = getIntentMessage(StartGameSettings.EXTRA_TEST_TYPE);
        gameType = getIntentMessage(StartGameSettings.EXTRA_GAME_TYPE);
        UtilsRG.info("Received user("+medicalUserId+") with operation name("+operationIssueName+"). Test type="+testType+ ", GameType="+gameType);
    }

    private void runCountDownBeforeStartGame(final long countDown_sec) {
        countDownText = (TextView) findViewById(R.id.gogamecountdown);
        new CountDownTimer((countDown_sec+1) * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long countdownNumber = (millisUntilFinished / 1000) -1 ;

                if(countDownText != null && countdownNumber != 0){
                    countDownText.setText("" + countdownNumber );
                }
                else if(countDownText != null){
                    countDownText.setText(R.string.attention);
                }

            }

            public void onFinish() {
                onCountDownFinish();
            }
        }.start();
    }

    private void onCountDownFinish() {
        waitAndChangeStatusToClick(minWaitTimeBeforeGameStarts_sec * 1000, maxWaitTimeBeforeGameStarts_sec * 1000);
    }

    private String getIntentMessage(String messageKey){
        Intent intent = getIntent();
        String message = intent.getStringExtra(messageKey);
        return message;
    }

    private void hideActionBar(ActionBar actionBar){
        if(actionBar != null){
            actionBar.hide();
        }

    }

    private void setBackgroundColor(Activity activity, int colorId){
        try {
            int color = ContextCompat.getColor(activity.getApplicationContext(), colorId);
            activity.getWindow().getDecorView().setBackgroundColor(color);
            UtilsRG.info("background color set to: " +color);
        }
        catch (Exception e){
            String message = "Could not set background color.";
            UtilsRG.error(message + "\n" + e.getLocalizedMessage());
        }
    }

    private int getRandomNumberInRange(int min, int max){
        Random r = new Random();
        int random =  r.nextInt(max - min + 1) + min;
        UtilsRG.info("Waiting random number=" +random);
        return random;
    }

    private void waitAndChangeStatusToClick(int minWaitTimeInMilliSeconds, int maxWaitTimeInMilliSeconds){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                onChangeStatusToClick();
            }
        }, getRandomNumberInRange(minWaitTimeInMilliSeconds,maxWaitTimeInMilliSeconds));
    }

    private void onChangeStatusToClick() {
        this.startTimeOfGame_millis = System.currentTimeMillis();
        UtilsRG.info("Now the user should hit screen.");
        if(countDownText != null)
            countDownText.setText(R.string.click);
        setBackgroundColor(activity, R.color.goGameGreen);
        currentGameStatus = GameStatus.CLICK;
    }

    private void onChangeStatusToWaiting() {
        setBackgroundColor(this, R.color.goGameBlue);
        currentGameStatus = GameStatus.WAITING;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.stopTimeOfGame_millis = System.currentTimeMillis();
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            checkTouchEvent();
        }
        return super.onTouchEvent(event);
    }

    private void checkTouchEvent() {
        double usersReactionTime = (this.stopTimeOfGame_millis - this.startTimeOfGame_millis) /  1000.0;

        if(currentGameStatus == GameStatus.CLICK){
            onChangeStatusToWaiting();
            if(usersMaxAcceptedReactionTime_sec < usersReactionTime){
                UtilsRG.info("User was to slow touching on the screen.");
                runCountDownBeforeStartGame(this.countDown_sec);
            }
            else{
                onCorrectTouch(usersReactionTime);
            }
        }
        else{
            //TODO: prevent user taps like a the tap master
            UtilsRG.info("User hit the screen to early.");
        }
    }

    private void onCorrectTouch(double usersReactionTime) {
        tryCounter ++;
        boolean userFinishedGameSuccessfully = (tryCounter == triesPerGameCount );
        trialManager.insertTrialtoReactionGameAsync(reactionGameId, true, usersReactionTime);
        UtilsRG.info("User touched at correct moment. ReactionGameId=("+reactionGameId+") and reationTime(" +usersReactionTime+")");

        if(!userFinishedGameSuccessfully){
            runCountDownBeforeStartGame(this.countDown_sec);
            // TODO: quick results view
        }
        else{
            UtilsRG.info("User finished the GO-Game seuccessfully.");
            initSingleGameResultView();
        }

        Toast.makeText(this, usersReactionTime + " s", Toast.LENGTH_LONG).show();
        UtilsRG.info(usersReactionTime + " s");
    }

    private void initSingleGameResultView() {
        UtilsRG.info("Init Intent by User("+medicalUserId+") with operation name("+operationIssueName+"). Test type="+testType+ ", GameType="+gameType);
        Intent intent = new Intent(this, SingleGameResultView.class);
        intent.putExtra(StartGameSettings.EXTRA_MEDICAL_USER_ID, medicalUserId);
        intent.putExtra(StartGameSettings.EXTRA_OPERATION_ISSUE_NAME, operationIssueName);
        intent.putExtra(StartGameSettings.EXTRA_GAME_TYPE, gameType);
        intent.putExtra(StartGameSettings.EXTRA_TEST_TYPE, testType);
        intent.putExtra(StartGameSettings.EXTRA_REACTION_GAME_ID, reactionGameId);
        startActivity(intent);
    }

}
