package it.grid.storm.space;

import it.grid.storm.namespace.CapabilityInterface;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.Quota;


public class SpaceUpdaterHelperFactory {

	public static SpaceUpdaterHelperInterface getSpaceUpdaterHelper(VirtualFSInterface vfs) {
		
		if (vfs == null) {
			throw new IllegalArgumentException("VirtualFSInterface null!");
		}
		
		String fsType = vfs.getFSType();
		CapabilityInterface cap = null;
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
