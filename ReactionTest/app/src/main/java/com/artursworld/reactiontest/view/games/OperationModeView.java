package com.artursworld.reactiontest.view.games;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.TimeLineItemClickListener;
import com.artursworld.reactiontest.controller.adapters.TimeLineAdapter;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.TimeLineModel;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.view.AudioRecordAndPlay;
import com.artursworld.reactiontest.view.dialogs.DialogHelper;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.List;

public class OperationModeView extends AppCompatActivity {

    private long nextReactionTestcountDown = 0;

    // timeline
    private RecyclerView recyclerTimeLineView;
    private TimeLineAdapter timeLineAdapter;
    private List<TimeLineModel> timeLineList = new ArrayList<>();
    private TimeLineItemClickListener listener = null;

    // boom button
    private BoomMenuButton addEventBtn;
    private boolean init = false;

    //dialog elements
    SwipeSelector eventTypeSwipeSelector = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_mode_result_view);

        recyclerTimeLineView = (RecyclerView) findViewById(R.id.recyclerView);
        if (recyclerTimeLineView != null) {
            recyclerTimeLineView.setLayoutManager(new LinearLayoutManager(this));
            recyclerTimeLineView.setHasFixedSize(true);

            listener = new TimeLineItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    String msg = "onClick pos:" + position;
                    UtilsRG.info(msg);
                    TastyToast.makeText(getApplicationContext(), msg, TastyToast.LENGTH_LONG, TastyToast.INFO);
                }

            };

            timeLineAdapter = new TimeLineAdapter(timeLineList, listener);
            recyclerTimeLineView.setAdapter(timeLineAdapter);
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
                R.drawable.ic_mic_white_36dp,
                R.drawable.ic_note_add_white_36dp
        };
        int itemCount = 3;
        for (int i = 0; i < itemCount; i++)
            subButtonDrawables[i] = ContextCompat.getDrawable(this, drawablesResource[i]);

        final String[] subButtonTexts = new String[]{getResources().getString(R.string.add_reaction_test), getResources().getString(R.string.add_audio), getResources().getString(R.string.add_event)};

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
                BoomType.LINE,  // The boom type.
                PlaceType.HAM_3_1,  // The place type.
                null,               // Ease type to move the sub buttons when showing.
                null,               // Ease type to scale the sub buttons when showing.
                null,               // Ease type to rotate the sub buttons when showing.
                null,               // Ease type to move the sub buttons when dismissing.
                null,               // Ease type to scale the sub buttons when dismissing.
                null,               // Ease type to rotate the sub buttons when dismissing.
                null                // Rotation degree.
        );

        final Activity activity = this;
        addEventBtn.setOnSubButtonClickListener(new BoomMenuButton.OnSubButtonClickListener() {
            @Override
            public void onClick(int buttonIndex) {
                TastyToast.makeText(getApplicationContext(), subButtonTexts[buttonIndex] + " clicked", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                int addNewReactionTestIndex = 0;
                int recordAudioIndex = 1;
                int addEventIndex = 2;

                if (buttonIndex == recordAudioIndex) {
                    UtilsRG.info("add audio record has been selected");
                    Intent intent = new Intent(activity, AudioRecordAndPlay.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                } else if (buttonIndex == addEventIndex) {
                    UtilsRG.info("addEventIndex has been selected");
                    MaterialDialog dialog = initNoteDialog(activity);
                    initEventTypeSwipeSelector(dialog, activity);
                    initTimePicker(dialog, activity);

                } else if (buttonIndex == addNewReactionTestIndex) {
                    UtilsRG.info("addNewReactionTestIndex has been selected");
                    //TODO: open reaction test
                }
            }
        });
    }

    private void initEventTypeSwipeSelector(MaterialDialog dialog, Activity activity) {
        eventTypeSwipeSelector = (SwipeSelector) dialog.getCustomView().findViewById(R.id.event_type_swipe_selector);
        eventTypeSwipeSelector.setItems(
                new SwipeItem(0, activity.getResources().getString(R.string.note), activity.getResources().getString(R.string.add_note_description)),
                new SwipeItem(1, activity.getResources().getString(R.string.intubation), activity.getResources().getString(R.string.intubation_description)),
                new SwipeItem(2, activity.getResources().getString(R.string.extubation), activity.getResources().getString(R.string.extubation_description)),
                new SwipeItem(3, activity.getResources().getString(R.string.sedation), activity.getResources().getString(R.string.sedation_description)),
                new SwipeItem(3, activity.getResources().getString(R.string.wakeup), activity.getResources().getString(R.string.wakeup_time_description))
        );
    }

    private MaterialDialog initNoteDialog(Activity activity) {
        boolean wrapInScrollView = true;
        return new MaterialDialog.Builder(activity)
                .title(R.string.add_event)
                .customView(R.layout.add_in_operation_event_view, wrapInScrollView)
                .positiveText(R.string.add)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog1, @NonNull DialogAction which) {
                        UtilsRG.info("'add' button clicked in order to add new event for operation");
                        SwipeItem selectedItem = eventTypeSwipeSelector.getSelectedItem();

                        // The value is the first argument provided when creating the SwipeItem.
                        int value = (Integer) selectedItem.value;
                        UtilsRG.info("selected type at intex:" +value);
                        // for example
                        if (value == 0) {
                            // The user selected slide number one.
                        }
                    }
                })
                .show();
    }

    /**
     * Initializes intubation time edit text
     */
    private void initTimePicker(MaterialDialog dialog, Activity activity) {
        View view = dialog.getCustomView();
        EditText timeEditText = (EditText) view.findViewById(R.id.event_time_picker);
        if (timeEditText != null) {
            timeEditText.setInputType(InputType.TYPE_NULL);
            DialogHelper.onFocusOpenTimePicker(activity, timeEditText);
            addOnTextChangeListener(activity, timeEditText, DBContracts.OperationIssueTable.INTUBATION_TIME);
            //TODO: hier
        }
    }

    /**
     * Saves new date in database if user changes it
     */
    private void addOnTextChangeListener(final Activity activity, EditText dateEditText, final String dateTime) {
        dateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable selectedDateOrTime) {
                final String selectedOperationIssue = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, activity);
                UtilsRG.info(dateTime + " for OperationIssue(" + selectedOperationIssue + ") has been chenged to: " + selectedDateOrTime.toString());

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... unusedParams) {
                        //TODO: add to db
                        return null;
                    }
                }.execute();
            }
        });
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
                timeLineAdapter = new TimeLineAdapter(timeLineList, listener);
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
}
