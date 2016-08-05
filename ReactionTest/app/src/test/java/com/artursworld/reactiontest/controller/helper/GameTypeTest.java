package com.artursworld.reactiontest.controller.helper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GameTypeTest {

    @Test
    public void type_isCorrect() throws Exception {
        Type.GameTypes type = Type.GameTypes.GoGame;
        assertEquals("Cheking types",Type.GameTypes.GoGame, type);
    }

    @Test
    public void getTypeById() throws Exception {
        Type.GameTypes type= Type.getGameType(Type.GameTypes.GoGame.getId());
        assertEquals("Cheking types",Type.GameTypes.GoGame, type);
    }

    @Test
    public void getTypeToString() throws Exception {
        String type = Type.getGameType(Type.GameTypes.GoGame);
        assertEquals("Cheking types","GoGame", type);
    }

}
