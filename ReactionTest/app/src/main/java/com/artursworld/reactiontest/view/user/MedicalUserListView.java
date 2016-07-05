package com.artursworld.reactiontest.view.user;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.adapters.MedicalUserListAdapter;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;

import java.util.List;

public class MedicalUserListView extends AppCompatActivity {

    ListView listView;
    MedicalUserManager userDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_user_list_view);
        userDB = new MedicalUserManager(getApplicationContext());
        initMedicalUserListView(userDB.getAllMedicalUsers());
    }

    private void initMedicalUserListView(List<MedicalUser> userList) {
        listView = (ListView) findViewById(R.id.medicalUserListView);
        boolean isEmptyUserList = true;
        if(userList != null){
            if(userList.size()> 0){
                isEmptyUserList = false;
            }

            String[] medicalIds = new String[userList.size()];
            int[] ages = new int[userList.size()];
            int[] images = new int[userList.size()];


            for(int i= 0 ; i<userList.size(); i++){
                medicalIds[i] = userList.get(i).getMedicalId();
                ages[i] = userList.get(i).getAge();
                images[i] = userList.get(i).getImage();
            }

            MedicalUserListAdapter adapter = new MedicalUserListAdapter(this, medicalIds, ages, images);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UtilsRG.info("selected user at position: "+position);
                    //Toast.makeText(getApplicationContext(), medicalIds[position], Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (isEmptyUserList){
            TextView textView = (TextView) findViewById(R.id.medical_user_list_view_empty_list);
            textView.setText(R.string.no_user_in_db);
        }



    }
}
