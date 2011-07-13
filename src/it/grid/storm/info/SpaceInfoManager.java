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
import it.grid.storm.info.BackgroundDUTasks.BgDUTask;
import it.grid.storm.space.CallableDU;
import it.grid.storm.space.DUResult;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.InvalidTSpaceTokenAttributesException;
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
    
    
    
	public static int start() {
		int result = 0;
		SpaceInfoManager.getInstance().foundSAtoAnalyze();
		result = SpaceInfoManager.getInstance().bDUTasks.howManyTask();
		LOG.debug(String.format("Tasks: %d", result));
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
	
	
    /**
     * Populate with DU tasks
     */
    void fakeSAtoAnalyze(List<String> absPaths) {
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
    void foundSAtoAnalyze()
    {
        List<StorageSpaceData> toAnalyze = spaceCatalog.getStorageSpaceNotInitialized();
        for (StorageSpaceData ssd : toAnalyze)
        {
            TSpaceToken sT = ssd.getSpaceToken();
            String absPath = ssd.getSpaceFileNameString();
            if (sT == null || absPath == null)
            {
                LOG.error("Unable to submit DU test, StorageSpaceData returns null values: SpaceToken=" + sT + " , SpaceFileNameString="
                        + absPath);
            }
            else
            {
                try
                {
                    bDUTasks.addTask(sT, absPath);
                    LOG.debug("Added " + absPath + " to the DU-Task Queue. (size:" + bDUTasks.howManyTask() + ")");
                }
                catch (SAInfoException e)
                {
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
            bDU.addStorageArea(task.getAbsPath(), task.getTaskId());
        }
        bDU.startExecution();
   
    }


    
    
}
