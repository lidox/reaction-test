package com.artursworld.reactiontest.view;

import android.app.Application;

public class MyApplication extends Application {

    private static MyApplication singleton;

    public static MyApplication getInstance(){
        return singleton;
    }
    @Override
    public void onCreate() {

        super.onCreate();
        singleton = this;
    }
}
