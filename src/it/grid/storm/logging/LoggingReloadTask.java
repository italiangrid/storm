/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * 
 */
package it.grid.storm.logging;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.StatusManager;


/**
 * @author zappi
 *
 */
public class LoggingReloadTask extends TimerTask {

    /** Class logger. */
    private Logger log = LoggerFactory.getLogger(LoggingReloadTask.class);

    /** Path to the logging configuration file. */
    private String loggingConfigFilePath;

    /** The last time the logging configuration was modified. */
    private long lastModification;

    /**
     * Constructor.
     * 
     * @param configFilePath path to the logging configuration file to watch for changes and reload.
     */
    public LoggingReloadTask(String configFilePath) {
        loggingConfigFilePath = Strings.safeTrimOrNullString(configFilePath);
        lastModification = -1;
    }

    @Override
    public void run() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        log.debug("Logger Context : " + loggerContext);
        StatusManager statusManager = loggerContext.getStatusManager();
        log.debug("Status Manager : " + statusManager);
        log.debug(new java.util.Date(System.currentTimeMillis()) + " - Run log config loader Task");
        File loggingConfigFile = null;
        try {
            loggingConfigFile = Files.getReadableFile(loggingConfigFilePath);
        } catch (IOException e) {
            log.error("Error loading logging configuration file: " + loggingConfigFilePath, e);
            return;
        }

        if (lastModification >= loggingConfigFile.lastModified()) {
            // file hasn't changed since the last time we looked
            log.trace("Logging configuration has not changed, skipping reload");
            return;
        }

        try {
            loggerContext.reset();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            FileInputStream fin = new FileInputStream(loggingConfigFile);
            configurator.doConfigure(fin);
            fin.close();
            loggerContext.start();
            System.out.println("Logger Context : " + loggerContext);
            List<ch.qos.logback.classic.Logger> listLogger = loggerContext.getLoggerList();
            for (Logger logger : listLogger) {
                System.out.println("-- Logger = " + logger.getName());
            }
            Logger logNew = LoggerFactory.getLogger("health");
            System.out.println("-- Logger = " + logNew.getName());
            log.info("Loaded new logging configuration file {}", loggingConfigFile.getAbsoluteFile());
            lastModification = loggingConfigFile.lastModified();
        } catch (JoranException e) {
            statusManager.add(new ErrorStatus("Error loading logging configuration file: "
                    + loggingConfigFile.getAbsolutePath(), this, e));
        } catch (IOException e) {
            statusManager.add(new ErrorStatus("Error loading logging configuration file: "
                    + loggingConfigFile.getAbsolutePath(), this, e));
        }
    }

}
