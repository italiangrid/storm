/**
 * 
 */
package it.grid.storm.tape.recalltable.persistence;


import it.grid.storm.config.Configuration;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 *
 */
public class PropertiesDB {
  
    private static final Logger log = LoggerFactory.getLogger(PropertiesDB.class);
    private static Configuration config = Configuration.getInstance();
    private String dataFileName = "recall-table.txt";
    private String propertiesDBName;

    public PropertiesDB() {
        String configurationDir = config.getConfigurationDir();
        char sep = File.pathSeparatorChar;
        propertiesDBName = configurationDir + sep + "db" + sep + dataFileName;
        log.debug("Properties RecallTable-DB = " + propertiesDBName);
    }
    
}
