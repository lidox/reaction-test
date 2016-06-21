package com.artursworld.reactiontest;

import android.content.Context;
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
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        log.info("Hallo World of Medicine");
        log.info(getStartUp());
        context = getApplicationContext();

        medicalUserManager = new MedicalUserManager(getApplicationContext());
        MedicalUser medUser = new MedicalUser();
        medUser.setMedicalId("Medico" + ( (int) (Math.random() * 100000000) ) );
        Date tomorrow = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));
        medUser.setBirthDate(tomorrow);
        medUser.setGender("male");

        // insert / create
        log.info("insert medicoUser");
        medicalUserManager.insert(medUser);

        log.info("rename user");
        medicalUserManager.renameMedicalUserByName(medUser, "coolName"+ Math.random());

        medUser = medicalUserManager.getUserByMedicoId(medUser.getMedicalId()).get(0);
        log.info(medUser.toString());

        // set up reaction game
        ReactionGameManager reactionGameManager = new ReactionGameManager(getApplicationContext());
        ReactionGame game = new ReactionGame(medUser);

        // insert reaction game
        log.info("insert reaction game 1");
        reactionGameManager.insert(game);

        game = new ReactionGame(medUser);

        log.info("insert reaction game 2");
        reactionGameManager.insert(game);

        // delete medical user and hopefully all its reactiongames
        log.info("delete medical user");
        medicalUserManager.delete(medUser);
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

    public void tesDB(){

    }
}
