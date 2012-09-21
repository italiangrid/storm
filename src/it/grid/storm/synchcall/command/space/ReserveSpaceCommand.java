/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.synchcall.command.space;

import it.grid.storm.acl.AclManager;
import it.grid.storm.acl.AclManagerFSAndHTTPS;
import it.grid.storm.catalogs.InvalidSpaceDataAttributesException;
import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.ReservationException;
import it.grid.storm.filesystem.Space;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.naming.NamespaceUtil;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TransferObjectDecodingException;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.InvalidTSpaceTokenAttributesException;
import it.grid.storm.srm.types.TAccessLatency;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRetentionPolicy;
import it.grid.storm.srm.types.TRetentionPolicyInfo;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TSpaceType;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.SpaceCommand;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.exception.InvalidReserveSpaceOutputDataAttributesException;
import it.grid.storm.synchcall.data.space.ReserveSpaceInputData;
import it.grid.storm.synchcall.data.space.ReserveSpaceOutputData;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * @author lucamag
 * @author Riccardo Zappi
 * @date May 29, 2008
 * 
 */
public class ReserveSpaceCommand extends SpaceCommand implements Command {

    private ReservedSpaceCatalog catalog;
    private static final Logger log = LoggerFactory.getLogger(ReserveSpaceCommand.class);
    private NamespaceInterface namespace;

    private static final boolean SUCCESS = true;
    private static final boolean FAILURE = false;

    TStatusCode statusCode = TStatusCode.EMPTY;
    String explanation = null;
    
