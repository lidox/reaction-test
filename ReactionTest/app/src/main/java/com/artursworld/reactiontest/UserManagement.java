package com.artursworld.reactiontest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.artursworld.reactiontest.entity.MedicalUser;
import com.artursworld.reactiontest.entity.ReactionGame;
import com.artursworld.reactiontest.model.MedicalUserManager;
import com.artursworld.reactiontest.model.ReactionGameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import ch.qos.logback.classic.android.BasicLogcatConfigurator;

public class UserManagement extends AppCompatActivity {

    static {
        BasicLogcatConfigurator.configureDefaultContext();
    }

    private Logger log = LoggerFactory.getLogger(UserManagement.class);

    private MedicalUserManager medicalUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        log.info("Hallo World of Medicine");
        log.info(getStartUp());
    }

    public String getStartUp(){
        StringBuilder ret = new StringBuilder();
        log.info(" _____  ______          _____ _______ _____ ____  _   _    _____          __  __ ______" + "\n");
        log.info("|  __ \\|  ____|   /\\   / ____|__   __|_   _/ __ \\| \\ | |  / ____|   /\\   |  \\/  |  ____|"+ "\n");
        log.info("| |__) | |__     /  \\ | |       | |    | || |  | |  \\| | | |  __   /  \\  | \\  / | |__   "+ "\n");
        log.info("|  _  /|  __|   / /\\ \\| |       | |    | || |  | | . ` | | | |_ | / /\\ \\ | |\\/| |  __| "+ "\n");
        log.info("| | \\ \\| |____ / ____ \\ |____   | |   _| || |__| | |\\  | | |__| |/ ____ \\| |  | | |____"+ "\n");
        log.info("|_|  \\_\\______/_/    \\_\\_____|  |_|  |_____\\____/|_| \\_|  \\_____/_/    \\_\\_|  |_|______|"+ "\n");
        return ret.toString();
    }
}
