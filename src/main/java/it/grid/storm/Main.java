package it.grid.storm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  private Main() {}

  public static void main(String[] args) {

    StoRM storm = new StoRM();

    Thread.setDefaultUncaughtExceptionHandler(new StoRMDefaultUncaughtExceptionHandler());

    Runtime.getRuntime().addShutdownHook(new ShutdownHook(storm));

    if (storm.startServices()) {

      log.info("StoRM: Backend services successfully started.");

    } else {

      log.error("StoRM: error starting storm services.");

      storm.stopServices();

      Runtime.getRuntime().exit(1);
    }
  }
}
