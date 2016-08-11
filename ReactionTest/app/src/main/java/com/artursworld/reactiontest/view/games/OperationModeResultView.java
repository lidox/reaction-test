package com.artursworld.reactiontest.view.games;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;

public class OperationModeResultView extends AppCompatActivity {

    // settings 5 min * 60 sec
    private long countDownInSeconds = 5 * 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_mode_result_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView countDownTextView = (TextView) findViewById(R.id.operation_mode_next_game_estimated_in_text);
        String estimatedTime = getResources().getString(R.string.next_game_estimated_in);
        runCountDown(countDownInSeconds, estimatedTime+": ", countDownTextView);
    }

    /**
     * Displays a count with a text in a textview
     *
     * @param countDown_sec the seconds to wait
     * @param prefixText    the text shown right to the countdown
     * @param textView      the textView where to display the countdown
     */
    private void runCountDown(long countDown_sec, final String prefixText, final TextView textView) {
        if ((textView != null) && (countDown_sec > 0)) {
            new CountDownTimer((countDown_sec + 1) * 1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    long countdownNumber = (millisUntilFinished / 1000) - 1;


                    if (countdownNumber != 0) {
                        long minutes = (countdownNumber % 3600) / 60;
                        long seconds = countdownNumber % 60;
                        String timeString = String.format("%02d:%02d", minutes, seconds);
                        String countdownNumberAsText = prefixText + timeString;
                        textView.setText(countdownNumberAsText);
                    } else {
                        textView.setText(R.string.make_a_new_try);
                    }

                }

                public void onFinish() {
                    //TODO: vibrate, make noise and wackel dackel button
                }
            }.start();
        }
    }

    /**
     * On click the retry button --> go back to the activity before the game starts
     * @param view
     */
    public void onRetryBtnClick(View view) {
        Intent intent = new Intent(this, StartGameSettings.class);
        startActivity(intent);
    }
}
