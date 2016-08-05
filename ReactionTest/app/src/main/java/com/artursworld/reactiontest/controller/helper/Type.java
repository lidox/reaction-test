package com.artursworld.reactiontest.controller.helper;


import android.app.Activity;

import com.artursworld.reactiontest.R;

public class Type {
    public enum TestTypes {
        GoGame(0),
        GoNoGoGame(1);


        private final int id;

        TestTypes(int value) {
            id = value;
        }

        public int getId() {
            return id;
        }
    }

    public enum GameTypes {
        PreOperation(0),
        InOperation(1),
        PostOperation(2),
        Trial(3);

        private final int id;

        GameTypes(int value) {
            id = value;
        }

        public int getId() {
            return id;
        }
    }

    public static TestTypes getType(int i) {
        switch (i) {
            case 0:
                return TestTypes.GoGame;
            case 1:
                return TestTypes.GoNoGoGame;
            default:
                return null;
        }
    }

    public static String getTranslatedType(TestTypes type, Activity activity) {
        switch (type) {
            case GoGame:
                return activity.getResources().getString(R.string.go_game);
            case GoNoGoGame:
                return activity.getResources().getString(R.string.go_no_go_game);
            default:
                return null;
        }
    }

    public static String getType(TestTypes type) {
        switch (type) {
            case GoGame:
                return TestTypes.GoGame.name();
            case GoNoGoGame:
                return TestTypes.GoNoGoGame.name();
            default:
                return null;
        }
    }

    public static GameTypes getGameType(int id) {
        switch (id) {
            case 0:
                return GameTypes.PreOperation;
            case 1:
                return GameTypes.InOperation;
            case 2:
                return GameTypes.PostOperation;
            case 3:
                return GameTypes.Trial;
            default:
                return null;
        }
    }

    public static String getTranslatedType(GameTypes type, Activity activity) {
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

    public static String getGameType(GameTypes type) {
        switch (type) {
            case PreOperation:
                return GameTypes.PreOperation.name();
            case InOperation:
                return GameTypes.InOperation.name();
            case PostOperation:
                return GameTypes.PostOperation.name();
            case Trial:
                return GameTypes.Trial.name();
            default:
                return null;
        }
    }

}
