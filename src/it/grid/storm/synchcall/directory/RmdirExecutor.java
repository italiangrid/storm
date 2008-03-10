package it.grid.storm.synchcall.directory;

import org.apache.log4j.Logger;
//import com.sun.org.apache.xml.internal.utils.NameSpace;
import it.grid.storm.authorization.AuthorizationCollector;
import it.grid.storm.authorization.AuthorizationDecision;
import it.grid.storm.common.SRMConstants;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
class RmdirExecutor
{
    private Logger log = DirectoryManagerImpl.log;
    private NamespaceInterface namespace;

    public RmdirExecutor()
    {
        namespace = NamespaceDirector.getNamespace();
    }

    /**
     * Method that provide SrmRmdir functionality.
     *@param inputData 	Contains information about input data for Rmdir request.
     *@return TReturnStatus Contains output data
     */
    public TReturnStatus doit(RmdirInputData inputData)
    {
        log.debug("srmRm: Start execution.");
        TReturnStatus returnStatus = null;

        /**
         * Validate RmdirInputData. The check is done at this level to separate
         * internal StoRM logic from xmlrpc specific operation.
         */

        if ((inputData == null) || ((inputData != null) && (inputData.getSurl() == null))) {
            try {
                returnStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "Invalid paramter specified.");
                log.error("srmRmdir: <>  Request for [SURL=] failed with [status: "+ returnStatus.toString()+"]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
            	log.error("srmRmdir: <>  Request for [SURL=] failed. Error creating returnStatus " + ex1);
            }
            return returnStatus;
        }

        /**
         * Check if GridUser in RmdirInputData is not null, otherwise return
         * with an error message.
         */
        GridUserInterface guser = inputData.getUser();
        // Create Input Structure
        if (guser == null) {
            log.debug("srmRm: Unable to get user credential. ");
            try {
                returnStatus = new TReturnStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE, "Unable to get user credential!");
                log.error("srmRmdir: <>  Request for [SURL=] failed with [status: "+ returnStatus.toString()+"]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
            	log.error("srmRmdir: <>  Request for [SURL=] failed. Error creating returnStatus " + ex1);
            }
            return returnStatus;
        }

        TSURL surl = inputData.getSurl();
        StoRI stori = null;

        // Check if SURL is not empty
        if (!surl.isEmpty()) {
            try {
                stori = namespace.resolveStoRIbySURL(surl, guser);
            } catch (NamespaceException ex) {
                log.debug("srmRm: Unable to build StoRI by SURL : '" + surl + "'"+ex);
                try {
                    returnStatus = new TReturnStatus(TStatusCode.SRM_INVALID_PATH, "Invalid SURL specified");
                    log.error("srmRmdir: <"+guser+">  Request for [SURL="+surl+"] failed with [status: "+ returnStatus.toString()+"]");
                } catch (InvalidTReturnStatusAttributeException ex1) {
                    log.error("srmRmdir: <"+guser+">  Request for [SURL="+surl+"] failed. Error creating returnStatus " + ex1);
                }
                return returnStatus;
            }

        } else {
            // Empty SURL. Error in surl creation.
            try {
                returnStatus = new TReturnStatus(TStatusCode.SRM_INVALID_PATH, "Invalid SURL specified");
                log.error("srmRmdir: <"+guser+">  Request for [SURL="+surl+"] failed with [status: "+ returnStatus.toString()+"]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
            	log.error("srmRmdir: <"+guser+">  Request for [SURL="+surl+"] failed. Error creating returnStatus " + ex1);
            }
            return returnStatus;

        }

        // Check here if recursive flag is not specifed
        // in input parameter.Use default value
        Boolean recursive = inputData.getRecursiveFlag();
        if (recursive == null) recursive = new Boolean(SRMConstants.recursiveFlag);

        // Grid User Identity
        VomsGridUser user = (VomsGridUser) guser;
        
     
        boolean failure = false;
        String explanation = "";
        TStatusCode statusCode = TStatusCode.EMPTY;

        // Maps the VOMS Grid user into Local User.
        LocalUser lUser = null;
        try {
            lUser = user.getLocalUser();
        } catch (CannotMapUserException ex) {
            log.error("Unable to setting up the ACL ");
            failure = true;
            explanation = "RMDIR : Unable to map the user '" + user + "' in a local user";
            statusCode = TStatusCode.SRM_AUTHORIZATION_FAILURE;
        }

        if (!(failure)) { // There is a local user for VOMS user
            AuthorizationDecision mkdirAuth = AuthorizationCollector.getInstance().canDelete(user, stori);

            if ((mkdirAuth != null) && (mkdirAuth.isPermit())) {
                log.debug("RMDIR is authorized for " + user + " and the directory = " + stori.getPFN() + " with recursove opt = " + recursive);
                returnStatus = manageAuthorizedRMDIR(lUser, stori, recursive.booleanValue());
                if(returnStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS))
                	log.info("srmRmdir: <"+guser+">  Request for [SURL="+surl+"] successfully done with [status: "+ returnStatus.toString()+"]");
                else
                   	log.error("srmRmdir: <"+guser+">  Request for [SURL="+surl+"] failed with [status: "+ returnStatus.toString()+"]");
            } else {
                failure = true;
                explanation = "User is not authorized to delete the directory";
                statusCode = TStatusCode.SRM_AUTHORIZATION_FAILURE;
            
            }
        }

        if (failure) { // Unauthorized access!
            try {
                returnStatus = new TReturnStatus(statusCode, explanation);
            	log.error("srmRmdir: <"+guser+">  Request for [SURL="+surl+"] failed with [status: "+ returnStatus.toString()+"]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
            	log.error("srmRmdir: <"+guser+">  Request for [SURL="+surl+"] failed .Error creating return status."+ex1);
            }
        }
        // Return status
        return returnStatus;
    }

    /**
     *
     * This method of FileSystem remove file and dir both from file system and
     * from DataBase
     *
     * @param user VomsGridUser
     * @param stori StoRI
     * @param recursive boolean
     * @return TReturnStatus
     */
    private TReturnStatus manageAuthorizedRMDIR(LocalUser lUser, StoRI stori, boolean recursive)
    {
        TReturnStatus returnStatus = null;
        boolean dirRemoved;

        boolean failure = false;
        String explanation = "";
        TStatusCode statusCode = TStatusCode.EMPTY;

        LocalFile directory = stori.getLocalFile();

        // Check if Directory to Remove exists
        if ((directory.exists()) && (directory.isDirectory())) {

            if (recursive) { // Recursive option
                // All directory and files contained are removed.
                log.debug("RECURSIVE=TRUE! Removing dir with all files included! ");

                boolean purgingResult = deleteDirectoryContent(directory, lUser);

                if (!(purgingResult)) { // There was some problems
                    failure = true;
                    statusCode = TStatusCode.SRM_FAILURE;
                    explanation = "Unable to delete some files within directory. Permission denied.";
                }
            }
            // Now Directory should be Empty;
            // NON-Recursive Option
            dirRemoved = removeFile(directory, lUser);
            if (!(dirRemoved)) { // There was some problems
                failure = true;
                statusCode = TStatusCode.SRM_NON_EMPTY_DIRECTORY;
                explanation = "Directory is not empty";
            } else { // Success!!
                failure = false;
                statusCode = TStatusCode.SRM_SUCCESS;
                explanation = "Directory removed with success!";
            }
        } else {
            log.debug("RMDIR : request with invalid directory specified!");
            // ParentDirectory doesn't exists!
            if (!directory.exists()) {
                failure = true;
                statusCode = TStatusCode.SRM_INVALID_PATH;
                explanation = "Directory does not exists";
            } else {
                if (!directory.isDirectory()) {
                    failure = true;
                    statusCode = TStatusCode.SRM_INVALID_PATH;
                    explanation = "Not a directory";
                }
            }
        }
        //Build the ReturnStatus
        try {
            returnStatus = new TReturnStatus(statusCode, explanation);
        } catch (InvalidTReturnStatusAttributeException ex1) {
            log.debug("RMDIR : Error creating returnStatus " + ex1);
        }

        return returnStatus;
    }

    private boolean removeFile(LocalFile file, LocalUser lUser)
    {
        boolean result = false;
        LocalFile[] list;
        if (file.exists()) { // existent file
            if (file.isDirectory()) { // the existent file is a directory
                // Check if directory is empty
                list = file.listFiles();
                if (list.length > 0) { // The directory is not empty!
                    // Produce an error, because only empty directory are
                    // permitted.
                    result = false;
                    log.debug("RMDIR : the target file '" + file + "' is a not-empty directory. ");
                } else { // The target is an empty directory
                    result = removeTarget(file, lUser);
                }
            } else { // the target is a file
                result = removeTarget(file, lUser);
            }
        } else { // The file does not exists
            result = false;
            log.debug("RMDIR : the target file '" + file + "' does not exists! ");
        }
        return result;
    }

    private boolean removeTarget(LocalFile file, LocalUser lUser)
    {
        boolean result = false;
        // Check Permission
        FilesystemPermission groupPermission = null;
        try {
            groupPermission = file.getGroupPermission(lUser);
        } catch (CannotMapUserException ex) {
            /**
             * @todo : Why this exception?
             */
            log.error("WHY THIS? " + ex);
        }

        FilesystemPermission userPermission = null;
        try {
            userPermission = file.getUserPermission(lUser);
        } catch (CannotMapUserException ex1) {
            /**
             * @todo : Why this exception?
             */
            log.debug("WHY THIS? " + ex1);
        }

        /**
         * @todo this check is not needed here. If Auth source say that user
         *       have the right permission. At this level could happen that a
         *       user create a Directory in JiT model, so without ACL, and then
         *       want to delete it. The permission on directory at filesystem level
         *       is not setted but the user must have the delete permission.
         */

        // Check if user or group permission are null to prevent Null Pointer
        boolean canDelete = true;
        /**
         * if(userPermission!=null) canDelete = userPermission.canDelete(); if
         * ((groupPermission!=null)&&(!canDelete)) canDelete =
         * groupPermission.canDelete();
         */
        // if ( (userPermission.canDelete()) || (groupPermission.canDelete())) {
        if (canDelete)
            result = file.delete();
        else
            log.debug("RMDIR : Unable to delete the file '" + file + "'. Permission denied.");

        return result;
    }

    /**
     * Recursive function for deleteAll
     */
    private boolean deleteDirectoryContent(LocalFile directory, LocalUser lUser)
    {
        boolean result = true;
        LocalFile[] list;
        if (directory.exists()) { // existent file
            if (directory.isDirectory()) { // the existent file is a directory
                // Scanning of directory
                list = directory.listFiles();
                if (list.length > 0) { // The directory is not empty
                    for (int i = 0; i < list.length; i++) {
                        // Delete each element within the directory
                        result = result && deleteDirectoryContent(list[i], lUser);
                        if (list[i].exists()) result = result && removeFile(list[i], lUser);
                    }
                } else {
                    // The directory is empty and it is deleted by the if in the
                    // for loop above
                }
            } else { //The target is a file
                result = removeFile(directory, lUser);
            }
        }
        return result;
    }

} //End of class
