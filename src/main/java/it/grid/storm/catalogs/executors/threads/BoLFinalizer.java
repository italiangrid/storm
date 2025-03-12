package it.grid.storm.catalogs.executors.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.persistence.dao.BoLChunkDAO;
import it.grid.storm.persistence.impl.mysql.BoLChunkDAOMySql;

public class BoLFinalizer implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(BoLFinalizer.class);

  private final BoLChunkDAO dao;
  private final long inProgressRequestsExpirationTime;

  public BoLFinalizer(long inProgressRequestsExpirationTime) {

    dao = BoLChunkDAOMySql.getInstance();
    this.inProgressRequestsExpirationTime = inProgressRequestsExpirationTime;
  }

  @Override
  public void run() {

    log.debug("BoL finalizer started ..");
    log.debug("Search for SRM_SUCCESS bol request to be moved to SRM_RELEASED ..");
    int nReleased = 0;
    try {
      nReleased = dao.releaseExpiredAndSuccessfulRequests();
    } catch (Throwable e) {
      log.error("{}: {}", e.getClass(), e.getMessage(), e);
    } finally {
      if (nReleased > 0) {
        log.info("Released {} expired and successful BoL requests", nReleased);
      }
      log.debug("Search for SRM_SUCCESS bol request to be moved to SRM_RELEASED .. DONE");
    }
    log.debug("Search for SRM_REQUEST_INPROGRESS bol request to be moved to SRM_ABORTED ..");
    int nAborted = 0;
    try {
      nAborted = dao.abortInProgressRequestsSince(inProgressRequestsExpirationTime);
    } catch (Throwable e) {
      log.error("{}: {}", e.getClass(), e.getMessage(), e);
    } finally {
      if (nAborted > 0) {
        log.info("Aborted {} in-progress BoL requests", nReleased);
      }
      log.debug("Search for SRM_REQUEST_INPROGRESS bol request to be moved to SRM_ABORTED .. DONE");
    }
  }
}
