/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.space;

import it.grid.storm.namespace.model.VirtualFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NullSpaceUpdaterHelper implements SpaceUpdaterHelperInterface {

  private static final Logger log = LoggerFactory.getLogger(NullSpaceUpdaterHelper.class);

  @Override
  public boolean increaseUsedSpace(VirtualFS vfs, long size) {

    log.debug("NullSpaceUpdaterHelper doesn't increase used size!");
    return true;
  }

  @Override
  public boolean decreaseUsedSpace(VirtualFS vfs, long size) {

    log.debug("NullSpaceUpdaterHelper doesn't decrease used size!");
    return true;
  }
}
