package it.grid.storm.space.gpfsquota;

import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.filesystem.swig.quota_info;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.util.GPFSSizeHelper;

/**
 * Describes information about quota block limits on a GPFS fileset.
 * 
 *
 */
public class GPFSQuotaInfo implements GPFSFilesetQuotaInfo {

	public static GPFSQuotaInfo fromNativeQuotaInfo(VirtualFSInterface fs,
		quota_info qi) {

		return new GPFSQuotaInfo(fs, qi);
	}

	private long blockHardLimit;
	private long blockSoftLimit;
	private long blockUsage;

	private String filesetName;
	private boolean quotaEnabled = false;
	private VirtualFSInterface VFS;

	private GPFSQuotaInfo(VirtualFSInterface fs, quota_info qi) {

		this.VFS = fs;
		this.filesetName = qi.getFileset_name();
		this.blockUsage = qi.getBlock_usage();
		this.blockHardLimit = qi.getBlock_hard_limit();
		this.blockSoftLimit = qi.getBlock_soft_limit();
		this.quotaEnabled = true;
	}

	public long getBlockHardLimit() {

		return blockHardLimit;
	}

	public long getBlockSoftLimit() {

		return blockSoftLimit;
	}

	public long getBlockUsage() {

		return blockUsage;
	}

	public String getFilesetName() {

		return filesetName;
	}

	@Override
	public SizeUnit getSizeUnit() {

		return SizeUnit.BYTES;
	}

	public VirtualFSInterface getVFS() {

		return VFS;
	}

	@Override
	public boolean isQuotaEnabled() {

		return quotaEnabled;
	}

	public void setBlockHardLimit(long blockHardLimit) {

		this.blockHardLimit = blockHardLimit;
	}

	public void setBlockSoftLimit(long blockSoftLimit) {

		this.blockSoftLimit = blockSoftLimit;
	}

	public void setBlockUsage(long blockUsage) {

		this.blockUsage = blockUsage;
	}

	public void setFilesetName(String filesetName) {

		this.filesetName = filesetName;
	}

	public void setVFS(VirtualFSInterface vFS) {

		VFS = vFS;
	}

	@Override
	public String toString() {
		return "GPFSQuotaInfo [filesetName=" + filesetName + ", blockUsage="
			+ getBlockUsageAsTSize() + ", blockHardLimit=" + getBlockHardLimitAsTSize() + ", blockSoftLimit="
			+ getBlockSoftLimitAsTSize() + ", quotaEnabled=" + quotaEnabled + "]";
	}

	@Override
	public TSizeInBytes getBlockUsageAsTSize() {

		return TSizeInBytes.make(GPFSSizeHelper.getBytesFromKIB(getBlockUsage()), 
			getSizeUnit());
	}

	@Override
	public TSizeInBytes getBlockHardLimitAsTSize() {

		return TSizeInBytes.make(GPFSSizeHelper.getBytesFromKIB(getBlockHardLimit()), 
			getSizeUnit());
	}

	@Override
	public TSizeInBytes getBlockSoftLimitAsTSize() {

		return TSizeInBytes.make(GPFSSizeHelper.getBytesFromKIB(getBlockSoftLimit()), 
			getSizeUnit());
	}

}
