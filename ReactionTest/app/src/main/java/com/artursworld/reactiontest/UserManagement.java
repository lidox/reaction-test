package com.artursworld.reactiontest;

import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.artursworld.reactiontest.entity.MedicalUser;
import com.artursworld.reactiontest.entity.ReactionGame;
import com.artursworld.reactiontest.model.MedicalUserManager;
import com.artursworld.reactiontest.model.ReactionGameManager;

import java.util.Date;

public class UserManagement extends AppCompatActivity {

    MedicalUserManager medicalUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        System.out.println("Hallo World of Medicine");
        System.out.println(getStartUp());
        medicalUserManager = new MedicalUserManager(this.getApplicationContext());
        MedicalUser medUser = new MedicalUser();
        medUser.setMedicalId("myFirstMedicalIdUser" + ( (int) (Math.random() * 100000000) ) );
        Date tomorrow = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));
        medUser.setBirthDate(tomorrow);
        medUser.setGender("Mänlich");

        // insert
        System.out.println("insert");
        medicalUserManager.insert(medUser);

        for(MedicalUser user : medicalUserManager.getMedicalUsers()){
            System.out.println(user.toString());
        }

        // update Test
        System.out.println("update");
        medUser.setGender("weiblich");
        medicalUserManager.update(medUser);

        for(MedicalUser user : medicalUserManager.getMedicalUsers()){
            System.out.println(user.toString());
        }

        // set up reaction game
        ReactionGameManager reactionGameManager = new ReactionGameManager(this.getApplicationContext());
        ReactionGame game = new ReactionGame();
        game.setDuration(600);
        game.setMedicalUser(medUser);
        game.setReationType("muscular");

        // insert reaction game
        System.out.println("insert 2 reaction games");
        reactionGameManager.insert(game);

        game = new ReactionGame();
        // TODO: refactor: new ReactionGame(medUser), creationDate by mili seconds
        //Date tomorrow = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));
        //game.setCreationDate(tomorrow);
        game.setMedicalUser(medUser);
        reactionGameManager.insert(game);

        for(ReactionGame gameItem :  reactionGameManager.getReactionGamesByMedicalUser(medUser)){
            System.out.println(gameItem.toString());
        }

        // delete reaction game
        System.out.println("delete reaction game");
        reactionGameManager.delete(game);
        for(ReactionGame gameItem :  reactionGameManager.getReactionGamesByMedicalUser(medUser)){
            System.out.println(gameItem.toString());
        }


        // delete medical user
        System.out.println("delete medical user");
        medicalUserManager.delete(medUser);

        for(MedicalUser user : medicalUserManager.getMedicalUsers()){
            System.out.println(user.toString());
        }

    }

    public String getStartUp(){
        StringBuilder ret = new StringBuilder();
        System.out.println(" _____  ______          _____ _______ _____ ____  _   _    _____          __  __ ______" + "\n");
        System.out.println("|  __ \\|  ____|   /\\   / ____|__   __|_   _/ __ \\| \\ | |  / ____|   /\\   |  \\/  |  ____|"+ "\n");
        System.out.println("| |__) | |__     /  \\ | |       | |    | || |  | |  \\| | | |  __   /  \\  | \\  / | |__   "+ "\n");
        System.out.println("|  _  /|  __|   / /\\ \\| |       | |    | || |  | | . ` | | | |_ | / /\\ \\ | |\\/| |  __| "+ "\n");
        System.out.println("| | \\ \\| |____ / ____ \\ |____   | |   _| || |__| | |\\  | | |__| |/ ____ \\| |  | | |____"+ "\n");
        System.out.println("|_|  \\_\\______/_/    \\_\\_____|  |_|  |_____\\____/|_| \\_|  \\_____/_/    \\_\\_|  |_|______|"+ "\n");
        return ret.toString();
    }
}
