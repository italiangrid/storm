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
package it.grid.storm.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author ritz
 * 
 */
public class ConfigurationKeys {

	private static final String fs = File.separator;
	private final String configFN = "src" + fs + "it" + fs + "grid" + fs
		+ "storm" + fs + "config" + fs + "Configuration.java";

	public HashMap<String, String> getMethodsKeys() {

		HashMap<String, String> method_key = new HashMap<String, String>();

		try {
			// Retrieve the list of defined methods
			Method m[] = Configuration.instance.getClass().getDeclaredMethods();

			for (int i = 0; i < m.length; i++) {
				String methodName = m[i].getName();
				if ((m[i].getName().substring(0, 3).equals("get"))
					&& (!m[i].getName().equals("getInstance"))) {
					String keyName = "unknown";
					method_key.put(methodName, keyName);
				}
			}
			System.out.println("Configuration has " + method_key.size()
				+ " methods getting key values.");
		} catch (SecurityException se) {
			System.out.println("Unable to list the definible keys.." + se);
		}

		// Store the source code into ArrayList
		int lineNr = 0;
		String configurationCodeFileName = System.getProperty("user.dir") + fs
			+ configFN;
		ArrayList<String> configurationCode = new ArrayList<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(
				configurationCodeFileName));
			String str;
			while ((str = in.readLine()) != null) {
				configurationCode.add(str);
				lineNr++;
			}
			in.close();
			System.out.println("Configuration has " + lineNr + " lines of code.");
		} catch (IOException e) {
			System.out.println("Some problem to read '" + configurationCodeFileName
				+ "'");
		}

		int currentLine = 0;
		// Scan the source code to retrieve the keys
		for (String line : configurationCode) {
			String method;
			currentLine++;
			// check if the line is a get method definition.
			if (line.contains("public")) {
				String[] fields = line.split(" ");
				for (String field : fields) {
					if (field.startsWith("get")) {
						int endIndex = field.indexOf("(");
						if (endIndex == -1) {
							endIndex = field.length();
						}
						method = field.substring(0, endIndex);

						// Check if the method is present into the list
						if (method_key.containsKey(method)) {
							// Found the method, search for the key.
							// System.out.println("found a method GET : '" + method + "'");
							int line_to_end = lineNr - currentLine;
							int lineToScan = (line_to_end > 5 ? 5 : line_to_end);
							for (int j = currentLine; j < currentLine + lineToScan; j++) {
								if (configurationCode.get(j).contains("String key")) {
									String lineKey = configurationCode.get(j);
									// System.out.println("Key line : " + lineKey);
									// Found a key.. extract the key-name
									int beginIndex = lineKey.indexOf("\"");
									if (beginIndex > 0) {
										// Found \"
										String keyName = lineKey.substring(beginIndex);
										endIndex = keyName.indexOf(";");
										if (endIndex > 0) {
											keyName = keyName.substring(1, endIndex - 1);
											// System.out.println("found a KEY : '" + keyName + "'");
											method_key.put(method, keyName);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return method_key;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getKeys() {

		ArrayList<String> definedKeys = new ArrayList<String>();

		HashMap<String, String> method_key = getMethodsKeys();

		// Print out method-keys
		Set<String> methods = method_key.keySet();
		for (String meth : methods) {
			// System.out.println("method: " + meth);
			String key = method_key.get(meth);
			// System.out.println("  key : " + key);
			if (key.equals("unknown")) {
				System.out.println("#### Meth: " + meth + "  --- " + key);
			}
			definedKeys.add(method_key.get(meth));
			// System.out.println("---");
		}
		return definedKeys;
	}

	public List<String> getTemplateKeys() {

		ArrayList<String> templateKeys = new ArrayList<String>();
		// Store the source code into ArrayList
		String templateFileName = System.getProperty("user.dir") + fs + "etc" + fs
			+ "storm.properties.template";
		Properties template = new Properties();
		try {
			template.load(new FileInputStream(templateFileName));
		} catch (IOException e) {
			System.out.println("Error while reading the properties template");
		}

		for (Enumeration<Object> scan = template.keys(); scan.hasMoreElements();) {
			templateKeys.add("" + scan.nextElement());
		}
		return templateKeys;
	}

	public List<String> getDuplicatedKeys() {

		ArrayList<String> duplicatedKeys = new ArrayList<String>();
		ArrayList<String> keys = new ArrayList<String>();
		// Store the source code into ArrayList
		String templateFileName = System.getProperty("user.dir") + fs + "etc" + fs
			+ "storm.properties.template";

		// Store the key into ArrayList
		int lineNr = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(templateFileName));
			String str;
			while ((str = in.readLine()) != null) {
				if (!(str.startsWith("#"))) {
					if (str.length() > 0) {
						String line = str.trim();
						int index = line.indexOf("=");
						if (index < 0) {
							System.out.println("strange line : " + line);
						} else {
							String keyName = line.substring(0, index);
							if (keys.contains(keyName)) {
								duplicatedKeys.add(keyName);
							} else {
								keys.add(keyName);
							}
						}
					}
				}
			}
			in.close();
			System.out.println("Configuration has " + lineNr + " lines of code.");
		} catch (IOException e) {
			System.out.println("Some problem to read '" + templateFileName + "'");
		}
		int count = 1;
		for (String key : keys) {
			System.out.println("key (" + count + "): " + key);
			count++;
		}
		return duplicatedKeys;
	}

	public HashMap<String, String> getKeyValue() {

		HashMap<String, String> method_keys = getMethodsKeys();
		HashMap<String, String> keys_values = new HashMap<String, String>();
		Method[] allMethods = Configuration.instance.getClass()
			.getDeclaredMethods();
		Set<String> methodNames = method_keys.keySet();
		System.out.println("sizes KeySet= " + methodNames.size());
		System.out.println("sizes methods = " + allMethods.length);
		for (Method m : allMethods) {
			// System.out.println("scanning for method '" + m + "'");
			if (methodNames.contains(m.getName())) {
				System.out.println("method invocable : " + m.getName());
				try {
					Object result = m.invoke(Configuration.instance, new Object[0]);
					System.out.println("Output = '" + result.toString() + "'");
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// aux = methodName.invoke(Configuration.instance, new Object[0]);

		return keys_values;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String configurationPathname = System.getProperty("user.dir")
			+ File.separator + "etc" + File.separator + "storm.properties";

		System.out.println("config file: " + configurationPathname);
		Configuration.getInstance().setConfigReader(
			new ConfigReader(configurationPathname, 0));

		ConfigurationKeys instance = new ConfigurationKeys();
		List<String> keys = instance.getKeys();
		int count = 1;
		for (String key : keys) {

			System.out.println(count + " " + key);
			count++;
		}
		System.out.println("**** keys : " + (count - 1));

		count = 1;
		List<String> tempKeys = instance.getTemplateKeys();
		for (String key : tempKeys) {

			if (!(keys.contains(key))) {
				System.out.println("UNKNOWN KEY in TEMPLATE (" + count + "):" + key);
				count++;
			}
		}

		for (String key : keys) {
			if (!(tempKeys.contains(key))) {
				System.out.println("Config KEY not in TEMPLATE (" + count + "):" + key);
				count++;
			}
		}

		for (String key : keys) {
			if ((tempKeys.contains(key))) {
				System.out.println("All right key (" + count + "): " + key);
				count++;
			}
		}

		System.out.println("### KEYS : " + (count - 1));

		System.out.println("  ================================  ");

		List<String> dupKeys = instance.getDuplicatedKeys();
		for (String key : dupKeys) {
			System.out.println("dup-key: " + key);
		}

		System.out.println(" ####################### ");
		instance.getKeyValue();
	}

}
