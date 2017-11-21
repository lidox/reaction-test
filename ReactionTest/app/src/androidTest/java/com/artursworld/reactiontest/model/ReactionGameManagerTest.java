package com.artursworld.reactiontest.model;

import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import com.artursworld.reactiontest.controller.export.ExportJson2CSV;
import com.artursworld.reactiontest.controller.export.ExportViaJSON;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.artursworld.reactiontest.model.persistence.manager.TrialManager;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReactionGameManagerTest extends InstrumentationTestCase {

    ReactionGameManager reactionGameManager;
    private RenamingDelegatingContext context;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
        reactionGameManager = new ReactionGameManager(context);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void test1() throws JSONException {
        try {
            String outputString = ExportViaJSON.getJSONString(context);
            List<String[]> exportableCSVList = ExportJson2CSV.getExportableCSVList(outputString);
            String fileNameToCreate = "reaction-data" + new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date());
            File file = File.createTempFile("gotit", ".csv");
            ExportJson2CSV.exportAsCSV(file, fileNameToCreate, exportableCSVList);
            Assert.assertTrue(exportableCSVList.size() > 0);
        } catch (Exception e) {
            UtilsRG.error("Cannot parse data: " + e.getLocalizedMessage());
        }
    }

    @Test
    public void test2() {
        List<String[]> testList = reactionGameManager.getAllReactionGameRecords();
        Assert.assertTrue(testList != null);
    }

    @Test
    public void test3() throws InterruptedException {

        // create user
        MedicalUserManager userDB = new MedicalUserManager(context);
        MedicalUser medicalUser = new MedicalUser();
        medicalUser.setMedicalId("UserName");
        userDB.insert(medicalUser);

        // create operation issue
        OperationIssueManager opDB = new OperationIssueManager(context);
        String operationIssueName = "OPISSUE";
        opDB.insertOperationIssueByMedId(medicalUser.getMedicalId(), operationIssueName);

        // create RT game
        ReactionGameManager rtDB = new ReactionGameManager(context);
        ReactionGame game = new ReactionGame();
        rtDB.insertReactionGameByOperationIssueName(game.getCreationDateFormatted(), Type.GameTypes.GoGame.name(), Type.TestTypes.InOperation.name(), operationIssueName);

        TrialManager trialDB = new TrialManager(context);
        trialDB.insertTrialtoReactionGameAsync(game.getCreationDateFormatted(), true, 300);
        trialDB.insertTrialtoReactionGameAsync(game.getCreationDateFormatted(), true, 288);
        trialDB.insertTrialtoReactionGameAsync(game.getCreationDateFormatted(), true, 1323);
        trialDB.insertTrialtoReactionGameAsync(game.getCreationDateFormatted(), true, 332);
        trialDB.insertTrialtoReactionGameAsync(game.getCreationDateFormatted(), true, 343);
        trialDB.insertTrialtoReactionGameAsync(game.getCreationDateFormatted(), true, 345);
        trialDB.insertTrialtoReactionGameAsync(game.getCreationDateFormatted(), true, 5346);
        trialDB.insertTrialtoReactionGameAsync(game.getCreationDateFormatted(), true, 236);

        Thread.sleep(2000);

        // get new reaction game by database
        ReactionGame newGame = rtDB.getReactionGameByDate(game.getCreationDate());

        // getReactionTimes
        double[] rt = newGame.getReactionTimesByDB();

        Assert.assertTrue(rt != null);

        double[] reactionTimes = new TrialManager(context).getAllReactionTimesFromDB();
        Assert.assertTrue(reactionTimes != null);
        Assert.assertTrue(reactionTimes.length == 8);
    }



}












































