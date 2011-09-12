package it.grid.storm.info;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.types.InvalidPFNAttributeException;
import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.info.BackgroundDUTasks.BgDUTask;
import it.grid.storm.namespace.CapabilityInterface;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.Quota;
import it.grid.storm.space.CallableDU;
import it.grid.storm.space.DUResult;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.space.quota.QuotaManager;
import it.grid.storm.srm.types.InvalidTSpaceTokenAttributesException;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SpaceInfoManager {
    
    private static SpaceInfoManager instance = new SpaceInfoManager();
    private int timeOutDurationInSec = 7200; //2 hours per task (This value is multiplied by attempt)
    private BackgroundDU bDU;
    private static final Logger LOG = LoggerFactory.getLogger(SpaceInfoManager.class);
    private AtomicInteger tasksToComplete;
    private AtomicInteger tasksToSave;
    private AtomicInteger success;
    private AtomicInteger failures; 
    private AtomicInteger numberOfTasks;
    
    //Package variable used in testing mode (without DB)
    static AtomicBoolean testMode = new AtomicBoolean(false); 
    
    private static final int MaxAttempt = 2; 
    
    private static CatalogUpdater persist = new CatalogUpdater();
    //Reference to the Catalog
    private static final ReservedSpaceCatalog spaceCatalog = new ReservedSpaceCatalog();
   
    private static int quotas = 0;
    
    
    private SpaceInfoManager() {
    }
    
    private final BackgroundDUTasks bDUTasks = new BackgroundDUTasks();
	private int attempt = 1;

    public static SpaceInfoManager getInstance() {
        return instance;
    }

    public static boolean isInProgress() {
    	boolean result = false;
    	if (SpaceInfoManager.getInstance().tasksToComplete.get()>0) {
    		result = true;
    	}
    	return result;
    }
    
    
	public static boolean isInProgress(TSpaceToken spaceToken) {
		boolean result = false;
		if (SpaceInfoManager.getInstance().tasksToComplete.get() > 0) {
			BackgroundDUTasks tasks = SpaceInfoManager.getInstance().bDUTasks;
		
			// Looking for Task with the same SpaceToken passed as parameter 
			Collection<BgDUTask> ts = tasks.getTasks();
			for (BgDUTask bgDUTask : ts) {
				if (bgDUTask.getSpaceToken().equals(spaceToken)) {
					result = true;
				}
			}	
		}
		return result;
	}
    
	
	public static int updateSpaceUsed() {
	    int quotaFailures = 0;
	    quotaFailures = execGPFSQuota(false, true);
	    startBackGroundDU();
	    return quotaFailures;
	}
	
	public static int howManyBackgroundDU() {
	    return SpaceInfoManager.getInstance().bDUTasks.howManyTask();
	}
	
	public static int howManyQuotas() {
	    return SpaceInfoManager.getInstance().quotas;
	}
	       
    public static int execGPFSQuota(boolean test, boolean bootstrap) {
        int result = 0;
        boolean quotaPeriodicCheck = Configuration.getInstance().getQuotaPeriodicCheckEnabled();
        if (bootstrap) {
            QuotaManager.getInstance().updateSAwithQuota(test);
            quotas = QuotaManager.getInstance().getHowmanyQuotas();
            LOG.info("Executed '" + quotas + " mmlsquota in order to update related SAs");
        } else if (quotaPeriodicCheck) {
            QuotaManager.getInstance().updateSAwithQuota(test);
            quotas = QuotaManager.getInstance().getHowmanyQuotas();
            LOG.info("Executed '" + quotas + ". Periodic check");
        } else {
            LOG.debug("Quota Check is disabled.");
        }
        return result;
    }
	
	
	private static int startBackGroundDU() {
    	int result = 0;
		//This call populate the Task Queue: "bDUTasks"
		SpaceInfoManager.getInstance().foundSAtoAnalyze();
		result = SpaceInfoManager.getInstance().bDUTasks.howManyTask();
		LOG.debug(String.format("Tasks: %d", result));
		//Submit the tasks
		SpaceInfoManager.getInstance().submitTasks(SpaceInfoManager.getInstance().bDUTasks);
		return result;
	}

		
	
	public static int startTest(List<String> absPaths) {
        int result = 0;
        testMode = new AtomicBoolean(true); 
        SpaceInfoManager.getInstance().fakeSAtoAnalyze(absPaths);
        result = SpaceInfoManager.getInstance().bDUTasks.howManyTask();
        SpaceInfoManager.getInstance().submitTasks(SpaceInfoManager.getInstance().bDUTasks);
        return result;	    
	}
	
	
	public static int stop() {
	    int result = 0;
	    SpaceInfoManager.getInstance().stopExecution();
	    result = SpaceInfoManager.getInstance().failures.get();
	    return result;
	}
	
	
	//********************************************
	// Package methods
	//********************************************
	

    public List<StorageSpaceData> retrieveSSDtoInitializeWithQuota() {
        // Dispatch SA to compute in two categories: Quota and DU tasks
        List<StorageSpaceData> ssdSet = new ArrayList<StorageSpaceData>();
        List<VirtualFSInterface> vfsSet = retrieveSAtoInitializeWithQuota();	
        ReservedSpaceCatalog ssdCatalog = new ReservedSpaceCatalog();
        for (VirtualFSInterface vfsEntry : vfsSet) {
			try {
				String spaceTokenDesc = vfsEntry.getSpaceTokenDescription();
				StorageSpaceData ssd = ssdCatalog.getStorageSpaceByAlias(spaceTokenDesc);
				ssdSet.add(ssd);
			} catch (NamespaceException e) {
				LOG.error("Unable to retrieve virtual file system list. NamespaceException : " + e.getMessage());
			}
		}
        return ssdSet;
    }
	
    
    public StorageSpaceData getSSDfromQuotaName(String quotaName) {
        StorageSpaceData ssd = null;
        List<VirtualFSInterface> vfsList = retrieveSAtoInitializeWithQuota();
        ReservedSpaceCatalog ssdCatalog = new ReservedSpaceCatalog();
        for (VirtualFSInterface vfsEntry : vfsList) {
            try {
                String qName = vfsEntry.getCapabilities().getQuota().getQuotaElementName();
                if (qName.equals(quotaName)) {
                    String spaceTokenDesc = vfsEntry.getSpaceTokenDescription();
                    ssd = ssdCatalog.getStorageSpaceByAlias(spaceTokenDesc);
                }
            }  catch (NamespaceException e) {
                LOG.error("Unable to retrieve virtual file system list. NamespaceException : " + e.getMessage());
            }
        }
        return ssd;
    }
    
    
    public List<String> retrieveQuotaNamesToUse() {
        List<String> quotaNames = new ArrayList<String>();
        List<VirtualFSInterface> vfsList = retrieveSAtoInitializeWithQuota();
        for (VirtualFSInterface vfsEntry : vfsList) {
            try {
                LOG.debug("vfsEntry (AliasName): "+vfsEntry.getAliasName());
                String quotaName = vfsEntry.getCapabilities().getQuota().getQuotaElementName();
                LOG.debug("Found this quotaName to check: '"+quotaName+"'");
                quotaNames.add(quotaName);
            }  catch (NamespaceException e) {
                LOG.error("Unable to retrieve virtual file system list. NamespaceException : " + e.getMessage());
            }
        }
        LOG.debug("Number of quotaNames: " + quotaNames.size() );
        return quotaNames;
    }
	
    
    public List<VirtualFSInterface> retrieveSAtoInitializeWithQuota() {
        // Dispatch SA to compute in two categories: Quota and DU tasks
        List<VirtualFSInterface> vfsSet = getAllVFS();
        List<VirtualFSInterface> vfsSetQuota = new ArrayList<VirtualFSInterface>();
        if (vfsSet.size() > 0) { // Exists at least a VFS defined
            for (VirtualFSInterface vfsItem : vfsSet) {
                if (gpfsQuotaEnabled(vfsItem)) {
                    vfsSetQuota.add(vfsItem);
                }
            }
        }
        LOG.debug("Number of VFS with Quota enabled: "+vfsSetQuota.size());
        return vfsSetQuota;
    }
    
    
 
	
	private boolean gpfsQuotaEnabled(VirtualFSInterface vfsItem) {
	    boolean result = false;
	    if (vfsItem!=null) {
	        CapabilityInterface cap = null;
	        Quota quota = null;
	        String fsType = "Unknown";
            try {
                fsType = vfsItem.getFSType();
                if ( fsType != null) {
                    if (fsType.trim().toLowerCase().equals("gpfs")) {
                        cap = vfsItem.getCapabilities();
                        if (cap!=null) {
                            quota = cap.getQuota();    
                        }
                        if (quota!=null) {
                           result = ((quota.getDefined()) && (quota.getEnabled()));    
                        }        
                    }
                }
                
            }
            catch (NamespaceException e) {
                LOG.error("Unable to retrieve virtual file system list. NamespaceException : " + e.getMessage());
            }
	        
	    }
	    return result;
	}
	
	
	private List<VirtualFSInterface> getAllVFS() {
	    Collection<VirtualFSInterface> vfsCollection = null ;
        try {
            vfsCollection = NamespaceDirector.getNamespace().getAllDefinedVFS();
        }
        catch (NamespaceException e) {
            LOG.error("Unable to retrieve virtual file system list. NamespaceException : " + e.getMessage());
        }
        if (vfsCollection!=null) {
            vfsCollection = new ArrayList<VirtualFSInterface>(vfsCollection);    
        } else {
            vfsCollection = new ArrayList<VirtualFSInterface>();
        }
        LOG.debug("Found '"+vfsCollection.size()+"' VFS defined in Namespace.xml" );
	    return (List<VirtualFSInterface>) vfsCollection;
	}
    
    
	
    /**
     * Populate with DU tasks
     */
    private void fakeSAtoAnalyze(List<String> absPaths) {
        //Create a list of SSD using the list of AbsPaths
        List<StorageSpaceData> toAnalyze = new ArrayList<StorageSpaceData>();
        for (String path : absPaths) {
            path = path + File.separator;
            String pathNorm = FilenameUtils.normalize(FilenameUtils.getFullPath(path)); 
            StorageSpaceData ssd = new StorageSpaceData();
            try {
                PFN spaceFN = PFN.make(pathNorm);
                LOG.trace("PFN : "+spaceFN);
                ssd.setSpaceToken(TSpaceToken.makeGUID_Token());
                ssd.setSpaceFileName(spaceFN);
                toAnalyze.add(ssd);
            } catch (InvalidTSpaceTokenAttributesException e) {
                LOG.error("Unable to create Space Token. "+e);
            } catch (InvalidPFNAttributeException e) {
                LOG.error("Unable to create PFN. "+e);
            }
        }
        
        for (StorageSpaceData ssd : toAnalyze) {
            TSpaceToken sT = ssd.getSpaceToken();
            String absPath = ssd.getSpaceFileNameString();
            try {
                bDUTasks.addTask(sT, absPath);
                LOG.debug(String.format("Added %s to the DU-Task Queue. (Task queue size:%d)", absPath, bDUTasks.howManyTask()));
            } catch (SAInfoException e) {
                LOG.error("Unable to add task with '"+absPath+"' absolute path."+e.getMessage());
            }
        }
        LOG.info(String.format("Background DU tasks size: %d", bDUTasks.getTasks().size()));
    }
	
    /**
     * Populate with DU tasks
     */
    private void foundSAtoAnalyze() {
        List<StorageSpaceData> toAnalyze = spaceCatalog.getStorageSpaceNotInitialized();
        for (StorageSpaceData ssd : toAnalyze) {
            TSpaceToken sT = ssd.getSpaceToken();
            String absPath = ssd.getSpaceFileNameString();
            if (sT == null || absPath == null) {
                LOG.error("Unable to submit DU test, StorageSpaceData returns null values: SpaceToken=" + sT + " , SpaceFileNameString="+ absPath);
            } else {
                //SA with GPFS should be already initializated
                try  {
                    bDUTasks.addTask(sT, absPath);
                    LOG.debug("Added " + absPath + " to the DU-Task Queue. (size:" + bDUTasks.howManyTask() + ")");
                } catch (SAInfoException e) {
                    LOG.error("Unable to add task with '" + absPath + "' absolute path." + e.getMessage());
                }
            }
        }
    }

    
    void stopExecution() {
        bDU.stopExecution(true);
        persist.stopSaver();
        LOG.debug("Stopping Background DU executions.");       
    }
    
    /**
     * Used to update the DB if the Task is root, otherwise manage the split DU
     * 
     * @param taskDU
     * @param result
     */
    void updateSA(DUResult result) {
        // Retrieve BDUTask
        BgDUTask task = bDUTasks.getBgDUTask(result.getAbsRootPath());

        //Update the BDU task with the result
        task.setDuResult(result);
        
        try {
            bDUTasks.updateTask(task);
        } catch (SAInfoException e1) {
            LOG.error("Something strange happen.."+e1);
        }
          
        // Check if the result is success, otherwise ...
        if (result.getCmdResult().equals(CallableDU.ExitCode.SUCCESS)) {
            TSpaceToken st = task.getSpaceToken();

            // Start the Pool dedicated to save the result into the DB
            try {

                // Store the result into the DB
                persist.saveData(st, result);

            } catch (SAInfoException e) {
                LOG.error("Unable to persist the DU result of '" + result.getAbsRootPath() + "' " + e);
            }
        } else {
            // Logging the failure
            LOG.warn(String.format("DU of space with %s token is terminated with status %s",
                                   task.getSpaceToken().toString(),
                                   result.getCmdResult()));
        }

        // Decrease the number of tasks to complete
        // - note: a task is complete despite of the final status (Success or
        // failure it is same)
        int ttc = tasksToComplete.decrementAndGet();
        LOG.debug("TaskToComplete: "+ttc);
        
        if (tasksToComplete.get() <= 0) {
            // All the tasks are completed.
            LOG.info("All the tasks are completed!");
            // Check for failed tasks
            
            for (BgDUTask bTask : bDUTasks.getTasks()) {

                if (bTask.getDuResult().isSuccess()) {

                    success.incrementAndGet();
                    
                } else {
                    
                    // Update the attempt and maintain the task in processing queue
                    bTask.increaseAttempt();
                    if (bTask.getAttempt()>MaxAttempt) {
                       LOG.error("Unable to compute Space Used for the SA with root '"+bTask.getAbsPath()+"' for the reason :"+bTask.getDuResult().getCmdResult());        
                       failures.incrementAndGet();
                    } else {
                        // Retry
                        try {
                            bDUTasks.updateTask(bTask);       
                        } catch (SAInfoException e) {
                            LOG.error("Something starnge happen." + e);
                        }    
                    }

                }
            }
            int s = success.get();
            int f = failures.get();
            int tot = numberOfTasks.get();
            int r = tot-s-f;
            LOG.debug(String.format("Total DU Tasks are %d (success:%d; to-retry:%d; failure:%d).",tot,s,r,f));
            if (r==0) {
                //No more retry, so shutdown bDU
                stopExecution();
            }
        }
    }

    
    
    /**
     * Method called when DUResult has been saved into the DB
     * 
     * @param taskDU
     * @param result
     */
    void savedSA(DUResult result) {
       this.tasksToSave.decrementAndGet();
       LOG.debug("Result saved..");
       
       //Check if all the tasks has been processed
//       if (this.tasksToComplete.get()<=0) {
//    	   //All the tasks has been processed
//    	   //Check if there was some failures
//           if ((failures.get()) >0) {
//               attempt++;
//               if (attempt>MaxAttempt) {
//                   //No more retry, so shutdown bDU
//                   bDU.stopExecution();
//                   persist.stopSaver();
//                   LOG.debug("Stopping Background DU executions.");
//               } else {
//                   LOG.info("-- Resubmit DU tasks --");
//                   //Remove from tasks queue successfully tasks
//                   bDUTasks.removeSuccessTask();
//                   
//                   submitTasks(bDUTasks);    
//               }
//    	   }
//       }       
    }

    
	/**
     * Submit all the tasks to a BackgroundDU
     */
    void submitTasks(BackgroundDUTasks tasks) {
    	
        bDU = new BackgroundDU(timeOutDurationInSec * attempt, TimeUnit.SECONDS);
        
        Collection<BgDUTask> tasksToSubmit = tasks.getTasks();
        LOG.debug("tasks to submit: "+tasksToSubmit);
        int size = tasksToSubmit.size();
        this.numberOfTasks = new AtomicInteger(size);
        this.tasksToComplete = new AtomicInteger(size);
        this.tasksToSave = new AtomicInteger(size);
    	this.failures = new AtomicInteger(0);
    	this.success = new AtomicInteger(0);
    	
        LOG.info("Submitting "+this.tasksToComplete+" DU tasks.");
        for (BgDUTask task : tasksToSubmit ) {
            //task.getSpaceToken();
            bDU.addStorageArea(task.getAbsPath(), task.getTaskId());
        }
       
        LOG.info("Setting fake used space to "+this.tasksToComplete+" DU tasks.");
        //Set fake used space, in order to avoid -1 result during the computation
        setFakeUsedSpace(tasksToSubmit);
        
        LOG.info("Start DU background execution");
        bDU.startExecution();
   
    }

    
    
    /**
     * 
     * @param tasksToSubmit
     */
    private void setFakeUsedSpace(Collection<BgDUTask> tasksToSubmit) {
        ReservedSpaceCatalog spaceCatalog = new ReservedSpaceCatalog();
        for (BgDUTask task : tasksToSubmit ) {
            TSpaceToken sToken = task.getSpaceToken();
            StorageSpaceData ssd = spaceCatalog.getStorageSpace(sToken);
            TSizeInBytes totalSize = ssd.getTotalSpaceSize();
            TSizeInBytes fakeUsed = TSizeInBytes.makeEmpty();
            try {
                fakeUsed = TSizeInBytes.make(totalSize.value() / 2, SizeUnit.BYTES);
            }
            catch (InvalidTSizeAttributesException e) {
                LOG.warn("Unable to create Fake Size to set to used_size");
            }
            ssd.setUsedSpaceSize(fakeUsed); //By default also freeSize will be updated.
            spaceCatalog.updateStorageSpace(ssd);
        }  
        
    }


    
    
}
