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

package it.grid.storm.space;

import it.grid.storm.catalogs.InvalidRetrievedDataException;
import it.grid.storm.catalogs.InvalidSpaceDataAttributesException;
import it.grid.storm.catalogs.MultipleDataEntriesException;
import it.grid.storm.catalogs.NoDataFoundException;
import it.grid.storm.catalogs.ReducedPtPChunkData;
import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.types.InvalidPFNAttributeException;
import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.UnapprochableSurlException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TransferObjectDecodingException;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TSpaceType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is part of the StoRM project. Copyright (c) 2008 INFN-CNAF.
 * <p>
 * 
 * This class implements a Space Manager.
 * 
 * Authors:
 * 
 * @author lucamag
 * 
 */

public class SpaceHelper {

	private static final int ADD = 0;
	private static final int REMOVE = 1;
	private Configuration config;
	private static final Logger log = LoggerFactory.getLogger(SpaceHelper.class);
	public static GridUserInterface storageAreaOwner = GridUserManager
		.makeSAGridUser();

	public SpaceHelper() {

		config = Configuration.getInstance();
	}

	/**
	 * @param log
	 * @param funcName
	 * @param user
	 * @param surl
	 */

	private void updateSpaceUsageForSA(Logger log, String funcName,
		GridUserInterface user, TSURL surl, int operation, long filesize) {

		log.debug(funcName + " Updating Storage Area free size on db");
		ReservedSpaceCatalog catalog = new ReservedSpaceCatalog();
		StoRI stori = null;
		// Retrieve the StoRI associate to the SURL
		if (user == null) {
			// implicit put done by TimerTask
			try {
				stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(surl);
			} catch (UnapprochableSurlException e) {
				log.warn("Unable to build a stori for requested surl " + surl
					+ " UnapprochableSurlException: " + e.getMessage());
				return;
			}
		} else {
			try {
				stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(surl, user);
			} catch (IllegalArgumentException e) {
				log.error(
					funcName + " Unable to build StoRI by SURL and user: " + surl, e);
				return;
			} catch (UnapprochableSurlException e) {
				log.warn("Unable to build a stori for surl " + surl + " for user "
					+ user + " UnapprochableSurlException: " + e.getMessage());
				return;
			}
		}
		// Get Virtual FileSystem for DB information
		VirtualFSInterface fs = stori.getVirtualFileSystem();

		// Get StorageSpaceData from the database
		StorageSpaceData spaceData = null;
		try {
			spaceData = catalog.getStorageSpaceByAlias(fs.getSpaceTokenDescription());

		} catch (NamespaceException e1) {
			log.error("Unable to create storage space data", e1);
			return;
		}

		// Get the localELement to know the real file size, if exists
		LocalFile localElement = stori.getLocalFile();

		if (spaceData != null
			&& (localElement.exists() || operation == SpaceHelper.ADD)) {

			// IF PutDone, calculate the real fileSize
			if (operation == SpaceHelper.REMOVE) {
				// increase used size by localElement size
				filesize = localElement.getExactSize();
				// else in case of RM the filesize is passed from the client
			}

			TSizeInBytes availableSize = spaceData.getAvailableSpaceSize();
			long usedSize = -1;
			if (operation == SpaceHelper.REMOVE) {
				// remainingSize = availableSize.value() - filesize;
				usedSize = spaceData.getUsedSpaceSize().value() + filesize;
			} else if (operation == SpaceHelper.ADD) {
				// The new remaining size cannot be greater than the total size
				long newAvailableSize = availableSize.value() + filesize;
				// Use Storage Area Total Size as upper limit for the new Unused Size
				long totalSize = spaceData.getTotalSpaceSize().value();
				newAvailableSize = (newAvailableSize > totalSize) ? totalSize
					: newAvailableSize;
				long reservedSize = spaceData.getReservedSpaceSize().isEmpty() ? 0
					: spaceData.getReservedSpaceSize().value();
				long unavailableSize = spaceData.getUnavailableSpaceSize().isEmpty() ? 0
					: spaceData.getUnavailableSpaceSize().value();
				usedSize = totalSize - newAvailableSize - reservedSize
					- unavailableSize;
			}

			// Prevent negative value
			if (usedSize < 0) {
				usedSize = 0;
			}

			// Update the unused space size with new value
			TSizeInBytes newUsedSize = spaceData.getTotalSpaceSize();
			try {
				newUsedSize = TSizeInBytes.make(usedSize, SizeUnit.BYTES);
			} catch (InvalidTSizeAttributesException ex) {
				// never thrown
				log
					.error(
						funcName
							+ " Unexpected InvalidTSizeAttributesException , Unable to create  new used size, so the previous one is used",
						ex);
			}

			spaceData.setUsedSpaceSize(newUsedSize);

			try {
				fs.storeSpaceByToken(spaceData);
			} catch (NamespaceException e) {
				log.error(funcName + " Unable to update the new free size.", e);
			}

			log.debug(funcName + " Storage Area used size updated to: "
				+ newUsedSize.value());

		} else {
			// Nothing to do. Problem with DB?
			log.error(funcName + " Unable to update the DB used size!");
			return;
		}

	}

