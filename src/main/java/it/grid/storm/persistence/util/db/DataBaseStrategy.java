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

package it.grid.storm.persistence.util.db;

import java.util.HashMap;
import java.util.Map;

public class DataBaseStrategy {

	private final String dbmsVendor;
	private final String driverName;
	private final String jdbcPrefix;
	private String dbName;
	private String dbPrefix;
	private String dbUrl;
	private String dbUsr;
	private String dbPwd;
	private SQLFormat formatter;

	public static final DataBaseStrategy MYSQL = new DataBaseStrategy("mysql",
	    "com.mysql.cj.jdbc.Driver", "jdbc:mysql://", new MySqlFormat());

	private static final Map<String, DataBaseStrategy> DATABASES = new HashMap<String, DataBaseStrategy>();

	static {
		DataBaseStrategy.DATABASES.put(DataBaseStrategy.MYSQL.toString(),
			DataBaseStrategy.MYSQL);
	}

	private DataBaseStrategy(String dbmsVendor, String driverName, String prefix,
		SQLFormat formatter) {

		this.dbmsVendor = dbmsVendor;
		this.driverName = driverName;
		jdbcPrefix = prefix;
		this.formatter = formatter;
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

	public void setDbUrl(String url) {

		dbUrl = url;
	}

	public String getDbUrl() {

		return dbUrl;
	}

	public String getConnectionString() {

		String connStr;
		connStr = jdbcPrefix + dbUrl + "/" + dbName;
		return connStr;
	}

	public void setFormatter(SQLFormat formatter) {

		this.formatter = formatter;
	}

	public SQLFormat getFormatter() {

		return formatter;
	}

	@Override
	public String toString() {

		return dbmsVendor;
	}


	public static DataBaseStrategy getInstance(String vendor) {

		return DataBaseStrategy.DATABASES.get(vendor);
	}

	public static String getDriverName(String vendor) {

		return (DataBaseStrategy.getInstance(vendor)).driverName;
	}

	public static String getJdbcPrefix(String vendor) {

		return (DataBaseStrategy.getInstance(vendor)).jdbcPrefix;
	}
}
