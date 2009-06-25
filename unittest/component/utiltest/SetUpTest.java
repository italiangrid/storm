/**
 * 
 */
package component.utiltest;


import it.grid.storm.logging.Files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

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
public class SetUpTest {

    /** Class logger. */
    private static Logger log = LoggerFactory.getLogger(SetUpTest.class);

    /** Path to the logging configuration file. */
    private String loggingConfigFilePath;

    /** The last time the logging configuration was modified. */
    private long lastModification;

    private static SetUpTest instance = null;

    private SetUpTest() {
        System.out.println("Setting up the Test framework");
    }

    public static void init(String loggerFilePath) {
        instance = new SetUpTest();
        instance.initializeLogger(loggerFilePath);
    }


    private void initializeLogger(String loggingConfigFilePath) {

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        log.debug("Logger Context : " + loggerContext);
        StatusManager statusManager = loggerContext.getStatusManager();
        log.debug("Status Manager : " + statusManager);
        log.debug("Starting time : " + new java.util.Date(System.currentTimeMillis()));
        this.loggingConfigFilePath = loggingConfigFilePath;
        File loggingConfigFile = null;
        try {
            loggingConfigFile = Files.getReadableFile(loggingConfigFilePath);
        } catch (IOException e) {
            log.error("Error loading logging configuration file: " + loggingConfigFilePath, e);
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
            log.debug("Logger Context : " + loggerContext);
            List<ch.qos.logback.classic.Logger> listLogger = loggerContext.getLoggerList();
            for (Logger logger : listLogger) {
                log.debug("-- Logger = " + logger.getName());
            }
            log.info("Loaded new logging configuration file {}", loggingConfigFile.getAbsoluteFile());
            lastModification = loggingConfigFile.lastModified();
        } catch (JoranException e) {
            statusManager.add(new ErrorStatus("Error loading logging configuration file: "
                    + loggingConfigFile.getAbsolutePath(), this, e));
        } catch (IOException e) {
            statusManager.add(new ErrorStatus("Error loading logging configuration file: "
                    + loggingConfigFile.getAbsolutePath(), this, e));
        }
        log.debug(" === Logging configuration ===");
        log.debug("  LogFileName : "+loggingConfigFile.getAbsolutePath()  );
        log.debug("  last modification : " + new java.util.Date(lastModification));
    }

}
