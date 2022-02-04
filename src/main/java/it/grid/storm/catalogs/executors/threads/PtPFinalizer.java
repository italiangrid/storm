package it.grid.storm.catalogs.executors.threads;

import static it.grid.storm.srm.types.TStatusCode.SRM_FAILURE;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.persistence.dao.PtPChunkDAO;
import it.grid.storm.persistence.impl.mysql.PtPChunkDAOMySql;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.command.datatransfer.PutDoneCommand;
import it.grid.storm.synchcall.command.datatransfer.PutDoneCommandException;


public class PtPFinalizer implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(PtPFinalizer.class);

  private static final String NAME = "Expired-PutRequests-Agent";

  private long inProgressRequestsExpirationTime;
  private final PtPChunkDAO dao;

  public PtPFinalizer(long inProgressRequestsExpirationTime) {

    this.inProgressRequestsExpirationTime = inProgressRequestsExpirationTime;
    dao = PtPChunkDAOMySql.getInstance();
    log.info("{} created.", NAME);
  }

  @Override
  public void run() {

    log.debug("{} run.", NAME);
    try {

      transitExpiredLifetimeRequests();
      transitExpiredInProgressRequests();

    } catch (Exception e) {

      log.error("{}: {}", e.getClass(), e.getMessage(), e);

    }
  }

  private void transitExpiredLifetimeRequests() {

    Map<Long, String> expiredRequests = dao.getExpiredSRM_SPACE_AVAILABLE();
    log.debug("{} lifetime-expired requests found ... ", NAME, expiredRequests.size());

    if (expiredRequests.isEmpty()) {
      return;
    }

    expiredRequests.entrySet().forEach(e -> executePutDone(e.getKey(), e.getValue()));

    int count =
        dao.transitExpiredSRM_SPACE_AVAILABLEtoSRM_FILE_LIFETIME_EXPIRED(expiredRequests.keySet());
    log.info("{} updated expired put requests - {} db rows affected", NAME, count);
  }

  private void executePutDone(Long id, String surl) {

    try {

      if (PutDoneCommand.executePutDone(TSURL.makeFromStringValidate(surl))) {
        log.info("{} successfully executed a srmPutDone on surl {}", NAME, surl);
      }

    } catch (InvalidTSURLAttributesException | PutDoneCommandException e) {

      log.error("{}. Unable to execute PutDone on request with id {} and surl {}: ", NAME, id, surl,
          e.getMessage(), e);
    }
  }

  private void transitExpiredInProgressRequests() {

    int count = dao.transitLongTimeInProgressRequestsToStatus(inProgressRequestsExpirationTime, SRM_FAILURE, "Request timeout");
    log.debug("{} moved in-progress put requests to failure - {} db rows affected", NAME, count);
  }
}