	/**
	 * This helper function is used to update the Free Size for certain Storage
	 * Area. From a PtPReducedChunk array.
	 * 
	 * 
	 * @param log
	 * @param funcName
	 * @param user
	 * @param spaceAvailableSURLs
	 */

	public void consumeSpaceForSA(Logger log, String funcName,
		GridUserInterface user, ArrayList spaceAvailableSURLs) {

		// Update the Storage Area Free size into the DB for each
		// PtPReducedChunkData specified

		for (int i = 0; i < spaceAvailableSURLs.size(); i++) {

			log.debug("srmPutDone: Updating Storage Area free size on db");

			ReducedPtPChunkData chunkData = (ReducedPtPChunkData) spaceAvailableSURLs
				.get(i);
			TSURL surl = chunkData.toSURL();

			updateSpaceUsageForSA(log, funcName, user, surl, SpaceHelper.REMOVE, 0);

		}

	}

	/**
	 * @param log
	 * @param funcName
	 * @param user
	 * @param surl
	 */
	public void decreaseFreeSpaceForSA(Logger log, String funcName,
		GridUserInterface user, TSURL surl) {

		updateSpaceUsageForSA(log, funcName, user, surl, SpaceHelper.REMOVE, 0);

	}

	/**
	 * Increase the free Storage Area free space in case of file removal The file
	 * size have to be passed as parameter since the file does not exists anymore.
	 * 
	 * @param log
	 * @param funcName
	 * @param user
	 * @param surl
	 * @param fileSize
	 */
	public void increaseFreeSpaceForSA(Logger log, String funcName,
		GridUserInterface user, TSURL surl, long fileSize) {

		updateSpaceUsageForSA(log, funcName, user, surl, SpaceHelper.ADD, fileSize);
	}

	public void increaseFreeSpaceForSA(Logger log, String funcName, TSURL surl,
		long fileSize) {

		updateSpaceUsageForSA(log, funcName, null, surl, SpaceHelper.ADD, fileSize);
	}

	public boolean isSAFull(Logger log, StoRI stori) {

		log.debug("Checking if the Storage Area is full");

		VirtualFSInterface fs = stori.getVirtualFileSystem();
		ReservedSpaceCatalog catalog = new ReservedSpaceCatalog();

		// Get StorageSpaceData from the database
		String SSDesc;
		StorageSpaceData spaceData = null;

		try {
			SSDesc = fs.getSpaceTokenDescription();
			spaceData = catalog.getStorageSpaceByAlias(SSDesc);

		} catch (NamespaceException e1) {
			log.error("Unable to create storage space data", e1);
			return false;
		}

		if ((spaceData != null) && (spaceData.getAvailableSpaceSize().value() == 0)) {
			log.debug("AvailableSize=" + spaceData.getAvailableSpaceSize().value());
			return true;
		} else {
			return false;
		}

	}

