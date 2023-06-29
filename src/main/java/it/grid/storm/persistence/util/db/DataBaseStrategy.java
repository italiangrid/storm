/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.util.db;

public class DataBaseStrategy {

	private final String dbmsVendor;
	private final String driverName;
	private final String jdbcPrefix;
	private String dbName;
	private String dbPrefix;
	private String dbHost;
	private String dbUsr;
	private String dbPwd;
	private SQLFormat formatter;
	private String properties;

	public DataBaseStrategy(String dbmsVendor, String driverName, String prefix,
		SQLFormat formatter) {

		this.dbmsVendor = dbmsVendor;
		this.driverName = driverName;
		jdbcPrefix = prefix;
		this.formatter = formatter;
		this.properties = "";
	}


	public String getDbmsVendor() {
		return dbmsVendor;
	}

	public String getDriverName() {
		return driverName;
	}

	public String getJdbcPrefix() {

		return jdbcPrefix;
	}

	public void setDbUsr(String usrDb) {

		dbUsr = usrDb;
	}

	public String getDbUsr() {

		return dbUsr;
	}

	public void setDbPwd(String pwd) {

		dbPwd = pwd;
	}

	public String getDbPwd() {

		return dbPwd;
	}

	public void setDbName(String dbName) {

		this.dbName = dbName;
	}

	public String getDbName() {

		return dbName;
	}

	public void setDbPrefix(String dbName) {

		dbPrefix = dbName;
	}

	public String getDbPrefix() {

		return dbPrefix;
	}

	public void setDbHost(String host) {

		dbHost = host;
	}

	public String getDbHost() {

		return dbHost;
	}

	public String getConnectionString() {

		String connStr = jdbcPrefix + dbHost + "/" + dbName;
		if (!properties.isEmpty()) {
		  connStr += "?" + properties;
		}
		return connStr;
	}

	public void setFormatter(SQLFormat formatter) {

		this.formatter = formatter;
	}

	public SQLFormat getFormatter() {

		return formatter;
	}

	public void setProperties(String encodedProperties) {

	    this.properties = encodedProperties;
	}

	@Override
	public String toString() {

		return dbmsVendor;
	}
}
