package it.grid.storm.authorization.sources;

import org.apache.log4j.Logger;

import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.config.Configuration;
import it.grid.storm.authorization.AuthorizationDecision;
import it.grid.storm.authorization.AuthorizationQueryInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.srm.types.TSURL;

import java.util.Iterator;
import java.util.Collection;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;

/**
 * Class implementing AuthorizationQueryInterface and that interacts with the
 * ECAR web service.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    May 2006
 */
public class ECARAuthorizationSource implements AuthorizationQueryInterface {

    static private Logger log = Logger.getLogger("EcarAuthorizationSource");

    /**
     * This method always returns NotApplicable: ECAR by itself cannot supply a
     * meaningful answer.
     *
	 * Please see AuthorizationQueryInterface for general contract.
	 */
    public AuthorizationDecision canUseStormAtAll(final GridUserInterface gridUser) {
        return AuthorizationDecision.NotApplicable;
    }

    /**
     * This method always returns NotApplicable: ECAR by itself cannot supply a
     * meaningful answer.
     *
	 * Please see AuthorizationQueryInterface for general contract.
	 */
    public AuthorizationDecision canChangeAcl(final GridUserInterface gridUser, final StoRI fileOrDirectory) {
        return AuthorizationDecision.NotApplicable;
    }

    /**
     * This method always returns NotApplicable: ECAR by itself cannot supply a
     * meaningful answer.
     *
	 * Please see AuthorizationQueryInterface for general contract.
     */
    public AuthorizationDecision canGiveaway(final GridUserInterface gridUser, final StoRI fileOrDirectory) {
        return AuthorizationDecision.NotApplicable;
    }

    /**
     * This method always returns NotApplicable: ECAR by itself cannot supply a
     * meaningful answer.
     *
	 * Please see AuthorizationQueryInterface for general contract.
	 */
    public AuthorizationDecision canRename(GridUserInterface gridUser, StoRI file) {
        return AuthorizationDecision.NotApplicable;
    }





    /**
	 * Check if user can be allowed read access to the specified file.
     *
     * NOTICE: it is also makes sure that there is Traverse permission on all
     * involved intermediary directories.
	 */
    public AuthorizationDecision canReadFile(GridUserInterface gridUser, StoRI file) {
        return doExtendedCheck(gridUser,file.getSURL(),FilesystemPermission.Read,file.getSURL(),"Read Authorization","Read");
    }

    /**
	 * Check if user can be allowed write access to the specified file.
     *
     * This request is considered meaningful only for already-existing
     * files; however, it is up to the actual implementation to check if
     * the file already exists.
     *
     * NOTICE: it also makes sure that there is Traverse permission on all
     * involved intermediary directories.
	 */
    public AuthorizationDecision canWriteFile(GridUserInterface gridUser, StoRI existingFile) {
        return doExtendedCheck(gridUser,existingFile.getSURL(),FilesystemPermission.Write,existingFile.getSURL(),"Write Authorization","Write");
    }

    /**
	 * Check if user can create the named file.  All levels of
	 * directories above the file to be created are assumed to exist;
	 * however, implementations of <code>canCreateNewFile</code> may
	 * possibly skip the existence check - it is up to the caller to
	 * ensure that the file does not already exist, or fail
	 * accordingly.
	 *
	 * <p>Involved in: <code>SrmCopy</code>,
	 * <code>srmPrepareToPut</code>
	 */
    public AuthorizationDecision canCreateNewFile(GridUserInterface gridUser, StoRI targetFile) {
        return doExtendedCheck(gridUser,targetFile.getSURL(),FilesystemPermission.Create,targetFile.getSURL().getParent(),"Create Authorization","Create");
    }

