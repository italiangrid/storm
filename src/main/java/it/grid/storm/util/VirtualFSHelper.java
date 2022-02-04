package it.grid.storm.util;

import java.util.List;

import com.google.common.collect.Lists;

import it.grid.storm.namespace.CapabilityInterface;
import it.grid.storm.namespace.Namespace;
import it.grid.storm.namespace.model.Quota;
import it.grid.storm.namespace.model.VirtualFS;

public class VirtualFSHelper {

  private VirtualFSHelper() {
    // empty constructor
  }

  public static final boolean isGPFSQuotaEnabledForVFS(VirtualFS vfs) {

    boolean result = false;

    if (vfs != null) {
      CapabilityInterface cap = null;
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
    List<VirtualFS> allVFS = Namespace.getInstance().getAllDefinedVFS();

    for (VirtualFS vfs : allVFS) {
      if (isGPFSQuotaEnabledForVFS(vfs))
        fss.add(vfs);
    }

    return fss;

  }
}
