package com.artursworld.reactiontest.view.games;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.adapters.MedicalUserSpinnerAdapter;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;
import com.artursworld.reactiontest.view.dialogs.AddOperationIssueFragment;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Displays pre configuration. The user can select the game type, test type, the operation etc.
 * before the game can start.
 */
public class StartGameSettings extends FragmentActivity implements AddOperationIssueFragment.AddOperationIssueListener {

    // Extras constants to transmit values from activity to other activity
    public final static String EXTRA_MEDICAL_USER_ID = "com.artursworld.reactiontest.EXTRA_MEDICAL_USER_ID";
    public final static String EXTRA_OPERATION_ISSUE_NAME = "com.artursworld.reactiontest.EXTRA_OPERATION_ISSUE_NAME";
    public final static String EXTRA_TEST_TYPE = "com.artursworld.reactiontest.EXTRA_TEST_TYPE";
    public final static String EXTRA_GAME_TYPE = "com.artursworld.reactiontest.EXTRA_GAME_TYPE";
    public final static String EXTRA_REACTION_GAME_ID = "com.artursworld.reactiontest.EXTRA_REACTION_GAME_ID";

    // UI elements
    private Spinner medicalUserSpinner;
    private Spinner operationIssueSpinner;
    private SwipeSelector testTypeSelector;
    private SwipeSelector gameTypeSelector;
    private TextView noUserInDBTextView;
    private Button addOperationIssueBtn;
    private String selectedMedicalUserId;

