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

package it.grid.storm.persistence.pool;

import static java.lang.String.format;

import it.grid.storm.config.Configuration;

public class DefaultDatabaseConnector implements DatabaseConnector {

  private final String name;
  private final String driver;
  private final String url;
  private final String username;
  private final String password;

  private DefaultDatabaseConnector(String database) {

    this.name = database;

    Configuration config = Configuration.getInstance();

    this.username = config.getDbUsername();
    this.password = config.getDbPassword();
    this.driver = config.getDbDriver();

    String subprotocol = config.getDbUrlSubprotocol();
    String subname = config.getDbUrlSubname();
    String hostname = config.getDbUrlHostname();
    String port = config.getDbUrlPort();
    String properties = config.getDbUrlProperties();

    if (properties.isEmpty()) {
      this.url = format("jdbc:%s:%s//%s:%s/%s", subprotocol, subname, hostname, port, database);
    } else {
      this.url = format("jdbc:%s:%s//%s:%s/%s?%s", subprotocol, subname, hostname, port, database, properties);
    }
  }

  @Override
  public String getDriverName() {
    return driver;
  }

  @Override
  public String getDbURL() {
     return url;
  }

  @Override
  public String getDbUsername() {
    return username;
  }

  @Override
  public String getDbPassword() {
    return password;
  }

  public static DatabaseConnector getStormDbDatabaseConnector() {
    return new DefaultDatabaseConnector("storm_db");
  }

  public static DatabaseConnector getStormBeIsamDatabaseConnector() {
    return new DefaultDatabaseConnector("storm_be_ISAM");
  }

  @Override
  public String getDbName() {
    return name;
  }
}
