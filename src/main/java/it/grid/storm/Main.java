package it.grid.storm;

import static java.lang.System.exit;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.config.Configuration;
import it.grid.storm.startup.Bootstrap;
import it.grid.storm.startup.BootstrapException;

public class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  public static final String DEFAULT_CONFIG_DIR = "/etc/storm/backend-server";
  public static final String DEFAULT_CONFIG_FILE = DEFAULT_CONFIG_DIR + "/storm.properties";
  public static final String DEFAULT_LOGGING_FILE = DEFAULT_CONFIG_DIR + "/logging.xml";

  private Main() {}

  public static void main(String[] args) {

    log.debug("Configure logging from %s ...", DEFAULT_LOGGING_FILE);
    Bootstrap.configureLogging(DEFAULT_LOGGING_FILE);
    log.debug("Load configuration from %s ...", DEFAULT_CONFIG_FILE);
    try {
      Configuration.init(DEFAULT_CONFIG_FILE);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      exit(1);
    }

    StoRM storm = new StoRM(Configuration.getInstance());

    try {
      storm.init();
    } catch (BootstrapException e) {
      log.error(e.getMessage(), e);
      exit(1);
    }

    Thread.setDefaultUncaughtExceptionHandler(new StoRMDefaultUncaughtExceptionHandler());

    Runtime.getRuntime().addShutdownHook(new ShutdownHook(storm));

    try {

      storm.startServices();
      log.info("StoRM: Backend services successfully started.");

    } catch (Exception e) {

      log.error("StoRM: error starting storm services: {}", e.getMessage());
      storm.stopServices();
      exit(1);
    }
  }
}