    // List of operations for a certain user
    private List<OperationIssue> selectedOperationIssuesList;

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if(toolbar != null){
                toolbar.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        UtilsRG.info("onCreateOptionsMenu()");
                        MenuInflater menuIf = getMenuInflater();
                        menuIf.inflate(R.menu.launcher_options_menu, menu);
                    }
                });
                toolbar.setTitleTextColor(Color.WHITE);
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.user_management_option_menu){
                            UtilsRG.info("Selected Option Menu user_management_option_menu");
                        }
                        else if (item.getItemId() == R.id.settings_option_menu){
                            UtilsRG.info("Selected Option Menu settings_option_menu");
                        }
                        return true;
                    }
                });
                this.setActionBar(toolbar);
                this.getActionBar().setDisplayShowTitleEnabled(true);
                this.getActionBar().setDisplayShowCustomEnabled(true);
                this.getActionBar().setDisplayUseLogoEnabled(true);

            }
        }
        setContentView(R.layout.activity_start_game_settings);
        initGuiElements();

        initMedicalUserSpinnerAsync();

        if (activity != null) {
            addItemsIntoSwipeSelector(Type.getTestTypesList(activity), testTypeSelector, R.id.test_type_swipe_selector);
            addItemsIntoSwipeSelector(Type.getGameTypesList(activity), gameTypeSelector, R.id.game_type_swipe_selector);
        }
    }

    /**
     * Adds all operations of the selected user into a spinner
     */
    public void addItemsOnOperationIssueSpinner(List<OperationIssue> selectedOperationIssuesList, Spinner operationIssueSpinner) {
        if (operationIssueSpinner == null)
            operationIssueSpinner = (Spinner) findViewById(R.id.start_game_settings_operation_issue_spinner);

        List<String> list = new ArrayList<String>();
        if (selectedOperationIssuesList != null) {
            if (selectedOperationIssuesList.size() > 0) {
                for (OperationIssue issue : selectedOperationIssuesList) {
                    list.add(issue.getDisplayName());
                }
            } else {
                list.add(getResources().getString(R.string.create_automatic));
            }
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        operationIssueSpinner.setAdapter(dataAdapter);
    }

    /**
     * Helper method for adding Strings into a swipe selector
     */
    public void addItemsIntoSwipeSelector(SwipeItem[] items, SwipeSelector selector, int rId) {
        if (selector == null)
            selector = (SwipeSelector) findViewById(rId);
        selector.setItems(items);
    }

    /**
     * Initializes UI elements
     */
    private void initGuiElements() {
        medicalUserSpinner = (Spinner) findViewById(R.id.start_game_settings_medicalid_spinner);
        operationIssueSpinner = (Spinner) findViewById(R.id.start_game_settings_operation_issue_spinner);
        testTypeSelector = (SwipeSelector) findViewById(R.id.test_type_swipe_selector);
        gameTypeSelector = (SwipeSelector) findViewById(R.id.game_type_swipe_selector);
        noUserInDBTextView = (TextView) findViewById(R.id.start_game_settings_no_user_in_db_textview);
        addOperationIssueBtn = (Button) findViewById(R.id.start_game_settings_add_operationBtn);
    }

    /**
     * Sets all a users operations into a list and displays results in a spinnr asynchronous
     */
    private void initOperationIssueListAsync() {
        new OperationIssueManager.getAllOperationIssuesByMedicoIdAsync(new OperationIssueManager.AsyncResponse() {

            @Override
            public void getAllOperationIssuesByMedicoId(List<OperationIssue> operationIssuesList) {
                selectedOperationIssuesList = operationIssuesList;
                addItemsOnOperationIssueSpinner(operationIssuesList, operationIssueSpinner);
                UtilsRG.info("Operation issues loaded for user(" + selectedMedicalUserId + ")=" + operationIssuesList.toString());
            }

        }, getApplicationContext()).execute(selectedMedicalUserId);
    }

    /**
     * Adds all existing users into a spinner asynchronous
     */
    private void initMedicalUserSpinnerAsync() {
        new MedicalUserManager.getAllMedicalUsers(new MedicalUserManager.AsyncResponse() {

            @Override
            public void getMedicalUserList(List<MedicalUser> medicalUserResultList) {
                initMedicalUserSpinner(medicalUserResultList);
            }

        }, getApplicationContext()).execute();
    }

    /**
     * Uses adapter to display some attributes per user in the spinner
     */
    private void initMedicalUserSpinner(List<MedicalUser> userList) {
        boolean isEmptyUserList = true;
        if (userList != null) {
            if (userList.size() > 0) {
                isEmptyUserList = false;
            }

            // attributes to display per item in spinner
            String[] medicalIds = new String[userList.size()];
            String[] birthDates = new String[userList.size()];
            int[] images = new int[userList.size()];


            // load those attributes
            for (int i = 0; i < userList.size(); i++) {
                medicalIds[i] = userList.get(i).getMedicalId();
                birthDates[i] = userList.get(i).getBirthDateAsString();
                images[i] = userList.get(i).getImage();
            }

            // use adapter to define UI order
            MedicalUserSpinnerAdapter adapter = new MedicalUserSpinnerAdapter(this, medicalIds, birthDates, images);
            medicalUserSpinner.setAdapter(adapter);

            // add click listener
            medicalUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedMedicalUserId = parent.getItemAtPosition(position).toString();
                    initOperationIssueListAsync();
                    UtilsRG.info("selected item: " + selectedMedicalUserId);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
        if (isEmptyUserList && noUserInDBTextView != null) {
            noUserInDBTextView.setText(R.string.no_user_in_db);
            medicalUserSpinner.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Displays dialog to add new operation for the selected user
     */
    public void onAddOperationIssueBtnCLick(View view) {
        UtilsRG.info("open Add Item Dialog");
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddOperationIssueFragment inputNameDialog = new AddOperationIssueFragment();
        inputNameDialog.setCancelable(false);
        inputNameDialog.show(fragmentManager, null);
    }

    /**
     * If user typed new operation name --> save new opration issue in database
     */
    @Override
    public void onFinishInputDialog(final String inputText) {
        if (inputText != null && !inputText.equals("")) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    new OperationIssueManager(getApplicationContext()).insertOperationIssueByMedIdAsync(selectedMedicalUserId, inputText);
                    initMedicalUserSpinnerAsync();
                    return null;
                }
            }.execute();
        }
    }

    /**
     * start the game intent and transmit settings to game intent
     */
    public void onStartGameBtnClick(View view) {
        String medicalUserId = selectedMedicalUserId;
        String operationIssueName = "";
        String testType = "";
        String gameType = "";

        boolean isCreateAutomatic = (operationIssueSpinner.getSelectedItem().equals(getResources().getString(R.string.create_automatic)));
        if(operationIssueSpinner == null)
            operationIssueSpinner = (Spinner) findViewById(R.id.start_game_settings_operation_issue_spinner);

        if ((operationIssueSpinner.getSelectedItem() == null) || isCreateAutomatic) {
            operationIssueName = getString(R.string.auto_genrated) + " " + UtilsRG.dayAndhourFormat.format(new Date());

            final String finalOperationIssueName = operationIssueName;
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... unusedParams) {
                    OperationIssueManager db = new OperationIssueManager(getApplicationContext());
                    if (db != null) {
                        db.insertOperationIssueByMedIdAsync(selectedMedicalUserId, finalOperationIssueName);
                    }
                    return null;
                }
            }.execute();
        } else {
            operationIssueName = operationIssueSpinner.getSelectedItem().toString();
        }

        if (testTypeSelector != null) {
            SwipeItem selectedItem = testTypeSelector.getSelectedItem();
            Type.TestTypes typeTest = Type.getTestType((Integer) selectedItem.value);
            testType = Type.getTestType(typeTest);
        }

        if (gameTypeSelector != null) {
            SwipeItem selectedItem = gameTypeSelector.getSelectedItem();
            Type.GameTypes gameTest = Type.getGameType((Integer) selectedItem.value);
            gameType = Type.getGameType(gameTest);
        }

        UtilsRG.info("User(" + medicalUserId + ") with operation name(" + operationIssueName + "). Test type=" + testType + ", GameType=" + gameType);

        startGameActivityByTypes(medicalUserId, operationIssueName, gameType, testType);
    }

    private void startGameActivityByTypes(String medicalUserId, String operationIssueName, String gameType, String testType) {
        if (gameType != null && testType != null) {
            UtilsRG.putString(UtilsRG.MEDICAL_USER, medicalUserId, this);
            UtilsRG.putString(UtilsRG.OPERATION_ISSUE, operationIssueName, this);
            UtilsRG.putString(UtilsRG.GAME_TYPE, gameType, this);
            UtilsRG.putString(UtilsRG.TEST_TYPE, testType, this);

            if (Type.TestTypes.InOperation.name() == testType) {
                Intent goNoGoGameIntent = new Intent(this, OperationModeView.class);
                startActivity(goNoGoGameIntent);
            } else if (Type.GameTypes.GoGame.name() == gameType) {
                Intent intent = new Intent(this, GoGameView.class);
                intent.putExtra(StartGameSettings.EXTRA_MEDICAL_USER_ID, medicalUserId);
                intent.putExtra(StartGameSettings.EXTRA_OPERATION_ISSUE_NAME, operationIssueName);
                intent.putExtra(StartGameSettings.EXTRA_GAME_TYPE, gameType);
                intent.putExtra(StartGameSettings.EXTRA_TEST_TYPE, testType);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            } else if (Type.GameTypes.GoNoGoGame.name() == gameType) {
                Intent goNoGoGameIntent = new Intent(this, GoNoGoGameView.class);
                goNoGoGameIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(goNoGoGameIntent);
            }
        }
    }

    //TODO: delete
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        UtilsRG.info("onCreateOptionsMenu()");
        MenuInflater menuIf = getMenuInflater();
        menuIf.inflate(R.menu.launcher_options_menu, menu);
        return true;
    }

    //TODO: delte
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
