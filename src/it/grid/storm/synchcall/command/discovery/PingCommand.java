package it.grid.storm.synchcall.command.discovery;

import it.grid.storm.Constants;
import it.grid.storm.config.Configuration;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.InvalidTExtraInfoAttributeException;
import it.grid.storm.srm.types.TExtraInfo;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.DiscoveryCommand;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.discovery.PingInputData;
import it.grid.storm.synchcall.data.discovery.PingOutputData;
import it.grid.storm.tape.recalltable.RecallTableCatalog;
import it.grid.storm.tape.recalltable.RecallTableException;
import it.grid.storm.tape.recalltable.model.RecallTaskData;
import it.grid.storm.tape.recalltable.persistence.RecallTaskBuilder;
import it.grid.storm.tape.recalltable.persistence.RecallTaskBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Map.Entry;

import javax.ws.rs.core.Response;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008 Company: INFN-CNAF and
 * ICTP/EGRID project
 * 
 * @author lucamag
 * @author Alberto Forti
 * @date May 28, 2008
 * 
 */

public class PingCommand extends DiscoveryCommand implements Command {

    public PingCommand() {}

    public OutputData execute(InputData data) {
        PingOutputData outputData = new PingOutputData();
        PingInputData inputData = (PingInputData) data;

        outputData.setVersionInfo("StoRM - SRM Version 2.2");

        ArrayOfTExtraInfo extraInfoArray = new ArrayOfTExtraInfo();
        TExtraInfo otherInfo = null;

        // Extract KEY from AuthorizationID
        String key = getKey(inputData.getAuthorizationID());

        // Refresh hashmap <KEY,VALUE>
        Properties pingValues = loadProperties();

        // Search key value
        boolean foundKey = pingValues.containsKey(key);
        if(foundKey)
		{
        	for(Entry<Object, Object> entry : pingValues.entrySet())
			{

				try
				{
					otherInfo = new TExtraInfo(entry.getKey().toString() , entry.getValue().toString());
					log.debug("srmPing: Found the value for key='" + key + "' = '"
						+ otherInfo + "'");
				} catch(InvalidTExtraInfoAttributeException ex)
				{
					log.error("Invalid KEY requested in Ping.");
					otherInfo = new TExtraInfo();
				}
				extraInfoArray.addTExtraInfo(otherInfo);
			}
		}
		else
		{ // Catch the special cases
			extraInfoArray = manageSpecialKey(key);
		}
       
        // Build the Output Data
        outputData.setExtraInfoArray(extraInfoArray);

        // Building INFO Log
        String infoLogs = "srmPing: " + "<" + inputData.getRequestor().toString() + ">" + "[AuthID:'"
                + inputData.getAuthorizationID() + "']" + "return values: [" + extraInfoArray + "]";
        log.info(infoLogs);

        return outputData;
    }
    
    
    /**
     * 
     * @param authorizationID String
     * @return String  normalizedAuthID
     */
    private String getKey(String authorizationID) {

		String result = authorizationID.trim();
//		String prefix = authorizationID.substring(0, 4).toLowerCase();
//		if(prefix.equals("key="))
    	if(result.substring(0, 4).equalsIgnoreCase("key="))
		{
			result = result.substring(4);
		}
		log.debug("Retrieved KEY:'" + result + "' from AuthorizationID : '"
			+ authorizationID + "'");
		return result;
	}

    /**
     * 
     * @return Properties
     */
    private Properties loadProperties() {

        Properties properties = new Properties();

        Configuration config = Configuration.getInstance();
        String configurationPATH = config.namespaceConfigPath(); // Default = "./etc/"
        String pingPropertiesFileName = config.getPingValuesPropertiesFilename(); // Default =
                                                                                  // "ping-values.properties"
        String propertiesFile = configurationPATH + File.separator + pingPropertiesFileName;

        // Check if the file Exists
        if (new File(propertiesFile).exists()) {
            // Read properties file.
            try {
                properties.load(new FileInputStream(propertiesFile));
                log.debug("srmPing: Loaded PING values from the properties file: '" + pingPropertiesFileName
                        + "'");
            } catch (IOException e) {
                log.error("Error while readind Ping Values in file : '" + propertiesFile + "' EXCEPTION:" + e);
            }
        }

        // Add in properties the Mandatory Properties Values
        properties.put(Constants.BE_VERSION.getKey(), Constants.BE_VERSION.getValue());
        properties.put(Constants.BE_OS_DISTRIBUTION.getKey(), Constants.BE_OS_DISTRIBUTION.getValue());
        log.debug("srmPing: Loaded NR " + properties.size() + " PING key/value couple.");
        return properties;
    }

