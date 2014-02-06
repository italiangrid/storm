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

/*
 * You may copy, distribute and modify this file under the terms of the INFN
 * GRID licence. For a copy of the licence please visit
 * 
 * http://www.cnaf.infn.it/license.html
 * 
 * Riccardo Zappi <riccardo.zappi@cnaf.infn.it.it>, 2007 $Id:
 * GridUserFactory.java 3604 2007-05-22 11:16:27Z rzappi $
 */

package it.grid.storm.griduser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

public class GridUserFactory {

	private static final Logger log = GridUserManager.log;
	private MapperInterface defaultMapperClass = null;

	private static GridUserFactory instance = null;

	protected static Logger getLogger() {

		return log;
	}

	private GridUserFactory() throws GridUserException {

		defaultMapperClass = makeMapperClass(GridUserManager.getMapperClassName());
	}

	static GridUserFactory getInstance() {

		if (instance == null) {
			try {
				instance = new GridUserFactory();
			} catch (GridUserException ex) {
				log.error("Unable to load GridUser Mapper Driver!", ex);
			}
		}
		return instance;
	}

	/**
	 * Used to modify the Mapper used to retrieve the Local User The Mapper setted
	 * will be referenceable by any GridUser built.
	 * 
	 * @param mapper
	 *          MapperInterface
	 */
	void setUserMapper(String mapperClassName) throws GridUserException {

		defaultMapperClass = makeMapperClass(mapperClassName);
	}

	/**
	 * Build a simple GridUser. No VOMS attributes are passed..
	 * 
	 * @return GridUserInterface
	 */
	GridUserInterface createGridUser(String distinguishName) {

		GridUserInterface user = new GridUser(defaultMapperClass, distinguishName);
		log.debug("Created new Grid User (NO VOMS) : {}", user);
		return user;
	}

	/**
	 * Build a simple GridUser. Parsing of proxy is not performed here! This
	 * methos is meaningful only for srmCopy call.
	 * 
	 * @return GridUserInterface
	 */
	GridUserInterface createGridUser(String distinguishName, String proxyString) {

		GridUserInterface user = new GridUser(defaultMapperClass, distinguishName,
			proxyString);
		log.debug("Created new Grid User (NO VOMS with PROXY) : {}", user);
		return user;
	}

	/**
	 * Build a VOMS Grid User, if FQAN passed are not null. Otherwise a simple
	 * GridUser instance wil be returned.
	 * 
	 * @return GridUserInterface
	 */
	GridUserInterface createGridUser(String distinguishName, FQAN[] fqans)
		throws IllegalArgumentException {

		GridUserInterface user = null;
		try {
			user = new VomsGridUser(defaultMapperClass, distinguishName, fqans);
		} catch (IllegalArgumentException e) {
		  log.error(e.getMessage(), e);
			throw e;
		}
		log.debug("Created new Grid User (VOMS USER) : {}", user);
		return user;
	}

	/**
	 * Build a VOMS Grid User, if FQAN passed are not null. Otherwise a simple
	 * GridUser instance wil be returned.
	 * 
	 * @return GridUserInterface
	 */
	GridUserInterface createGridUser(String distinguishName, FQAN[] fqans,
		String proxyString) throws IllegalArgumentException {

		GridUserInterface user = null;
		try {
			user = new VomsGridUser(defaultMapperClass, distinguishName, proxyString,
				fqans);
		} catch (IllegalArgumentException e) {
		  log.error(e.getMessage(), e);
			throw e;
		}
		log.debug("Created new Grid User (VOMS USER with PROXY) : {}" , user);
		return user;
	}

	GridUserInterface decode(Map inputParam) {

		// Member name for VomsGridUser Creation
		String member_DN = new String("userDN");
		String member_Fqans = new String("userFQANS");

		// Get DN and FQANs[]
		String dnString = (String) inputParam.get(member_DN);

		List fqansList = null;
		try {
			fqansList = Arrays.asList((Object[]) inputParam.get(member_Fqans));
		} catch (NullPointerException e) {
			log.debug("Empty FQAN[] found.", e);
		}

		// Destination Fqans array
		FQAN[] fqans = null;

		if (fqansList != null) {
			// Define FQAN[]
			fqans = new FQAN[fqansList.size()];
			log.debug("fqans_vector Size: {}" , fqansList.size());

			for (int i = 0; i < fqansList.size(); i++) {

				String fqan_string = (String) fqansList.get(i);
				log.debug("FQAN[{}]: {}",i, fqan_string);

				FQAN fq = new FQAN(fqan_string);
				fqans[i] = fq;
			}
		}

		if (dnString != null) {
			log.debug("DN: {}" , dnString);
			// Creation of srm GridUser type
			if (fqans != null && fqans.length > 0) {
				log.debug("VomsGU with FQAN");
				try {
					return createGridUser(dnString, fqans);
				} catch (IllegalArgumentException e) {
				  log.error(e.getMessage(), e);
				}
			} else {
				return createGridUser(dnString);
			}
		}
		return null;
	}

