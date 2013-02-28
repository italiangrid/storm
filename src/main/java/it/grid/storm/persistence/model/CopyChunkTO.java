/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.persistence.model;


import it.grid.storm.srm.types.*;
import it.grid.storm.namespace.*;
import java.util.Date;


/**
 * holds all info necessary to complete a srmCopy of a single file,
 * and to report advancement status of this part of the request.
 * Data supplied to the constructor should already be available
 * in the user request (front-end).
 */


public class CopyChunkTO {
	// FIXME: lacks constructor!!

  /**
	public TRequestId getRequestId();
	public StoRI getFromSURL();
	public StoRI getToSURL();
	public String getStorageSystemInfo();
	public TDirOption getDirOption();
	public TOverwriteMode getOverwriteMode();
	public TLifetimeInSeconds getRequestedLifetime();
	public TFileStorageType getFileStorageType();
	public TSpaceID getSpaceId();
	public String[] getClientAcceptedTransportProtocols();

	public int getEstimatedWaitTimeInQueue();
	public int getEstimatedProgressingTime();
	public int setEstimatedWaitTimeInQueue(final Date time);
	public int setEstimatedProgressingTime(final Date time);

	public TSizeInBytes getFileSize();
	public TSizeInBytes setFileSize(final size_t size);

	public TReturnStatus getStatus();
	public int setStatus(final TStatusCode code, final String explanation);

	public int getProgressCounter();

	public void advanceProgressCounter();

	public String getTURL();
	public String setTURL(final String TURL);

	public TLifetimeInSeconds getRemainingLifetime();
**/
}
