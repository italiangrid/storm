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

import java.util.Map;

import static it.grid.storm.srm.types.TStatusCode.SRM_ABORTED;
import static it.grid.storm.srm.types.TStatusCode.SRM_AUTHENTICATION_FAILURE;
import static it.grid.storm.srm.types.TStatusCode.SRM_AUTHORIZATION_FAILURE;
import static it.grid.storm.srm.types.TStatusCode.SRM_CUSTOM_STATUS;
import static it.grid.storm.srm.types.TStatusCode.SRM_DONE;
import static it.grid.storm.srm.types.TStatusCode.SRM_DUPLICATION_ERROR;
import static it.grid.storm.srm.types.TStatusCode.SRM_EXCEED_ALLOCATION;
import static it.grid.storm.srm.types.TStatusCode.SRM_FAILURE;
import static it.grid.storm.srm.types.TStatusCode.SRM_FATAL_INTERNAL_ERROR;
import static it.grid.storm.srm.types.TStatusCode.SRM_FILE_BUSY;
import static it.grid.storm.srm.types.TStatusCode.SRM_FILE_IN_CACHE;
import static it.grid.storm.srm.types.TStatusCode.SRM_FILE_LIFETIME_EXPIRED;
import static it.grid.storm.srm.types.TStatusCode.SRM_FILE_LOST;
import static it.grid.storm.srm.types.TStatusCode.SRM_FILE_PINNED;
import static it.grid.storm.srm.types.TStatusCode.SRM_FILE_UNAVAILABLE;
import static it.grid.storm.srm.types.TStatusCode.SRM_INTERNAL_ERROR;
import static it.grid.storm.srm.types.TStatusCode.SRM_INVALID_PATH;
import static it.grid.storm.srm.types.TStatusCode.SRM_INVALID_REQUEST;
import static it.grid.storm.srm.types.TStatusCode.SRM_LAST_COPY;
import static it.grid.storm.srm.types.TStatusCode.SRM_LOWER_SPACE_GRANTED;
import static it.grid.storm.srm.types.TStatusCode.SRM_NON_EMPTY_DIRECTORY;
import static it.grid.storm.srm.types.TStatusCode.SRM_NOT_SUPPORTED;
import static it.grid.storm.srm.types.TStatusCode.SRM_NO_FREE_SPACE;
import static it.grid.storm.srm.types.TStatusCode.SRM_NO_USER_SPACE;
import static it.grid.storm.srm.types.TStatusCode.SRM_PARTIAL_SUCCESS;
import static it.grid.storm.srm.types.TStatusCode.SRM_RELEASED;
import static it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_INPROGRESS;
import static it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_QUEUED;
import static it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_SUSPENDED;
import static it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_TIMED_OUT;
import static it.grid.storm.srm.types.TStatusCode.SRM_SPACE_AVAILABLE;
import static it.grid.storm.srm.types.TStatusCode.SRM_SPACE_LIFETIME_EXPIRED;
import static it.grid.storm.srm.types.TStatusCode.SRM_SUCCESS;
import static it.grid.storm.srm.types.TStatusCode.SRM_TOO_MANY_RESULTS;

import java.util.HashMap;
import java.util.Iterator;
import it.grid.storm.srm.types.TStatusCode;

/**
 * Package private auxiliary class used to convert between DB raw data and StoRM object model
 * representation of StatusCode.
 * 
 * @author: EGRID ICTP
 * @version: 2.0
 * @date: June 2005
 */
public class StatusCodeConverter {

  private Map<Integer, TStatusCode> DBtoSTORM = new HashMap<Integer, TStatusCode>();
  private Map<TStatusCode, Object> STORMtoDB = new HashMap<TStatusCode, Object>();

  private static StatusCodeConverter c = new StatusCodeConverter();

