/**
 * 
 */
package it.grid.storm.tape.recalltable.persistence;

import it.grid.storm.config.Configuration;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.srm.types.TRequestToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author zappi
 * 
 */
public class PropertiesDB {

    private static final Logger log = LoggerFactory.getLogger(PropertiesDB.class);
    private static Configuration config = Configuration.getInstance();
    private final String dataFileName = "recall-table.txt";
    private final String propertiesDBName; 


    public PropertiesDB() {
        String configurationDir = PropertiesDB.config.configurationDir();
        char sep = File.separatorChar;
        propertiesDBName = configurationDir + sep + "etc" + sep + "db" + sep + dataFileName;
        log.debug("Properties RecallTable-DB = " + propertiesDBName);
    }


    public PropertiesDB(boolean test) {
        String configurationDir;
        if (test) {
            configurationDir = System.getProperty("user.dir");
        } else {
            configurationDir = PropertiesDB.config.configurationDir();
        }
        char sep = File.separatorChar;
        propertiesDBName = configurationDir + sep + "etc" + sep + "db" + sep + dataFileName;
        // log.debug("Properties RecallTable-DB = " + propertiesDBName);
        File tasksDBfile = new File(propertiesDBName);
        boolean success = false;
        try {
            success = tasksDBfile.createNewFile();
        } catch (IOException e) {
            log.error("Error while trying to check : " + propertiesDBName);
            e.printStackTrace();
        }
        if (success) {
            log.debug("TaskDB = '" + propertiesDBName + "' exists ? " + success);
        }
    }


    // *************** PERSISTENCE METHODS ****************

    /**
     * 
     * @param task
     * @throws FileNotFoundException
     * @throws IOException
     * @throws DataAccessException
     */
    public void addRecallTask(RecallTaskTO task) throws FileNotFoundException, IOException, DataAccessException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesDBName));

        // Retrieve the Request-Token (unique-key)
        TRequestToken taskToken = task.getRequestToken();
        if (taskToken == null) {
            log.error("You are trying to store a Task without a task-id.");
            throw new DataAccessException("You are trying to store a Task without a task-id.");
        }
        // Build the String related to Task-id
        String taskStr = task.toString();
        // Insert the new property entry
        properties.setProperty(taskToken.getValue(), taskStr);
        // Store the properties into disk
        properties.store(new FileOutputStream(propertiesDBName), null);
    }


    public void setRecallTask(List<RecallTaskTO> listTasks) throws FileNotFoundException, IOException,
            DataAccessException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesDBName));

        TRequestToken taskToken = null;
        String taskStr = null;
        for (RecallTaskTO recallTaskTO : listTasks) {
            // Retrieve the Task-id (unique-key)
            taskToken = recallTaskTO.getRequestToken();
            if (taskToken == null) {
                log.error("You are trying to store a Task without a RequestToken.");
                throw new DataAccessException("You are trying to store a Task without a Request-Token.");
            }
            // Build the String related to Task-id
            taskStr = recallTaskTO.toString();
            // Insert the new property entry
            properties.setProperty(taskToken.getValue(), taskStr);
            taskToken = null;
        }
        // Store the properties into disk
        properties.store(new FileOutputStream(propertiesDBName), null);
    }


    public List<RecallTaskTO> getRecallTask(UUID taskId) throws FileNotFoundException, IOException, DataAccessException {
        ArrayList<RecallTaskTO> result = new ArrayList<RecallTaskTO>();
        Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesDBName));
  
        for (Object values : properties.values()) {
            String v = (String)values;
            RecallTaskTO task = RecallTaskBuilder.build(v);
            if (task.getTaskId().equals(taskId)) {
                result.add(task);
            }
        }
        if (result.isEmpty()) {
            log.error("Unable to retrieve the task with ID = " + taskId);
            throw new DataAccessException("Unable to find the task with ID = " + taskId);     
        }
        return result;
    }


    
    public void updateRecallTask(RecallTaskTO task) throws FileNotFoundException, IOException, DataAccessException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesDBName));

        UUID taskId = task.getTaskId();

        // Check if the Task exists within the Properties DB
        boolean taskExist = properties.containsKey(taskId.toString());
        if (!(taskExist)) {
            log.error("Unable to find the task with ID = " + taskId);
            throw new DataAccessException("Unable to find the task with ID = " + taskId);
        } else {
            // Build the String related to Task-id
            String taskStr = task.toString();
            // Insert the new property entry
            properties.setProperty(taskId.toString(), taskStr);
            log.debug("Removed tasks '" + taskId + "'");
        }

        // Store the properties into disk
        properties.store(new FileOutputStream(propertiesDBName), null);
    }


    public void deleteRecallTask(UUID taskId) throws FileNotFoundException, IOException, DataAccessException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesDBName));

        // Retrieve the Task from taskId
        String task = properties.getProperty(taskId.toString());
        if (task == null) {
            log.error("Unable to find the task with ID = " + taskId);
            throw new DataAccessException("Unable to find the task with ID = " + taskId);
        } else {
            properties.remove(taskId);
            log.debug("Removed tasks '" + taskId + "'");
        }

        // Store the properties into disk
        properties.store(new FileOutputStream(propertiesDBName), null);
    }
  
    
    public LinkedHashMap<TRequestToken, RecallTaskTO> getAll() throws FileNotFoundException, IOException, DataAccessException {
         
        LinkedHashMap<TRequestToken, RecallTaskTO> tasksDBmem = new LinkedHashMap<TRequestToken, RecallTaskTO>();
        ArrayList<RecallTaskTO> tasksList = new ArrayList<RecallTaskTO>();
        Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesDBName));
        Collection<Object> values = properties.values();
        for (Object element : values) {
            String line = (String) element;
            RecallTaskTO task = RecallTaskBuilder.build(line);
            tasksList.add(task);
        }
        RecallTaskTO[] tasksArray = tasksList.toArray(new RecallTaskTO[tasksList.size()]);
        Arrays.sort(tasksArray);
        // Create the ordered LinkedHashMap
        for (RecallTaskTO element : tasksArray) {
            tasksDBmem.put(element.getRequestToken(), element);
        }
        return tasksDBmem;
    }

}
