package com.artursworld.reactiontest.view.settings;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.nbsp.materialfilepicker.MaterialFilePicker;

import java.util.regex.Pattern;


public class FileChooserDialogs {

    private Activity activity = null;
    public static final int PERMISSION_REQUEST_CODE = 78;

    public FileChooserDialogs(Activity activity){
        this.activity = activity;
    }


    public void importFile() {
        UtilsRG.info("importing json file...");

        String readExternalStorage = Manifest.permission.READ_EXTERNAL_STORAGE;
        if(hasPermission(PERMISSION_REQUEST_CODE, readExternalStorage)){
            new MaterialFilePicker()
                    .withActivity(activity)
                    .withRequestCode(PERMISSION_REQUEST_CODE)
                    .withFilter(Pattern.compile(".*\\.json$")) // Filtering files and directories by file name using regexp
                    .withFilterDirectories(true) // Set directories filterable (false by default)
                    .withHiddenFiles(true) // Show hidden files and folders
                    .start();

        }


    }

    private boolean hasPermission(int requestCode, String readExternalStorage) {
        boolean hasPermission = false;
        int permissionCheck = ContextCompat.checkSelfPermission(activity, readExternalStorage);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{readExternalStorage}, requestCode);
        } else {
            hasPermission = true;
        }
        return hasPermission;
    }
}
