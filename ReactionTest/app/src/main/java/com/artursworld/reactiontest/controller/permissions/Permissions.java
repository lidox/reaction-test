package com.artursworld.reactiontest.controller.permissions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class Permissions {

    public static final Integer WRITE_EXST = 0x3;
    public static final Integer READ_EXST = 0x4;

    private static String CLASS_NAME = Permissions.class.getSimpleName();

    /**
     * Asks for a permission by request code code
     *
     * @param permission  the permission to ask. E.g: Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXST
     * @param requestCode the request code to use
     */
    public static void askForPermission(String permission, Integer requestCode, Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(activity,
                        new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            }
        } else {
            Log.d(CLASS_NAME, permission + " is already granted.");
        }
    }

}
