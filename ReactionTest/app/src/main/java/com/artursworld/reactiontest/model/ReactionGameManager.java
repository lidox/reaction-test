package com.artursworld.reactiontest.model;


import android.content.ContentValues;
import android.content.Context;

import com.artursworld.reactiontest.entity.ReactionGame;

import java.text.SimpleDateFormat;

public class ReactionGameManager extends MedicalUserDbManager {

    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ReactionGameManager(Context context) {
        super(context);
    }

    public long insert(ReactionGame reactionGame) {
        ContentValues values = new ContentValues();
        values.put(ReactionGameContract.ReactionGame.COLUMN_NAME_CREATION_DATE, dateFormat.format(reactionGame.getCreationDate()));
        values.put(ReactionGameContract.ReactionGame.COLUMN_NAME_DURATION, reactionGame.getDuration());
        values.put(ReactionGameContract.ReactionGame.COLUMN_NAME_HITS, reactionGame.getHits());
        values.put(ReactionGameContract.ReactionGame.COLUMN_NAME_MISSES, reactionGame.getMisses());
        values.put(ReactionGameContract.ReactionGame.COLUMN_NAME_REACTION_TYPE, reactionGame.getReationType());
        values.put(ReactionGameContract.ReactionGame.COLUMN_NAME_MEDICALUSER_ID, reactionGame.getMedicalUser().getMedicalId());

        return database.insert(ReactionGameContract.ReactionGame.TABLE_NAME, null, values);
    }

}
