/**
 * @author  Alberto Forti
 * @author  CNAF - INFN Bologna
 * @date    Aug 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.dataTransfer;

import org.apache.log4j.Logger;

public class DataTransferManagerImpl implements DataTransferManager
{
    private static final Logger log = Logger.getLogger("dataTransfer");
    private Functionality functionality = null;
    private PutDoneExecutor putDone = null;
    private ReleaseFilesExecutor releaseFiles = null;
    private ExtendFileLifeTimeExecutor extendFileLifeTime = null;
    private AbortExecutor abort = null;

    /**
     * Constructor.
     * @param func Functionality
     */
    public DataTransferManagerImpl(Functionality func)
    {
        functionality = func;
        switch (func.getFuncId()) {
        case DataTransferManager.PUTDONE_Id:
            putDone = new PutDoneExecutor();
            break;
        case DataTransferManager.RELEASEFILES_Id:
            releaseFiles = new ReleaseFilesExecutor();
            break;
        case DataTransferManager.EXTENDFILELIFETIME_Id:
            extendFileLifeTime = new ExtendFileLifeTimeExecutor();
            break;
        case DataTransferManager.ABORT_REQUEST_Id:
            abort = new AbortExecutor();
            break;    
        case DataTransferManager.ABORT_FILES_Id:
            abort = new AbortExecutor();
            break;        
        default:
            log.error("Unable to instantiate DataTransferManager. Please select the supported functionality.");
        }
    }

    public PutDoneOutputData putDone(PutDoneInputData inputData)
    {
        if (putDone != null) {
            return putDone.doIt(inputData);
        } else {
            log.error("DataTransferManager instantiated. Error calling " + functionality.toString());
            return null;
        }
    }
    
    public ReleaseFilesOutputData releaseFiles(ReleaseFilesInputData inputData)
    {
        if (releaseFiles != null) {
            return releaseFiles.doIt(inputData);
        } else {
            log.error("DataTransferManager instantiated. Error calling " + functionality.toString());
            return null;
        }
    }
    
    public ExtendFileLifeTimeOutputData extendFileLifeTime(ExtendFileLifeTimeInputData inputData)
    {
        if (extendFileLifeTime != null) {
            return extendFileLifeTime.doIt(inputData);
        } else {
            log.error("DataTransferManager instantiated. Error calling " + functionality.toString());
            return null;
        }
    }

    public AbortRequestOutputData abortRequest(AbortRequestInputData inputData)
    {
        if (abort != null) {
            return abort.doIt(inputData);
        } else {
            log.error("DataTransferManager instantiated. Error calling " + functionality.toString());
            return null;
        }
    }
    
    public AbortFilesOutputData abortFiles(AbortFilesInputData inputData)
    {
        if (abort != null) {
            return abort.doIt(inputData);
        } else {
            log.error("DataTransferManager instantiated. Error calling " + functionality.toString());
            return null;
        }
    }
    
    
}
