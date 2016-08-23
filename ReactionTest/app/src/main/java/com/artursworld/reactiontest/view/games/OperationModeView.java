package com.artursworld.reactiontest.view.games;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.adapters.TimeLineAdapter;
import com.artursworld.reactiontest.controller.helper.Orientation;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.TimeLineModel;

import java.util.ArrayList;
import java.util.List;

public class OperationModeView extends AppCompatActivity {

    private long nextReactionTestcountDown = 0;

    // timeline
    private RecyclerView mRecyclerView;

    private TimeLineAdapter mTimeLineAdapter;

    private List<TimeLineModel> mDataList = new ArrayList<>();

    private Orientation mOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_mode_result_view);

        mOrientation = Orientation.vertical;
        setTitle("Vertical TimeLine");


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setHasFixedSize(true);
        }

        initView();
    }

    private void initView() {

        for (int i = 0; i < 2; i++) {
            TimeLineModel model = new TimeLineModel();
            model.setLabel("Random" + i);
            mDataList.add(model);
        }

        mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation);
        mRecyclerView.setAdapter(mTimeLineAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPreferances(this);
    }

    /**
     * Displays a count with a text in a textview
     *
     * @param countDown_sec the seconds to wait
     * @param prefixText    the text shown right to the countdown
     * @param textView      the textView where to display the countdown
     */
    private void runNextReactionTestCountDown(long countDown_sec, final String prefixText, final TextView textView) {
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

    /*
* Loads some settings from shared pereferances
*/
    private void loadPreferances(final Activity activity) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String countDownInSecondsKey = getResources().getString(R.string.operation_mode_next_reaction_test_countdown);
                nextReactionTestcountDown = UtilsRG.getIntByKey(countDownInSecondsKey, activity, 5) * 60;
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                TextView countDownTextView = (TextView) findViewById(R.id.operation_mode_next_game_estimated_in_text);
                String estimatedTime = getResources().getString(R.string.next_game_estimated_in);
                runNextReactionTestCountDown(nextReactionTestcountDown, estimatedTime + ": ", countDownTextView);
            }
        }.execute();
    }

    /**
     * On click the retry button --> go back to the activity before the game starts
     *
     * @param view
     */
    public void onRetryBtnClick(View view) {
        Intent intent = new Intent(this, StartGameSettings.class);
        startActivity(intent);
    }
}
