/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @author Magnoni Luca
 * @version 1.0
 */
package it.grid.storm.synchcall.space;

public interface SpaceManager
{
    public static final int           RESERVESPACE_Id   = 0;
    public static final int           GETSPACEMETA_Id   = 1;
    public static final int           RELEASESPACE_Id   = 2;
    public static final int           GETSPACETOKENS_Id = 3;

    public static final Functionality RESERVESPACE      = new Functionality("ReserveSpace", RESERVESPACE_Id);
    public static final Functionality GETSPACEMETA      = new Functionality("GetSpaceMetaData", GETSPACEMETA_Id);
    public static final Functionality RELEASESPACE      = new Functionality("ReleaseSpace", RELEASESPACE_Id);
    public static final Functionality GETSPACETOKENS    = new Functionality("ReleaseSpace", GETSPACETOKENS_Id);

    /**
     * Method that provide reserveSpace functionality.
     * @param inputData    Contain information about data OF SrmReserveSpace request.
     * @return ReserverSpaceOutputData that contain all SrmReserveSpace return parameter.
     */
    public ReserveSpaceOutputData reserveSpace(ReserveSpaceInputData inputData);

    /**
     * Method that provide GetSpaceMetaData functionality.
     * @param inputData    Contains information about input data for Mkdir request.
     * @return GetSpaceMetaDataOutputData Contains output data
     */
    public GetSpaceMetaDataOutputData getSpaceMetaData(GetSpaceMetaDataInputData inputData);

    /**
     * Method that provide ReleaseSpace functionality.
     * @param inputData    Contains information about input data for rm request.
     * @return RmOutputData Contains output data
     */
    public ReleaseSpaceOutputData releaseSpace(ReleaseSpaceInputData inputData);
    
    /**
     * Method that provide GetSpaceTokens functionality.
     * @param inputData    Contains information about input data for rm request.
     * @return RmOutputData Contains output data
     */
    public GetSpaceTokensOutputData getSpaceTokens(GetSpaceTokensInputData inputData);

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