    /****************************
     *  SPECIAL KEY MANAGEMENT 
     ****************************/
    
    /**
     * Dispatcher for manage the special keys on Ping
     * 
     * @param param
     * @return
     */
    private ArrayOfTExtraInfo manageSpecialKey(String key) {
        ArrayOfTExtraInfo arrayResult = new ArrayOfTExtraInfo();
        SpecialKey specialKey = SpecialKey.fromString(key);
        switch (specialKey) {
            case ALL:
				arrayResult = allKeys(extractParam(key));
				break;
			case BE_OS_PLATFORM:
				 try
				{
					arrayResult.addTExtraInfo(new TExtraInfo(key,
						Constants.BE_OS_PLATFORM.getValue()));
				} catch(InvalidTExtraInfoAttributeException e)
				{
					log.warn("Really strange!");
				}
				break;
			case BE_OS_KERNEL_RELEASE:
				try
				{
					arrayResult.addTExtraInfo(new TExtraInfo(key,
						Constants.BE_OS_KERNEL_RELEASE.getValue()));
				} catch(InvalidTExtraInfoAttributeException e)
				{
					log.warn("Really strange!");
				}
				break;
			case TEST_POST_NEW_TASK:
				arrayResult = test_post_new_task(extractParam(key));
				break;
			case TEST_PUT_NEW_STATUS:
				arrayResult = test_put_new_status(extractParam(key));
				break;
			case TEST_PUT_RETRY_VALUE:
				arrayResult = test_put_retry_value(extractParam(key));
				break;
			case TEST_TAKEOVER:
				arrayResult = test_takeover(extractParam(key));
				break;
            default: { 
                TExtraInfo extraInfo = new TExtraInfo();
                try {
                    extraInfo = new  TExtraInfo(SpecialKey.UNKNOWN.toString(), SpecialKey.UNKNOWN.getDescription()+":'"+key+"'");
                } catch (InvalidTExtraInfoAttributeException e) {
                    log.warn("Really strange!");
                }
                arrayResult.addTExtraInfo(extraInfo);
                break;
            }
        }
        return arrayResult;
    }

    /**
     * 
     * @param param
     * @return
     */
    private ArrayOfTExtraInfo allKeys(String param) {
        ArrayOfTExtraInfo arrayResult = new ArrayOfTExtraInfo();
        Properties pingValues = loadProperties();
        TExtraInfo otherInfo = new TExtraInfo();
        log.debug("srmPing: Found a request to retrieve ALL key values. (NR:" + pingValues.size()+ ")");
          // Insert all keys/values in to the results
          for (Enumeration<?> e = pingValues.propertyNames(); e.hasMoreElements();) {
              String key = (String) e.nextElement();
              String value = pingValues.getProperty(key);
              try {
                  otherInfo = new TExtraInfo(key, value);
              } catch (InvalidTExtraInfoAttributeException ex) {
                  log.error("Invalid KEY (key='" + key + "') requested in Ping.");
                  otherInfo = new TExtraInfo();
              }
              arrayResult.addTExtraInfo(otherInfo);
          }
        return arrayResult;
    }
    