	MapperInterface makeMapperInstance(Class mapperClass)
		throws CannotMapUserException {

		MapperInterface mapperInstance = null;

		if (mapperClass == null) {
			throw new CannotMapUserException(
				"Cannot build Mapper Driver instance without a valid Mapper Driver Class!");
		}

		if (!MapperInterface.class.isAssignableFrom(mapperClass)) {
			throw new CannotMapUserException(
				"Unable to instantiate the Mapper Driver. "
					+ "The provided MapperClass does not implements MapperInterface");
		}
		try {
			mapperInstance = (MapperInterface) mapperClass.newInstance();
		} catch (IllegalAccessException ex) {
			log.error("Unable to instantiate the Mapper Driver. Illegal Access.", ex);
			throw new CannotMapUserException(
				"Unable to instantiate the Mapper Driver. Illegal Access.", ex);
		} catch (InstantiationException ex) {
			log.error("Unable to instantiate the Mapper Driver. Generic problem..",
				ex);
			throw new CannotMapUserException(
				"Unable to instantiate the Mapper Driver", ex);
		}

		return mapperInstance;
	}

	private MapperInterface makeMapperClass(String mapperClassName)
		throws GridUserException {

		MapperInterface mapper = null;
		Class mapperClass = null;
		if (mapperClassName == null) {
			throw new GridUserException(
				"Cannot load Mapper Driver without a valid Mapper Driver Class Name!");
		}

		// Retrieve the Class of driver
		try {
			mapperClass = Class.forName(mapperClassName);
		} catch (ClassNotFoundException e) {
			throw new GridUserException(
				"Cannot load Mapper Driver instance without a valid Mapper Driver Class Name!",
				e);
		}

		// Check if the Class implements the right interface
		if (!MapperInterface.class.isAssignableFrom(mapperClass)) {
			throw new GridUserException(
				"Cannot load Mapper Driver instance without a valid Mapper Driver Class Name!");
		}
		try {
			Constructor<MapperInterface>[] constructors = (Constructor<MapperInterface>[]) mapperClass
				.getConstructors();
			boolean found = false;
			for (Constructor<MapperInterface> constructor : constructors) {
				if (constructor.getParameterTypes().length == 0) {
					found = true;
					break;
				}
			}
			if (found) {
				mapper = (MapperInterface) mapperClass.newInstance();
			} else {
				try {
					Method method = ((Class<MapperInterface>) mapperClass).getMethod(
						"getInstance", null);
					if (Modifier.isStatic(method.getModifiers())) {
						try {
							mapper = (MapperInterface) method.invoke(this, null);
						} catch (IllegalArgumentException e) {
						  log.error(e.getMessage(), e);
							throw new GridUserException(
								"Cannot instantiate Mapper Driver using getInstance for Mapper Driver named :'"
									+ mapperClassName + "'");
						} catch (InvocationTargetException e) {
						  log.error(e.getMessage(), e);
							throw new GridUserException(
								"Cannot instantiate Mapper Driver using getInstance for Mapper Driver named :'"
									+ mapperClassName + "'");
						}
					} else {
						log
							.error("Unable to instantiate the class using eiter no args constructor niether getInstance method. getInstance exists but is not static");
						throw new GridUserException(
							"Cannot instantiate Mapper Driver using new or getInstance for Mapper Driver named :'"
								+ mapperClassName + "'");
					}
				} catch (SecurityException e) {
				  log.error(e.getMessage(), e);
					throw new GridUserException(
						"Cannot instantiate Mapper Driver using getInstance for Mapper Driver named :'"
							+ mapperClassName + "'");
				} catch (NoSuchMethodException e) {
				  log.error(e.getMessage(), e);
					throw new GridUserException(
						"Cannot instantiate Mapper Driver using new or getInstance for Mapper Driver named :'"
							+ mapperClassName + "'");
				}
			}

		} catch (IllegalAccessException e) {
		  log.error(e.getMessage(), e);
			throw new GridUserException(
				"Cannot create a new Instance of the Mapper Driver named :'"
					+ mapperClassName + "'");
		} catch (InstantiationException e) {

		  log.error(e.getMessage(), e);
			throw new GridUserException(
				"Cannot create a new Instance of the Mapper Driver named :'"
					+ mapperClassName + "'");
		}
		return mapper;
	}
}
