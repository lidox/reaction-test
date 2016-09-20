package com.artursworld.reactiontest.model;


import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.Gender;

import org.junit.Test;

public class GenderTest extends InstrumentationTestCase {

    private RenamingDelegatingContext context;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
    }

    @Test
    public void testGetGenderByIndex() {
        Gender male = Gender.findByIndex(0);
        assertEquals("Checking gender",Gender.MALE, male);
    }

    @Test
    public void testGetGenderByTranslation(){
        String femaleTranslated = context.getResources().getString(R.string.female);
        Gender gender = Gender.findByTranslationText(femaleTranslated);
        assertEquals("Checking gender", Gender.FEMALE, gender);
    }


    @Test
    public void testGetGenderByResourceId(){
        Gender gender = Gender.findByResourceId(R.string.female);
        assertEquals("Checking gender", Gender.FEMALE, gender);
    }

    @Test
    public void testGetGenderByName(){
        Gender gender = Gender.findByName("Male");
        assertEquals("Checking gender", Gender.MALE, gender);
    }

}
