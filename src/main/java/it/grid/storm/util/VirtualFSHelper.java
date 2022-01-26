package it.grid.storm.util;

import java.util.List;

import com.google.common.collect.Lists;

import it.grid.storm.namespace.CapabilityInterface;
import it.grid.storm.namespace.Namespace;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.Quota;

public class VirtualFSHelper {

  private VirtualFSHelper() {
    // empty constructor
  }

  public static final boolean isGPFSQuotaEnabledForVFS(VirtualFSInterface vfs) {

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

  public static List<VirtualFSInterface> getGPFSQuotaEnabledFilesystems() {

    List<VirtualFSInterface> fss = Lists.newArrayList();
    List<VirtualFSInterface> allVFS = Namespace.getInstance().getAllDefinedVFS();

    for (VirtualFSInterface vfs : allVFS) {
      if (isGPFSQuotaEnabledForVFS(vfs))
        fss.add(vfs);
    }

    return fss;

  }
}
