package com.artursworld.reactiontest.view.games;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
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
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Eases.EaseType;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.ClickEffectType;
import com.nightonke.boommenu.Types.DimType;
import com.nightonke.boommenu.Types.OrderType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

import java.util.ArrayList;
import java.util.List;

public class OperationModeView extends AppCompatActivity {

    private long nextReactionTestcountDown = 0;

    // timeline
    private RecyclerView recyclerTimeLineView;
    private TimeLineAdapter timeLineAdapter;
    private List<TimeLineModel> timeLineList = new ArrayList<>();

    // boom button
    private BoomMenuButton addEventBtn;
    private boolean init = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_mode_result_view);

        recyclerTimeLineView = (RecyclerView) findViewById(R.id.recyclerView);
        if (recyclerTimeLineView != null) {
            recyclerTimeLineView.setLayoutManager(new LinearLayoutManager(this));
            recyclerTimeLineView.setHasFixedSize(true);
        }

        loadViewList();

        addEventBtn = (BoomMenuButton) findViewById(R.id.add_event_to_timeline_btn);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (init) return;
        init = true;

        Drawable[] subButtonDrawables = new Drawable[3];
        int[] drawablesResource = new int[]{
                R.drawable.ic_alarm_add_white_36dp,
                R.drawable.ic_audiotrack_white_36dp,
                R.drawable.ic_note_add_white_36dp
        };
        int itemCount = 3;
        for (int i = 0; i < itemCount; i++)
            subButtonDrawables[i] = ContextCompat.getDrawable(this, drawablesResource[i]);

        String[] subButtonTexts = new String[]{getResources().getString(R.string.add_reaction_test), getResources().getString(R.string.add_audio), getResources().getString(R.string.add_note)};

        int[][] subButtonColors = new int[3][2];
        for (int i = 0; i < itemCount; i++) {
            subButtonColors[i][1] = ContextCompat.getColor(this, R.color.colorPrimary);
            subButtonColors[i][0] = Util.getInstance().getPressedColor(subButtonColors[i][1]);
        }


        addEventBtn.init(
                subButtonDrawables, // The drawables of images of sub buttons. Can not be null.
                subButtonTexts,     // The texts of sub buttons, ok to be null.
                subButtonColors,    // The colors of sub buttons, including pressed-state and normal-state.
                ButtonType.HAM,     // The button type.
                BoomType.PARABOLA,  // The boom type.
                PlaceType.HAM_3_1,  // The place type.
                null,               // Ease type to move the sub buttons when showing.
                null,               // Ease type to scale the sub buttons when showing.
                null,               // Ease type to rotate the sub buttons when showing.
                null,               // Ease type to move the sub buttons when dismissing.
                null,               // Ease type to scale the sub buttons when dismissing.
                null,               // Ease type to rotate the sub buttons when dismissing.
                null                // Rotation degree.
        );
    }

    /**
     * Load items from database to display in the timeline
     */
    private void loadViewList() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //TODO: connect to db
                for (int i = 0; i < 2; i++) {
                    TimeLineModel model = new TimeLineModel();
                    model.setLabel("Random" + i);
                    timeLineList.add(model);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                timeLineAdapter = new TimeLineAdapter(timeLineList, Orientation.vertical);
                recyclerTimeLineView.setAdapter(timeLineAdapter);
            }
        }.execute();
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
