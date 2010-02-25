package it.grid.storm.synchcall.command.space;

import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.Quota;
import it.grid.storm.namespace.model.QuotaType;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.ArrayOfTMetaDataSpace;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TMetaDataSpace;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TSpaceType;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.SpaceCommand;
import it.grid.storm.synchcall.command.space.quota.GPFSQuotaCommand;
import it.grid.storm.synchcall.command.space.quota.GPFSQuotaParameters;
import it.grid.storm.synchcall.command.space.quota.QuotaException;
import it.grid.storm.synchcall.command.space.quota.QuotaInfoInterface;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.space.GetSpaceMetaDataInputData;
import it.grid.storm.synchcall.data.space.GetSpaceMetaDataOutputData;
import it.grid.storm.synchcall.data.space.InvalidGetSpaceMetaDataOutputAttributeException;

import java.util.ArrayList;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * This class represents the GetSpaceMetaDataManager Class. This class hava a
 * reseveSpace method that perform all operation nedded to satisfy a SRM space
 * release request.
 * 
 * @author lucamag
 * @date May 29, 2008
 * 
 */

public class GetSpaceMetaDataCommand extends SpaceCommand implements Command {

    private ReservedSpaceCatalog catalog = null;

    private static final boolean SUCCESS = true;
    private static final boolean FAILURE = false;
    private static final boolean GLOBALSTATUS = true;
    private static final boolean LOCALSTATUS = false;

    /**
     * Constructor. Bind the Executor with ReservedSpaceCatalog
     */

    public GetSpaceMetaDataCommand() {
        catalog = new ReservedSpaceCatalog();
    }

    /**
     * 
     * @param data
     *            GetSpaceMetaDataInputData
     * @return GetSpaceMetaDataOutputData
     */
    public OutputData execute(InputData indata) {
        log.debug("<GetSpaceMetaData Start!>");

        GetSpaceMetaDataInputData data = (GetSpaceMetaDataInputData) indata;

        int errorCount = 0;
        int sizeBulkRequest = data.getSpaceTokenArray().size();

        GetSpaceMetaDataOutputData response = null;
        ArrayOfTMetaDataSpace arrayData = new ArrayOfTMetaDataSpace();
        TReturnStatus globalStatus = null;

        TSpaceToken token;
        TMetaDataSpace metadata = null;

        // For each Space Token retrieve MetaData info
        for (int i = 0; i < sizeBulkRequest; i++) {

            // Retrieve entry from Space Catalog corresponding to the TOKEN
            token = data.getSpaceToken(i);
            metadata = catalog.getMetaDataSpace(token);

            if (metadata == null) { // There are some problems...
                errorCount++;
                metadata = TMetaDataSpace.makeEmpty();
                metadata.setSpaceToken(token);
                TReturnStatus status = null;
                try {
                    status = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "Space token not valid");
                } catch (InvalidTReturnStatusAttributeException e) {
                    log.debug("dataTransferManger: Error creating returnStatus " + e);
                }
                metadata.setStatus(status);
                log.error(formatLogMessage(SUCCESS, LOCALSTATUS, data.getUser(), token, null, status));
            } else { // Retrieved with success MetaData from the catalog

                // Check if it is a VOSpaceToken, that is STATIC SPACE
                // RESERVATION
                if (metadata.getSpaceType().equals(TSpaceType.VOSPACE)) {
                    TSpaceToken tk = metadata.getSpaceToken();
                    log.debug("srmGetSpaceMetaData: Space Token '" + tk
                            + "' is pointing to a STATIC space reservation.");
                    log.debug("srmGetSpaceMetaData: going to execute Quota Command for SA with token '" + tk + "'");
                    QuotaInfoInterface quotaInfo = null;
                    try {
                        // Retrieve MetaData Info from the execution of Quota
                        // Command
                        quotaInfo = retrieveInfoFromQuota(token);
                        // Update METADATA result with QuotaInfo

                        // Setting UNUSED SIZE
                        long freeSpace = quotaInfo.getBlockSoftLimit() - quotaInfo.getBlockUsage();
                        try {
                            TSizeInBytes unused = (TSizeInBytes.make(freeSpace, quotaInfo.getSizeUnit()));
                            long unusedD = new Double(unused.getSizeIn(SizeUnit.BYTES)).longValue();
                            metadata.setUnSize(TSizeInBytes.make(unusedD, SizeUnit.BYTES));
                        } catch (InvalidTSizeAttributesException ex2) {
                            log.error("srmGetSpaceMetaData: freeSpace (" + freeSpace
                                    + " KBytes) returned by Quota is wrong.");
                            log.error("srmGetSpaceMetaData: QuotaInfo returned was: " + quotaInfo);
                        }

                        // Setting TOTAL SIZE
                        try {
                            TSizeInBytes totalSize =
                                    (TSizeInBytes.make(quotaInfo.getBlockSoftLimit(), quotaInfo.getSizeUnit()));
                            long totalSizeL = new Double(totalSize.getSizeIn(SizeUnit.BYTES)).longValue();
                            metadata.setTotalSize(TSizeInBytes.make(totalSizeL, SizeUnit.BYTES));
                        } catch (InvalidTSizeAttributesException ex3) {
                            log.error("srmGetSpaceMetaData: TotalSize (" + quotaInfo.getBlockSoftLimit()
                                    + " KBytes) returned by Quota is wrong.");
                            log.error("srmGetSpaceMetaData: QuotaInfo returned was: " + quotaInfo);
                        }

                        // Setting GUARANTEED SIZE
                        try {
                            TSizeInBytes totalSize =
                                    (TSizeInBytes.make(quotaInfo.getBlockSoftLimit(), quotaInfo.getSizeUnit()));
                            long totalSizeL = new Double(totalSize.getSizeIn(SizeUnit.BYTES)).longValue();
                            metadata.setGuarSize(TSizeInBytes.make(totalSizeL, SizeUnit.BYTES));
                        } catch (InvalidTSizeAttributesException ex3) {
                            log.error("srmGetSpaceMetaData: GuaranteedSize (" + quotaInfo.getBlockSoftLimit()
                                    + " KBytes) returned by Quota is wrong.");
                            log.error("srmGetSpaceMetaData: QuotaInfo returned was: " + quotaInfo);
                        }

                    } catch (QuotaException qe) {
                        log.error("srmGetSpaceMetaData: Unable to complete Quota Command." + qe.getMessage());
                    }
                } else {
                    log.debug("srmGetSpaceMetaData: Space Token '" + metadata.getSpaceToken()
                            + "' is pointing to a dynamic space reservation.");

                    // Into the TMetaDataSpace constructor (from the SpaceData
                    // object retrieved from catalog)
                    // is setted the correct status for the request.
                    // In case of lifetime expired , SRM_SPACE_LIFETIME_EXPIRED
                    // is setted,
                    // otherwise SRM_SUCCESS.
                    /**
                     * @todo : Above description of todo.
                     */
                }
            }
            arrayData.addTMetaDataSpace(metadata);

        }

