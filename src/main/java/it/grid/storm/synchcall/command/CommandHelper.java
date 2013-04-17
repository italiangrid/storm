package it.grid.storm.synchcall.command;

import java.util.List;
import org.slf4j.Logger;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLReturnStatusAttributeException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.data.DataHelper;
import it.grid.storm.synchcall.data.InputData;

public class CommandHelper {

	public static TReturnStatus buildStatus(TStatusCode statusCode,
		String explaination) throws IllegalArgumentException, IllegalStateException {

		if (statusCode == null) {
			throw new IllegalArgumentException(
				"Unable to build the status, null arguments: statusCode=" + statusCode);
		}
		try {
			return new TReturnStatus(statusCode, explaination);
		} catch (InvalidTReturnStatusAttributeException e) {
			// Never thrown
			throw new IllegalStateException(
				"Unexpected InvalidTReturnStatusAttributeException "
					+ "in building TReturnStatus: " + e.getMessage());
		}
	}

	public static TSURLReturnStatus buildStatus(TSURL surl,
		TReturnStatus returnStatus) throws IllegalArgumentException,
		IllegalStateException {

		if (surl == null || returnStatus == null) {
			throw new IllegalArgumentException(
				"Unable to build the status, null arguments: surl=" + surl
					+ " returnStatus=" + returnStatus);
		}
		try {
			return new TSURLReturnStatus(surl, returnStatus);
		} catch (InvalidTSURLReturnStatusAttributeException e) {
			// Never thrown
			throw new IllegalStateException(
				"Unexpected InvalidTSURLReturnStatusAttributeException "
					+ "in building TSURLReturnStatus: " + e.getMessage());
		}
	}

	public static void printRequestOutcome(String srmCommand, Logger log,
		TReturnStatus status) {

		if (status.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
			log.info(srmCommand + ": Request successfully done with: [status: "
				+ status + "]");
		} else {
			if (status.getStatusCode().equals(TStatusCode.SRM_PARTIAL_SUCCESS)) {
				log.info(srmCommand + ": Request partially done with [status: "
					+ status + "]");
			} else {
				log.info(srmCommand + ": Request failed with [status: " + status + "]");
			}
		}
	}

	public static void printRequestOutcome(String srmCommand, Logger log,
		TReturnStatus status, InputData inputData) {

		if (status.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
			log.info(srmCommand + ": user <" + DataHelper.getRequestor(inputData)
				+ "> Request successfully done with: [status: " + status + "]");
		} else {
			if (status.getStatusCode().equals(TStatusCode.SRM_PARTIAL_SUCCESS)) {
				log.info(srmCommand + ": <" + DataHelper.getRequestor(inputData)
					+ "> Request partially done with [status: " + status + "]");
			} else {
				log.info(srmCommand + ": <" + DataHelper.getRequestor(inputData)
					+ "> Request failed with [status: " + status + "]");
			}
		}
	}

	public static void printRequestOutcome(String srmCommand, Logger log,
		TReturnStatus status, InputData inputData, List<String> surls) {

		if (status.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
			log.info(srmCommand + ": user <" + DataHelper.getRequestor(inputData)
				+ "> Request for  [SURL: " + surls
				+ "] successfully done with: [status: " + status + "]");
		} else {
			if (status.getStatusCode().equals(TStatusCode.SRM_PARTIAL_SUCCESS)) {
				log.info(srmCommand + ": <" + DataHelper.getRequestor(inputData)
					+ "> Request for [SURL: " + surls + "] partially done with [status: "
					+ status + "]");
			} else {
				log.info(srmCommand + ": <" + DataHelper.getRequestor(inputData)
					+ "> Request for [SURL: " + surls + "] failed with [status: "
					+ status + "]");
			}
		}
	}

	public static void printRequestOutcome(String srmCommand, Logger log,
		TReturnStatus status, InputData inputData, TRequestToken token) {

		if (status.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
			log.info(srmCommand + ": user <" + DataHelper.getRequestor(inputData)
				+ "> Request for [token:" + token
				+ "] successfully done with: [status: " + status + "]");
		} else {
			if (status.getStatusCode().equals(TStatusCode.SRM_PARTIAL_SUCCESS)) {
				log.info(srmCommand + ": <" + DataHelper.getRequestor(inputData)
					+ "> Request for [token:" + token + "] partially done with [status: "
					+ status + "]");
			} else {
				log.info(srmCommand + ": <" + DataHelper.getRequestor(inputData)
					+ "> Request for [token:" + token + "] failed with [status: "
					+ status + "]");
			}
		}
	}

	public static void printRequestOutcome(String srmCommand, Logger log,
		TReturnStatus status, InputData inputData, TRequestToken token,
		List<String> surls) {

		if (status.getStatusCode().equals(TStatusCode.SRM_SUCCESS)
			|| status.getStatusCode().equals(TStatusCode.SRM_SPACE_AVAILABLE)
			|| status.getStatusCode().equals(TStatusCode.SRM_FILE_PINNED)) {
			log.info(srmCommand + ": user <" + DataHelper.getRequestor(inputData)
				+ "> Request for [token:" + token + "] for [SURL: " + surls
				+ "] successfully done with: [status: " + status + "]");
		} else {
			if (status.getStatusCode().equals(TStatusCode.SRM_PARTIAL_SUCCESS)) {
				log.info(srmCommand + ": <" + DataHelper.getRequestor(inputData)
					+ "> Request for [token:" + token + "] for [SURL: " + surls
					+ "] partially done with [status: " + status + "]");
			} else {
				log.info(srmCommand + ": <" + DataHelper.getRequestor(inputData)
					+ "> Request for [token:" + token + "] for [SURL: " + surls
					+ "] failed with [status: " + status + "]");
			}
		}
	}

	public static void printSurlOutcome(String srmCommand, Logger log,
		TReturnStatus status, InputData inputData, TSURL surl) {

		// TODO add all the successfull status for any request
		if (status.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
			log.info(srmCommand + ": user <" + DataHelper.getRequestor(inputData)
				+ "> operation on [SURL: " + surl
				+ "] successfully done with: [status: " + status + "]");
		} else {
			log.info(srmCommand + ": <" + DataHelper.getRequestor(inputData)
				+ "> operation on [SURL: " + surl + "] failed with [status: " + status
				+ "]");
		}
	}

}
