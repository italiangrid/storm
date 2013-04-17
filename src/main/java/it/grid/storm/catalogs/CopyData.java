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

package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a CopyChunkData, that is part of a multifile Copy srm
 * request. It contains data about: the requestToken, the fromSURL, the toSURL,
 * the target fileLifeTime, the target fileStorageType and any available target
 * spaceToken, the target overwriteOption to be applied in case the file already
 * exists, the fileSize of the existing file if any, return status of the file
 * together with its error string.
 * 
 * @author EGRID - ICTP Trieste
 * @date September, 2005
 * @version 2.0
 */
public class CopyData extends SurlMultyOperationRequestData {

	private static final Logger log = LoggerFactory.getLogger(CopyData.class);

	/**
	 * SURL to which the srmCopy will put the file
	 */
	protected TSURL destinationSURL;

	/**
	 * requested lifetime - BEWARE!!! It is the fileLifetime at destination in
	 * case of Volatile files!
	 */
	protected TLifeTimeInSeconds lifetime;

	/**
	 * TFileStorageType at destination
	 */
	protected TFileStorageType fileStorageType;

	/**
	 * SpaceToken to use for toSURL
	 */
	protected TSpaceToken spaceToken;

	/**
	 * specifies the behaviour in case of existing files for Put part of the copy
	 * (could be local or remote!)
	 */
	protected TOverwriteMode overwriteOption;

	public CopyData(TSURL fromSURL, TSURL destinationSURL,
		TLifeTimeInSeconds lifetime, TFileStorageType fileStorageType,
		TSpaceToken spaceToken, TOverwriteMode overwriteOption, TReturnStatus status)
		throws InvalidCopyDataAttributesException,
		InvalidSurlRequestDataAttributesException {

		super(fromSURL, status);
		if (destinationSURL == null || lifetime == null || fileStorageType == null
			|| spaceToken == null || overwriteOption == null) {
			throw new InvalidCopyDataAttributesException(fromSURL, destinationSURL,
				lifetime, fileStorageType, spaceToken, overwriteOption, status);
		}
		this.destinationSURL = destinationSURL;
		this.lifetime = lifetime;
		this.fileStorageType = fileStorageType;
		this.spaceToken = spaceToken;
		this.overwriteOption = overwriteOption;
	}

	/**
	 * Method that returns the toSURL of the srm request to which this chunk
	 * belongs.
	 */
	public TSURL getDestinationSURL() {

		return destinationSURL;
	}

	/**
	 * Method that returns the requested pin life time for this chunk of the srm
	 * request.
	 */
	public TLifeTimeInSeconds getLifetime() {

		return lifetime;
	}

	/**
	 * Method that returns the fileStorageType for this chunk of the srm request.
	 */
	public TFileStorageType getFileStorageType() {

		return fileStorageType;
	}

	/**
	 * Method that returns the space token supplied for this chunk of the srm
	 * request.
	 */
	public TSpaceToken getSpaceToken() {

		return spaceToken;
	}

	/**
	 * Method that returns the overwriteOption specified in the srm request.
	 */
	public TOverwriteMode getOverwriteOption() {

		return overwriteOption;
	}

	/**
	 * Method that sets the status of this request to SRM_DUPLICATION_ERROR; it
	 * needs the explanation String which describes the situation in greater
	 * detail; if a null is passed, then an empty String is used as explanation.
	 */
	public void changeStatusSRM_DUPLICATION_ERROR(String explanation) {

		setStatus(TStatusCode.SRM_DUPLICATION_ERROR, explanation);
	}

	/**
	 * Method that sets the status of this request to SRM_FATAL_INTERNAL_ERROR; it
	 * needs the explanation String which describes the situation in greater
	 * detail; if a null is passed, then an empty String is used as explanation.
	 */
	public void changeStatusSRM_FATAL_INTERNAL_ERROR(String explanation) {

		setStatus(TStatusCode.SRM_FATAL_INTERNAL_ERROR, explanation);
	}

	@Override
	protected Logger getLog() {

		return CopyData.log;
	}
}
