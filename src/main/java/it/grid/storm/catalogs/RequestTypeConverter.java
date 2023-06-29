/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import static it.grid.storm.catalogs.RequestSummaryDataTO.BOL_REQUEST_TYPE;
import static it.grid.storm.catalogs.RequestSummaryDataTO.COPY_REQUEST_TYPE;
import static it.grid.storm.catalogs.RequestSummaryDataTO.PTG_REQUEST_TYPE;
import static it.grid.storm.catalogs.RequestSummaryDataTO.PTP_REQUEST_TYPE;
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
class RequestTypeConverter {

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