	public long getSAFreeSpace(Logger log, StoRI stori) {

		log.debug("Checking if the Storage Area is full");

		VirtualFSInterface fs = stori.getVirtualFileSystem();
		ReservedSpaceCatalog catalog = new ReservedSpaceCatalog();

		// Get StorageSpaceData from the database
		String SSDesc;
		StorageSpaceData spaceData = null;

		try {
			SSDesc = fs.getSpaceTokenDescription();
			spaceData = catalog.getStorageSpaceByAlias(SSDesc);

		} catch (NamespaceException e1) {
			log.error("Unable to create storage space data", e1);
			return -1;
		}

		if (spaceData != null) {
			return spaceData.getAvailableSpaceSize().value();
		} else {
			return -1;
		}

	}

	/**
	 * Verifies if the storage area to which the provided stori belongs has been
	 * initialized The verification is made on used space field
	 * 
	 * @param log
	 * @param stori
	 * @return
	 */
	public boolean isSAInitialized(Logger log, StoRI stori)
		throws IllegalArgumentException {

		log.debug("Checking if the Storage Area is initialized");
		if (stori == null || log == null) {
			throw new IllegalArgumentException(
				"Unable to perform the SA initialization check, provided null parameters: log : "
					+ log + " , stori : " + stori);
		}
		boolean response = false;
		VirtualFSInterface fs = stori.getVirtualFileSystem();
		ReservedSpaceCatalog catalog = new ReservedSpaceCatalog();
		// Get StorageSpaceData from the database
		String SSDesc;
		try {
			SSDesc = fs.getSpaceTokenDescription();
		} catch (NamespaceException e) {
			// will never happen
			log.error(
				"NamespaceException during VirtualFSInterface.getSpaceTokenDescription(). "
					+ "This is impossible, this exception is never thrown", e);
			return false;
		}
		StorageSpaceData spaceData = catalog.getStorageSpaceByAlias(SSDesc);
		if (spaceData != null && !(spaceData.getUsedSpaceSize() == null)
			&& !spaceData.getUsedSpaceSize().isEmpty()
			&& !(spaceData.getUsedSpaceSize().value() < 0)) {
			response = true;
		}
		log.debug("The storage area is initialized with token alias "
			+ spaceData.getSpaceTokenAlias() + " is " + (response ? "" : "not")
			+ "initialized");
		return response;
	}

	/**
	 * 
	 * @param log
	 * @param stori
	 * @return
	 */
	public TSpaceToken getTokenFromStoRI(Logger log, StoRI stori) {

		log.debug("SpaceHelper: getting space token from StoRI");
		TSpaceToken token = TSpaceToken.makeEmpty();
		VirtualFSInterface fs = stori.getVirtualFileSystem();

		try {
			token = fs.getSpaceToken();
		} catch (NamespaceException e) {
			log.warn("Unable to retrieve SpaceToken for stori:'" + stori + "'");
		}

		return token;

	}

	/**
	 * Returns the spaceTokens associated to the 'user' AND 'spaceAlias'. If
	 * 'spaceAlias' is NULL or an empty string then this method returns all the
	 * space tokens this 'user' owns.
	 * 
	 * @param user
	 *          VomsGridUser user.
	 * @param spaceAlias
	 *          User space token description.
	 */
	private Boolean isDefaultSpaceToken(TSpaceToken token) {

		Boolean found = false;

		config = Configuration.getInstance();
		List<String> tokens = config.getListOfDefaultSpaceToken();
		for (int i = 0; i < tokens.size(); i++) {
			if ((tokens.get(i)).toLowerCase().equals(token.getValue().toLowerCase())) {
				found = true;
			}
		}

		return found;
	}

