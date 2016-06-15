package com.artursworld.reactiontest.model;

import android.app.Instrumentation;
import android.content.Context;
import android.test.RenamingDelegatingContext;

import com.artursworld.reactiontest.entity.MedicalUser;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertNotEquals;


@RunWith(JUnit4.class)
public class MedicalUserManagerTest {

    MedicalUserManager medicalUserManager;
    Context mMockContext;

    @Before
    public void setUp() {
        //mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
    }

    public class ExampleUnitTest {
        @Test
        public void addition_isCorrect() throws Exception {
            medicalUserManager = new MedicalUserManager(mMockContext);

            MedicalUser newUser = new MedicalUser();
            newUser.setMedicalId("JUnitMedicalIdUser");
            newUser.setGender("maennlich");

            long result = medicalUserManager.insert(newUser);
            assertNotEquals(-1L, result);
        }
    }

}
