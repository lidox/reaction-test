package com.artursworld.reactiontest.controller.helper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GenderTest {

    @Test
    public void getGenderByString() {
        Gender male = Gender.valueOf("MALE");
        assertEquals("Checking gender",Gender.MALE, male);
    }

    @Test
    public void getStringByGender(){
        String gender = Gender.MALE.name();
        assertEquals("Checking gender", "MALE", gender);
    }
}
