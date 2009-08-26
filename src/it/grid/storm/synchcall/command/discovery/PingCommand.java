package it.grid.storm.synchcall.command.discovery;

import it.grid.storm.Constants;
import it.grid.storm.config.Configuration;
import it.grid.storm.persistence.PersistenceDirector;
import it.grid.storm.persistence.dao.TapeRecallDAO;
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
import it.grid.storm.tape.recalltable.model.RecallTaskStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

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
        if (foundKey) {
            try {
                otherInfo = new TExtraInfo(key, pingValues.getProperty(key));
                log.debug("srmPing: Found the value for key='" + key + "' = '" + otherInfo + "'");
            } catch (InvalidTExtraInfoAttributeException ex) {
                log.error("Invalid KEY requested in Ping.");
                otherInfo = new TExtraInfo();
            }
            extraInfoArray.addTExtraInfo(otherInfo);
        } else {
            // Catch the special case KEY=ALL
            if (key.equals("ALL")) {
                log.debug("srmPing: Found a request to retrieve ALL kye values. (NR:" + pingValues.size()
                        + ")");
                // Insert all keys/values in to the results
                for (Enumeration e = pingValues.propertyNames(); e.hasMoreElements();) {
                    key = (String) e.nextElement();
                    String value = pingValues.getProperty(key);
                    try {
                        otherInfo = new TExtraInfo(key, value);
                    } catch (InvalidTExtraInfoAttributeException ex) {
                        log.error("Invalid KEY (key='" + key + "') requested in Ping.");
                        otherInfo = new TExtraInfo();
                    }
                    extraInfoArray.addTExtraInfo(otherInfo);
                } // end for
            } else {
                // Key unknown..
                log.warn("Unable to retrieve KEY (key='" + key + "') value requested in srmPing.");
                otherInfo = new TExtraInfo();
            }
        }

        try {

            TapeRecallDAO tapeDAO = PersistenceDirector.getDAOFactory().getTapeRecallDAO();

            RecallTaskTO task = tapeDAO.takeoverTask();

            if (task != null) {
                tapeDAO.setTaskStatus(task.getTaskId(), RecallTaskStatus.SUCCESS.getStatusId());

                log.info("Task \"" + task.getTaskId() + "\" set to success: " + task.getFileName());
            }

        } catch (DataAccessException e) {
            log.error("DB error", e);
        }

        // Build the Output Data
        outputData.setExtraInfoArray(extraInfoArray);

        // Buildin INFO Log
        String infoLogs = "srmPing: " + "<" + inputData.getRequestor().toString() + ">" + "[AuthID:'"
                + inputData.getAuthorizationID() + "']" + "return values: [" + extraInfoArray + "]";
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
        String prefix = authorizationID.substring(0, 4).toLowerCase();
        if (prefix.equals("key=")) {
            result = authorizationID.substring(4);
        }
        log.debug("Retrieved KEY:'" + result + "' from AuthorizationID : '" + authorizationID + "'");
        return result;
    }

    /**
     * 
     * @return Properties
     */
    private Properties loadProperties() {

        Properties properties = new Properties();

        Configuration config = Configuration.getInstance();
        String configurationPATH = config.getNamespaceConfigPath(); // Default = "./etc/"
        String pingPropertiesFileName = config.getPingValuesPropertiesFilename(); // Default =
                                                                                  // "ping-values.properties"
        String propertiesFile = configurationPATH + File.separator + pingPropertiesFileName;

        // Check if the file Exists
        boolean exists = (new File(propertiesFile).exists());

        if (exists) {
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
        log.debug("srmPing: Loaded NR" + properties.size() + " PING key/value couple.");
        return properties;
    }
}
