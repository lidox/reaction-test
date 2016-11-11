package com.artursworld.reactiontest.controller.importer;


import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;


import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.artursworld.reactiontest.model.persistence.manager.TrialManager;

import org.json.JSONArray;
import org.junit.Test;

import java.util.List;

public class ImportViaJSONTest extends InstrumentationTestCase {

    private RenamingDelegatingContext context;
    private DBContracts.DatabaseHelper db;
    private ImportViaJSON importer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
        importer = new ImportViaJSON();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testImportDataToDBbyJSON() throws Exception{
        String jsonString = "[{\"name\":\"Arturo Vidal\",\"age\":24,\"gender\":\"Male\",\"games\":[{\"datetime\":\"2016-10-30 19:52:43.194\",\"type\":\"Trial\",\"times\":[649,464,524,402,390]},{\"datetime\":\"2016-10-30 19:53:21.012\",\"type\":\"PreOperation\",\"times\":[701,412,419,404,369]},{\"datetime\":\"2016-11-02 15:59:15.201\",\"type\":\"Trial\",\"times\":[444,756,449,420,474]}]},{\"name\":\"Thomas Müssler\",\"age\":0,\"gender\":\"Male\",\"games\":[{\"datetime\":\"2016-10-30 19:09:01.982\",\"type\":\"Trial\",\"times\":[539,578,388,334,344]},{\"datetime\":\"2016-10-30 19:09:33.404\",\"type\":\"PreOperation\",\"times\":[366,467,471,420,513]},{\"datetime\":\"2016-10-31 10:52:56.851\",\"type\":\"InOperation\",\"times\":[392,656,637,439,683]},{\"datetime\":\"2016-10-31 11:00:27.929\",\"type\":\"InOperation\",\"times\":[361,669,419,-303,806]},{\"datetime\":\"2016-10-31 11:09:01.305\",\"type\":\"InOperation\",\"times\":[896,967,590,846,526]},{\"datetime\":\"2016-10-31 11:14:34.542\",\"type\":\"InOperation\",\"times\":[543,544,420,419,415]},{\"datetime\":\"2016-10-31 11:22:02.896\",\"type\":\"InOperation\",\"times\":[763,492,563,489,434]},{\"datetime\":\"2016-10-31 11:32:42.431\",\"type\":\"InOperation\",\"times\":[527,566,424,483,435]},{\"datetime\":\"2016-10-31 11:35:57.345\",\"type\":\"InOperation\",\"times\":[503,508,369,471,397]},{\"datetime\":\"2016-10-31 11:41:25.581\",\"type\":\"InOperation\",\"times\":[496,397,734,1005,472]}]},{\"name\":\"Hans dieter\",\"age\":22,\"gender\":\"Male\",\"games\":[{\"datetime\":\"2016-10-27 17:50:33.796\",\"type\":\"Trial\",\"times\":[435,363,344,345,338]},{\"datetime\":\"2016-10-27 17:51:23.334\",\"type\":\"PreOperation\",\"times\":[370,368,292,331,339]},{\"datetime\":\"2016-10-28 09:47:45.872\",\"type\":\"PreOperation\",\"times\":[771,791,1023,365,677]},{\"datetime\":\"2016-10-28 09:54:21.644\",\"type\":\"PreOperation\",\"times\":[429,698,553,546,662]},{\"datetime\":\"2016-10-28 10:16:24.185\",\"type\":\"PreOperation\",\"times\":[2091,793,1772,382,472]},{\"datetime\":\"2016-10-28 10:48:39.609\",\"type\":\"PreOperation\",\"times\":[443,499,514,453,564]},{\"datetime\":\"2016-10-28 10:49:11.696\",\"type\":\"InOperation\",\"times\":[459,448,575,412,458]},{\"datetime\":\"2016-10-28 11:04:03.968\",\"type\":\"InOperation\",\"times\":[463,443,481,540,513]},{\"datetime\":\"2016-10-30 19:18:02.158\",\"type\":\"PostOperation\",\"times\":[370,435,360,453,352]}]}]";
        JSONArray object = new JSONArray(jsonString);
        List<JsonUser> list = importer.getUserIdList(object);
        assertEquals("Checking list size", 3, list.size());

        // now insert into database
        importer.insertUsersToDB(context, list);
        String medicoId = "Arturo Vidal";
        MedicalUser user = new MedicalUserManager(context).getUserByMedicoId(medicoId);
        List<OperationIssue> opList = new OperationIssueManager(context).getAllOperationIssuesByMedicoId(medicoId);
        assertEquals("Checking oplist size", 1, opList.size());

        List<ReactionGame> gameList = new ReactionGameManager(context).getAllReactionGameList(opList.get(0).getDisplayName().trim(), "DESC");
        assertEquals("Checking oplist size", 3, gameList.size());

        List<Integer> trailList = new TrialManager(context).getAllReactionTimesList(gameList.get(0).getCreationDateFormatted());
        assertEquals(5,trailList.size());
    }

