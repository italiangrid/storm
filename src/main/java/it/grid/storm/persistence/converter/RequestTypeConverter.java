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

package it.grid.storm.persistence.converter;

import static it.grid.storm.persistence.model.RequestSummaryDataTO.BOL_REQUEST_TYPE;
import static it.grid.storm.persistence.model.RequestSummaryDataTO.COPY_REQUEST_TYPE;
import static it.grid.storm.persistence.model.RequestSummaryDataTO.PTG_REQUEST_TYPE;
import static it.grid.storm.persistence.model.RequestSummaryDataTO.PTP_REQUEST_TYPE;
import static it.grid.storm.srm.types.TRequestType.BRING_ON_LINE;
import static it.grid.storm.srm.types.TRequestType.COPY;
import static it.grid.storm.srm.types.TRequestType.EMPTY;
import static it.grid.storm.srm.types.TRequestType.PREPARE_TO_GET;
import static it.grid.storm.srm.types.TRequestType.PREPARE_TO_PUT;

import java.util.Map;

import com.google.common.collect.Maps;

import it.grid.storm.srm.types.TRequestType;

/**
 * Package private auxiliary class used to convert between DB and StoRM object model representation
 * of the request type.
 */
public class RequestTypeConverter {

  private Map<String, TRequestType> dbToStorm = Maps.newHashMap();
  private Map<TRequestType, String> stormToDb = Maps.newHashMap();

  private static RequestTypeConverter c = new RequestTypeConverter();

  private RequestTypeConverter() {

    dbToStorm.put(PTG_REQUEST_TYPE, PREPARE_TO_GET);
    dbToStorm.put(PTP_REQUEST_TYPE, PREPARE_TO_PUT);
    dbToStorm.put(COPY_REQUEST_TYPE, COPY);
    dbToStorm.put(BOL_REQUEST_TYPE, BRING_ON_LINE);
    dbToStorm.keySet().forEach(key -> stormToDb.put(dbToStorm.get(key), key));
  }

  /**
   * Method that returns the only instance of RequestTypeConverter.
   */
  public static RequestTypeConverter getInstance() {

    return c;
  }

  /**
   * Method that returns the String used by DB to represent the given TRequestType. An empty String
   * is returned if no match is found.
   */
  public String toDB(TRequestType rt) {

    String aux = stormToDb.get(rt);
    if (aux == null)
      return "";
    return aux;
  }

  /**
   * Method that returns the TRequestType used by StoRM to represent the supplied String
   * representation of DB. TRequestType.EMPTY is returned if no StoRM type is found.
   */
  public TRequestType toSTORM(String s) {

    TRequestType aux = dbToStorm.get(s);
    if (aux == null)
      return EMPTY;
    return aux;
  }
}
