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

// import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.common.types.TransferProtocol;
import it.grid.storm.griduser.GridUserInterface;

/**
 * Interface for the SRM Client functionality needed in StoRM.
 * 
 * @author EGRID - ICTP Trieste
 * @version 2.0
 * @date September, 2005
 */
public interface SRMClient {

	/**
	 * Method used to execute an srmPrepareToPut: it will connect to the
	 * webservice derived from the toSURL using the credentials available in
	 * GridUser. It requires the lifetime, the fileStorageType, the spaceToken,
	 * the fileSize, the protocol, a description, the overwriteOption, the
	 * retryTime. The method returns an SRMPrepareToPutReply containing the result
	 * of the operation, and throws an SRMClient exception in case the operation
	 * could not be completed.
	 */
	public SRMPrepareToPutReply prepareToPut(GridUserInterface su, TSURL toSURL,
		TLifeTimeInSeconds lifetime, TFileStorageType fileStorageType,
		TSpaceToken spaceToken, TSizeInBytes filesize, TransferProtocol protocol,
		String description, TOverwriteMode overwriteOption,
		TLifeTimeInSeconds retryTime) throws SRMClientException;

	/**
	 * Method used to execute an srmStatusOfPutRequest: it requires the request
	 * token returned durign an srmPrepareToPut operation, the GridUser that
	 * issued the command, and the toSURL of the request. An
	 * SRMStatusOfPutRequestReply is returned, but in case the operation fails an
	 * SRMClientException is thrown containing an explanation String.
	 */
	public SRMStatusOfPutRequestReply statusOfPutRequest(TRequestToken rt,
		GridUserInterface gu, TSURL toSURL) throws SRMClientException;

	/**
	 * Method used to execute an srmPutDone: it requires the request token, the
	 * GridUser that issued the command, and the toSURL that must be putDone.
	 * SRMPutDoneReply is returned but in case the operation fails an
	 * SRMClientException is thrown containing an explanation String.
	 */
	public SRMPutDoneReply srmPutDone(TRequestToken rt, GridUserInterface gu,
		TSURL toSURL) throws SRMClientException;
}
