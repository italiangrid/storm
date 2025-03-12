/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.model;

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;

public interface ReducedChunkData {

	public TSURL fromSURL();

	public boolean isPinned();

	public long primaryKey();

	public void setPrimaryKey(long l);

	public TReturnStatus status();

}
