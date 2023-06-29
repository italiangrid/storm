/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace;

import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.namespace.model.RetentionPolicy;
import it.grid.storm.namespace.model.ExpirationMode;
import it.grid.storm.namespace.model.AccessLatency;

public interface PropertyInterface {

	public TSizeInBytes getTotalOnlineSize();

	public TSizeInBytes getTotalNearlineSize();

	public RetentionPolicy getRetentionPolicy();

	public ExpirationMode getExpirationMode();

	public AccessLatency getAccessLatency();

	public boolean isOnlineSpaceLimited();

	public boolean hasLimitedSize();

}
