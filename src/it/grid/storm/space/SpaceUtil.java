package it.grid.storm.space;

import it.grid.storm.catalogs.InvalidRetrievedDataException;
import it.grid.storm.catalogs.MultipleDataEntriesException;
import it.grid.storm.catalogs.NoDataFoundException;
import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.Quota;
import it.grid.storm.namespace.model.QuotaType;
import it.grid.storm.space.quota.GPFSRepQuotaCommand;
import it.grid.storm.space.quota.GPFSQuotaParameters;
import it.grid.storm.space.quota.QuotaException;
import it.grid.storm.space.quota.QuotaInfoInterface;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceUtil {

	private ReservedSpaceCatalog catalog = null;
	private Logger log = LoggerFactory.getLogger(SpaceUtil.class);
	
    public SpaceUtil() {
    	catalog = new ReservedSpaceCatalog();
    }

	
//	public StorageSpaceData getRealTimeSpaceData(TSpaceToken token) {
//	
//		StorageSpaceData storageSpaceData = catalog.getStorageSpace(token);
//		
//		//Check if it is needed an update using quota or other tool     		
//	    log.debug("Executing Quota Command for SA with token '" + token + "'");
//        QuotaInfoInterface quotaInfo = null;
//        try {
//            // Retrieve MetaData Info from the execution of Quota Command
//            quotaInfo = retrieveInfoFromQuota(token);
//            // Update METADATA result with QuotaInfo
//
//            
//            // Setting UNUSED SIZE
//            long freeSpace = quotaInfo.getBlockSoftLimit() - quotaInfo.getBlockUsage();
//            try {
//                TSizeInBytes unused = (TSizeInBytes.make(freeSpace, quotaInfo.getSizeUnit()));
//                long unusedD = new Double(unused.getSizeIn(SizeUnit.BYTES)).longValue();
//                storageSpaceData.setUsedSpaceSize(TSizeInBytes.make(unusedD, SizeUnit.BYTES));
//            } catch (InvalidTSizeAttributesException ex2) {
//                log.error("srmGetSpaceMetaData: freeSpace (" + freeSpace
//                        + " KBytes) returned by Quota is wrong.");
//                log.error("srmGetSpaceMetaData: QuotaInfo returned was: " + quotaInfo);
//            }
//
//            // Setting TOTAL SIZE and GUARANTEED SIZE
//            try {
//                TSizeInBytes totalSize =
//                        (TSizeInBytes.make(quotaInfo.getBlockSoftLimit(), quotaInfo.getSizeUnit()));
//                long totalSizeL = new Double(totalSize.getSizeIn(SizeUnit.BYTES)).longValue();
//                storageSpaceData.setTotalSpaceSize(TSizeInBytes.make(totalSizeL, SizeUnit.BYTES));
//                storageSpaceData.setReservedSpaceSize(TSizeInBytes.make(totalSizeL, SizeUnit.BYTES));
//            } catch (InvalidTSizeAttributesException ex3) {
//                log.error(" TotalSize (" + quotaInfo.getBlockSoftLimit()+ " KBytes) returned by Quota is wrong.");
//                log.error("QuotaInfo returned was: " + quotaInfo);
//            }
//
//            catalog.updateAllStorageSpace(storageSpaceData);
//            
//        } catch (QuotaException qe) {
//            log.error("srmGetSpaceMetaData: Unable to complete Quota Command." + qe.getMessage());
//        } catch (NoDataFoundException e) {
//        	log.error(""+e.getMessage());
//		} catch (InvalidRetrievedDataException e) {
//			log.error(""+e.getMessage());
//		} catch (MultipleDataEntriesException e) {
//			log.error(""+e.getMessage());
//		}
//        
//        
//        
//        return storageSpaceData;
//	}
	
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

            GPFSRepQuotaCommand quotaCommand = new GPFSRepQuotaCommand();
            ArrayList<String> params = new ArrayList<String>();

            params.add(0, param1);
            params.add(1, param2);
            GPFSQuotaParameters quotaParameters = new GPFSQuotaParameters(params);

            //Call the CallableGPFSQuota
            
           // result = quotaCommand.executeGetQuotaInfo(quotaParameters);

        }
        return result;
    }
	
}
