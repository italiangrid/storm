/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.health;

import it.grid.storm.scheduler.ChunkType;

public class OperationType {

	private int operationIndex = -1;
	private String operationName;
	private String operationDescription;
	private OperationTypeCategory opTypeCategory;

	public final static OperationType UNDEF = new OperationType(0, "UNDEF",
		"Undefined", OperationTypeCategory.UNKNOWN);

	public final static OperationType PTG = new OperationType(1, "PTG",
		"srmPrepareToGet", OperationTypeCategory.ASYNCH);
	public final static OperationType SPTG = new OperationType(20, "SPTG",
		"srmPrepareToGetStatus", OperationTypeCategory.SYNCH_DB);
	public final static OperationType PTP = new OperationType(2, "PTP",
		"srmPrepareToPut", OperationTypeCategory.ASYNCH);
	public final static OperationType SPTP = new OperationType(21, "SPTP",
		"srmPrepareToPutStatus", OperationTypeCategory.SYNCH_DB);
	public final static OperationType COPY = new OperationType(3, "COPY",
		"srmCopy", OperationTypeCategory.ASYNCH);
	public final static OperationType BOL = new OperationType(4, "BOL",
		"srmBringOnLine", OperationTypeCategory.ASYNCH);

	public final static OperationType PNG = new OperationType(13, "PNG",
		"srmPing", OperationTypeCategory.PURESYNCH);

	public final static OperationType MKD = new OperationType(11, "MKD",
		"srmMkdir", OperationTypeCategory.SYNCH_FS);
	public final static OperationType MV = new OperationType(12, "MV", "srmMv",
		OperationTypeCategory.SYNCH_FS);
	public final static OperationType RM = new OperationType(18, "RM", "srmRm",
		OperationTypeCategory.SYNCH_FS);
	public final static OperationType RMD = new OperationType(19, "RMD",
		"srmRmdir", OperationTypeCategory.SYNCH_FS);

	public final static OperationType EFL = new OperationType(7, "ELT",
		"srmExtendLifeTime", OperationTypeCategory.SYNCH_DB);
	public final static OperationType GST = new OperationType(9, "GST",
		"srmGetSpaceTokens", OperationTypeCategory.SYNCH_DB);
	public final static OperationType RSP = new OperationType(16, "RSP",
		"srmReleaseSpace", OperationTypeCategory.SYNCH_DB);

	public final static OperationType AF = new OperationType(5, "AF",
		"srmAbortFile", OperationTypeCategory.SYNCH_FS_DB);
	public final static OperationType AR = new OperationType(6, "AR",
		"srmAbortRequest", OperationTypeCategory.SYNCH_FS_DB);
	public final static OperationType GSM = new OperationType(8, "GSM",
		"srmGetSpaceMetaData", OperationTypeCategory.SYNCH_FS_DB);
	public final static OperationType LS = new OperationType(10, "LS", "srmLs",
		OperationTypeCategory.SYNCH_FS_DB);
	public final static OperationType PD = new OperationType(14, "PD",
		"srmPutDone", OperationTypeCategory.SYNCH_FS_DB);
	public final static OperationType RF = new OperationType(15, "RF",
		"srmReleaseFile", OperationTypeCategory.SYNCH_FS_DB);
	public final static OperationType RS = new OperationType(17, "RS",
		"srmReserveSpace", OperationTypeCategory.SYNCH_FS_DB);

	public OperationType(int operationIndex, String operationName,
		String operationDescription, OperationTypeCategory opCat) {

		this.operationIndex = operationIndex;
		this.operationName = operationName;
		this.operationDescription = operationDescription;
		this.opTypeCategory = opCat;
	}

	public static OperationType makeFromChunkType(ChunkType chunkType) {

		OperationType result = OperationType.UNDEF;
		switch (chunkType.getIndex()) {
		case 1:
			result = OperationType.PTG;
			break;
		case 2:
			result = OperationType.PTP;
			break;
		case 3:
			result = OperationType.COPY;
			break;
		case 4:
			result = OperationType.BOL;
			break;
		default:
			result = OperationType.UNDEF;
			break;
		}
		return result;
	}

	public boolean isSynchronousOperation() {

		boolean result = false;
		if (this.operationIndex > 4) {
			result = true;
		}
		return result;
	}

	public String getOperationDescription() {

		return this.operationDescription;
	}

	public OperationTypeCategory getOperationTypeCategory() {

		return this.opTypeCategory;
	}

	@Override
	public String toString() {

		return this.operationName;
	}

	@Override
	public int hashCode() {

		return this.operationIndex;
	}

	@Override
	public boolean equals(Object obj) {

		boolean result = false;
		if (obj instanceof OperationType) {
			OperationType other = (OperationType) obj;
			if (other.operationIndex == this.operationIndex) {
				result = true;
			}
		}
		return result;
	}
}