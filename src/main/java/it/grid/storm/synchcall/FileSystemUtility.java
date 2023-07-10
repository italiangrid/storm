/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall;

import static it.grid.storm.filesystem.RandomWaitFilesystemAdapter.maybeWrapFilesystem;
import static it.grid.storm.metrics.StormMetricRegistry.METRIC_REGISTRY;

import it.grid.storm.filesystem.Filesystem;
import it.grid.storm.filesystem.FilesystemIF;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.MetricsFilesystemAdapter;
import it.grid.storm.filesystem.swig.genericfs;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.model.VirtualFS;
import org.slf4j.Logger;

public class FileSystemUtility {

  private static Logger log = NamespaceDirector.getLogger();

  public static LocalFile getLocalFileByAbsolutePath(String absolutePath)
      throws NamespaceException {

    LocalFile file = null;
    VirtualFS vfs = null;
    genericfs fsDriver = null;
    FilesystemIF fs = null;
    try {
      vfs = NamespaceDirector.getNamespace().resolveVFSbyAbsolutePath(absolutePath);
    } catch (NamespaceException ex) {
      log.error("Unable to retrieve VFS by Absolute Path", ex);
    }
    if (vfs == null) {
      throw new NamespaceException("No VFS found in StoRM for this file :'" + absolutePath + "'");
    }

    try {
      fsDriver = (genericfs) (vfs.getFSDriver()).newInstance();

      FilesystemIF wrappedFs = new Filesystem(fsDriver);

      wrappedFs = maybeWrapFilesystem(wrappedFs);

      fs = new MetricsFilesystemAdapter(wrappedFs, METRIC_REGISTRY.getRegistry());

      file = new LocalFile(absolutePath, fs);
    } catch (NamespaceException ex1) {
      log.error("Error while retrieving FS driver", ex1);
    } catch (IllegalAccessException ex1) {
      log.error("Error while using reflection in FS Driver", ex1);
    } catch (InstantiationException ex1) {
      log.error("Error while instancing new FS driver", ex1);
    }

    return file;
  }
}
