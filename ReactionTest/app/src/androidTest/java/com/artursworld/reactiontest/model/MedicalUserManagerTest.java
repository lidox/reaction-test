package com.artursworld.reactiontest.model;

import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import com.artursworld.reactiontest.entity.MedicalUser;
import com.artursworld.reactiontest.entity.ReactionGame;
import com.artursworld.reactiontest.util.UtilsRG;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import ch.qos.logback.classic.android.BasicLogcatConfigurator;

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
        UtilsRG.log.info("insert");
        medicalUserManager.insert(medUser);

        // set up reaction game
        ReactionGameManager reactionGameManager = new ReactionGameManager(context);
        ReactionGame game = new ReactionGame();
        game.setDuration(600);
        game.setMedicalUser(medUser);
        game.setReationType("muscular");

        // insert reaction game
        //log.info("insert 2 reaction games");
        reactionGameManager.insert(game);

        game = new ReactionGame(medUser);
        reactionGameManager.insert(game);

        assertEquals(2, reactionGameManager.getAllReactionGames().size());

        // delete medical user and hopefully all its reactiongames
        UtilsRG.log.info("delete medical user");
        medicalUserManager.delete(medUser);

        assertEquals(0, reactionGameManager.getAllReactionGames().size());
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
        UtilsRG.log.info("insert");
        medicalUserManager.insert(medUser);

        // set up reaction game
        ReactionGameManager reactionGameManager = new ReactionGameManager(context);
        ReactionGame game = new ReactionGame();
        game.setDuration(600);
        game.setMedicalUser(medUser);
        game.setReationType("muscular");

        // insert reaction game
        //log.info("insert 2 reaction games");
        reactionGameManager.insert(game);

        game = new ReactionGame(medUser);
        reactionGameManager.insert(game);

        assertEquals(2, reactionGameManager.getAllReactionGames().size());
    }


    @Test
    public void testInsertUser() throws Exception {
        medicalUserManager = new MedicalUserManager(context);
        MedicalUser medUser = new MedicalUser();
        medUser.setMedicalId("myFirstMedicalIdUser" + ( (int) (Math.random() * 100000000) ) );
        Date tomorrow = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));
        medUser.setBirthDate(tomorrow);
        medUser.setGender("Mänlich");

        // insert
        UtilsRG.log.info("insert");
        medicalUserManager.insert(medUser);

        for(MedicalUser user : medicalUserManager.getMedicalUsers()){
            UtilsRG.log.info(user.toString());
        }

        // update Test
        UtilsRG.log.info("update");
        medUser.setGender("weiblich");
        medicalUserManager.update(medUser);

        for(MedicalUser user : medicalUserManager.getMedicalUsers()){
            UtilsRG.log.info(user.toString());
        }

        // set up reaction game
        ReactionGameManager reactionGameManager = new ReactionGameManager(context);
        ReactionGame game = new ReactionGame();
        game.setDuration(600);
        game.setMedicalUser(medUser);
        game.setReationType("muscular");

        // insert reaction game
        //log.info("insert 2 reaction games");
        reactionGameManager.insert(game);

        game = new ReactionGame(medUser);
        reactionGameManager.insert(game);

        for(ReactionGame gameItem :  reactionGameManager.getReactionGamesByMedicalUser(medUser)){
            UtilsRG.log.info(gameItem.toString());
        }

        // delete reaction game
        UtilsRG.log.info("delete reaction game");
        reactionGameManager.delete(game);
        for(ReactionGame gameItem :  reactionGameManager.getReactionGamesByMedicalUser(medUser)){
            UtilsRG.log.info(gameItem.toString());
        }


        // delete medical user
        UtilsRG.log.info("delete medical user");
        medicalUserManager.delete(medUser);

        for(MedicalUser user : medicalUserManager.getMedicalUsers()){
            UtilsRG.log.info(user.toString());
        }

        int allUserCount =  medicalUserManager.getMedicalUsers().size();
        assertEquals(allUserCount, 0);
    }
}


