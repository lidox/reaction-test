package com.artursworld.reactiontest.view.games;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.adapters.MedicalUserSpinnerAdapter;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;
import com.artursworld.reactiontest.view.dialogs.AddOperationIssueFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StartGameSettings extends FragmentActivity implements AddOperationIssueFragment.AddOperationIssueListener {

    public final static String EXTRA_MEDICAL_USER_ID = "com.artursworld.reactiontest.EXTRA_MEDICAL_USER_ID";
    public final static String EXTRA_OPERATION_ISSUE_NAME = "com.artursworld.reactiontest.EXTRA_OPERATION_ISSUE_NAME";
    public final static String EXTRA_TEST_TYPE = "com.artursworld.reactiontest.EXTRA_TEST_TYPE";
    public final static String EXTRA_GAME_TYPE = "com.artursworld.reactiontest.EXTRA_GAME_TYPE";
    public final static String EXTRA_REACTION_GAME_ID = "com.artursworld.reactiontest.EXTRA_REACTION_GAME_ID";


    private Spinner medicalUserSpinner;
    private Spinner operationIssueSpinner;
    private Spinner gameTypeSpinner;
    private Spinner testTypeSpinner;
    private TextView noUserInDBTextView;
    private Button addOperationIssueBtn;
    private String selectedMedicalUserId;
    private List<OperationIssue> selectedOperationIssuesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game_settings);

        initGuiElements();

        initMedicalUserSpinnerAsync();

        Activity activity = this;
        if (activity != null){
            addItemsIntoSpinner(UtilsRG.getTestTypesList(activity), testTypeSpinner, R.id.start_game_settings_test_type_spinner );
            addItemsIntoSpinner(UtilsRG.getGameTypesList(activity), gameTypeSpinner, R.id.start_game_settings_game_type_spinner );
        }
    }

    public void addItemsOnOperationIssueSpinner(List<OperationIssue> selectedOperationIssuesList, Spinner operationIssueSpinner) {
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
                list.add(getResources().getString(R.string.create_automatic));
            }
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        operationIssueSpinner.setAdapter(dataAdapter);
    }

    public void addItemsIntoSpinner(String[] items, Spinner spinner, int rId) {
        if(spinner == null)
            spinner = (Spinner) findViewById(rId);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void initGuiElements() {
        medicalUserSpinner = (Spinner)findViewById(R.id.start_game_settings_medicalid_spinner);
        operationIssueSpinner = (Spinner) findViewById(R.id.start_game_settings_operation_issue_spinner);
        gameTypeSpinner = (Spinner) findViewById(R.id.start_game_settings_game_type_spinner);
        testTypeSpinner = (Spinner) findViewById(R.id.start_game_settings_test_type_spinner);
        noUserInDBTextView = (TextView) findViewById(R.id.start_game_settings_no_user_in_db_textview);
        addOperationIssueBtn = (Button) findViewById(R.id.start_game_settings_add_operationBtn);
    }

    private void initOperationIssueListAsync() {
        new OperationIssueManager.getAllOperationIssuesByMedicoIdAsync(new OperationIssueManager.AsyncResponse(){

            @Override
            public void getAllOperationIssuesByMedicoId(List<OperationIssue> operationIssuesList) {
                selectedOperationIssuesList = operationIssuesList;
                addItemsOnOperationIssueSpinner(operationIssuesList, operationIssueSpinner);
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

    public void onAddOperationIssueBtnCLick(View view){
        UtilsRG.info("open Add Item Dialog");
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddOperationIssueFragment inputNameDialog = new AddOperationIssueFragment();
        inputNameDialog.setCancelable(false);
        inputNameDialog.show(fragmentManager, null);
    }

    @Override
    public void onFinishInputDialog(String inputText) {
        if(inputText !=null && !inputText.equals("")){
            new OperationIssueManager(getApplicationContext()).insertOperationIssueByMedIdAsync(selectedMedicalUserId, inputText);
            initMedicalUserSpinnerAsync();
        }
    }

    private void setGameSettingsBeforeStartGame(String medicalUserId, String testType, String gameType) {
        String operationIssueName;
        if(operationIssueSpinner.getSelectedItem() == null){
            operationIssueName = getString(R.string.auto_genrated)+ " "+ UtilsRG.dayAndhourFormat.format(new Date());
            new OperationIssueManager(getApplicationContext()).insertOperationIssueByMedIdAsync(selectedMedicalUserId, operationIssueName);
        }
        else{
            operationIssueName = operationIssueSpinner.getSelectedItem().toString();
        }

        if(testTypeSpinner.getSelectedItem() !=null)
            testType = testTypeSpinner.getSelectedItem().toString();
        if(gameTypeSpinner.getSelectedItem() !=null)
            gameType = gameTypeSpinner.getSelectedItem().toString();
        UtilsRG.info("User("+medicalUserId+") with operation name("+operationIssueName+"). Test type="+testType+ ", GameType="+gameType);
    }

    public void onStartGmaeBtnClick(View view){
        String medicalUserId = selectedMedicalUserId;
        String operationIssueName="";
        String testType="";
        String gameType="";
        boolean isCreateAutomatic = (operationIssueSpinner.getSelectedItem().equals(getResources().getString(R.string.create_automatic)));
        if((operationIssueSpinner.getSelectedItem() == null) || isCreateAutomatic){
            operationIssueName = getString(R.string.auto_genrated)+ " "+ UtilsRG.dayAndhourFormat.format(new Date());
            new OperationIssueManager(getApplicationContext()).insertOperationIssueByMedIdAsync(selectedMedicalUserId, operationIssueName);
        }
        else{
            operationIssueName = operationIssueSpinner.getSelectedItem().toString();
        }

        if(testTypeSpinner.getSelectedItem() != null) {
            Type.TestTypes type = Type.getTestType(testTypeSpinner.getSelectedItemPosition());
            testType = Type.getTestType(type);
        }
        if(gameTypeSpinner.getSelectedItem() != null){
            Type.GameTypes type = Type.getGameType(gameTypeSpinner.getSelectedItemPosition());
            gameType = Type.getGameType(type);
        }

        UtilsRG.info("User("+medicalUserId+") with operation name("+operationIssueName+"). Test type="+testType+ ", GameType="+gameType);

        Intent intent = new Intent(this, GoGameView.class);
        intent.putExtra(StartGameSettings.EXTRA_MEDICAL_USER_ID, medicalUserId);
        intent.putExtra(StartGameSettings.EXTRA_OPERATION_ISSUE_NAME, operationIssueName);
        intent.putExtra(StartGameSettings.EXTRA_GAME_TYPE, gameType);
        intent.putExtra(StartGameSettings.EXTRA_TEST_TYPE, testType);
        startActivity(intent);
    }

}
