package com.artursworld.reactiontest.view.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.adapters.MedicalUserListAdapter;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;

import java.util.List;

public class MedicalUserListView extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_user_list_view);

        initMedicalUserListViewAsync();
    }

    private void initMedicalUserListViewAsync() {
        new MedicalUserManager.getAllMedicalUsers(new MedicalUserManager.AsyncResponse(){

            @Override
            public void getMedicalUserList(List<MedicalUser> medicalUserResultList) {
                initMedicalUserListView(medicalUserResultList);
            }

        }, getApplicationContext()).execute();
    }

    private void initMedicalUserListView(List<MedicalUser> userList) {
        listView = (ListView) findViewById(R.id.medicalUserListView);
        TextView textView = (TextView) findViewById(R.id.medical_user_list_view_empty_list);
        boolean isEmptyUserList = true;
        if (userList != null) {
            if (userList.size() > 0) {
                isEmptyUserList = false;
            }

            String[] medicalIds = new String[userList.size()];
            int[] ages = new int[userList.size()];
            int[] images = new int[userList.size()];


            for (int i = 0; i < userList.size(); i++) {
                medicalIds[i] = userList.get(i).getMedicalId();
                ages[i] = userList.get(i).getAge();
                images[i] = userList.get(i).getImage();
            }

            MedicalUserListAdapter adapter = new MedicalUserListAdapter(this, medicalIds, ages, images);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UtilsRG.info("selected user at position: " + position);
                }
            });
        }
        if (isEmptyUserList && textView != null) {
            textView.setText(R.string.no_user_in_db);
        }
    }

    public void onAddUserButtonClick(View view){
        Intent intent = new Intent(this, AddMedicalUser.class);
        startActivity(intent);
    }
}
