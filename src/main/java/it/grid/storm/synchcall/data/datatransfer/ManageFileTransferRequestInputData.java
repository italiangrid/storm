package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.InputData;

public interface ManageFileTransferRequestInputData extends InputData {

	/**
	 * @return the requestToken
	 */
	public TRequestToken getRequestToken();
}
