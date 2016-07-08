package com.artursworld.reactiontest.view.games;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.adapters.MedicalUserListAdapter;
import com.artursworld.reactiontest.controller.adapters.MedicalUserSpinnerAdapter;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;
import com.artursworld.reactiontest.view.UserManagement;

import java.util.ArrayList;
import java.util.List;

public class StartGameSettings extends AppCompatActivity {

    Spinner medicalUserSpinner;
    Spinner operationIssueSpinner;
    TextView noUserInDBTextView;
    private String selectedMedicalUserId;
    private List<OperationIssue> selectedOperationIssuesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game_settings);

        initGuiElements();

        initMedicalUserSpinnerAsync();

    }

    // add items into spinner dynamically
    public void addItemsOnOperationIssueSpinner(List<OperationIssue> selectedOperationIssuesList) {
        if(operationIssueSpinner == null)
            operationIssueSpinner = (Spinner) findViewById(R.id.start_game_settings_operation_issue_spinner);

        List<String> list = new ArrayList<String>();
        if (selectedOperationIssuesList != null){
            if(selectedOperationIssuesList.size()> 0){
                for(OperationIssue issue: selectedOperationIssuesList){
                    list.add(issue.getDisplayName());
                }
            }
            else{
                //TODO: no item in list
            }
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        operationIssueSpinner.setAdapter(dataAdapter);
    }

    private void initGuiElements() {
        medicalUserSpinner = (Spinner)findViewById(R.id.start_game_settings_medicalid_spinner);
        operationIssueSpinner = (Spinner) findViewById(R.id.start_game_settings_operation_issue_spinner);
        noUserInDBTextView = (TextView) findViewById(R.id.start_game_settings_no_user_in_db_textview);
    }

    private void initOperationIssueListAsync() {
        new OperationIssueManager.getAllOperationIssuesByMedicoIdAsync(new OperationIssueManager.AsyncResponse(){

            @Override
            public void getAllOperationIssuesByMedicoId(List<OperationIssue> operationIssuesList) {
                selectedOperationIssuesList = operationIssuesList;
                addItemsOnOperationIssueSpinner(operationIssuesList);
                UtilsRG.info("Operation issues loaded for user(" +selectedMedicalUserId+")="+operationIssuesList.toString());
            }

        }, getApplicationContext()).execute(selectedMedicalUserId);
    }

    private void initMedicalUserSpinnerAsync() {
        new MedicalUserManager.getAllMedicalUsers(new MedicalUserManager.AsyncResponse(){

            @Override
            public void getMedicalUserList(List<MedicalUser> medicalUserResultList) {
                initMedicalUserSpinner(medicalUserResultList);
            }

        }, getApplicationContext()).execute();
    }

    private void initMedicalUserSpinner(List<MedicalUser> userList) {
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

            MedicalUserSpinnerAdapter adapter = new MedicalUserSpinnerAdapter(this, medicalIds, ages, images);
            medicalUserSpinner.setAdapter(adapter);

            medicalUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedMedicalUserId = parent.getItemAtPosition(position).toString();
                    initOperationIssueListAsync();
                    UtilsRG.info("selected item: "+selectedMedicalUserId);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }

            });
        }
        if (isEmptyUserList && noUserInDBTextView != null) {
            noUserInDBTextView.setText(R.string.no_user_in_db);
            //TODO: hide all other elements
            medicalUserSpinner.setVisibility(View.INVISIBLE);
        }
    }

    public void onStartGmaeBtnClick(View v){
        Intent intent = new Intent(this, GoGameView.class);
        String message = "super secret message from first view";
        intent.putExtra(UserManagement.EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