    @Test
    public void testgetUserIdListTest1() throws Exception{
        String jsonString = "[{\"name\":\"Arturo Vidal\",\"age\":24,\"gender\":\"Male\",\"games\":[{\"datetime\":\"2016-10-30 19:52:43.194\",\"type\":\"Trial\",\"times\":[649,464,524,402,390]},{\"datetime\":\"2016-10-30 19:53:21.012\",\"type\":\"PreOperation\",\"times\":[701,412,419,404,369]},{\"datetime\":\"2016-11-02 15:59:15.201\",\"type\":\"Trial\",\"times\":[444,756,449,420,474]}]},{\"name\":\"Thomas Müssler\",\"age\":0,\"gender\":\"Male\",\"games\":[{\"datetime\":\"2016-10-30 19:09:01.982\",\"type\":\"Trial\",\"times\":[539,578,388,334,344]},{\"datetime\":\"2016-10-30 19:09:33.404\",\"type\":\"PreOperation\",\"times\":[366,467,471,420,513]},{\"datetime\":\"2016-10-31 10:52:56.851\",\"type\":\"InOperation\",\"times\":[392,656,637,439,683]},{\"datetime\":\"2016-10-31 11:00:27.929\",\"type\":\"InOperation\",\"times\":[361,669,419,-303,806]},{\"datetime\":\"2016-10-31 11:09:01.305\",\"type\":\"InOperation\",\"times\":[896,967,590,846,526]},{\"datetime\":\"2016-10-31 11:14:34.542\",\"type\":\"InOperation\",\"times\":[543,544,420,419,415]},{\"datetime\":\"2016-10-31 11:22:02.896\",\"type\":\"InOperation\",\"times\":[763,492,563,489,434]},{\"datetime\":\"2016-10-31 11:32:42.431\",\"type\":\"InOperation\",\"times\":[527,566,424,483,435]},{\"datetime\":\"2016-10-31 11:35:57.345\",\"type\":\"InOperation\",\"times\":[503,508,369,471,397]},{\"datetime\":\"2016-10-31 11:41:25.581\",\"type\":\"InOperation\",\"times\":[496,397,734,1005,472]}]},{\"name\":\"Hans dieter\",\"age\":22,\"gender\":\"Male\",\"games\":[{\"datetime\":\"2016-10-27 17:50:33.796\",\"type\":\"Trial\",\"times\":[435,363,344,345,338]},{\"datetime\":\"2016-10-27 17:51:23.334\",\"type\":\"PreOperation\",\"times\":[370,368,292,331,339]},{\"datetime\":\"2016-10-28 09:47:45.872\",\"type\":\"PreOperation\",\"times\":[771,791,1023,365,677]},{\"datetime\":\"2016-10-28 09:54:21.644\",\"type\":\"PreOperation\",\"times\":[429,698,553,546,662]},{\"datetime\":\"2016-10-28 10:16:24.185\",\"type\":\"PreOperation\",\"times\":[2091,793,1772,382,472]},{\"datetime\":\"2016-10-28 10:48:39.609\",\"type\":\"PreOperation\",\"times\":[443,499,514,453,564]},{\"datetime\":\"2016-10-28 10:49:11.696\",\"type\":\"InOperation\",\"times\":[459,448,575,412,458]},{\"datetime\":\"2016-10-28 11:04:03.968\",\"type\":\"InOperation\",\"times\":[463,443,481,540,513]},{\"datetime\":\"2016-10-30 19:18:02.158\",\"type\":\"PostOperation\",\"times\":[370,435,360,453,352]}]}]";
        JSONArray object = new JSONArray(jsonString);
        List<JsonUser> list = importer.getUserIdList(object);
        assertEquals("Checking list size", 3, list.size());
    }

