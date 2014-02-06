/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package it.grid.storm.filesystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 */
public class FileSystemCheckerFactory {

	private static Logger log = LoggerFactory
		.getLogger(FileSystemCheckerFactory.class);

	public enum FileSystemCheckerType {
		Mtab, Mounts
	}

	private final FileSystemCheckerType chosenType;
	private static final FileSystemCheckerType defaultType = FileSystemCheckerType.Mtab;

	private static FileSystemCheckerFactory instance = null;

	/**
	 * @param type
	 */
	private FileSystemCheckerFactory(FileSystemCheckerType type) {

		chosenType = type;
	}

	/**
	 * Singleton getter method
	 * 
	 * @return the class instance already created by a call to the init method,
	 *         creates a new one using the defaultType otherwise
	 */
	public static FileSystemCheckerFactory getInstance() {

		if (instance == null) {
			log
				.info("FileSystemCheckerFactory not explicitaly initialized, "
				  + "using default checker type :{}",  defaultType);
			init(defaultType);
		}
		return instance;
	}

	/**
	 * Initializes the class by creating the singleton instance if not already
	 * done, does nothing if it already exists and has the chosenType is the same
	 * as the provided FileSystemCheckerType throws an IllegalStateException
	 * otherwise
	 * 
	 * @param type
	 * @throws IllegalArgumentException
	 *           if type is null
	 * @throws IllegalStateException
	 *           if class already initialized with a different
	 *           FileSystemCheckerType
	 */
	public static void init(FileSystemCheckerType type)
		throws IllegalArgumentException, IllegalStateException {

		if (type == null) {
			log
				.error("Unable to init FileSystemCheckerFactory. Received null FileSystemCheckerType parameter!");
			throw new IllegalArgumentException(
				"Received null FileSystemCheckerType parameter!");
		}
		if (instance == null) {
			instance = new FileSystemCheckerFactory(type);
		} else {
			if (!instance.chosenType.equals(type)) {
				log
					.warn("FileSystemCheckerFactory already initialized for {}. "
					  + "Cannot initialize it again for {}.",
					 instance.chosenType,
					 type);
				throw new IllegalStateException(
					"Asked to initialize the already initialized FileSystemCheckerFactory "
						+ "with FileSystemCheckerType " + type
						+ ". Current FileSystemCheckerType is " + instance.chosenType);
			} else {
				log
					.info("Asked to re-initialize the already initialized FileSystemCheckerFactory, nothing to do");
			}
		}
	}

	/**
	 * Creates the proper FileSystemChecker implementation object using the
	 * chosenType available attribute
	 * 
	 * @return
	 */
	public FileSystemChecker createFileSystemChecker()
		throws IllegalStateException, FileSystemCheckerException {

		switch (this.chosenType) {
		case Mtab:
			return FileSystemCheckerMtabMonolithic.getInstance();
		case Mounts:
			return FileSystemCheckerMountsMonolithic.getInstance();
		default:
			log
				.error("No correct FileSystemChecker setted : "
					+ this.chosenType
					+ " unable to create the FileSystemChecker. Available FileSystemCheckerType : "
					+ FileSystemCheckerFactory.FileSystemCheckerType.values()
					+ " Please contact StoRM developers");
			throw new IllegalStateException(
				"No correct FileSystemCheckerType setted : " + this.chosenType
					+ ". Available FileSystemCheckerType : "
					+ FileSystemCheckerFactory.FileSystemCheckerType.values()
					+ " Please contact StoRM developers");
		}
	}

}
