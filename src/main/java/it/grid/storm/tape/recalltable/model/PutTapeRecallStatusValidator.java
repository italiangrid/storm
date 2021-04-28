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

package it.grid.storm.tape.recalltable.model;

import static java.lang.String.format;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.util.SURLValidator;
import it.grid.storm.util.TokenValidator;

public class PutTapeRecallStatusValidator implements RequestValidator {

  private static final Logger log = LoggerFactory.getLogger(PutTapeRecallStatusValidator.class);

  public static final String REQUEST_TOKEN_KEY = "requestToken";
  public static final String SURL_KEY = "surl";

  public static final String NOT_FOUND_PROPERTY = "Invalid body. Not found property %s.";
  public static final String INVALID_TOKEN = "Invalid token: %s.";
  public static final String INVALID_SURL = "Invalid SURL: %s.";

  private InputStream input = null;

  private Response validationResponse = null;

  private String requestToken = null;
  private StoRI stori = null;

  public PutTapeRecallStatusValidator(InputStream input) {

    this.input = input;
  }

  /**
   * Parse and validate input.
   * <p>
   * If this method returns <code>true</code> the input data can be retrieved with the methods:
   * {@link #getRequestToken()} and {@link #getStoRI()}.
   * <p>
   * If this method returns <code>false</code> the response can be retrieved with the method
   * {@link #getResponse()}.
   * 
   * @return <code>true</code> for successful validation process, <code>false</code> otherwise.
   */
  public boolean validate() {

    Properties props;
    PropertiesParser parser = new PropertiesParser();
    try {
      props = parser.parse(input);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      validationResponse = Response.status(INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
      return false;
    }

    if (!props.containsKey(REQUEST_TOKEN_KEY)) {
      validationResponse = Response.status(BAD_REQUEST).entity(format(NOT_FOUND_PROPERTY, REQUEST_TOKEN_KEY)).build();
      return false;
    }

    if (!props.containsKey(SURL_KEY)) {
      validationResponse = Response.status(BAD_REQUEST).entity(format(NOT_FOUND_PROPERTY, SURL_KEY)).build();
      return false;
    }

    requestToken = props.getProperty(REQUEST_TOKEN_KEY);

    if (requestToken.length() == 0 || !TokenValidator.valid(requestToken)) {
      validationResponse = Response.status(BAD_REQUEST).entity(format(INVALID_TOKEN, requestToken)).build();
      return false;
    }

    String surlString = props.getProperty(SURL_KEY);

    if (surlString.length() == 0) {
      validationResponse = Response.status(BAD_REQUEST).entity(format(INVALID_SURL, surlString)).build();
      return false;
    }

    if (!validateSurl(surlString)) {
      validationResponse = Response.status(BAD_REQUEST).entity(format(INVALID_SURL, surlString)).build();
      return false;
    }

    return true;
  }

  public String getRequestToken() {

    return requestToken;
  }

  public StoRI getStoRI() {

    return stori;
  }

  public Response getResponse() {

    return validationResponse;
  }

  private boolean validateSurl(String surlString) {

    TSURL surl;

    if (!SURLValidator.valid(surlString)) {
      validationResponse =
          Response.status(400).entity("Invalid surl: " + surlString + "\n\n").build();
      return false;
    }

    try {

      surl = TSURL.makeFromStringValidate(surlString);

    } catch (InvalidTSURLAttributesException e) {
      validationResponse = Response.status(400).build();
      return false;
    }
    try {
      stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(surl);
    } catch (Exception e) {
      log.warn("Unable to build a stori for surl {} UnapprochableSurlException: {}", surl,
          e.getMessage(), e);
      return false;
    }
    return true;
  }
}
