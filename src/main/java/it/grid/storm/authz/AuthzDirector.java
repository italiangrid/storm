/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.authz;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import it.grid.storm.authz.path.PathAuthz;
import it.grid.storm.authz.path.conf.PathAuthzDBReader;
import it.grid.storm.authz.sa.AuthzDBReaderException;
import it.grid.storm.authz.sa.SpaceDBAuthz;
import it.grid.storm.authz.sa.test.MockSpaceAuthz;
import it.grid.storm.namespace.Namespace;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.model.SAAuthzType;
import it.grid.storm.namespace.model.VirtualFS;
import it.grid.storm.srm.types.TSpaceToken;

public class AuthzDirector {

  private static final Logger log = LoggerFactory.getLogger(AuthzDirector.class);
  private static String configurationPATH;

  // Map between 'SpaceToken' and the related 'SpaceAuthz'
  private static Map<TSpaceToken, SpaceAuthzInterface> spaceAuthzs = null;

  // PathAuthz is only one, shared by all SAs
  private static PathAuthzInterface pathAuthz = null;

  /**
   * Scan the Namespace.xml to retrieve the list of file AuthZDB to digest
   */
  private static Map<TSpaceToken, SpaceAuthzInterface> buildSpaceAuthzsMAP() {

    Map<TSpaceToken, SpaceAuthzInterface> spaceAuthzMap = Maps.newHashMap();

    // Retrieve the list of VFS from Namespace
    Namespace ns = Namespace.getInstance();
    List<VirtualFS> vfss;
    try {
      vfss = Lists.newArrayList(ns.getAllDefinedVFS());
      for (VirtualFS vfs : vfss) {
        String vfsName = vfs.getAliasName();
        SAAuthzType authzTp = vfs.getStorageAreaAuthzType();
        String authzName = "";
        if (authzTp.equals(SAAuthzType.AUTHZDB)) {
          // The Space Authz is based on Authz DB
          authzName = vfs.getStorageAreaAuthzDB();
          log.debug("Loading AuthzDB '{}'", authzName);
          if (existsAuthzDBFile(authzName)) {
            // Digest the Space AuthzDB File
            TSpaceToken spaceToken = vfs.getSpaceToken();
            SpaceAuthzInterface spaceAuthz = new SpaceDBAuthz(authzName);
            spaceAuthzMap.put(spaceToken, spaceAuthz);
          } else {
            log.error("File AuthzDB '{}' related to '{}' does not exists.", authzName, vfsName);
          }
        } else {
          authzName = vfs.getStorageAreaAuthzFixed();
        }
        log.debug("VFS ['{}'] = {} : {}", vfsName, authzTp, authzName);
      }
    } catch (NamespaceException e) {
      log.error("Unable to initialize AUTHZ DB! Error: {}", e.getMessage(), e);
    }

    return spaceAuthzMap;
  }

  /**
   * Utility method
   * 
   * @param dbFileName
   * @return
   * @throws AuthzDBReaderException
   */
  private static boolean existsAuthzDBFile(String dbFileName) {

    String fileName = configurationPATH + File.separator + dbFileName;
    boolean exists = (new File(fileName)).exists();
    if (!exists) {
      log.warn("The AuthzDB File '{}' does not exists", dbFileName);
    }
    return exists;
  }

  // ****************************************
  // PUBLIC METHODS
  // ****************************************

  /******************************
   * SPACE AUTHORIZATION ENGINE
   ******************************/
  public static void initializeSpaceAuthz() {

    // Build Space Authzs MAP
    spaceAuthzs = buildSpaceAuthzsMAP();
  }

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

  /******************************
   * PATH AUTHORIZATION ENGINE
   ******************************/

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
