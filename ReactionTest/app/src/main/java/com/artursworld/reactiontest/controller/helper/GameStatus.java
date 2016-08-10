package com.artursworld.reactiontest.controller.helper;

/*
* Holds the status of a game 
*/
public enum GameStatus {

    // user need to wait
    WAITING,

    // color changed to wrong color, so user don't need to click
    WRONG_COLOR,

    // now user need to click as fast as possible
    CLICK
}
