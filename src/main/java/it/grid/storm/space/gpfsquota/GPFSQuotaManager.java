package it.grid.storm.space.gpfsquota;

import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.concurrency.NamedThreadFactory;
import it.grid.storm.config.Configuration;
import it.grid.storm.filesystem.FilesystemError;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.util.VirtualFSHelper;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * GPFSQuotaManager. Currently supports only GPFS fileset quotas.
 * This manager starts periodic tasks that fetch quota information from gpfs fs
 * and update the space area data on the Storm database. If no quota limits
 * are enforced for a given fileset, the total size is computed starting from
 * the free space available on the filesystem.
 * 
 * The manager must be started with the {@link #start()} method, and shutdown
 * with the {@link #shutdown()} method.
 * 
 * Quota calculation can also be triggered with the {@link #triggerComputeQuotas()}
 * method.
 * 
 * 
 * This is a singleton. 
 * 
 * @author Andrea Ceccanti <andrea.ceccanti@cnaf.infn.it>
 */
public enum GPFSQuotaManager {

	INSTANCE;

	private static final Logger log = LoggerFactory
		.getLogger(GPFSQuotaManager.class);

	/**
	 * Relax period (in msecs). Request to compute are accepted only if the
	 * relax period has expired since last request submission time. 
	 */
	private static final long DEFAULT_RELAX_PERIOD = 15000;

	/**
	 * The underlying execution service
	 */
	private ScheduledExecutorService workerScheduler;

	/**
	 * The completion service used to block and wait for the submitted tasks.
	 */
	private CompletionService<GPFSFilesetQuotaInfo> quotaService;

	/**
	 * The list of GPFS filesystems which have quota enabled.
	 */
	private List<VirtualFSInterface> quotaEnabledFilesystems;

	/**
	 * The last exception thrown by a GPFS quota calculation job. 
	 */
	private Throwable lastFailure = null;

	/**
	 * The time when the last quota calculation job was submitted.
	 */
	private long lastSubmissionTime = 0L;

	/**
	 * A lock to sync access to {@link #lastSubmissionTime}
	 */
	private Object submissionTimeLock = new Object();

	private void configureScheduler() {

		// 1 submitter, 1 thread per for quota enabled FS
		int numThreads = 1 + quotaEnabledFilesystems.size();

		workerScheduler = Executors.newScheduledThreadPool(numThreads,
			new NamedThreadFactory("GPFSQuotaWorker"));

		quotaService = new ExecutorCompletionService<GPFSFilesetQuotaInfo>(
			workerScheduler);
	}

	private void configureWorkers() {

		long refreshPeriod = Configuration.getInstance()
				.getGPFSQuotaRefreshPeriod();
		
		log.info("GPFSQuotaManager refresh period (in seconds): %d",
			refreshPeriod);

		workerScheduler.scheduleWithFixedDelay(new QuotaJobSubmitter(), 0,
			refreshPeriod, 
			TimeUnit.SECONDS);

	}

	public synchronized void start() {

		log.info("Starting GPFSQuotaManager...");
		quotaEnabledFilesystems = VirtualFSHelper.getGPFSQuotaEnabledFilesystems();

		if (quotaEnabledFilesystems.size() == 0) {
			log.info("No Quota enabled GPFS filesystems found.");
			return;
		}

		configureScheduler();
		configureWorkers();

	}

	private StorageSpaceData getStorageSpaceDataForVFS(VirtualFSInterface vfs) {

		ReservedSpaceCatalog rsc = new ReservedSpaceCatalog();
		String spaceToken = vfs.getSpaceTokenDescription();
		return rsc.getStorageSpaceByAlias(spaceToken);
	}

	private void persistStorageSpaceData(StorageSpaceData ssd) {

		ReservedSpaceCatalog rsc = new ReservedSpaceCatalog();
		rsc.updateStorageSpace(ssd);
	}

	private void handleNoLimitsQuota(GPFSFilesetQuotaInfo info, StorageSpaceData ssd){
		log.debug("Quota enabled on fs rooted at {} but no limits enforced.", 
			info.getVFS().getRootPath());
		
		try {
			
			long freeSizeFromFS = info.getVFS().getFSDriverInstance().get_free_space();
			TSizeInBytes freeSizeInBytes = TSizeInBytes.make(freeSizeFromFS, 
				SizeUnit.BYTES);	
			
			ssd.setTotalGuaranteedSize(freeSizeInBytes);
			ssd.setTotalSpaceSize(freeSizeInBytes);
			
		} catch (FilesystemError e) {
			log.error("Error computing free space on fs rooted at {}. {}", info.getVFS().getRootPath(),
				e.getMessage(), e);
			
			ssd.setTotalGuaranteedSize(null);
			ssd.setTotalSpaceSize(null);
			
		} catch (NamespaceException e) {
			log.error("Error accessing fs driver for fs rooted at {}. {}",
				info.getVFS().getRootPath(),
				e.getMessage(),
				e);
			
			ssd.setTotalGuaranteedSize(null);
			ssd.setTotalSpaceSize(null);
			
		}
	}
	
	private void handleQuotaInfo(GPFSFilesetQuotaInfo info) {

		StorageSpaceData ssd = getStorageSpaceDataForVFS(info.getVFS());

		ssd.setUsedSpaceSize(info.getBlockUsageAsTSize());
		
		if (info.getBlockSoftLimit() > 0){
			ssd.setTotalSpaceSize(info.getBlockSoftLimitAsTSize());
			ssd.setTotalGuaranteedSize(info.getBlockSoftLimitAsTSize());
		}else{
			handleNoLimitsQuota(info, ssd);
		}

		persistStorageSpaceData(ssd);
		log.debug("Persisted storage space data for quota info: {}", info);
	}

	class QuotaJobSubmitter implements Runnable {

		@Override
		public void run() {

			int completedTasks = 0;

			for (VirtualFSInterface vfs : quotaEnabledFilesystems) {
				log.info("Submitting GPFS quota info for vfs rooted at : {}",
					vfs.getRootPath());

				quotaService.submit(new GetGPFSFilesetQuotaInfoCommand(vfs));
			}

			synchronized (submissionTimeLock) {
				lastSubmissionTime = System.currentTimeMillis();
			}

			while (completedTasks < quotaEnabledFilesystems.size()) {

				try {

					GPFSFilesetQuotaInfo info = quotaService.take().get();
					handleQuotaInfo(info);
					completedTasks++;

				} catch (InterruptedException e) {
					log.info("GPFS Submitter execution interrupted. Exiting...");
					return;

				} catch (ExecutionException e) {
					log.error("Error getting GPFS quota info: {}", e.getMessage(), e);
					completedTasks++;
					setLastFailure(e);
				}
			}
		}
	}

	public synchronized void triggerComputeQuotas() {

		if (quotaEnabledFilesystems.size() == 0) {
			log.info("No Quota enabled GPFS filesystems found.");
			return;
		}

		long currentTime = System.currentTimeMillis();

		if (currentTime - lastSubmissionTime > DEFAULT_RELAX_PERIOD) {
			log.info("Triggering on demand GPFS quota computation.");
			workerScheduler.submit(new QuotaJobSubmitter());
		} else {
			log
				.info(
					"Skipping GPFS quota computation since last submission is within the last {} seconds.",
					TimeUnit.MILLISECONDS.toSeconds(DEFAULT_RELAX_PERIOD));
		}
	}


	private synchronized void setLastFailure(Throwable t) {

		lastFailure = t;
	}

	public synchronized Throwable getLastFailure() {

		return lastFailure;
	}

	public synchronized void resetFailure() {

		lastFailure = null;
	}

	public synchronized void shutdown() {
		log.info("GPFSQuotaManager shutting down...");
		if (workerScheduler != null)
			workerScheduler.shutdownNow();
	}

}
