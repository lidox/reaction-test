package com.artursworld.reactiontest.view.games;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.TimeLineItemClickListener;
import com.artursworld.reactiontest.controller.adapters.TimeLineAdapter;
import com.artursworld.reactiontest.controller.helper.OpStatus;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.ITimeLineItem;
import com.artursworld.reactiontest.model.entity.InOpEvent;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.manager.InOpEventManager;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.artursworld.reactiontest.view.dialogs.DialogHelper;
import com.artursworld.reactiontest.view.statistics.ReactionGameChart;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class OperationModeView extends AppCompatActivity implements Observer {

    private static final String TYPE_AUDIO = "Audio";
    private long nextReactionTestCountDown = 0;

    // timeline
    private RecyclerView recyclerTimeLineView;
    private List<ITimeLineItem> timeLineList = new ArrayList<>();
    private TimeLineItemClickListener listener = null;
    private TextView reactionPerformanceLabel = null;

    // boom button
    private BoomMenuButton addEventBtn;
    private boolean isInitialized = false;
    private boolean isChartDisplayed = true;

    // add event dialog ui elements
    private SwipeSelector eventTypeSwipeSelector = null;
    private SwipeItem[] swipeList = null;
    private EditText timePickerEditText = null;
    private EditText noteEditText = null;

    // global settings
    String operationIssue = null;
    Activity activity = null;
    boolean activityIsPaused = true;
    private OperationModeView self = null;


    private boolean countDownIsRunning = false;
    private TextView countDownTextView = null;
    private ReactionGameChart goGameChart = null;
    private ImageView expandImage = null;
    private long vibrationDurationOnCountDownFinish = 0;
    private CountDownTimer countDownTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_mode_result_view);
        activity = this;
        self = this;
        addEventBtn = (BoomMenuButton) findViewById(R.id.add_event_to_timeline_btn);
        reactionPerformanceLabel = (TextView) findViewById(R.id.reaction_time_performance_chart);
        recyclerTimeLineView = (RecyclerView) findViewById(R.id.recyclerView);
        expandImage = (ImageView) findViewById(R.id.expand_icon);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityIsPaused = false;
        loadPreferences();
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityIsPaused = true;
    }

    /**
     * Click listener for items in the timeline
     *
     * @param view     the view clicked
     * @param position the position clicked at
     */
    private void onClickTimeLineItem(View view, int position) {
        if (timeLineList != null) {
            try {
                InOpEvent event = (InOpEvent) timeLineList.get(position);
                String msg = "clicked on " + event.toString();
                UtilsRG.info(msg);
                if (event.getType().equals("Audio")) {
                    MaterialDialog dialog = getAudioPlayDialog((OperationModeView) activity, event);
                    new AudioRecorder(dialog, event, activity, true);
                } else if (event.getType().equals("ReactionTest")) {

                } else {
                    MaterialDialog dialog = getEditNoteDialog(activity, event);
                    initEventTypeSwipeSelector(dialog, activity);
                    initTimePicker(dialog, activity);
                    displaySelectedEvent(event);
                }
            } catch (ClassCastException e) {
                ReactionGame game = (ReactionGame) timeLineList.get(position);
                UtilsRG.info("Reaction Test selected: " + game.toString());
            } catch (Exception e) {
                UtilsRG.error("could not open detail dialog for  timeline item at position:" + position + " in view:" + view.toString());
            }

        }
    }

    private MaterialDialog getAudioPlayDialog(final OperationModeView activity, final InOpEvent event) {
        return new MaterialDialog.Builder(activity)
                .title(R.string.play_audio)
                .customView(R.layout.record_audio_dialog, true)
                .negativeText(R.string.delete)
                .onNegative(getEventDeleteDialog(event))
                .show();
    }

    private void displaySelectedEvent(InOpEvent event) {
        if (event != null) {
            if (eventTypeSwipeSelector != null) {
                Object swipeIndex = getSwipeIndexByEvent(event);
                eventTypeSwipeSelector.selectItemWithValue(swipeIndex);
            }

            if (timePickerEditText != null)
                timePickerEditText.setText(event.getHoursAndMinutes());
            if (noteEditText != null)
                noteEditText.setText(event.getAdditionalNote());
        }
    }

    private Object getSwipeIndexByEvent(InOpEvent event) {
        if (swipeList != null) {
            String title = event.getType();
            for (int i = 0; i < swipeList.length; i++) {
                if (swipeList[i].title.equals(title)) {
                    return swipeList[i].value;
                }
            }
        }
        return 0;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (isInitialized) return;
        isInitialized = true;

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
                //TastyToast.makeText(getApplicationContext(), subButtonTexts[buttonIndex] + " clicked", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                int addNewReactionTestIndex = 0;
                int recordAudioIndex = 1;
                int addEventIndex = 2;

                if (buttonIndex == recordAudioIndex) {
                    boolean isAudioRecordAllowed = UtilsRG.permissionAllowed(activity, Manifest.permission.RECORD_AUDIO);
                    if (isAudioRecordAllowed) {
                        UtilsRG.info("add audio record has been selected");
                        openAudioDialog(activity);
                    } else {
                        UtilsRG.info("isAudioRecordAllowed = false");
                        String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        if (UtilsRG.requestPermission(activity, permissions, AudioRecorder.REQUEST_CODE_RECORD_AUDIO)) {
                            UtilsRG.startInstalledAppDetailsActivity(activity);
                        }
                    }
                } else if (buttonIndex == addEventIndex) {
                    UtilsRG.info("addEventIndex has been selected");
                    MaterialDialog dialog = initNoteDialog(activity);
                    initEventTypeSwipeSelector(dialog, activity);
                    initTimePicker(dialog, activity);

                } else if (buttonIndex == addNewReactionTestIndex) {
                    startIntentNewReactionTest(activity);
                }
            }
        });
    }

    /**
     * Opens the audio dialog to reacord audio mesasge
     *
     * @param activity the running activity
     */
    private void openAudioDialog(Activity activity) {
        InOpEvent event = new InOpEvent(operationIssue, new Date(), TYPE_AUDIO, null);
        MaterialDialog audioDialog = getAudioDialog(activity);
        new AudioRecorder(audioDialog, event, activity, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AudioRecorder.REQUEST_CODE_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    UtilsRG.info("Permission Granted: REQUEST_CODE_RECORD_AUDIO");
                    openAudioDialog(activity);
                } else {
                    UtilsRG.info("Permission Denied: REQUEST_CODE_RECORD_AUDIO");
                    String[] permission = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    UtilsRG.requestPermission(activity, permission, AudioRecorder.REQUEST_CODE_RECORD_AUDIO);
                }
                break;
        }
    }

    /**
     * Starts the reaction test
     *
     * @param activity
     */
    private void startIntentNewReactionTest(Activity activity) {
        if (activity != null) {
            UtilsRG.info("addNewReactionTestIndex has been selected");
            Intent intent = new Intent(activity, GoGameView.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }
    }

    private void initEventTypeSwipeSelector(MaterialDialog dialog, Activity activity) {
        eventTypeSwipeSelector = (SwipeSelector) dialog.getCustomView().findViewById(R.id.event_type_swipe_selector);
        swipeList = new SwipeItem[6];
        swipeList[0] = new SwipeItem(0, activity.getResources().getString(R.string.note), activity.getResources().getString(R.string.add_note_description));
        swipeList[1] = new SwipeItem(1, activity.getResources().getString(R.string.intubation), activity.getResources().getString(R.string.intubation_description));
        swipeList[2] = new SwipeItem(2, activity.getResources().getString(R.string.extubation), activity.getResources().getString(R.string.extubation_description));
        swipeList[3] = new SwipeItem(3, activity.getResources().getString(R.string.sedation), activity.getResources().getString(R.string.sedation_description));
        swipeList[4] = new SwipeItem(4, activity.getResources().getString(R.string.wakeup), activity.getResources().getString(R.string.wakeup_time_description));
        swipeList[5] = new SwipeItem(5, activity.getResources().getString(R.string.operation_finished), activity.getResources().getString(R.string.operation_finished_description));
        eventTypeSwipeSelector.setItems(swipeList);
    }

    private MaterialDialog getAudioDialog(Activity activity) {
        return new MaterialDialog.Builder(activity)
                .title(R.string.add_audio)
                .customView(R.layout.record_audio_dialog, true)
                .positiveText(R.string.save)
                .negativeText(R.string.cancel)
                .show();
    }

    private MaterialDialog initNoteDialog(Activity activity) {
        return new MaterialDialog.Builder(activity)
                .title(R.string.add_event)
                .customView(R.layout.add_in_operation_event_view, true)
                .positiveText(R.string.add)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog1, @NonNull DialogAction which) {
                        UtilsRG.info("'add' button clicked in order to add new event for operation");
                        InOpEvent event = getInOpEventByUI();
                        new AsyncTask<InOpEvent, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(InOpEvent... params) {
                                InOpEvent event = params[0];
                                new InOpEventManager(getApplicationContext()).insertEvent(event);
                                if(OpStatus.FINISHED == OpStatus.findByTranslationText(event.getType())){
                                    return true;
                                }
                                return false;
                            }

                            @Override
                            protected void onPostExecute(Boolean isOpIsFinished) {
                                super.onPostExecute(isOpIsFinished);
                                loadTimeLineItems();
                                if(isOpIsFinished){

                                    UtilsRG.info("Canceling countdown");
                                    if(countDownTimer!= null){
                                        countDownTimer.cancel();
                                        countDownTextView.setText("");
                                    }

                                }
                            }
                        }.execute(event);

                    }
                })
                .show();
    }

    private MaterialDialog getEditNoteDialog(final Activity activity, final InOpEvent event) {
        boolean wrapInScrollView = true;
        return new MaterialDialog.Builder(activity)
                .title(R.string.edit_event)
                .customView(R.layout.add_in_operation_event_view, wrapInScrollView)
                .positiveText(R.string.save)
                .negativeText(R.string.delete)

                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        UtilsRG.info("display/edit event: " + event);
                        InOpEvent event = getInOpEventByUI();
                        new AsyncTask<InOpEvent, Void, Void>() {
                            @Override
                            protected Void doInBackground(InOpEvent... params) {
                                InOpEvent event = params[0];
                                new InOpEventManager(getApplicationContext()).updateEvent(event);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                loadTimeLineItems();
                            }
                        }.execute(event);

                    }
                })
                .onNegative(getEventDeleteDialog(event))
                .show();
    }

    /**
     * Display dialog to be able to delete the event
     *
     * @param event the event to delete
     * @return the dialog
     */
    public MaterialDialog.SingleButtonCallback getEventDeleteDialog(final InOpEvent event) {
        return new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                UtilsRG.info("delete event has been clicked: " + event);

                new MaterialDialog.Builder(activity)
                        .title(R.string.delete)
                        .content(R.string.delete_event)
                        .positiveText(R.string.agree)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {

                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                new AsyncTask<InOpEvent, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(InOpEvent... params) {
                                        InOpEvent event = params[0];
                                        new InOpEventManager(getApplicationContext()).deleteEvent(event);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);
                                        loadTimeLineItems();
                                    }
                                }.execute(event);
                            }
                        })
                        .show();
            }
        };
    }

    /**
     * Reads all attributes of the event, which is displayed in the UI
     *
     * @return the InOpEvent
     */
    private InOpEvent getInOpEventByUI() {
        SwipeItem selectedItem = eventTypeSwipeSelector.getSelectedItem();
        String eventType = null;
        String note = null;

        if (selectedItem != null)
            eventType = selectedItem.title;

        Date timestamp = DialogHelper.getDateTimeFromUI(new Date(), timePickerEditText);

        if (noteEditText != null)
            note = noteEditText.getText().toString();

        return new InOpEvent(operationIssue, timestamp, eventType, note);
    }


    /**
     * Initializes time picker edit text and sets current time as text
     */
    private void initTimePicker(MaterialDialog dialog, Activity activity) {
        try {
            View view = dialog.getCustomView();
            timePickerEditText = (EditText) view.findViewById(R.id.event_time_picker);
            noteEditText = (EditText) view.findViewById(R.id.event_note);
            if (timePickerEditText != null) {
                timePickerEditText.setInputType(InputType.TYPE_NULL);
                DialogHelper.onFocusOpenTimePicker(activity, timePickerEditText);

                try {
                    String currentHourAndMinutes = UtilsRG.timeFormat.format(new Date());
                    timePickerEditText.setText(currentHourAndMinutes);
                } catch (Exception e) {
                    UtilsRG.error(e.getLocalizedMessage());
                }
            }
        } catch (Exception e) {
            UtilsRG.error("Unexpected error: " + e.getLocalizedMessage());
        }
    }

    /**
     * Load items from database to display in the timeline
     */
    private void loadTimeLineItems() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                List<InOpEvent> timeLineEventList = new InOpEventManager(activity).getInOpEventListByOperationIssue(operationIssue, InOpEventManager.SORT_ASC);
                List<ReactionGame> reactionGameList = new ReactionGameManager(activity).getReactionGameList(operationIssue, Type.GameTypes.GoGame.name(), Type.TestTypes.InOperation.name(), "ASC");
                timeLineList = new ArrayList<ITimeLineItem>();
                if (reactionGameList != null) {
                    for (ReactionGame item : reactionGameList) {
                        timeLineList.add((ITimeLineItem) item);
                    }
                }
                if (timeLineEventList != null) {
                    for (InOpEvent item : timeLineEventList) {
                        timeLineList.add((ITimeLineItem) item);
                    }
                }

                //Sort list
                Collections.sort(timeLineList);

                UtilsRG.info("items loaded");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                addEventsToTimeLine();
                initOperationChart();
            }
        }.execute();
    }

    private void addEventsToTimeLine() {
        recyclerTimeLineView = (RecyclerView) findViewById(R.id.recyclerView);
        if (recyclerTimeLineView != null) {
            recyclerTimeLineView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerTimeLineView.setHasFixedSize(true);

            listener = new TimeLineItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    onClickTimeLineItem(view, position);
                }

            };
            TimeLineAdapter timeLineAdapter = new TimeLineAdapter(timeLineList, listener, activity);
            recyclerTimeLineView.setAdapter(timeLineAdapter);
        }
    }

    private void initOperationChart() {
        if (isChartDisplayed) {
            goGameChart = new ReactionGameChart(R.id.reaction_go_game_graph, activity);
            if (self != null) {
                goGameChart.addObserver(self);
            }
        }

        addDisplayChartListener(reactionPerformanceLabel, expandImage);
    }

    /**
     * Toggle chart to show or hide its content
     */
    private void toggleShowHideChart() {
        if (isChartDisplayed) {
            isChartDisplayed = false;
            expandImage.setImageResource(R.drawable.ic_expand_more_black_24dp);
            goGameChart.hideChart();
        } else {
            isChartDisplayed = true;
            expandImage.setImageResource(R.drawable.ic_expand_less_black_24dp);
            goGameChart.showChart();
        }
    }

    /**
     * Adds a toggle function to a textView in order to show and hide the chart
     *
     * @param reactionPerformanceLabel the textView to toggle on off
     * @param expandImage              the imageView, which is show right to the textView
     */
    private void addDisplayChartListener(TextView reactionPerformanceLabel, final ImageView expandImage) {
        if (reactionPerformanceLabel != null && expandImage != null) {

            reactionPerformanceLabel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    toggleShowHideChart();
                }
            });

            expandImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleShowHideChart();
                }
            });
        }
    }

    /**
     * Displays a count with a text in a textview
     *
     * @param countDown_sec the seconds to wait
     * @param prefixText    the text shown right to the countdown
     * @param textView      the textView where to display the countdown
     */
    private void runNextReactionTestCountDown(final long countDown_sec, final String prefixText, final TextView textView) {
        if (countDown_sec <= 0) {
            onCountDownFinish();
        } else if (!countDownIsRunning) {
            if ((textView != null) && (countDown_sec > 0)) {
                countDownTimer = new CountDownTimer((countDown_sec + 1) * 1000, 1000) {

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
                        countDownIsRunning = true;

                    }

                    public void onFinish() {
                        onCountDownFinish();
                    }
                }.start();
            }
        }
    }

    /**
     * Get users attention, because it's time for a new test.
     * Thus so attention dialog, vibrate device and make beep.
     */
    private void onCountDownFinish() {
        countDownIsRunning = false;
        if(!isOperationFinished()){
            UtilsRG.info("onCountDownFinish() started...");
            if (countDownTextView != null) {
                countDownTextView.setText(R.string.make_a_new_try);
            }
            UtilsRG.vibrateDevice(vibrationDurationOnCountDownFinish, activity);
            UtilsRG.beepDevice((int) vibrationDurationOnCountDownFinish);
            openAttentionDialog();
        }
    }

    /**
     * It's time to start a new reaction test, so display attention dialog
     */
    private void openAttentionDialog() {
        if (activity != null && !activityIsPaused) {
            new MaterialDialog.Builder(activity)
                    .title(R.string.attention)
                    .content(R.string.its_time_to_make_a_reaction_test)
                    .positiveText(R.string.agree)
                    .negativeText(R.string.later)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {

                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            startIntentNewReactionTest(activity);
                        }
                    })
                    .show();
        }
    }

    /**
     * Loads some settings from shared pereferances
     */
    private void loadPreferences() {
        final Activity activity = this;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String countDownInSecondsKey = activity.getResources().getString(R.string.in_op_notify_every_x_minutes);
                int nextReactionTestCountDownMin = UtilsRG.getIntByKey(countDownInSecondsKey, activity, 5);
                UtilsRG.info("operation_mode_next_reaction_test_countdown=" + nextReactionTestCountDownMin);
                nextReactionTestCountDown = nextReactionTestCountDownMin * 60;

                int alarmDuration = UtilsRG.getIntByKey(activity.getResources().getString(R.string.vibration_duration_in_millis_key), activity, 3);
                UtilsRG.info("alarm duration loaded by settings=" + alarmDuration);
                vibrationDurationOnCountDownFinish = alarmDuration * 1000;

                operationIssue = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, activity);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                loadTimeLineItems();
            }
        }.execute();
    }

    /**
     * Show countdown a textView
     */
    private void displayCountDown() {
        UtilsRG.info("Start displayCountDown");
        countDownTextView = (TextView) findViewById(R.id.operation_mode_next_game_estimated_in_text);
        String estimatedTime = getResources().getString(R.string.next_game_estimated_in);
        if (goGameChart != null) {
            if (goGameChart.containsInOpReactionTests()) {
                Date currentTime = new Date();
                long difference = (currentTime.getTime() - goGameChart.getLatestInOpReactionTestDate().getTime()) / 1000;
                long countdown = nextReactionTestCountDown - difference;
                runNextReactionTestCountDown(countdown, estimatedTime + ": ", countDownTextView);
            }
        }
    }

    /**
     * The chart has been loaded, so the countdown can start
     *
     * @param observable observable
     * @param data       the transmitted data
     */
    @Override
    public void update(Observable observable, Object data) {
        UtilsRG.info("Observed that chart has been loaded. So start countdown if possible");
        boolean hasNoPreOperationTest = (boolean) data;
        if (hasNoPreOperationTest) {
            UtilsRG.info("hasNoPreOperationTest=" + hasNoPreOperationTest);
            String errorMessage = getResources().getString(R.string.there_is_no_preoperation_reaction_test);//"No pre operation test found";
            TastyToast.makeText(activity.getApplicationContext(), errorMessage, TastyToast.LENGTH_LONG, TastyToast.ERROR);
            finish();
        } else {
            displayCountDownIfOpNotFinished();
        }
    }

    private void displayCountDownIfOpNotFinished() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                if(android.os.Debug.isDebuggerConnected())
                    android.os.Debug.waitForDebugger();

                return isOperationFinished();
            }

            @Override
            protected void onPostExecute(Boolean operationHasFinished) {
                UtilsRG.info("operation has fished= " + operationHasFinished);
                if (!operationHasFinished)
                    displayCountDown();
                super.onPostExecute(operationHasFinished);
            }
        }.execute();
    }

    /**
     * Check if the operation has been finished
     *
     * @return true if operation has finished, otherwise false
     */
    @NonNull
    private Boolean isOperationFinished() {
        List<InOpEvent> events = new InOpEventManager(activity.getApplicationContext()).getInOpEventListByOperationIssue(operationIssue, "ASC");
        for (InOpEvent event : events) {
            if (event != null) {
                if (event.getType() != null) {
                    if (OpStatus.FINISHED == OpStatus.findByTranslationText(event.getType())) {
                        UtilsRG.info("Operation Status = " + OpStatus.FINISHED.name());
                        return true;
                    }
                }
            }
        }
        UtilsRG.info("Operation Status = " + OpStatus.RUNNING.name());
        return false;
    }
}
