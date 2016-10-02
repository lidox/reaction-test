package com.artursworld.reactiontest.view.statistics;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.export.ExportViaJSON;
import com.artursworld.reactiontest.controller.util.UtilsRG;

public class StatisticsView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_view);
    }


    public void onExportUserViaJSON(View view){
        UtilsRG.info("export selected user via JSON");
        new ExportViaJSON(this).export();
    }
}
