package com.artursworld.reactiontest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UserManagement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Hallo World of Medicine");
        setContentView(R.layout.activity_user_management);
    }
}
