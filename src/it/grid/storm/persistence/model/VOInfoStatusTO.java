package it.grid.storm.persistence.model;

import it.grid.storm.info.model.VOInfoStatusData;
import it.grid.storm.persistence.PersistenceDirector;

import java.io.Serializable;

import org.slf4j.Logger;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF</p>
 *
 * @author R.Zappi
 * @version 1.0
 */

/**
 *
 * @author Riccardo Zappi - riccardo.zappi AT cnaf.infn.it
 * @version $Id  $
 */
public class VOInfoStatusTO implements Serializable, Comparable {

    private static final Logger log = PersistenceDirector.getLogger();

    private String voInfoLocalIdentifier = null;
    private long usedSpaceNearLine = -1L;
    private long availableSpaceNearLine = -1L;
    private long ReservedSpaceNearLine = -1L;
    private long usedSpaceOnLine = -1L;
    private long availableSpaceOnLine = -1L;
    private long ReservedSpaceOnLine = -1L;


    /**
     * No-arg constructor for JavaBean tools.
     */
    public VOInfoStatusTO() {
        super();
    }


    /**
     * Minimal constructor.
     *
     * @param maker User
     */
    public VOInfoStatusTO(String voInfoLocalIdentifier) {
        this.voInfoLocalIdentifier = voInfoLocalIdentifier;
    }



    /**
     * Constructor from Domain Object
     *
     * @param spaceData SpaceData
     */
    public VOInfoStatusTO(VOInfoStatusData voInfoData) {
        if (voInfoData != null) {
            log.debug("Building VOInfoStatusTO with " + voInfoData);
            if (voInfoData.getVOInfoLocalID()!=null) {
            }
        }
    }


    /**
     *
     * @param o Object
     * @return int
     */
    public int compareTo(Object o) {
        return 0;
    }

}
