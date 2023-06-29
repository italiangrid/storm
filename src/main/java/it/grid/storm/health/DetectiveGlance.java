/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.health;

import org.slf4j.Logger;

public class DetectiveGlance {

	private static Logger log = HealthDirector.LOGGER;

	private static long totPtGRequest = 0L;
	private static long totPtPRequest = 0L;
	private static long totSYNCHRequest = 0L;

	/**
     *
     */
	public DetectiveGlance() {

	}

	/**
	 * Get current size of heap in bytes
	 * 
	 * @return long
	 */
	private static long getHeapSize() {

		long heapSize = Runtime.getRuntime().totalMemory();
		return heapSize;
	}

	/**
	 * Get maximum size of heap in bytes. The heap cannot grow beyond this size.
	 * Any attempt will result in an OutOfMemoryException.
	 * 
	 * @return long
	 */
	private static long getHeapMaxSize() {

		long heapMaxSize = Runtime.getRuntime().maxMemory();
		return heapMaxSize;
	}

	/**
	 * Get amount of free memory within the heap in bytes. This size will increase
	 * after garbage collection and decrease as new objects are created.
	 * 
	 * @return long
	 */
	private static long getHeapFreeSize() {

		long heapFreeSize = Runtime.getRuntime().freeMemory();
		return heapFreeSize;
	}

	public static void addPtGRequests(int nrPtGRequest) {

		totPtGRequest += nrPtGRequest;
	}

	public static void addPtPRequests(int nrPtPRequest) {

		totPtPRequest += nrPtPRequest;
	}

	public static void addSynchRequest(int nrSynchRequest) {

		totSYNCHRequest += nrSynchRequest;
	}

	public StoRMStatus haveaLook() {

	  log.trace("DetectiveGlance.haveaLook");
		StoRMStatus stormStatus = new StoRMStatus();
		stormStatus.setHeapFreeSize(getHeapFreeSize());
		stormStatus.setMAXHeapSize(getHeapMaxSize());
		stormStatus.setHeapSize(getHeapSize());
		SimpleBookKeeper bk = HealthDirector.getHealthMonitor()
			.getSimpleBookKeeper();
		if (bk != null) {

			int ptgReq = bk.getNumberOfRequest(OperationType.PTG);
			// Sum partial to the Total
			addPtGRequests(ptgReq);
			int ptgSucc = bk.getNumberOfSuccess(OperationType.PTG);
			long meanPtG = bk.getMeansDuration(OperationType.PTG);

			stormStatus.setPtGNumberRequests(ptgReq);
			stormStatus.setPtGSuccessRequests(ptgSucc);
			stormStatus.setPtGMeanDuration(meanPtG);
			stormStatus.setTotalPtGRequest(totPtGRequest);

			int ptpReq = bk.getNumberOfRequest(OperationType.PTP);
			// Sum partial to the Total
			addPtPRequests(ptpReq);
			int ptpSucc = bk.getNumberOfSuccess(OperationType.PTP);
			long meanPtP = bk.getMeansDuration(OperationType.PTP);

			stormStatus.setPtPNumberRequests(ptpReq);
			stormStatus.setPtPSuccessRequests(ptpSucc);
			stormStatus.setPtPMeanDuration(meanPtP);
			stormStatus.setTotalPtPRequest(totPtPRequest);

			int synchRequest = bk.getNumberOfSynchRequest();
			addSynchRequest(synchRequest);

			stormStatus.setSynchRequest(synchRequest);

			stormStatus.calculateLifeTime();
			bk.cleanLogBook();
			log.debug(" .. glance completed.");
		} else {
			log.warn("No SympleBookKeper available from HealtMonitor!");
		}
		return stormStatus;
	}
}