  private StatusCodeConverter() {

    DBtoSTORM.put(Integer.valueOf(0), SRM_SUCCESS);
    DBtoSTORM.put(Integer.valueOf(1), SRM_FAILURE);
    DBtoSTORM.put(Integer.valueOf(2), SRM_AUTHENTICATION_FAILURE);
    DBtoSTORM.put(Integer.valueOf(3), SRM_AUTHORIZATION_FAILURE);
    DBtoSTORM.put(Integer.valueOf(4), SRM_INVALID_REQUEST);
    DBtoSTORM.put(Integer.valueOf(5), SRM_INVALID_PATH);
    DBtoSTORM.put(Integer.valueOf(6), SRM_FILE_LIFETIME_EXPIRED);
    DBtoSTORM.put(Integer.valueOf(7), SRM_SPACE_LIFETIME_EXPIRED);
    DBtoSTORM.put(Integer.valueOf(8), SRM_EXCEED_ALLOCATION);
    DBtoSTORM.put(Integer.valueOf(9), SRM_NO_USER_SPACE);
    DBtoSTORM.put(Integer.valueOf(10), SRM_NO_FREE_SPACE);
    DBtoSTORM.put(Integer.valueOf(11), SRM_DUPLICATION_ERROR);
    DBtoSTORM.put(Integer.valueOf(12), SRM_NON_EMPTY_DIRECTORY);
    DBtoSTORM.put(Integer.valueOf(13), SRM_TOO_MANY_RESULTS);
    DBtoSTORM.put(Integer.valueOf(14), SRM_INTERNAL_ERROR);
    DBtoSTORM.put(Integer.valueOf(15), SRM_FATAL_INTERNAL_ERROR);
    DBtoSTORM.put(Integer.valueOf(16), SRM_NOT_SUPPORTED);
    DBtoSTORM.put(Integer.valueOf(17), SRM_REQUEST_QUEUED);
    DBtoSTORM.put(Integer.valueOf(18), SRM_REQUEST_INPROGRESS);
    DBtoSTORM.put(Integer.valueOf(19), SRM_REQUEST_SUSPENDED);
    DBtoSTORM.put(Integer.valueOf(20), SRM_ABORTED);
    DBtoSTORM.put(Integer.valueOf(21), SRM_RELEASED);
    DBtoSTORM.put(Integer.valueOf(22), SRM_FILE_PINNED);
    DBtoSTORM.put(Integer.valueOf(23), SRM_FILE_IN_CACHE);
    DBtoSTORM.put(Integer.valueOf(24), SRM_SPACE_AVAILABLE);
    DBtoSTORM.put(Integer.valueOf(25), SRM_LOWER_SPACE_GRANTED);
    DBtoSTORM.put(Integer.valueOf(26), SRM_DONE);
    DBtoSTORM.put(Integer.valueOf(27), SRM_PARTIAL_SUCCESS);
    DBtoSTORM.put(Integer.valueOf(28), SRM_REQUEST_TIMED_OUT);
    DBtoSTORM.put(Integer.valueOf(29), SRM_LAST_COPY);
    DBtoSTORM.put(Integer.valueOf(30), SRM_FILE_BUSY);
    DBtoSTORM.put(Integer.valueOf(31), SRM_FILE_LOST);
    DBtoSTORM.put(Integer.valueOf(32), SRM_FILE_UNAVAILABLE);
    DBtoSTORM.put(Integer.valueOf(33), SRM_CUSTOM_STATUS);

    Object aux;
    for (Iterator<Integer> i = DBtoSTORM.keySet().iterator(); i.hasNext();) {
      aux = i.next();
      STORMtoDB.put(DBtoSTORM.get(aux), aux);
    }
  }

  /**
   * Method that returns the only instance of StatusCodeConverter.
   */
  public static StatusCodeConverter getInstance() {

    return c;
  }

  /**
   * Method that returns the int used in the DB to represent the given TStatusCode. -1 is returned
   * if no match is found.
   */
  public int toDB(TStatusCode sc) {

    Integer aux = (Integer) STORMtoDB.get(sc);
    if (aux == null)
      return -1;
    return aux.intValue();
  }

  /**
   * Method that returns the TStatusCode used by StoRM to represent the supplied int representation
   * of the DB. TStatusCode.EMPTY is returned if no StoRM type is found.
   */
  public TStatusCode toSTORM(int n) {

    TStatusCode aux = DBtoSTORM.get(Integer.valueOf(n));
    if (aux == null)
      return TStatusCode.EMPTY;
    return aux;
  }
}
