/**
 * 
 */
package it.grid.storm.tape.recalltable;


import it.grid.storm.persistence.DAOFactory;
import it.grid.storm.persistence.PersistenceDirector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author zappi
 *
 */
public class RecallTableCatalog {

    private static final Logger log = LoggerFactory.getLogger(RecallTableCatalog.class);
    private final DAOFactory daoFactory;

    /**
     * Default constructor
     */
    public RecallTableCatalog() {
        log.debug("Building RECALL TABLE Catalog ...");
        // Binding to the persistence component
        daoFactory = PersistenceDirector.getDAOFactory();

    }
    
    
}
