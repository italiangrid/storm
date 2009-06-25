/*
 * You may copy, distribute and modify this file under the terms of
 * the INFN GRID licence.
 * For a copy of the licence please visit
 *
 *    http://www.cnaf.infn.it/license.html
 *
 * Riccardo Zappi <riccardo.zappi@cnaf.infn.it.it>, 2007
 * $Id: GridUserFactory.java 3604 2007-05-22 11:16:27Z rzappi $
 *
 */

package it.grid.storm.griduser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
public class GridUserFactory {

    private static final Logger log = GridUserManager.log;
    private String defaultMapperClassName = GridUserManager.getMapperClassName();
    private MapperInterface defaultMapperClass = null;


    private static GridUserFactory instance = null;

    protected static Logger getLogger() {
        return log;
    }

    private GridUserFactory() throws GridUserException {
        super();
        instance = this;
        defaultMapperClass= makeMapperClass(defaultMapperClassName);
    }

    static GridUserFactory getInstance() {
        if (instance == null) {
            try {
                instance = new GridUserFactory();
            }
            catch (GridUserException ex) {
                log.error("Unable to load GridUser Mapper Driver!",ex);
            }
        }
        return instance;
    }

    /**
     * Used to modify the Mapper used to retrieve the Local User
     * The Mapper setted will be referenceable by any GridUser built.
     *
     * @param mapper MapperInterface
     */
    void setUserMapper(String mapperClassName) throws GridUserException {
        this.defaultMapperClassName = mapperClassName;
        defaultMapperClass= makeMapperClass(defaultMapperClassName);
    }

    /**
     * Build a simple GridUser. No VOMS attributes are passed..
     *
     * @return GridUserInterface
     */
    public GridUserInterface createGridUser(String distinguishName) {
        GridUser user = null;

        user = new GridUser(defaultMapperClass, distinguishName);
        log.debug("Created new Grid User (NO VOMS) : "+user);
        return user;
    }

    /**
     * Build a simple GridUser. Parsing of proxy is not performed here!
     * This methos is meaningful only for srmCopy call.
     *
     * @return GridUserInterface
     */
    GridUserInterface createGridUser(String distinguishName, String proxyString) {
        GridUser user = null;

        user = new GridUser(defaultMapperClass, distinguishName);
        user.setProxyString(proxyString);

        log.debug("Created new Grid User (NO VOMS con PROXY) : "+user);
        return user;
    }

    /**
     * Build a VOMS Grid User, if FQAN passed are not null.
     * Otherwise a simple GridUser instance wil be returned.
     *
     * @return GridUserInterface
     */
    GridUserInterface createGridUser(String distinguishName, FQAN[] fqans) {
        GridUserInterface user = null;
        user = new VomsGridUser(defaultMapperClass, distinguishName, null, fqans );
        log.debug("Created new Grid User (VOMS USER) : "+user);
        return user;
    }

    /**
     * Build a VOMS Grid User, if FQAN passed are not null.
     * Otherwise a simple GridUser instance wil be returned.
     *
     * @return GridUserInterface
     */
    GridUserInterface createGridUser(String distinguishName, FQAN[] fqans, String proxyString) {
        GridUserInterface user = null;
        user = new VomsGridUser(defaultMapperClass, distinguishName, proxyString, fqans );
        log.debug("Created new Grid User (VOMS USER) : "+user);
        return user;
    }

    /**
     * Special method used to build a GridUser with a specific Mapper without change
     * the behaviour of this factory with default Mapper. To change the behaviour of this factory use
     * setUserMapper method instead.
     *
     * Build a VOMS Grid User, if FQAN passed are not null.
     * Otherwise a simple GridUser instance wil be returned.
     *
     *
     * @return GridUserInterface
     */
    GridUserInterface createGridUser(String distinguishName, FQAN[] fqans, String proxyString, MapperInterface userMapper) {
        GridUserInterface user = null;
        log.debug("**** NULL METHOD **** Created new Grid User (VOMS USER) : "+user);
        return user;
    }

