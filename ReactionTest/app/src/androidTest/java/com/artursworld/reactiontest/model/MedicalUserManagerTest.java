package com.artursworld.reactiontest.model;

import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import com.artursworld.reactiontest.controller.helper.Gender;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;

import org.junit.Test;

import java.util.Date;
import java.util.List;

public class MedicalUserManagerTest extends InstrumentationTestCase {

    private DBContracts.DatabaseHelper db;
    MedicalUserManager medicalUserManager;
    private RenamingDelegatingContext context;
    private OperationIssueManager opManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
        db = new DBContracts.DatabaseHelper(context);
        medicalUserManager = new MedicalUserManager(context);
        opManager = new OperationIssueManager(context);
    }

    @Override
    public void tearDown() throws Exception {
        db.close();
        super.tearDown();
    }

    @Test
    public void test1(){
        String key = "Bar12345Bar12345"; // 128 bit key
        String initVector = "RandomInitVector"; // 16 bytes IV
        //String key = "D3C8C8778D581F3C4136CFD296F97"; // 128 bit key
        //String initVector = "RandomInitVector"; // 16 bytes IV


        String encryptedString = Encryptor.encrypt(key, initVector, "Hello World");
        String decryptedString = Encryptor.decrypt(key, initVector, encryptedString);
        System.out.println(decryptedString);
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

        medicalUserManager = new MedicalUserManager(context);
        MedicalUser medUser = new MedicalUser();
        medUser.setMedicalId("Medico" + ( (int) (Math.random() * 100000000) ) );
        Date tomorrow = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));
        medUser.setBirthDate(tomorrow);
        medUser.setGender(Gender.FEMALE);

        // insert / create
        medicalUserManager.insert(medUser);

        // set up reaction game
        ReactionGameManager reactionGameManager = new ReactionGameManager(context);
        ReactionGame game = new ReactionGame();
        game.setDuration(600);

        // insert reaction game
        UtilsRG.log.info("inserting for medicaluser:"+medUser);


        // add operation issue
        OperationIssueManager issueManager = new OperationIssueManager(context);
        String operationIssue = "Test";
        issueManager.insertOperationIssueByMedIdAsync(medUser.getMedicalId(), operationIssue);
        Thread.sleep(3000);
        reactionGameManager.insertReactionGameByOperationIssueNameAsync(UtilsRG.dateFormat.format(new Date()), operationIssue, "gogame",  "preop");


        //assertEquals(1, reactionGameManager.getReactionGameList(operationIssue, "gogame", "preop", "ASC" ).size());

        // delete medical user and hopefully all its reactiongames
        UtilsRG.log.info("delete medical user");
        medicalUserManager.deleteUserById(medUser.getMedicalId());

        //assertEquals(0, reactionGameManager.getReactionGameList(operationIssue, "gogame", "preop", "ASC" ).size());

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

        medicalUserManager.markUserAsDeletedById(resultUser.getMedicalId(), true);

        MedicalUser user = medicalUserManager.getUserByMedicoId(medIdToInsert);
        assertTrue("Check that user marked as deleted now",user.isMarkedAsDeleted());
    }

    @Test
    public void testUpdateUser() throws Exception {
        // create user to be inserted into database
        MedicalUser medUser = new MedicalUser();
        String medIdToInsert = "Dude_To_Update" + ( (int) (Math.random() * 100000000) );
        medUser.setMedicalId(medIdToInsert);
        Date birthDateYesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        medUser.setBirthDate(birthDateYesterday);
        medUser.setBmi(22);
        medUser.setGender(Gender.MALE);
        medUser.setMarkedAsDeleted(false);

        // insert
        medicalUserManager.insert(medUser);


        MedicalUser resultUser = medicalUserManager.getUserByMedicoId(medIdToInsert);
        assertEquals("Check that user has been created",medIdToInsert, resultUser.getMedicalId());

        // modify user
        resultUser.setMarkedAsDeleted(true);
        resultUser.setBmi(10);
        resultUser.setGender(Gender.MALE);
        String newId = "Muhahaha";
        resultUser.setMedicalId(newId);

        //medicalUserManager.updateMedicalUserByCreationDate(resultUser);

        //MedicalUser user = medicalUserManager.getUserByMedicoId(newId);
        //assertTrue("Check that user marked as deleted now",user.isMarkedAsDeleted());
        //assertEquals(10.0, user.getBmi());
        //assertEquals(Gender.MALE, user.getGender());
        //assertEquals(newId, user.getMedicalId());
    }

    @Test
    public void testCreateUpdateUserByOperationIssueIncludingUpdateCascade() throws Exception {
        // create user to be inserted into database
        MedicalUser medUser = new MedicalUser();

        String id = "User" + ((int) (Math.random() * 100000000));
        medUser.setMedicalId(id);

        Date birthDateYersterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        medUser.setBirthDate(birthDateYersterday);

        medUser.setBmi(29.9);
        medUser.setGender(Gender.FEMALE);

        // insert
        medicalUserManager.insert(medUser);

        String resultID = null;
        MedicalUser resultUser = medicalUserManager.getUserByMedicoId(id);
        assertNotNull("Check get user by id", resultUser);
        if (resultUser != null)
            resultID = resultUser.getMedicalId();

        String assertMessage = "Create user and check if exists in database";
        assertEquals(assertMessage, id, resultID);

        // -----------------
        String a = DBContracts.CREATE_OPERATION_ISSUE_TABLE;
        String b = DBContracts.CREATE_MEDICAL_USER_TABLE;
        opManager.insertOperationIssueByMedIdAsync(resultID, "super hot issue");




        // update user
        String newId = "hans dieter";
        resultUser.setMedicalId(newId);
        Thread.sleep(1000);
        medicalUserManager.updateMedicalUser(resultUser);
        MedicalUser updatedUser = medicalUserManager.getUserByMedicoId(newId);

        assertNotNull(updatedUser);

        // check update cascade
        List<OperationIssue> list = opManager.getAllOperationIssuesByMedicoId(updatedUser.getMedicalId());
        assertTrue(list.size() > 0);

        List<OperationIssue> list2 = opManager.getAllOperationIssuesByMedicoId(id);
        assertTrue(list2.size() == 0);
    }



}