    @Test
    public void testgetUserIdListTest2() throws Exception{
        String jsonString = "[{\"name\":\"Arturo Vidal\",\"age\":24,\"gender\":\"Male\",\"games\":[{\"datetime\":\"2016-10-30 19:52:43.194\",\"type\":\"Trial\",\"times\":['afsf',649,464,524,402,390]},{\"datetime\":\"2016-10-30 19:53:21.012\",\"type\":\"PreOperation\",\"times\":[701,412,419,404,369]},{\"datetime\":\"2016-11-02 15:59:15.201\",\"type\":\"Trial\",\"times\":[444,756,449,420,474]}]},{\"name\":\"Thomas Müssler\",\"age\":0,\"gender\":\"Male\",\"games\":[{\"datetime\":\"2016-10-30 19:09:01.982\",\"type\":\"Trial\",\"times\":[539,578,388,334,344]},{\"datetime\":\"2016-10-30 19:09:33.404\",\"type\":\"PreOperation\",\"times\":[366,467,471,420,513]},{\"datetime\":\"2016-10-31 10:52:56.851\",\"type\":\"InOperation\",\"times\":[392,656,637,439,683]},{\"datetime\":\"2016-10-31 11:00:27.929\",\"type\":\"InOperation\",\"times\":[361,669,419,-303,806]},{\"datetime\":\"2016-10-31 11:09:01.305\",\"type\":\"InOperation\",\"times\":[896,967,590,846,526]},{\"datetime\":\"2016-10-31 11:14:34.542\",\"type\":\"InOperation\",\"times\":[543,544,420,419,415]},{\"datetime\":\"2016-10-31 11:22:02.896\",\"type\":\"InOperation\",\"times\":[763,492,563,489,434]},{\"datetime\":\"2016-10-31 11:32:42.431\",\"type\":\"InOperation\",\"times\":[527,566,424,483,435]},{\"datetime\":\"2016-10-31 11:35:57.345\",\"type\":\"InOperation\",\"times\":[503,508,369,471,397]},{\"datetime\":\"2016-10-31 11:41:25.581\",\"type\":\"InOperation\",\"times\":[496,397,734,1005,472]}]},{\"name\":\"Hans dieter\",\"age\":22,\"gender\":\"Male\",\"games\":[{\"datetime\":\"2016-10-27 17:50:33.796\",\"type\":\"Trial\",\"times\":[435,363,344,345,338]},{\"datetime\":\"2016-10-27 17:51:23.334\",\"type\":\"PreOperation\",\"times\":[370,368,292,331,339]},{\"datetime\":\"2016-10-28 09:47:45.872\",\"type\":\"PreOperation\",\"times\":[771,791,1023,365,677]},{\"datetime\":\"2016-10-28 09:54:21.644\",\"type\":\"PreOperation\",\"times\":[429,698,553,546,662]},{\"datetime\":\"2016-10-28 10:16:24.185\",\"type\":\"PreOperation\",\"times\":[2091,793,1772,382,472]},{\"datetime\":\"2016-10-28 10:48:39.609\",\"type\":\"PreOperation\",\"times\":[443,499,514,453,564]},{\"datetime\":\"2016-10-28 10:49:11.696\",\"type\":\"InOperation\",\"times\":[459,448,575,412,458]},{\"datetime\":\"2016-10-28 11:04:03.968\",\"type\":\"InOperation\",\"times\":[463,443,481,540,513]},{\"datetime\":\"2016-10-30 19:18:02.158\",\"type\":\"PostOperation\",\"times\":[370,435,360,453,352]}]}]";
        JSONArray object = new JSONArray(jsonString);
        List<JsonUser> list = importer.getUserIdList(object);
        assertEquals("Checking list size. The json contains wrong values", 0, list.size());
    }

    @Test
    public void testgetUserIdListTest3() throws Exception{
        JSONArray object = null;
        List<JsonUser> list = importer.getUserIdList(object);
        assertEquals("Checking for null value", 0, list.size());
    }
}
