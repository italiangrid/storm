/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.asynch;

import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.config.Configuration;
import it.grid.storm.scheduler.Scheduler;
import it.grid.storm.scheduler.SchedulerException;
import it.grid.storm.scheduler.SchedulerStatus;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is in charge of periodically polling the DB for newly added
 * requests.
 * 
 * @author EGRID ICTP Trieste
 * @version 1.0
 * @date October 2006
 */
public class AdvancedPicker {

	private static final Logger log = LoggerFactory
			.getLogger(AdvancedPicker.class);

	/* link to scheduler that handles Feeder taks in StoRM! */
	private final Scheduler s = SchedulerFacade.getInstance()
			.crusherScheduler();

	/* Timer object in charge of retrieving info from the DB! */
	private Timer retriever = null;
	private TimerTask retrievingTask = null;

	/* delay time before starting retriever thread, in mssec */
	private final long delay = Configuration.getInstance()
			.getPickingInitialDelay() * 1000;

	/* period of execution of retrieving, in mssec */
	private final long period = Configuration.getInstance()
			.getPickingTimeInterval() * 1000;

	/* boolean that indicates there is a token to abort! */
	private boolean abort = false;

	/* collection with chunks to abort! */
	private Collection<TSURL> abortSURLS = null;

	/* TRequestToken of request to abort! */
	private TRequestToken abortToken = null;

	/**
	 * Method used to command This AdvancedPicker to stop periodic reading of
	 * data from the DB. Any current operation continues until the end, but no
	 * further reading takes place.
	 */
	public void stopIt() {

		log.debug("ADVANCED PICKER: stopped");

		if (retriever != null && retrievingTask != null) {

			retrievingTask.cancel();
			retriever.cancel();
		}

	}

	/**
	 * Method used to command This AdvancedPicker to start periodic reading of
	 * data from the DB.
	 */
	public void startIt() {

		log.debug("ADVANCED PICKER: started");

		retriever = new Timer();

		retrievingTask = new TimerTask() {

			@Override
			public void run() {
				try {
					retrieve();
				} catch (Throwable e) {
					log.error(
							"ADVANCED PICKER: problem when retrieving requests: {}",
							e.getMessage(), e);
				}
			}
		}; // retrieving task

		retriever.scheduleAtFixedRate(retrievingTask, delay, period);
	}

