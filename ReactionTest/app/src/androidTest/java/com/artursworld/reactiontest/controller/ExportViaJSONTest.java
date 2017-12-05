package com.artursworld.reactiontest.controller;

import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import com.artursworld.reactiontest.controller.export.ExportViaJSON;
import com.artursworld.reactiontest.controller.helper.Gender;
import com.artursworld.reactiontest.controller.importer.ImportViaJSON;
import com.artursworld.reactiontest.controller.importer.JsonUser;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.Medicament;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.model.persistence.manager.MedicamentManager;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;

import org.json.JSONArray;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * Testing the JSON export function
 */
public class ExportViaJSONTest extends InstrumentationTestCase {

    private RenamingDelegatingContext context;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
    }

    @Test
    public void testGetJSON() throws InterruptedException {
        //TODO: not implemented yet
        // create user to be inserted into database
        MedicalUser medUser = new MedicalUser();
        String medIdToInsert = ""+( (int) (Math.random() * 100000000) );
        medUser.setMedicalId(medIdToInsert);
        Date birthDateYesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        medUser.setBirthDate(birthDateYesterday);
        medUser.setBmi(29.9);
        medUser.setGender(Gender.FEMALE);

        // insert
        new MedicalUserManager(context).insert(medUser);
        String operationName = "bla";
        new OperationIssueManager(context).insertOperationIssueByMedIdAsync(medUser.getMedicalId(), operationName);
        Thread.sleep(3000);
        new MedicamentManager(context).insertMedicament(new Medicament(operationName, "test", 23, "d", new Date()));
        String JSON = new ExportViaJSON(context).getJSONString(context);
        System.out.println(JSON);
        //assertEquals("Checking gender",Gender.MALE, male);
    }
}
