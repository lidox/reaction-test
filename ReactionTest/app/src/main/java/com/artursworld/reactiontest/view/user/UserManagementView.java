package com.artursworld.reactiontest.view.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;

public class UserManagementView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UtilsRG.info(UserManagementView.class.getSimpleName()+ " onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management_view);
    }

    public void onAddUserButtonClick(View view){
        UtilsRG.info("add user clicked");
        Intent intent = new Intent(this, AddMedicalUser.class);
        startActivity(intent);
    }
}
