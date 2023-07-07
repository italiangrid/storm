package it.grid.storm.catalogs.executors.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.persistence.dao.BoLChunkDAO;
import it.grid.storm.persistence.impl.mysql.BoLChunkDAOMySql;

public class BoLFinalizer implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(BoLFinalizer.class);

  private final BoLChunkDAO dao;

  public BoLFinalizer() {

    dao = BoLChunkDAOMySql.getInstance();
  }

  @Override
  public void run() {

    log.debug("BoL finalizer started ..");

    try {

      int n = dao.releaseExpiredAndSuccessfulRequests();
      if (n > 0) {
        log.info("Released {} expired and successful BoL requests", n);
      }

    } catch (Exception e) {

      log.error("{}: {}", e.getClass(), e.getMessage(), e);

    }
  }
}
