package it.grid.storm.synchcall.data.directory;

import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.AbstractInputData;

public class AnonymousRmdirInputData extends AbstractInputData implements
	RmdirInputData {

	private final TSURL surl;
	private final Boolean recursive;

	public AnonymousRmdirInputData(TSURL surl, Boolean recursive)
		throws IllegalArgumentException {

		if (surl == null || recursive == null) {
			throw new IllegalArgumentException(
				"Unable to create the object, invalid arguments: surl=" + surl
					+ " recursive=" + recursive);
		}
		this.surl = surl;
		this.recursive = recursive;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.synchcall.data.directory.RmdirInputData#getSurl()
	 */
	@Override
	public TSURL getSurl() {

		return surl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.synchcall.data.directory.RmdirInputData#getRecursive()
	 */
	@Override
	public Boolean getRecursive() {

		return recursive;
	}

}