	/**
	 * This method is used by the namespace parser component to insert a new Space
	 * Token Description data into the space catalog. In this way a standard Space
	 * Token is created, making it work for the GetSpaceMetaData request an
	 * SrmPreparateToPut with SpaceToken.
	 * 
	 * The following code check if a SA_token with the same space description is
	 * already present into the catalog, if no data are found the new data are
	 * inserted, if yes the new data and the data already present are compared,
	 * and if needed an update operation is performed.
	 * 
	 * The mandatory parameters are:
	 * 
	 * @param spaceTokenAlias
	 *          the space token description the user have to specify into the
	 *          namespace.xml file
	 * @param totalOnLineSize
	 *          the size the user have to specify into the namespace.xml file
	 * @param date
	 * @param spaceFileName
	 *          the space file name will be used to get the free size. It is the
	 *          StFNRoot.
	 */

	public TSpaceToken createVOSA_Token(String spaceTokenAlias,
		TSizeInBytes totalOnLineSize, String spaceFileName) {

		// TODO errors are not managed in this function
		TSpaceToken spaceToken = null;
		ArrayOfTSpaceToken tokenArray;
		ReservedSpaceCatalog spaceCatalog = new ReservedSpaceCatalog();

		// Try with fake user, if it does not work remove it and use different
		// method

		// First, check if the same VOSpaceArea already exists
		tokenArray = spaceCatalog.getSpaceTokensByAlias(spaceTokenAlias);

		if (tokenArray == null || tokenArray.size() == 0) {
			// the VOSpaceArea does not exist yet
			SpaceHelper.log.debug("VoSpaceArea " + spaceTokenAlias
				+ " still does not exists. Start creation process.");

			PFN sfname = null;
			try {
				sfname = PFN.make(spaceFileName);
			} catch (InvalidPFNAttributeException e1) {
				log.error("Error building PFN with " + spaceFileName + " : " + e1);
			}

			StorageSpaceData ssd = null;

			try {
				ssd = new StorageSpaceData(storageAreaOwner, TSpaceType.VOSPACE,
					spaceTokenAlias, totalOnLineSize, totalOnLineSize,
					TLifeTimeInSeconds.makeInfinite(), null, null, sfname);
				// ssd.setReservedSpaceSize(totalOnLineSize);
				try {
					ssd.setUnavailableSpaceSize(TSizeInBytes.make(0, SizeUnit.BYTES));
					ssd.setReservedSpaceSize(TSizeInBytes.make(0, SizeUnit.BYTES));

				} catch (InvalidTSizeAttributesException e) {
					// never thrown
					log.error("Unexpected InvalidTSizeAttributesException: "
						+ e.getMessage());
				}
				spaceToken = ssd.getSpaceToken();
			} catch (InvalidSpaceDataAttributesException e) {
				log.error("Error building StorageSpaceData: " + e);
			}

			try {
				spaceCatalog.addStorageSpace(ssd);
			} catch (DataAccessException e) {
				log.error("Error storing StorageSpaceData on the DB: " + e);
			}
			// Track into global set to remove obsolete SA_token
			ReservedSpaceCatalog.addSpaceToken(spaceToken);

		} else {
			/*
			 * the VOspaceArea already exists. Compare new data and data already
			 * present to check if the parameter has changed or not, and then perform
			 * update operation into catalog if it is needed. Only static information
			 * changes determine an update of the exeisting row
			 */
			SpaceHelper.log.debug("VOSpaceArea for space token description "
				+ spaceTokenAlias + " already present into  DB.");

			boolean equal = false;
			spaceToken = tokenArray.getTSpaceToken(0);
			StorageSpaceData catalog_ssd = null;
			try {
				catalog_ssd = spaceCatalog.getStorageSpace(spaceToken);
			} catch (TransferObjectDecodingException e) {
				log
					.error("Unable to build StorageSpaceData from StorageSpaceTO. TransferObjectDecodingException: "
						+ e.getMessage());
			} catch (DataAccessException e) {
				log.error("Unable to build get StorageSpaceTO. DataAccessException: "
					+ e.getMessage());
			}

			if (catalog_ssd != null) {

				if (catalog_ssd.getOwner().getDn().equals(storageAreaOwner.getDn())
					&& (catalog_ssd.getSpaceTokenAlias().equals(spaceTokenAlias))
					&& (catalog_ssd.getTotalSpaceSize().value() == totalOnLineSize
						.value())
					&& (catalog_ssd.getSpaceFileName().toString().equals(spaceFileName))) {
					equal = true;
				}

			}

			// false otherwise
			if (equal) {
				// Do nothing if equals, everything are already present into
				// the DB
				SpaceHelper.log.debug("VOSpaceArea for space token description "
					+ spaceTokenAlias + " is already up to date.");
				ReservedSpaceCatalog.addSpaceToken(spaceToken);

			} else {
				// If the new data has been modified, update the data into the
				// catalog
				SpaceHelper.log.debug("VOSpaceArea for space token description "
					+ spaceTokenAlias
					+ " is different in some parameters. Updating the catalog.");
				try {
					catalog_ssd.setOwner(storageAreaOwner);
					catalog_ssd.setTotalSpaceSize(totalOnLineSize);
					catalog_ssd.setTotalGuaranteedSize(totalOnLineSize);

					PFN sfn = null;
					try {
						sfn = PFN.make(spaceFileName);
					} catch (InvalidPFNAttributeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catalog_ssd.setSpaceFileName(sfn);

					spaceCatalog.updateAllStorageSpace(catalog_ssd);
					ReservedSpaceCatalog.addSpaceToken(spaceToken);

				} catch (NoDataFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidRetrievedDataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MultipleDataEntriesException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			// Warning. CHeck if there are multiple token with same alisa, this
			// is not allowed
			if (tokenArray.size() > 1) {
				SpaceHelper.log
					.error("Error: multiple Space Token found for the same space Alias: "
						+ spaceTokenAlias + ". Only one has been evaluated!");
			}

		}
		return spaceToken;

	}

	/**
	 * This method should be use at the end of the namespace insert process
	 * (through the createVO_SA_token(...)) to remmove from the database the old
	 * VO_SA_token inserted from the previous namsespace.xml configuration
	 * 
	 */
	public void purgeOldVOSA_token() {

		purgeOldVOSA_token(SpaceHelper.log);
	}

	public void purgeOldVOSA_token(Logger log) {

		ReservedSpaceCatalog spacec = new ReservedSpaceCatalog();
		log.debug("VO SA: garbage collecting obsolete VOSA_token");

		Iterator<TSpaceToken> iter = ReservedSpaceCatalog.getTokenSet().iterator();
		while (iter.hasNext()) {
			log.debug("VO SA token REGISTRED:" + iter.next().getValue());
		}

		GridUserInterface stormServiceUser = GridUserManager.makeSAGridUser();

		// Remove obsolete space
		ArrayOfTSpaceToken token_a = spacec.getSpaceTokens(stormServiceUser, null);
		for (int i = 0; i < token_a.size(); i++) {
			log.debug("VO SA token IN CATALOG:"
				+ token_a.getTSpaceToken(i).getValue());
		}

		if ((token_a != null) && (token_a.size() > 0)) {
			for (int i = 0; i < token_a.size(); i++) {

				if (!ReservedSpaceCatalog.getTokenSet().contains(
					token_a.getTSpaceToken(i))) {
					// This VOSA_token is no more used, removing it from persistence
					TSpaceToken tokenToRemove = token_a.getTSpaceToken(i);
					log.debug("VO SA token " + tokenToRemove
						+ " is no more used, removing it from persistence.");
					spacec.release(stormServiceUser, tokenToRemove);
				}
			}
		} else {
			log
				.warn("Space Catalog garbage SA_Token: no SA TOKENs specified. Please check your namespace.xml file.");
		}

		ReservedSpaceCatalog.clearTokenSet();

	}

	/**
	 * @param spaceData
	 * @return
	 */
	public static boolean isStorageArea(StorageSpaceData spaceData)
		throws IllegalArgumentException {

		if (spaceData == null) {
			log.error("Received null spaceData parameter");
			throw new IllegalArgumentException("Received null spaceData parameter");
		}
		boolean result = false;
		if (spaceData.getOwner() != null) {
			result = spaceData.getOwner().equals(SpaceHelper.storageAreaOwner);
		}
		return result;
	}
}
