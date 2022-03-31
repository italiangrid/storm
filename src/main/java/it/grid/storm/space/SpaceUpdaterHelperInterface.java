package it.grid.storm.space;

import it.grid.storm.namespace.model.VirtualFS;

public interface SpaceUpdaterHelperInterface {

	public boolean increaseUsedSpace(VirtualFS vfs, long size);

	public boolean decreaseUsedSpace(VirtualFS vfs, long size);

}
