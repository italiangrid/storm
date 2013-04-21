package it.grid.storm.space.gpfsquota;

import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.srm.types.TSizeInBytes;

public interface GPFSFilesetQuotaInfo {

	public String getFilesetName();

	public long getBlockUsage();

	public TSizeInBytes getBlockUsageAsTSize();

	public long getBlockHardLimit();

	public TSizeInBytes getBlockHardLimitAsTSize();

	public long getBlockSoftLimit();

	public TSizeInBytes getBlockSoftLimitAsTSize();

	public boolean isQuotaEnabled();

	public VirtualFSInterface getVFS();

	public SizeUnit getSizeUnit();
}
