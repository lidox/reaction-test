package com.artursworld.reactiontest.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.view.games.StartGameSettings;
import com.artursworld.reactiontest.view.settings.SettingsActivity;
import com.artursworld.reactiontest.view.user.AddMedicalUser;
import com.artursworld.reactiontest.view.user.UserManagementView;
import com.victor.loading.rotate.RotateLoading;

import java.util.List;

/**
 * Displays launcher of the app
 */
public class LauncherView extends AppCompatActivity {

    private RotateLoading loadingImage = null;
    int loadingInSeconds = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLoadingImage();
        UtilsRG.log.info("Hallo World of Medicine");
        getStartUp();
        checkIfUserExists();
    }

    /**
     * Starts showing loading image
     */
    private void startLoadingImage() {
        loadingImage = (RotateLoading) findViewById(R.id.loading);
        if (loadingImage != null) {
            loadingImage.start();
        }
    }

    /**
     * Checks if a user exists. If not, opens create user view.
     * Otherwise opens view to start game.
     */
    private void checkIfUserExists() {
        final Activity activity = this;
        new AsyncTask<Void, Void, List<MedicalUser>>() {

            @Override
            protected List<MedicalUser> doInBackground(Void... params) {
                List<MedicalUser> list = new MedicalUserManager(getApplicationContext()).getAllMedicalUsers();
                UtilsRG.info(list.size() + ". users has been found in the app.");
                try {
                    Thread.sleep(loadingInSeconds *1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return list;
            }

            @Override
            protected void onPostExecute(List<MedicalUser> medicalUsers) {
                super.onPostExecute(medicalUsers);

                if(medicalUsers != null && activity != null){
                    if(medicalUsers.size() == 0){
                        Intent intent = new Intent(activity, AddMedicalUser.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.putExtra(getResources().getString(R.string.no_user_in_the_database), true);
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(activity, StartGameSettings.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }

                activity.finish();
                }

            }

        }.execute();
    }

    /**
     * Logging start of the app. Used for debugging reasons
     */
    public String getStartUp() {
        StringBuilder ret = new StringBuilder();
        UtilsRG.log.info(" _____  ______          _____ _______ _____ ____  _   _    _____          __  __ ______" + "\n");
        UtilsRG.log.info("|  __ \\|  ____|   /\\   / ____|__   __|_   _/ __ \\| \\ | |  / ____|   /\\   |  \\/  |  ____|" + "\n");
        UtilsRG.log.info("| |__) | |__     /  \\ | |       | |    | || |  | |  \\| | | |  __   /  \\  | \\  / | |__   " + "\n");
        UtilsRG.log.info("|  _  /|  __|   / /\\ \\| |       | |    | || |  | | . ` | | | |_ | / /\\ \\ | |\\/| |  __| " + "\n");
        UtilsRG.log.info("| | \\ \\| |____ / ____ \\ |____   | |   _| || |__| | |\\  | | |__| |/ ____ \\| |  | | |____" + "\n");
        UtilsRG.log.info("|_|  \\_\\______/_/    \\_\\_____|  |_|  |_____\\____/|_| \\_|  \\_____/_/    \\_\\_|  |_|______|" + "\n");
        return ret.toString();
    }


}
