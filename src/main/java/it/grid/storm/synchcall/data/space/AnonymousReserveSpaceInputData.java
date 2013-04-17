package it.grid.storm.synchcall.data.space;

import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRetentionPolicyInfo;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.synchcall.data.AbstractInputData;

public class AnonymousReserveSpaceInputData extends AbstractInputData implements
	ReserveSpaceInputData {

	private final String spaceTokenAlias;
	private final TRetentionPolicyInfo retentionPolicyInfo;
	private final TSizeInBytes desiredSize;
	private final TSizeInBytes guaranteedSize;
	private TLifeTimeInSeconds spaceLifetime;
	private final ArrayOfTExtraInfo storageSystemInfo;

	public AnonymousReserveSpaceInputData(String spaceTokenAlias,
		TRetentionPolicyInfo retentionPolicyInfo, TSizeInBytes spaceDesired,
		TSizeInBytes spaceGuaranteed, ArrayOfTExtraInfo storageSystemInfo)
		throws IllegalArgumentException {

		if (spaceTokenAlias == null) {
			throw new IllegalArgumentException(
				"Unable to create the object, invalid arguments: spaceTokenAlias="
					+ spaceTokenAlias);
		}
		this.spaceTokenAlias = spaceTokenAlias;
		if (spaceDesired == null) {
			this.desiredSize = TSizeInBytes.makeEmpty();
		} else {
			this.desiredSize = spaceDesired;
		}
		if (spaceGuaranteed == null) {
			this.guaranteedSize = TSizeInBytes.makeEmpty();
		} else {
			this.guaranteedSize = spaceGuaranteed;
		}
		this.spaceLifetime = TLifeTimeInSeconds.makeEmpty();
		this.storageSystemInfo = storageSystemInfo;
		this.retentionPolicyInfo = retentionPolicyInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.synchcall.data.space.ReserveSpaceInputData#getSpaceTokenAlias
	 * ()
	 */
	@Override
	public String getSpaceTokenAlias() {

		return spaceTokenAlias;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.synchcall.data.space.ReserveSpaceInputData#getRetentionPolicyInfo
	 * ()
	 */
	@Override
	public TRetentionPolicyInfo getRetentionPolicyInfo() {

		return retentionPolicyInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.synchcall.data.space.ReserveSpaceInputData#getSpaceDesired()
	 */
	@Override
	public TSizeInBytes getDesiredSize() {

		return desiredSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.synchcall.data.space.ReserveSpaceInputData#getSpaceGuaranteed
	 * ()
	 */
	@Override
	public TSizeInBytes getGuaranteedSize() {

		return guaranteedSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.synchcall.data.space.ReserveSpaceInputData#getSpaceLifetime()
	 */
	@Override
	public TLifeTimeInSeconds getSpaceLifetime() {

		return spaceLifetime;
	}

	@Override
	public void setSpaceLifetime(TLifeTimeInSeconds spaceLifetime) {

		this.spaceLifetime = spaceLifetime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.synchcall.data.space.ReserveSpaceInputData#getStorageSystemInfo
	 * ()
	 */
	@Override
	public ArrayOfTExtraInfo getStorageSystemInfo() {

		return storageSystemInfo;
	}

}
