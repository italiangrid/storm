/**
 * 
 */
package it.grid.storm.startup;

import it.grid.storm.logging.LoggingReloadTask;

import java.util.Timer;


/**
 * @author zappi
 *
 */
public class InitLoggers {



    public static void main(String[] args) throws Exception {
        final Timer taskTimer = new Timer(true);
        String fileSeparator = System.getProperty("file.separator");
        String logFile = System.getProperty("user.dir") + fileSeparator + "conf" + fileSeparator + "new-logging.xml";
        System.out.println("LogFile = " + logFile);
        initializeLogging(logFile, taskTimer);

    }

    /**
     * Initializes the logging system and starts the process to watch for config
     * file changes.
     * 
     * @param loggingConfigFilePath
     *            path to the logging configuration file
     * @param reloadTasks
     *            timer controlling the reloading of tasks
     */
    private static void initializeLogging(String loggingConfigFilePath, Timer reloadTasks) {
        LoggingReloadTask reloadTask = new LoggingReloadTask(loggingConfigFilePath);
        int refreshPeriod = 5 * 60 * 1000; // check/reload every 5 minutes
        reloadTask.run();
        reloadTasks.scheduleAtFixedRate(reloadTask, refreshPeriod, refreshPeriod);
    }

}
