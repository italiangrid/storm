/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * Auxiliary Class that reads the welcome message file and provides is as String.
 */
package it.grid.storm.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 * 
 */
public class WelcomeMessage
{

	private static Logger log = LoggerFactory.getLogger(WelcomeMessage.class);
	
	private static final String welcomeFileName = "welcome.txt";

	public static String getWelcomeMessage() {

		String welcomeFilePath = Configuration.getInstance().namespaceConfigPath() + File.separatorChar + welcomeFileName;
		if(!new File(welcomeFilePath).exists())
		{
			log.error("Unable to create the welcome message. The welcome.txt file does not exist at path " + welcomeFilePath);
			return null;
		}
		StringBuilder messageBuilder = new StringBuilder();

		try
		{
			BufferedReader input = new BufferedReader(new FileReader(welcomeFilePath));
			try
			{
				String line = null;
				while((line = input.readLine()) != null)
				{
					messageBuilder.append(line);
					messageBuilder.append(System.getProperty("line.separator"));
				}
			} finally
			{
				input.close();
			}
		} catch(IOException e)
		{
			log.error("Unable to create the welcome message. " + "Error reading welcome file at path " + welcomeFilePath + "."
				+ " IOException : " + e.getMessage());
			return null;
		}

		return messageBuilder.toString();
	}
}
