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

/**
 * 
 */
package it.grid.storm.authz.sa.conf;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 * 
 */
public class FileAuthzDBWatcher {

	private static final Logger log = LoggerFactory.getLogger(FileAuthzDBWatcher.class);
	private String authzDBPath;
	private Map<String, AuthzDBFileStatus> authzDBfiles; // <filename, status>
	private final long delay = 1000; // 1 seconds
	private long period = -1L;
	private Timer timer = new Timer();
	private FileWatcher fWatcher = new FileWatcher();

	public enum AuthzDBFileStatus {
		ERROR1("exists in path but it is not defined in Namespace."), ERROR2(
			"exists in Namespace but it does not exists in path."), WARN(
			"AuthzDB exists but it does not be parsed yet."), OK(
			"AuthzDB exists and it was parsed.");

		AuthzDBFileStatus(String msg) {

			this.msg = msg;
		}

		private final String msg;

		@Override
		public String toString() {

			return this.msg;
		}
	}

	// authzDB filename filter. Return only '.authz' files.
	private FilenameFilter authzDBFileFilter = new FilenameFilter() {

		public boolean accept(File dir, String name) {

			return name.endsWith(".authz");
		}
	};

	public FileAuthzDBWatcher(long period, String authzDBPath)
		throws AuthzDBReaderException {

		authzDBfiles = new HashMap<String, AuthzDBFileStatus>();
		AuthzDBFileStatus status = AuthzDBFileStatus.ERROR1;
		File authzPath = new File(authzDBPath);
		if (!authzPath.exists()) {
			throw new AuthzDBReaderException("AuthzDBPath '" + authzDBPath
				+ "' does not exists!");
		}
		if (!authzPath.isDirectory()) {
			throw new AuthzDBReaderException("AuthzDBPath '" + authzDBPath
				+ "' is not a directory!");
		}
		String[] authzDBs = authzPath.list(authzDBFileFilter);
		log.debug("Found {} authzDBs in '{}' path.", authzDBs.length, authzDBPath);
		for (String authzDB : authzDBs) {
			authzDBfiles.put(authzDB, status);
		}
		this.authzDBPath = authzDBPath;
		this.period = period;
	}

	public void watchAuthzDBFile(String dbFileName) throws AuthzDBReaderException {

		AuthzDBFileStatus status = null;
		File f = new File(dbFileName);
		if (f.exists()) {
			status = AuthzDBFileStatus.WARN;
		} else {
			status = AuthzDBFileStatus.ERROR2;
		}
		authzDBfiles.put(dbFileName, status);
		fWatcher.addFileToWatch(dbFileName);
	}

	public Map<String, AuthzDBFileStatus> getObservedAuthzDBFiles() {

		return this.authzDBfiles;
	}

	/**
     * 
     */
	public void startWatching() {

		timer.schedule(fWatcher, delay, period);
		log.debug("AuthzDB-Watcher started.");
		log.debug(" and observing the files: {}", authzDBfiles);
	}

	public void onChange(String dbFileName) {

		// Call the Parser

		// //////TEMPORARY FIX
		// ////// THIS METHOD HAS BEEN COMMENTED TO MAKE EVERYTHING COMPILE
		// onChangeAuthzDB(dbFileName);
		authzDBfiles.put(dbFileName, AuthzDBFileStatus.OK);
	}

	/**
	 * 
	 * @param dbFileName
	 */
	public void authzDBParsed(String dbFileName) {

		long parsingTime = System.currentTimeMillis();
		fWatcher.setParsingTime(dbFileName, parsingTime);
		log.debug("Authz DB '{}' parsed", dbFileName);
	}

	/**
     * 
     */
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		int count = 1;
		for (String element : authzDBfiles.keySet()) {
			sb.append('[' + count + "] - ");
			sb.append(element);
			sb.append('<');
			sb.append("Last Modification: ");
			File f = new File(element);
			Date dM = new Date(f.lastModified());
			sb.append(formatter.format(dM));
			sb.append(" - Parsed: ");
			Date dP = new Date(fWatcher.getParsingTime(element));
			sb.append(formatter.format(dP));
			sb.append('>');
			count++;
		}
		return sb.toString();
	}

	/**
	 * 
	 * @author zappi
	 * 
	 */
	private class FileWatcher extends TimerTask {

		private Map<String, Long> lastModification;

		public FileWatcher() {

			lastModification = new HashMap<String, Long>();
		}

		public void addFileToWatch(String dbFileName) {

			lastModification.put(dbFileName, Long.MIN_VALUE);
		}

		public void setParsingTime(String dbFileName, long parsingTime) {

			lastModification.put(dbFileName, parsingTime);
			log.debug("Watcher updated with the occurred parsing of '{}'", dbFileName);
		}

		public long getParsingTime(String dbFileName) {

			if (lastModification.containsKey(dbFileName)) {
				return lastModification.get(dbFileName).longValue();
			}
			return -1L;
		}

		/*
         */
		@SuppressWarnings("boxing")
		@Override
		public void run() {

			log.debug("File Watcher is refreshing for AuthzDB files.");

			// Check for new authz db files
			File authzPath = new File(authzDBPath);
			String[] authzDBs = authzPath.list(authzDBFileFilter);
			log.debug("Found {} authzDBs in '{}' path.", authzDBs.length, authzDBPath);
			for (String authzDB : authzDBs) {
				if (!(authzDBfiles.containsKey(authzDB))) {
					authzDBfiles.put(authzDB, AuthzDBFileStatus.ERROR1);
					log.debug("Found a new authz files ('{}')in authzDB path.", authzDB);
				}
			}

			// Check for known authz files
			File f;
			for (String fileName : lastModification.keySet()) {
				f = new File(fileName);
				if (!f.exists()) {
					// authz DB file is disappeared
					log.error("Unable to find the AuthzDB file '{}'", fileName);
					authzDBfiles.put(fileName, AuthzDBFileStatus.ERROR2);
				}
				if (f.lastModified() > lastModification.get(fileName)) {
					log.debug("Found authzDB '{}' modified!", fileName);
					onChange(fileName);
				}
			}
		}
	}

}
