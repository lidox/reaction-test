package com.artursworld.reactiontest.model;

import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import com.artursworld.reactiontest.entity.MedicalUser;
import com.artursworld.reactiontest.entity.ReactionGame;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import ch.qos.logback.classic.android.BasicLogcatConfigurator;

public class ReactionGameManagerTest extends InstrumentationTestCase {
    // Logging
    static {
        BasicLogcatConfigurator.configureDefaultContext();
    }
    private Logger log = LoggerFactory.getLogger(MedicalUserManager.class);

    private DBContracts.DatabaseHelper db;
    ReactionGameManager reactionGameManager;
    private RenamingDelegatingContext context;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
        db = new DBContracts.DatabaseHelper(context);
    }

    @Override
    public void tearDown() throws Exception {
        db.close();
        super.tearDown();
    }

}
