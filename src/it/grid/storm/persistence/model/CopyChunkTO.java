/*
 * CopyChunkData
 *
 * Copyright (c) 2005, Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, distribute and modify this file under the terms of
 * the INFN GRID licence.
 *
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
