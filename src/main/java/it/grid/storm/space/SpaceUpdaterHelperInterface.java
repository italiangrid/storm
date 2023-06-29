/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.space;

import it.grid.storm.namespace.model.VirtualFS;

public interface SpaceUpdaterHelperInterface {

	public boolean increaseUsedSpace(VirtualFS vfs, long size);

	public boolean decreaseUsedSpace(VirtualFS vfs, long size);

}
