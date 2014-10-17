package it.grid.storm.space;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.srm.types.TSizeInBytes;


public class SimpleSpaceUpdaterHelper implements SpaceUpdaterHelperInterface {

	private static final Logger log = LoggerFactory
		.getLogger(SimpleSpaceUpdaterHelper.class);

	private ReservedSpaceCatalog rsc;
	
	public SimpleSpaceUpdaterHelper() {
		rsc = new ReservedSpaceCatalog();
	}
	
	private StorageSpaceData getStorageSpaceDataForVFS(VirtualFSInterface vfs) {

		return rsc.getStorageSpaceByAlias(vfs.getSpaceTokenDescription());
	}
	
	private void persistStorageSpaceData(StorageSpaceData ssd) {
		
		rsc.updateStorageSpace(ssd);
	}
	
	@Override
	public boolean increaseUsedSpace(VirtualFSInterface vfs, long size) {

		log.debug("Increase {} used space: {} bytes ", vfs.getAliasName(), size);
		StorageSpaceData ssd = getStorageSpaceDataForVFS(vfs);
		
		long newSize = size + ssd.getUsedSpaceSize().value();
		log.debug("Setting new used space value as {} bytes ...", newSize);
		
		if (newSize > ssd.getTotalSpaceSize().value()) {
			log.error("Not enough space! Total space is {}", 
				ssd.getTotalSpaceSize().value());
			return false;
		}
		
		ssd.setUsedSpaceSize(TSizeInBytes.make(newSize, SizeUnit.BYTES));
		
		log.debug("Saving new used space value to persistence ...");
		persistStorageSpaceData(ssd);
		return true;
	}

	@Override
	public boolean decreaseUsedSpace(VirtualFSInterface vfs, long size) {

		log.debug("Decrease {} used space: {} bytes ", vfs.getAliasName(), size);
		StorageSpaceData ssd = getStorageSpaceDataForVFS(vfs);
		
		long newSize = ssd.getUsedSpaceSize().value() - size;
		log.debug("Setting new used space value as {} bytes ...", newSize);
		
		if (newSize < 0) {
			log.warn("Used space is negative! Rounding it to zero ...");
			newSize = 0;
		}
		
		ssd.setUsedSpaceSize(TSizeInBytes.make(newSize, SizeUnit.BYTES));
		
		log.debug("Saving new used space value to persistence ...");
		persistStorageSpaceData(ssd);
		return true;
	}

}
