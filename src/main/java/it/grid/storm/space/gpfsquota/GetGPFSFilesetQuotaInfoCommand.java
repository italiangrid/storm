package it.grid.storm.space.gpfsquota;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.filesystem.swig.gpfs;
import it.grid.storm.namespace.model.VirtualFS;

/**
 * Computes GPFS fileset quota by leveraging {@link gpfs#get_fileset_quota_info(String)}.
 */
public class GetGPFSFilesetQuotaInfoCommand implements
	Callable<GPFSFilesetQuotaInfo> {

	private static final Logger log = LoggerFactory
		.getLogger(GetGPFSFilesetQuotaInfoCommand.class);

	private VirtualFS vfs;

	public GetGPFSFilesetQuotaInfoCommand(VirtualFS vfs) {

		this.vfs = vfs;
	}

	@Override
	public GPFSFilesetQuotaInfo call() throws Exception {

		String fsRoot = vfs.getRootPath();
		log.debug("GPFS quota info command running for fs rooted at {}", fsRoot);

		if (!(vfs.getFSDriverInstance() instanceof gpfs))
			throw new IllegalArgumentException(
				"VFS driver is not GPFS for fs rooted at " + fsRoot);

		gpfs fs = (gpfs) vfs.getFSDriverInstance();

		if (!fs.is_quota_enabled(fsRoot)) {
			log.error("GPFS Quota not enabled on fileset rooted at {}", fsRoot);
			return null;
		}

		GPFSQuotaInfo info = GPFSQuotaInfo.fromNativeQuotaInfo(vfs,
			fs.get_fileset_quota_info(fsRoot));
		
		log.debug("Computed GPFS fileset quota info for fs rooted at {}: {}",
			fsRoot, info);
		
		return info;
	}

}
