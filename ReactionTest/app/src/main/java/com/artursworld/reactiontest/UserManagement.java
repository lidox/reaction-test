package com.artursworld.reactiontest;

import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.artursworld.reactiontest.entity.MedicalUser;
import com.artursworld.reactiontest.model.MedicalUserManager;

public class UserManagement extends AppCompatActivity {

    MedicalUserManager medicalUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        System.out.println("Hallo World of Medicine");
        System.out.println(getStartUp());
        medicalUserManager = new MedicalUserManager(this.getApplicationContext());
        MedicalUser newUser = new MedicalUser();
        newUser.setMedicalId("myFirstMedicalIdUser" +Math.random());
        newUser.setGender("Mänlich");

        // insert
        System.out.println("insert");
        medicalUserManager.insert(newUser);

        for(MedicalUser user : medicalUserManager.getMedicalUsers()){
            System.out.println(user.toString());
        }

        // update Test
        System.out.println("update");
        newUser.setGender("weiblich");
        medicalUserManager.update(newUser);

        for(MedicalUser user : medicalUserManager.getMedicalUsers()){
            System.out.println(user.toString());
        }

        // delete
        System.out.println("delete");
        medicalUserManager.delete(newUser);

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
