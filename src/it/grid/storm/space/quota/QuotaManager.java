package it.grid.storm.space.quota;

import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.info.SpaceInfoManager;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.Quota;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TSizeInBytes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QuotaManager {

    private static final Logger log = LoggerFactory.getLogger(QuotaManager.class);
    private static QuotaManager instance = new QuotaManager(); 
    
 //   private static Map<String,Quota> quotaMaps = new HashMap<String, Quota>();
    private int howmanyQuotas;
    
    //Private constructor - singleton
    private QuotaManager() {        
    }
    
    public static QuotaManager getInstance() {
        return instance;
    }
    
    
    /**
     * Populate the bind hashmap
     */
    public int updateSAwithQuota(boolean test) {
        int nrFailures = 0;
        List<String> quotaNames = SpaceInfoManager.getInstance().retrieveQuotaNamesToUse();
        log.debug("QuotaNames: {} ", quotaNames);
        
        List<VirtualFSInterface> vfsQuotas = SpaceInfoManager.getInstance().retrieveSAtoInitializeWithQuota();
        log.debug("vfs Quotas (size) : " + (vfsQuotas!=null?vfsQuotas.size():0));
        
        List<VirtualFSInterface> vfsQuotaEnabled = new ArrayList<VirtualFSInterface>();      
        
        //Execute Quota Command to retrieve info about used space
        GPFSLsQuotaCommand quotaCmd = new GPFSLsQuotaCommand();
        ArrayList<GPFSQuotaInfo> quotaResult = quotaCmd.executeGetQuotaInfo(test);
        
        //Update the result into the DB
        for (GPFSQuotaInfo gpfsQuotaInfoEntry : quotaResult) {
            if (gpfsQuotaInfoEntry.isFailure()) {
                nrFailures++;
            } else {
                String qName = gpfsQuotaInfoEntry.getQuotaEntryName();
                log.debug(".. evaluating QuotaEntryName : '"+qName+"'");
                if (quotaNames.contains(qName)) {
                    //Retrieve corresponding VFS
                    VirtualFSInterface vfsItem = retrieveCorrespondingVFS(vfsQuotas, qName);
                    if (vfsItem!=null) {
                        vfsQuotaEnabled.add(vfsItem);
                    }
                    // Elaborate gpfsQuotaInfoEntry;
                    StorageSpaceData ssd = SpaceInfoManager.getInstance().getSSDfromQuotaName(qName);
                    process(gpfsQuotaInfoEntry, ssd);
                } else {
                    log.debug("Found '"+qName+"' quota Name not corresponding to a Storage Area.");
                    nrFailures++;
                }    
            }
            
        }
        log.debug("vfs Enabled Quotas : "+ (vfsQuotaEnabled!=null?vfsQuotaEnabled.size():0));
        setHowmanyQuotas(vfsQuotaEnabled.size());
        
        return nrFailures;
    }
    
    
    private VirtualFSInterface retrieveCorrespondingVFS(List<VirtualFSInterface> vfsQuotas, String dName) {
        VirtualFSInterface result = null;
        for (VirtualFSInterface vfsItem : vfsQuotas) {
            try {
                if (vfsItem.getCapabilities().getQuota().getQuotaElementName().equals(dName)) {
                    result =  vfsItem;
                }
            }
            catch (NamespaceException e) {
                log.error("Unable to retrieve virtual file system list. NamespaceException : " + e.getMessage());
            }
        }
        return result;
    }

    
    /**
     * Method used to save data into Catalog
     * 
     * @param gpfsQuotaInfoEntry
     * @param ssd
     */
    private void process(GPFSQuotaInfo gpfsQuotaInfoEntry, StorageSpaceData ssd) {
        long usedSize = gpfsQuotaInfoEntry.getCurrentBlocksUsage();
        log.debug("Used size for '"+gpfsQuotaInfoEntry.getQuotaEntryName()+"' is "+usedSize+" KB.");
        try {
            //Convert in BYTES
            usedSize = usedSize * 1024;
            TSizeInBytes us = TSizeInBytes.make(usedSize, SizeUnit.BYTES);
            ssd.setUsedSpaceSize(us);
        } catch (InvalidTSizeAttributesException e) {
            log.error("Negative size?");
        }        
        log.debug("Saving updated Used Size into DB ... ");
        ReservedSpaceCatalog spaceCatalog = new ReservedSpaceCatalog();
        spaceCatalog.updateStorageSpace(ssd);
        log.debug("... saved. ");
    }
    

    
    
//    public void addStorageAreaWithQuota(VirtualFSInterface vfsItem) {
//        String alias = null;
//        Quota quotaElement = null;
//        if (vfsItem!=null) {
//            try {
//                quotaElement = vfsItem.getCapabilities().getQuota();
//                alias = vfsItem.getAliasName();
//            }
//            catch (NamespaceException e) {
//                log.warn("Unable to retrieve capability element of Namespace.");
//            }
//            if (quotaElement!=null) {
//                boolean enabled = quotaElement.getEnabled();
//                if (enabled) {
//                    quotaMaps.put(alias, quotaElement);
//                }
//            }    
//        }
//    }

    /**
     * @param howmanyQuotas the howmanyQuotas to set
     */
    public void setHowmanyQuotas(int howmanyQuotas) {
        this.howmanyQuotas = howmanyQuotas;
    }

    /**
     * @return the howmanyQuotas
     */
    public int getHowmanyQuotas() {
        return howmanyQuotas;
    }
    
    
}