    /**
     * 
     * @param param
     * @return
     */
    private ArrayOfTExtraInfo test_post_new_task(String param) {
        ArrayOfTExtraInfo arrayResult = new ArrayOfTExtraInfo();
        // Recall Table Catalog
        String errorStr;
        RecallTableCatalog rtCat = null;
        try {
            rtCat = new RecallTableCatalog(false);
        } catch (DataAccessException e) {
            errorStr = "Unable to use RecallTable DB.";
            log.error(errorStr);
        }

        // Parsing of the inputString to extract the fields of RecallTask
        RecallTaskData rtd;
        try {
            rtd = RecallTaskData.buildFromString(param);
            log.debug("RTD=" + rtd.toString());
            // Store the new Recall Task if it is all OK.
            RecallTaskTO task = RecallTaskBuilder.buildFromPOST(rtd);
            if (rtCat != null) {
                rtCat.insertNewTask(task);
                URI newResource = URI.create("/" + task.getTaskId());
                log.debug("New task resource created: " + newResource);
            }       
           
        } catch (RecallTableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
      
        return arrayResult;
    }
 
    private ArrayOfTExtraInfo test_put_new_status(String param) {
        ArrayOfTExtraInfo arrayResult = new ArrayOfTExtraInfo();
        // TODO  to implement this special key
        return arrayResult;
    }
    
    private ArrayOfTExtraInfo test_put_retry_value(String param) {
        ArrayOfTExtraInfo arrayResult = new ArrayOfTExtraInfo();
        // TODO  to implement this special key       
        return arrayResult;
    }
    
    private ArrayOfTExtraInfo test_takeover(String param) {
        ArrayOfTExtraInfo arrayResult = new ArrayOfTExtraInfo();
        TExtraInfo otherInfo = new TExtraInfo();

        // take over only one task per time
        int numbOfTask = 1;

        // Recall Table Catalog
        RecallTableCatalog rtCat = null;

        try {
            rtCat = new RecallTableCatalog(false);
            // Retrieve the Task
            ArrayList<RecallTaskTO> tasks = rtCat.takeoverNTasks(numbOfTask);

            if (tasks != null) {
                // Build the response

                for (RecallTaskTO recallTaskTO : tasks) {
                    otherInfo = new TExtraInfo(recallTaskTO.getTaskId().toString(), recallTaskTO.toString());
                    arrayResult.addTExtraInfo(otherInfo);
                }
            }
        } catch (DataAccessException e) {
            log.error("Unable to use RecallTable DB.");
        } catch (InvalidTExtraInfoAttributeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return arrayResult;
    }
    
    /**********************************
     * UTILITY METHODS
     **********************************/
    
    /**
     * 
     * @param key
     * @return
     */
    private static String extractCmd(String key) {

    	String cmd = key.trim().toLowerCase();
		int indexOfEqual = cmd.indexOf("=");
		if(indexOfEqual < 0)
		{
			indexOfEqual = cmd.length();
		}
		return cmd.substring(0, indexOfEqual);
	}

    /**
     * @param key
     * @return
     */
    private static String extractParam(String key) {

		String normalizedKey = key.trim().toLowerCase();
		String param = "";
		int equalIndex = normalizedKey.indexOf("=");
		if(equalIndex > 0)
		{
			param = normalizedKey.substring(equalIndex + 1);
		}
		return param;
	}
    
    /**********************************
     * MAIN for TEST PURPOUSE
     **********************************/
      
    public static void main(String arg[]){

    }

    /**
     * 
     *
     */
   private enum SpecialKey {
	   
       ALL("all","return all the pair <key,value> defined in properties"),
       BE_OS_PLATFORM(Constants.BE_OS_PLATFORM.getKey(),"returns the operating system platform"),
       BE_OS_KERNEL_RELEASE(Constants.BE_OS_KERNEL_RELEASE.getKey(),"returns the operating system kernel release"),
       TEST_TAKEOVER("take-over","testing the take-over method"), 
       TEST_POST_NEW_TASK("new-task","testing the take-over method"), 
       TEST_PUT_NEW_STATUS("new-status","testing the take-over method"), 
       TEST_PUT_RETRY_VALUE("retry-value","testing the take-over method"),
       UNKNOWN("unknown","Unable to manage the key");

       
       private final String operationName;
       private final String operationDescription;      
       
       private SpecialKey(String opName, String opDescr) {

			this.operationName = opName;
			this.operationDescription = opDescr;
		}

		public String getDescription() {

			return operationDescription;
		}
       
       public static SpecialKey fromString(String keyStr) {

			String cmd = extractCmd(keyStr);
			for(SpecialKey keyValue : SpecialKey.values())
			{
				if(keyValue.toString().equalsIgnoreCase(cmd))
				{
					return keyValue;
				}
			}
			return SpecialKey.UNKNOWN;
		}
       
       @Override
		public String toString() {

			return operationName;
		}
	}
}
