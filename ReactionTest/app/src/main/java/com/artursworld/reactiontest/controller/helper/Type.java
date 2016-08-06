package com.artursworld.reactiontest.controller.helper;


import android.app.Activity;

import com.artursworld.reactiontest.R;

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


        private final int id;

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

        private final int id;

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

}
