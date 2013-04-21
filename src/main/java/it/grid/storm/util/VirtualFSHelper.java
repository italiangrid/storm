package it.grid.storm.util;

import it.grid.storm.namespace.CapabilityInterface;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.Quota;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VirtualFSHelper {
	
	public static final boolean isGPFSQuotaEnabledForVFS(VirtualFSInterface vfs) {

		boolean result = false;
	
		if (vfs != null) {
			CapabilityInterface cap = null;
			Quota quota = null;
			String fsType = "Unknown";
			fsType = vfs.getFSType();
			if (fsType != null) {
				if (fsType.trim().toLowerCase().equals("gpfs")) {
					cap = vfs.getCapabilities();
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
	
	public static List<VirtualFSInterface> getGPFSQuotaEnabledFilesystems(){
		
		List<VirtualFSInterface> fss = new ArrayList<VirtualFSInterface>();
		
		Collection<VirtualFSInterface> allVFS;
		try {
			allVFS = NamespaceDirector.getNamespace().getAllDefinedVFS();
		
		} catch (NamespaceException e1) {
			
			throw new IllegalStateException(e1);
		}
		
		for (VirtualFSInterface vfs : allVFS){
			if (isGPFSQuotaEnabledForVFS(vfs))
				fss.add(vfs);
		}
		
		return fss;
		
	}
}
