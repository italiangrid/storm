package it.grid.storm.synchcall.dataTransfer;

public interface DataTransferManager
{

    public static final int           PUTDONE_Id            = 0;
    public static final int           RELEASEFILES_Id       = 1;
    public static final int           EXTENDFILELIFETIME_Id = 2;
    public static final int           ABORT_REQUEST_Id      = 3;
    public static final int           ABORT_FILES_Id        = 4;

    public static final Functionality PUTDONE            = new Functionality("PUTDONE", PUTDONE_Id);
    public static final Functionality RELEASEFILES       = new Functionality("RELEASEFILES", RELEASEFILES_Id);
    public static final Functionality EXTENDFILELIFETIME = new Functionality("EXTENDFILELIFETIME",
                                                                 EXTENDFILELIFETIME_Id);
    public static final Functionality ABORT_REQUEST      = new Functionality("ABORT_REQUEST", ABORT_REQUEST_Id);
    public static final Functionality ABORT_FILES      = new Functionality("ABORT_FILES", ABORT_FILES_Id);
    

    /**
     * Method that provides the srmPutDone functionality.
     * @param data PutDoneInputData
     * @return PutDoneOutputData
     */
    public PutDoneOutputData putDone(PutDoneInputData inputData);

    /**
     * Method that provides the srmReleaseFiles functionality.
     * @param data ReleaseFilesInputData
     * @return ReleaseFilesOutputData
     */
    public ReleaseFilesOutputData releaseFiles(ReleaseFilesInputData data);
    
    /**
     * Method that provides the srmExtendFileLifeTime functionality.
     * @param data ExtendFileLifeTimeInputData
     * @return ExtendFileLifeTimeOutputData
     */
    public ExtendFileLifeTimeOutputData extendFileLifeTime(ExtendFileLifeTimeInputData data);

    /**
     * Method that provides the srmAbortRequest functionality.
     * @param data AbortRequestInputData
     * @return AbortRequestOutputData
     */
    public AbortRequestOutputData abortRequest(AbortRequestInputData data);
    
    /**
     * Method that provides the srmAbortFiles functionality.
     * @param data AbortFilesInputData
     * @return AbortFilesOutputData
     */
    public AbortFilesOutputData abortFiles(AbortFilesInputData data);
    
    
    
    
    
    public static class Functionality
    {

        private String description;
        private int    funcId;

        private Functionality(String description, int funcId) {
            this.description = description;
            this.funcId = funcId;
        }

        public int getFuncId()
        {
            return funcId;
        }

        public String toString()
        {
            return description;
        }

        public boolean equals(Object obj)
        {
            if (obj == null) {
                return false;
            }
            if (obj instanceof Functionality) {
                Functionality func = (Functionality) obj;
                if (func.funcId == this.funcId) {
                    return true;
                }
            } else {
                return false;
            }
            return false;
        }
    }
}
