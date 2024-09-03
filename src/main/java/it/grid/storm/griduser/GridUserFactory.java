/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/*
 * You may copy, distribute and modify this file under the terms of the INFN
 * GRID licence. For a copy of the licence please visit
 * 
 * http://www.cnaf.infn.it/license.html
 * 
 * Riccardo Zappi <riccardo.zappi@cnaf.infn.it.it>, 2007 $Id:
 * GridUserFactory.java 3604 2007-05-22 11:16:27Z rzappi $
 */

package it.grid.storm.griduser;

import java.util.Map;

import org.slf4j.Logger;

import it.grid.storm.jna.lcmaps.MapperInterface;

public class GridUserFactory {

  private static final Logger log = GridUserManager.log;
  private static final String GRID_USER_MAPPER_CLASSNAME =
      "it.grid.storm.griduser.StormLcmapsJNAMapper";

  private MapperInterface defaultMapperClass = null;

  private static GridUserFactory instance = null;

  private GridUserFactory() throws GridUserException {

    defaultMapperClass = makeMapperClass(GRID_USER_MAPPER_CLASSNAME);
  }

  static GridUserFactory getInstance() {

    if (instance == null) {
      try {
        instance = new GridUserFactory();
      } catch (GridUserException ex) {
        log.error("Unable to load GridUser Mapper Driver!", ex);
      }
    }
    return instance;
  }

  /**
   * Build a simple GridUser. No VOMS attributes are passed..
   * 
   * @return GridUserInterface
   */
  GridUserInterface createGridUser(String distinguishName) {

    GridUserInterface user = new GridUser(defaultMapperClass, distinguishName);
    log.debug("Created new Grid User (NO VOMS) : {}", user);
    return user;
  }

  /**
   * Build a simple GridUser. Parsing of proxy is not performed here! This methos is meaningful only
   * for srmCopy call.
   * 
   * @return GridUserInterface
   */
  GridUserInterface createGridUser(String distinguishName, String proxyString) {

    GridUserInterface user = new GridUser(defaultMapperClass, distinguishName, proxyString);
    log.debug("Created new Grid User (NO VOMS with PROXY) : {}", user);
    return user;
  }

  /**
   * Build a VOMS Grid User, if FQAN passed are not null. Otherwise a simple GridUser instance wil
   * be returned.
   * 
   * @return GridUserInterface
   */
  GridUserInterface createGridUser(String distinguishName, FQAN[] fqans)
      throws IllegalArgumentException {

    GridUserInterface user = null;
    try {
      user = new VomsGridUser(defaultMapperClass, distinguishName, fqans);
    } catch (IllegalArgumentException e) {
      log.error(e.getMessage(), e);
      throw e;
    }
    log.debug("Created new Grid User (VOMS USER) : {}", user);
    return user;
  }

  /**
   * Build a VOMS Grid User, if FQAN passed are not null. Otherwise a simple GridUser instance wil
   * be returned.
   * 
   * @return GridUserInterface
   */
  GridUserInterface createGridUser(String distinguishName, FQAN[] fqans, String proxyString)
      throws IllegalArgumentException {

    GridUserInterface user = null;
    try {
      user = new VomsGridUser(defaultMapperClass, distinguishName, proxyString, fqans);
    } catch (IllegalArgumentException e) {
      log.error(e.getMessage(), e);
      throw e;
    }
    log.debug("Created new Grid User (VOMS USER with PROXY) : {}", user);
    return user;
  }

  GridUserInterface decode(Map<String, Object> inputParam) {

    // Member name for VomsGridUser Creation
    String member_DN = new String("userDN");
    String member_Fqans = new String("userFQANS");

    // Get DN and FQANs[]
    String dnString = (String) inputParam.get(member_DN);
    Object[] fqansArr = (Object[]) inputParam.get(member_Fqans);

    // Destination FQANs array
    FQAN[] fqans = null;

    if (fqansArr != null) {
      // Define FQAN[]
      fqans = new FQAN[fqansArr.length];
      log.debug("fqans_vector Size: {}", fqansArr.length);

      for (int i = 0; i < fqansArr.length; i++) {

        log.debug("FQAN[{}]: {}", i, (String) fqansArr[i]);
        fqans[i] = new FQAN((String) fqansArr[i]);
      }
    }

    if (dnString != null) {
      log.debug("DN: {}", dnString);
      // Creation of srm GridUser type
      if (fqans != null && fqans.length > 0) {
        log.debug("VomsGU with FQAN");
        try {
          return createGridUser(dnString, fqans);
        } catch (IllegalArgumentException e) {
          log.error(e.getMessage(), e);
        }
      } else {
        return createGridUser(dnString);
      }
    }
    return null;
  }

  private MapperInterface makeMapperClass(String mapperClassName) throws GridUserException {

    return new LcmapsJNAMapper();
  }
}
