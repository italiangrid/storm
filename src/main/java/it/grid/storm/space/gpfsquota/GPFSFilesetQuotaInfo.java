package it.grid.storm.space.gpfsquota;

import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.namespace.model.VirtualFS;
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

	public VirtualFS getVFS();

	public SizeUnit getSizeUnit();
}
