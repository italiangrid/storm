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
package it.grid.storm.authz.path.conf;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.AuthzException;
import it.grid.storm.authz.path.model.PathACE;
import it.grid.storm.authz.path.model.PathAuthzEvaluationAlgorithm;
import it.grid.storm.config.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.io.FileNotFoundException;

import org.slf4j.Logger;

/**
 * @author zappi
 */
public class PathAuthzDBReader {

	private final Logger log = AuthzDirector.getLogger();

	private final String authzDBFilename;
	private PathAuthzDB pathAuthzDB;

	private static enum LineType {
		COMMENT, ALGORITHM_NAME, PATH_ACE, OTHER
	}

	public PathAuthzDBReader(String filename) throws Exception {

		log.info("Path Authorization : Inizializating ...");
		if (!(existsAuthzDBFile(filename))) {
			String configurationPATH = Configuration.getInstance()
				.namespaceConfigPath();
			if (configurationPATH.length() == 0) {
				String userDir = System.getProperty("user.dir");
				log.debug("Unable to found the configuration path. Assume: '" + userDir
					+ "'");
				configurationPATH = userDir + File.separator + "etc";
			}
			authzDBFilename = configurationPATH + File.separator + filename;
		} else {
			authzDBFilename = filename;
		}
		log.debug("Loading Path Authz DB : '" + authzDBFilename + "'.");
		pathAuthzDB = loadPathAuthzDB(authzDBFilename);
		log
			.info("Path Authz DB ('" + pathAuthzDB.getPathAuthzDBID() + "') loaded.");
		log.info(pathAuthzDB.toString());
	}

	public void refreshPathAuthzDB() throws Exception {

		log.debug("<PathAuthzDBReader> Start refreshing.");
		pathAuthzDB = loadPathAuthzDB(authzDBFilename);
		log.debug("<PathAuthzDBReader> End refreshing.");
		log.info("Path Authz DB ('" + pathAuthzDB.getPathAuthzDBID()
			+ "') RE-loaded.");
		log.info(pathAuthzDB.toString());
	}

	public PathAuthzDB getPathAuthzDB() {

		return pathAuthzDB;
	}

	/**************************
	 * Private BUILDERs helper
	 * 
	 * @param authzDBFilename
	 **************************/

	private PathAuthzDB loadPathAuthzDB(String authzDBFilename) throws Exception {

		if (existsAuthzDBFile(authzDBFilename)) {
			log.debug("Parsing the Path Authz DB ...");
			PathAuthzDB result = parsePathAuthzDB(authzDBFilename);
			log.info("Loaded a Path Authz DB containing '" + result.getACL().size()
				+ "' path ACE.");
			return result;
		}
		log.warn("Unable to get a valid Path Authz DB. Loaded the default one! ");
		return new PathAuthzDB();
	}

