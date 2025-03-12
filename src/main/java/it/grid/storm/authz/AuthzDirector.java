/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.authz;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.authz.path.PathAuthz;
import it.grid.storm.authz.path.conf.PathAuthzDBReader;
import it.grid.storm.authz.sa.test.MockSpaceAuthz;
import it.grid.storm.srm.types.TSpaceToken;

public class AuthzDirector {

  private static final Logger log = LoggerFactory.getLogger(AuthzDirector.class);

  // Map between 'SpaceToken' and the related 'SpaceAuthz'
  private static Map<TSpaceToken, SpaceAuthzInterface> spaceAuthzs = null;

  // PathAuthz is only one, shared by all SAs
  private static PathAuthzInterface pathAuthz = null;

  /**
   * Retrieve the Space Authorization module related to the Space Token
   * 
   * @param token
   * @return
   */
  public static SpaceAuthzInterface getSpaceAuthz(TSpaceToken token) {

    SpaceAuthzInterface spaceAuthz = new MockSpaceAuthz();
    // Retrieve the SpaceAuthz related to the Space Token
    if ((spaceAuthzs != null) && (spaceAuthzs.containsKey(token))) {
      spaceAuthz = spaceAuthzs.get(token);
      log.debug("Space Authz related to S.Token ='{}' is '{}'", token,
          spaceAuthz.getSpaceAuthzID());
    } else {
      log.debug("Space Authz related to S.Token ='{}' does not exists. " + "Use the MOCK one.",
          token);
    }
    return spaceAuthz;
  }

  /**
   * Initializing the Path Authorization engine
   * 
   * @param pathAuthz2
   */
  public static void initializePathAuthz(String pathAuthzDBFileName) throws DirectorException {

    PathAuthzDBReader authzDBReader;
    try {
      authzDBReader = new PathAuthzDBReader(pathAuthzDBFileName);
    } catch (Exception e) {
      log.error("Unable to build a PathAuthzDBReader: {}", e.getMessage(), e);
      throw new DirectorException("Unable to build a PathAuthzDBReader");
    }
    AuthzDirector.pathAuthz = new PathAuthz(authzDBReader.getPathAuthzDB());
  }

  /**
   * Retrieve the Path Authorization module
   * 
   * @todo: To implement this.
   */
  public static PathAuthzInterface getPathAuthz() {

    return AuthzDirector.pathAuthz;
  }

}
