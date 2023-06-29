/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.scheduler;

import it.grid.storm.asynch.RequestChunk;

public interface PersistentRequestChunk extends RequestChunk {

	public String getRequestToken();

	public void persistStatus();

}
