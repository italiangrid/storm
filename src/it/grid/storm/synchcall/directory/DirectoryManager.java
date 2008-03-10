package it.grid.storm.synchcall.directory;

import it.grid.storm.srm.types.TReturnStatus;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
public interface DirectoryManager
{

    public static final int           LS_Id    = 0;
    public static final int           RM_Id    = 1;
    public static final int           RMDIR_Id = 2;
    public static final int           MKDIR_Id = 3;
    public static final int           MV_Id    = 4;

    public static final Functionality LS       = new Functionality("LS", LS_Id);
    public static final Functionality RM       = new Functionality("RM", RM_Id);
    public static final Functionality RMDIR    = new Functionality("RMDIR", RMDIR_Id);
    public static final Functionality MKDIR    = new Functionality("MKDIR", MKDIR_Id);
    public static final Functionality MV       = new Functionality("MV", MV_Id);

    /**
     * Method that provide ls functionality.
     *@param inputData 	Contain information about data OF SrmLS request.
     *@return LSOutputData that contain all SRM return parameter.
     */
    public LSOutputData ls(LSInputData inputData);

    /**
     * Method that provide SrmMkdir functionality.
     *@param inputData 	Contains information about input data for Mkdir request.
     *@return TReturnStatus Contains output data
     */
    public TReturnStatus mkdir(MkdirInputData inputData);

    /**
     *Method that provide SrmRm functionality.
     *@param inputData 	Contains information about input data for rm request.
     *@return RmOutputData Contains output data
     */
    public RmOutputData rm(RmInputData inputData);

    /**
     * Method that provide SrmRmdir functionality.
     *@param inputData 	Contains information about input data for Rmdir request.
     *@return TReturnStatus Contains output data
     */
    public TReturnStatus rmdir(RmdirInputData inputData);

    /**
     * Method that provide SrmMV functionality.
     *@param inputData    Contains information about input data for Rmdir request.
     *@return outputData Contains output data
     */
    public MvOutputData mv(MvInputData inputData);

    /**
     *
     * <p>Title: </p>
     *
     * <p>Description: </p>
     *
     * <p>Copyright: Copyright (c) 2006</p>
     *
     * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
     *
     * @author Riccardo Zappi
     * @version 1.0
     */
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
