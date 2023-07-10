/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/** */
package it.grid.storm.authz.sa.model;

import it.grid.storm.authz.sa.AuthzDBInterface;
import it.grid.storm.namespace.model.SAAuthzType;
import java.util.List;

/** @author zappi */
public abstract class AuthzDBFixed implements AuthzDBInterface {

  /*
   * (non-Javadoc)
   *
   * @see it.grid.storm.authz.sa.AuthzDBInterface#getAuthzDBType()
   */
  public SAAuthzType getAuthzDBType() {

    return SAAuthzType.FIXED;
  }

  /*
   * (non-Javadoc)
   *
   * @see it.grid.storm.authz.sa.AuthzDBInterface#getOrderedListOfACE()
   */
  public abstract List<SpaceACE> getOrderedListOfACE();
}
