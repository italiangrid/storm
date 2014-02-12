package it.grid.storm.synchcall.command;

import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLReturnStatusAttributeException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.data.DataHelper;
import it.grid.storm.synchcall.data.InputData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

public class CommandHelper {
  
  public static final Map<TStatusCode, String> STATUS_MESSAGES;
  private static final String FAILED_STRING = "failed";

  static{
    
    STATUS_MESSAGES = new HashMap<TStatusCode, String>();
    STATUS_MESSAGES.put(TStatusCode.SRM_SUCCESS, "succesfully done");
    STATUS_MESSAGES.put(TStatusCode.SRM_SPACE_AVAILABLE, "succesfully done");
    STATUS_MESSAGES.put(TStatusCode.SRM_FILE_PINNED, "succesfully done");
    STATUS_MESSAGES.put(TStatusCode.SRM_PARTIAL_SUCCESS, "partially succeded");
   
  }

  private static String getStatusMessage(TStatusCode statusCode){
  
    String result = STATUS_MESSAGES.get(statusCode);
    if (result == null)
      return FAILED_STRING;

    return result;
  }
  
	public static TReturnStatus buildStatus(TStatusCode statusCode,
		String explaination) throws IllegalArgumentException, IllegalStateException {

		if (statusCode == null) {
			throw new IllegalArgumentException(
				"Unable to build the status, null arguments: statusCode=" + statusCode);
		}
		try {
			return new TReturnStatus(statusCode, explaination);
		} catch (InvalidTReturnStatusAttributeException e) {
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
			throw new IllegalStateException(
				"Unexpected InvalidTSURLReturnStatusAttributeException "
					+ "in building TSURLReturnStatus: " + e.getMessage());
		}
	}

	public static void printRequestOutcome(String srmCommand, Logger log,
		TReturnStatus status) {
	  
	  log.info("{}: Request {} with: [status: {}]",
	    srmCommand, 
	    getStatusMessage(status.getStatusCode()),
	    status);
	}

	public static void printRequestOutcome(String srmCommand, Logger log,
		TReturnStatus status, InputData inputData) {

	  log.info("{}: user <{}> Request {} with: [status: {}]",
	    srmCommand,
	    DataHelper.getRequestor(inputData),
	    getStatusMessage(status.getStatusCode()),
	    status);
	  
	}

	public static void printRequestOutcome(String srmCommand, Logger log,
		TReturnStatus status, InputData inputData, List<String> surls) {

	  log.info("{}: user <{}> Request for [SURL: {}] {} with: [status: {}]",
	    srmCommand,
	    DataHelper.getRequestor(inputData),
	    surls,
	    getStatusMessage(status.getStatusCode()),
	    status);
	}

	public static void printRequestOutcome(String srmCommand, Logger log,
		TReturnStatus status, InputData inputData, TRequestToken token, ArrayOfSURLs surls) {

	  log.info("{}: user <{}> Request for [token: {}] for [SURL: {}] {} with: [status: {}]",
	    srmCommand,
	    DataHelper.getRequestor(inputData),
	    token,
	    surls,
	    getStatusMessage(status.getStatusCode()),
	    status);
	}

	public static void printRequestOutcome(String srmCommand, Logger log,
		TReturnStatus status, InputData inputData, TRequestToken token) {
	  
	  log.info("{}: user <{}> Request for [token: {}] {} with: [status: {}]",
	    srmCommand,
	    DataHelper.getRequestor(inputData),
	    token,
	    getStatusMessage(status.getStatusCode()),
	    status);

	}

	public static void printRequestOutcome(String srmCommand, Logger log,
		TReturnStatus status, InputData inputData, TRequestToken token,
		List<String> surls) {
	  
	  log.info("{}: user<{}> Request for [token: {}] for [SURL: {}] {} with "
	    +" [status: {}]",
	    srmCommand,
	    DataHelper.getRequestor(inputData),
	    token,
	    surls,
	    getStatusMessage(status.getStatusCode()),
	    status);
	}

	public static void printSurlOutcome(String srmCommand, Logger log,
		TReturnStatus status, InputData inputData, TSURL surl) {

	  log.info("{}: user <{}> operation on [SURL: {}] {} with: [status: {}]",
	    srmCommand,
	    DataHelper.getRequestor(inputData),
	    surl,
	    getStatusMessage(status.getStatusCode()),
	    status);
	  
	}
	
	public static void printSurlOutcome(String srmCommand, 
	  Logger log,
		TReturnStatus status, 
		InputData inputData, 
		TRequestToken token, 
		TSURL surl) {

	  log.info("{}: user <{}> operation for token [token:{}] on [SURL: {}] {} with: [status: {}]",
	    srmCommand,
	    DataHelper.getRequestor(inputData),
	    token,
	    surl,
	    getStatusMessage(status.getStatusCode()),
	    status);
	}
}