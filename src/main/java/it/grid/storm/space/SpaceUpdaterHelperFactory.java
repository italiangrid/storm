package it.grid.storm.space;

import it.grid.storm.namespace.model.Capability;
import it.grid.storm.namespace.model.Quota;
import it.grid.storm.namespace.model.VirtualFS;


public class SpaceUpdaterHelperFactory {

	public static SpaceUpdaterHelperInterface getSpaceUpdaterHelper(VirtualFS vfs) {
		
		if (vfs == null) {
			throw new IllegalArgumentException("VirtualFS null!");
		}
		
		String fsType = vfs.getFSType();
		Capability cap = null;
		Quota quota = null;
		
		if (fsType != null) {
			if (fsType.trim().toLowerCase().equals("gpfs")) {
				cap = vfs.getCapabilities();
					if (cap != null) {
						quota = cap.getQuota();
					}
					if (quota != null) {
						if ((quota.getDefined()) && (quota.getEnabled())) {
							return new NullSpaceUpdaterHelper();
						}
					}
			}
		}
		
		return new SimpleSpaceUpdaterHelper();
	}
	
}
