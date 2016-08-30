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
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.TimeLineItemClickListener;
import com.artursworld.reactiontest.controller.adapters.TimeLineAdapter;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class OperationModeView extends AppCompatActivity {

    private static final String TYPE_AUDIO = "Audio";
    private long nextReactionTestCountDown = 0;

    // timeline
    private RecyclerView recyclerTimeLineView;
    private List<ITimeLineItem> timeLineList = new ArrayList<>();
    private TimeLineItemClickListener listener = null;

    // boom button
    private BoomMenuButton addEventBtn;
    private boolean isInitialized = false;

    // add event dialog ui elements
    SwipeSelector eventTypeSwipeSelector = null;
    SwipeItem[] swipeList = null;
    EditText timePickerEditText = null;
    EditText noteEditText = null;

    // global settings
    String operationIssue = null;
    Activity activity = null;

    private boolean countDownIsRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_mode_result_view);
        activity = this;
        addEventBtn = (BoomMenuButton) findViewById(R.id.add_event_to_timeline_btn);
        recyclerTimeLineView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences(this);
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
                UtilsRG.error("could not open detail dialog for  timeline item at position:" + position + " in view:" +view.toString());
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
                    UtilsRG.info("add audio record has been selected");
                    //TODO: open record audio dialog
                    InOpEvent event = new InOpEvent(operationIssue, new Date(), TYPE_AUDIO, null);
                    MaterialDialog audioDialog = getAudioDialog(activity);
                    AudioRecorder recorder = new AudioRecorder(audioDialog, event, activity, false);
                } else if (buttonIndex == addEventIndex) {
                    UtilsRG.info("addEventIndex has been selected");
                    MaterialDialog dialog = initNoteDialog(activity);
                    initEventTypeSwipeSelector(dialog, activity);
                    initTimePicker(dialog, activity);

                } else if (buttonIndex == addNewReactionTestIndex) {
                    UtilsRG.info("addNewReactionTestIndex has been selected");
                    Intent intent = new Intent(activity, GoGameView.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    //TODO: open reaction test
                }
            }
        });
    }

    private void initEventTypeSwipeSelector(MaterialDialog dialog, Activity activity) {
        eventTypeSwipeSelector = (SwipeSelector) dialog.getCustomView().findViewById(R.id.event_type_swipe_selector);
        swipeList = new SwipeItem[5];
        swipeList[0] = new SwipeItem(0, activity.getResources().getString(R.string.note), activity.getResources().getString(R.string.add_note_description));
        swipeList[1] = new SwipeItem(1, activity.getResources().getString(R.string.intubation), activity.getResources().getString(R.string.intubation_description));
        swipeList[2] = new SwipeItem(2, activity.getResources().getString(R.string.extubation), activity.getResources().getString(R.string.extubation_description));
        swipeList[3] = new SwipeItem(3, activity.getResources().getString(R.string.sedation), activity.getResources().getString(R.string.sedation_description));
        swipeList[4] = new SwipeItem(4, activity.getResources().getString(R.string.wakeup), activity.getResources().getString(R.string.wakeup_time_description));
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
                        InOpEvent event = getInOpEventByUI();
                        new AsyncTask<InOpEvent, Void, Void>() {
                            @Override
                            protected Void doInBackground(InOpEvent... params) {
                                InOpEvent event = params[0];
                                new InOpEventManager(getApplicationContext()).insertEvent(event);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                loadViewList();
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
                                loadViewList();
                            }
                        }.execute(event);

                    }
                })
                .onNegative(getEventDeleteDialog(event))
                .show();
    }

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
                                        loadViewList();
                                    }
                                }.execute(event);
                            }
                        })
                        .show();
            }
        };
    }

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
    }

    /**
     * Load items from database to display in the timeline
     */
    private void loadViewList() {
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
                new ReactionGameChart(R.id.reaction_go_game_graph, activity);
            }
        }.execute();
    }

    /**
     * Displays a count with a text in a textview
     *
     * @param countDown_sec the seconds to wait
     * @param prefixText    the text shown right to the countdown
     * @param textView      the textView where to display the countdown
     */
    private void runNextReactionTestCountDown(final long countDown_sec, final String prefixText, final TextView textView) {
        if (!countDownIsRunning) {
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
                        countDownIsRunning = true;

                    }

                    public void onFinish() {
                        //TODO: vibrate, make noise and wackel dackel button
                        countDownIsRunning = false;
                    }
                }.start();
            }
        }
    }

    /**
     * Loads some settings from shared pereferances
     *
     * @param activity
     */
    private void loadPreferences(final Activity activity) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String countDownInSecondsKey = getResources().getString(R.string.operation_mode_next_reaction_test_countdown);
                nextReactionTestCountDown = UtilsRG.getIntByKey(countDownInSecondsKey, activity, 5) * 60;
                operationIssue = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, activity);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                displayCountDown();
                loadViewList();
            }
        }.execute();
    }

    private void displayCountDown() {
        TextView countDownTextView = (TextView) findViewById(R.id.operation_mode_next_game_estimated_in_text);
        String estimatedTime = getResources().getString(R.string.next_game_estimated_in);
        runNextReactionTestCountDown(nextReactionTestCountDown, estimatedTime + ": ", countDownTextView);
    }
}
