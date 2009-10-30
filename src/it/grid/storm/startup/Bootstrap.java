/**
 * 
 */
package it.grid.storm.startup;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.logging.LoggingReloadTask;

import java.util.Timer;

/**
 * @author zappi
 */
public class Bootstrap {

    private static final Timer reloadTasks = new Timer(true);

    /**
     * Initializes the logging system and starts the process to watch for config file changes.
     * 
     * @param loggingConfigFilePath path to the logging configuration file
     * @param reloadTasks timer controlling the reloading of tasks
     */
    public static void initializeLogging(String loggingConfigFilePath) {
        LoggingReloadTask reloadTask = new LoggingReloadTask(loggingConfigFilePath);
        int refreshPeriod = 5 * 60 * 1000; // check/reload every 5 minutes
        reloadTask.run();
        reloadTasks.scheduleAtFixedRate(reloadTask, refreshPeriod, refreshPeriod);
    }

    public static void initializePathAuthz(String pathAuthzDBFileName) {
        AuthzDirector.initializePathAuthz(pathAuthzDBFileName);
    }

}
