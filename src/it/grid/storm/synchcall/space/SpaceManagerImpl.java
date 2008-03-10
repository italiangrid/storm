/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
package it.grid.storm.synchcall.space;

import java.util.Date;

import org.apache.log4j.Logger;
import it.grid.storm.catalogs.InvalidRetrievedDataException;
import it.grid.storm.catalogs.InvalidSpaceDataAttributesException;
import it.grid.storm.catalogs.MultipleDataEntriesException;
import it.grid.storm.catalogs.NoDataFoundException;
import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.StorageSpaceData;
import it.grid.storm.common.types.InvalidPFNAttributeException;
import it.grid.storm.common.types.PFN;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.ReservationException;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.naming.NamespaceUtil;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSpaceTokenAttributesException;
import it.grid.storm.srm.types.TAccessLatency;
import it.grid.storm.srm.types.TRetentionPolicy;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TSpaceType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.synchcall.space.quota.QuotaException;

public class SpaceManagerImpl implements SpaceManager {

    private static final Logger log = Logger.getLogger("synch");
    private NamespaceInterface namespace;
    private ReservedSpaceCatalog catalog;

    private Functionality functionality = null;

    private GetSpaceMetaDataExecutor getSpaceMetaData = null;
    private ReserveSpaceExecutor reserve = null;
    private ReleaseSpaceExecutor release = null;
    private GetSpaceTokensExecutor getSpaceTokens = null;

    /**
     * Constructor.
     */
    public SpaceManagerImpl(Functionality func) {

      namespace = NamespaceDirector.getNamespace();
      catalog = new ReservedSpaceCatalog();
      switch (func.getFuncId()) {
      case SpaceManager.RESERVESPACE_Id:
        reserve = new ReserveSpaceExecutor();
        break;
      case SpaceManager.GETSPACEMETA_Id:
        getSpaceMetaData = new GetSpaceMetaDataExecutor();
        break;
      case SpaceManager.RELEASESPACE_Id:
        release = new ReleaseSpaceExecutor();
        break;
      case SpaceManager.GETSPACETOKENS_Id:
          getSpaceTokens = new GetSpaceTokensExecutor();
          break;
      default:
          log.error("Unable to instanciate SpaceManager. Please select the supported functionality.");
      }
    }

    /**
     * Method that provide ReserveSpace functionality.
     *
     * @param inputData Contain information about data of ReserveSpace request.
     * @return ReserveSpaceOutputData that contain all SRM return parameter.
     * @todo Implement this it.grid.storm.synchcall.directory.DirectoryManager
     *   method
     */
    public ReserveSpaceOutputData reserveSpace(ReserveSpaceInputData inputData) {
          if (reserve != null) {
            return reserve.doIt(inputData);
          }
          else {
            log.error("Space Manager instanciate for " + functionality.toString());
            return null;
          }
    }

    /**
     * Method that provide GetSpaceMetaData functionality.
     * @param inputData    Contains information about input data for GetSpaceMetaData request.
     * @return GetSpaceMetaDataOutputData Contains output data
     */
    public GetSpaceMetaDataOutputData getSpaceMetaData(GetSpaceMetaDataInputData inputData) {
        GetSpaceMetaDataOutputData result = null;
        if (getSpaceMetaData != null) {
              result = getSpaceMetaData.doIt(inputData);
          }
          else {
            log.error("Space Manager instanciate for " + functionality.toString());
          }
        return result;
     }

    /**
     * Method that provide ReleaseSpace functionality.
     * @param inputData    Contains information about input data for ReleaseSpace request.
     * @return GetSpaceMetaDataOutputData Contains output data
     */
    public ReleaseSpaceOutputData releaseSpace(ReleaseSpaceInputData inputData) {
        if (release != null) {
            return release.doIt(inputData);
          }
          else {
            log.error("Space Manager instanciate for " + functionality.toString());
            return null;
          }
     }

    /**
    * Method that provide GetSpaceTokens functionality.
    * @param inputData    Contains information about input data for GetSpaceTokens request.
    * @return GetSpaceTokensOutputData Contains output data
    */
   public GetSpaceTokensOutputData getSpaceTokens(GetSpaceTokensInputData inputData) {
       if (getSpaceTokens != null) {
           return getSpaceTokens.doIt(inputData);
         }
         else {
           log.error("Space Manager instanciate for " + functionality.toString());
           return null;
         }
    }
}
