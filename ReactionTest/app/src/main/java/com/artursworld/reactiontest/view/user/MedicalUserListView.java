package com.artursworld.reactiontest.view.user;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.adapters.MedicalUserListAdapter;

public class MedicalUserListView extends AppCompatActivity {

    ListView listView;
    String[] medicalIds = {"medA", "wmedB", "medC"};
    int[] ages = { 18, 21, 56};
    int[] images = {R.drawable.male_icon, R.drawable.female_icon, R.drawable.male_icon};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_user_list_view);

        listView = (ListView) findViewById(R.id.medicalUserListView);

        MedicalUserListAdapter adapter = new MedicalUserListAdapter(this, medicalIds, ages, images);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), medicalIds[position], Toast.LENGTH_SHORT).show();
            }
        });
    }
}
