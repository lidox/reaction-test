package com.artursworld.reactiontest.view.games;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.GameStatus;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.artursworld.reactiontest.model.persistence.manager.TrialManager;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.Date;

/*
* Displays the go game view and tracks user interactions in background
*/
public class GoGameView extends AppCompatActivity {

    // selected attributes for the game
    private TrialManager trialManager;
    private String reactionGameId;
    private String medicalUserId;
    private String operationIssueName;
    private String testType;
    private String gameType;

    private Activity activity;
    private GameStatus currentGameStatus;
    private TextView countDownText;

    // go game configurations 
    private int countDown_sec = 4;
    private int minWaitTimeBeforeGameStarts_sec = 2;
    private int maxWaitTimeBeforeGameStarts_sec = 4;
    private int usersMaxAcceptedReactionTime_sec = 5;
    private int triesPerGameCount;
    private int tryCounter = 0;

    private long startTimeOfGame_millis;
    private long stopTimeOfGame_millis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_game);
        activity = this;
        loadPreferances(activity);
        getGameSettingsByIntent();
        insertReactionGameAsync();
        hideActionBar(getSupportActionBar());
        onChangeStatusToWaiting();
        runCountDownBeforeStartGame(this.countDown_sec);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Inserts a new reaction game to database
     */
    private void insertReactionGameAsync() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (trialManager == null)
                    trialManager = new TrialManager(getApplicationContext());

                if (reactionGameId == null) {
                    reactionGameId = UtilsRG.dateFormat.format(new Date());
                    new ReactionGameManager(getApplicationContext()).insertReactionGameByOperationIssueNameAsync(reactionGameId, operationIssueName, gameType, testType);
                }
                return null;
            }

        }.execute();
    }

    /*
    * Sets the game settings by the intent before
    */
    private void getGameSettingsByIntent() {
        /*medicalUserId = getIntentMessage(StartGameSettings.EXTRA_MEDICAL_USER_ID);
        operationIssueName = getIntentMessage(StartGameSettings.EXTRA_OPERATION_ISSUE_NAME);
        testType = getIntentMessage(StartGameSettings.EXTRA_TEST_TYPE);
        gameType = getIntentMessage(StartGameSettings.EXTRA_GAME_TYPE);
        */

        medicalUserId = UtilsRG.getStringByKey(UtilsRG.MEDICAL_USER, this);
        operationIssueName = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, this);
        testType = UtilsRG.getStringByKey(UtilsRG.TEST_TYPE, this);
        gameType = UtilsRG.getStringByKey(UtilsRG.GAME_TYPE, this);
        UtilsRG.info("Received user(" + medicalUserId + ") with operation name(" + operationIssueName + "). Test type=" + testType + ", GameType=" + gameType);
    }

    /*
    * Displays a countdown before the user can click
    */
    private void runCountDownBeforeStartGame(final long countDown_sec) {
        countDownText = (TextView) findViewById(R.id.gogamecountdown);
        new CountDownTimer((countDown_sec + 1) * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long countdownNumber = (millisUntilFinished / 1000) - 1;

                if (countDownText != null && countdownNumber != 0) {
                    String countdownNumberAsText = "" + countdownNumber;
                    countDownText.setText(countdownNumberAsText);
                } else if (countDownText != null) {
                    countDownText.setText(R.string.attention);
                }

            }

            public void onFinish() {
                onCountDownFinish();
            }
        }.start();
    }

    /*
    * Waits a random time in a range before user can click on device to test reaction
    */
    private void onCountDownFinish() {
        waitAndChangeStatusToClick(minWaitTimeBeforeGameStarts_sec * 1000, maxWaitTimeBeforeGameStarts_sec * 1000);
    }


    //TODO: no needed anymore?
    /*
    * Hides the actionbar
    */
    private void hideActionBar(ActionBar actionBar) {
        if (actionBar != null) {
            actionBar.hide();
        }

    }


    /*
    * Waits a random number before status changes
    */
    private void waitAndChangeStatusToClick(int minWaitTimeInMilliSeconds, int maxWaitTimeInMilliSeconds) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                onChangeStatusToClick();
            }
        }, UtilsRG.getRandomNumberInRange(minWaitTimeInMilliSeconds, maxWaitTimeInMilliSeconds));
    }

    /*
    * The background color changed to the user can click on device
    */
    private void onChangeStatusToClick() {
        this.startTimeOfGame_millis = android.os.SystemClock.uptimeMillis();
        UtilsRG.info("Now the user should hit screen.");
        if (countDownText != null)
            countDownText.setText(R.string.click);
        UtilsRG.setBackgroundColor(activity, R.color.goGameGreen);
        currentGameStatus = GameStatus.CLICK;
    }

    /*
    * This status is used to wait until user can click again
    */
    private void onChangeStatusToWaiting() {
        UtilsRG.setBackgroundColor(this, R.color.colorPrimary);
        currentGameStatus = GameStatus.WAITING;
    }

    /*
    * called than a user touches the display
    */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.stopTimeOfGame_millis = android.os.SystemClock.uptimeMillis();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            checkTouchEvent();
        }
        return super.onTouchEvent(event);
    }

    /*
    * Validation for a users touch event
    */
    private void checkTouchEvent() {
        double usersReactionTime = (this.stopTimeOfGame_millis - this.startTimeOfGame_millis) / 1000.0;

        if (currentGameStatus == GameStatus.CLICK) {
            onChangeStatusToWaiting();
            if (usersMaxAcceptedReactionTime_sec < usersReactionTime) {
                UtilsRG.info("User was to slow touching on the screen.");
                runCountDownBeforeStartGame(this.countDown_sec);
            } else {
                onCorrectTouch(usersReactionTime);
            }
        } else {
            //TODO: prevent user taps like a the tap master
            UtilsRG.info("User hit the screen to early.");
        }
    }

    /*
    * Called than user touch at right time
    */
    private void onCorrectTouch(double usersReactionTime) {
        tryCounter++;
        boolean userFinishedGameSuccessfully = (tryCounter == triesPerGameCount);
        if (trialManager == null)
            trialManager = new TrialManager(this.getApplicationContext());
        if (trialManager != null)
            trialManager.insertTrialtoReactionGameAsync(reactionGameId, true, usersReactionTime);
        UtilsRG.info("User touched at correct moment. ReactionGameId=(" + reactionGameId + ") and reationTime(" + usersReactionTime + ")");

        if (!userFinishedGameSuccessfully) {
            runCountDownBeforeStartGame(this.countDown_sec);
        } else {
            UtilsRG.info("User finished the GO-Game seuccessfully.");
            initSingleGameResultView();
        }

        UtilsRG.info("usersReactionTime=" + usersReactionTime + " s");
        UtilsRG.info(usersReactionTime + " s");
    }

    /*
    * open new intent if game is finished
    */
    private void initSingleGameResultView() {
        UtilsRG.info("Init Intent by User(" + medicalUserId + ") with operation name(" + operationIssueName + "). Test type=" + testType + ", GameType=" + gameType);
        Intent intent = new Intent(this, SingleGameResultView.class);
        intent.putExtra(StartGameSettings.EXTRA_MEDICAL_USER_ID, medicalUserId);
        intent.putExtra(StartGameSettings.EXTRA_OPERATION_ISSUE_NAME, operationIssueName);
        intent.putExtra(StartGameSettings.EXTRA_GAME_TYPE, gameType);
        intent.putExtra(StartGameSettings.EXTRA_TEST_TYPE, testType);
        intent.putExtra(StartGameSettings.EXTRA_REACTION_GAME_ID, reactionGameId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    //TODO: use util function
    /*
    * Loads some settings from shared pereferances
    */
    private void loadPreferances(final Activity activity) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
                triesPerGameCount = mySharedPreferences.getInt("go_game_tries_per_game", 1);
                String countdownCountKey = getResources().getString(R.string.go_game_countdown_count);
                countDown_sec = mySharedPreferences.getInt(countdownCountKey, 1);

                minWaitTimeBeforeGameStarts_sec = 1;
                String maxRandomWaitTimeBeforeGameStartsKey = getResources().getString(R.string.go_game_max_random_waiting_time);
                maxWaitTimeBeforeGameStarts_sec = mySharedPreferences.getInt(maxRandomWaitTimeBeforeGameStartsKey, 2);
                return null;
            }
        }.execute();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            stopTimeOfGame_millis = event.getDownTime();
            double usersReactionTime = (event.getDownTime() - startTimeOfGame_millis) / 1000.0;
            UtilsRG.info("event.getDownTime(): " + usersReactionTime);
            checkTouchEvent();
        }
        return true;
    }
}
