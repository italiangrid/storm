package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.InputData;

public interface AbortInputData extends InputData {

	public static enum AbortType {
		ABORT_REQUEST, ABORT_FILES;
	}

	public TRequestToken getRequestToken();

	public AbortType getType();

}
