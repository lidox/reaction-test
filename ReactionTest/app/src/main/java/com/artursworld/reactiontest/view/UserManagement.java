package com.artursworld.reactiontest.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.view.games.GoGameView;
import com.artursworld.reactiontest.view.user.AddMedicalUser;

public class UserManagement extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.artursworld.reactiontest.MESSAGE";
    private MedicalUserManager medicalUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        UtilsRG.log.info("Hallo World of Medicine");
        UtilsRG.log.info(getStartUp());

    }

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

    public void startGame(View view) {
        Intent intent = new Intent(this, GoGameView.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = "super secret message from first view";//editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void startCreateMedicalUser(View view){
        Intent intent = new Intent(this, AddMedicalUser.class);
        startActivity(intent);
    }
}
