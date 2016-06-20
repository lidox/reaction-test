package com.artursworld.reactiontest.model;

import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import com.artursworld.reactiontest.entity.MedicalUser;
import com.artursworld.reactiontest.entity.ReactionGame;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import ch.qos.logback.classic.android.BasicLogcatConfigurator;

public class MedicalUserManagerTest extends InstrumentationTestCase {
    // Logging
    static {
        BasicLogcatConfigurator.configureDefaultContext();}
    private Logger log = LoggerFactory.getLogger(MedicalUserManager.class);

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
    public void testInsertUser() throws Exception {
        medicalUserManager = new MedicalUserManager(context);
        MedicalUser medUser = new MedicalUser();
        medUser.setMedicalId("myFirstMedicalIdUser" + ( (int) (Math.random() * 100000000) ) );
        Date tomorrow = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));
        medUser.setBirthDate(tomorrow);
        medUser.setGender("Mänlich");

        // insert
        log.info("insert");
        medicalUserManager.insert(medUser);

        for(MedicalUser user : medicalUserManager.getMedicalUsers()){
            log.info(user.toString());
        }

        // update Test
        log.info("update");
        medUser.setGender("weiblich");
        medicalUserManager.update(medUser);

        for(MedicalUser user : medicalUserManager.getMedicalUsers()){
            log.info(user.toString());
        }

        // set up reaction game
        ReactionGameManager reactionGameManager = new ReactionGameManager(context);
        ReactionGame game = new ReactionGame();
        game.setDuration(600);
        game.setMedicalUser(medUser);
        game.setReationType("muscular");

        // insert reaction game
        log.info("insert 2 reaction games");
        reactionGameManager.insert(game);

        game = new ReactionGame();
        // TODO: refactor: new ReactionGame(medUser)
        //Date tomorrow = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));
        //game.setCreationDate(tomorrow);
        game.setMedicalUser(medUser);
        reactionGameManager.insert(game);

        for(ReactionGame gameItem :  reactionGameManager.getReactionGamesByMedicalUser(medUser)){
            log.info(gameItem.toString());
        }

        // delete reaction game
        log.info("delete reaction game");
        reactionGameManager.delete(game);
        for(ReactionGame gameItem :  reactionGameManager.getReactionGamesByMedicalUser(medUser)){
            log.info(gameItem.toString());
        }


        // delete medical user
        log.info("delete medical user");
        medicalUserManager.delete(medUser);

        for(MedicalUser user : medicalUserManager.getMedicalUsers()){
            log.info(user.toString());
        }

        int allUserCount =  medicalUserManager.getMedicalUsers().size();
        assertEquals(allUserCount, 0);
    }

    //According to Zainodis annotation only for legacy and not valid with gradle>1.1:
    //@Test
    public void testAddEntry(){
        System.out.print("testAddEntry");
        assertEquals("hallo", "hallo");
        // Here i have my new database wich is not connected to the standard database of the App
    }
}


