package com.artursworld.reactiontest.view.settings;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.artursworld.reactiontest.controller.importer.ImportViaJSON;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;


public class FileChooserDialogs {

    private Activity activity = null;
    public static final int PERMISSION_REQUEST_CODE = 78;

    public FileChooserDialogs(Activity activity){
        this.activity = activity;
    }

    /**
     * Opens file chooser on device to select a file
     */
    public void importFile() {
        UtilsRG.info("Open filechooser to read a file");

        String readExternalStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if(hasPermission(PERMISSION_REQUEST_CODE, readExternalStoragePermission)){
            new MaterialFilePicker()
                    .withActivity(activity)
                    .withRequestCode(PERMISSION_REQUEST_CODE)
                    .withFilter(Pattern.compile(".*\\.json$"))
                    .withFilterDirectories(true)
                    .withHiddenFiles(true) // Show hidden files and folders
                    .start();
        }
    }

    /**
     * Check if user has given permission
     * @param requestCode
     * @param permissionToCheck the permission to check
     * @return true if user has permission. Otherwise false
     */
    private boolean hasPermission(int requestCode, String permissionToCheck) {
        boolean hasPermission = false;
        int permissionCheck = ContextCompat.checkSelfPermission(activity, permissionToCheck);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permissionToCheck}, requestCode);
        } else {
            hasPermission = true;
        }
        return hasPermission;
    }

    /**
     * This will be executed if a file has been selected by file chooser. This method must be
     * implemented in the Parent-Activity in the method: onActivityResult
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the intent containing the path of selected data
     */
    public void onFileSelectedByFileChooser(int requestCode, int resultCode, Intent data) {
        if (requestCode == FileChooserDialogs.PERMISSION_REQUEST_CODE && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            UtilsRG.info("Selected file by file chooser. filepath = "+filePath);
            TastyToast.makeText(activity, "filepath="+filePath, TastyToast.LENGTH_LONG, TastyToast.INFO);

            //TODO: go on
            //JSONObject json = getJSONbyFilePath(filePath);
            //new ImportViaJSON().importDataToDBbyJSON(json, activity);
        }
    }

    private JSONObject getJSONbyFilePath(String filePath) {
        File dir = null;
        try {

            dir = Environment.getExternalStorageDirectory();
            File yourFile = new File(dir, filePath);
            FileInputStream stream = new FileInputStream(yourFile);
            String jString = null;
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                jString = Charset.defaultCharset().decode(bb).toString();
            }
            finally {
                stream.close();
            }
            UtilsRG.info("JSON file has been read successfully. Content: " +jString);
            return new JSONObject(jString);

        } catch (Exception e) {
            UtilsRG.error("Could not read JSON file at dir = " + dir + " and filepath =" +filePath);
            UtilsRG.error(e.getLocalizedMessage());
            return null;
        }
    }
}
