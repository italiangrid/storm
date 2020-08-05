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

import it.grid.storm.authz.sa.model.FileAuthzDB;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.GridUserInterface;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 */
public class SpaceDBAuthz extends SpaceAuthz {

	private static final Logger log = LoggerFactory.getLogger(SpaceDBAuthz.class);
	public final static String UNDEF = "undef-SpaceAuthzDB";
	private String spaceAuthzDBID = "not-defined";
	private static String configurationPATH;
	private String dbFileName;
	private FileAuthzDB authzDB;

	public SpaceDBAuthz() {

	}

	/**
	 * @return
	 */
	public static SpaceDBAuthz makeEmpty() {

		SpaceDBAuthz result = new SpaceDBAuthz();
		result.setSpaceAuthzDBID("default-SpaceAuthzDB");
		// * @todo other assignments
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

		// TODO Auto-generated method stub

		// Check if the Cache is Locked (in the case, skip the use of the cache)

		// Check the presence of guser in the Cache

		// If requestor is present in the AuthzCache, retrieve the response for
		// the SpaceOp

		// Else, compute the Authz Answer for ALL the SpaceOp and insert into
		// the cache

		// Return the result
		return false;
	}

	@Override
	public boolean authorizeAnonymous(SRMSpaceRequest srmSpaceOp) {

		// TODO Auto-generated method stub
		return false;
	}

	/**********************************************************************
	 * AUTHZ Algorithm
	 */

	/**
	 * Implementation of NFSv4.1 ACL evaluation algorithm - simplified version:
	 * http://tools.ietf.org/html/draft-ietf-nfsv4-acl-mapping-02#section-2 - full
	 * version: http://tools.ietf.org/html/rfc3530#section-5.11.2
	 */
	private boolean nfs4AuthzAlgorithm(GridUserInterface guser,
		SRMSpaceRequest srmSpaceOp) {

		return false;
	}

	/**********************************************************************
	 * CACHE mechanism
	 */

	/**
	 * Method to check the presence of Requestor within the Cache
	 * 
	 * @param guser
	 * @return
	 */
	private boolean isPresent(GridUserInterface guser) {

		return false;
	}

	/**
	 * Method to add (and store somewhere) a new Requestor
	 * 
	 * @param guser
	 */
	private void addRequestorToCache(GridUserInterface guser) {

	}

	private void refreshCache() {

		// Take the LOCK on the Cache

		// At the end, release the LOCK on the Cache
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
			log.error("The AuthzDB File '" + dbFileName + "' does not exists");
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

	/**
	 * @param authzDB
	 */
	void setAuthzDB(FileAuthzDB authzDB) {

		// Refresh the cache

		// Set the updated authzDB
		this.authzDB = authzDB;
	}

	public String getSpaceAuthzID() {

		return spaceAuthzDBID;
	}

	public void refresh() {

		// TODO Auto-generated method stub

	}

}