    private String formatLogMessage(boolean success, GridUserInterface user,
            TSizeInBytes desSize, TSizeInBytes guarSize,
            TLifeTimeInSeconds lifetime, TRetentionPolicyInfo rpinfo,
            TStatusCode code, String expl) {

        TReturnStatus status = null;
        try {
            status = new TReturnStatus(code, expl);
        } catch (InvalidTReturnStatusAttributeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return formatLogMessage(success, user, desSize, guarSize, lifetime,
                rpinfo, status);
    }

    private String formatLogMessage(boolean success, GridUserInterface user,
            TSizeInBytes desSize, TSizeInBytes guarSize,
            TLifeTimeInSeconds lifetime, TRetentionPolicyInfo rpinfo,
            TReturnStatus status) {
        StringBuffer buf = new StringBuffer("srmReserveSpace: ");
        buf.append("<" + user + "> ");
        buf.append("Request for [");
        buf.append("desiredSizeOfTotalSpace: " + desSize);
        buf.append(", desiredSizeOfGuaranteedSpace: " + guarSize);
        buf.append("] ");
        buf.append("with [desiredLifetimeOfReservedSpace: " + lifetime + "] ");
        buf.append("with [retentionPolicyInfo: " + rpinfo + "] ");

        if (success) {
            buf.append("successfully done with:[status:");
        } else {
            buf.append("failed with:[status:");
        }

        buf.append(status);

        buf.append("]");

        return buf.toString();

    }

    public ReserveSpaceCommand()
    {
        namespace = NamespaceDirector.getNamespace();
        catalog = new ReservedSpaceCatalog();
    }

    
    /**
     * Method that provide space reservation for srmReserveSpace request.
     * 
     * @param data
     *            Contain information about data procived in SRM request.
     * @return SpaceResOutputData that contain all SRM return parameter.
     * @todo Implement this it.grid.storm.synchcall.space.SpaceManager method
     */
    public OutputData execute(InputData indata) {

        ReserveSpaceInputData data = (ReserveSpaceInputData) indata;

        log.debug("<SpaceReservationManager>:reserveSpace start.");

        if (!checkParameters(data))
        {
            return manageError(statusCode, explanation);
        }
        
        String spaceFN = null;
        try
        {
            spaceFN = getSpaceFN(data.getUser());
        } catch(Exception e)
        {
            log.error(formatLogMessage(FAILURE, data.getUser(), data.getDesiredSize(),
                                       data.getGuaranteedSize(), data.getLifetime(),
                                       data.getRetentionPolicyInfo(), statusCode, explanation));
            return manageError(statusCode, explanation);
        }

        VirtualFSInterface vfs = null;
        try
        {
            vfs = getSpaceVFS(spaceFN);
        } catch(Exception e)
        {
            log.error(formatLogMessage(FAILURE, data.getUser(), data.getDesiredSize(),
                                       data.getGuaranteedSize(), data.getLifetime(),
                                       data.getRetentionPolicyInfo(), statusCode, explanation));
            return manageError(statusCode, explanation);
        }
        
        setDefaults(data, vfs);
        
        String relativeSpaceFN = null;
        try
        {
            relativeSpaceFN = getRelativeSpaceFilePath(vfs, spaceFN);
        } catch(Exception e)
        {
            log.error(formatLogMessage(FAILURE, data.getUser(), data.getDesiredSize(),
                                       data.getGuaranteedSize(), data.getLifetime(),
                                       data.getRetentionPolicyInfo(), statusCode, explanation));
            return manageError(statusCode, explanation);
        }
        
        SpaceSize spaceSize = null;
        try
        {
            spaceSize = computeSpaceSize(data.getDesiredSize(), data.getGuaranteedSize(), vfs);
        } catch(Exception e)
        {
            log.error(formatLogMessage(FAILURE, data.getUser(), data.getDesiredSize(),
                                       data.getGuaranteedSize(), data.getLifetime(),
                                       data.getRetentionPolicyInfo(), statusCode, explanation));
            return manageError(statusCode, explanation);
        }

       
        StoRI spaceStori = null;
        try
        {
            spaceStori = getSpaceStoRI(vfs, relativeSpaceFN, spaceSize.getDesiderataSpaceSize());
        } catch(Exception e)
        {
            log.error(formatLogMessage(FAILURE, data.getUser(), data.getDesiredSize(),
                                       data.getGuaranteedSize(), data.getLifetime(),
                                       data.getRetentionPolicyInfo(), statusCode, explanation));
            return manageError(statusCode, explanation);
        }
        
        log.debug("Reserve Space File Size :" + spaceSize.getDesiderataSpaceSize().toString());
        try
        {
            /*
             * here the file is created, (note that since there is no chance to space is reserved)
             * reclaim allocated space from gpfs, space is never allocated!)
             * errors from here on must revert this operation
             */
            spaceStori.getSpace().fakeAllot();
        } catch(ReservationException e)
        {
            log.debug("Space reservation fail at FS level" + e);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Unable to create Space File into filesystem. \n";
            log.error(formatLogMessage(FAILURE, data.getUser(), data.getDesiredSize(), data.getGuaranteedSize(),
                                       data.getLifetime(), data.getRetentionPolicyInfo(), statusCode,
                                       explanation));
            return manageError(statusCode, explanation);
        }
        
        try
        {
            setSpaceFilePermissions(spaceStori,data.getUser());
        } catch(Exception e)
        {
            log.error(formatLogMessage(FAILURE, data.getUser(), data.getDesiredSize(),
                                       data.getGuaranteedSize(), data.getLifetime(),
                                       data.getRetentionPolicyInfo(), statusCode, explanation));
            revertAllocation(spaceStori.getSpace());
            return manageError(statusCode, explanation);
        }
        
        TSpaceToken spaceToken = null;
        try
        {
            spaceToken = registerIntoDB(data.getUser(), data.getSpaceTokenAlias(), spaceSize.getTotalSize(), spaceSize.getDesiderataSpaceSize(),
                                        data.getLifetime(), spaceStori.getPFN());
        } catch(Exception e)
        {
            log.error(formatLogMessage(FAILURE, data.getUser(), data.getDesiredSize(),
                                       data.getGuaranteedSize(), data.getLifetime(),
                                       data.getRetentionPolicyInfo(), statusCode, explanation));
            revertAllocation(spaceStori.getSpace());
            return manageError(statusCode, explanation);
        }
        
        ReserveSpaceOutputData output = null;
        try
        {
            output = buildOutput(spaceSize, spaceToken, data.getLifetime());
            log.info(formatLogMessage(SUCCESS, data.getUser(), data.getDesiredSize(), data.getGuaranteedSize(),
                                      data.getLifetime(), data.getRetentionPolicyInfo(), output.getStatus()));
        } catch(Exception e)
        {
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Unable to build a valid output object ";
            log.error(formatLogMessage(FAILURE, data.getUser(), data.getDesiredSize(),
                                       data.getGuaranteedSize(), data.getLifetime(),
                                       data.getRetentionPolicyInfo(), statusCode, explanation));
            revertAllocation(spaceStori.getSpace());
            return manageError(statusCode, explanation);
        }
        return output;
    }
    
    private void revertAllocation(Space space)
    {
        try
        {
            space.fakeRelease();
        } catch(ReservationException e)
        {
            log.error("Error releasing space: ReservationException" + e.getMessage());
        }
    }

    private StoRI getSpaceStoRI(VirtualFSInterface vfs, String relativeSpaceFN,
            TSizeInBytes desiderataSpaceSize) throws Exception
    {
        StoRI spaceFile = null;
        try
        {
            spaceFile = vfs.createSpace(relativeSpaceFN, desiderataSpaceSize.value());
        }
        catch (NamespaceException e)
        {
            log.debug("Unable to create Space File in VFS ", e);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Unable to create Space File in VFS \n" + e;
            throw new Exception(explanation);
        }
        return spaceFile;
    }

    private boolean checkParameters(ReserveSpaceInputData data){
        
        if (data == null)
        {
            explanation = "Invalid Parameter specified";
            statusCode = TStatusCode.SRM_FAILURE;
            log.error(formatLogMessage(FAILURE, null, null, null, null, null, statusCode, explanation));
            return false;
        }

        if (data.getUser() == null)
        {
            log.error("SpaceRes: Unable to get user credential. ");
            statusCode = TStatusCode.SRM_AUTHENTICATION_FAILURE;
            explanation = "Unable to get user credential!";
            log.error(formatLogMessage(FAILURE, null, data.getDesiredSize(), data.getGuaranteedSize(),
                                       data.getLifetime(), data.getRetentionPolicyInfo(), statusCode,
                                       explanation));
            return false;
        }

        if (data.getRetentionPolicyInfo() == null)
        {
            log.debug("SpaceRes: Invalid request, null TRetentionPolicyInfo specified");
            statusCode = TStatusCode.SRM_INVALID_REQUEST;
            explanation = "RetentionPolicy not specified.";
            log.error(formatLogMessage(FAILURE, data.getUser(), data.getDesiredSize(), data.getGuaranteedSize(),
                                       data.getLifetime(), data.getRetentionPolicyInfo(), statusCode,
                                       explanation));
            return false;
        }
        
        TAccessLatency latency = data.getRetentionPolicyInfo().getAccessLatency();
        TRetentionPolicy retentionPolicy = data.getRetentionPolicyInfo().getRetentionPolicy();
//      latency != Empty,Online --> fail
//      retentionPolicy != Empty,REPLICA  --> fail
        if (!((latency == null || latency.equals(TAccessLatency.EMPTY) || latency.equals(TAccessLatency.ONLINE)) && (retentionPolicy == null
                || retentionPolicy.equals(TRetentionPolicy.EMPTY) || retentionPolicy.equals(TRetentionPolicy.REPLICA))))
        {
            log.debug("SpaceRes: Invalid TRetentionPolicyInfo specified:  "
                    + data.getRetentionPolicyInfo().getAccessLatency().toString() + ","
                    + (data.getRetentionPolicyInfo().getRetentionPolicy().toString()));
            statusCode = TStatusCode.SRM_NOT_SUPPORTED;
            explanation = "RetentionPolicy requested cannot be satisfied.";
            log.error(formatLogMessage(FAILURE, data.getUser(), data.getDesiredSize(), data.getGuaranteedSize(),
                                       data.getLifetime(), data.getRetentionPolicyInfo(), statusCode,
                                       explanation));
            return false;
        }
        return true;
    }
    
    
    private String getSpaceFN(GridUserInterface user) throws Exception
    {
        String spaceFN = null;
        try
        {
            spaceFN = namespace.makeSpaceFileURI(user);
            log.debug(" Space FN : " + spaceFN);
        }
        catch (NamespaceException ex)
        {
            log.error("Unable to build default Space FN ", ex);
            statusCode = TStatusCode.SRM_INVALID_REQUEST;
            explanation = "Unable to build default Space FN \n" + ex;
            throw new Exception(explanation);
        }
        return spaceFN;
    }
    
    private VirtualFSInterface getSpaceVFS(String spaceFN) throws Exception{
        
        VirtualFSInterface vfs = null;
        try
        {
            vfs = namespace.resolveVFSbyAbsolutePath(spaceFN);
            log.debug("Space File belongs to VFS : " + vfs.getAliasName());
        }
        catch (NamespaceException ex2)
        {
            log.debug("Unable to resolve VFS ", ex2);
            statusCode = TStatusCode.SRM_INVALID_REQUEST;
            explanation = "Unable to resolve VFS \n" + ex2;
            throw new Exception(explanation);
        }

        return vfs;
    }
    
    private void setDefaults(ReserveSpaceInputData data, VirtualFSInterface vfs)
    {
        if (data.getRetentionPolicyInfo().getAccessLatency() == null
                || data.getRetentionPolicyInfo().getAccessLatency().equals(TAccessLatency.EMPTY))
        {
            data.getRetentionPolicyInfo().setAccessLatency(TAccessLatency.ONLINE);
        }
        if (data.getRetentionPolicyInfo().getRetentionPolicy() == null
                || data.getRetentionPolicyInfo().getRetentionPolicy().equals(TRetentionPolicy.EMPTY))
        {
            data.getRetentionPolicyInfo().setRetentionPolicy(TRetentionPolicy.REPLICA);
        }
        if (data.getLifetime().isEmpty())
        {
            log.debug("LifeTime is EMPTY. Using default value.");
            data.setLifetime(vfs.getDefaultValues().getDefaultSpaceLifetime());
        }
    }
    
    private SpaceSize computeSpaceSize(TSizeInBytes totalSize, TSizeInBytes guarSize, VirtualFSInterface vfs) throws Exception
    {
        
     // ************ RETRIEVE THE SIZEs OF SPACE ********************
        TSizeInBytes desiderataSpaceSize = TSizeInBytes.makeEmpty();

        // Malformed request check: if totalSize and guaranteedSize are used
        // defined
        // and guaranteedSize > totalSize we consider the request as malformed
        if ((!(totalSize.isEmpty())) && (!((guarSize.isEmpty()) || guarSize.value() == 0)))
        {
            if (totalSize.value() < guarSize.value())
            {
                log.debug("Error: totalSize < guaranteedSize");
                statusCode = TStatusCode.SRM_INVALID_REQUEST;
                explanation = "Error: totalSize can not be greater then guaranteedSize";
                throw new Exception(explanation);
            }
        }
        else
        { // Assign default values if totalSize and guaranteedSize are
          // not defined
            if (!(totalSize.isEmpty()))
            {
                    guarSize = vfs.getDefaultValues().getDefaultGuaranteedSpaceSize();
                    if (totalSize.value() < guarSize.value())
                    {
                        guarSize = totalSize;
                    }
            }
            else
            {
                if (!((guarSize.isEmpty()) || guarSize.value() == 0))
                {
                        totalSize = vfs.getDefaultValues().getDefaultTotalSpaceSize();
                        if (totalSize.value() < guarSize.value())
                        {
                            totalSize = guarSize;
                            log.debug("GuaranteedSize greater than default total size!");
                        }
                }
                else
                {
                        totalSize = vfs.getDefaultValues().getDefaultTotalSpaceSize();
                        guarSize = vfs.getDefaultValues().getDefaultGuaranteedSpaceSize();
                    // totalSize must be greater than guaranteedSize the following
                    // check is to be sure
                    // that the default parameters are correctly set.
                    if (totalSize.value() < guarSize.value())
                    {
                        totalSize = guarSize;
                    }
                }
            }
        }

        log.debug("Parameter parsing end");
        /*
         * At this point either totalSize and guarSize contains significative
         * value. desiderataSpaceSize is setted to totalSize.
         */
        desiderataSpaceSize = totalSize;
        // This is valid because StoRM only reserve GUARANTEED space.
        guarSize = desiderataSpaceSize;

        TSizeInBytes freeSpace = null;
        try
        {
            freeSpace = TSizeInBytes.make(vfs.getFilesystem().getFreeSpace(), SizeUnit.BYTES);
        }
        catch (InvalidTSizeAttributesException e)
        {
            log.debug("Error while retrieving free Space in underlying Filesystem", e);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Error while retrieving free Space in underlying Filesystem \n" + e;
            throw new Exception(explanation);
        }
        catch (NamespaceException ex)
        {
            log.debug("Error while retrieving free Space in underlying Filesystem. Unable to retrieve FS Driver",
                      ex);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Error while retrieving free Space in underlying Filesystem. Unable to retrieve FS Driver \n"
                    + ex;
            throw new Exception(explanation);
        }

        /**
         * @todo Change here, also granted SpaceSize must be considered.
         */
        boolean lower_space = false;
        // If there is not enogh free space on storage
        if (freeSpace.value() < desiderataSpaceSize.value())
        {
            if (freeSpace.value() < guarSize.value())
            {
                // Not enough freespace
                log.debug("<SpaceResManager>:reserveSpace Not Enough Free Space on storage!");
                statusCode = TStatusCode.SRM_NO_FREE_SPACE;
                explanation = "SRM has not more free space.";
                throw new Exception(explanation);
            }
            else
            {
                // Enough free space to reserve granted space asked.
                desiderataSpaceSize = guarSize;
                lower_space = true;
            }
        }
        return this.new SpaceSize(desiderataSpaceSize, totalSize, lower_space);
    }
    
    private String getRelativeSpaceFilePath(VirtualFSInterface vfs, String spaceFN) throws Exception{
        
        String relativeSpaceFN = null;
        try
        {
            log.debug("ExtraceRelativeSpace: root:" + vfs.getRootPath() + " spaceFN:" + spaceFN);
            relativeSpaceFN = NamespaceUtil.extractRelativePath(vfs.getRootPath(), spaceFN);
            log.debug("relativeSpaceFN:" + relativeSpaceFN);
        }
        catch (NamespaceException ex3)
        {
            log.debug("Unable to retrieve the relative space file name ", ex3);
            statusCode = TStatusCode.SRM_INVALID_REQUEST;
            explanation = "Unable to retrieve the relative space file name \n" + ex3;
            throw new Exception(explanation);
        }
        return relativeSpaceFN;
    }
    
    private void setSpaceFilePermissions(StoRI spaceStori, GridUserInterface user) throws Exception
    {
        
        FilesystemPermission fp = FilesystemPermission.ReadWrite;

        AclManager manager = AclManagerFSAndHTTPS.getInstance();
        LocalFile localFile = spaceStori.getLocalFile();
        LocalUser localUser;
        try
        {
            localUser = user.getLocalUser();
        }
        catch (CannotMapUserException e)
        {
            log.debug("Unable to setting up the ACL ", e);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Unable to setting up the ACL ";
            throw new Exception(explanation);
        }
        if (localFile == null || localUser == null)
        {
            log.warn("Unable to setting up the ACL. Null value/s : LocalFile " + localFile + " , localUser "
                    + localUser);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Unable to setting up the ACL ";
            throw new Exception(explanation);
        }
        if (spaceStori.hasJustInTimeACLs())
        {
            // JiT Case
            log.debug("<SpaceResManager>:reserveSpace AddACL for FIle: " + spaceStori.getPFN() + "  " + "USER RW");
            try
            {
                manager.grantUserPermission(localFile, localUser, fp);
            }
            catch (IllegalArgumentException e)
            {
                log.error("Unable to grant user permission on space file. IllegalArgumentException: "
                        + e.getMessage());
                statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                explanation = "Unable to grant group permission on space file ";
                throw new Exception(explanation);
            }
        }
        else
        {
            // AoT Case
            log.debug("<SpaceResManager>:reserveSpace AddACL for FIle: " + spaceStori.getPFN() + "  " + "GROUP RW");
            try
            {
                manager.grantGroupPermission(localFile, localUser, fp);
            }
            catch (IllegalArgumentException e)
            {
                log.error("Unable to grant group permission on the space file. IllegalArgumentException: "
                        + e.getMessage());
                statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                explanation = "Unable to setting up the ACL ";
                throw new Exception(explanation);
            }
        }
    }
    
    private TSpaceToken registerIntoDB(GridUserInterface user, String spaceTokenAlias,
            TSizeInBytes totalSize, TSizeInBytes desiderataSpaceSize, TLifeTimeInSeconds lifeTime, PFN pfn) throws Exception
    {
        log.debug("-- Creating Storage Space Data ...");
        StorageSpaceData spaceData = null;
        try
        {
            //passing null TStorageSystemInfo, is not used even when added to the DB
            spaceData = new StorageSpaceData(user, TSpaceType.PERMANENT, spaceTokenAlias, totalSize,
                                             desiderataSpaceSize, lifeTime, null, new Date(), pfn);
        } catch(InvalidSpaceDataAttributesException e)
        {
            log.debug("Unable to create Storage Space Data", e);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Unable to create storage space data.";
            log.error(formatLogMessage(FAILURE, user,totalSize , desiderataSpaceSize,
                                       lifeTime, null, statusCode, explanation));
            throw new Exception(explanation);
        }

        spaceData.setUsedSpaceSize(TSizeInBytes.make(0, SizeUnit.BYTES));
        spaceData.setUnavailableSpaceSize(TSizeInBytes.make(0, SizeUnit.BYTES));
        spaceData.setReservedSpaceSize(desiderataSpaceSize);
        
        log.debug("Created space data: " + spaceData.toString());
        try
        {
            catalog.addStorageSpace(spaceData);
        } catch(DataAccessException e)
        {
            log.debug("Unable to register Storage Space Data into DB", e);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Unable to register Storage Space Data into DB.";
            log.error(formatLogMessage(FAILURE, user, totalSize,desiderataSpaceSize ,
                                       lifeTime, null, statusCode, explanation));
            throw new Exception(explanation);
        }
        
        TSpaceToken spaceToken = null;
        try
        {
            spaceToken = TSpaceToken.make(spaceData.getSpaceToken().toString());
        } catch(InvalidTSpaceTokenAttributesException e)
        {
            log.debug("Error creating Space Token . Critical error!" + e);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Unable to create space token.";
            log.error(formatLogMessage(FAILURE, user, totalSize, desiderataSpaceSize, lifeTime, null,
                                       statusCode, explanation));
            throw new Exception(explanation);
        }
        return spaceToken;
    }
    
    private ReserveSpaceOutputData buildOutput(SpaceSize spaceSize, TSpaceToken spaceToken,
            TLifeTimeInSeconds lifeTime) throws Exception
    {
        TReturnStatus status = null;
        try
        {
            if (!spaceSize.isLowerSpace())
            {
                status = new TReturnStatus(TStatusCode.SRM_SUCCESS, "Space Reservation done");

            }
            else
            {
                status = new TReturnStatus(TStatusCode.SRM_LOWER_SPACE_GRANTED,
                                           "Space Reservation done, lower space granted.");
            }
        } catch(InvalidTReturnStatusAttributeException e)
        {
            log.debug("InvalidTReturnStatusAttributeException", e);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Unable to build a valid return status ";
            throw new Exception(explanation);
        }

        ReserveSpaceOutputData outputData = null;
        try
        {
            outputData = new ReserveSpaceOutputData(spaceSize.getTotalSize(),
                                                    spaceSize.getDesiderataSpaceSize(), lifeTime, spaceToken,
                                                    status);
        } catch(InvalidReserveSpaceOutputDataAttributesException ex11)
        {
            log.error("Error creating Output DATA . Critical error!" + ex11);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Unable to build a valid return output object ";
            throw new Exception(explanation);
        }
        return outputData;
    }
    
    private class SpaceSize{
        
        private final TSizeInBytes desiderataSpaceSize;
        private final TSizeInBytes totalSize;
        private final boolean lowerSpace;
        
        public SpaceSize(TSizeInBytes desiderataSpaceSize, TSizeInBytes totalSize, boolean lowerSpace)
        {
            this.desiderataSpaceSize = desiderataSpaceSize;
            this.totalSize = totalSize;
            this.lowerSpace = lowerSpace;
        }
        
        protected TSizeInBytes getDesiderataSpaceSize()
        {
            return desiderataSpaceSize;
        }
        
        protected TSizeInBytes getTotalSize()
        {
            return totalSize;
        }
        
        protected boolean isLowerSpace()
        {
            return lowerSpace;
        }
    }
    
    /**
     * Method that reset an already done reservation to the original status.
     * 
     * @param token
     *            TSpaceToken that contains information about data procived in
     *            SRM request.
     * @return TReturnStatus that contains of all SRM return parameters.
     */
    public TReturnStatus resetReservation(TSpaceToken token) {

        String explanation = null;
        TStatusCode statusCode = TStatusCode.EMPTY;

        StorageSpaceData sdata;
        try
        {
            sdata = catalog.getStorageSpace(token);
        } catch(TransferObjectDecodingException e)
        {
            log.error("Unable to build StorageSpaceData from StorageSpaceTO. TransferObjectDecodingException: "
                      + e.getMessage());
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Error building space data from row DB data\n" + e;
            return manageErrorStatus(statusCode, explanation);
        } catch(DataAccessException e)
        {
            log.error("Unable to build get StorageSpaceTO. DataAccessException: " + e.getMessage());
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Error retrieving row space token data from DB\n" + e;
            return manageErrorStatus(statusCode, explanation);
        }
        
        // Check if it a VO_SA_Token, in that case do nothing
        if (sdata.getSpaceType().equals(TSpaceType.VOSPACE)) {
            return manageErrorStatus(TStatusCode.SRM_SUCCESS, "Abort file done.");
        }

        GridUserInterface user = sdata.getOwner();
        PFN spacePFN = sdata.getSpaceFileName();

        // Obtain the PFN of the user-root directory.

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
            log.debug("ExtractRelativeSpace: root:" + vfs.getRootPath()
                    + " spaceFN:" + spaceFN);
            relativeSpaceFN = NamespaceUtil.extractRelativePath(vfs
                    .getRootPath(), spaceFN);
            log.debug("relativeSpaceFN:" + relativeSpaceFN);
        } catch (NamespaceException ex3) {
            log.debug("Unable to retrieve the relative space file name ", ex3);
            statusCode = TStatusCode.SRM_INVALID_REQUEST;
            explanation = "Unable to retrieve the relative space file name \n"
                + ex3;
            return manageErrorStatus(statusCode, explanation);
        }

        // ************ RETRIEVE THE SIZEs OF SPACE from DB********************
        TSizeInBytes desiderataSpaceSize = sdata.getTotalSpaceSize();
        TSizeInBytes totalSize = sdata.getTotalSpaceSize();
        TSizeInBytes guarSize = sdata.getReservedSpaceSize();

        /**
         * Check free space on file system.
         */

        TSizeInBytes freeSpace;
        try {
            long bytesFree = vfs.getFilesystem().getFreeSpace();
            freeSpace = TSizeInBytes.make(bytesFree, SizeUnit.BYTES);
        } catch (InvalidTSizeAttributesException e) {
            log
            .debug(
                    "Error while retrieving free Space in underlying Filesystem",
                    e);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Error while retrieving free Space in underlying Filesystem \n"
                + e;
            return manageErrorStatus(statusCode, explanation);
        } catch (NamespaceException ex) {
            log
            .debug(
                    "Error while retrieving free Space in underlying Filesystem. Unable to retrieve FS Driver",
                    ex);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Error while retrieving free Space in underlying Filesystem. Unable to retrieve FS Driver \n"
                + ex;
            return manageErrorStatus(statusCode, explanation);
        }

        /*
         * Start with the reservation
         */
        boolean authorize = true;

        if (authorize) {

            // Call wrapper to reserve Space.
            log.debug("reserve Space File Size :"
                    + desiderataSpaceSize.toString());

            StoRI spaceFile = null;
            try {
                spaceFile = vfs.createSpace(relativeSpaceFN,
                        desiderataSpaceSize.value());
            } catch (NamespaceException ex4) {
                log.debug("Unable to create Space File in VFS ", ex4);
                statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                explanation = "Unable to create Space File in VFS \n" + ex4;
                return manageErrorStatus(statusCode, explanation);
            }

            // Create Space into FileSyste
            try {
                spaceFile.getSpace().allot();
            } catch (ReservationException e) {
                log.debug("Space reservation fail at FS level" + e);
                statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                explanation = "Unable to create Space File into filesystem. \n";
                return manageErrorStatus(statusCode, explanation);
            }

            // Call wrapper to set ACL on file created.
            /** Check for JiT or AoT */
            boolean hasJiTACL = spaceFile.hasJustInTimeACLs();

            FilesystemPermission fp = FilesystemPermission.ReadWrite;

            if (hasJiTACL) {
                // JiT Case
                log.debug("<SpaceResManager>:reserveSpace AddACL for FIle: "
                        + spacePFN + "  " + "USER RW");
                try {
                    AclManager manager = AclManagerFSAndHTTPS.getInstance();
                    // TODO ACL manager
                    LocalFile localFile = spaceFile.getLocalFile();
                    LocalUser localUser = user.getLocalUser();
                    if (localFile == null || localUser == null)
                    {
                        log.warn("Unable to setting up the ACL. Null value/s : LocalFile " + localFile
                                + " , localUser " + localUser);
                        statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                        explanation = "Unable to setting up the ACL ";
                        return manageErrorStatus(statusCode, explanation);
                    }
                    else
                    {
                        try
                        {
                            manager.grantGroupPermission(localFile, localUser, fp);
                        }
                        catch (IllegalArgumentException e)
                        {
                            log.error("Unable to grant group permission on space file. IllegalArgumentException: " + e.getMessage());
                            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                            explanation = "Unable to grant group permission on space file ";
                            return manageErrorStatus(statusCode, explanation);
                        }
                    }
//                    spaceFile.getLocalFile().grantUserPermission(
//                            user.getLocalUser(), fp);
                } catch (CannotMapUserException ex5) {
                    log.debug("Unable to setting up the ACL ", ex5);
                    statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                    explanation = "Unable to setting up the ACL ";
                    return manageErrorStatus(statusCode, explanation);
                }
            } else {
                // AoT Case
                log.debug("<SpaceResManager>:reserveSpace AddACL for FIle: "
                        + spacePFN + "  " + "GROUP RW");
                try {
                    AclManager manager = AclManagerFSAndHTTPS.getInstance();
                    //TODO ACL manager
                    LocalFile localFile = spaceFile.getLocalFile();
                    LocalUser localUser = user.getLocalUser();
                    if (localFile == null || localUser == null)
                    {
                        log.warn("Unable to setting up the ACL. Null value/s : LocalFile " + localFile
                                + " , localUser " + localUser);
                        statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                        explanation = "Unable to setting up the ACL ";
                        return manageErrorStatus(statusCode, explanation);
                    }
                    else
                    {
                        try
                        {
                            manager.grantGroupPermission(localFile, localUser, fp);
                        }
                        catch (IllegalArgumentException e)
                        {
                            log.error("Unable to grant group permission on space file. IllegalArgumentException: " + e.getMessage());
                            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                            explanation = "Unable to grant group permission on space file ";
                            return manageErrorStatus(statusCode, explanation);
                        }
                    }
//                    spaceFile.getLocalFile().grantGroupPermission(
//                            user.getLocalUser(), fp);
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
        // sdata.setSpaceFileName(spacePFN);
        sdata.setUsedSpaceSize(desiderataSpaceSize);

        // UpdateData into the ReserveSpaceCatalog
        try
        {
            catalog.updateStorageSpaceFreeSpace(sdata);
        } catch(DataAccessException e)
        {
            log.error("Unable to persist the space reservation into the DB. DataAccessException: " + e.getMessage());
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Error persisting space token data into the DB\n" + e.getMessage();
            return manageErrorStatus(statusCode, explanation);
        }

        return manageErrorStatus(TStatusCode.SRM_SUCCESS,
        "Successfull creation.");
    }

    /**
     * Method that update an already done reservation.
     * 
     * @param data
     *            Contain information about data procvided in a SRM request.
     * @return SpaceResOutputData that contain all SRM return parameter.
     */

    public TReturnStatus updateReservation(TSpaceToken token,
            TSizeInBytes sizeToAdd, TSURL toSurl) {

        String explanation = null;
        TStatusCode statusCode = TStatusCode.EMPTY;

        StorageSpaceData sdata;
        try
        {
            sdata = catalog.getStorageSpace(token);
        } catch(TransferObjectDecodingException e)
        {
            log.error("Unable to build StorageSpaceData from StorageSpaceTO. TransferObjectDecodingException: "
                      + e.getMessage());
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Error building space data from row DB data\n" + e;
            return manageErrorStatus(statusCode, explanation);
        } catch(DataAccessException e)
        {
            log.error("Unable to build get StorageSpaceTO. DataAccessException: " + e.getMessage());
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Error retrieving row space token data from DB\n" + e;
            return manageErrorStatus(statusCode, explanation);
        }
        GridUserInterface user = sdata.getOwner();
        PFN spacePFN = sdata.getSpaceFileName();

        // Check if it a VO_SA_Token, in that case do nothing
        if (sdata.getSpaceType().equals(TSpaceType.VOSPACE)) {
            return manageErrorStatus(TStatusCode.SRM_SUCCESS, "Abort file done.");
        }

        /**
         * Verification of the free Space available in file system
         */

        // Obtain the PFN of the user-root directory.
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
            log.debug("ExtractRelativeSpace: root:" + vfs.getRootPath()
                    + " spaceFN:" + spaceFN);
            relativeSpaceFN = NamespaceUtil.extractRelativePath(vfs
                    .getRootPath(), spaceFN);
            log.debug("relativeSpaceFN:" + relativeSpaceFN);
        } catch (NamespaceException ex3) {
            log.debug("Unable to retrieve the relative space file name ", ex3);
            statusCode = TStatusCode.SRM_INVALID_REQUEST;
            explanation = "Unable to retrieve the relative space file name \n"
                + ex3;
            return manageErrorStatus(statusCode, explanation);
        }

        // ************ RETRIEVE THE SIZEs OF SPACE from DB********************
        TSizeInBytes desiderataSpaceSize = sdata.getTotalSpaceSize();
        TSizeInBytes availableSize = sdata.getAvailableSpaceSize();

        log.debug("available Size : " + availableSize.value());
        log.debug("Size of removed file: " + sizeToAdd.value());

        /**
         * Add to the desiderata size the bytes freed from the abort
         */
        try {
            desiderataSpaceSize = TSizeInBytes.make(availableSize.value()
                    + sizeToAdd.value(), SizeUnit.BYTES);
        } catch (InvalidTSizeAttributesException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        /**
         * Check free space on file system.
         */

        //michele: what this code should do?
//        TSizeInBytes freeSpace;
//        try {
//            long bytesFree = vfs.getFilesystem().getFreeSpace();
//            freeSpace = TSizeInBytes.make(bytesFree, SizeUnit.BYTES);
//        } catch (InvalidTSizeAttributesException e) {
//            log
//            .debug(
//                    "Error while retrieving free Space in underlying Filesystem",
//                    e);
//            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
//            explanation = "Error while retrieving free Space in underlying Filesystem \n"
//                + e;
//            return manageErrorStatus(statusCode, explanation);
//        } catch (NamespaceException ex) {
//            log
//            .debug(
//                    "Error while retrieving free Space in underlying Filesystem. Unable to retrieve FS Driver",
//                    ex);
//            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
//            explanation = "Error while retrieving free Space in underlying Filesystem. Unable to retrieve FS Driver \n"
//                + ex;
//            return manageErrorStatus(statusCode, explanation);
//        }

        /*
         * Start with the reservation
         */
            // Call wrapper to reserve Space.
            log.debug("reserve Space File Size :"
                    + desiderataSpaceSize.toString());

            StoRI spaceFile = null;
            try {
                spaceFile = vfs.createSpace(relativeSpaceFN,
                        desiderataSpaceSize.value());
            } catch (NamespaceException ex4) {
                log.debug("Unable to create Space File in VFS ", ex4);
                statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                explanation = "Unable to create Space File in VFS \n" + ex4;
                return manageErrorStatus(statusCode, explanation);
            }

            // Remove the old spaceFile with not updated size
            LocalFile localFile = spaceFile.getLocalFile();

            if (localFile != null) {
                // Old spaceFile with wrong chunk
                localFile.delete();
            } else {
                // spaceFile does not exist yet
            }

            // Create Space into FileSyste
            try {
                spaceFile.getSpace().allot();
            } catch (ReservationException e) {
                log.debug("Space reservation fail at FS level" + e);
                revertOldSpaceFileDeletion(localFile);
                statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                explanation = "Unable to create Space File into filesystem. \n";
                return manageErrorStatus(statusCode, explanation);
            }

            // Call wrapper to set ACL on file created.
            /** Check for JiT or AoT */
            boolean hasJiTACL = spaceFile.hasJustInTimeACLs();

            FilesystemPermission fp = FilesystemPermission.ReadWrite;

            if (hasJiTACL) {
                // JiT Case
                log.debug("<SpaceResManager>:reserveSpace AddACL for FIle: "
                        + spacePFN + "  " + "USER RW");
                try {
                    AclManager manager = AclManagerFSAndHTTPS.getInstance();
                    // TODO ACL manager
                    localFile = spaceFile.getLocalFile();
                    LocalUser localUser = user.getLocalUser();
                    if (localFile == null || localUser == null)
                    {
                        log.warn("Unable to setting up the ACL. Null value/s : LocalFile " + localFile
                                + " , localUser " + localUser);
                        revertOldSpaceFileDeletion(localFile);
                        statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                        explanation = "Unable to setting up the ACL ";
                        return manageErrorStatus(statusCode, explanation);
                    }
                    else
                    {
                        try
                        {
                            manager.grantGroupPermission(localFile, localUser, fp);
                        }
                        catch (IllegalArgumentException e)
                        {
                            log.error("Unable to grant group permission on space file. IllegalArgumentException: " + e.getMessage());
                            revertOldSpaceFileDeletion(localFile);
                            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                            explanation = "Unable to grant group permission on space file ";
                            return manageErrorStatus(statusCode, explanation);
                        }
                    }
//                    spaceFile.getLocalFile().grantUserPermission(
//                            user.getLocalUser(), fp);
                } catch (CannotMapUserException ex5) {
                    log.debug("Unable to setting up the ACL ", ex5);
                    revertOldSpaceFileDeletion(localFile);
                    statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                    explanation = "Unable to setting up the ACL ";
                    return manageErrorStatus(statusCode, explanation);
                }
            } else {
                // AoT Case
                log.debug("<SpaceResManager>:reserveSpace AddACL for FIle: "
                        + spacePFN + "  " + "GROUP RW");
                try {
                    AclManager manager = AclManagerFSAndHTTPS.getInstance();
                    //TODO ACL manager
                    localFile = spaceFile.getLocalFile();
                    LocalUser localUser = user.getLocalUser();
                    if (localFile == null || localUser == null)
                    {
                        log.warn("Unable to setting up the ACL. Null value/s : LocalFile " + localFile
                                + " , localUser " + localUser);
                        revertOldSpaceFileDeletion(localFile);
                        statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                        explanation = "Unable to setting up the ACL ";
                        return manageErrorStatus(statusCode, explanation);
                    }
                    else
                    {
                        try
                        {
                            manager.grantGroupPermission(spaceFile.getLocalFile(), localUser, fp);
                        }
                        catch (IllegalArgumentException e)
                        {
                            log.error("Unable to grant group permission on space file. IllegalArgumentException: " + e.getMessage());
                            revertOldSpaceFileDeletion(localFile);
                            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                            explanation = "Unable to grant group permission on space file ";
                            return manageErrorStatus(statusCode, explanation);
                        }
                    }
//                    spaceFile.getLocalFile().grantGroupPermission(
//                            user.getLocalUser(), fp);
                } catch (CannotMapUserException ex5) {
                    log.debug("Unable to setting up the ACL ", ex5);
                    revertOldSpaceFileDeletion(localFile);
                    statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                    explanation = "Unable to setting up the ACL ";
                    return manageErrorStatus(statusCode, explanation);
                }
            }

        /*
         * Update data into DB
         */

        /*
         * TODO Update the StorageFile information related to the removed chunk.
         * The SURL has been associated during the Put operation to the
         * SpaceFile, due to an abort operation the SURL must be removed from
         * the StorageFile relation, and the size of the spaceFile have to be
         * updated.
         * 
         */
        //
        try {
            availableSize = TSizeInBytes.make(sdata.getAvailableSpaceSize().value()
                    + sizeToAdd.value(), SizeUnit.BYTES);
        } catch (InvalidTSizeAttributesException e) {
            log.error("Unable to compute new available space size. InvalidTSizeAttributesException: " + e.getMessage());
            revertOldSpaceFileDeletion(localFile);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Error computing new available space size\n" + e.getMessage();
            return manageErrorStatus(statusCode, explanation);
        }
        // Update the free bytes!
        sdata.forceAvailableSpaceSize(availableSize);

        // UpdateData into the ReserveSpaceCatalog
        try
        {
            catalog.updateStorageSpaceFreeSpace(sdata);
        } catch(DataAccessException e)
        {
            log.error("Unable to persist the space reservation into the DB. DataAccessException: " + e.getMessage());
            revertOldSpaceFileDeletion(localFile);
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            explanation = "Error persisting space token data into the DB\n" + e.getMessage();
            return manageErrorStatus(statusCode, explanation);
        }
        return manageErrorStatus(TStatusCode.SRM_SUCCESS, "Successfull creation.");
    }

    private void revertOldSpaceFileDeletion(LocalFile localFile)
    {
        // TODO Implement this method
    }

    /**
     * This function is use to create update data in case of various kind of
     * error.
     * 
     * @param statusCode
     *            TStatusCode
     * @param explanation
     *            String
     * @return outputData SpaceResOutputData
     */

    private ReserveSpaceOutputData manageError(TStatusCode statusCode, String explanation)
    {
        TReturnStatus status = null;
        try
        {
            status = new TReturnStatus(statusCode, explanation);
        }
        catch (InvalidTReturnStatusAttributeException ex1)
        {
            log.warn("SpaceManger: Error creating returnStatus " + ex1);
        }

        return new ReserveSpaceOutputData(status);
    }

    private TReturnStatus manageErrorStatus(TStatusCode statusCode, String explanation)
    {
        TReturnStatus status = null;
        try
        {
            status = new TReturnStatus(statusCode, explanation);
        }
        catch (InvalidTReturnStatusAttributeException ex1)
        {
            log.warn("SpaceManger: Error creating returnStatus " + ex1);
        }
        return status;
    }
}
