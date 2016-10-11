package com.artursworld.reactiontest.controller;

import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import com.artursworld.reactiontest.controller.export.ExportViaJSON;
import com.artursworld.reactiontest.controller.helper.Gender;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;

import org.junit.Test;

import java.util.Date;

/**
 * Testing the JSON export function
 */
public class ExportViaJSONTest  extends InstrumentationTestCase {

    private RenamingDelegatingContext context;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
    }

    @Test
    public void testGetJSON() {
        //TODO: not implemented yet
        // create user to be inserted into database
        MedicalUser medUser = new MedicalUser();
        String medIdToInsert = "0123456789" + ( (int) (Math.random() * 100000000) );
        medUser.setMedicalId(medIdToInsert);// + ( (int) (Math.random() * 100000000) ) );
        Date birthDateYersterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        medUser.setBirthDate(birthDateYersterday);
        medUser.setBmi(29.9);
        medUser.setGender(Gender.FEMALE);

        // insert
        new MedicalUserManager(context).insert(medUser);

        new ExportViaJSON(context).export();
        //assertEquals("Checking gender",Gender.MALE, male);
    }
}
