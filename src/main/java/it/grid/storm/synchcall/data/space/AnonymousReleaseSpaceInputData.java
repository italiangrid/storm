package it.grid.storm.synchcall.data.space;

import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.synchcall.data.AbstractInputData;

public class AnonymousReleaseSpaceInputData extends AbstractInputData implements
	ReleaseSpaceInputData {

	private final TSpaceToken spaceToken;
	private final boolean forceFileRelease;

	public AnonymousReleaseSpaceInputData(TSpaceToken spaceToken,
		Boolean forceFileRelease) throws IllegalArgumentException {

		if (spaceToken == null || forceFileRelease == null) {
			throw new IllegalArgumentException(
				"Unable to create the object, invalid arguments: spaceToken="
					+ spaceToken + " forceFileRelease=" + forceFileRelease);
		}
		this.spaceToken = spaceToken;
		this.forceFileRelease = forceFileRelease;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.synchcall.data.space.ReleaseSpaceInputData#getSpaceToken()
	 */
	@Override
	public TSpaceToken getSpaceToken() {

		return spaceToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.synchcall.data.space.ReleaseSpaceInputData#isForceFileRelease
	 * ()
	 */
	@Override
	public boolean isForceFileRelease() {

		return forceFileRelease;
	}

}
