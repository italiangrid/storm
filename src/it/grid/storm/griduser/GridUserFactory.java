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

import java.util.Vector;
import org.apache.commons.logging.Log;
import java.util.Map;

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

    private static final Log log = GridUserManager.log;
    private String defaultMapperClassName = GridUserManager.getDefaultMapperClassName();
    private Class defaultMapperClass = null;


    private static GridUserFactory instance = null;

    protected static Log getLogger() {
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
                log.fatal("Unable to load GridUser Mapper Driver!");
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

        return user;
    }

    /**
     * Build a VOMS Grid User, if FQAN passed are not null.
     * Otherwise a simple GridUser instance wil be returned.
     *
     * @return GridUserInterface
     */
    GridUserInterface createGridUser(String distinguishName, String[] fqanString) {
        GridUserInterface user = null;

        user = new VomsGridUser(defaultMapperClass, distinguishName, distinguishName, fqanString );

        return user;
    }

    /**
     * Build a VOMS Grid User, if FQAN passed are not null.
     * Otherwise a simple GridUser instance wil be returned.
     *
     * @return GridUserInterface
     */
    GridUserInterface createGridUser(String distinguishName, String[] fqanString, String proxyString) {
        GridUserInterface user = null;

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
    GridUserInterface createGridUser(String distinguishName, String[] fqanString, String proxyString,
                                            MapperInterface userMapper) {
        GridUserInterface user = null;

        return user;
    }

    /**
     * Factory method taking a XML structure.
     *
     * <p> As no proxy certificate is passed, {@link
     * #getUserCredentials()} will return <code>null</code> for
     * objects constructed this way.
     *
     * @param inputParam The XML structure
     * @return A VomsGridUser object, encapsulating the given credentials
     * @see    #VomsGridUser(String, Fqan[], String)
     */
    GridUserInterface decode(Map inputParam) {
        // Member name for VomsGridUser Creation
        String member_DN = new String("userDN");
        String member_Fqans = new String("userFQANS");

        // Get DN and FQANs[]
        String dn = (String) inputParam.get(member_DN);

        if (dn != null) {
            log.debug("DN: " + dn);
            //Retrieve FQANs
            Vector fqans_vector = (Vector) inputParam.get(member_Fqans);

            if (fqans_vector != null) {
                //===== VOMS GRID USER =====
                // Define FQAN[]
                //Fqan[] fqans = new Fqan[fqans_vector.size()];

                log.debug("fqans_vector Size: " + fqans_vector.size());

                //Convert Vector into array of Strings
                String[] fqans = (String[])fqans_vector.toArray(new String[fqans_vector.size()]);

                /**
                 * Why convert fqan vector in an array of Fqan when the mapping works with an array of String?
                 *
                for (int i = 0; i < fqans_vector.size(); i++) {
                    String fqan_string = (String) fqans_vector.get(i);
                    log.debug("FQAN[" + i + "]:" + fqan_string);
                    // Create Fqan
                    Fqan fq = new Fqan(fqan_string);
                    // Add this into Array of Fqans
                    fqans[i] = fq;
                }
}               **/

                //Create VOMS Grid User
                log.debug("VomsGU with FQAN");
                return createGridUser(dn, fqans);
            }
            else {
                //===== Normal GRID USER =====
                return createGridUser(dn);
            }

        }
        else {
            return null;
        }
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
    private Class makeMapperClass(String mapperClassName) throws GridUserException {

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
        for (int i=0; i<intfs.length; i++) {
            if (intfs[i].equals(MapperInterface.class)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new GridUserException("Cannot load Mapper Driver instance without a valid Mapper Driver Class Name!");
        }

        return mapperClass;
    }



}
