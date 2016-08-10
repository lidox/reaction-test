package com.artursworld.reactiontest.view.games;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;

import java.util.Date;

public class GoNoGoGameView extends AppCompatActivity {

    // by the app user selected attributes
    private String reactionGameId;
    private String medicalUserId;
    private String operationIssueName;
    private String testType;
    private String gameType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_no_go_game_view);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        UtilsRG.info("onResume " + GoNoGoGameView.class.getSimpleName());
        initSelectedGameAttributes();
        initReactionGameId(new Date());
    }

    /**
     * Initializes by the user selected game attributes
     */
    private void initSelectedGameAttributes() {
        Activity activity = this;
        if (activity != null) {
            if (medicalUserId == null) {
                medicalUserId = UtilsRG.getStringByKey(UtilsRG.MEDICAL_USER, this);
            }
            if (operationIssueName == null) {
                operationIssueName = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, this);
            }
            if (gameType == null) {
                gameType = UtilsRG.getStringByKey(UtilsRG.GAME_TYPE, this);
            }
            if (testType == null) {
                testType = UtilsRG.getStringByKey(UtilsRG.TEST_TYPE, this);
            }
            if (reactionGameId == null) {
                reactionGameId = UtilsRG.getStringByKey(UtilsRG.REACTION_GAME_ID, this);
            }
            UtilsRG.info("Received user(" + medicalUserId + "), operation name(" + operationIssueName + ")");
            UtilsRG.info("Test type=" + testType + ", GameType=" + gameType + ", reactionGameId=" + reactionGameId);
        }
    }

    /**
     * Creates a new reaction game via database
     *
     * @param date the date timestamp equals the id (primary key) of the reaction game
     */
    private void initReactionGameId(Date date) {
        if (reactionGameId == null) {
            reactionGameId = UtilsRG.dateFormat.format(date);
            if ((getApplicationContext() != null) && (reactionGameId != null)) {
                ReactionGameManager db = new ReactionGameManager(getApplicationContext());
                db.insertReactionGameByOperationIssueNameAsync(reactionGameId, operationIssueName, gameType, testType);
                UtilsRG.putString(UtilsRG.REACTION_GAME_ID, reactionGameId, this);
            }
        }
    }
}
