package com.artursworld.reactiontest.controller.helper;


import android.app.Activity;

import com.artursworld.reactiontest.R;
import com.roughike.swipeselector.SwipeItem;

import junit.framework.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* Contains the game and test types in order to be able to translate the types
*/
public class Type {

    /*
    * All supported game types in the app
    */
    public enum GameTypes {
        GoGame(0),
        GoNoGoGame(1);

        private static final Map<String, GameTypes> lookupByName = new HashMap<String, GameTypes>();
        private final int id;

        static {
            for (GameTypes g : values()) {
                lookupByName.put(g.name(), g);
            }
        }

        public static GameTypes findByName(String name) {
            return lookupByName.get(name);
        }


        GameTypes(int value) {
            id = value;
        }

        public int getId() {
            return id;
        }
    }

    /*
    * All test types provided by the app
    */
    public enum TestTypes {
        PreOperation(0),
        InOperation(1),
        PostOperation(2),
        Trial(3);

        private static final Map<String, TestTypes> lookupByName = new HashMap<String, TestTypes>();
        private final int id;

        static {
            for (TestTypes g : values()) {
                lookupByName.put(g.name(), g);
            }
        }

        public static TestTypes findByName(String name) {
            return lookupByName.get(name);
        }

        TestTypes(int value) {
            id = value;
        }

        public int getId() {
            return id;
        }
    }

    public static GameTypes getGameType(int i) {
        switch (i) {
            case 0:
                return GameTypes.GoGame;
            case 1:
                return GameTypes.GoNoGoGame;
            default:
                return null;
        }
    }

    /*
    * Returns the translated game type to be displayed
    */
    public static String getTranslatedGameType(GameTypes type, Activity activity) {
        switch (type) {
            case GoGame:
                return activity.getResources().getString(R.string.go_game);
            case GoNoGoGame:
                return activity.getResources().getString(R.string.go_no_go_game);
            default:
                return null;
        }
    }

    /*
    * Returns the game type
    */
    public static String getGameType(GameTypes type) {
        switch (type) {
            case GoGame:
                return GameTypes.GoGame.name();
            case GoNoGoGame:
                return GameTypes.GoNoGoGame.name();
            default:
                return null;
        }
    }

    /**
     * Returns game type by string
     *
     * @param type
     * @return
     */
    public static GameTypes getGameType(String type) {

        if (type.equals(GameTypes.GoGame.name()))
            return GameTypes.GoGame;
        if (type.equals(GameTypes.GoNoGoGame.name()))
            return GameTypes.GoNoGoGame;

        return null;
    }

    /*
    * Returns the test type
    */
    public static TestTypes getTestType(int id) {
        switch (id) {
            case 0:
                return TestTypes.PreOperation;
            case 1:
                return TestTypes.InOperation;
            case 2:
                return TestTypes.PostOperation;
            case 3:
                return TestTypes.Trial;
            default:
                return null;
        }
    }

    /*
    * Returns the translated test type to be displayed in a view
    */
    public static String getTranslatedType(TestTypes type, Activity activity) {
        switch (type) {
            case PreOperation:
                return activity.getResources().getString(R.string.pre_operation);
            case InOperation:
                return activity.getResources().getString(R.string.in_operation);
            case PostOperation:
                return activity.getResources().getString(R.string.post_operation);
            case Trial:
                return activity.getResources().getString(R.string.trial);
            default:
                return null;
        }
    }

    /*
    * Returns the test type
    */
    public static String getTestType(TestTypes type) {
        switch (type) {
            case PreOperation:
                return TestTypes.PreOperation.name();
            case InOperation:
                return TestTypes.InOperation.name();
            case PostOperation:
                return TestTypes.PostOperation.name();
            case Trial:
                return TestTypes.Trial.name();
            default:
                return null;
        }
    }

    public static TestTypes getTestType(String type) {

        if (type.equals(TestTypes.PreOperation.name()))
            return TestTypes.PreOperation;
        if (type.equals(TestTypes.InOperation.name()))
            return TestTypes.InOperation;
        if (type.equals(TestTypes.PostOperation.name()))
            return TestTypes.PostOperation;
        if (type.equals(TestTypes.Trial.name()))
            return TestTypes.Trial;

        return null;
    }

    /*
    * Returns supported test types
    */
    public static SwipeItem[] getTestTypesList(Activity activity) {
        List<TestTypes> testTypeList = Arrays.asList(TestTypes.values());
        SwipeItem[] ret = new SwipeItem[testTypeList.size()];
        String[] descriptions = new String[4];
        descriptions[0] = activity.getResources().getString(R.string.pre_operation_description);
        descriptions[1] = activity.getResources().getString(R.string.in_operation_description);
        descriptions[2] = activity.getResources().getString(R.string.post_operation_description);
        descriptions[3] = activity.getResources().getString(R.string.trial_description);
        for (int i = 0; i < testTypeList.size(); i++) {
            ret[i] = new SwipeItem(i, Type.getTranslatedType(testTypeList.get(i), activity), descriptions[i]);
        }
        return ret;
    }

    /**
     * Get the gametypes and its descriptions
     *
     * @param activity the running activity
     * @return an array on game types supported by the app
     */
    public static SwipeItem[] getGameTypesList(Activity activity) {
        String[] descriptions = new String[1];
        descriptions[0] = activity.getResources().getString(R.string.go_game_description);
        //descriptions[1] = activity.getResources().getString(R.string.go_no_go_game_description);
        List<GameTypes> gameTypeList = Arrays.asList(GameTypes.values());
        SwipeItem[] ret = new SwipeItem[gameTypeList.size()-1];
        for (int i = 0; i < gameTypeList.size()-1; i++) {
            ret[i] = new SwipeItem(i, Type.getTranslatedGameType(gameTypeList.get(i), activity), descriptions[i]);
        }
        return ret;
    }

}