    public GridUserInterface decode(Map inputParam)
    {
        // Member name for VomsGridUser Creation
        String member_DN = new String("userDN");
        String member_Fqans = new String("userFQANS");

        // Get DN and FQANs[]
        String dnString = (String) inputParam.get(member_DN);

        List fqansList = null;
        try {
            fqansList = Arrays.asList( (Object[]) inputParam.get(member_Fqans));
        }
        catch (NullPointerException e) {
            log.debug("Empty FQAN[] found."+e);
        }

        // Destination Fqans array
        FQAN[] fqans = null;

        if (fqansList != null) {
            // Define FQAN[]
            fqans = new FQAN[fqansList.size()];
            log.debug("fqans_vector Size: " + fqansList.size());

            for (int i=0; i<fqansList.size(); i++) {
                String fqan_string = (String) fqansList.get(i);
                log.debug("FQAN[" + i + "]:" + fqan_string);
                // Create Fqan
                FQAN fq = new FQAN(fqan_string);
                // Add this into Array of Fqans
                fqans[i] = fq;
            }
        }

        // Setting up VomsGridUser
        if (dnString != null) {
            log.debug("DN: " + dnString);
            // Creation of srm GridUser type
            if (fqansList != null) {
                log.debug("VomsGU with FQAN");
                return createGridUser(dnString, fqans);
            } else {
                return createGridUser(dnString);
            }
        }
        return null;
    }



    /**
     * Method used by Grid User (or Voms Grid User) to instatiate the mapper.
     *
     * @param mapperClass Class
     * @return MapperInterface
     * @throws GridUserException
     */
    MapperInterface makeMapperInstance(Class mapperClass) throws CannotMapUserException {

        MapperInterface mapperInstance = null;

        if (mapperClass == null) {
            throw new CannotMapUserException("Cannot build Mapper Driver instance without a valid Mapper Driver Class!");
        }

        try {
            mapperInstance = (MapperInterface) mapperClass.newInstance();
        }
        catch (IllegalAccessException ex) {
            log.error("Unable to instantiate the Mapper Driver. Illegal Access.", ex);
            throw new CannotMapUserException("Unable to instantiate the Mapper Driver. Illegal Access.", ex);
        }
        catch (InstantiationException ex) {
            log.error("Unable to instantiate the Mapper Driver. Generic problem..", ex);
            throw new CannotMapUserException("Unable to instantiate the Mapper Driver. Generic problem..", ex);
        }

        return mapperInstance;
    }


    /**
     * PRIVATE METHODs
     */

    /**
     *
     * Mapper Factory
     *
     * @param mapperClassName String
     * @return Class
     * @throws GridUserException
     */
    private MapperInterface makeMapperClass(String mapperClassName) throws GridUserException {

        MapperInterface mapper = null;
        Class mapperClass = null;
        if (mapperClassName == null) {
            throw new GridUserException("Cannot load Mapper Driver without a valid Mapper Driver Class Name!");
        }

        // Retrieve the Class of driver
        try {
            mapperClass = Class.forName(mapperClassName);
        }
        catch (ClassNotFoundException e) {
            throw new GridUserException("Cannot load Mapper Driver instance without a valid Mapper Driver Class Name!", e);
        }

        //Check if the Class implements the right interface
        Class[] intfs = mapperClass.getInterfaces();
        boolean found = false;
        for (Class intf : intfs) {
            if (intf.equals(MapperInterface.class)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new GridUserException("Cannot load Mapper Driver instance without a valid Mapper Driver Class Name!");
        }
        try {
            mapper = (MapperInterface) mapperClass.newInstance();
        }
        catch (IllegalAccessException ex) {
            log.error("makeMapperClass EXCEPTION. "+ex);
            throw new GridUserException("Cannot create a new Instance of the Mapper Driver named :'"+mapperClassName+"'");
        }
        catch (InstantiationException ex) {
            log.error("makeMapperClass EXCEPTION. "+ex);
            throw new GridUserException("Cannot create a new Instance of the Mapper Driver named :'"+mapperClassName+"'");
        }
        return mapper; //mapperClass;
    }



}
