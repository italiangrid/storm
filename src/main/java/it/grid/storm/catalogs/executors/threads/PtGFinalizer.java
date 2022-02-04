package it.grid.storm.catalogs.executors.threads;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.persistence.dao.PtGChunkDAO;
import it.grid.storm.persistence.impl.mysql.PtGChunkDAOMySql;
import it.grid.storm.srm.types.TSURL;


public class PtGFinalizer implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(PtGFinalizer.class);

  private final PtGChunkDAO dao;

  public PtGFinalizer() {

    dao = PtGChunkDAOMySql.getInstance();
  }

  @Override
  public void run() {

    log.debug("PtG finalizer started ..");

    try {

      Collection<TSURL> surls = dao.transitExpiredSRM_FILE_PINNED();

      if (surls.size() > 0) {
        log.info("Moved {} expired and successful PtG requests to SRM_FILE_PINNED", surls.size());
        log.debug("Released surls:");
        surls.forEach(surl -> {
          log.debug("{}", surl);
        });
      }

    } catch (Exception e) {

      log.error("{}: {}", e.getClass(), e.getMessage(), e);
    }
  }
}
