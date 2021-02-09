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

package it.grid.storm.persistence.model;

import it.grid.storm.persistence.pool.MySqlFormat;
import it.grid.storm.persistence.pool.SQLFormat;

public abstract class SQLHelper {

  private final SQLFormat formatter = new MySqlFormat();

  public String format(Object value) {

    return formatter.format(value);
  }

  /**
   * 
   * @param value boolean
   * @return String
   */
  public String format(boolean value) {

    return formatter.format(new Boolean(value));
  }

  /**
   * 
   * @param value int
   * @return String
   */
  public String format(int value) throws NumberFormatException {

    return formatter.format(new Integer(value));
  }

  /**
   * 
   * @param value long
   * @return String
   */
  public String format(long value) throws NumberFormatException {

    return formatter.format(new Long(value));
  }

  /**
   * 
   * @param date Date
   * @return String
   */
  public String format(java.util.Date date) {

    return formatter.format(date);
  }

}
