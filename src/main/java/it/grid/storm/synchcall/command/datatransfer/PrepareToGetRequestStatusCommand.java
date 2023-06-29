/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;

public class PrepareToGetRequestStatusCommand extends
	FileTransferRequestStatusCommand implements Command {

	private static final String SRM_COMMAND = "srmStatusOfGetRequest";

	public PrepareToGetRequestStatusCommand() {

	}

	@Override
	protected TReturnStatus computeRequestStatus(
		ArrayOfTSURLReturnStatus arrayOfFileStatuses) {

		boolean atLeastOneSuccessOrPinned = false;
		boolean atLeastOneQueued = false;
		boolean atLeastOneInProgress = false;
		boolean atLeastOneAborted = false;
		boolean atLeastOneFailed = false;
		for (TSURLReturnStatus surlStatus : arrayOfFileStatuses.getArray()) {
			switch (surlStatus.getStatus().getStatusCode()) {
			case SRM_SUCCESS:
				atLeastOneSuccessOrPinned = true;
				break;
			case SRM_FILE_PINNED:
				atLeastOneSuccessOrPinned = true;
				break;
			case SRM_REQUEST_QUEUED:
				atLeastOneQueued = true;
				break;
			case SRM_REQUEST_INPROGRESS:
				atLeastOneInProgress = true;
			case SRM_RELEASED:
				break;
			case SRM_ABORTED:
				atLeastOneAborted = true;
				break;
			default:
				atLeastOneFailed = true;
				break;
			}
		}
		if (atLeastOneSuccessOrPinned
			&& !(atLeastOneQueued || atLeastOneInProgress || atLeastOneAborted || atLeastOneFailed)) {
			return CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS,
				"Request executed successfully");
		}
		if (((atLeastOneSuccessOrPinned || atLeastOneAborted || atLeastOneFailed) && atLeastOneQueued)
			|| atLeastOneInProgress) {
			return CommandHelper.buildStatus(TStatusCode.SRM_REQUEST_INPROGRESS,
				"Request in progress");
		}
		if (atLeastOneSuccessOrPinned && atLeastOneFailed
			&& !(atLeastOneQueued || atLeastOneInProgress)) {
			return CommandHelper.buildStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
				"Partial success, some surls are failed");
		}
		if (atLeastOneAborted
			&& !(atLeastOneQueued || atLeastOneInProgress
				|| atLeastOneSuccessOrPinned || atLeastOneFailed)) {
			return CommandHelper.buildStatus(TStatusCode.SRM_ABORTED,
				"Request aborted");
		}
		return CommandHelper.buildStatus(TStatusCode.SRM_FAILURE, "Request failed");
	}

	@Override
	protected String getSrmCommand() {

		return SRM_COMMAND;
	}

	@Override
	protected TRequestType getRequestType() {

		return TRequestType.PREPARE_TO_GET;
	};

}
