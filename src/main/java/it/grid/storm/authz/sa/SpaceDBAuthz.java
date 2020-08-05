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

/**
 * 
 */
package it.grid.storm.authz.sa;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.GridUserInterface;

/**
 * @author zappi
 */
public class SpaceDBAuthz extends SpaceAuthz {

  private static final Logger log = LoggerFactory.getLogger(SpaceDBAuthz.class);

  public static final String UNDEF = "undef-SpaceAuthzDB";

  private String spaceAuthzDBID = "not-defined";
  private static String configurationPATH;
  private String dbFileName;

  public SpaceDBAuthz() {

  }

  /**
   * @return
   */
  public static SpaceDBAuthz makeEmpty() {

    SpaceDBAuthz result = new SpaceDBAuthz();
    result.setSpaceAuthzDBID("default-SpaceAuthzDB");
    return result;
  }

  public SpaceDBAuthz(String dbFileName) {

    Configuration config = Configuration.getInstance();
    configurationPATH = config.namespaceConfigPath();
    if (existsAuthzDBFile(dbFileName)) {
      this.dbFileName = dbFileName;
      spaceAuthzDBID = dbFileName;
    }
  }

  /**
   * @param string
   */
  void setSpaceAuthzDBID(String id) {

    spaceAuthzDBID = id;
  }

  /**
   * 
   */
  @Override
  public boolean authorize(GridUserInterface guser, SRMSpaceRequest srmSpaceOp) {

    return false;
  }

  @Override
  public boolean authorizeAnonymous(SRMSpaceRequest srmSpaceOp) {

    return false;
  }


  /**********************************************************************
   * BUILDINGs METHODS
   */

  /**
   * Check the existence of the AuthzDB file
   */
  private boolean existsAuthzDBFile(String dbFileName) {

    String fileName = configurationPATH + File.separator + dbFileName;
    boolean exists = (new File(fileName)).exists();
    if (!(exists)) {
      log.error("The AuthzDB File '{}' does not exists", dbFileName);
    }
    return exists;
  }

  /**
   * Return the AuthzDB FileName
   * 
   * @return
   */
  String getAuthzDBFileName() {

    return dbFileName;
  }

  public String getSpaceAuthzID() {

    return spaceAuthzDBID;
  }

  /**
   * 
   */
  public void refresh() {

    // empty
  }

}
