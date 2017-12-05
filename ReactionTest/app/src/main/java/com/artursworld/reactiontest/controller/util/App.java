package com.artursworld.reactiontest.controller.util;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Dependencies:
 * 1. Add manifest:
 * <application
 *    android:name="com.artursworld.playground.App"
 */
public class App extends Application {

    private static App singleton;
    private static Context ctx = null;

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this.getApplicationContext();
        singleton = this;
        Log.i("App", "running application: reaction game");
    }

    public static Context getAppContext(){
        return ctx;
    }


    public static App getInstance(){
        return singleton;
    }
}
