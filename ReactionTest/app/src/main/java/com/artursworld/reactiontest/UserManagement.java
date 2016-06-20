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


        medicalUserManager = new MedicalUserManager(this.getApplicationContext());
        MedicalUser medUser = new MedicalUser();
        medUser.setMedicalId("myFirstMedicalIdUser" + ( (int) (Math.random() * 100000000) ) );
        Date tomorrow = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));
        medUser.setBirthDate(tomorrow);
        medUser.setGender("Mänlich");

        // insert
        log.info("insert");
        medicalUserManager.insert(medUser);

        for(MedicalUser user : medicalUserManager.getMedicalUsers()){
            log.info(user.toString());
        }

        // update Test
        log.info("update");
        medUser.setGender("weiblich");
        medicalUserManager.update(medUser);

        for(MedicalUser user : medicalUserManager.getMedicalUsers()){
            log.info(user.toString());
        }

        // set up reaction game
        ReactionGameManager reactionGameManager = new ReactionGameManager(this.getApplicationContext());
        ReactionGame game = new ReactionGame();
        game.setDuration(600);
        game.setMedicalUser(medUser);
        game.setReationType("muscular");

        // insert reaction game
        log.info("insert 2 reaction games");
        reactionGameManager.insert(game);

        game = new ReactionGame();
        // TODO: refactor: new ReactionGame(medUser)
        //Date tomorrow = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));
        //game.setCreationDate(tomorrow);
        game.setMedicalUser(medUser);
        reactionGameManager.insert(game);

        for(ReactionGame gameItem :  reactionGameManager.getReactionGamesByMedicalUser(medUser)){
            log.info(gameItem.toString());
        }

        // delete reaction game
        log.info("delete reaction game");
        reactionGameManager.delete(game);
        for(ReactionGame gameItem :  reactionGameManager.getReactionGamesByMedicalUser(medUser)){
            log.info(gameItem.toString());
        }


        // delete medical user
        log.info("delete medical user");
        medicalUserManager.delete(medUser);

        for(MedicalUser user : medicalUserManager.getMedicalUsers()){
            log.info(user.toString());
        }

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
