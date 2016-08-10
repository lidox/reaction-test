package com.artursworld.reactiontest.view.games;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.GameStatus;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;

import java.util.Date;

public class GoNoGoGameView extends AppCompatActivity {

    // by the app user selected attributes
    private String medicalUserId;
    private String operationIssueName;
    private String testType;
    private String gameType;

    // game attributes
    private String reactionGameId;
    private GameStatus currentGameStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_no_go_game_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UtilsRG.info("onResume " + GoNoGoGameView.class.getSimpleName());

        // init attributes
        initSelectedGameAttributes();
        initReactionGameId(new Date());

        // init game UI stuff
        setGameStatusAndDisplayUIElements(GameStatus.WAITING);
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
     * Creates a new reaction game via database, if not created yet
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

    /**
     * Sets game status and displays corresponding UI Elements for the user
     *
     * @param gameStatus the current game status
     */
    public void setGameStatusAndDisplayUIElements(GameStatus gameStatus) {
        this.currentGameStatus = gameStatus;
        if (gameStatus == GameStatus.WAITING) {
            UtilsRG.setBackgroundColor(this, R.color.goGameBlue);
        }
    }

}
