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

import it.grid.storm.config.Configuration;

public class StormBeIsamConnectionPool extends DefaultDatabaseConnectionPool {

  private static StormBeIsamConnectionPool instance;

  public static synchronized StormBeIsamConnectionPool getInstance() {
    if (instance == null) {
      instance = new StormBeIsamConnectionPool();
    }
    return instance;
  }

  private final static Configuration c = Configuration.getInstance();

  private StormBeIsamConnectionPool() {

    super(DefaultMySqlDatabaseConnector.getStormBeIsamDatabaseConnector(), c.getDbPoolSize(),
        c.getDbPoolMinIdle(), c.getDbPoolMaxWaitMillis(), c.isDbPoolTestOnBorrow(),
        c.isDbPoolTestWhileIdle());
  }

}
