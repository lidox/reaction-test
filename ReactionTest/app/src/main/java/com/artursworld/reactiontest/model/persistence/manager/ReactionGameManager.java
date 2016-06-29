package com.artursworld.reactiontest.model.persistence.manager;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.artursworld.reactiontest.model.persistence.EntityDbManager;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.controller.util.UtilsRG;

import java.util.ArrayList;
import java.util.List;

public class ReactionGameManager extends EntityDbManager {

    private static final String WHERE_ID_EQUALS = DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE + " =?";

    public ReactionGameManager(Context context) {
        super(context);
    }

    public long insert(ReactionGame reactionGame) {
        ContentValues values = new ContentValues();
        values.put(DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE, UtilsRG.dateFormat.format(reactionGame.getCreationDate()));
        values.put(DBContracts.ReactionGame.COLUMN_NAME_DURATION, reactionGame.getDuration());
        /*values.put(DBContracts.ReactionGame.COLUMN_NAME_HITS, reactionGame.getHits());
        values.put(DBContracts.ReactionGame.COLUMN_NAME_MISSES, reactionGame.getMisses());
        values.put(DBContracts.ReactionGame.COLUMN_NAME_REACTION_TYPE, reactionGame.getReationType());
        values.put(DBContracts.ReactionGame.COLUMN_NAME_MEDICAL_ID, reactionGame.getMedicalUser().getMedicalId());

        try {
            long ret = database.insertOrThrow(DBContracts.ReactionGame.TABLE_NAME, null, values);
            UtilsRG.log.info("Reaction-game("+reactionGame.getCreationDateFormatted()+") inserted successfully for medical user("+reactionGame.getMedicalUser().getMedicalId()+")");
            return ret;
        }
        catch (Exception e){
            UtilsRG.log.error("Could not insert reactiongame ("+reactionGame.getCreationDateFormatted()+") for medical user("+reactionGame.getMedicalUser().getMedicalId()+")"+"\n" + e.getLocalizedMessage());
            return -1L;
        }
        */return -1;
    }

    public List<ReactionGame> getReactionGamesByMedicalUser(MedicalUser medicalUser) {
        List<ReactionGame> reactionGameList = new ArrayList<ReactionGame>();
        /*try {
            Cursor cursor = database.query(DBContracts.ReactionGame.TABLE_NAME,
                    new String[] { DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE,
                            DBContracts.ReactionGame.COLUMN_NAME_DURATION,

                            DBContracts.ReactionGame.COLUMN_NAME_HITS,
                            DBContracts.ReactionGame.COLUMN_NAME_MISSES,
                            DBContracts.ReactionGame.COLUMN_NAME_REACTION_TYPE,
                            DBContracts.ReactionGame.COLUMN_NAME_MEDICAL_ID

                    },

                    DBContracts.ReactionGame.COLUMN_NAME_MEDICAL_ID + "=\"" +medicalUser.getMedicalId()+"\"",
                    null, null, null, null);

            while (cursor.moveToNext()) {
                ReactionGame reactionGame = new ReactionGame();
                try {
                    reactionGame.setCreationDate(UtilsRG.dateFormat.parse(cursor.getString(0)));
                } catch (Exception e) {
                    reactionGame.setCreationDate(null);
                }
                reactionGame.setDuration(cursor.getDouble(1));
                reactionGame.setHits(cursor.getInt(2));
                reactionGame.setMisses(cursor.getInt(3));
                reactionGame.setReationType(cursor.getString(4));
                reactionGame.setMedicalUser(medicalUser);
                reactionGameList.add(reactionGame);
            }
        }
        catch (Exception e){
            UtilsRG.log.error("Failure at method getReactionGamesByMedicalUser(meduser: " + medicalUser.getMedicalId());
            UtilsRG.log.error(e.getLocalizedMessage());
        }*/
        return reactionGameList;
    }

    public List<ReactionGame> getAllReactionGames() {
        List<ReactionGame> reactionGameList = new ArrayList<ReactionGame>();
        /*try {
            Cursor cursor = database.query(DBContracts.ReactionGame.TABLE_NAME,
                    new String[] { DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE,
                            DBContracts.ReactionGame.COLUMN_NAME_DURATION,
                            DBContracts.ReactionGame.COLUMN_NAME_HITS,
                            DBContracts.ReactionGame.COLUMN_NAME_MISSES,
                            DBContracts.ReactionGame.COLUMN_NAME_REACTION_TYPE,
                            DBContracts.ReactionGame.COLUMN_NAME_MEDICAL_ID
                    },
                    null, null, null, null, null);

            while (cursor.moveToNext()) {
                ReactionGame reactionGame = new ReactionGame();
                reactionGame.setCreationDate(UtilsRG.dateFormat.parse(cursor.getString(0)));
                reactionGame.setCreationDate(null);
                reactionGame.setDuration(cursor.getDouble(1));
                reactionGame.setHits(cursor.getInt(2));
                reactionGame.setMisses(cursor.getInt(3));
                reactionGame.setReationType(cursor.getString(4));
                //reactionGame.setMedicalUser(cursor.getString(5));
                reactionGameList.add(reactionGame);
            }
        }
        catch (Exception e){
            UtilsRG.log.error(e.getLocalizedMessage());
        }*/
        return reactionGameList;
    }

    public int delete(ReactionGame reactionGame) {
        return database.delete(DBContracts.ReactionGame.TABLE_NAME,
                WHERE_ID_EQUALS, new String[] { reactionGame.getCreationDateFormatted() });
    }


}
