package it.grid.storm.space;

public interface SpaceUpdaterHelperInterface {

	public boolean increaseUsedSpace(long size);

	public boolean decreaseUsedSpace(long size);

}
