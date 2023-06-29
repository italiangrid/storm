/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.info.du;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.namespace.model.VirtualFS;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.space.DUResult;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TSizeInBytes;

public class DiskUsageTask implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(DiskUsageTask.class);

  private final ReservedSpaceCatalog spaceCatalog = new ReservedSpaceCatalog();
  private VirtualFS vfs;

  public DiskUsageTask(VirtualFS vfs) {
    this.vfs = vfs;
  }

  @Override
  public void run() {

    String spaceToken = vfs.getSpaceTokenDescription();
    String rootPath = vfs.getRootPath();
    log.info("DiskUsageTask for {} on {} started ...", spaceToken, rootPath);

    try {

      DiskUsageExecCommand duCommand = new DiskUsageExecCommand(rootPath);
      log.debug("du-command: {}", duCommand);
      DUResult result = duCommand.execute();
      log.debug("du-result: {}", result);
      updateUsedSpaceOnPersistence(spaceToken, result);
      long seconds = TimeUnit.MILLISECONDS.toSeconds(result.getDurationInMillis());
      log.info("DiskUsageTask for {} successfully ended in {}s with used-size = {} bytes",
          spaceToken, seconds, result.getSizeInBytes());

    } catch (IOException e) {

      log.error("DiskUsageTask for {} has failed: {}", spaceToken, e.getMessage(), e);
    }
  }

  private void updateUsedSpaceOnPersistence(String spaceToken, DUResult duResult) {

    Preconditions.checkNotNull(spaceToken, "Received null spaceToken!");
    Preconditions.checkNotNull(duResult, "Received null duResult!");

    StorageSpaceData ssd = spaceCatalog.getStorageSpaceByAlias(spaceToken);

    if (ssd == null) {
      failPersistence(spaceToken, "Unable to retrieve StorageSpaceData");
      return;
    }

    try {

      ssd.setUsedSpaceSize(TSizeInBytes.make(duResult.getSizeInBytes(), SizeUnit.BYTES));
      spaceCatalog.updateStorageSpace(ssd);
      log.debug("StorageSpace table updated for SA: '{}' with used size = {}", spaceToken,
          duResult.getSizeInBytes());

    } catch (InvalidTSizeAttributesException | DataAccessException e) {

      failPersistence(spaceToken, e.getMessage());
    }
  }

  private void failPersistence(String spaceToken, String detail) {

    log.error("StorageSpaceData with alias {} not updated on persistence: {}", spaceToken, detail);
  }

  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder("DiskUsageTask [");
    builder.append("vfsName=" + vfs.getAliasName() + ",");
    builder.append("vfsRootPath=" + vfs.getRootPath());
    builder.append("]");
    return builder.toString();
  }

}
