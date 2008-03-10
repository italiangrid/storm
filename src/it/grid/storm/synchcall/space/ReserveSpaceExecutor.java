package it.grid.storm.synchcall.space;


import java.io.File;
import java.util.Date;

import javax.transaction.TransactionRequiredException;

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
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.ReservationException;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.DefaultValues.SpaceDefault;
import it.grid.storm.namespace.naming.NamespaceUtil;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSpaceTokenAttributesException;
import it.grid.storm.srm.types.TAccessLatency;
import it.grid.storm.srm.types.TRetentionPolicy;
import it.grid.storm.srm.types.TRetentionPolicyInfo;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TSpaceType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.common.types.SizeUnit;

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
 * @author lucamag
 * @version 1.0
 */
public class ReserveSpaceExecutor  {
    
    private ReservedSpaceCatalog catalog;
    private static final Logger log = Logger.getLogger("synch");
    private NamespaceInterface namespace;
      
    private static final boolean SUCCESS = true;
    private static final boolean FAILURE = false;
    private static final boolean LOCALSTATUS = false;
    
    
    
    private String formatLogMessage(boolean success, GridUserInterface user,TSizeInBytes desSize, TSizeInBytes guarSize, 
    		TLifeTimeInSeconds lifetime, TRetentionPolicyInfo rpinfo, TStatusCode code, String expl) {
    
    	TReturnStatus status = null;
		try {
			status = new TReturnStatus(code,expl);
		} catch (InvalidTReturnStatusAttributeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return formatLogMessage(success, user, desSize, guarSize, lifetime, rpinfo, status);
    }
    	    
    private String formatLogMessage(boolean success, GridUserInterface user,TSizeInBytes desSize, TSizeInBytes guarSize, 
    		TLifeTimeInSeconds lifetime, TRetentionPolicyInfo rpinfo, TReturnStatus status) {
    	StringBuffer buf = new StringBuffer("srmReserveSpace: ");
    	buf.append("<"+user+"> ");
    	buf.append("Request for [");
    	buf.append("desiredSizeOfTotalSpace: "+desSize);
    	buf.append(", desiredSizeOfGuaranteedSpace: "+guarSize);
    	buf.append("] ");
    	buf.append("with [desiredLifetimeOfReservedSpace: "+lifetime+"] ");
    	buf.append("with [retentionPolicyInfo: "+rpinfo+"] ");
    	
    	if(success)
    		buf.append("successfully done with:[status:");
    	else
    		buf.append("failed with:[status:");

    	buf.append(status);	
    	
    	buf.append("]");
    	
    	return buf.toString();
    	
    }
    
    /**
     * Constructor.
     */
    public ReserveSpaceExecutor() {
        namespace = NamespaceDirector.getNamespace();
        catalog = new ReservedSpaceCatalog();
     
    }
    

  /**
   * Method that provide space reservation for srmReserveSpace request.
   *
   * @param data Contain information about data procived in SRM request.
   * @return SpaceResOutputData that contain all SRM return parameter.
   * @todo Implement this it.grid.storm.synchcall.space.SpaceManager method
   */
    public ReserveSpaceOutputData doIt(ReserveSpaceInputData data) {

        ReserveSpaceOutputData outputData = null;
        TSizeInBytes freeSpace = null;
        TSpaceToken tok = null;
        TReturnStatus status = null;
        Date date = new Date();

        String explanation = null;
        TStatusCode statusCode = TStatusCode.EMPTY;
        boolean failure = false;
        boolean lower_space = false;

        log.debug("<SpaceReservationManager>:reserveSpace start.");

        /**
         * Check if SpareResInputData is a valid input parameter.
         * Otherwise return whit error status.
         */

        if (data == null) {
            explanation = "Invalid Parameter specified";
            statusCode = TStatusCode.SRM_FAILURE;
            log.error(formatLogMessage(FAILURE, null, null, null, null, null, statusCode, explanation));
            return manageError(statusCode,explanation);
        }

        /**
         * @todo: Verify permission by UserID.
          SRM_UNAUTHORIZED_ACCESS:
           Requester has no permissions for the srmReserveSpace operation
           (although the user you have a valid authentication information).
           Please contact the site administration to authorization info.
         **/


        /** 
         *  Check if GridUser in InputData is not null, 
         *  otherwise return with an error message.
         */

        GridUserInterface user =  data.getUser();
        if (user == null) {
            log.error("SpaceRes: Unable to get user credential. ");
            statusCode = TStatusCode.SRM_AUTHENTICATION_FAILURE;
            explanation = "Unable to get user credential!";
            log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
            		data.getRetentionPolicyInfo(), statusCode, explanation));
            return manageError(statusCode,explanation);
        }

