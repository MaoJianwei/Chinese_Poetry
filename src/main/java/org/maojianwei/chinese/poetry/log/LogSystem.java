package org.maojianwei.chinese.poetry.log;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mao on 4/10/16.
 */
public class LogSystem {

    public static void initAppLogSystem(){
        LogManager.resetConfiguration();//avoid config override by other jar
        PropertyConfigurator.configure(LogSystem.class.getResource("/log4j.properties"));
        // TODO - Attention!!!  The method of Reference to resource file included by Maven is below!
        // LogSystem.class.getResource("/log4j.properties")
    }

    public static void main(String args[]){

        LogSystem logSystem = new LogSystem();
        logSystem.testLog();
    }

    public void  testLog() {

        Logger logger = LoggerFactory.getLogger(getClass());
        logger.info("radar");
        int a = 118;
        logger.warn("contect tower on {}", a);
        logger.error("contect tower on {}", a);
    }
}
