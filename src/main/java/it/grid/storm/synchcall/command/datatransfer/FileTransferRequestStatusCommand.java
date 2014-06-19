package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.catalogs.surl.SURLStatusManager;
import it.grid.storm.catalogs.surl.SURLStatusManagerFactory;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.command.DataTransferCommand;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferOutputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferRequestFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferRequestInputData;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileTransferRequestStatusCommand extends
  DataTransferCommand implements Command {

  protected abstract String getSrmCommand();

  protected abstract TRequestType getRequestType();

  private static final Logger log = LoggerFactory
    .getLogger(FileTransferRequestStatusCommand.class);

  public FileTransferRequestStatusCommand() {

  };

  public TRequestToken getTokenFromInputData(InputData inputData) {

    return ((ManageFileTransferRequestInputData) inputData).getRequestToken();
  }

  public boolean inputDataHasSURLArray(InputData inputData) {

    return (inputData instanceof ManageFileTransferRequestFilesInputData)
      || (inputData instanceof ManageFileTransferFilesInputData);
  }

  public boolean validInputData(InputData inputData) {

    return (inputData instanceof ManageFileTransferRequestFilesInputData)
      || (inputData instanceof ManageFileTransferFilesInputData)
      || (inputData instanceof ManageFileTransferRequestInputData);
  }
  
  public List<TSURL> getSURLListFromInputData(InputData inputData) {

    if (inputDataHasSURLArray(inputData)) {
      return ((ManageFileTransferFilesInputData) inputData).getArrayOfSURLs()
        .getArrayList();
    }
    return null;
  }
  
  protected ManageFileTransferOutputData handleExpiredRequestToken(InputData id){
    ManageFileTransferOutputData outputData = new ManageFileTransferOutputData(
      CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST,
        "Expired request token"));
    
    SurlStatusCommandHelper.printRequestOutcome(outputData.getReturnStatus(),
      id, getSrmCommand());
    return outputData;
  }
  
  protected ManageFileTransferOutputData handleInvalidRequestToken(InputData id){
    
    ManageFileTransferOutputData outputData = new ManageFileTransferOutputData(
      CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST,
        "Invalid request token"));
    
    SurlStatusCommandHelper.printRequestOutcome(outputData.getReturnStatus(),
      id, getSrmCommand());
    return outputData;
  }

  protected boolean tokenNotFound(InputData inputData){
    TRequestToken token = getTokenFromInputData(inputData);
    
    return (RequestSummaryCatalog.getInstance().typeOf(token).isEmpty());
  }
  
  protected boolean tokenHasExpired(InputData inputData){
    TRequestToken token = getTokenFromInputData(inputData);
    
    return(token.hasExpirationDate() && token.isExpired());
  }
  
  protected ManageFileTransferOutputData handleInternalError(InputData id, 
    Throwable t){
    log.warn(t.getMessage(),t);
    
    ManageFileTransferOutputData outputData = new ManageFileTransferOutputData(
      CommandHelper.buildStatus(TStatusCode.SRM_FAILURE,
        "Internal error: "+t.getMessage()));
    
    
      
    SurlStatusCommandHelper.printRequestOutcome(outputData.getReturnStatus(),
      id, getSrmCommand());
    
    return outputData;
  }
  
  @Override
  public ManageFileTransferOutputData execute(InputData inputData)
    throws IllegalArgumentException, CommandException {

    log.debug(getSrmCommand() + "Started.");
    
    if (!validInputData(inputData)){
      throw new IllegalArgumentException(
        "Unable to execute the task. Wrong input argument type: "
          + inputData.getClass());
    }
    
    Map<TSURL, TReturnStatus> surlStatuses = null;
    SURLStatusManager checker = SURLStatusManagerFactory
      .newSURLStatusManager();
    
    if (tokenNotFound(inputData)){
      return handleInvalidRequestToken(inputData);
    }
    
    if (tokenHasExpired(inputData)){
      return handleExpiredRequestToken(inputData);
    }
    
    try {
      
      surlStatuses = checker.getSURLStatuses(getTokenFromInputData(inputData), 
        getSURLListFromInputData(inputData));
      
    } catch (IllegalArgumentException e) {  
      return handleInternalError(inputData, e);
    } 
    
    
    if (surlStatuses.isEmpty()) {
      // Case 1: no candidate SURLs in the DB. SRM_INVALID_REQUEST or
      // SRM_FAILURE are returned.
      log.info("No SURLs found in the DB. Request failed");
      TReturnStatus returnStatus;
      if (inputData instanceof ManageFileTransferRequestFilesInputData) {
        returnStatus = CommandHelper.buildStatus(
          TStatusCode.SRM_INVALID_REQUEST,
          "Invalid request token, no match with provided surls");
      } else {
        if (inputData instanceof ManageFileTransferRequestInputData) {
          returnStatus = CommandHelper.buildStatus(
            TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");
        } else {
          if (inputData instanceof ManageFileTransferFilesInputData) {
            returnStatus = CommandHelper.buildStatus(
              TStatusCode.SRM_INVALID_REQUEST,
              "None of the specified SURLs was found");
          } else {
            throw new IllegalStateException("Unexpected InputData received: "
              + inputData.getClass());
          }
        }
      }
      
      SurlStatusCommandHelper.printRequestOutcome(returnStatus, inputData,
        getSrmCommand());
      
      return new ManageFileTransferOutputData(returnStatus);
    }
    
    ArrayOfTSURLReturnStatus surlReturnStatuses = 
      encodeSURLReturnStatuses(surlStatuses, 
        getSURLListFromInputData(inputData));
    
    TReturnStatus requestStatus;
    if (inputData instanceof ManageFileTransferRequestInputData) {
      RequestSummaryData data = RequestSummaryCatalog.getInstance().find(
        ((ManageFileTransferRequestInputData) inputData).getRequestToken());
      if (data != null) {
        requestStatus = data.getStatus();
      } else {
        requestStatus = computeRequestStatus(surlReturnStatuses);
      }
    } else {
      requestStatus = computeRequestStatus(surlReturnStatuses);
    }
    SurlStatusCommandHelper.printRequestOutcome(requestStatus, inputData,
      getSrmCommand());
    return new ManageFileTransferOutputData(requestStatus, surlReturnStatuses);
  }

  protected abstract TReturnStatus computeRequestStatus(
    ArrayOfTSURLReturnStatus arrayOfFileStatuses);
  
  
  protected ArrayOfTSURLReturnStatus encodeSURLReturnStatuses(
    Map<TSURL,TReturnStatus> statuses){
    
    ArrayOfTSURLReturnStatus retStatuses = 
      new ArrayOfTSURLReturnStatus(statuses.size());
    
    for (Entry<TSURL, TReturnStatus> rs: statuses.entrySet()){
      retStatuses.addTSurlReturnStatus(CommandHelper.buildStatus(rs.getKey(), 
        rs.getValue()));
    }
    
    return retStatuses;
    
  }
  
  protected ArrayOfTSURLReturnStatus encodeSURLReturnStatuses(
    Map<TSURL,TReturnStatus> statuses, List<TSURL> surls){
    
    if (surls == null || surls.isEmpty())
      return encodeSURLReturnStatuses(statuses);
    
    ArrayOfTSURLReturnStatus retStatuses = 
      new ArrayOfTSURLReturnStatus();
           
    for (TSURL s: surls){
      TReturnStatus status = statuses.get(s);
      
      if (status == null){
        status = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH, 
          "Invalid SURL path.");
      }
      
      retStatuses.addTSurlReturnStatus(CommandHelper.buildStatus(s, status));
    }
    
    return retStatuses;
  }

}
