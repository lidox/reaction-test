package com.artursworld.reactiontest.model;

import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import com.artursworld.reactiontest.model.contracts.DBContracts;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.manager.MedicalUserManager;
import com.artursworld.reactiontest.model.manager.ReactionGameManager;
import com.artursworld.reactiontest.controller.util.UtilsRG;

import org.junit.Test;

import java.util.Date;

public class MedicalUserManagerTest extends InstrumentationTestCase {

    private DBContracts.DatabaseHelper db;
    MedicalUserManager medicalUserManager;
    private RenamingDelegatingContext context;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
        db = new DBContracts.DatabaseHelper(context);
    }

    @Override
    public void tearDown() throws Exception {
        db.close();
        super.tearDown();
    }

    @Test
    public void testOnDeleteCascade() throws Exception {
        medicalUserManager = new MedicalUserManager(context);
        MedicalUser medUser = new MedicalUser();
        medUser.setMedicalId("Medico" + ( (int) (Math.random() * 100000000) ) );
        Date tomorrow = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));
        medUser.setBirthDate(tomorrow);
        medUser.setGender("male");

        // insert / create
        medicalUserManager.insert(medUser);

        // set up reaction game
        ReactionGameManager reactionGameManager = new ReactionGameManager(context);
        ReactionGame game = new ReactionGame();
        game.setDuration(600);
        game.setMedicalUser(medUser);
        game.setReationType("muscular");

        // insert reaction game
        UtilsRG.log.info("inserting for medicaluser:"+medUser);
        reactionGameManager.insert(game);

        UtilsRG.log.info("again inserting for user with id:"+medUser);
        ReactionGame game2 = new ReactionGame(medUser);
        game.setCreationDate(new Date());
        game.setDuration(500);
        game.setMedicalUser(medUser);
        game.setReationType("frontal");
        reactionGameManager.insert(game2);

        assertEquals(2, reactionGameManager.getReactionGamesByMedicalUser(medUser).size());

        // delete medical user and hopefully all its reactiongames
        UtilsRG.log.info("delete medical user");
        medicalUserManager.delete(medUser);

        assertEquals(0, reactionGameManager.getReactionGamesByMedicalUser(medUser).size());
    }

    @Test
    public void testGetAllReactionGames() throws Exception {
        medicalUserManager = new MedicalUserManager(context);
        MedicalUser medUser = new MedicalUser();
        medUser.setMedicalId("Medico" + ( (int) (Math.random() * 100000000) ) );
        Date tomorrow = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));
        medUser.setBirthDate(tomorrow);
        medUser.setGender("male");

        // insert / create
        medicalUserManager.insert(medUser);

        // set up reaction game
        ReactionGameManager reactionGameManager = new ReactionGameManager(context);
        ReactionGame game = new ReactionGame(medUser);

        // insert reaction game
        reactionGameManager.insert(game);

        game = new ReactionGame(medUser);
        reactionGameManager.insert(game);

        assertEquals(2, reactionGameManager.getAllReactionGames().size());
    }

    @Test
    public void testCreateUser() throws Exception {
        medicalUserManager = new MedicalUserManager(context);
        MedicalUser medUser = new MedicalUser();
        medUser.setMedicalId("myFirstMedicalIdUser" + ( (int) (Math.random() * 100000000) ) );
        Date tomorrow = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));
        medUser.setBirthDate(tomorrow);
        int medicalUserCountBeforeTest = medicalUserManager.getAllMedicalUsers().size();

        // insert
        UtilsRG.log.info("insert user with medico_id=" + medUser.getMedicalId());
        medicalUserManager.insert(medUser);

        UtilsRG.log.info("database contains following users:");
        for(MedicalUser user : medicalUserManager.getAllMedicalUsers()){
            UtilsRG.log.info(user.getMedicalId());
        }

        String assertMessage = "Create user and check users count before and after insertation";
        assertEquals(assertMessage,(medicalUserCountBeforeTest+1), medicalUserManager.getAllMedicalUsers().size());
    }
}


