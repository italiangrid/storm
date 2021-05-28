package it.grid.storm.persistence.dao;

import java.util.List;
import java.util.Map;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;

public interface SURLStatusDAO {

  boolean abortActivePtGsForSURL(GridUserInterface user, TSURL surl, String explanation);

  boolean abortActivePtPsForSURL(GridUserInterface user, TSURL surl, String explanation);

  Map<TSURL, TReturnStatus> getPinnedSURLsForUser(GridUserInterface user, List<TSURL> surls);

  Map<TSURL, TReturnStatus> getPinnedSURLsForUser(GridUserInterface user, TRequestToken token,
      List<TSURL> surls);

  Map<TSURL, TReturnStatus> getSURLStatuses(TRequestToken token);

  Map<TSURL, TReturnStatus> getSURLStatuses(TRequestToken token, List<TSURL> surls);

  int markSURLsReadyForRead(TRequestToken token, List<TSURL> surls);

  void releaseSURL(TSURL surl);

  void releaseSURLs(GridUserInterface user, List<TSURL> surls);

  void releaseSURLs(List<TSURL> surls);

  void releaseSURLs(TRequestToken token, List<TSURL> surls);

  boolean surlHasOngoingPtGs(TSURL surl);

  boolean surlHasOngoingPtPs(TSURL surl, TRequestToken token);

}
