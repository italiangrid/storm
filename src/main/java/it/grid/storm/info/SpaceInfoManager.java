/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.info;

import static it.grid.storm.config.Configuration.DISKUSAGE_SERVICE_ENABLED;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.model.VirtualFS;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.space.gpfsquota.GPFSQuotaManager;
import it.grid.storm.space.init.UsedSpaceFile;
import it.grid.storm.space.init.UsedSpaceFile.SaUsedSize;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TSizeInBytes;
import java.io.FileNotFoundException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceInfoManager {

  private static final SpaceInfoManager instance = new SpaceInfoManager();

  private static final String USED_SPACE_INI_FILEPATH =
      Configuration.getInstance().configurationDir() + "/used-space.ini".replaceAll("/+", "/");

  private static final Logger log = LoggerFactory.getLogger(SpaceInfoManager.class);

  // Reference to the Catalog
  private final ReservedSpaceCatalog spaceCatalog = new ReservedSpaceCatalog();
  // Reference to the NamespaceDirector
  private final NamespaceInterface namespace = NamespaceDirector.getNamespace();

  private SpaceInfoManager() {}

  public static SpaceInfoManager getInstance() {
    return instance;
  }

  public void initializeUsedSpace() {

    log.info("Initializing used-space info for quota enabled GPFS SA");
    GPFSQuotaManager.INSTANCE.start();

    log.debug("Getting the list of not initialized storage spaces ... ");
    List<StorageSpaceData> ssni = spaceCatalog.getStorageSpaceNotInitialized();
    List<StorageSpaceData> ssdqe = retrieveSSDtoInitializeWithQuota();
    ssni.removeAll(ssdqe);
    log.info("There are {} storage space(s) to initialize", ssni.size());

    if (ssni.isEmpty()) {
      return;
    }

    log.info("Check used-space initialization through ini files ... ");
    ssni = initUsedSpaceFromINIFile(ssni);

    if (ssni.isEmpty()) {
      return;
    }

    if (Configuration.getInstance().getDiskUsageServiceEnabled()) {
      log.info(
          "The remaining {} storage spaces will be initialized by DiskUsage service", ssni.size());
    } else {
      log.warn(
          "The remaining {} storage spaces WON'T be initialized with DUs. "
              + "Please enable DiskUsage service by setting '{}' as true.",
          ssni.size(),
          DISKUSAGE_SERVICE_ENABLED);
    }
  }

  public final int getQuotasDefined() {

    return namespace.getVFSWithQuotaEnabled().size();
  }

  /**
   * @return a list of StorageSpaceData related to SA with quota enabled to be initialized. Can be
   *     empty.
   */
  public List<StorageSpaceData> retrieveSSDtoInitializeWithQuota() {

    // Dispatch SA to compute in two categories: Quota and DU tasks
    List<StorageSpaceData> ssdSet = Lists.newArrayList();
    List<VirtualFS> vfsSet = namespace.getVFSWithQuotaEnabled();
    for (VirtualFS vfsEntry : vfsSet) {
      String spaceTokenDesc = vfsEntry.getSpaceTokenDescription();
      StorageSpaceData ssd = spaceCatalog.getStorageSpaceByAlias(spaceTokenDesc);
      ssdSet.add(ssd);
    }
    return ssdSet;
  }

  public List<StorageSpaceData> initUsedSpaceFromINIFile(List<StorageSpaceData> ssds) {

    List<StorageSpaceData> notFound = Lists.newArrayList();

    UsedSpaceFile usedSpaceFile = null;
    try {
      usedSpaceFile = new UsedSpaceFile(USED_SPACE_INI_FILEPATH);
    } catch (FileNotFoundException e) {
      log.info("No {} file found!", USED_SPACE_INI_FILEPATH);
    } catch (Exception e) {
      log.error("{}: {} ", e.getClass().getName(), e.getMessage());
    }
    if (usedSpaceFile == null) {
      log.info("Exiting used-space initialization from ini file");
      return ssds;
    }
    for (StorageSpaceData ssd : ssds) {
      String saName = ssd.getSpaceTokenAlias();
      log.debug("Evaluating StorageSpaceData: {}", saName);
      if (usedSpaceFile.hasSA(saName)) {
        log.debug("{} found! Updating used space on persistence... ", saName);
        updateUsedSpaceOnPersistence(usedSpaceFile.getSAUsedSize(saName));
        log.info("{} used-space updated from {}", saName, usedSpaceFile.getIniFile());
      } else {
        log.debug("{} not found into {}!", saName, USED_SPACE_INI_FILEPATH);
        notFound.add(ssd);
      }
    }
    return notFound;
  }

  private void updateUsedSpaceOnPersistence(SaUsedSize usedSize) {

    Preconditions.checkNotNull(usedSize, "Received null usedSize!");
    StorageSpaceData ssd = spaceCatalog.getStorageSpaceByAlias(usedSize.getSaName());

    if (ssd != null) {
      try {
        ssd.setUsedSpaceSize(TSizeInBytes.make(usedSize.getUsedSize(), SizeUnit.BYTES));
        spaceCatalog.updateStorageSpace(ssd);
        log.debug(
            "StorageSpace table updated for SA: '{}' with used size = {}",
            usedSize.getSaName(),
            usedSize.getUsedSize());
      } catch (InvalidTSizeAttributesException | DataAccessException e) {
        failPersistence(usedSize.getSaName(), e.getMessage());
      }
    } else {
      failPersistence(usedSize.getSaName(), "Unable to retrieve StorageSpaceData");
    }
  }

  private void failPersistence(String spaceToken, String detail) {

    log.error("StorageSpaceData with alias {} not updated on persistence: {}", spaceToken, detail);
  }
}
