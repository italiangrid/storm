/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.rest.metadata.service;

import static it.grid.storm.checksum.ChecksumAlgorithm.ADLER32;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceStatus.NEARLINE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceStatus.ONLINE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceType.FILE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceType.FOLDER;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import it.grid.storm.ea.StormEA;
import it.grid.storm.filesystem.FSException;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.namespace.InvalidDescendantsEmptyRequestException;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.model.VirtualFS;
import it.grid.storm.rest.metadata.model.FileAttributes;
import it.grid.storm.rest.metadata.model.StoriMetadata;
import it.grid.storm.rest.metadata.model.VirtualFsMetadata;
import it.grid.storm.srm.types.TDirOption;

public class StoriMetadataService {

  private static final Logger log = LoggerFactory.getLogger(StoriMetadataService.class);

  private ResourceService resourceService;

  public StoriMetadataService(ResourceService resourceService) {

    this.resourceService = resourceService;
  }

  public StoriMetadata getMetadata(String stfnPath)
      throws ResourceNotFoundException, NamespaceException, IOException, FSException {

    StoRI stori = resourceService.getResource(stfnPath);
    LocalFile localFile = stori.getLocalFile();
    if (localFile.exists()) {
      log.debug("{} exists", localFile.getAbsolutePath());
      return buildFileMetadata(stori);
    }
    String errorMessage = String.format("%s not exists", localFile.getAbsolutePath());
    throw new ResourceNotFoundException(errorMessage);
  }

  private StoriMetadata buildFileMetadata(StoRI stori) throws IOException, FSException {

    VirtualFS vfs = stori.getVirtualFileSystem();
    String canonicalPath = stori.getLocalFile().getCanonicalPath();
    log.debug("VirtualFS is {}", vfs.getAliasName());
    VirtualFsMetadata vfsMeta =
        VirtualFsMetadata.builder().name(vfs.getAliasName()).root(vfs.getRootPath()).build();

    FileAttributes attributes = null;
    List<String> children = null;
    if (stori.getLocalFile().isDirectory()) {
      children = Lists.newArrayList();
      try {
        for (StoRI child : stori.getChildren(TDirOption.makeFirstLevel())) {
          children.add(child.getFilename());
        }
      } catch (InvalidDescendantsEmptyRequestException e) {
        log.debug("{} is an empty directory", stori.getLocalFile());
      }
    } else {
      attributes = FileAttributes.builder().pinned(StormEA.isPinned(canonicalPath))
          .migrated(StormEA.getMigrated(canonicalPath))
          .premigrated(StormEA.getPremigrated(canonicalPath))
          .checksum(StormEA.getChecksum(canonicalPath, ADLER32))
          .tsmRecD(StormEA.getTSMRecD(canonicalPath)).tsmRecR(StormEA.getTSMRecR(canonicalPath))
          .tsmRecT(StormEA.getTSMRecT(canonicalPath)).build();
    }
    return StoriMetadata.builder().absolutePath(stori.getAbsolutePath())
        .lastModified(new Date((new File(canonicalPath)).lastModified()))
        .type(stori.getLocalFile().isDirectory() ? FOLDER : FILE)
        .status(stori.getLocalFile().isOnDisk() ? ONLINE : NEARLINE).filesystem(vfsMeta)
        .attributes(attributes).children(children).build();
  }
}
