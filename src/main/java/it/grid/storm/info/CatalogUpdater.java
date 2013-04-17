package it.grid.storm.info;

import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.types.InvalidPFNAttributeException;
import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.concurrency.NamedThread;
import it.grid.storm.concurrency.NamedThreadFactory;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TransferObjectDecodingException;
import it.grid.storm.space.DUResult;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalogUpdater {

	private static final Logger LOG = LoggerFactory
		.getLogger(CatalogUpdater.class);
	private final ExecutorService saver = Executors.newFixedThreadPool(1,
		new NamedThreadFactory("CatalogSaver"));

	// Reference to the Catalog
	private static final ReservedSpaceCatalog spaceCatalog = new ReservedSpaceCatalog();

	public Future<?> saveData(TSpaceToken token, DUResult duResult)
		throws SAInfoException {

		Future<?> saverFuture = null;
		try {
			saverFuture = saver.submit(new NamedThread(new SaveSA(token, duResult),
				"SaverThread"));
		} catch (RejectedExecutionException rej) {
			LOG.error("Unable to start Saver tasks." + rej.getMessage());
		}
		return saverFuture;
	}

	public void stopSaver() {

		try {
			shutdown(false, 1000);
		} catch (InterruptedException ex) {
			LOG.debug("DU Info SAVER is interrupted.");
		}
	}

	private void shutdown(boolean interrupt, long waitMillis)
		throws InterruptedException {

		if (interrupt) {
			LOG.debug("Tasks killed: " + saver.shutdownNow().size());
		} else {
			saver.shutdown();
		}
		try {
			saver.awaitTermination(waitMillis, TimeUnit.MILLISECONDS);
		} finally {
			LOG.debug("Tasks killed: " + saver.shutdownNow().size());
		}
		LOG.debug("SAVER is terminated? : " + saver.isTerminated());
	}

	private class SaveSA implements Runnable {

		private DUResult duResult;
		private TSpaceToken sT;

		public SaveSA(TSpaceToken spaceToken, DUResult duResult) {

			super();
			this.duResult = duResult;
			sT = spaceToken;
		}

		public void run() {

			// TODO errors are not managed in this function
			LOG.debug("Saving info into DB... ");
			StorageSpaceData ssd = null;
			if (SpaceInfoManager.getInstance().testMode.get()) {
				// this IS a TEST!
				// Create a fake SSD
				ssd = new StorageSpaceData();
				try {
					PFN spaceFN = PFN.make(duResult.getAbsRootPath());
					ssd.setSpaceToken(sT);
					ssd.setSpaceFileName(spaceFN);
				} catch (InvalidPFNAttributeException e) {
					LOG.error("Unable to create PFN. " + e);
				}

			} else {
				// This is not a TEST!
				// Retrieve SA from Catalog
				try {
					ssd = spaceCatalog.getStorageSpace(sT);
				} catch (TransferObjectDecodingException e) {
					LOG
						.error("Unable to build StorageSpaceData from StorageSpaceTO. TransferObjectDecodingException: "
							+ e.getMessage());
				} catch (DataAccessException e) {
					LOG.error("Unable to build get StorageSpaceTO. DataAccessException: "
						+ e.getMessage());
				}
			}

			long usedSize = duResult.getSize();

			try {
				TSizeInBytes us = TSizeInBytes.make(usedSize, SizeUnit.BYTES);
				ssd.setUsedSpaceSize(us);
			} catch (InvalidTSizeAttributesException e) {
				LOG.error("Negative size?");
			}
			if (SpaceInfoManager.getInstance().testMode.get()) {
				// this IS a TEST!
				LOG.debug("Saved SSD info into the DB ");
			} else {
				// This is not a TEST!
				// Update the SSD into the DB
				spaceCatalog.updateStorageSpace(ssd);
			}

			// Notify the manager about the saving was success
			SpaceInfoManager.getInstance().savedSA(duResult);
			LOG.debug(String.format("DU info of %s is saved into DB.",
				duResult.getAbsRootPath()));
		}

	}
}
