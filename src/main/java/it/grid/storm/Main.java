package it.grid.storm;

import static java.lang.System.exit;
import static java.lang.System.getProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.config.Configuration;
import it.grid.storm.startup.BootstrapException;

public class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  /* System properties */
  private static final String CONFIG_FILE_PATH = "storm.configuration.file";
  private static final String REFRESH_RATE = "storm.configuration.refresh";

  public static final String DEFAULT_CONFIG_FILE = "/etc/storm/backend-server/storm.properties";
  public static final int DEFAULT_REFRESH_RATE = 5000;

  private Main() {}

  public static void main(String[] args) {

    initConfiguration();

    StoRM storm = new StoRM();

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

  private static void initConfiguration() {

    String filePath = getProperty(CONFIG_FILE_PATH, DEFAULT_CONFIG_FILE);
    int refreshRate;
    try {
      refreshRate = Integer.valueOf(getProperty(REFRESH_RATE));
    } catch (NumberFormatException e) {
      refreshRate = DEFAULT_REFRESH_RATE;
    }
    Configuration.init(filePath, refreshRate);
  }
}
