package it.grid.storm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.space.gpfsquota.GPFSQuotaManager;

public class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  private Main() {}

  public static void main(String[] args) {

    StoRM storm = init(args);

    Runtime.getRuntime().addShutdownHook(new ShutdownHook(storm));
    Thread.setDefaultUncaughtExceptionHandler(new StoRMDefaultUncaughtExceptionHandler());

    if (startServices(storm)) {

      log.info("StoRM: Backend services successfully started.");

    } else {

      log.error("StoRM: error starting storm services.");
    }
  }

  private static StoRM init(String[] args) {

    if (args.length != 2) {
      log.error("Please specify properties file path and refresh rate.");
      System.exit(1);
    }
    String configFilePath = args[0];
    int refresh = 0;
    try {
      refresh = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      log.error("Invalid refresh rate");
      System.exit(1);
    }

    return new StoRM(configFilePath, refresh);
  }

  private static boolean startServices(StoRM storm) {

    boolean response = true;
    if (!storm.pickerIsRunning()) {
      storm.startPicker();
    }
    try {
      if (!storm.xmlRpcServerIsRunning()) {
        storm.startXmlRpcServer();
      }
    } catch (Exception e) {

      log.error("Unable to start the xmlrpc server. Exception: {}", e.getMessage(), e);

      stopServices(storm);
      return false;
    }
    try {
      if (!storm.restServerIsRunning()) {
        storm.startRestServer();
      }
    } catch (Exception e) {

      log.error("Unable to start the Rest server. Exception: {}", e.getMessage(), e);

      stopServices(storm);
      return false;
    }
    if (!storm.spaceGCIsRunning()) {
      storm.startSpaceGC();
    }
    if (!storm.isExpiredAgentRunning()) {
      storm.startExpiredAgent();
    }
    return response;
  }

  private static boolean stopServices(StoRM storm) {

    storm.stopPicker();
    storm.stopXmlRpcServer();
    storm.stopRestServer();
    storm.stopSpaceGC();
    storm.stopExpiredAgent();

    GPFSQuotaManager.INSTANCE.shutdown();

    return true;
  }
}
