/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.space;

import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRetentionPolicyInfo;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.synchcall.data.InputData;

public interface ReserveSpaceInputData extends InputData {

	/**
	 * @return the spaceTokenAlias
	 */
	public String getSpaceTokenAlias();

	/**
	 * @return the retentionPolicyInfo
	 */
	public TRetentionPolicyInfo getRetentionPolicyInfo();

	/**
	 * @return the spaceDesired
	 */
	public TSizeInBytes getDesiredSize();

	/**
	 * @return the spaceGuaranteed
	 */
	public TSizeInBytes getGuaranteedSize();

	/**
	 * @return the spaceLifetime
	 */
	public TLifeTimeInSeconds getSpaceLifetime();

	/**
	 * @return the storageSystemInfo
	 */
	public ArrayOfTExtraInfo getStorageSystemInfo();

	/**
	 * @param spaceLifetime
	 */
	void setSpaceLifetime(TLifeTimeInSeconds spaceLifetime);

}
