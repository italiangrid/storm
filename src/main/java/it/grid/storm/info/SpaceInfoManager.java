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
import it.grid.storm.common.types.InvalidPFNAttributeException;
import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.info.BackgroundDUTasks.BgDUTask;
import it.grid.storm.namespace.CapabilityInterface;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
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
import it.grid.storm.srm.types.InvalidTSpaceTokenAttributesException;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceInfoManager {

	private static final Logger log = 
	  LoggerFactory.getLogger(SpaceInfoManager.class);
	private static final SpaceInfoManager instance = new SpaceInfoManager();
	private int timeOutDurationInSec = 7200; // 2 hours per task (This value is
																						// multiplied by attempt)
	private BackgroundDU bDU;
	private AtomicInteger tasksToComplete;
	private AtomicInteger tasksToSave;
	private AtomicInteger success;
	private AtomicInteger failures;
	private AtomicInteger numberOfTasks;

	// Package variable used in testing mode (without DB)
	AtomicBoolean testMode = new AtomicBoolean(false);

	private final int MaxAttempt = 2;

	private CatalogUpdater persist = new CatalogUpdater();
	// Reference to the Catalog
	private final ReservedSpaceCatalog spaceCatalog = new ReservedSpaceCatalog();

	private int quotas = 0;
	private int quotasDefined = 0;

	private SpaceInfoManager() {
		List<VirtualFSInterface> vfsS = retrieveSAtoInitializeWithQuota();
		if (vfsS != null) {
			this.quotasDefined = vfsS.size();
		} else {
			this.quotasDefined = 0;
		}
	}

	public final int getQuotasDefined() {
		return quotasDefined;
	}

	private final BackgroundDUTasks bDUTasks = new BackgroundDUTasks();
	private int attempt = 1;

	public static SpaceInfoManager getInstance() {
		return instance;
	}

	public static boolean isInProgress() {
		boolean result = false;
		if (SpaceInfoManager.getInstance().tasksToComplete.get() > 0) {
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

	public void updateSpaceUsed() {

		GPFSQuotaManager.INSTANCE.start();
		startBackGroundDU();
	}

	public static int howManyBackgroundDU() {

		return SpaceInfoManager.getInstance().bDUTasks.howManyTask();
	}

	public void execGPFSQuota() {

		GPFSQuotaManager.INSTANCE.start();
	}

	private int startBackGroundDU() {

		int result = 0;
		// This call populate the Task Queue: "bDUTasks"
		SpaceInfoManager.getInstance().foundSAtoAnalyze();
		result = SpaceInfoManager.getInstance().bDUTasks.howManyTask();
		log.debug("Tasks: {}", result);
		// Submit the tasks
		SpaceInfoManager.getInstance().submitTasks(
			SpaceInfoManager.getInstance().bDUTasks);
		return result;
	}

	public int startTest(List<String> absPaths) {

		int result = 0;
		testMode = new AtomicBoolean(true);
		SpaceInfoManager.getInstance().fakeSAtoAnalyze(absPaths);
		result = SpaceInfoManager.getInstance().bDUTasks.howManyTask();
		SpaceInfoManager.getInstance().submitTasks(
			SpaceInfoManager.getInstance().bDUTasks);
		return result;
	}

	public static int stop() {

		int result = 0;
		SpaceInfoManager.getInstance().stopExecution();
		result = SpaceInfoManager.getInstance().failures.get();
		return result;
	}

	// ********************************************
	// Package methods
	// ********************************************

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

	public StorageSpaceData getSSDfromQuotaName(String quotaName) {

		StorageSpaceData ssd = null;
		List<VirtualFSInterface> vfsList = retrieveSAtoInitializeWithQuota();
		ReservedSpaceCatalog ssdCatalog = new ReservedSpaceCatalog();
		for (VirtualFSInterface vfsEntry : vfsList) {
			String qName = vfsEntry.getCapabilities().getQuota()
				.getQuotaElementName();
			if (qName.equals(quotaName)) {
				String spaceTokenDesc = vfsEntry.getSpaceTokenDescription();
				ssd = ssdCatalog.getStorageSpaceByAlias(spaceTokenDesc);
			}
		}
		return ssd;
	}

	public List<String> retrieveQuotaNamesToUse() {

		List<String> quotaNames = new ArrayList<String>();
		List<VirtualFSInterface> vfsList = retrieveSAtoInitializeWithQuota();
		for (VirtualFSInterface vfsEntry : vfsList) {
			log.debug("vfsEntry (AliasName): {}", vfsEntry.getAliasName());
			String quotaName = vfsEntry.getCapabilities().getQuota()
				.getQuotaElementName();
			log.debug("Found this quotaName to check: '{}'", quotaName); 
			quotaNames.add(quotaName);
		}
		log.debug("Number of quotaNames: {}" ,quotaNames.size());
		return quotaNames;
	}

	public List<VirtualFSInterface> retrieveSAtoInitializeWithQuota() {

		// Dispatch SA to compute in two categories: Quota and DU tasks
		List<VirtualFSInterface> vfsSet = null;
		;
		try {
			vfsSet = new ArrayList<VirtualFSInterface>(NamespaceDirector
				.getNamespace().getAllDefinedVFS());
		} catch (NamespaceException e) {
		  log.error(e.getMessage(),e);
			log.error("Returning an empty VFS list");
			return new ArrayList<VirtualFSInterface>();
		}
		log.debug("Found '{}' VFS defined in Namespace.xml", vfsSet.size());
		List<VirtualFSInterface> vfsSetQuota = new ArrayList<VirtualFSInterface>();
		if (vfsSet.size() > 0) { // Exists at least a VFS defined
			for (VirtualFSInterface vfsItem : vfsSet) {
				if (gpfsQuotaEnabled(vfsItem)) {
					vfsSetQuota.add(vfsItem);
				}
			}
		}
		log.debug("Number of VFS with Quota enabled: {}", vfsSetQuota.size());
		return vfsSetQuota;
	}

	private boolean gpfsQuotaEnabled(VirtualFSInterface vfsItem) {

		boolean result = false;
		if (vfsItem != null) {
			CapabilityInterface cap = null;
			Quota quota = null;
			String fsType = "Unknown";
			fsType = vfsItem.getFSType();
			if (fsType != null) {
				if (fsType.trim().toLowerCase().equals("gpfs")) {
					cap = vfsItem.getCapabilities();
					if (cap != null) {
						quota = cap.getQuota();
					}
					if (quota != null) {
						result = ((quota.getDefined()) && (quota.getEnabled()));
					}
				}
			}
		}
		return result;
	}

	/**
	 * Populate with DU tasks
	 */
	private void fakeSAtoAnalyze(List<String> absPaths) {

		// Create a list of SSD using the list of AbsPaths
		List<StorageSpaceData> toAnalyze = new ArrayList<StorageSpaceData>();
		for (String path : absPaths) {
			path = path + File.separator;
			String pathNorm = FilenameUtils
				.normalize(FilenameUtils.getFullPath(path));
			StorageSpaceData ssd = new StorageSpaceData();
			try {
				PFN spaceFN = PFN.make(pathNorm);
				log.trace("PFN : " , spaceFN);
				ssd.setSpaceToken(TSpaceToken.make(new it.grid.storm.common.GUID()
					.toString()));

				ssd.setSpaceFileName(spaceFN);
				toAnalyze.add(ssd);
			} catch (InvalidTSpaceTokenAttributesException e) {
				log.error("Unable to create Space Token. {}", e.getMessage(), e);
			} catch (InvalidPFNAttributeException e) {
				log.error("Unable to create PFN. {}", e.getMessage(),e);
			}
		}

		for (StorageSpaceData ssd : toAnalyze) {
			TSpaceToken sT = ssd.getSpaceToken();
			String absPath = ssd.getSpaceFileNameString();
			try {
				bDUTasks.addTask(sT, absPath);
				log.debug("Added {} to the DU-Task Queue. (Task queue size: {})", 
				  absPath,
					bDUTasks.howManyTask());
			} catch (SAInfoException e) {
			  log.error(e.getMessage(), e);
			}
		}
		log.info("Background DU tasks size: {}", bDUTasks.getTasks().size());
	}

	private void foundSAtoAnalyze() {

		List<StorageSpaceData> toAnalyze = spaceCatalog
			.getStorageSpaceNotInitialized();
		for (StorageSpaceData ssd : toAnalyze) {
			TSpaceToken sT = ssd.getSpaceToken();
			String absPath = ssd.getSpaceFileNameString();
			if (sT == null || absPath == null) {
				log
					.error("Unable to submit DU test, StorageSpaceData returns "
					  + "null values: SpaceToken={}, SpaceFileNameString={}",
					  sT, absPath);
			} else {

				try {
					bDUTasks.addTask(sT, absPath);
					log.debug("Added {} to the DU-Task Queue. (size: {})",
					  absPath, bDUTasks.howManyTask());

				} catch (SAInfoException e) {
				  log.error(e.getMessage(),e);
				}
			}
		}
	}

	void stopExecution() {
	  log.trace("SpaceInfoManager.stopExecution");
		bDU.stopExecution(true);
		persist.stopSaver();
	}

	void updateSA(DUResult result) {

		// Retrieve BDUTask
		BgDUTask task = bDUTasks.getBgDUTask(result.getAbsRootPath());

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

						log.error("Unable to compute Space Used for the SA with root {}. Reason: {}",
						  bTask.getAbsPath(),
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

	void submitTasks(BackgroundDUTasks tasks) {

		bDU = new BackgroundDU(timeOutDurationInSec * attempt, TimeUnit.SECONDS);

		Collection<BgDUTask> tasksToSubmit = tasks.getTasks();
		log.debug("tasks to submit: {}", tasksToSubmit);
		int size = tasksToSubmit.size();
		this.numberOfTasks = new AtomicInteger(size);
		this.tasksToComplete = new AtomicInteger(size);
		this.tasksToSave = new AtomicInteger(size);
		this.failures = new AtomicInteger(0);
		this.success = new AtomicInteger(0);

		log.info("Submitting {} DU tasks.", tasksToComplete);

		for (BgDUTask task : tasksToSubmit) {
			bDU.addStorageArea(task.getAbsPath(), task.getTaskId());
		}

		log.info("Setting fake used space to {} DU tasks.", tasksToComplete);
		setFakeUsedSpace(tasksToSubmit);

		log.info("Start DU background execution");
		bDU.startExecution();
	}

	private void setFakeUsedSpace(Collection<BgDUTask> tasksToSubmit) {

		ReservedSpaceCatalog spaceCatalog = new ReservedSpaceCatalog();
		for (BgDUTask task : tasksToSubmit) {
			TSpaceToken sToken = task.getSpaceToken();
			StorageSpaceData ssd = null;
			try {
				ssd = spaceCatalog.getStorageSpace(sToken);
			} catch (TransferObjectDecodingException e) {
				log
					.error("Unable to build StorageSpaceData from StorageSpaceTO. TransferObjectDecodingException: {}",
					  e.getMessage(),e);
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

	public int initSpaceFromINIFile() {

		List<StorageSpaceData> toAnalyze = spaceCatalog
			.getStorageSpaceNotInitialized();
		ArrayList<String> toAnalyzeAlias = new ArrayList<String>();
		for (StorageSpaceData ssd : toAnalyze) {
			toAnalyzeAlias.add(ssd.getSpaceTokenAlias());
		}
		printInitializedStorageAreas(toAnalyzeAlias);
		int storageSpaceUpdated = 0;
		UsedSpaceFile usedSpaceFile = new UsedSpaceFile(toAnalyzeAlias);
		List<SaUsedSize> saUsedSizeList = usedSpaceFile.getDefinedSizes();
		for (SaUsedSize saUsedSize : saUsedSizeList) {
			if (saUsedSize.hasUpdateTime()) {
				try {
					updateUsedSpaceOnPersistence(saUsedSize.getSaName(),
						saUsedSize.getUsedSize(), saUsedSize.getUpdateTime());
					storageSpaceUpdated++;
				} catch (IllegalArgumentException e) {
				  log.error("Error updating used space for SA {} to value {} with update time {}. {}",
				    saUsedSize.getSaName(),
				    saUsedSize.getUsedSize(),
				    saUsedSize.getUpdateTime(),
				    e.getMessage(),
				    e);
				}
			} else {
				try {
					updateUsedSpaceOnPersistence(saUsedSize.getSaName(),
						saUsedSize.getUsedSize());
					storageSpaceUpdated++;
				} catch (IllegalArgumentException e) {
				  log.error("Error updating used space for SA {} to value {} with update time {}. {}",
				    saUsedSize.getSaName(),
				    saUsedSize.getUsedSize(),
				    saUsedSize.getUpdateTime(),
				    e.getMessage(),
				    e);
				}
			}
		}

		return storageSpaceUpdated;
	}

	private void printInitializedStorageAreas(List<String> saAlias) {

		try {
			for (VirtualFSInterface vfs : NamespaceDirector.getNamespace()
				.getAllDefinedVFS()) {
				if (!(saAlias.contains(vfs.getSpaceTokenDescription()))) {
					log.debug("SA {} already initialized", vfs.getAliasName());
				}
			}
		} catch (NamespaceException e) {
		  log.error(e.getMessage(), e);
		}
	}

	private void updateUsedSpaceOnPersistence(String saName, Long usedSize)
		throws IllegalArgumentException {

		if (saName == null || usedSize == null) {
		  log.error("Null arguments: saName={} usedSize={}",
		    saName, usedSize);
			throw new IllegalArgumentException("Received null arguments: saName = "
				+ saName + " usedSize = " + usedSize);
		}
		updateUsedSpaceOnPersistence(saName, usedSize, null);
	}

	private void updateUsedSpaceOnPersistence(String saName, Long usedSize,
		Date updateTime) throws IllegalArgumentException {

		if (saName == null || usedSize == null) {
		  log.error("Null arguments: saName={} usedSize={}",
		    saName, usedSize);
			throw new IllegalArgumentException("Received null arguments: saName = "
				+ saName + " usedSize = " + usedSize);
		}
		StorageSpaceData ssd = spaceCatalog.getStorageSpaceByAlias(saName);
		if (ssd != null) {
			try {
				ssd.setUsedSpaceSize(TSizeInBytes.make(usedSize, SizeUnit.BYTES));
			} catch (InvalidTSizeAttributesException e) {
			  log.error(e.getMessage(),e);
				throw new IllegalArgumentException("Invalid Used Size: " + usedSize
					+ " Unable to update Storage Space");
			}
			// Persist the change.
			if (updateTime == null) {
				spaceCatalog.updateStorageSpace(ssd);
			} else {
				spaceCatalog.updateStorageSpace(ssd, updateTime);
			}
			log.debug("StorageSpace table updated for SA: '{}' with used size = {}",
			  saName, usedSize);

		} else {
			log.warn("Unable to retrieve StorageSpaceData with Alias: {}", saName);
			throw new IllegalArgumentException(
				"Unable to retrieve StorageSpaceData with Alias: " + saName);
		}
	}
}
