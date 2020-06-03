package it.grid.storm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.space.gpfsquota.GPFSQuotaManager;

public class ShutdownHook extends Thread {

  private static final Logger log = LoggerFactory.getLogger(ShutdownHook.class);

  private StoRM storm;

  public ShutdownHook(StoRM storm) {
    this.storm = storm;
  }

  @Override
  public void run() {

    log.info("StoRM: Backend shutdown...");
    log.info("StoRM: stopping Backend services...");

    try {
      storm.stopPicker();
      storm.stopXmlRpcServer();
      storm.stopRestServer();
      storm.stopSpaceGC();
      storm.stopExpiredAgent();
      GPFSQuotaManager.INSTANCE.shutdown();

      log.info("StoRM: Backend successfully stopped.");

    } catch (Throwable e) {

      log.error(e.getMessage(), e);
      log.error("StoRM: error stopping storm services.");
    }

    log.info("StoRM: Backend shutdown complete.");
  }
}
