package it.grid.storm;

import static java.lang.System.exit;
import static java.lang.System.getProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.config.Configuration;
import it.grid.storm.startup.BootstrapException;

public class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  public static final String CONFIG_FILE_PROPERTY = "storm.configuration.file";
  public static final String DEFAULT_CONFIG_FILE = "/etc/storm/backend-server/storm.properties";

  private Main() {}

  public static void main(String[] args) {

    String filePath = getProperty(CONFIG_FILE_PROPERTY, DEFAULT_CONFIG_FILE);
    Configuration.init(filePath);

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
