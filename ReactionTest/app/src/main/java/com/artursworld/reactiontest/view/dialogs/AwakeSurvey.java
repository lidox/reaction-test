package com.artursworld.reactiontest.view.dialogs;


import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.Global;
import com.artursworld.reactiontest.controller.util.Strings;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * In order to estimate the patients alertness, display an awake survey
 */
public class AwakeSurvey {

    private AppCompatActivity activity;
    private String reactionGameId;
    private int patientsAlertnessValue;

    public AwakeSurvey(AppCompatActivity activity, String reactionGameId) {
        this.activity = activity;
        this.reactionGameId = reactionGameId;
    }

    public void displayAwakeSurvey() {

        // check if awake survey has to be displayed
        if (Global.hasToDisplayAwakeSurvey()) {

            MaterialDialog dialog = getAwakeSurveyDialog();

            dialog.show();

            View dialogView = dialog.getCustomView();

            setDefaultPatientsAlertnessByUI(dialogView);

            addOnSeekBarProgressChangeDescriptionText(dialogView);

        } else {
            // no need to display awake survey
            activity.finish();
        }
    }

    /**
     * Get the awake survey dialog
     *
     * @return the awake survey dialog
     */
    private MaterialDialog getAwakeSurveyDialog() {
        return new MaterialDialog.Builder(activity)
                .title(R.string.estimation_of_alertness)
                .customView(R.layout.popup_awake_survey, true)
                .positiveText(R.string.ok)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog1, @NonNull DialogAction which) {
                        updatePatientsAlertnessValueAndCloseDialog();
                    }
                }).build();
    }

    /**
     * Updates the patients alertness value by reaction game id and closes awake survey dialog
     */
    private void updatePatientsAlertnessValueAndCloseDialog() {
        if (reactionGameId != null && activity != null){
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    new ReactionGameManager(activity).updatePatientsAlertness(reactionGameId, patientsAlertnessValue);
                    return null;
                }
            }.execute();
        }

        if(activity != null)
            activity.finish();
    }

    /**
     * Set patients alertness by UI
     *
     * @param dialogView the awake survey dialog
     */
    private void setDefaultPatientsAlertnessByUI(View dialogView) {
        DiscreteSeekBar seekBar = (DiscreteSeekBar) dialogView.findViewById(R.id.seekbar_alertness);
        this.patientsAlertnessValue = seekBar.getProgress();
    }

    /**
     * Changes the description text of the alertness factor if the seek bars progress change
     *
     * @param dialogView the awakey survey dialogs view
     */
    private void addOnSeekBarProgressChangeDescriptionText(final View dialogView) {

        DiscreteSeekBar seekBar = (DiscreteSeekBar) dialogView.findViewById(R.id.seekbar_alertness);

        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                final TextView descriptionText = (TextView) dialogView.findViewById(R.id.description);
                UtilsRG.info(value + ". has been selected");
                if (value == 5) {
                    descriptionText.setText(Strings.getStringByRId(R.string.awake_and_motivated));
                } else if (value == 4) {
                    descriptionText.setText(Strings.getStringByRId(R.string.just_awake));
                } else if (value == 3) {
                    descriptionText.setText(Strings.getStringByRId(R.string.awake_and_follow));
                } else if (value == 2) {
                    descriptionText.setText(Strings.getStringByRId(R.string.not_awake_and_not_follow));
                } else if (value == 1) {
                    descriptionText.setText(Strings.getStringByRId(R.string.cancel_because_aggressive_or_not_awake));
                }
                patientsAlertnessValue = value;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            }
        });
    }

}
