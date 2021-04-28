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

public class PutUpdateTaskValidator implements RequestValidator {

  public static final String INVALID_BODY = "Invalid body. Expected [status|retry-value]={value}.";

  public static final String STATUS_KEY = "status";
  public static final String RETRY_VALUE_KEY = "retry-value";

  public static final String NOT_FOUND_PROPERTY = "Invalid body. Not found property %s.";
  public static final String EMPTY_BODY = "No properties found in body.";
  public static final String WRONG_SIZE = "Expected one property. Found %d.";
  public static final String NOT_INT_VALUE = "Invalid integer value '%s'.";

  private static final Logger LOG = LoggerFactory.getLogger(PutUpdateTaskValidator.class);

  private InputStream input = null;

  private Response validationResponse = null;

  private Integer value = null;
  private boolean isStatus = false;
  private boolean isRetryValue = false;

  public PutUpdateTaskValidator(InputStream input) {

    this.input = input;
  }

  public boolean validate() {

    Properties props;
    PropertiesParser parser = new PropertiesParser();
    try {
      props = parser.parse(input);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
      validationResponse = Response.status(INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
      return false;
    }

    if (props.size() == 0) {

      LOG.debug(EMPTY_BODY);
      validationResponse = Response.status(BAD_REQUEST).entity(EMPTY_BODY).build();
      return false;

    }

    if (props.size() > 1) {

      String message = format(WRONG_SIZE, props.size());
      LOG.debug(message);
      validationResponse = Response.status(BAD_REQUEST).entity(message).build();
      return false;

    }

    isStatus = props.containsKey(STATUS_KEY);
    isRetryValue = props.containsKey(RETRY_VALUE_KEY);

    if (!isStatus && !isRetryValue) {

      LOG.debug(INVALID_BODY);
      validationResponse = Response.status(BAD_REQUEST).entity(INVALID_BODY).build();
      return false;
    }

    String valueStr = null;
    try {

      valueStr = isStatus ? props.getProperty(STATUS_KEY) : props.getProperty(RETRY_VALUE_KEY);
      value = Integer.valueOf(valueStr);

    } catch (NumberFormatException e) {

      String message = format(NOT_INT_VALUE, valueStr);
      LOG.error(message);
      validationResponse = Response.status(BAD_REQUEST).entity(message).build();
      return false;
    }

    return true;
  }

  public Response getResponse() {

    return validationResponse;
  }

  public Integer getValue() {

    return value;
  }

  public boolean isStatusRequest() {

    return isStatus;
  }

  public boolean isRetryValueRequest() {

    return isRetryValue;
  }
}
