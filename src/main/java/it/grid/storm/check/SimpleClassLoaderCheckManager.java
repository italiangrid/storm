/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.check;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto THIS CLASS HAS TO BE TESTED
 */
public class SimpleClassLoaderCheckManager extends CheckManager {

	private static final Logger log = LoggerFactory
		.getLogger(SimpleClassLoaderCheckManager.class);

	private ArrayList<Check> checks = new ArrayList<Check>();

	@Override
	protected Logger getLogger() {

		return log;
	}

	@Override
	protected void loadChecks() {

		CodeSource source = SimpleClassLoaderCheckManager.class
			.getProtectionDomain().getCodeSource();
		URL location = null;
		if (source != null) {
			location = source.getLocation();
			log.info("location: {}", location);
		}
		String packageResourcePath = "it" + File.separatorChar + "grid"
			+ File.separatorChar + "storm" + File.separatorChar + "check"
			+ File.separatorChar + "sanity";
		List<String> classes = getClasseNamesInPackage(location.toString(),
			packageResourcePath);
		for (String className : classes) {
			Class<?> classe = null;
			try {
				classe = Class.forName(className);
			} catch (ClassNotFoundException e) {
				log.error(e.getMessage());
			}
			Constructor<?> constructor;
			try {
				constructor = classe.getConstructor();
				try {
					Check check = (Check) constructor.newInstance();
					checks.add(check);
				} catch (IllegalArgumentException e) {
					log.error(e.getMessage(), e);
				} catch (InstantiationException e) {
					log.error(e.getMessage(), e);
				} catch (IllegalAccessException e) {
					log.error(e.getMessage(), e);
				} catch (InvocationTargetException e) {
					log.error(e.getMessage(), e);
				}
			} catch (SecurityException e1) {
				log.error(e1.getMessage(), e1);
			} catch (NoSuchMethodException e1) {
				log.error(e1.getMessage(), e1);
			}
		}
	}

	private List<String> getClasseNamesInPackage(String jarName,
		String packageName) {

		ArrayList<String> arrayList = new ArrayList<String>();
		packageName = packageName.replaceAll("\\.", "" + File.separatorChar);
		try {
			JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
			JarEntry jarEntry;
			while (true) {
				jarEntry = jarFile.getNextJarEntry();
				if (jarEntry == null) {
					break;
				}
				if ((jarEntry.getName().startsWith(packageName))
					&& (jarEntry.getName().endsWith(".class"))) {
					arrayList.add(jarEntry.getName().replaceAll("" + File.separatorChar,
						"\\."));
				}
			}
			jarFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrayList;
	}

	@Override
	protected List<Check> prepareSchedule() {

		return checks;
	}
}
