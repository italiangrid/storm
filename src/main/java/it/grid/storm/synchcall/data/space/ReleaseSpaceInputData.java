package it.grid.storm.synchcall.data.space;

import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.synchcall.data.InputData;

public interface ReleaseSpaceInputData extends InputData {

	/**
	 * @return the spaceToken
	 */
	public TSpaceToken getSpaceToken();

	/**
	 * @return the forceFileRelease
	 */
	public boolean isForceFileRelease();

}
