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

package it.grid.storm.asynch;

import it.grid.storm.asynch.BuilderException;
import it.grid.storm.asynch.PtG;
import it.grid.storm.catalogs.AnonymousPtGData;
import it.grid.storm.catalogs.IdentityPtGData;
import it.grid.storm.catalogs.InvalidFileTransferDataAttributesException;
import it.grid.storm.catalogs.InvalidPtGDataAttributesException;
import it.grid.storm.catalogs.InvalidSurlRequestDataAttributesException;
import it.grid.storm.catalogs.PtGData;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.datatransfer.FileTransferInputData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 * 
 */
public class PtGBuilder {

	private static Logger log = LoggerFactory.getLogger(PtGBuilder.class);

	public static PtG build(FileTransferInputData inputData)
		throws BuilderException {

		TSURL toSURL = inputData.getSurl();
		TLifeTimeInSeconds pinLifetime = inputData.getDesiredPinLifetime();
		TURLPrefix transferProtocols = inputData.getTransferProtocols();
		TDirOption dirOption = TDirOption.makeNotDirectory();
		TSizeInBytes fileSize = TSizeInBytes.makeEmpty();
		TReturnStatus status;
		try {
			status = new TReturnStatus(TStatusCode.SRM_REQUEST_INPROGRESS,
				"Synchronous request created");
		} catch (InvalidTReturnStatusAttributeException e) {
			log
				.error("Unable to build TReturnStatus. InvalidTReturnStatusAttributeException: "
					+ e.getMessage());
			throw new BuilderException(
				"Error building PtG TReturnStatus. Building failed");
		}
		;
		TTURL transferURL = TTURL.makeEmpty();
		PtGData data;
		try {
			if (inputData instanceof IdentityInputData) {
				data = new IdentityPtGData(((IdentityInputData) inputData).getUser(),
					toSURL, pinLifetime, dirOption, transferProtocols, fileSize, status,
					transferURL);
			} else {
				data = new AnonymousPtGData(toSURL, pinLifetime, dirOption,
					transferProtocols, fileSize, status, transferURL);
			}
			data.store();
		} catch (InvalidPtGDataAttributesException e) {
			log
				.error("Unable to build PtGChunkData. InvalidPtGChunkDataAttributesException: "
					+ e.getMessage());
			throw new BuilderException(
				"Error building PtG PtGChunkData. Building failed");
		} catch (InvalidFileTransferDataAttributesException e) {
			log
				.error("Unable to build PtGChunkData. InvalidFileTransferChunkDataAttributesException: "
					+ e.getMessage());
			throw new BuilderException(
				"Error building PtG PtGChunkData. Building failed");
		} catch (InvalidSurlRequestDataAttributesException e) {
			log
				.error("Unable to build PtGChunkData. InvalidSurlRequestDataAttributesException: "
					+ e.getMessage());
			throw new BuilderException(
				"Error building PtG PtGChunkData. Building failed");
		}
		return new PtG(data);
	}
}
