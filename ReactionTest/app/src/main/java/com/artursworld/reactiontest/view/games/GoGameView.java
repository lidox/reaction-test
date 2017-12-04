package com.artursworld.reactiontest.view.games;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.GameStatus;
import com.artursworld.reactiontest.controller.util.ClickCountTester;
import com.artursworld.reactiontest.controller.util.Global;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.artursworld.reactiontest.model.persistence.manager.TrialManager;

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
    private RelativeLayout backgroundImage;

    // go game configurations
    private int usersMaxAcceptedReactionTime_sec = 5;
    private int satisfactionTime = 500;
    private int hitTime = 500;
    private int countDown_sec = 4;
    private int minWaitTimeBeforeGameStarts_sec = 2;
    private int maxWaitTimeBeforeGameStarts_sec = 4;
    private int triesPerGameCount;
    private int tryCounter = 0;

    private ClickCountTester clickCountTester = null;
    private boolean isCountDownTimerCanceled;
    private CountDownTimer countDownTimer = null;

    private long startTimeOfGame_millis;
    private long stopTimeOfGame_millis;
    private long uiTimeCollapsed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_game);
        activity = this;
        currentGameStatus = GameStatus.WAITING;
        loadPreferences(activity);
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
        UtilsRG.setBackgroundColor(activity, R.color.goGameBlack);
        currentGameStatus = GameStatus.WAITING;
        countDownTimer = new CountDownTimer((countDown_sec + 1) * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long countdownNumber = (millisUntilFinished / 1000) - 1;

                backgroundImage = (RelativeLayout) findViewById(R.id.go_game_background);
                countDownText = (TextView) findViewById(R.id.gogamecountdown);
                if (countDownText != null && countdownNumber != 0) {
                    String countdownNumberAsText = "" + countdownNumber;
                    countDownText.setText(countdownNumberAsText);
                } else if (countDownText != null) {
                    showAttention();
                }
            }

            public void onFinish() {
                onCountDownFinish();
            }
        }.start();
        isCountDownTimerCanceled = false;
    }

    /*
    * Waits a random time in a range before user can click on device to test reaction
    */
    private void onCountDownFinish() {
        waitAndChangeStatusToClick(minWaitTimeBeforeGameStarts_sec * 1000, maxWaitTimeBeforeGameStarts_sec * 1000);
    }


    /*
    * Waits a random number before status changes
    */
    private void waitAndChangeStatusToClick(int minWaitTimeInMilliSeconds, int maxWaitTimeInMilliSeconds) {
        showAttention();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (!isCountDownTimerCanceled)
                    onChangeStatusToClick();
            }
        }, UtilsRG.getRandomNumberInRange(minWaitTimeInMilliSeconds, maxWaitTimeInMilliSeconds));
    }

    /*
    * The background color changed to the user can click on device
    */
    private void onChangeStatusToClick() {
        this.startTimeOfGame_millis = android.os.SystemClock.uptimeMillis();
        clickCountTester = new ClickCountTester();
        UtilsRG.info("Now the user should hit screen.");
        showClickNowUI();
    }

    /*
    * This status is used to wait until user can click again
    */
    private void onChangeStatusToWaiting() {
        UtilsRG.setBackgroundColor(this, R.color.goGameBlack);
        currentGameStatus = GameStatus.WAITING;
    }

    /**
     * Validation for a users touch event
     *
     * @param usersReactionTime the reaction time of the user
     */
    private void checkTouchEvent(double usersReactionTime) {
        UtilsRG.info("usersReactionTime: " + usersReactionTime);
        if (currentGameStatus == GameStatus.CLICK) {
            onChangeStatusToWaiting();
            if (usersMaxAcceptedReactionTime_sec < usersReactionTime) {
                UtilsRG.info("User was to slow touching on the screen.");
                runCountDownBeforeStartGame(this.countDown_sec);
            } else {
                onCorrectTouch(usersReactionTime);
            }
        }
    }

    /*
    * Called than user touch at right time
    */
    private void onCorrectTouch(final double usersReactionTime) {
        UtilsRG.info("users reaction time=" + usersReactionTime + " s");

        tryCounter++;

        if (trialManager == null)
            trialManager = new TrialManager(this.getApplicationContext());

        trialManager.insertTrialtoReactionGameAsync(reactionGameId, true, usersReactionTime);
        UtilsRG.info("User touched at correct moment. ReactionGameId=(" + reactionGameId + ") and reationTime(" + usersReactionTime + ")");
        final long countDown = this.countDown_sec;

        showHitWithSuccess();

        // wait some time before going on
        new Handler().postDelayed(new Runnable() {
            public void run() {
                showSatisfyingOutro();

                // wait some time before going on
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        boolean userFinishedGameSuccessfully = (tryCounter == triesPerGameCount);
                        repeatGameOrFinish(userFinishedGameSuccessfully, countDown);
                    }
                }, satisfactionTime);
            }
        }, hitTime);
    }

    /**
     * Repeats the game if the game is not finished. Otherwise finishes the game
     *
     * @param userFinishedGameSuccessfully is game already finished
     * @param countDown                    the count down time to display
     */
    private void repeatGameOrFinish(boolean userFinishedGameSuccessfully, long countDown) {
        if (!userFinishedGameSuccessfully) {
            runCountDownBeforeStartGame(countDown);
        } else {
            UtilsRG.info("User finished the GO-Game successfully.");
            initSingleGameResultView();
        }
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

    /*
    * Loads some settings from shared pereferances
    */
    private void loadPreferences(final Activity activity) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
                triesPerGameCount = Global.getTryCountPerGame(); //mySharedPreferences.getInt("go_game_tries_per_game", 1);
                String countdownCountKey = getResources().getString(R.string.go_game_countdown_count);
                countDown_sec = mySharedPreferences.getInt(countdownCountKey, 1);
                minWaitTimeBeforeGameStarts_sec = 1;
                String maxRandomWaitTimeBeforeGameStartsKey = getResources().getString(R.string.go_game_max_random_waiting_time);
                maxWaitTimeBeforeGameStarts_sec = mySharedPreferences.getInt(maxRandomWaitTimeBeforeGameStartsKey, 2);

                getGameSettingsByIntent();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                insertReactionGameAsync();
                onChangeStatusToWaiting();
                runCountDownBeforeStartGame(countDown_sec);
            }
        }.execute();
    }

    /**
     * Stop attention time and display warning to the user.
     * The user should not click too often.
     */
    private void showUserClickedTooOftenWarning() {
        UtilsRG.info("userHasClickedTooOften");

        showTooEarlyClickedUI();

        if (countDownTimer != null) {
            isCountDownTimerCanceled = true;
            countDownTimer.cancel();
        }

        Handler handler = new Handler();
        int displayWarningDuration_sec = 2 * 1000;
        handler.postDelayed(new Runnable() {
            public void run() {
                runCountDownBeforeStartGame(countDown_sec);
            }
        }, displayWarningDuration_sec);
    }

    /**
     * Called to process key event where user click on the display
     *
     * @param event event The key event fired
     * @return Return true if this event was consumed. Otherwise false.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        double usersReactionTime = (event.getDownTime() - this.startTimeOfGame_millis - uiTimeCollapsed) / 1000.0;
        this.stopTimeOfGame_millis = event.getDownTime();

        String message = "RT TIME: " + stopTimeOfGame_millis + " - " + this.startTimeOfGame_millis + " - " + uiTimeCollapsed + " = " + usersReactionTime;
        UtilsRG.info(message);
        // TastyToast.makeText(getApplicationContext(), message, TastyToast.LENGTH_LONG, TastyToast.DEFAULT);

        UtilsRG.info("user click on the display at: " + this.stopTimeOfGame_millis);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            validateReactionTime(usersReactionTime);
            return true;
        }

        return super.onTouchEvent(event);
    }

    /**
     * Called to process key event where user click on the selfie-stick button
     * +
     *
     * @param event The key event fired
     * @return Return true if this event was consumed.
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        double usersReactionTime = (event.getDownTime() - this.startTimeOfGame_millis - uiTimeCollapsed) / 1000.0;
        this.stopTimeOfGame_millis = event.getDownTime();
        UtilsRG.info("selfie-stick button clicked at: " + this.stopTimeOfGame_millis);

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                validateReactionTime(usersReactionTime);
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }


    /**
     * On click on screen.
     *
     * @param usersReactionTime the users reaction time
     */
    private void validateReactionTime(double usersReactionTime) {
        if (clickCountTester == null)
            clickCountTester = new ClickCountTester();
        if (clickCountTester.checkClickCount(3, 3)) {
            showUserClickedTooOftenWarning();
        } else {
            checkTouchEvent(usersReactionTime);
        }
    }

    @Override
    public void onBackPressed() {

        UtilsRG.info("Back has been pressed during the reaction testing. Why do people do this? ... So delete created reaction game! :(");
        deleteReactionGameAsync(reactionGameId);

        super.onBackPressed();
    }

    private void deleteReactionGameAsync(final String reactionGameId) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (reactionGameId != null) {
                    new ReactionGameManager(getApplicationContext()).deleteById(reactionGameId);
                }
                return null;
            }

        }.execute();
    }

    /**
     * Display 'attention' UI elements
     */
    private void showAttention() {
        setStateUIElements(R.string.attention, R.color.goGameBlack, GameStatus.WAITING);
    }

    /**
     * Display 'too early clicked' UI elements
     */
    private void showTooEarlyClickedUI() {
        setStateUIElements(R.string.too_early, R.color.colorAccentMiddle, GameStatus.WRONG_COLOR);
    }

    /**
     * Display 'click now!' UI elements
     */
    private void showClickNowUI() {
        setStateUIElements(R.string.click, R.color.goGameWhite, GameStatus.CLICK);
    }

    /**
     * Display successful 'hit' UI elements
     */
    private void showHitWithSuccess() {
        setStateUIElements(R.string.placeholder, R.color.goGameBlack, GameStatus.HIT);
    }

    /**
     * Display satisfying 'Outro' UI elements
     */
    private void showSatisfyingOutro() {
        setStateUIElements(R.string.placeholder, R.color.goGameBlack, GameStatus.SATISFACTION);
    }

    /**
     * Set UI elements for a specific state (e.g. 'attention state')
     *
     * @param textToShowId     text to display
     * @param colorToDisplayId background color to set
     * @param statusToSet      status to be set
     * @return the time collapsed for UI changes
     */
    private long setStateUIElements(final int textToShowId, final int colorToDisplayId, final GameStatus statusToSet) {
        long startTime = android.os.SystemClock.uptimeMillis();


        final GoGameView activity = this;

        final int[] imageIds = Global.getImageIdsBySelectedScenario();

        // change text
        if (countDownText != null)
            countDownText.setText(textToShowId);

        if (backgroundImage != null) {
            int scenarioImageIndex = statusToSet.ordinal();
            setBackground(activity, backgroundImage, imageIds[scenarioImageIndex]);
            // backgroundImage.setBackgroundResource(imageIds[scenarioImageIndex]);
        }

        // background color
        UtilsRG.setBackgroundColor(activity, colorToDisplayId);

        // set game status
        activity.currentGameStatus = statusToSet;

        this.uiTimeCollapsed = SystemClock.uptimeMillis() - startTime;
        return uiTimeCollapsed;
    }

    /**
     * Sets background image in a faster way
     *
     * @param context    the app context
     * @param view       the view to set the background
     * @param drawableId the id of the drawable
     */
    private void setBackground(Context context, View view, int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
        view.setBackground(bitmapDrawable);
    }

}
