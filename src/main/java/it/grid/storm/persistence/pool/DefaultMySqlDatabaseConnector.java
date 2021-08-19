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

public class DefaultMySqlDatabaseConnector implements DatabaseConnector {

  private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

  private final String name;
  private final String url;
  private final String username;
  private final String password;

  private DefaultMySqlDatabaseConnector(String database) {

    this.name = database;

    Configuration config = Configuration.getInstance();

    this.username = config.getDbUsername();
    this.password = config.getDbPassword();

    String hostname = config.getDbHostname();
    int port = config.getDbPort();
    String properties = config.getDbProperties();

    if (properties.isEmpty()) {
      this.url = format("jdbc:mysql://%s:%d/%s", hostname, port, database);
    } else {
      this.url = format("jdbc:mysqk://%s:%d/%s?%s", hostname, port, database, properties);
    }
  }

  @Override
  public String getDriverName() {
    return MYSQL_DRIVER;
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
    return new DefaultMySqlDatabaseConnector("storm_db");
  }

  public static DatabaseConnector getStormBeIsamDatabaseConnector() {
    return new DefaultMySqlDatabaseConnector("storm_be_ISAM");
  }

  @Override
  public String getDbName() {
    return name;
  }
}
