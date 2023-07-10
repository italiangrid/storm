/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.config.Configuration;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoRMDataSource {

  public static final Logger log = LoggerFactory.getLogger(StoRMDataSource.class);

  public static class Builder {

    private static final String VALIDATION_QUERY = "select 1 from dual";

    private String driver;
    private String url;

    private String username;
    private String password;

    private int maxPooledConnections = 200;
    private int initialPoolSize = 10;

    private BasicDataSource ds;

    public Builder() {}

    public Builder driver(String driver) {
      this.driver = driver;
      return this;
    }

    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public Builder username(String username) {
      this.username = username;
      return this;
    }

    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public Builder maxPooledConnections(int maxPool) {
      if (maxPool < 1) {
        throw new IllegalArgumentException("maxPooledConnections must be >= 1");
      }
      this.maxPooledConnections = maxPool;
      return this;
    }

    public Builder initialPoolSize(int initialSize) {
      if (initialSize <= 0) {
        throw new IllegalArgumentException("initialSize must be >= 0");
      }
      this.initialPoolSize = initialSize;
      return this;
    }

    private void sanityChecks() {
      if ((username == null) || (username.isEmpty()))
        throw new IllegalArgumentException("null or empty username");

      if ((driver == null) || (driver.isEmpty()))
        throw new IllegalArgumentException("null or empty driver");

      if ((url == null) || (url.isEmpty())) throw new IllegalArgumentException("null or empty url");

      if ((password == null) || (password.isEmpty()))
        throw new IllegalArgumentException("null or empty password");
    }

    private void logConfiguration() {
      if (log.isDebugEnabled()) {
        log.debug("driver: {}", driver);
        log.debug("url: {}", url);
        log.debug("username: {}", username);
        log.debug("password: {}", password);
        log.debug("initialPoolSize: {}", initialPoolSize);
        log.debug("maxPooledConnections: {}", maxPooledConnections);
      }
    }

    public StoRMDataSource build() {
      sanityChecks();
      logConfiguration();
      ds = new BasicDataSource();
      ds.setDriverClassName(driver);
      ds.setUrl(url);
      ds.setUsername(username);
      ds.setPassword(password);
      ds.setInitialSize(initialPoolSize);
      ds.setMaxTotal(maxPooledConnections);
      ds.setValidationQuery(VALIDATION_QUERY);
      ds.setTestWhileIdle(true);
      ds.setPoolPreparedStatements(true);
      ds.setMaxOpenPreparedStatements(200);
      return new StoRMDataSource(this);
    }
  }

  private StoRMDataSource(Builder b) {
    this.dataSource = b.ds;
  }

  private BasicDataSource dataSource;

  /** @return the dataSource */
  public DataSource getDataSource() {
    return dataSource;
  }

  /**
   * @throws SQLException
   * @see org.apache.commons.dbcp.BasicDataSource#close()
   */
  public void close() throws SQLException {
    dataSource.close();
  }

  /**
   * @return
   * @throws SQLException
   * @see org.apache.commons.dbcp.BasicDataSource#getConnection()
   */
  public Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  private static volatile StoRMDataSource instance = null;

  public static synchronized StoRMDataSource getInstance() {
    return instance;
  }

  public static synchronized void init() {
    if (instance != null) {
      log.warn("Called init on already initialized Storm data source.");
      log.warn("The datasource will be closed and re-initialized.");
      try {
        instance.close();
      } catch (SQLException e) {
        log.error("Error closing storm data source: {}", e.getMessage(), e);
      }
    }

    log.info("Initializing StoRM datasource");
    Configuration conf = Configuration.getInstance();
    instance =
        new StoRMDataSource.Builder()
            .driver(conf.getDBDriver())
            .url(conf.getStormDbURL())
            .username(conf.getDBUserName())
            .password(conf.getDBPassword())
            .build();
  }
}
