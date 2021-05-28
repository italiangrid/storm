package it.grid.storm.persistence.model;

import it.grid.storm.srm.types.TRequestToken;

public interface SynchMultyOperationRequestData extends RequestData {

  public TRequestToken getGeneratedRequestToken();

  public void store();
}
