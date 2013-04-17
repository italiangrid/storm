package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.InputData;

public interface ExtendFileLifeTimeInputData extends InputData {

	/**
	 * @return the reqToken
	 */
	public TRequestToken getRequestToken();

	/**
	 * @return the arrayOfSURLs
	 */
	public ArrayOfSURLs getArrayOfSURLs();

	/**
	 * @return the newFileLifetime
	 */
	public TLifeTimeInSeconds getNewFileLifetime();

	/**
	 * @return the newPinLifetime
	 */
	public TLifeTimeInSeconds getNewPinLifetime();

}
