package com.artursworld.reactiontest.view.games;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.GameStatus;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.artursworld.reactiontest.model.persistence.manager.TrialManager;

import org.slf4j.helpers.Util;

import java.util.Date;

public class GoNoGoGameView extends AppCompatActivity {

    // by the app user selected attributes
    private String medicalUserId;
    private String operationIssueName;
    private String testType;
    private String gameType;

    // game attributes
    private String reactionGameId;
    private GameStatus currentGameStatus;
    private long startTimeOfGame_millis;
    private long stopTimeOfGame_millis;
    private boolean toggle = false;
    private int tryCounter = 0;

    // game settings
    private int minWaitTimeBeforeGameStartInSeconds = 1;
    private int maxWaitTimeBeforeGameStartsInSeconds = 2;
    private int usersMaxAcceptedReactionTime_sec = 5;
    private int countDown_sec = 4;
    private int triesPerGameCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_no_go_game_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UtilsRG.info("onResume " + GoNoGoGameView.class.getSimpleName());

        // init attributes
        initSelectedGameAttributes();
        initReactionGameId(new Date());

        // init game UI stuff
        setGameStatusAndDisplayUIElements(GameStatus.WAITING);

        // startGame
        runCountDownAndStartGame(countDown_sec);
    }

    /**
     * Initializes by the user selected game attributes
     */
    private void initSelectedGameAttributes() {
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
     * Creates a new reaction game via database, if not created yet
     *
     * @param date the date timestamp equals the id (primary key) of the reaction game
     */
    private void initReactionGameId(Date date) {
        if (reactionGameId == null) {
            reactionGameId = UtilsRG.dateFormat.format(date);
            if ((getApplicationContext() != null) && (reactionGameId != null)) {
                ReactionGameManager db = new ReactionGameManager(getApplicationContext());
                db.insertReactionGameByOperationIssueNameAsync(reactionGameId, operationIssueName, gameType, testType);
                UtilsRG.putString(UtilsRG.REACTION_GAME_ID, reactionGameId, this);
            }
        }
    }

    /**
     * Sets game status and displays corresponding UI Elements for the user
     *
     * @param gameStatus the current game status
     */
    public void setGameStatusAndDisplayUIElements(GameStatus gameStatus) {
        this.currentGameStatus = gameStatus;
        if (gameStatus == GameStatus.WAITING) {
            UtilsRG.setBackgroundColor(this, R.color.colorPrimary);
        }
    }

    /**
     * Displays a count down and after that starts game
     *
     * @param countDown_sec the count down in seconds to wait before game start
     */
    private void runCountDownAndStartGame(long countDown_sec) {
        final TextView countDownText = (TextView) findViewById(R.id.gonogogamecountdown);
        new CountDownTimer((countDown_sec + 1) * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long countdownNumber = (millisUntilFinished / 1000) - 1;

                if (countDownText != null) {
                    if (countdownNumber != 0) {
                        String countdownNumberAsText = "" + countdownNumber;
                        countDownText.setText(countdownNumberAsText);
                    } else {
                        countDownText.setText(R.string.attention);
                    }
                }
            }

            public void onFinish() {
                onStartGame(minWaitTimeBeforeGameStartInSeconds, maxWaitTimeBeforeGameStartsInSeconds);
            }
        }.start();
    }

    /**
     * Waits some seconds and executes onChangeStatusToClick
     *
     * @param minWaitTimeInSeconds
     * @param maxWaitTimeInSeconds
     */
    private void onStartGame(int minWaitTimeInSeconds, int maxWaitTimeInSeconds) {
        UtilsRG.info(Type.GameTypes.GoNoGoGame.name() + " has been started now!");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                onDisplayColorChanged();
            }
        }, UtilsRG.getRandomNumberInRange((minWaitTimeInSeconds * 1000), (maxWaitTimeInSeconds * 1000)));
    }

    private void onDisplayColorChanged() {
        TextView countDownText = (TextView) findViewById(R.id.gonogogamecountdown);
        // random 1 oder 0
        boolean isWrongColor = UtilsRG.getRandomNumberInRange(0, 1) == 0;
        if (isWrongColor) {
            UtilsRG.info("Wrong color. The user should not hit screen.");
            this.currentGameStatus = GameStatus.WRONG_COLOR;

            UtilsRG.setBackgroundColor(this, getToggledBackGroundColor());
            onStartGame(minWaitTimeBeforeGameStartInSeconds, maxWaitTimeBeforeGameStartsInSeconds);
        } else {
            this.startTimeOfGame_millis = System.currentTimeMillis();
            UtilsRG.info("Now the user should hit screen.");

            if (countDownText != null)
                countDownText.setText(R.string.click);

            UtilsRG.setBackgroundColor(this, R.color.goGameGreen);
            this.currentGameStatus = GameStatus.CLICK;
        }
    }

    /**
     * Toggle between two colors
     *
     * @return the toggled color
     */
    private int getToggledBackGroundColor() {
        // toggle between colors
        int backGroundColor;
        if (toggle) {
            backGroundColor = R.color.colorPrimaryLight;
            toggle = !toggle;
        } else {
            backGroundColor = R.color.colorAccentLight;
            toggle = !toggle;
        }
        UtilsRG.info("selected color = "+backGroundColor);
        return backGroundColor;
    }

    /*
* called than a user touches the display
*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.stopTimeOfGame_millis = System.currentTimeMillis();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            double usersReactionTime = (this.stopTimeOfGame_millis - this.startTimeOfGame_millis) / 1000.0;
            if (currentGameStatus == GameStatus.CLICK) {
                setGameStatusAndDisplayUIElements(GameStatus.WAITING);
                if (usersMaxAcceptedReactionTime_sec < usersReactionTime) {
                    UtilsRG.info("User was to slow touching on the screen.");
                    runCountDownAndStartGame(this.countDown_sec);
                } else {
                    onCorrectTouch(usersReactionTime);
                }
            } else {
                //TODO: prevent user taps like a the tap master
                UtilsRG.info("User hit the screen at the wrong status(" + this.currentGameStatus + ")");
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * User hit the screen at the correct time
     *
     * @param usersReactionTime the users reaction time
     */
    private void onCorrectTouch(double usersReactionTime) {
        tryCounter++;
        boolean userFinishedGameSuccessfully = (tryCounter == triesPerGameCount);
        TrialManager trialManager = new TrialManager(this);
        if (trialManager != null)
            trialManager.insertTrialtoReactionGameAsync(reactionGameId, true, usersReactionTime);
        UtilsRG.info("User touched at correct moment. ReactionGameId=(" + reactionGameId + ") and reationTime(" + usersReactionTime + ")");

        if (!userFinishedGameSuccessfully) {
            runCountDownAndStartGame(this.countDown_sec);
            // TODO: quick results view?
        } else {
            UtilsRG.info("User finished the GO-No-Go-Game successfully.");
            Intent intent = new Intent(this, SingleGameResultView.class);
            startActivity(intent);
        }

        Toast.makeText(this, usersReactionTime + " s", Toast.LENGTH_LONG).show();
        UtilsRG.info(usersReactionTime + " s");
    }

}
