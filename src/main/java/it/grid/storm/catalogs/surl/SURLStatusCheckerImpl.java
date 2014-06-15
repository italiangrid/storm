package it.grid.storm.catalogs.surl;

import it.grid.storm.catalogs.PtPChunkDAO;
import it.grid.storm.catalogs.PtPChunkDataTO;

import java.util.List;

public class SURLStatusCheckerImpl implements SURLStatusChecker {

  @Override
  public boolean isSURLBusy(String surl) {

    final PtPChunkDAO dao = PtPChunkDAO.getInstance();

    final List<PtPChunkDataTO> results = dao.findActivePtPsOnSURL(surl);

    return (results.size() > 0);
  }


  @Override
  public boolean isSURLBusy(String surl, String requestTokenToExclude) {

    final PtPChunkDAO dao = PtPChunkDAO.getInstance();

    final List<PtPChunkDataTO> results = dao.findActivePtPsOnSURL(surl,
      requestTokenToExclude);

    return (results.size() > 0);
  }


  @Override
  public boolean isSURLPinned(String surl) {
    return false;
  }

}
