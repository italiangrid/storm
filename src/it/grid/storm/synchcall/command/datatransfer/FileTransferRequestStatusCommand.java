package it.grid.storm.synchcall.command.datatransfer;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.command.DataTransferCommand;
import it.grid.storm.synchcall.command.SurlStatusCommandHelper;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferOutputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferRequestFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferRequestInputData;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.UnknownTokenException;

public abstract class FileTransferRequestStatusCommand extends DataTransferCommand implements Command
{
    protected abstract String getSrmCommand();
    protected abstract TRequestType getRequestType();
    
    private static final Logger log = LoggerFactory.getLogger(FileTransferRequestStatusCommand.class);
    
    public FileTransferRequestStatusCommand() {
    };

    @Override
    public OutputData execute(InputData inputData) throws IllegalArgumentException, CommandException
    {
        TReturnStatus globalStatus = null;
        log.debug(getSrmCommand() + "Started.");
        if (!(inputData instanceof ManageFileTransferRequestFilesInputData
                || inputData instanceof ManageFileTransferFilesInputData || inputData instanceof ManageFileTransferRequestInputData))
        {
            throw new IllegalArgumentException("Unable to execute the task. Wrong input argument type: "
                    + inputData.getClass());
        }
        Map<TSURL, TReturnStatus> surlStastuses;
        try
        {
            surlStastuses = SurlStatusCommandHelper.getSurlsStatus(inputData, getRequestType());
        } catch(IllegalArgumentException e)
        {
            log.warn("Unexpected IllegalArgumentException in getSurlsStatus: " + e);
            ManageFileTransferOutputData outputData = new ManageFileTransferOutputData(CommandHelper.buildStatus(TStatusCode.SRM_FAILURE, 
            "Internal error. Unablr to determine current SURL status"));
            SurlStatusCommandHelper.printRequestOutcome(outputData.getReturnStatus(), inputData, getSrmCommand());
            return outputData;
        } catch(RequestUnknownException e)
        {
            log.info("No surls status available. RequestUnknownException: " + e.getMessage());
            ManageFileTransferOutputData outputData = new ManageFileTransferOutputData(CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token and surls"));
            SurlStatusCommandHelper.printRequestOutcome(outputData.getReturnStatus(), inputData, getSrmCommand());
            return outputData;
        } catch(UnknownTokenException e)
        {
            log.info("No surls status available. UnknownTokenException: " + e.getMessage());
            ManageFileTransferOutputData outputData = new ManageFileTransferOutputData(CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token"));
            SurlStatusCommandHelper.printRequestOutcome(outputData.getReturnStatus(), inputData, getSrmCommand());
            return outputData;
        } catch(ExpiredTokenException e)
        {
            log.info("The request is expired: ExpiredTokenException: " + e.getMessage());
            ManageFileTransferOutputData outputData = new ManageFileTransferOutputData(CommandHelper.buildStatus(TStatusCode.SRM_REQUEST_TIMED_OUT, "Request expired"));
            SurlStatusCommandHelper.printRequestOutcome(outputData.getReturnStatus(), inputData, getSrmCommand());
            return outputData;
        }
        if (surlStastuses.isEmpty())
        {
            // Case 1: no candidate SURLs in the DB. SRM_INVALID_REQUEST or SRM_FAILURE are returned.
            log.info("No SURLs found in the DB. Request failed");
            TReturnStatus returnStatus;
            if(inputData instanceof ManageFileTransferRequestFilesInputData)
            {
                returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token, no match with provided surls");
            }
            else
            {
                if(inputData instanceof ManageFileTransferRequestInputData)
                {
                    returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");
                }
                else
                {
                    if(inputData instanceof ManageFileTransferFilesInputData)
                    {
                        returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST,
                        "None of the specified SURLs was found");                        
                    }
                    else
                    {
                        throw new IllegalStateException("Unexpected InputData received: " + inputData.getClass());
                    }
                }
            }
            SurlStatusCommandHelper.printRequestOutcome(returnStatus, inputData, getSrmCommand());
            return new ManageFileTransferOutputData(returnStatus);
        }
        ArrayOfTSURLReturnStatus surlReturnStatuses;
        if(inputData instanceof ManageFileTransferFilesInputData)
        {
            surlReturnStatuses = SurlStatusCommandHelper.prepareSurlsReturnStatus(surlStastuses,
                                                          ((ManageFileTransferFilesInputData)inputData).getArrayOfSURLs());
            
        }
        else
        {
            surlReturnStatuses = SurlStatusCommandHelper.prepareSurlsReturnStatus(surlStastuses);            
        }
        SurlStatusCommandHelper.printRequestOutcome(globalStatus, inputData, getSrmCommand());
        return new ManageFileTransferOutputData(globalStatus, surlReturnStatuses);
    }

}
