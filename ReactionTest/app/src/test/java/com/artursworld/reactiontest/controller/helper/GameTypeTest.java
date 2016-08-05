package com.artursworld.reactiontest.controller.helper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GameTypeTest {

    @Test
    public void type_isCorrect() throws Exception {
        Type.TestTypes type = Type.TestTypes.GoGame;
        assertEquals("Cheking types",Type.TestTypes.GoGame, type);
    }

    @Test
    public void getTypeById() throws Exception {
        Type.TestTypes type= Type.getType(Type.TestTypes.GoGame.getId());
        assertEquals("Cheking types",Type.TestTypes.GoGame, type);
    }

    @Test
    public void getTypeToString() throws Exception {
        String type = Type.getType(Type.TestTypes.GoGame);
        assertEquals("Cheking types","GoGame", type);
    }

}
