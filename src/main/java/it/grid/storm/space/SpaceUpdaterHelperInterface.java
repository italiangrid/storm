package it.grid.storm.space;

import it.grid.storm.namespace.VirtualFSInterface;

public interface SpaceUpdaterHelperInterface {

	public boolean increaseUsedSpace(VirtualFSInterface vfs, long size);

	public boolean decreaseUsedSpace(VirtualFSInterface vfs, long size);

}
