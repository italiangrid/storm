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

package it.grid.storm.info.remote.resources;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.info.SpaceInfoManager;
import it.grid.storm.info.model.SpaceStatusSummary;
import it.grid.storm.info.remote.Constants;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.space.quota.BackgroundGPFSQuota;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TSizeInBytes;

import javax.ws.rs.Consumes;
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

import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;

@Path("/" + Constants.RESOURCE)
public class SpaceStatusResource {

	private static final Logger log = LoggerFactory
		.getLogger(SpaceStatusResource.class);

	private static final ReservedSpaceCatalog catalog = new ReservedSpaceCatalog();

	@GET
	@Produces("application/json")
	@Path("/{alias}")
	public String getStatusSummary(@PathParam("alias") String saAlias) {

		String result = "";
		log.debug("Received call getStatusSummary for SA '" + saAlias + "'");

		int quotaDefined = SpaceInfoManager.getInstance().getQuotasDefined();
		if (quotaDefined > 0) {
			// Update SA used space using quota defined..
			BackgroundGPFSQuota.getInstance().submitGPFSQuota();
			log.info("Submitted an asynchronous GPFS Quota job");
		}

		// Load SA values
		SpaceStatusSummary saSum;
		try {
			saSum = SpaceStatusSummary.createFromDB(saAlias);
		} catch (IllegalArgumentException e) {
			log
				.info("Unable to load requested space status summary from database. IllegalArgumentException: "
					+ e.getMessage());
			ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
			responseBuilder.status(Response.Status.NOT_FOUND);
			responseBuilder
				.entity("Unable to load requested space status info from database");
			throw new WebApplicationException(responseBuilder.build());
		}
		result = saSum.getJsonFormat();
		return result;
	}

