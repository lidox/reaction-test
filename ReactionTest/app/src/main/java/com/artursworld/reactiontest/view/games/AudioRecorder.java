package com.artursworld.reactiontest.view.games;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.InOpEvent;
import com.artursworld.reactiontest.model.persistence.manager.InOpEventManager;
import com.skyfishjy.library.RippleBackground;

import java.io.IOException;

public class AudioRecorder {

    public static final int REQUEST_CODE_RECORD_AUDIO = 2;
    private ImageView recordButton = null;
    private RippleBackground rippleBackground = null;
    private TextView statusText = null;
    private MediaRecorder recorder = null;
    private String musicFile = null;
    private boolean isRecording = false;
    private boolean hasRecorded = false;
    private MediaPlayer mediaPlayer = null;

    public AudioRecorder(MaterialDialog audioDialog, final InOpEvent event, final Activity activity, final boolean playOnly) {
        boolean isAudioRecordAllowed = UtilsRG.permissionAllowed(activity, Manifest.permission.RECORD_AUDIO);
        if (isAudioRecordAllowed) {
            UtilsRG.info("isAudioRecordAllowed = true");
            initAudioDialog(audioDialog, event, activity, playOnly);
        }
        if (!isAudioRecordAllowed) {
            UtilsRG.info("isAudioRecordAllowed = false");
            String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            UtilsRG.requestPermission(activity, permissions, REQUEST_CODE_RECORD_AUDIO);
        }
    }

    private void initAudioDialog(MaterialDialog audioDialog, final InOpEvent event, final Activity activity, final boolean playOnly) {
        recordButton = (ImageView) audioDialog.getCustomView().findViewById(R.id.record_icon);
        statusText = (TextView) audioDialog.getCustomView().findViewById(R.id.record_state_label);
        rippleBackground = (RippleBackground) audioDialog.getCustomView().findViewById(R.id.ripple_background);

        recordButton.setBackgroundResource(R.drawable.ic_fiber_manual_record_black_48dp);
        if (playOnly) {
            recordButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
            statusText.setVisibility(View.INVISIBLE);
        }

        musicFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + event.getAudioTimeStampt() + ".3gp";

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(musicFile);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!playOnly && !isRecording && !hasRecorded) {
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

                } else if (!playOnly && isRecording && hasRecorded) {
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                    isRecording = false;
                    recordButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
                    statusText.setText(activity.getResources().getString(R.string.play));
                    rippleBackground.stopRippleAnimation();
                } else if (!isRecording && hasRecorded) {
                    playMusic();
                } else if (playOnly) {
                    isRecording = false;
                    hasRecorded = true;
                    recordButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
                    statusText.setText(activity.getResources().getString(R.string.play));
                    playMusic();
                }

            }
        });

        audioDialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog1, @NonNull DialogAction which) {
                UtilsRG.info("save audio has been clicked");
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        if (recorder != null) {
                            recorder.stop();
                            recorder.release();
                            recorder = null;
                            isRecording = false;
                        }
                        new InOpEventManager(activity.getApplicationContext()).insertEvent(event);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Intent intent = activity.getIntent();
                        activity.finish();
                        activity.startActivity(intent);
                    }
                }.execute(/*event*/);

            }
        });
    }

    private void playMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            play(musicFile);
        } else {
            if (!mediaPlayer.isPlaying()) {
                play(musicFile);
            }
        }
    }

    private void play(String outputFile) {
        try {
            UtilsRG.info("play audio");
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
        } catch (Exception e) {
            UtilsRG.error("Could not play recorded audio!" + e.getLocalizedMessage());
        }
        mediaPlayer.start();
    }
}
