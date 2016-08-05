package com.artursworld.reactiontest.controller.helper;


import android.app.Activity;

import com.artursworld.reactiontest.R;

public class Type {
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
