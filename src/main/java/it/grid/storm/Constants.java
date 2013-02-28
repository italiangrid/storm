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

package it.grid.storm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	private static final Logger log = LoggerFactory.getLogger(Constants.class);
	
	public static Entry BE_VERSION = new Entry("BE-Version", ">=1.5.3 (manual building)");
    public static final Entry NAMESPACE_VERSION = new Entry("Namespace-version", "1.5.0");
    public static final Entry BE_OS_DISTRIBUTION = new Entry("BE-OS-Distribution", getDistribution());
    public static final Entry BE_OS_PLATFORM = new Entry("BE-OS-Platform", getPlatform());
    public static final Entry BE_OS_KERNEL_RELEASE = new Entry("BE-OS-Kernel-Release", getKernelRelease());
    private static String distribution = null;
    private static String platform = null;
    private static String kernelRelease = null;
    private static final String notAvailable = "N/A";
    
	static
	{
		ClassLoader loader = Constants.class.getClassLoader();
		if(loader == null)
		{
			loader = ClassLoader.getSystemClassLoader();
		}
		String VERSIONResourcePath =
									 "it" + File.separatorChar + "grid"
										 + File.separatorChar + "storm"
										 + File.separatorChar + "VERSION";
		InputStream inStream = loader.getResourceAsStream(VERSIONResourcePath);
		if(inStream == null)
		{
            log.warn("No "
                    + VERSIONResourcePath
                    + " file available in resource path. Using the default values (NOT IN PRODUCTION!). Maybe you are using StoRM built without the original ANT.");
	}
		else
		{
			Properties versioningroperties = new Properties();
			try
			{
				versioningroperties.load(inStream);
			} catch(IOException e)
			{
				log.warn("Unable to retrieve the VERSION property file.");
			}
			parseVersionInStream(versioningroperties);
		}
	}
    
    private static void parseVersionInStream(Properties prop)
    {
        String version = prop.getProperty("VERSION","N/A");
        String age = prop.getProperty("RELEASE","N/A");
        String svnRevision = prop.getProperty("svn.revision.number","N/A");
        String buildDate = prop.getProperty("build.date","N/A");
    	BE_VERSION = new Entry("BE-Version", version+"-"+age);
    	log.debug("SVN-Revision : "+svnRevision);
    	log.debug("Build Date   : "+buildDate);
    }
    
    /**
     * @return
     */
    public static String getDistribution()
    {
    	if(distribution == null)
    	{
    		populateDistribution();
    	}
    	return distribution;
    }
    
    /**
     * @return
     */
    public static String getPlatform()
    {
    	if(platform == null)
    	{
    		populatePlatformKernel();
    	}
    	return platform;
    }
    
    /**
     * @return
     */
    public static String getKernelRelease()
    {
    	if(kernelRelease == null)
    	{
    		populatePlatformKernel();
    	}
    	return kernelRelease;
    }
    
    /**
     * 
     */
    private static void populateDistribution() {
        distribution = notAvailable;
        String issuePath = File.separatorChar + "etc" + File.separatorChar + "issue";
        File issueFile = new File(issuePath);
        if (!issueFile.exists() || !issueFile.isFile() || !issueFile.canRead()) {
            log.warn("Unable to read " + issueFile.getAbsolutePath() + " file!!");
        } else {
            try {
                BufferedReader issueReader = new BufferedReader(new FileReader(issueFile));
                String output = issueReader.readLine();
                if (output == null) {
                    log.warn("The file " + issueFile.getAbsolutePath() + " is empty!");
                } else {
                    distribution = output;
                }
            } catch (FileNotFoundException e) {
                log.error("Unable to read file '" + issueFile.getAbsolutePath() + "'. " + e);
            } catch (IOException e) {
                log.error("Unable to read file '" + issueFile.getAbsolutePath() + "'." + e);
            }
        }
    }
    
    /**
     * 
     */
    private static void populatePlatformKernel() {

        try {
            Process p = Runtime.getRuntime().exec("uname -ri");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String error = stdError.readLine();
            String output = stdInput.readLine();
            if (output == null || stdInput.read() != -1 || error != null) {
                while (error != null) {
                    error += stdError.readLine();
                }
                log.error("Unable to invoke \'uname -ri\' . Standard error : " + error);
            } else {
                String[] fields = output.trim().split(" ");
                kernelRelease = fields[0];
                platform = fields[1];
            }
        } catch (IOException e) {
            log.error("Unable to invoke \'uname -ri\' . IOException " + e);
        } finally {
            if (distribution == null) {
                platform = notAvailable;
                kernelRelease = notAvailable;
            }
        }
    }
    
    
    public static class Entry
	{
		private final String key;
		private final String value;

		private Entry(String key, String value) {

			this.key = key;
			this.value = value;
		}

		public String getKey() {

			return key;
		}

		public String getValue() {

			return value;
		}
	}
}