    /**
     * Private auxiliary method that performs an extended check in the sense that it also
     * looks for the Traverse permission on all intermidiary directories. It also requires
     * a logString that identifies the calling method, to improve logging of operations.
     */
    private AuthorizationDecision doExtendedCheck(GridUserInterface gridUser, TSURL file, FilesystemPermission mainPermission, TSURL mainTarget, String logString, String logPermissionString) {
        log.info("ECAR Authorization Source: "+logString+" invoked for "+gridUser.getDn()+" on SURL "+file.toString());
        TSURL parent = null; //TSURL representing a parent directory
        boolean ok = true; //boolean _true_ as long as _all_ currently tested permissions are true.
        ECARClient client = new ECARClient(Configuration.getInstance().getECARServiceEndPoint());
        try {
            client.open();
            ECARTranslator translate = new ECARTranslator(); //translator containing all logic to convert from StoRM object model to ECAR web service data representation
            Collection parentList = file.getParents();
            Iterator i = parentList.iterator();
            while (ok && i.hasNext()) {
                parent = (TSURL) i.next();
                log.debug("ECAR Authorization Source - "+logString+" - asking for Traverse on "+parent.toString());
                ok = ok && client.canAccess(translate.logicalName(parent), translate.permission(FilesystemPermission.Traverse), translate.user((VomsGridUser)gridUser));
                log.debug("ECAR Authorization Source - "+logString+" - reply from ECAR: "+ok);
            } //end while
            if (!ok) {
                log.info("DENIED");
                return AuthorizationDecision.Deny;
            }
            log.debug("ECAR Authorization Source - "+logString+" - asking for "+logPermissionString+" on "+mainTarget.toString());
            ok = client.canAccess(translate.logicalName(mainTarget), translate.permission(mainPermission), translate.user((VomsGridUser)gridUser));
            log.debug("ECAR Authorization Source - "+logString+" - reply from ECAR: "+ok);
            client.close();
            if (!ok) {
                log.info("DENIED");
                return AuthorizationDecision.Deny;
            } else {
                log.info("ALLOWED");
                return AuthorizationDecision.Permit;
            }
        } catch (ECARServiceException e) {
            //ECAR WebService launched a SoapFault
            client.close();
            log.warn("ECAR Authorizaton Souce: "+logString+" got a SoapFault while querying the service: "+e);
            return AuthorizationDecision.Indeterminate;
        } catch (ECARClientException e) {
            //Client could not communicate successfully with WebService!
            client.close();
            log.error("ECAR Authorization Source: "+logString+" received an error from its ECAR consumer: "+e);
            return AuthorizationDecision.Indeterminate;
        } catch (ECARMissingPolicyException e) {
            //ECAR replied saying it does not have information for answering!
            client.close();
            log.warn("ECAR Authorization Source: "+logString+" received an exception about MISSING policy: "+e);
            return AuthorizationDecision.NotApplicable;
        } catch (Exception e) {
            //Unexpected exception with consumer!
            client.close();
            log.error("ECAR Authorization Source: "+logString+" received an unexpected error from its ECAR consumer: "+e);
            return AuthorizationDecision.Indeterminate;
        }
    }















    /**
	 * Check if user can list a directory contents.  Note that
	 * implementations may possibly skip the existence check on the
	 * named directory, it is up to the caller to ensure that the file
	 * or directory being operated upon actually exists in the StoRM
	 * namespace.
	 *
	 *
	 * <p>Involved in: <code>srmLs</code>,
	 */
    public AuthorizationDecision canListDirectory(GridUserInterface gridUser, StoRI directory) {
        return doCheck(gridUser,directory.getSURL(),FilesystemPermission.ListDirectory,"ListDirectory Authorization","ListDirectory");
    }