	/**
	 * @param authzDBFilename
	 * @return
	 */
	private PathAuthzDB parsePathAuthzDB(String authzDBFilename) throws Exception {

		PathAuthzEvaluationAlgorithm algorithm = null;
		LinkedList<PathACE> aces = new LinkedList<PathACE>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(authzDBFilename));
		} catch (FileNotFoundException e) {
			log.error("Unable to get a FIleReader on \'" + authzDBFilename
				+ "\' . FileNotFoundException: " + e);
			throw new Exception("No file available at path \'" + authzDBFilename
				+ "\' . FileNotFoundException: " + e.getMessage());
		}
		try {
			String str;
			while ((str = reader.readLine()) != null) {
				ParseLineResults parsedLine = parseLine(str);
				switch (parsedLine.type) {
				case COMMENT:
					log.debug("comment line  : " + parsedLine.getComment());
					break;
				case ALGORITHM_NAME:
					if (algorithm != null) {
						log
							.error("Attention! More than one Algorithm specified in configuration file: '"
								+ parsedLine.getAlgorithmName() + "' , " + algorithm.getClass());
						throw new Exception(
							"More than one Algorithm specified in configuration file");
					}
					try {
						algorithm = buildAlgorithmInstance(parsedLine.getAlgorithmName());
						log.debug("algorithm name: " + parsedLine.getAlgorithmName());
					} catch (Exception e) {
						log.error("Unable to get Algorithm: '"
							+ parsedLine.getAlgorithmName() + "'");
						throw new Exception("Unable to build a valid Algorithm");
					}
					break;
				case PATH_ACE:
					aces.add(parsedLine.getPathAce());
					log.debug("path ace      : " + parsedLine.getPathAce());
					break;
				case OTHER:
					log.debug("something was wrong in '" + str + "'");
					break;
				}
			}
		} catch (IOException e) {
			log.error("Error while reading Path Authz DB '" + authzDBFilename + "'");
			throw new Exception("Error while reading Path Authz DB. IOException: "
				+ e);
		} finally {
			reader.close();
		}
		return new PathAuthzDB(authzDBFilename, algorithm, aces);
	}

	private PathAuthzEvaluationAlgorithm buildAlgorithmInstance(
		String algorithmName) throws Exception {

		Class<?> clazz = null;
		try {
			clazz = Class.forName(algorithmName);
		} catch (ClassNotFoundException e) {
			log.error("Unable to load the Path Authz Algorithm Class '"
				+ algorithmName + "' . ClassNotFoundException: " + e);
			throw new Exception("Unable to load a class with name \'" + algorithmName
				+ "\'");
		}
		Class<? extends PathAuthzEvaluationAlgorithm> authzAlgClass;
		try {
			authzAlgClass = clazz.asSubclass(PathAuthzEvaluationAlgorithm.class);
		} catch (ClassCastException e) {
			log.error("The loaded class Class '" + algorithmName
				+ "' is not a PathAuthzEvaluationAlgorithm. ClassCastException: " + e);
			throw new Exception("Class \'" + algorithmName
				+ "\' is not a PathAuthzEvaluationAlgorithm");
		}
		Method instanceMethod;
		try {
			instanceMethod = authzAlgClass.getMethod("getInstance", new Class[0]);
		} catch (NoSuchMethodException e) {
			log.error("The loaded class Class '" + algorithmName
				+ "' has not a getInstance method. NoSuchMethodException: " + e);
			throw new Exception("Class \'" + algorithmName
				+ "\' has not a getInstance method");
		} catch (SecurityException e) {
			log.error("Unable to get getInstance method. SecurityException: " + e);
			throw new Exception("Unable to get getInstance method");
		}
		if (instanceMethod == null) {
			log.error("The retrieved getInstance methos is null");
			throw new Exception("The retrieved getInstance methos is null");
		}
		Object authzAlgInstance;
		try {
			authzAlgInstance = instanceMethod.invoke(null, new Object[0]);
		} catch (IllegalAccessException e) {
			log.error("Unable to call getInstance method. IllegalAccessException: "
				+ e);
			throw new Exception("Unable to call getInstance method");
		} catch (IllegalArgumentException e) {
			log.error("Unable to call getInstance method. IllegalArgumentException: "
				+ e);
			throw new Exception("Unable to call getInstance method");
		} catch (InvocationTargetException e) {
			log
				.error("Unable to call getInstance method. InvocationTargetException: "
					+ e);
			throw new Exception("Unable to call getInstance method");
		}

		if (authzAlgInstance instanceof PathAuthzEvaluationAlgorithm) {
			log
				.debug("Found a valid Path Authz Evaluation Algorithm. It implements the algorithm : "
					+ ((PathAuthzEvaluationAlgorithm) authzAlgInstance).getDescription());
			return (PathAuthzEvaluationAlgorithm) authzAlgInstance;
		} else {
			log
				.error("The method  getInstance of class '"
					+ algorithmName
					+ "' does not return a valid Path Authz Evaluation Algorithm object but a \'"
					+ authzAlgInstance.getClass() + "\'");
			throw new Exception(
				"Unable to get a valid instance of PathAuthzEvaluationAlgorithm");
		}
	}

	/**
	 * @param str
	 * @return ParseLineResults
	 * @throws AuthzException
	 */
	private ParseLineResults parseLine(String pathACEString) {

		ParseLineResults result = null;
		if (pathACEString.startsWith(PathACE.COMMENT)) {
			// COMMENT LINE
			result = new ParseLineResults(LineType.COMMENT);
			result.setComment(pathACEString);
		} else {
			if (pathACEString.startsWith(PathACE.ALGORITHM)) {
				// EVALUATION ALGORITHM
				if (pathACEString.contains("=")) {
					String algName = pathACEString
						.substring(pathACEString.indexOf("=") + 1);
					result = new ParseLineResults(LineType.ALGORITHM_NAME);
					result.setAlgorithmName(algName.trim());
				}
			} else {
				// Check if it is an empty line
				if (pathACEString.trim().length() == 0) {
					result = new ParseLineResults(LineType.COMMENT);
					result.setComment("");
				} else {
					// SUPPOSE ACE Line
					try {
						PathACE ace = PathACE.buildFromString(pathACEString);
						result = new ParseLineResults(LineType.PATH_ACE);
						result.setPathAce(ace);
					} catch (AuthzException e) {
						log
							.error("Something of inexiplicable in the line " + pathACEString);
						log.error(" - explanation: " + e.getMessage());
						result = new ParseLineResults(LineType.OTHER);
					}

				}
			}
		}
		return result;
	}

	/***********************************************
	 * UTILITY Methods
	 */

	private boolean existsAuthzDBFile(String fileName) {

		File file = new File(fileName);
		if (!file.exists()) {
			log.warn("The AuthzDB File '" + fileName + "' does not exists");
			return false;
		}
		if (!file.isFile()) {
			log.warn("The AuthzDB File '" + fileName + "' is a directory");
			return false;
		}
		if (!file.canRead()) {
			log.warn("The AuthzDB File '" + fileName + "' cannot be read");
			return false;
		}
		return true;
	}

	private class ParseLineResults {

		private final LineType type;
		private String comment = null;
		private String algorithmName = null;
		private PathACE pathAce = null;

		/**
		 * @param
		 */
		public ParseLineResults(LineType type) {

			this.type = type;
		}

		public void setComment(String comment) {

			this.comment = comment;
		}

		public void setAlgorithmName(String algName) {

			algorithmName = algName;
		}

		public void setPathAce(PathACE ace) {

			pathAce = ace;
		}

		public String getComment() {

			return comment;
		}

		public String getAlgorithmName() {

			return algorithmName;
		}

		public PathACE getPathAce() {

			return pathAce;
		}
	}
}