        boolean requestSuccess = (errorCount == 0);
        boolean requestFailure = (errorCount == sizeBulkRequest);

        // Create Global Status Response
        try {
            if (requestSuccess) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS, "");
                log.info(formatLogMessage(SUCCESS,
                                          GLOBALSTATUS,
                                          data.getUser(),
                                          null,
                                          data.getSpaceTokenArray(),
                                          globalStatus));
            } else if (requestFailure) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "No valid space tokens");
                log.error(formatLogMessage(FAILURE,
                                           GLOBALSTATUS,
                                           data.getUser(),
                                           null,
                                           data.getSpaceTokenArray(),
                                           globalStatus));
            } else {
                globalStatus =
                        new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS, "Check space tokens statuses for details");
                log.error(formatLogMessage(SUCCESS,
                                           GLOBALSTATUS,
                                           data.getUser(),
                                           null,
                                           data.getSpaceTokenArray(),
                                           globalStatus));
            }
        } catch (InvalidTReturnStatusAttributeException ex) {
            log.error("srmGetSpaceMetaData: Impossible is happen!!", ex);
            return new GetSpaceMetaDataOutputData();
        }

        log.debug("<GetSpaceMetaData > all value retrived...");
        try {
            response = new GetSpaceMetaDataOutputData(globalStatus, arrayData);
        } catch (InvalidGetSpaceMetaDataOutputAttributeException ex1) {
            log.error("srmGetSpaceMetaData: Unable to build Output", ex1);
        }
        return response;
    }

    /**
     * 
     * @param token
     *            TSpaceToken
     * @return QuotaInfoInterface
     * @throws QuotaException
     */
    private QuotaInfoInterface retrieveInfoFromQuota(TSpaceToken token) throws QuotaException {
        QuotaInfoInterface result = null;
        // Retrieve the VFS from Root stored in metadata
        StorageSpaceData space = catalog.getStorageSpace(token);
        PFN rootPFN = space.getSpaceFileName();
        VirtualFSInterface vfs = null;
        String vfsName = "N/A";
        String fsType = "Unknown";
        try {
            vfs = NamespaceDirector.getNamespace().resolveVFSbyPFN(rootPFN);
            vfsName = vfs.getAliasName();
            fsType = vfs.getFSType();
        } catch (NamespaceException ex1) {
            log.error("srmGetSpaceMetaData: Unable to retrieve the Storage Area (VFS) with Root :" + rootPFN, ex1);
            throw new QuotaException("VFS with root '" + rootPFN + "' does not exists in Namespace");
        }

        // Retrieve the Quota Information bound with the VFS retrieved
        Quota quota;
        try {
            quota = vfs.getCapabilities().getQuota();
            log.debug("srmGetSpaceMetaData: Retrieved Quota from VFS ('" + vfsName + "'):" + quota);
            // Check if Quota is defined
            if (quota == null) {
                throw new QuotaException("Quota Element is not defined for the VFS :" + vfsName);
            }
        } catch (NamespaceException ex2) {
            log.error("srmGetSpaceMetaData: Unable to retrieve the Quota Info from the VFS :" + vfsName, ex2);
            throw new QuotaException("Unable to retrieve the Quota Info from the VFS :" + vfsName);
        }

        /**
         * QUOTA COMMAND EXECUTION
         */

        // Create the Quota Command
        if (!(fsType.toLowerCase().equals("gpfs"))) {
            // File System is not GPFS
            log.warn("srmGetSpaceMetaData: Quota command is enabled only on GPFS filesystem." + " VFS (" + vfsName
                    + ") is " + fsType + " type");
            throw new QuotaException("Unable to execute a quota command for FS Type = '" + fsType + "'.");
        } else {
            // Check if the quota is enabled
            boolean quotaEnabled = quota.getEnabled();
            if (!(quotaEnabled)) {
                log.debug("QUOTA:" + quota);
                throw new QuotaException("Unable to execute a quota command because Quota is DISABLED");
            }

            // Check if QuotaID is fileset and retrieve Fileset Value (param1)
            String param1 = null;
            int quotaT = quota.getQuotaType().getOrdinalNumber();
            switch (quotaT) {
                case 0: //FileSet
                    param1 = "-j " + quota.getQuotaType().getValue();
                    break;
                case 1: // User
                    param1 = "-u " + quota.getQuotaType().getValue();
                    break;
                case 2: // Group
                    param1 = "-g " + quota.getQuotaType().getValue();
                    break;
                default:
                    throw new QuotaException("Unable to execute a quota command because Quota Type '"
                            + QuotaType.string(quotaT) + "' is not supported");
            }

            // Retrieve Device
            String param2 = null;
            param2 = quota.getDevice();

            GPFSQuotaCommand quotaCommand = new GPFSQuotaCommand();
            ArrayList<String> params = new ArrayList<String>();

            params.add(0, param1);
            params.add(1, param2);
            GPFSQuotaParameters quotaParameters = new GPFSQuotaParameters(params);

            result = quotaCommand.executeGetQuotaInfo(quotaParameters);

        }
        return result;
    }

    /**
     * 
     * @param success
     *            boolean
     * @param globalStatus
     *            boolean
     * @param user
     *            GridUserInterface
     * @param token
     *            TSpaceToken
     * @param arrayOfToken
     *            ArrayOfTSpaceToken
     * @param status
     *            TReturnStatus
     * @return String
     */
    private String formatLogMessage(boolean success, boolean globalStatus, GridUserInterface user, TSpaceToken token,
            ArrayOfTSpaceToken arrayOfToken, TReturnStatus status) {
        StringBuffer buf = new StringBuffer("srmGetSpaceMetaData: ");
        buf.append("<" + user + "> ");
        buf.append("Request for [spacetoken:");
        if (!globalStatus) {
            buf.append(token);
        } else {
            buf.append(arrayOfToken);
        }
        buf.append("] ");
        if (success) {
            buf.append("successfully done with:[status:");
        } else {
            buf.append("failed with:[status:");
        }
        buf.append(status);
        buf.append("]");
        return buf.toString();
    }

}
