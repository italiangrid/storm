/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.xmlrpc.converter.datatransfer;

import java.util.Hashtable;
import java.util.Map;
import org.slf4j.Logger;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferOutputData;
import it.grid.storm.xmlrpc.converter.Converter;

/**
 * @author Michele Dibenedetto
 * 
 */
public abstract class ManageFileTransferConverter implements Converter {

	public ManageFileTransferConverter() {

		super();
	}

	public Map<String, Object> convertFromOutputData(OutputData data) {

		getLogger().debug(
			"Started ReleaseFilesConverter - Creation of XMLRPC Output Structure!");

		Hashtable<String, Object> outputParam = new Hashtable<String, Object>();
		ManageFileTransferOutputData outputData = (ManageFileTransferOutputData) data;
		/* (1) returnStatus */
		TReturnStatus returnStatus = outputData.getReturnStatus();
		if (returnStatus != null) {
			returnStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);
		}

		/* (2) arrayOfFileStatuses */
		ArrayOfTSURLReturnStatus arrayOfFileStatuses = outputData
			.getArrayOfFileStatuses();
		if (arrayOfFileStatuses != null) {
			arrayOfFileStatuses.encode(outputParam,
				ArrayOfTSURLReturnStatus.PNAME_ARRAYOFFILESTATUSES);
		}

		getLogger().debug(
			"ReleaseFilesConverter - Sending: " + outputParam.toString());

		return outputParam;
	}

	protected abstract Logger getLogger();

}
