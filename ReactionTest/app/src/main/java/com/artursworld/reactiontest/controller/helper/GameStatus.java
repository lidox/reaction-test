package com.artursworld.reactiontest.controller.helper;

/**
 * Holds the status of a game .
 * enum --> int: yourEnum.ordinal()
 * int --> enum: EnumType.values()[someInt]
 * String --> enum: EnumType.valueOf(yourString)
 * enum --> String yourEnum.name()
 */
public enum GameStatus {

    // attention, user need to wait --> get ready
    WAITING,

    // now user need to click as fast as possible --> shoot
    CLICK,

    // hit, user clicked at the right moment
    HIT,

    // too early, color changed to wrong color, so user don't need to click --> miss
    WRONG_COLOR,

    // display 'satisfaction' UI elements --> outro
    SATISFACTION


}
