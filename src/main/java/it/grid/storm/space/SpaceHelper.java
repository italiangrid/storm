/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.space;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.catalogs.InvalidRetrievedDataException;
import it.grid.storm.catalogs.InvalidSpaceDataAttributesException;
import it.grid.storm.catalogs.MultipleDataEntriesException;
import it.grid.storm.catalogs.NoDataFoundException;
import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.model.VirtualFS;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TransferObjectDecodingException;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TSpaceType;

public class SpaceHelper {

	private static final int ADD_FREE_SPACE = 0;
	private static final int REMOVE_FREE_SPACE = 1;
	private Configuration config;
	private static final Logger log = LoggerFactory.getLogger(SpaceHelper.class);
	public static GridUserInterface storageAreaOwner = GridUserManager
		.makeSAGridUser();

	public SpaceHelper() {

		config = Configuration.getInstance();
	}

	public boolean isSAFull(Logger log, StoRI stori) {

		log.debug("Checking if the Storage Area is full");

		VirtualFS fs = stori.getVirtualFileSystem();
		ReservedSpaceCatalog catalog = new ReservedSpaceCatalog();

		// Get StorageSpaceData from the database
		String ssDesc = fs.getSpaceTokenDescription();
		StorageSpaceData spaceData = catalog.getStorageSpaceByAlias(ssDesc);

		if ((spaceData != null) && (spaceData.getAvailableSpaceSize().value() == 0)) {
			log.debug("AvailableSize={}" , spaceData.getAvailableSpaceSize().value());
			return true;
		} else {
			return false;
		}

	}

	public long getSAFreeSpace(Logger log, StoRI stori) {

		log.debug("Checking if the Storage Area is full");

		VirtualFS fs = stori.getVirtualFileSystem();
		ReservedSpaceCatalog catalog = new ReservedSpaceCatalog();

		// Get StorageSpaceData from the database
		String ssDesc = fs.getSpaceTokenDescription();
		StorageSpaceData spaceData = catalog.getStorageSpaceByAlias(ssDesc);

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
	public boolean isSAInitialized(Logger log, StoRI stori) {

		log.debug("Checking if the Storage Area is initialized");
		if (stori == null) {
			throw new IllegalArgumentException(
				"Unable to perform the SA initialization check, provided null parameters: log : "
					+ log + " , stori : " + stori);
		}
		boolean response = false;
		VirtualFS fs = stori.getVirtualFileSystem();
		ReservedSpaceCatalog catalog = new ReservedSpaceCatalog();
		// Get StorageSpaceData from the database
		String ssDesc = fs.getSpaceTokenDescription();

		StorageSpaceData spaceData = catalog.getStorageSpaceByAlias(ssDesc);

		if (spaceData != null && spaceData.getUsedSpaceSize() != null
			&& !spaceData.getUsedSpaceSize().isEmpty()
			&& spaceData.getUsedSpaceSize().value() >= 0) {

			response = true;
		}
		log.debug("The storage area is initialized with token alias {} is {} initialized"
			, spaceData.getSpaceTokenAlias() , (response ? "" : "not"));
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
		VirtualFS fs = stori.getVirtualFileSystem();
		return fs.getSpaceToken();

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
			SpaceHelper.log.debug("VoSpaceArea {} still does not exists. Start creation process." , spaceTokenAlias);

			PFN sfname = PFN.make(spaceFileName);

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
					log.error("Unexpected InvalidTSizeAttributesException: {}"
						, e.getMessage(),e);
				}
				spaceToken = ssd.getSpaceToken();
			} catch (InvalidSpaceDataAttributesException e) {
				log.error("Error building StorageSpaceData: " , e);
			}

			try {
				spaceCatalog.addStorageSpace(ssd);
			} catch (DataAccessException e) {
				log.error("Error storing StorageSpaceData on the DB: " , e);
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
					.error("Unable to build StorageSpaceData from StorageSpaceTO. TransferObjectDecodingException: {}"
						, e.getMessage(),e);
			} catch (DataAccessException e) {
				log.error("Unable to build get StorageSpaceTO. DataAccessException: {}"
					, e.getMessage(),e);
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
				SpaceHelper.log.debug("VOSpaceArea for space token description {} is already up to date."
					, spaceTokenAlias);
				ReservedSpaceCatalog.addSpaceToken(spaceToken);

			} else {
				// If the new data has been modified, update the data into the
				// catalog
				SpaceHelper.log.debug("VOSpaceArea for space token description {} is different in some parameters. Updating the catalog."
					, spaceTokenAlias);
				try {
					catalog_ssd.setOwner(storageAreaOwner);
					catalog_ssd.setTotalSpaceSize(totalOnLineSize);
					catalog_ssd.setTotalGuaranteedSize(totalOnLineSize);

					PFN sfn = PFN.make(spaceFileName);
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
					.error("Error: multiple Space Token found for the same space Alias: {}. Only one has been evaluated!"
						, spaceTokenAlias);
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
			log.debug("VO SA token REGISTRED: {}" , iter.next().getValue());
		}

		GridUserInterface stormServiceUser = GridUserManager.makeSAGridUser();

		// Remove obsolete space
		ArrayOfTSpaceToken token_a = spacec.getSpaceTokens(stormServiceUser, null);
		for (int i = 0; i < token_a.size(); i++) {
			log.debug("VO SA token IN CATALOG: {}" , token_a.getTSpaceToken(i).getValue());
		}

		if ((token_a != null) && (token_a.size() > 0)) {
			for (int i = 0; i < token_a.size(); i++) {

				if (!ReservedSpaceCatalog.getTokenSet().contains(
					token_a.getTSpaceToken(i))) {
					// This VOSA_token is no more used, removing it from persistence
					TSpaceToken tokenToRemove = token_a.getTSpaceToken(i);
					log.debug("VO SA token {}  is no more used, removing it from persistence." , tokenToRemove);
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
