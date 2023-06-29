/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * 
 */
package it.grid.storm.tape.recalltable.resources;

import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.tape.recalltable.TapeRecallCatalog;
import it.grid.storm.tape.recalltable.TapeRecallException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ritz
 * 
 */
@Path("/recalltable/cardinality/tasks/")
public class TasksCardinality {

	private static final Logger log = LoggerFactory.getLogger(TasksCardinality.class);

	/**
	 * Get the number of tasks that are queued.
	 * 
	 * @return
	 * @throws TapeRecallException
	 */
	@GET
	@Path("/queued")
	@Produces("text/plain")
	public Response getNumberQueued() {

		int nQueued = 0;

		try {

			TapeRecallCatalog rtCat = new TapeRecallCatalog();
			nQueued = rtCat.getNumberTaskQueued();

		} catch (DataAccessException e) {

			String errorStr = "Unable to use RecallTable DB.";
			log.error(errorStr, e);
			return Response.serverError().entity(errorStr).build();
		}

		if (nQueued > 0) {
			log.info("Number of tasks queued = {}", nQueued);
		} else {
			log.trace("Number of tasks queued = {}", nQueued);
		}
		return Response.ok().entity(Integer.toString(nQueued)).build();
	}

	/**
	 * Get the number of tasks that are ready for take over.
	 * 
	 * @return
	 */
	@GET
	@Path("/readyTakeOver")
	@Produces("text/plain")
	public Response getReadyForTakeover() {

		int nReadyForTakeover = 0;

		try {

			TapeRecallCatalog rtCat = new TapeRecallCatalog();
			nReadyForTakeover = rtCat.getReadyForTakeOver();

		} catch (DataAccessException e) {

			String errorStr = "Unable to use RecallTable DB.";
			log.error(errorStr, e);
			return Response.serverError().entity(errorStr).build();
		}

		log.debug("Number of tasks queued = {}", nReadyForTakeover);
		return Response.ok().entity(Integer.toString(nReadyForTakeover)).build();
	}

}