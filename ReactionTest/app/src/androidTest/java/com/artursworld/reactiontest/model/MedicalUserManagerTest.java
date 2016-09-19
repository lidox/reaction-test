package com.artursworld.reactiontest.model;

import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import com.artursworld.reactiontest.controller.helper.Gender;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;

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
        medicalUserManager = new MedicalUserManager(context);
    }

    @Override
    public void tearDown() throws Exception {
        db.close();
        super.tearDown();
    }

    @Test
    public void testCreateAndDeleteUser() throws Exception {
        // create user to be inserted into database
        MedicalUser medUser = new MedicalUser();
        String medIdToInsert = "0123456789" + ( (int) (Math.random() * 100000000) );
        medUser.setMedicalId(medIdToInsert);// + ( (int) (Math.random() * 100000000) ) );
        Date birthDateYersterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        medUser.setBirthDate(birthDateYersterday);
        medUser.setBmi(29.9);
        medUser.setGender(Gender.FEMALE);

        // insert
        medicalUserManager.insert(medUser);


        String resultID = null;
        MedicalUser resultUser = medicalUserManager.getUserByMedicoId(medIdToInsert);
        assertNotNull("Check get user by id", resultUser);
        if (resultUser != null)
            resultID = resultUser.getMedicalId();

        String assertMessage = "Create user and check if exists in database";
        assertEquals(assertMessage,medIdToInsert, resultID);

        // delete user
        int resultCode = medicalUserManager.deleteUserById(medIdToInsert);
        String assertMessageFromDelete = "Delete user and check the database result code";
        assertTrue(assertMessageFromDelete, resultCode > -1);
    }

    @Test
    public void testOnDeleteCascade() throws Exception {
        /*
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
        //game.setMedicalUser(medUser);
        //game.setReationType("muscular");

        // insert reaction game
        UtilsRG.log.info("inserting for medicaluser:"+medUser);
        reactionGameManager.insert(game);

        UtilsRG.log.info("again inserting for user with id:"+medUser);
        //ReactionGame game2 = new ReactionGame(medUser);
        game.setCreationDate(new Date());
        game.setDuration(500);
        //game.setMedicalUser(medUser);
        //game.setReationType("frontal");
        //reactionGameManager.insert(game2);

        assertEquals(2, reactionGameManager.getReactionGamesByMedicalUser(medUser).size());

        // delete medical user and hopefully all its reactiongames
        UtilsRG.log.info("delete medical user");
        medicalUserManager.delete(medUser);

        assertEquals(0, reactionGameManager.getReactionGamesByMedicalUser(medUser).size());
        */
    }

    @Test
    public void testGetAllReactionGames() throws Exception {
        /*
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
       // ReactionGame game = new ReactionGame(medUser);

        // insert reaction game
       // reactionGameManager.insert(game);

        //game = new ReactionGame(medUser);
        //reactionGameManager.insert(game);

        assertEquals(2, reactionGameManager.getAllReactionGames().size());
        */
    }

    @Test
    public void testCreateAndMarkAsDeletedUser() throws Exception {
        // create user to be inserted into database
        MedicalUser medUser = new MedicalUser();
        String medIdToInsert = "Dude_" + ( (int) (Math.random() * 100000000) );
        medUser.setMedicalId(medIdToInsert);
        Date birthDateYesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        medUser.setBirthDate(birthDateYesterday);
        medUser.setBmi(22);
        medUser.setGender(Gender.MALE);

        // insert
        medicalUserManager.insert(medUser);


        MedicalUser resultUser = medicalUserManager.getUserByMedicoId(medIdToInsert);
        assertFalse("Check that user not marked yet",resultUser.isMarkedAsDeleted());


        medicalUserManager.markUserAsDeletedById(resultUser, true);

        MedicalUser user = medicalUserManager.getUserByMedicoId(medIdToInsert);
        assertTrue("Check that user marked as deleted now",user.isMarkedAsDeleted());
    }
}