    /**
	 * Check if user can descend the specified path.  If
	 * <code>path</code> points to a file, then check if the specified
	 * path can be descended to the directory containing that file.
	 *
	 * <p>Note that implementations may possibly skip the existence
	 * check on the named entity, it is up to the caller to ensure
	 * that the file or directory being operated upon actually exists
	 * in the StoRM namespace.
	 *
	 * <p>Involved in: <code>srmCopy</code>, <code>srmLs</code>,
	 * <code>srmPrepareToGet</code>, <code>srmPrepareToPut</code>,
	 * <code>srmMv</code>, <code>srmMkdir</code>, <code>srmRm</code>,
	 * <code>srmRmdir</code>.
	 */
    public AuthorizationDecision canTraverseDirectory(GridUserInterface gridUser, StoRI path) {
        return doCheck(gridUser,path.getSURL(),FilesystemPermission.Traverse,"TraverseDirectory Authorization","Traverse");
    }

    /**
	 * Check if user can delete the specified file or directory.
	 * Note that implementations may possibly skip
	 * the existence check on the named entity, it is up to the caller
	 * to ensure that the file or directory being operated upon
	 * actually exists in the StoRM namespace.
	 *
	 * <p>Involved in: <code>srmRm</code>, <code>srmRmdir</code>.
	 */
    public AuthorizationDecision canDelete(GridUserInterface gridUser, StoRI file) {
        return doCheck(gridUser,file.getSURL(),FilesystemPermission.Delete,"Delete Authorization","Delete");
    }

    /**
	 * Check if user can create the specified directory.
	 * Note that implementations may possibly skip
	 * the existence check on the named entity, it is up to the caller
	 * to ensure that the file or directory being operated upon
	 * actually exists in the StoRM namespace.
	 *
	 * <p>Involved in: <code>srmMkdir</code>.
	 */
    public AuthorizationDecision canMakeDirectory(GridUserInterface gridUser, StoRI targetDirectory) {
        return doCheck(gridUser,targetDirectory.getSURL().getParent(),FilesystemPermission.CreateSubdirectory,"MakeDirectory Authorization","CreateSubdirectory");
    }

    /**
     * Private auxiliary method that performs an extended check in the sense that it also
     * looks for the Traverse permission on all intermidiary directories. It also requires
     * a logString that identifies the calling method, to improve logging of operations.
     */
    private AuthorizationDecision doCheck(GridUserInterface gridUser, TSURL file, FilesystemPermission mainPermission, String logString, String logPermissionString) {
        log.info("ECAR Authorization Source: "+logString+" invoked for "+gridUser.getDn()+" on file "+file.toString());
        TSURL parent = null; //TSURL representing a parent directory
        boolean ok = true; //boolean _true_ as long as _all_ currently tested permissions are true.
        ECARClient client = new ECARClient(Configuration.getInstance().getECARServiceEndPoint());
        try {
            client.open();
            ECARTranslator translate = new ECARTranslator(); //translator containing all logic to convert from StoRM object model to ECAR web service data representation
            String lfnTrans = translate.logicalName(file);
            int permTrans = translate.permission(mainPermission);
            String userTrans = translate.user((VomsGridUser)gridUser);
            log.info("ECAR Authorization Source - "+logString+" - asking for "+permTrans+" on "+lfnTrans+" for user "+userTrans);
            ok = client.canAccess(lfnTrans,permTrans,userTrans);
            log.info("ECAR Authorization Source - "+logString+" - reply from ECAR: "+ok);
            client.close();
            if (!ok) return AuthorizationDecision.Deny; else return AuthorizationDecision.Permit;
        } catch (ECARServiceException e) {
            //ECAR WebService launched a SoapFault
            client.close();
            log.error("ECAR Authorizaton Souce: "+logString+" got a SoapFault while querying the service: "+e);
            return AuthorizationDecision.Indeterminate;
        } catch (ECARClientException e) {
            //Client could not communicate successfully with WebService!
            client.close();
            log.error("ECAR Authorization Source: "+logString+" received an error from its ECAR consumer: "+e);
            return AuthorizationDecision.Indeterminate;
        } catch (Exception e) {
            //Unexpected exception with consumer!
            client.close();
            log.error("ECAR Authorization Source: "+logString+" received an unexpected error from its ECAR consumer: "+e);
            return AuthorizationDecision.Indeterminate;
        }
    }



}
