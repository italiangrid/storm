package it.grid.storm.synchcall.data.directory;

import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.InputData;

public interface MvInputData extends InputData {

	/**
	 * @return the toSURL
	 */
	public TSURL getToSURL();

	/**
	 * @return the fromSURL
	 */
	public TSURL getFromSURL();

}
