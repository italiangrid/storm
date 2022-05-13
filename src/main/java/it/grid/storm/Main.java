package it.grid.storm;

import static java.lang.System.exit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.startup.BootstrapException;

public class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  private Main() {}

  public static void main(String[] args) {

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

}
