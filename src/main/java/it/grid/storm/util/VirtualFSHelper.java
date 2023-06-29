/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.util;

import java.util.List;

import com.google.common.collect.Lists;

import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.model.Capability;
import it.grid.storm.namespace.model.Quota;
import it.grid.storm.namespace.model.VirtualFS;

public class VirtualFSHelper {

  private VirtualFSHelper() {
    // empty constructor
  }

  public static final boolean isGPFSQuotaEnabledForVFS(VirtualFS vfs) {

    boolean result = false;

    if (vfs != null) {
      Capability cap = null;
      Quota quota = null;
      String fsType = vfs.getFSType();
      if (fsType != null && fsType.trim().equalsIgnoreCase("gpfs")) {
        cap = vfs.getCapabilities();
        if (cap != null) {
          quota = cap.getQuota();
        }
        if (quota != null) {
          result = ((quota.getDefined()) && (quota.getEnabled()));
        }
      }
    }
    return result;
  }

  public static List<VirtualFS> getGPFSQuotaEnabledFilesystems() {

    List<VirtualFS> fss = Lists.newArrayList();
    List<VirtualFS> allVFS = NamespaceDirector.getNamespace().getAllDefinedVFS();

    for (VirtualFS vfs : allVFS) {
      if (isGPFSQuotaEnabledForVFS(vfs))
        fss.add(vfs);
    }

    return fss;

  }
}
