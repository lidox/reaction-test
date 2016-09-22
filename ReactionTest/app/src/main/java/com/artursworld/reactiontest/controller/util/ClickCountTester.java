package com.artursworld.reactiontest.controller.util;


import android.view.MotionEvent;

public class ClickCountTester {

    private long usersTapStartTime = 0;
    private int usersTapCount = 0;

    /**
     * Check if user click too often
     *
     * @param withinXseconds the seconds within the user can click max times
     * @param maxClickCount  the maximal click count within the seconds
     * @return true if clicked to often, otherwise false
     */
    public boolean checkClickCount(int withinXseconds, int maxClickCount) {
        //get system current milliseconds
        long time = System.currentTimeMillis();

        //if it is the first time, or if it has been more than 3 seconds since the first tap ( so it is like a new try), we reset everything
        if (usersTapStartTime == 0 || (time - usersTapStartTime > (withinXseconds * 1000))) {
            usersTapStartTime = time;
            usersTapCount = 1;
        }
        //it is not the first, and it has been  less than 3 seconds since the first
        else {
            usersTapCount++;
        }

        if (usersTapCount == maxClickCount) {
            UtilsRG.info("User taped " + maxClickCount + " times within " + withinXseconds + " seconds");
            return true;
        }
        return false;
    }

}
