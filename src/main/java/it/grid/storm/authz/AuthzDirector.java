/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.authz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.authz.path.PathAuthz;
import it.grid.storm.authz.path.conf.PathAuthzDBReader;

public class AuthzDirector {

  private static final Logger log = LoggerFactory.getLogger(AuthzDirector.class);

  // PathAuthz is only one, shared by all SAs
  private static PathAuthzInterface pathAuthz = null;

  /**
   * Initialize the Path Authorization engine
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
