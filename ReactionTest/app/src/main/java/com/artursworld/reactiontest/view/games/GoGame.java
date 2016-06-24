package com.artursworld.reactiontest.view.games;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.view.UserManagement;

public class GoGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_game);

        UtilsRG.info("received message="+getIntentMessage(UserManagement.EXTRA_MESSAGE));

        hideActionBar(getSupportActionBar());
    }

    private String getIntentMessage(String messageKey){
        Intent intent = getIntent();
        String message = intent.getStringExtra(messageKey);
        return message;
    }

    private void hideActionBar(ActionBar actionBar){
        actionBar.hide();
    }
}
