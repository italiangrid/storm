package it.grid.storm.synchcall.data.directory;

import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.AbstractInputData;

public class AnonymousMkdirInputData extends AbstractInputData implements
	MkdirInputData {

	private final TSURL surl;

	public AnonymousMkdirInputData(TSURL surl) {

		if (surl == null) {
			throw new IllegalArgumentException(
				"Unable to create the object, invalid arguments: surl=" + surl);
		}
		this.surl = surl;
	}

	@Override
	public TSURL getSurl() {

		return surl;
	}

}
