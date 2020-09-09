package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;

public class PrepareToPutRequestStatusCommand extends
	FileTransferRequestStatusCommand implements Command {

	private static final String SRM_COMMAND = "srmStatusOfPutRequest";

	public PrepareToPutRequestStatusCommand() {

	}

	@Override
	protected String getSrmCommand() {

		return SRM_COMMAND;
	}

	@Override
	protected TRequestType getRequestType() {

		return TRequestType.PREPARE_TO_PUT;
	};

	@Override
	protected TReturnStatus computeRequestStatus(
		ArrayOfTSURLReturnStatus arrayOfFileStatuses) {

		boolean atLeastOneSuccessOrSpaceAvailable = false;
		boolean atLeastOneQueued = false;
		boolean atLeastOneInProgress = false;
		boolean atLeastOneAborted = false;
		boolean atLeastOneFailed = false;
		for (TSURLReturnStatus surlStatus : arrayOfFileStatuses.getArray()) {
			switch (surlStatus.getStatus().getStatusCode()) {
			case SRM_SUCCESS:
				atLeastOneSuccessOrSpaceAvailable = true;
				break;
			case SRM_SPACE_AVAILABLE:
				atLeastOneSuccessOrSpaceAvailable = true;
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
		if (atLeastOneSuccessOrSpaceAvailable
			&& !(atLeastOneQueued || atLeastOneInProgress || atLeastOneAborted || atLeastOneFailed)) {
			return CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS,
				"Request executed successfully");
		}
		if (((atLeastOneSuccessOrSpaceAvailable || atLeastOneAborted || atLeastOneFailed) && atLeastOneQueued)
			|| atLeastOneInProgress) {
			return CommandHelper.buildStatus(TStatusCode.SRM_REQUEST_INPROGRESS,
				"Request in progress");
		}
		if (atLeastOneSuccessOrSpaceAvailable && atLeastOneFailed
			&& !(atLeastOneQueued || atLeastOneInProgress)) {
			return CommandHelper.buildStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
				"Partial success, some surls are failed");
		}
		if (atLeastOneAborted
			&& !(atLeastOneQueued || atLeastOneInProgress
				|| atLeastOneSuccessOrSpaceAvailable || atLeastOneFailed)) {
			return CommandHelper.buildStatus(TStatusCode.SRM_ABORTED,
				"Request aborted");
		}
		return CommandHelper.buildStatus(TStatusCode.SRM_FAILURE, "Request failed");
	}

}
