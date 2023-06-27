package it.grid.storm.space;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.namespace.model.VirtualFS;

public class SimpleSpaceUpdaterHelper implements SpaceUpdaterHelperInterface {

	private static final Logger log = LoggerFactory
		.getLogger(SimpleSpaceUpdaterHelper.class);

	private ReservedSpaceCatalog rsc;
	
	public SimpleSpaceUpdaterHelper() {
		rsc = new ReservedSpaceCatalog();
	}
	
	private StorageSpaceData getStorageSpaceDataForVFS(VirtualFS vfs) {

		return rsc.getStorageSpaceByAlias(vfs.getSpaceTokenDescription());
	}
	
	@Override
	public boolean increaseUsedSpace(VirtualFS vfs, long size) {

		log.debug("Increase {} used space: {} bytes ", vfs.getAliasName(), size);

		if (size < 0) {
			log.error("Size to add is a negative value: {}", size);
			return false;
		}
		if (size == 0) {
			log.debug("Size is zero, vfs {} used space won't be increased!",
				vfs.getAliasName());
			return true;
		}

		log.debug("Get StorageSpaceData from vfs ...");
		StorageSpaceData ssd = getStorageSpaceDataForVFS(vfs);

		if (ssd == null) {
			log.error("Unable to get StorageSpaceData from alias name {}",
				vfs.getAliasName());
			return false;
		}

		return rsc.increaseUsedSpace(ssd.getSpaceToken().getValue(), size);
	}

	@Override
	public boolean decreaseUsedSpace(VirtualFS vfs, long size) {

		log.debug("Decrease {} used space: {} bytes ", vfs.getAliasName(), size);

		if (size < 0) {
			log.error("Size to remove is a negative value: {}", size);
			return false;
		}
		if (size == 0) {
			log.debug("Size is zero, vfs {} used space won't be decreased!",
				vfs.getAliasName());
			return true;
		}

		log.debug("Get StorageSpaceData from vfs ...");
		StorageSpaceData ssd = getStorageSpaceDataForVFS(vfs);

		if (ssd == null) {
			log.error("Unable to get StorageSpaceData from alias name {}",
				vfs.getAliasName());
			return false;
		}

		return rsc.decreaseUsedSpace(ssd.getSpaceToken().getValue(), size);
	}

}
