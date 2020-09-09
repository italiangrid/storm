package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.synchcall.data.InputData;

public interface ManageFileTransferFilesInputData extends InputData {

	/**
	 * @return the arrayOfSURLs
	 */
	public ArrayOfSURLs getArrayOfSURLs();

}
