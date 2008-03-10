/**
 * @author  Alberto Forti
 * @author  CNAF-INFN Bologna
 * @date    Feb 2007
 * @version 1.0
 */

package it.grid.storm.synchcall.discovery;

import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.TExtraInfo;
import it.grid.storm.srm.types.InvalidTExtraInfoAttributeException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.logging.Log;
import it.grid.storm.config.Configuration;
import java.io.File;
import java.util.Enumeration;
import it.grid.storm.Constants;

public class PingExecutor
{
    private Log log = DiscoveryManagerImpl.getLogger();

    public PingExecutor() {
    }

    public PingOutputData doIt(PingInputData inputData)
    {
        PingOutputData outputData = new PingOutputData();

        outputData.setVersionInfo("StoRM - SRM Version 2.2");

        ArrayOfTExtraInfo extraInfoArray = new ArrayOfTExtraInfo();
        TExtraInfo otherInfo = null;

        //Extract KEY from AuthorizationID
        String key = getKey(inputData.getAuthorizationID());

        //Refresh hashmap <KEY,VALUE>
        Properties pingValues = loadProperties();

        //Search key value
        boolean foundKey = pingValues.containsKey(key);
        if (foundKey) {
          try {
            otherInfo = new TExtraInfo(key, pingValues.getProperty(key));
            log.debug("srmPing: Found the value for key='" + key + "' = '"+otherInfo+"'");
          }
          catch (InvalidTExtraInfoAttributeException ex) {
            log.error("Invalid KEY requested in Ping.");
            otherInfo = new TExtraInfo();
          }
          extraInfoArray.addTExtraInfo(otherInfo);
        } else {
          //Catch the special case KEY=ALL
          if (key.equals("ALL")) {
            log.debug("srmPing: Found a request to retrieve ALL kye values. (NR:"+pingValues.size()+")");
            //Insert all keys/values in to the results
            for (Enumeration e = pingValues.propertyNames(); e.hasMoreElements(); ) {
              key = (String) e.nextElement();
              String value = pingValues.getProperty(key);
              try {
                otherInfo = new TExtraInfo(key, value);
              }
              catch (InvalidTExtraInfoAttributeException ex) {
                log.error("Invalid KEY (key='" + key + "') requested in Ping.");
                otherInfo = new TExtraInfo();
              }
              extraInfoArray.addTExtraInfo(otherInfo);
            } // end for
          } else {
            //Key unknown..
            log.warn("Unable to retrieve KEY (key='"+key+"') value requested in srmPing.");
            otherInfo = new TExtraInfo();
          }
        }

        //Build the Output Data
        outputData.setExtraInfoArray(extraInfoArray);

        //Buildin INFO Log
        String infoLogs = "srmPing: " +
                          "<"+inputData.getRequestor().toString()+">" +
                          "[AuthID:'"+inputData.getAuthorizationID()+"']" +
                          "return values: ["+
                          extraInfoArray + "]";
        log.info(infoLogs);

        return outputData;
    }

    /**
     *
     * @param authorizationID String
     * @return String
     */
    private String getKey(String authorizationID) {
      String result = authorizationID.trim();
      String prefix = authorizationID.substring(0,4).toLowerCase();
      if (prefix.equals("key=")) {
        result = authorizationID.substring(4);
      }
      log.debug("Retrieved KEY:'"+result+"' from AuthorizationID : '"+authorizationID+"'");
      return result;
    }


    /**
     *
     * @return Properties
     */
    private Properties loadProperties() {

      Properties properties = new Properties();

      Configuration config = Configuration.getInstance();
      String configurationPATH = config.getNamespaceConfigPath(); //Default = "./etc/"
      String pingPropertiesFileName = config.getPingValuesPropertiesFilename(); //Default = "ping-values.properties"
      String propertiesFile = configurationPATH + File.separator + pingPropertiesFileName;

      //Check if the file Exists
      boolean exists = (new File(propertiesFile).exists());

      if (exists) {
        // Read properties file.
        try {
          properties.load(new FileInputStream(propertiesFile));
          log.debug("srmPing: Loaded PING values from the properties file: '"+pingPropertiesFileName+"'" );
        }
        catch (IOException e) {
           log.error("Error while readind Ping Values in file : '"+propertiesFile+"' EXCEPTION:"+e);
        }
      }

      //Add in properties the Mandatory Properties Values
      properties.put(Constants.BE_VERSION.getKey(), Constants.BE_VERSION.getValue());
      log.debug("srmPing: Loaded NR"+properties.size()+" PING key/value couple.");
      return properties;
    }
}