	/**
	 * Private method that connects to the DB and retrieves requests in status
	 * SRM_REQUEST_QUEUED, fetching the TRequestToken, VomsGridUser and
	 * TRequestType; the global status of each request then changes to
	 * SRM_SUCCESS, appropriate Feeders get created and forwarded to the crusher
	 * scheduler.
	 * 
	 * There could be internal errors that get handled as follows:
	 * 
	 * (1) If the request type is not supported, the request is dropped and the
	 * global status transits to SRM_NOT_SUPPORTED; however each chunk data
	 * status remains in SRM_REQUEST_QUEUED because it is impossible to know
	 * where in the DB tables to update the chunk status!
	 * 
	 * (2) If the request type is supported, but the corresponding Feeder cannot
	 * be created, then the global status transits to SRM_FAILURE, as well as
	 * the status of each chunk.
	 * 
	 * (3) If the Scheduler throws any exception, then the global status
	 * transits to SRM_FAILURE, as well as that of each chunk. Under anomalous
	 * circumstances it could be that it is not possible to update the status of
	 * each chunk, in which case the chunk status remains SRM_REQUEST_QUEUED.
	 * This last case is particularly pernicious, so a FATAL log is signalled:
	 * it means the code was not updated!
	 */
	public void retrieve() {

		int crusherCapacity = -1;

		SchedulerStatus status = s.getStatus(0);

		crusherCapacity = status.getRemainingSize();

		Collection<RequestSummaryData> requests = null;
		
			requests = RequestSummaryCatalog
					.getInstance().fetchNewRequests(crusherCapacity);
				

		if (requests.isEmpty()) {

			log.trace("ADVANCED PICKER: no request to dispatch.");

		} else {

			log.info("ADVANCED PICKER: dispatching {} requests.",
					requests.size());

		}

		TRequestType rtype = null;
		TRequestToken rt = null;

		for (RequestSummaryData rsd : requests) {

			rtype = rsd.requestType();
			rt = rsd.requestToken();

			if ((abort) && rt.equals(abortToken)) {

				if (abortSURLS == null) {

					RequestSummaryCatalog.getInstance().abortInProgressRequest(
							abortToken);

				} else {

					RequestSummaryCatalog.getInstance()
							.abortChunksOfInProgressRequest(abortToken,
									abortSURLS);
				}

				abortToken = null;
				abortSURLS = null;
				abort = false;

			} else {

				// process it
				try {

					if (rtype == TRequestType.PREPARE_TO_GET) {

						s.schedule(new PtGFeeder(rsd));

					} else if (rtype == TRequestType.PREPARE_TO_PUT) {

						s.schedule(new PtPFeeder(rsd));

					} else if (rtype == TRequestType.COPY) {

						s.schedule(new CopyFeeder(rsd));

					} else if (rtype == TRequestType.BRING_ON_LINE) {

						s.schedule(new BoLFeeder(rsd));

					} else {

						log.warn(
								"ADVANCED PICKER received request {} of type {} which is "
										+ "NOT currently supported. Dropping request... ",
								rt, rtype);

						log.warn(
								"ADVANCED PICKER: Beware that the global status of "
										+ "request {} will transit to SRM_FAILURE, but each chunk in the "
										+ "request will remain in SRM_REQUEST_QUEUED!",
								rt);

						try {

							RequestSummaryCatalog
									.getInstance()
									.updateGlobalStatus(
											rt,
											new TReturnStatus(
													TStatusCode.SRM_NOT_SUPPORTED,
													"Request of type "
															+ rtype
															+ " is currently not supported!"));

						} catch (InvalidTReturnStatusAttributeException ex) {

							log.error(
									"ADVANCED PICKER! Unable to change global status in "
											+ "DB: {}", ex.getMessage(), ex);

						}

					}

				} catch (InvalidPtGFeederAttributesException e) {

					log.error(
							"ADVANCED PICKER ERROR! PtGFeeder could not be created "
									+ "because of invalid attributes: {}",
							e.getMessage(), e);

					log.error("PtG Request is being dropped: {}",
							rsd.requestToken());

					RequestSummaryCatalog
							.getInstance()
							.failRequest(rsd,
									"Internal error does not allow request to be fed to scheduler.");

				} catch (InvalidPtPFeederAttributesException e) {

					log.error(
							"ADVANCED PICKER ERROR! PtPFeeder could not be created "
									+ "because of invalid attributes: {}",
							e.getMessage(), e);

					log.error("PtP Request is being dropped: {}",
							rsd.requestToken());

					RequestSummaryCatalog
							.getInstance()
							.failRequest(rsd,
									"Internal error does not allow request to be fed to scheduler.");

				} catch (InvalidCopyFeederAttributesException e) {

					log.error(
							"ADVANCED PICKER ERROR! CopyFeeder could not be created "
									+ "because of invalid attributes: {}",
							e.getMessage(), e);

					log.error("Copy Request is being dropped: {}",
							rsd.requestToken());

					RequestSummaryCatalog
							.getInstance()
							.failRequest(rsd,
									"Internal error does not allow request to be fed to scheduler.");

				} catch (InvalidBoLFeederAttributesException e) {

					log.error(
							"ADVANCED PICKER ERROR! BoLFeeder could not be created "
									+ "because of invalid attributes: {}",
							e.getMessage(), e);

					log.error("BoL Request is being dropped: {}",
							rsd.requestToken());

					RequestSummaryCatalog
							.getInstance()
							.failRequest(rsd,
									"Internal error does not allow request to be fed to scheduler.");

				} catch (SchedulerException e) {

					log.error(
							"ADVANCED PICKER ERROR! The request could not be scheduled"
									+ "because of scheduler errors: {}",
							e.getMessage(), e);
					log.error(
							"ADVANCED PICKER ERROR! Request {} of type {} dropped.",
							rsd.requestToken(), rsd.requestType());

					RequestSummaryCatalog
							.getInstance()
							.failRequest(rsd,
									"Internal scheduler has problems accepting request feed.");

				}
			}
		}

		// reset abort flag in case the supplied request token was not found in
		// the internal list of requests (so the logic to reset it was no
		// executed)
		if (abort) {

			abortToken = null;
			abortSURLS = null;
			abort = false;
		}
	}

	/**
	 * Method used to remove the request identified by the supplied
	 * TRequestToken, from the internal queue of Requests that must be
	 * scheduled.
	 * 
	 * If a null TRequestToken is supplied, or some other abort request has been
	 * issued, then FALSE is returned; otherwise TRUE is returned.
	 */
	synchronized public boolean abortRequest(TRequestToken rt) {

		if (abort) {

			return false;
		}

		if (rt == null) {

			return false;
		}

		abortToken = rt;
		abort = true;

		return true;
	}

	/**
	 * Method used to remove chunks of the request identified by the supplied
	 * TRequestToken, with surls given by the collection c. Chunks in the DB get
	 * their status changed and so will not be considered for processing.
	 * 
	 * If a null TRequestToken or Collection is supplied, or some other abort
	 * request has been issued, then FALSE is returned; otherwise TRUE is
	 * returned.
	 */
	synchronized public boolean abortChunksOfRequest(TRequestToken rt,
			Collection<TSURL> c) {

		if (abort) {

			return false;
		}

		if ((rt == null) || (c == null)) {

			return false;
		}

		abortToken = rt;
		abortSURLS = c;
		abort = true;

		return true;
	}

}
