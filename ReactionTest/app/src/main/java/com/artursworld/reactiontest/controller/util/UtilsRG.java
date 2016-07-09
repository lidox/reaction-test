package com.artursworld.reactiontest.controller.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

import ch.qos.logback.classic.android.BasicLogcatConfigurator;

public class UtilsRG {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static SimpleDateFormat dayAndhourFormat = new SimpleDateFormat("dd.MM HH:mm");

    static {
        BasicLogcatConfigurator.configureDefaultContext();
    }

    public static Logger log = LoggerFactory.getLogger(UtilsRG.class);

    public static void info(String message){
        log.info(message);
    }

    public static void error(String message){
        log.error(message);
    }

}