        /**
         * Check is TRetentionPolicy specified by client is REPLICA, ONLINE,
         * otherwise return error (SRM_INVALID_REQUEST).
          */
        
        if (data.getRetentionPolicyInfo() != null) {
            if (data.getRetentionPolicyInfo().getAccessLatency().equals(TAccessLatency.EMPTY))
                // Set accessLatency to the default value.
                data.getRetentionPolicyInfo().setAccessLatency(TAccessLatency.ONLINE);
            
            if (!(data.getRetentionPolicyInfo().getAccessLatency().equals(TAccessLatency.ONLINE))
                || !(data.getRetentionPolicyInfo().getRetentionPolicy().equals(TRetentionPolicy.REPLICA))) {
                log.debug("SpaceRes: Invalid TRetentionPolicyInfo specified:  "
                          + data.getRetentionPolicyInfo().getAccessLatency().toString() + ","
                          + (data.getRetentionPolicyInfo().getRetentionPolicy().toString()));
                statusCode = TStatusCode.SRM_NOT_SUPPORTED;
                explanation = "RetentionPolicy requested cannot be satisfied.";
                log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
                		data.getRetentionPolicyInfo(), statusCode, explanation));
                return manageError(statusCode, explanation);
            }
        } else {
            log.debug("SpaceRes: Invalid TRetentionPolicyInfo specified:  "
                      + data.getRetentionPolicyInfo().getAccessLatency().toString() + ","
                      + (data.getRetentionPolicyInfo().getRetentionPolicy().toString()));
            statusCode = TStatusCode.SRM_INVALID_REQUEST;
            explanation = "RetentionPolicy requested cannot be satisfied.";
            log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
            		data.getRetentionPolicyInfo(), statusCode, explanation));
            return manageError(statusCode, explanation);
        }

        /********************
         * Authorize the creation of Storage Space File with desiderata space
         *
         * 1) There is enough free space in the file system?
         * 2) The user is authorized to create a storage space with this size?
         * 3) The user has permission for create storege space file (wite permission)?
         *
         **/
        boolean authorize = true;

        /**
         * Verification of the free Space available in file system
         */
        //Obtain the PFN of the user-root directory.

        String spaceFN = null;
        try {
            spaceFN = namespace.makeSpaceFileURI(user);
            /**
             * This parsing it used to eliminate each double / present. 
             */
            //while(spaceFN.indexOf("//")!=-1) {
            //    spaceFN = spaceFN.replaceAll("//","/");
            //}
            log.debug(" Space FN : " + spaceFN);

        } catch (NamespaceException ex) {
            log.error("Unable to build default Space FN ", ex);
            statusCode = TStatusCode.SRM_INVALID_REQUEST;
            explanation = "Unable to build default Space FN \n" + ex;
            log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
            		data.getRetentionPolicyInfo(), statusCode, explanation));
            return manageError(statusCode, explanation);
        }

        VirtualFSInterface vfs = null;
        try {
            vfs = namespace.resolveVFSbyAbsolutePath(spaceFN);
            log.debug("Space File belongs to VFS : " + vfs.getAliasName());
        } catch (NamespaceException ex2) {
            log.debug("Unable to resolve VFS ", ex2);
            statusCode = TStatusCode.SRM_INVALID_REQUEST;
            explanation = "Unable to resolve VFS \n" + ex2;
            log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
            		data.getRetentionPolicyInfo(), statusCode, explanation));
            return manageError(statusCode, explanation);
        }

        String relativeSpaceFN = null;
        try {
            log.debug("ExtraceRelativeSpace: root:"+vfs.getRootPath()+" spaceFN:"+spaceFN);
            relativeSpaceFN = NamespaceUtil.extractRelativePath(vfs.getRootPath(), spaceFN);
            log.debug("relativeSpaceFN:"+relativeSpaceFN);
        } catch (NamespaceException ex3) {
            log.debug("Unable to retrieve the relative space file name ", ex3);
            statusCode = TStatusCode.SRM_INVALID_REQUEST;
            explanation = "Unable to retrieve the relative space file name \n" + ex3;
            log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
            		data.getRetentionPolicyInfo(), statusCode, explanation));
            return manageError(statusCode,explanation);
        }


        //********** TLifeTime ****************/
        TLifeTimeInSeconds lifeTime = TLifeTimeInSeconds.makeEmpty();
        if(data.getLifetime().isEmpty()) {
            log.debug("LifeTime is EMPTY. Using default value.");
            try {
                lifeTime = vfs.getDefaultValues().getDefaultSpaceLifetime();
            } catch (NamespaceException e) {
                log.debug("Error while retrieving default Default Space Lifetime", e);
                explanation = "Error while retrieving default Lifetime Type \n" + e;
                failure = true;
            }
        } else {
            lifeTime = data.getLifetime();
            log.debug("LifeTime: "+data.getLifetime().value());

        }

        //************ RETRIEVE THE SIZEs OF SPACE ********************
        TSizeInBytes desiderataSpaceSize = TSizeInBytes.makeEmpty();
        
        TSizeInBytes totalSize = data.getDesiredSize();
        TSizeInBytes guarSize = data.getGuaranteedSize();

        // Malformed request check: if totalSize and guaranteedSize are used defined
        // and guaranteedSize > totalSize we consider the request as malformed
        if ( (!(totalSize.isEmpty())) && (!((guarSize.isEmpty())||guarSize.value() == 0)) ) {
            if (totalSize.value() < guarSize.value()) {
                log.debug("Error: totalSize < guaranteedSize");
                statusCode = TStatusCode.SRM_INVALID_REQUEST;
                explanation = "Error: totalSize can not be greater then guaranteedSize";
                log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
                		data.getRetentionPolicyInfo(), statusCode, explanation));
                return manageError(statusCode, explanation);
            }
        } else { // Assign default values if totalSize and guaranteedSize are not defined
            if ( !(totalSize.isEmpty()) ) {
                try {
                    guarSize = vfs.getDefaultValues().getDefaultGuaranteedSpaceSize();
                    if (totalSize.value() < guarSize.value()) guarSize = totalSize;
                } catch (NamespaceException ex1) {
                    guarSize = totalSize;
                }
            } else if ( !((guarSize.isEmpty())||guarSize.value() == 0) ) {
                try {
                    totalSize = vfs.getDefaultValues().getDefaultTotalSpaceSize();
                    if (totalSize.value() < guarSize.value()) {
                        totalSize = guarSize;
                        log.debug("GuaranteedSize greater than default total size!");
                    }
                } catch (NamespaceException ex1) {
                    totalSize = guarSize;
                }
            } else {
                try {
                    totalSize = vfs.getDefaultValues().getDefaultTotalSpaceSize();
                } catch (NamespaceException ex1) {
                    log.debug("Error while retrieving default TOTAL Space Values", ex1);
                    explanation = "Error while retrieving default TOTAL Space Values \n" + ex1;
                    failure = true;
                }
                try {
                    guarSize = vfs.getDefaultValues().getDefaultGuaranteedSpaceSize();
                } catch (NamespaceException ex1) {
                    log.debug("Error while retrieving default Guaranteed Space Values", ex1);
                    explanation = "Error while retrieving default Guaranteed Space Values \n" + ex1;
                    failure = true;
                }
                // totalSize must be greater than guaranteedSize the following check is to be sure
                // that the default parameters are correctly set.
                if (totalSize.value() < guarSize.value()) totalSize = guarSize;
            }
        }

        log.debug("Parameter parsing end");
        
        /**
         * At this point either totalSize and guarSize contains 
         * significative value. 
         * desiderataSpaceSize is setted to totalSize.
         */
        desiderataSpaceSize = totalSize;
        
        //This is valid because StoRM only reserve GUARANTEED space.
        guarSize = desiderataSpaceSize;

        /**
         *  Check free space on file system.
         */
        
        try {
            long bytesFree = vfs.getFilesystem().getFreeSpace();
            freeSpace = TSizeInBytes.make(bytesFree,SizeUnit.BYTES);
        } catch (InvalidTSizeAttributesException e ) {
            log.debug("Error while retrieving free Space in underlying Filesystem", e);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Error while retrieving free Space in underlying Filesystem \n" + e;
            log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
            		data.getRetentionPolicyInfo(), statusCode, explanation));
            return manageError(statusCode, explanation);
        } catch (NamespaceException ex) {
            log.debug("Error while retrieving free Space in underlying Filesystem. Unable to retrieve FS Driver", ex);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Error while retrieving free Space in underlying Filesystem. Unable to retrieve FS Driver \n" + ex;
            log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
            		data.getRetentionPolicyInfo(), statusCode, explanation));
            return manageError(statusCode, explanation);
        }

        /**
         * @todo Change here, also granted SpaceSize must be considered.
         */
        //If there is not enogh free space on storage
        if (freeSpace.value() < desiderataSpaceSize.value()) {
            if(freeSpace.value() < guarSize.value()) {
                //Not enough freespace
                log.debug("<SpaceResManager>:reserveSpace Not Enough Free Space on storage!");
                statusCode = TStatusCode.SRM_NO_FREE_SPACE;
                explanation = "SRM has not more free space.";
                log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
                		data.getRetentionPolicyInfo(), statusCode, explanation));
                return manageError(statusCode,explanation);
            } else {
                //Enough free space to reserve granted space asked.
                desiderataSpaceSize = guarSize;
                lower_space = true;
            }
        }
        
        /**************************
         * Verification of user permission
         */
        /** @todo Call out to authorization source
         *    Action  : srmReserveSpace of desiderataSize
         *    Subject : user
         *    Object  : VO space
         * */

        /**************************
         * Verification of user quota
         */
        /** @todo
         *   - Retrieve space consumed
         *   - Retrieve space available in quoata policy
         *   - Call out to authorization source:
         *      Action  : srmReserveSpace of desiderataSize
         *      Subject : user
         *      Object  : User space
         * */

        if (authorize) {
            //Create SpaceFile Name finding right StoRI by UserID, Virtual Organization
            PFN spacePFN = null;
            try {
                spacePFN = PFN.make(spaceFN);
                log.debug("Space File name (PFN Form) identified: " + spacePFN);
            }
            catch (InvalidPFNAttributeException ex6) {
                log.debug("Unable to create Space File in PFN Format", ex6);
                statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                explanation = "Unable to create Space File in PFN Format \n" + ex6;
                log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
                		data.getRetentionPolicyInfo(), statusCode, explanation));
                return manageError(statusCode, explanation);
            }

            //Call wrapper to reserve Space.
            log.debug("reserve Space File Size :" + desiderataSpaceSize.toString());

            StoRI spaceFile = null;
            try {
                spaceFile = vfs.createSpace(relativeSpaceFN, desiderataSpaceSize.value());
            }
            catch (NamespaceException ex4) {
                log.debug("Unable to create Space File in VFS ", ex4);
                statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                explanation = "Unable to create Space File in VFS \n" + ex4;
                log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
                		data.getRetentionPolicyInfo(), statusCode, explanation));
                return manageError(statusCode, explanation);
            }

            //Create Space into FileSyste
            try {
                spaceFile.getSpace().allot();
            } catch( ReservationException e) {
                log.debug("Space reservation fail at FS level"+e);
                statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                explanation = "Unable to create Space File into filesystem. \n";
                log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
                		data.getRetentionPolicyInfo(), statusCode, explanation));
                return manageError(statusCode, explanation);
            }

            //Call wrapper to set ACL on file created.
            /**Check for JiT or AoT */
            boolean hasJiTACL = spaceFile.hasJustInTimeACLs();

            FilesystemPermission fp = FilesystemPermission.ReadWrite;

            if(hasJiTACL) {
                //JiT Case
                log.debug("<SpaceResManager>:reserveSpace AddACL for FIle: " + spacePFN + "  " + "USER RW");
                try {
                    spaceFile.getLocalFile().grantUserPermission(user.getLocalUser(), fp);
                } catch (CannotMapUserException ex5) {
                    log.debug("Unable to setting up the ACL ", ex5);
                    statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                    explanation = "Unable to setting up the ACL ";
                    log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
                    		data.getRetentionPolicyInfo(), statusCode, explanation));
                    return manageError(statusCode, explanation);
                } 
            } else {
                //AoT Case
                log.debug("<SpaceResManager>:reserveSpace AddACL for FIle: " + spacePFN + "  " + "GROUP RW");
                try {
                    spaceFile.getLocalFile().grantGroupPermission(user.getLocalUser(), fp);
                } catch (CannotMapUserException ex5) {
                    log.debug("Unable to setting up the ACL ", ex5);
                    statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                    explanation = "Unable to setting up the ACL ";
                    log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
                    		data.getRetentionPolicyInfo(), statusCode, explanation));
                    return manageError(statusCode, explanation,1, spaceFile.getLocalFile());
                } 
            }



            /**
             * Create Storage Space in StoRM domain
             */
            log.debug("-- Creating Storage Space Data ...");
            StorageSpaceData spaceDt = null;

            /**
             * @todo REMOVE THIS AS SOON DB IS UPDATED!
             */
            TSpaceType spaceType = TSpaceType.PERMANENT;
            
            // FIXME: storageSystemInfo is passed as NULL. To fix managing the new type ArrayOfTExtraInfo
            try {
                spaceDt = new StorageSpaceData(data.getUser(), spaceType, data.getSpaceTokenAlias(),
                        totalSize, guarSize, lifeTime,
                        null, date, spacePFN);
                log.debug(spaceDt.toString());
                log.debug("-- Created Storage Space Data --");
            }
            catch (InvalidSpaceDataAttributesException ex7) {
                log.debug("Unable to create Storage Space Data", ex7);
                statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                explanation = "Unable to create storage space data.";
                log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
                		data.getRetentionPolicyInfo(), statusCode, explanation));
                return manageError(statusCode, explanation,2, spaceFile.getLocalFile());
            }

            /**
             * Add Storage Space in Catalog
             */

         // if(spaceDt.getSpaceTokenAlias().contains("PURGEVO"))
         //    	catalog.purgeOldVOSA_token();
            
         // if(spaceDt.getSpaceTokenAlias().contains("LUCATEST"))
         //	catalog.createVOSA_Token(spaceDt.getSpaceTokenAlias(), spaceDt.getGuaranteedSize(), spaceDt.getSpaceFileNameString(), false);
         //else {
            try {
                catalog.addStorageSpace(spaceDt);
            }
            catch (MultipleDataEntriesException ex8) {
                log.debug("MultipleDataEntriesException", ex8);
            }
            catch (InvalidRetrievedDataException ex8) {
                log.debug("InvalidRetrievedDataException", ex8);
            }
            catch (NoDataFoundException ex8) {
                log.debug("NoDataFoundException", ex8);
            }

            try {
                tok = TSpaceToken.make(spaceDt.getSpaceToken().toString());
            }
            catch (InvalidTSpaceTokenAttributesException ex10) {
                log.debug("Error creating Space Token . Critical error!" + ex10);
                statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                explanation = "Unable to create space token.";
                log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
                		data.getRetentionPolicyInfo(), statusCode, explanation));
                return manageError(statusCode, explanation,2, spaceFile.getLocalFile());
            }
         //   }
        }
        if (! (failure)) {
            try {
                if(!lower_space)  {
                    status = new TReturnStatus(TStatusCode.SRM_SUCCESS, "Space Reservation done");
                    log.info(formatLogMessage(SUCCESS, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
                    		data.getRetentionPolicyInfo(), status));
                    
                } else { 
                    status = new TReturnStatus(TStatusCode.SRM_LOWER_SPACE_GRANTED,"Space Reservation done, lower space granted.");
                    log.info(formatLogMessage(SUCCESS, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
                    		data.getRetentionPolicyInfo(), status));
                }
            }
            catch (InvalidTReturnStatusAttributeException ex9) {
                log.debug("InvalidTReturnStatusAttributeException", ex9);
            }
        }
        else { //************ FAILURE !!!
            try {
                status = new TReturnStatus(TStatusCode.SRM_FAILURE, explanation);
            }
            catch (InvalidTReturnStatusAttributeException ex) {
                log.debug("Error creating status!Critical error!" + ex);
            }
        }

        try {
            outputData = new ReserveSpaceOutputData(totalSize, guarSize, lifeTime, tok, status);
        }
        catch (InvalidReserveSpaceOutputDataAttributesException ex11) {
            log.error("Error creating Output DATA . Critical error!" + ex11);
        }
        
        log.error(formatLogMessage(FAILURE, user, data.getDesiredSize(), data.getGuaranteedSize(), data.getLifetime(),
        		data.getRetentionPolicyInfo(), status));
        return (outputData);

    }

    /**
     * Method that reset an already done 
     * reservation to the original status.
     *
     * @param token TSpaceToken that contains information about data procived in SRM request.
     * @return TReturnStatus that contains of all SRM return parameters.
     */ 
    public TReturnStatus resetReservation(TSpaceToken token) {

        String explanation = null;
        TStatusCode statusCode = TStatusCode.EMPTY;

        StorageSpaceData sdata = catalog.getStorageSpace(token);
        //Check if it a VO_SA_Token, in that case do nothing
        if(sdata.getSpaceType().equals(TSpaceType.VOSPACE)) {
  		return manageErrorStatus(TStatusCode.SRM_SUCCESS, "Abort file done.");
	}
		
	GridUserInterface user = sdata.getUser();
        PFN spacePFN = sdata.getSpaceFileName();

        //Obtain the PFN of the user-root directory.

        String spaceFN = null;

        spaceFN = spacePFN.toString();

        VirtualFSInterface vfs = null;
        try {
            vfs = namespace.resolveVFSbyAbsolutePath(spaceFN);
            log.debug("Space File belongs to VFS : " + vfs.getAliasName());
        } catch (NamespaceException ex2) {
            log.debug("Unable to resolve VFS ", ex2);
            statusCode = TStatusCode.SRM_INVALID_REQUEST;
            explanation = "Unable to resolve VFS \n" + ex2;
            return manageErrorStatus(statusCode, explanation);
        }

        String relativeSpaceFN = null;
        try {
            log.debug("ExtractRelativeSpace: root:"+vfs.getRootPath()+" spaceFN:"+spaceFN);
            relativeSpaceFN = NamespaceUtil.extractRelativePath(vfs.getRootPath(), spaceFN);
            log.debug("relativeSpaceFN:"+relativeSpaceFN);
        } catch (NamespaceException ex3) {
            log.debug("Unable to retrieve the relative space file name ", ex3);
            statusCode = TStatusCode.SRM_INVALID_REQUEST;
            explanation = "Unable to retrieve the relative space file name \n" + ex3;
            return manageErrorStatus(statusCode,explanation);
        }

        //************ RETRIEVE THE SIZEs OF SPACE from DB********************
        TSizeInBytes desiderataSpaceSize = sdata.getTotalSize();
        TSizeInBytes totalSize = sdata.getTotalSize();
        TSizeInBytes guarSize = sdata.getGuaranteedSize();


        /**
         *  Check free space on file system.
         */

        TSizeInBytes freeSpace;
        try {
            long bytesFree = vfs.getFilesystem().getFreeSpace();
            freeSpace = TSizeInBytes.make(bytesFree,SizeUnit.BYTES);
        } catch (InvalidTSizeAttributesException e ) {
            log.debug("Error while retrieving free Space in underlying Filesystem", e);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Error while retrieving free Space in underlying Filesystem \n" + e;
            return manageErrorStatus(statusCode, explanation);
        } catch (NamespaceException ex) {
            log.debug("Error while retrieving free Space in underlying Filesystem. Unable to retrieve FS Driver", ex);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Error while retrieving free Space in underlying Filesystem. Unable to retrieve FS Driver \n" + ex;
            return manageErrorStatus(statusCode, explanation);
        }



        /*
         * Start with the reservation
         */
        boolean authorize =true;

        if (authorize) {
 
            //Call wrapper to reserve Space.
            log.debug("reserve Space File Size :" + desiderataSpaceSize.toString());

            StoRI spaceFile = null;
            try {
                spaceFile = vfs.createSpace(relativeSpaceFN, desiderataSpaceSize.value());
            }
            catch (NamespaceException ex4) {
                log.debug("Unable to create Space File in VFS ", ex4);
                statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                explanation = "Unable to create Space File in VFS \n" + ex4;
                return manageErrorStatus(statusCode, explanation);
            }

            //Create Space into FileSyste
            try {
                spaceFile.getSpace().allot();
            } catch( ReservationException e) {
                log.debug("Space reservation fail at FS level"+e);
                statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                explanation = "Unable to create Space File into filesystem. \n";
                return manageErrorStatus(statusCode, explanation);
            }

            //Call wrapper to set ACL on file created.
            /**Check for JiT or AoT */
            boolean hasJiTACL = spaceFile.hasJustInTimeACLs();

            FilesystemPermission fp = FilesystemPermission.ReadWrite;

            if(hasJiTACL) {
                //JiT Case
                log.debug("<SpaceResManager>:reserveSpace AddACL for FIle: " + spacePFN + "  " + "USER RW");
                try {
                    spaceFile.getLocalFile().grantUserPermission(user.getLocalUser(), fp);
                } catch (CannotMapUserException ex5) {
                    log.debug("Unable to setting up the ACL ", ex5);
                    statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                    explanation = "Unable to setting up the ACL ";
                    return manageErrorStatus(statusCode, explanation);
                } 
            } else {
                //AoT Case
                log.debug("<SpaceResManager>:reserveSpace AddACL for FIle: " + spacePFN + "  " + "GROUP RW");
                try {
                    spaceFile.getLocalFile().grantGroupPermission(user.getLocalUser(), fp);
                } catch (CannotMapUserException ex5) {
                    log.debug("Unable to setting up the ACL ", ex5);
                    statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                    explanation = "Unable to setting up the ACL ";
                    return manageErrorStatus(statusCode, explanation);
                } 
            }

        }

        /*
         * Update data into DB
         */
        //sdata.setSpaceFileName(spacePFN);
        sdata.setUnusedSize(desiderataSpaceSize);

        //UpdateData into the ReserveSpaceCatalog
        try {
            catalog.updateStorageSpace(sdata);
            
        } catch (NoDataFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return manageErrorStatus(TStatusCode.SRM_FAILURE, "Error updating DB.");
        } catch (InvalidRetrievedDataException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return manageErrorStatus(TStatusCode.SRM_FAILURE, "Error updating DB.");
        } catch (MultipleDataEntriesException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return manageErrorStatus(TStatusCode.SRM_FAILURE, "Error updating DB.");
        }


        return manageErrorStatus(TStatusCode.SRM_SUCCESS, "Successfull creation.");
    }
    
    /**
     * Method that update an already done 
     * reservation.
     *
     * @param data Contain information about data procvided in a SRM request.
     * @return SpaceResOutputData that contain all SRM return parameter.
     */ 
    
    public TReturnStatus updateReservation(TSpaceToken token, TSizeInBytes sizeToAdd, TSURL toSurl) {

        String explanation = null;
        TStatusCode statusCode = TStatusCode.EMPTY;

        StorageSpaceData sdata = catalog.getStorageSpace(token);
        GridUserInterface user = sdata.getUser();
        PFN spacePFN = sdata.getSpaceFileName();
	
	//Check if it a VO_SA_Token, in that case do nothing
        if(sdata.getSpaceType().equals(TSpaceType.VOSPACE)) {
  		return manageErrorStatus(TStatusCode.SRM_SUCCESS, "Abort file done.");
	}
	
        /**
         * Verification of the free Space available in file system
         */

        //Obtain the PFN of the user-root directory.

        String spaceFN = null;

        spaceFN = spacePFN.toString();

        VirtualFSInterface vfs = null;
        try {
            vfs = namespace.resolveVFSbyAbsolutePath(spaceFN);
            log.debug("Space File belongs to VFS : " + vfs.getAliasName());
        } catch (NamespaceException ex2) {
            log.debug("Unable to resolve VFS ", ex2);
            statusCode = TStatusCode.SRM_INVALID_REQUEST;
            explanation = "Unable to resolve VFS \n" + ex2;
            return manageErrorStatus(statusCode, explanation);
        }

        String relativeSpaceFN = null;
        try {
            log.debug("ExtractRelativeSpace: root:"+vfs.getRootPath()+" spaceFN:"+spaceFN);
            relativeSpaceFN = NamespaceUtil.extractRelativePath(vfs.getRootPath(), spaceFN);
            log.debug("relativeSpaceFN:"+relativeSpaceFN);
        } catch (NamespaceException ex3) {
            log.debug("Unable to retrieve the relative space file name ", ex3);
            statusCode = TStatusCode.SRM_INVALID_REQUEST;
            explanation = "Unable to retrieve the relative space file name \n" + ex3;
            return manageErrorStatus(statusCode,explanation);
        }

        //************ RETRIEVE THE SIZEs OF SPACE from DB********************
        TSizeInBytes desiderataSpaceSize = sdata.getTotalSize();
        TSizeInBytes totalSize = sdata.getTotalSize();
        TSizeInBytes guarSize = sdata.getGuaranteedSize();
        TSizeInBytes unusedSize = sdata.getUnusedSizes();
        
        log.debug("Unused Size : "+unusedSize.value());
        log.debug("Size of removed file: "+sizeToAdd.value() );
        
        /**
         * Add to the desiderata size the bytes freed from the abort
         */
        try {
            desiderataSpaceSize =  TSizeInBytes.make(unusedSize.value()+sizeToAdd.value(), SizeUnit.BYTES);
        } catch (InvalidTSizeAttributesException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        /**
         *  Check free space on file system.
         */

        TSizeInBytes freeSpace;
        try {
            long bytesFree = vfs.getFilesystem().getFreeSpace();
            freeSpace = TSizeInBytes.make(bytesFree,SizeUnit.BYTES);
        } catch (InvalidTSizeAttributesException e ) {
            log.debug("Error while retrieving free Space in underlying Filesystem", e);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Error while retrieving free Space in underlying Filesystem \n" + e;
            return manageErrorStatus(statusCode, explanation);
        } catch (NamespaceException ex) {
            log.debug("Error while retrieving free Space in underlying Filesystem. Unable to retrieve FS Driver", ex);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Error while retrieving free Space in underlying Filesystem. Unable to retrieve FS Driver \n" + ex;
            return manageErrorStatus(statusCode, explanation);
        }



        /*
         * Start with the reservation
         */
        boolean authorize =true;

        if (authorize) {

            //Call wrapper to reserve Space.
            log.debug("reserve Space File Size :" + desiderataSpaceSize.toString());

            StoRI spaceFile = null;
            try {
                 spaceFile = vfs.createSpace(relativeSpaceFN, desiderataSpaceSize.value());
            }
            catch (NamespaceException ex4) {
                log.debug("Unable to create Space File in VFS ", ex4);
                statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                explanation = "Unable to create Space File in VFS \n" + ex4;
                return manageErrorStatus(statusCode, explanation);
            }

            //Remove the old spaceFile with not updated size
            LocalFile localFile = spaceFile.getLocalFile();
            
            if(localFile!= null) {
                //Old spaceFile with wrong chunk
                localFile.delete();
            } else{
                //spaceFile does not exist yet
            }
                
            //            Create Space into FileSyste
            try {
                spaceFile.getSpace().allot();
            } catch( ReservationException e) {
                log.debug("Space reservation fail at FS level"+e);
                statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                explanation = "Unable to create Space File into filesystem. \n";
                return manageErrorStatus(statusCode, explanation);
            }

            
            //Call wrapper to set ACL on file created.
            /**Check for JiT or AoT */
            boolean hasJiTACL = spaceFile.hasJustInTimeACLs();

            FilesystemPermission fp = FilesystemPermission.ReadWrite;

            if(hasJiTACL) {
                //JiT Case
                log.debug("<SpaceResManager>:reserveSpace AddACL for FIle: " + spacePFN + "  " + "USER RW");
                try {
                    spaceFile.getLocalFile().grantUserPermission(user.getLocalUser(), fp);
                } catch (CannotMapUserException ex5) {
                    log.debug("Unable to setting up the ACL ", ex5);
                    statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                    explanation = "Unable to setting up the ACL ";
                    return manageErrorStatus(statusCode, explanation);
                } 
            } else {
                //AoT Case
                log.debug("<SpaceResManager>:reserveSpace AddACL for FIle: " + spacePFN + "  " + "GROUP RW");
                try {
                    spaceFile.getLocalFile().grantGroupPermission(user.getLocalUser(), fp);
                } catch (CannotMapUserException ex5) {
                    log.debug("Unable to setting up the ACL ", ex5);
                    statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                    explanation = "Unable to setting up the ACL ";
                    return manageErrorStatus(statusCode, explanation);
                } 
            }

        }

        /*
         * Update data into DB
         */
        
        /*
         *TODO Update the StorageFile information related to the removed chunk.
         * The SURL has been associated during the Put operation to the SpaceFile, 
         * due to an abort operation the SURL must be removed from
         * the StorageFile relation, and the size of the spaceFile have to be 
         * updated.
         * 
         */
        //  
        
        try {
            unusedSize = TSizeInBytes.make(sdata.getUnusedSizes().value() + sizeToAdd.value(), SizeUnit.BYTES);
        } catch (InvalidTSizeAttributesException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        //Update the free  bytes!
        sdata.setUnusedSize(unusedSize);

        //UpdateData into the ReserveSpaceCatalog
        try {
            
            catalog.updateStorageSpace(sdata);
            
        } catch (NoDataFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return manageErrorStatus(TStatusCode.SRM_FAILURE, "Error updating DB.");
        } catch (InvalidRetrievedDataException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return manageErrorStatus(TStatusCode.SRM_FAILURE, "Error updating DB.");
        } catch (MultipleDataEntriesException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return manageErrorStatus(TStatusCode.SRM_FAILURE, "Error updating DB.");
        }

        return manageErrorStatus(TStatusCode.SRM_SUCCESS, "Successfull creation.");
    }
    
  /**
   * This function is use to create updata data 
   * in case of variuos kind of error.
   *
   * @param statusCode TStatusCode
   * @param explanation String
   * @return outputData SpaceResOutputData
   */
  
  private ReserveSpaceOutputData manageError(TStatusCode statusCode, String explanation ) { 
      TReturnStatus status = null;
      try {
          status = new TReturnStatus(statusCode,explanation);
      } catch (InvalidTReturnStatusAttributeException ex1) {
          log.warn("SpaceManger: Error creating returnStatus " + ex1);
      }
      
     
      return new ReserveSpaceOutputData(status);
  }
  
  private TReturnStatus manageErrorStatus(TStatusCode statusCode, String explanation ) { 
      TReturnStatus status = null;
      try {
          status = new TReturnStatus(statusCode,explanation);
      } catch (InvalidTReturnStatusAttributeException ex1) {
          log.warn("SpaceManger: Error creating returnStatus " + ex1);
      }
      return status;
  }

  /**
   * This function is use to create updata data 
   * in case of variuos kind of error.
   * This function hadle also rollback cases.
   *
   * @param statusCode TStatusCode
   * @param explanation String
   * @param rollbackLevel int
   * @return outputData SpaceResOutputData
   */
  
  private ReserveSpaceOutputData manageError(TStatusCode statusCode, String explanation, int rollbackLevel, LocalFile spaceFile ) { 

      if(rollbackLevel==1) {
          /**
           * @todo Error putting ACL. Remove Space File?
           */
          File physicalSpaceFile = new File(spaceFile.getPath());
          physicalSpaceFile.delete();
          
      }
      if(rollbackLevel==2) {
          /**
           * @todo Error adding data into Persistence. Remove Space File?
           */
          File physicalSpaceFile = new File(spaceFile.getPath());
          physicalSpaceFile.delete();
          
      }
      
      return manageError(statusCode, explanation);
  }
  
}
