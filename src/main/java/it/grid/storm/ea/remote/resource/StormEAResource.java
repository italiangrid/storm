package it.grid.storm.ea.remote.resource;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

/**
* 
*/

import it.grid.storm.ea.ExtendedAttributesException;
import it.grid.storm.ea.StormEA;
import it.grid.storm.ea.remote.Constants;

/**
 * @author Michele Dibenedetto
 */
@Path("/" + Constants.RESOURCE + "/" + Constants.VERSION + "/{filePath}")
public class StormEAResource {

	private static final Logger log = LoggerFactory
		.getLogger(StormEAResource.class);

	private static final StormEA ea = StormEA.getDefaultStormExtendedAttributes();

	@GET
	@Path("/" + Constants.ADLER_32)
	@Produces("text/plain")
	public String getAdler32Checksum(@PathParam("filePath") String filePath)
		throws WebApplicationException {

		RequestParameters parameters = new RequestParameters.Builder(filePath)
			.build();
		log.info("Getting {} checksum for file {}",
		  Constants.ADLER_32,
			parameters.getFilePathDecoded());

		String checksum;
		try {
			checksum = ea.getChecksum(parameters.getFilePathDecoded(), Constants.ADLER_32);
		} catch (ExtendedAttributesException e) {
		  log.error(e.getMessage(), e);
			throw new WebApplicationException(Response.status(INTERNAL_SERVER_ERROR)
				.entity("Unable to get the checksum, Extended attributes management failure")
				.build());
		}
		log.info("Checksum for file {} is {}", filePath, checksum);
		return checksum;
	}

	@PUT
	@Path("/" + Constants.ADLER_32)
	@Produces("text/plain")
	public void setAdler32Checksum(@PathParam("filePath") String filePath,
		@QueryParam(Constants.CHECKSUM_VALUE_KEY) String checksum)
		throws WebApplicationException {

		RequestParameters parameters = new RequestParameters.Builder(filePath)
			.checksum(checksum).build();

		log.info("Setting {} checksum for file {} with value {}",
		  Constants.ADLER_32,
		  parameters.getFilePathDecoded(),
		  parameters.getChecksumDecoded());

		try {
			ea.setChecksum(parameters.getFilePathDecoded(), parameters.getChecksumDecoded(),
					Constants.ADLER_32);
		} catch (ExtendedAttributesException e) {
		  log.error(e.getMessage(), e);
			throw new WebApplicationException(Response.status(INTERNAL_SERVER_ERROR)
				.entity("Unable to set the checksum, Extended attributes management failure")
				.build());
		}
	}
}