	@PUT
	@Path("/{alias}/" + Constants.UPDATE_OPERATION)
	@Consumes("text/plain")
	public void putStatusSummary(@PathParam("alias") String saAlias,
		@QueryParam(Constants.TOTAL_SPACE_KEY) Long totalSpace,
		@QueryParam(Constants.USED_SPACE_KEY) Long usedSpace,
		@QueryParam(Constants.RESERVED_SPACE_KEY) Long reservedSpace,
		@QueryParam(Constants.UNAVALILABLE_SPACE_KEY) Long unavailableSpace)
		throws WebApplicationException {

		// Decode received parameters
		String saAliasDecoded;
		try {
			saAliasDecoded = URLDecoder.decode(saAlias.trim(),
				Constants.ENCODING_SCHEME);
		} catch (UnsupportedEncodingException e) {
			log.error("Unable to decode parameters. UnsupportedEncodingException : "
				+ e.getMessage());
			ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
			responseBuilder.status(Response.Status.BAD_REQUEST);
			responseBuilder
				.entity("Unable to decode paramethesr, unsupported encoding \'"
					+ Constants.ENCODING_SCHEME + "\'");
			throw new WebApplicationException(responseBuilder.build());
		}
		log.debug("Decoded saAlias = " + saAliasDecoded);
		/*
		 * saAlias is mandatory if totalSpace is set then all parameters are needed
		 */
		if (saAliasDecoded == null
			|| saAliasDecoded.equals("")
			|| (totalSpace != null && (usedSpace == null || reservedSpace == null || unavailableSpace == null))) {
			log
				.error("Unable to update space alias status. Some parameters are missing : saAlias "
					+ saAliasDecoded
					+ " totalSpace "
					+ totalSpace
					+ " usedSpace "
					+ usedSpace
					+ " reservedSpace "
					+ reservedSpace
					+ " unavailableSpace " + unavailableSpace);
			ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
			responseBuilder.status(Response.Status.BAD_REQUEST);
			responseBuilder
				.entity("Unable to evaluate permissions. Some parameters are missing");
			throw new WebApplicationException(responseBuilder.build());
		}
		/*
		 * If not null size parameters must be >= 0
		 */
		if ((totalSpace != null && totalSpace < 0)
			|| (usedSpace != null && usedSpace < 0)
			|| (reservedSpace != null && reservedSpace < 0)
			|| (unavailableSpace != null && unavailableSpace < 0)) {
			log
				.error("Unable to update space alias status. Some size parameters are lower than zero : totalSpace "
					+ totalSpace
					+ " usedSpace "
					+ usedSpace
					+ " reservedSpace "
					+ reservedSpace + " unavailableSpace " + unavailableSpace);
			ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
			responseBuilder.status(Response.Status.BAD_REQUEST);
			responseBuilder
				.entity("Unable to evaluate permissions. Some size parameters are lower than zero");
			throw new WebApplicationException(responseBuilder.build());
		}
		// returns null if no rows found
		StorageSpaceData storageSpaceData = catalog
			.getStorageSpaceByAlias(saAliasDecoded);
		if (storageSpaceData == null) {
			log.error("The storage space with alias \'" + saAliasDecoded
				+ "\' is not on StoRM Database");
			ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
			responseBuilder.status(Response.Status.NOT_FOUND);
			responseBuilder
				.entity("Unable to update space alias status. Some parameters are not well formed");
			throw new WebApplicationException(responseBuilder.build());
		}
		SpaceStatusSummary spaceStatusSummary;
		if (totalSpace != null) {
			spaceStatusSummary = new SpaceStatusSummary(saAlias, totalSpace);
		} else {
			spaceStatusSummary = new SpaceStatusSummary(saAlias, storageSpaceData
				.getTotalSpaceSize().value());
		}
		if (usedSpace != null) {
			spaceStatusSummary.setUsedSpace(usedSpace);
		}
		if (reservedSpace != null) {
			spaceStatusSummary.setReservedSpace(reservedSpace);
		}
		if (unavailableSpace != null) {
			spaceStatusSummary.setUnavailableSpace(unavailableSpace);
		}
		try {
			updateSASummary(storageSpaceData, spaceStatusSummary, totalSpace != null);
		} catch (IllegalArgumentException e) {
			log.error("Unable to update storage space \'" + saAliasDecoded
				+ "\' with the provided space values. IllegalArgumentException: "
				+ e.getMessage());
			ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
			responseBuilder.status(Response.Status.BAD_REQUEST);
			responseBuilder
				.entity("Unable to update space alias status. Some parameters are not valid");
			throw new WebApplicationException(responseBuilder.build());
		}
	}

	/**
	 * @param storageSpaceData
	 * @param spaceStatusSummary
	 * @throws IllegalArgumentException
	 */
	private void updateSASummary(StorageSpaceData storageSpaceData,
		SpaceStatusSummary spaceStatusSummary, boolean updateTotalSpace)
		throws IllegalArgumentException {

		// fill in the StorageSpaceData the provided values
		try {
			if (updateTotalSpace && spaceStatusSummary.getTotalSpace() >= 0) {
				storageSpaceData.setTotalSpaceSize(TSizeInBytes.make(
					spaceStatusSummary.getTotalSpace(), SizeUnit.BYTES));
			}
			if (spaceStatusSummary.getUsedSpace() >= 0) {
				storageSpaceData.setUsedSpaceSize(TSizeInBytes.make(
					spaceStatusSummary.getUsedSpace(), SizeUnit.BYTES));
			}
			if (spaceStatusSummary.getReservedSpace() >= 0) {
				storageSpaceData.setReservedSpaceSize(TSizeInBytes.make(
					spaceStatusSummary.getReservedSpace(), SizeUnit.BYTES));
			}
			if (spaceStatusSummary.getUnavailableSpace() >= 0) {
				storageSpaceData.setUnavailableSpaceSize(TSizeInBytes.make(
					spaceStatusSummary.getUnavailableSpace(), SizeUnit.BYTES));
			}
		} catch (InvalidTSizeAttributesException e) {
			throw new IllegalArgumentException(
				"Unable to produce the TSizeInBytes object for some of the SpaceStatusSummary fields:"
					+ spaceStatusSummary.toString());
		}
		catalog.updateStorageSpace(storageSpaceData);
	}
}
