package com.artursworld.reactiontest.view.games;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.GameStatus;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.view.UserManagement;

import java.util.Random;

public class GoGameView extends AppCompatActivity {

    private Activity activity;
    private GameStatus statusOfGame;
    private TextView countDownText;

    private int countDown_sec = 4;
    private int minWaitTimeBeforeGameStarts_sec = 2;
    private int maxWaitTimeBeforeGameStarts_sec = 4;
    private int usersMaxAcceptedReactionTime_sec = 5;

    private long startTimeOfGame;
    private long stopTimeOfGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_game);
        activity = this;
        statusOfGame = GameStatus.WAITING;

        UtilsRG.info("received message="+getIntentMessage(UserManagement.EXTRA_MESSAGE));

        hideActionBar(getSupportActionBar());
        setBackGroundColor(activity, R.color.goGameBlue);
        runCountDown();
    }

    private void runCountDown() {
        countDownText = (TextView) findViewById(R.id.gogamecountdown);
        new CountDownTimer(countDown_sec * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                if(countDownText != null)
                    countDownText.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                onCountDownFinish();
            }
        }.start();
    }

    private void onCountDownFinish() {
        waitAndChangeStatusToClick(minWaitTimeBeforeGameStarts_sec * 1000, maxWaitTimeBeforeGameStarts_sec * 1000);
        if(countDownText != null)
            countDownText.setText(R.string.attention);
    }

    private String getIntentMessage(String messageKey){
        Intent intent = getIntent();
        String message = intent.getStringExtra(messageKey);
        return message;
    }

    private void hideActionBar(ActionBar actionBar){
        actionBar.hide();
    }

    private void setBackGroundColor(Activity activity, int colorId){
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
        UtilsRG.info("Now user should tap on phone. It's time!");
        if(countDownText != null)
            countDownText.setText(R.string.click);
        setBackGroundColor(activity, R.color.goGameGreen);
        this.startTimeOfGame = System.currentTimeMillis();
        statusOfGame = GameStatus.CLICK;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            //String text = "x = " + event.getX() + ", y = " + event.getY();
            //Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            if(statusOfGame == GameStatus.CLICK){
                onCorrectTouch();
            }
            else{
                UtilsRG.info("to early");
            }
        }
        return super.onTouchEvent(event);
    }

    private void onCorrectTouch() {
        this.stopTimeOfGame = System.currentTimeMillis();
        double usersReactionTime = (this.stopTimeOfGame - this.startTimeOfGame) /  1000.0;

        setBackGroundColor(this, R.color.goGameBlue);
        statusOfGame = GameStatus.WAITING;
        boolean userHasDoneThreeTrials = false;

        if(usersMaxAcceptedReactionTime_sec < usersReactionTime){
            UtilsRG.info("User was to slow touching on the screen.");
            runCountDown();
        }
        else if(!userHasDoneThreeTrials){
            UtilsRG.info("User touched at correct moment.");
            //TODO: add trial to trialList
            runCountDown();
        }
        else{
            UtilsRG.info("User finished the GO-Game seuccessfully.");
            //TODO: add trial to trialList
            // TODO: go to next view
        }

        Toast.makeText(this, usersReactionTime + " s", Toast.LENGTH_LONG).show();
    }
}
