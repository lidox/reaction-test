package com.artursworld.reactiontest.view.games;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.InOpEvent;
import com.sdsmdg.tastytoast.TastyToast;
import com.skyfishjy.library.RippleBackground;

import java.io.IOException;

public class AudioRecorder {

    private ImageView recordButton = null;
    private RippleBackground rippleBackground = null;
    private TextView statusText = null;
    private MediaRecorder recorder = null;
    private String outputFile = null;
    private boolean isRecording = false;
    private boolean hasRecorded = false;
    private MediaPlayer mediaPlayer = null;

    public AudioRecorder(MaterialDialog audioDialog, InOpEvent event, final Activity activity) {
        recordButton = (ImageView) audioDialog.getCustomView().findViewById(R.id.record_icon);
        statusText = (TextView) audioDialog.getCustomView().findViewById(R.id.record_state_label);
        rippleBackground = (RippleBackground) audioDialog.getCustomView().findViewById(R.id.ripple_background);

        recordButton.setBackgroundResource(R.drawable.ic_fiber_manual_record_black_48dp);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + event.getAudioTimeStampt() + ".3gp";//TODO use id

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(outputFile);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording && !hasRecorded) {
                    try {
                        isRecording = true;
                        recorder.prepare();
                        recorder.start();
                        hasRecorded = true;
                        statusText.setText("");
                        rippleBackground.startRippleAnimation();
                    } catch (IOException e) {
                        isRecording = false;
                        rippleBackground.stopRippleAnimation();
                        UtilsRG.error("could not prepare()" + e.getLocalizedMessage());
                    }

                    recordButton.setBackgroundResource(R.drawable.ic_mic_black_48dp);

                } else if (isRecording && hasRecorded) {
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                    isRecording = false;
                    recordButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
                    statusText.setText(activity.getResources().getString(R.string.play));
                    rippleBackground.stopRippleAnimation();
                } else if (!isRecording && hasRecorded) {
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                        play();
                    } else {
                        if (!mediaPlayer.isPlaying()) {
                            play();
                        }
                    }
                }
            }
        });

    }

    private void play() {
        try {
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
        } catch (Exception e) {
            UtilsRG.error("Could not play recorded audio!" + e.getLocalizedMessage());
        }
        mediaPlayer.start();
    }
}
