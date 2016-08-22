package com.artursworld.reactiontest.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.view.games.StartGameSettings;
import com.artursworld.reactiontest.view.settings.SettingsActivity;
import com.artursworld.reactiontest.view.user.UserManagementView;

/**
 * Displays launcher of the app
 */
public class LauncherView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_view);

        UtilsRG.log.info("Hallo World of Medicine");
        UtilsRG.log.info(getStartUp());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    /**
     * Logging start of the app. Used for debugging reasons
     */
    public String getStartUp(){
        StringBuilder ret = new StringBuilder();
        UtilsRG.log.info(" _____  ______          _____ _______ _____ ____  _   _    _____          __  __ ______" + "\n");
        UtilsRG.log.info("|  __ \\|  ____|   /\\   / ____|__   __|_   _/ __ \\| \\ | |  / ____|   /\\   |  \\/  |  ____|"+ "\n");
        UtilsRG.log.info("| |__) | |__     /  \\ | |       | |    | || |  | |  \\| | | |  __   /  \\  | \\  / | |__   "+ "\n");
        UtilsRG.log.info("|  _  /|  __|   / /\\ \\| |       | |    | || |  | | . ` | | | |_ | / /\\ \\ | |\\/| |  __| "+ "\n");
        UtilsRG.log.info("| | \\ \\| |____ / ____ \\ |____   | |   _| || |__| | |\\  | | |__| |/ ____ \\| |  | | |____"+ "\n");
        UtilsRG.log.info("|_|  \\_\\______/_/    \\_\\_____|  |_|  |_____\\____/|_| \\_|  \\_____/_/    \\_\\_|  |_|______|"+ "\n");
        return ret.toString();
    }

    /*
    * Displays pre game settings on button click
    */
    public void startGame(View view) {
        Intent intent = new Intent(this, StartGameSettings.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    /*
    * Displays user list onbutton click
    */
    public void onStartUserManagement(View view){
        Intent intent = new Intent(this, UserManagementView.class);
        startActivity(intent);
    }

    /**
     * Displays game settings in a preference fragment
     */
    public void onStartSettings(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
