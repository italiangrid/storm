package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TRequestToken;

public interface SynchMultyOperationRequestData extends RequestData {

	public TRequestToken getGeneratedRequestToken();

	public void store();
}
