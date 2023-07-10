/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.info.remote.resources;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import it.grid.storm.info.remote.Constants;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path("/info/ping")
public class Ping {

  // The Java method will process HTTP GET requests
  @GET
  // The Java method will produce content identified by the MIME Media
  // type "text/plain"
  @Produces("text/plain")
  public String getClichedMessage() {

    // Return some cliched textual content
    return "Hello World";
  }

  @GET
  // The Java method will produce content identified by the MIME Media
  // type "text/plain"
  @Produces("text/plain")
  @Path("/queryMeGet")
  public String getParameterizedMessage(
      @QueryParam("uno") String uno, @QueryParam("due") String due) {

    String unoDecoded, dueDecoded;
    try {
      unoDecoded = URLDecoder.decode(uno.trim(), Constants.ENCODING_SCHEME);
      dueDecoded = URLDecoder.decode(due.trim(), Constants.ENCODING_SCHEME);
    } catch (UnsupportedEncodingException e) {
      System.err.println(
          "Unable to decode parameters. UnsupportedEncodingException : " + e.getMessage());
      throw new WebApplicationException(
          Response.status(BAD_REQUEST)
              .entity(
                  "Unable to decode parameters, unsupported encoding \'"
                      + Constants.ENCODING_SCHEME
                      + "\'")
              .build());
    }
    return "Hello by GET my friend " + unoDecoded + " from " + dueDecoded;
  }

  @PUT
  // The Java method will produce content identified by the MIME Media
  // type "text/plain"
  @Produces("text/plain")
  @Path("/queryMePut")
  public String putParameterizedMessage(
      @QueryParam("uno") String uno, @QueryParam("due") String due) {

    String unoDecoded, dueDecoded;
    try {
      unoDecoded = URLDecoder.decode(uno.trim(), Constants.ENCODING_SCHEME);
      dueDecoded = URLDecoder.decode(due.trim(), Constants.ENCODING_SCHEME);
    } catch (UnsupportedEncodingException e) {
      System.err.println(
          "Unable to decode parameters. UnsupportedEncodingException : " + e.getMessage());
      throw new WebApplicationException(
          Response.status(BAD_REQUEST)
              .entity(
                  "Unable to decode parameters, unsupported encoding \'"
                      + Constants.ENCODING_SCHEME
                      + "\'")
              .build());
    }
    return "Hello by PUT my friend " + unoDecoded + " from " + dueDecoded;
  }
}
