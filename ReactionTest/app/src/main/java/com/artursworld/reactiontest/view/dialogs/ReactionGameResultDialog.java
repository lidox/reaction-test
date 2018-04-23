package com.artursworld.reactiontest.view.dialogs;


import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.App;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.artursworld.reactiontest.view.games.OperationModeView;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DecimalFormat;

public class ReactionGameResultDialog {

    private ReactionGame game;
    private Activity activity;

    public ReactionGame getGame() {
        return game;
    }

    public void setGame(ReactionGame game) {
        this.game = game;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public MaterialDialog show() {
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(game.getCreationDateFormatted())
                .customView(R.layout.reaction_game_details_view, true)
                .positiveText(R.string.save)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        updateBrainTemperatureByUi(dialog, game, R.id.add_temperature_id, activity);
                    }
                })
                .show();

        setUiParameters(game, dialog);
        return dialog;
    }

    private void setUiParameters(ReactionGame game, MaterialDialog dialog) {
        // avg rt time
        double avgRt = new ReactionGameManager(App.getAppContext()).getAverageReactionTime(game.getReactionGameId());
        TextView avgRtText = (TextView) dialog.findViewById(R.id.reaction_time_title_1);
        avgRtText.setText(new DecimalFormat("#").format(avgRt * 1000));

        // median rt time
        double medianRt = new ReactionGameManager(App.getAppContext()).getMedianReactionTime(game.getReactionGameId());
        TextView medianRtText = (TextView) dialog.findViewById(R.id.reaction_time_median_title);
        medianRtText.setText(new DecimalFormat("#").format(medianRt * 1000));

        // rt count
        String rtCount = new ReactionGameManager(App.getAppContext()).getTryCount(game.getReactionGameId());
        TextView tryCountRtText = (TextView) dialog.findViewById(R.id.reaction_time_tries_title);
        tryCountRtText.setText(rtCount);

        // rating
        String rating = new ReactionGameManager(App.getAppContext()).getRating(game.getReactionGameId());
        TextView ratingText = (TextView) dialog.findViewById(R.id.reaction_time_rating_title);
        ratingText.setText(rating);

        // brain temperature
        double temperature = (game.getBrainTemperature());
        if (temperature > 0) {
            MaterialEditText temperatureText = (MaterialEditText) dialog.findViewById(R.id.add_temperature_id);
            temperatureText.setText(new DecimalFormat("##.#").format(temperature));
        }
    }

    private void updateBrainTemperatureByUi(@NonNull MaterialDialog dialog, ReactionGame game, int uiId, Activity activity) {
        try {
            // update brain temperature
            MaterialEditText temperatureText = (MaterialEditText) dialog.findViewById(uiId);
            double temperature = Double.parseDouble(temperatureText.getText().toString());
            if (temperature > 0) {

                // update in db
                game.setBrainTemperature(temperature);
                new ReactionGameManager(App.getAppContext()).update(game);

                // open operation mode again
                Intent intent = new Intent(activity, OperationModeView.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                activity.startActivity(intent);
            }
        } catch (Exception e) {
            UtilsRG.error("Could not update temperature! Error: " + e.getMessage());
        }
    }
}
