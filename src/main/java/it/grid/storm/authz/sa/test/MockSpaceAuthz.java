/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.authz.sa.test;

import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.sa.AuthzDBInterface;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.griduser.GridUserInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockSpaceAuthz implements SpaceAuthzInterface {

  private static final String MOCK_ID = "mock-space-authz";
  private static final Logger log = LoggerFactory.getLogger(MockSpaceAuthz.class);

  public MockSpaceAuthz() {}

  /**
   * authorize
   *
   * @param guser GridUserInterface
   * @param srmSpaceOp SRMSpaceRequest
   * @return boolean
   */
  @Override
  public boolean authorize(GridUserInterface guser, SRMSpaceRequest srmSpaceOp) {

    log.debug("MOCK Space Authz : Authorize = Always TRUE");
    return true;
  }

  @Override
  public boolean authorizeAnonymous(SRMSpaceRequest srmSpaceOp) {

    log.debug("MOCK Space Authz : Authorize = Always TRUE");
    return true;
  }

  /**
   * setAuthzDB
   *
   * @param authzDB AuthzDBInterface
   */
  public void setAuthzDB(AuthzDBInterface authzDB) {

    log.debug("MOCK Space Authz : Set Authz DB :D ");
  }

  public void refresh() {

    log.debug("MOCK Space Authz : Refresh DB : ;) ");
  }

  public String getSpaceAuthzID() {

    return MOCK_ID;
  }
}
