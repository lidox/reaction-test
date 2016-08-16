package com.artursworld.reactiontest.model.persistence.strictmode;

import android.app.Application;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;

import com.artursworld.reactiontest.BuildConfig;
import com.artursworld.reactiontest.controller.util.UtilsRG;

public class StrictModeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UtilsRG.info(StrictModeApplication.class.getSimpleName() + " onCreate");

        if (BuildConfig.DEBUG) {
            enableStrictMode();
        }
    }

    private void enableStrictMode() {
        if (Build.VERSION.SDK_INT >= 9) {
            // strict mode is available from API 9
            strictModeConfigurations();
        }

        if (Build.VERSION.SDK_INT >= 16) {
            // from API 16 on the strict mode must be enabled like this when used in Application class
            new Handler().postAtFrontOfQueue(new Runnable() {
                @Override
                public void run() {
                    strictModeConfigurations();
                }
            });
        }

    }

    private void strictModeConfigurations() {
        UtilsRG.info(StrictModeApplication.class.getSimpleName() + " strictModeConfigurations");
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        );
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDialog()
                .build()
        );
    }
}
