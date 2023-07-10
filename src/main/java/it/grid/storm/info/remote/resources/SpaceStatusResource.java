/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.info.remote.resources;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import it.grid.storm.info.SpaceInfoManager;
import it.grid.storm.info.model.SpaceStatusSummary;
import it.grid.storm.info.remote.Constants;
import it.grid.storm.space.gpfsquota.GPFSQuotaManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/" + Constants.RESOURCE)
public class SpaceStatusResource {

  private static final Logger log = LoggerFactory.getLogger(SpaceStatusResource.class);

  @GET
  @Produces("application/json")
  @Path("/{alias}")
  public String getStatusSummary(@PathParam("alias") String saAlias) {

    String result = "";
    log.debug("Received call getStatusSummary for SA '{}'", saAlias);

    int quotaDefined = SpaceInfoManager.getInstance().getQuotasDefined();
    if (quotaDefined > 0) {
      // Update SA used space using quota defined..
      GPFSQuotaManager.INSTANCE.triggerComputeQuotas();
    }

    // Load SA values
    SpaceStatusSummary saSum;
    try {
      saSum = SpaceStatusSummary.createFromDB(saAlias);
    } catch (IllegalArgumentException e) {
      log.info(
          "Unable to load requested space status summary from database. IllegalArgumentException: "
              + e.getMessage());
      throw new WebApplicationException(
          Response.status(NOT_FOUND)
              .entity("Unable to load requested space status info from database")
              .build());
    }
    result = saSum.getJsonFormat();
    return result;
  }
}
