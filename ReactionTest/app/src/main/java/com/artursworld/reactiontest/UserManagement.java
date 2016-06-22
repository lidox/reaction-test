package com.artursworld.reactiontest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.artursworld.reactiontest.entity.MedicalUser;
import com.artursworld.reactiontest.entity.ReactionGame;
import com.artursworld.reactiontest.model.MedicalUserManager;
import com.artursworld.reactiontest.model.ReactionGameManager;
import com.artursworld.reactiontest.util.UtilsRG;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import ch.qos.logback.classic.android.BasicLogcatConfigurator;

public class UserManagement extends AppCompatActivity {

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
}
