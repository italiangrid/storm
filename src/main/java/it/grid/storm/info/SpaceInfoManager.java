/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.info;

import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.info.BackgroundDUTasks.BgDUTask;
import it.grid.storm.namespace.CapabilityInterface;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.Quota;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TransferObjectDecodingException;
import it.grid.storm.space.DUResult;
import it.grid.storm.space.ExitCode;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.space.gpfsquota.GPFSQuotaManager;
import it.grid.storm.space.init.UsedSpaceFile;
import it.grid.storm.space.init.UsedSpaceFile.SaUsedSize;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class SpaceInfoManager {

	private static final SpaceInfoManager instance = new SpaceInfoManager();
	
	private static final String usedSpaceIniFilePath = Configuration
		.getInstance().configurationDir() + "/used-space.ini".replaceAll("/+", "/");
	
	private static final Logger log = LoggerFactory
		.getLogger(SpaceInfoManager.class);
	
	private int timeOutDurationInSec = 7200; // 2 hours per task (per attempt)
	private BackgroundDU bDU;
	private AtomicInteger tasksToComplete;
	private AtomicInteger tasksToSave;
	private AtomicInteger success;
	private AtomicInteger failures;
	private AtomicInteger numberOfTasks;
	
	private CatalogUpdater persist = new CatalogUpdater();
	
	// Reference to the Catalog
	private final ReservedSpaceCatalog spaceCatalog = new ReservedSpaceCatalog();
	// Reference to the NamespaceDirector
	private final NamespaceInterface namespace = NamespaceDirector.getNamespace();
 
	private final BackgroundDUTasks bDUTasks = new BackgroundDUTasks();
	private final int MaxAttempt = 2;
	
	private SpaceInfoManager() {
	}

	public static SpaceInfoManager getInstance() {
		return instance;
	}

	public void initializeUsedSpace() {
		
		log.info("Initializing used-space info for quota enabled GPFS SA");
		GPFSQuotaManager.INSTANCE.start();
				
		log.debug("Getting the list of not initialized storage spaces ... ");
		List<StorageSpaceData> ssni = spaceCatalog.getStorageSpaceNotInitialized();
		log.debug("{} storage space(s) are not initialized", ssni.size());
		List<StorageSpaceData> ssdqe = retrieveSSDtoInitializeWithQuota();
		log.debug("{} quota enabled storage space(s) are not initialized",
			ssdqe.size());
		ssni.removeAll(ssdqe);
		log.debug("Removed quota enabled storage spaces");
		log.debug("There are {} storage space(s) to initialize", ssni.size());
		
		log.debug("Check used-space initialization through ini files ... ");
		ssni = initUsedSpaceFromINIFile(ssni);

		log.debug("Initializing used-space on remaining {} storage spaces",
			ssni.size());
		int n = initUsedSpaceUsingDU(ssni);
		log.debug("Launched {} du on {} storage areas", n, ssni.size());
	}
	
	public final int getQuotasDefined() {
		
		return retrieveSAtoInitializeWithQuota().size();
	}
	
	public boolean isInProgress() {

		return SpaceInfoManager.getInstance().tasksToComplete.get() > 0;
	}

	public boolean isInProgress(TSpaceToken spaceToken) {

		if (isInProgress()) {
			for (BgDUTask task: bDUTasks.getTasks()) {
				if (spaceToken.equals(task.getSpaceToken())) {
					return true;
				}
			}
		}
		return false;
	}
	
	private int initUsedSpaceUsingDU(List<StorageSpaceData> ssds) {
		
		log.debug("Clear background du tasks ... ");
		bDUTasks.clearTasks();
		for (StorageSpaceData ssd : ssds) {
			TSpaceToken st = ssd.getSpaceToken();
			String sfns = ssd.getSpaceFileNameString();
			try {
				log.debug("Adding du task for [{}, {}] ... ", st.toString(), sfns);
				bDUTasks.addTask(st, sfns);
				log.debug("Added {} to the DU-Task Queue. (size: {})", sfns, 
					bDUTasks.howManyTask());
			} catch (SAInfoException e) {
				log.error(e.getMessage(), e);
			}
		}
		log.debug("Starting tasks ... ");
		startBackGroundDU();
		return bDUTasks.howManyTask();
	}
	
	private void startBackGroundDU() {

		submitTasks();
	}
	
	public static int stop() {

		SpaceInfoManager.getInstance().stopExecution();
		return SpaceInfoManager.getInstance().failures.get();
	}

	/**
	 * @return a list of StorageSpaceData related to SA with quota enabled to be
	 *         initialized. Can be empty.
	 */
	public List<StorageSpaceData> retrieveSSDtoInitializeWithQuota() {

		// Dispatch SA to compute in two categories: Quota and DU tasks
		List<StorageSpaceData> ssdSet = new ArrayList<StorageSpaceData>();
		List<VirtualFSInterface> vfsSet = retrieveSAtoInitializeWithQuota();
		ReservedSpaceCatalog ssdCatalog = new ReservedSpaceCatalog();
		for (VirtualFSInterface vfsEntry : vfsSet) {
			String spaceTokenDesc = vfsEntry.getSpaceTokenDescription();
			StorageSpaceData ssd = ssdCatalog.getStorageSpaceByAlias(spaceTokenDesc);
			ssdSet.add(ssd);
		}
		return ssdSet;
	}

	private List<VirtualFSInterface> retrieveSAtoInitializeWithQuota() {

		// Dispatch SA to compute in two categories: Quota and DU tasks
		List<VirtualFSInterface> vfsSet = null;
		;
		try {
			vfsSet = new ArrayList<VirtualFSInterface>(namespace.getAllDefinedVFS());
		} catch (NamespaceException e) {
		  log.error(e.getMessage(),e);
			log.error("Returning an empty VFS list");
			return new ArrayList<VirtualFSInterface>();
		}
		log.debug("Found '{}' VFS defined in Namespace.xml", vfsSet.size());
		List<VirtualFSInterface> vfsSetQuota = new ArrayList<VirtualFSInterface>();
		if (vfsSet.size() > 0) { // Exists at least a VFS defined
			for (VirtualFSInterface vfsItem : vfsSet) {
				if (isGPFSQuotaEnabled(vfsItem)) {
					vfsSetQuota.add(vfsItem);
				}
			}
		}
		log.debug("Number of VFS with Quota enabled: {}", vfsSetQuota.size());
		return vfsSetQuota;
	}

	private boolean isGPFSQuotaEnabled(VirtualFSInterface vfsItem) {

		Preconditions.checkNotNull(vfsItem, "vfsItem must not be null!");
		
		String fsType = vfsItem.getFSType();
		if (fsType == null) {
			log.debug("isGPFSQuotaEnabled: fsType is null!");
			return false;
		}
		if (!fsType.trim().toLowerCase().equals("gpfs")) {
			log.debug("isGPFSQuotaEnabled: fsType is not gpfs, exiting...");
			return false;
		}
		CapabilityInterface cap = vfsItem.getCapabilities();
		if (cap == null) {
			log.debug("isGPFSQuotaEnabled: fs capabilities are null!");
			return false;
		}
		Quota	quota = cap.getQuota();
		if (quota == null) {
			log.debug("isGPFSQuotaEnabled: quota is null!");
			return false;
		}
		return (quota.getDefined() && quota.getEnabled());
	}
	
	void stopExecution() {

	  log.trace("SpaceInfoManager.stopExecution");
		bDU.stopExecution(true);
		persist.stopSaver();
	}

	void updateSA(DUResult result) {

		// Retrieve BDUTask
		BgDUTask task = bDUTasks.getTask(result.getAbsRootPath());

		// Update the BDU task with the result
		task.setDuResult(result);

		try {
			bDUTasks.updateTask(task);
		} catch (SAInfoException e1) {
			log.error("Something strange happen.. {}", e1.getMessage(), e1);
		}

		// Check if the result is success, otherwise ...
		if (result.getCmdResult().equals(ExitCode.SUCCESS)) {
			TSpaceToken st = task.getSpaceToken();

			// Start the Pool dedicated to save the result into the DB
			try {

				// Store the result into the DB
				persist.saveData(st, result);

			} catch (SAInfoException e) {
			  log.error(e.getMessage(),e);
			}
		} else {
			// Logging the failure
			log.warn("DU of space with {} token is terminated with status {}", task
					.getSpaceToken().toString(), result.getCmdResult());
		}

		// Decrease the number of tasks to complete
		// - note: a task is complete despite of the final status (Success or
		// failure it is same)
		int ttc = tasksToComplete.decrementAndGet();
		log.debug("TaskToComplete: {}", ttc);

		if (tasksToComplete.get() <= 0) {
			// All the tasks are completed.
			log.info("All the tasks are completed!");
			// Check for failed tasks

			for (BgDUTask bTask : bDUTasks.getTasks()) {

				if (bTask.getDuResult().isSuccess()) {

					success.incrementAndGet();

				} else {

					// Update the attempt and maintain the task in processing queue
					bTask.increaseAttempt();
					if (bTask.getAttempt() > MaxAttempt) {

						log.error("Unable to compute Space Used for the SA with root {}. "
							+ "Reason: {}", bTask.getAbsPath(), 
							bTask.getDuResult().getCmdResult());
						
						failures.incrementAndGet();
					} else {
						// Retry
						try {
							bDUTasks.updateTask(bTask);
						} catch (SAInfoException e) {
						  log.error(e.getMessage(),e);
						}
					}
				}
			}
			int s = success.get();
			int f = failures.get();
			int tot = numberOfTasks.get();
			int r = tot - s - f;
			log.debug("Total DU Tasks are {} (success: {}; to-retry: {}; failure: {}.", 
			  tot, s, r, f);
			
			if (r == 0) {
				stopExecution();
			}
		}
	}

	void savedSA(DUResult result) {
		this.tasksToSave.decrementAndGet();
		log.debug("Result saved..");
	}

	void submitTasks() {

		bDU = new BackgroundDU(timeOutDurationInSec, TimeUnit.SECONDS);

		log.debug("Tasks to submit: {}", bDUTasks.getTasks());
		int size = bDUTasks.getTasks().size();
		this.numberOfTasks = new AtomicInteger(size);
		this.tasksToComplete = new AtomicInteger(size);
		this.tasksToSave = new AtomicInteger(size);
		this.failures = new AtomicInteger(0);
		this.success = new AtomicInteger(0);

		log.debug("Submitting {} DU tasks.", tasksToComplete);

		for (BgDUTask task : bDUTasks.getTasks()) {
			log.info("Submitting DU task on {}", task.getAbsPath());
			bDU.addStorageArea(task.getAbsPath(), task.getTaskId());
		}

		log.debug("Setting fake used space to {} DU tasks.", tasksToComplete);
		setFakeUsedSpace(bDUTasks.getTasks());

		log.debug("Start DU background execution");
		bDU.startExecution();
		log.debug("Submitted all the {} DU task.", size);
	}

	private void setFakeUsedSpace(Collection<BgDUTask> tasksToSubmit) {

		for (BgDUTask task : tasksToSubmit) {
			TSpaceToken sToken = task.getSpaceToken();
			StorageSpaceData ssd = null;
			try {
				ssd = spaceCatalog.getStorageSpace(sToken);
			} catch (TransferObjectDecodingException e) {
				log.error("Unable to build StorageSpaceData from StorageSpaceTO. "
					+ "TransferObjectDecodingException: {}", e.getMessage(), e);
			} catch (DataAccessException e) {
				log.error("Unable to build get StorageSpaceTO. DataAccessException: {}",
				  e.getMessage(),e);
			}
			TSizeInBytes totalSize = ssd.getTotalSpaceSize();
			TSizeInBytes fakeUsed = TSizeInBytes.makeEmpty();
			try {
				fakeUsed = TSizeInBytes.make(totalSize.value() / 2, SizeUnit.BYTES);
			} catch (InvalidTSizeAttributesException e) {
				log.warn("Unable to create Fake Size to set to used_size. {}",
				  e.getMessage());
			}
			ssd.setUsedSpaceSize(fakeUsed); 
			spaceCatalog.updateStorageSpace(ssd);
		}
	}

	public List<StorageSpaceData> initUsedSpaceFromINIFile(
		List<StorageSpaceData> ssds) {

		List<StorageSpaceData> notFound = new ArrayList<StorageSpaceData>();
		
		UsedSpaceFile usedSpaceFile = null;
		try {
			usedSpaceFile = new UsedSpaceFile(usedSpaceIniFilePath);
		} catch (FileNotFoundException e) {
			log.info("No {} file found!", usedSpaceIniFilePath);
		} catch (Throwable e) {
			log.error("{}: {} ", e.getClass().getName(), e.getMessage());
		} finally {
			if (usedSpaceFile == null) {
				log.info("Exiting used-space initialization from ini file");
				return ssds;
			}
		}
		for (StorageSpaceData ssd : ssds) {
			String saName = ssd.getSpaceTokenAlias();
			log.debug("Evaluating StorageSpaceData: {}", saName);
			if (usedSpaceFile.hasSA(saName)) {
				log.debug("{} found! Updating used space on persistence... ", saName);
				updateUsedSpaceOnPersistence(usedSpaceFile.getSAUsedSize(saName));
				log.info("{} used-space updated from {}", saName,
					usedSpaceFile.getIniFile()		);
			} else {
				log.debug("{} not found into {}!", saName, usedSpaceIniFilePath);
				notFound.add(ssd);
			}
		}
		return notFound;
	}

	private void updateUsedSpaceOnPersistence(SaUsedSize usedSize) {

		Preconditions.checkNotNull(usedSize, "Received null usedSize!");

		StorageSpaceData ssd = spaceCatalog.getStorageSpaceByAlias(usedSize
			.getSaName());
		
		Preconditions
			.checkNotNull(ssd, "Unable to retrieve StorageSpaceData with Alias: "
				+ usedSize.getSaName());
		
		try {
			ssd.setUsedSpaceSize(TSizeInBytes.make(usedSize.getUsedSize(),
				SizeUnit.BYTES));
		} catch (InvalidTSizeAttributesException e) {
			log.error(e.getMessage(), e);
			throw new IllegalArgumentException("Invalid Used Size: " + usedSize
				+ " Unable to update Storage Space");
		}
		// Persist the change.
		if (usedSize.hasUpdateTime()) {
			spaceCatalog.updateStorageSpace(ssd, usedSize.getUpdateTime());
		} else {
			spaceCatalog.updateStorageSpace(ssd);
		}
		log.debug("StorageSpace table updated for SA: '{}' with used size = {}",
			usedSize.getSaName(), usedSize.getUsedSize());

	}
}
