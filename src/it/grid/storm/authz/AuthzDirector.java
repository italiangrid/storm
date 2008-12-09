package it.grid.storm.authz;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import java.util.Map;
import it.grid.storm.authz.sa.conf.AuthzDBReaderException;
import it.grid.storm.authz.sa.*;


public class AuthzDirector {

    private static final Log log = LogFactory.getLog("authz");
    private static Map<String,SpaceAuthzInterface> spaceAuthzs = null;
    private static Map<String,PathAuthzInterface> pathAuthzs = null;

    private static boolean initialized = false;


    public AuthzDirector() {
        super();
    }

    protected static void initializeDirector(boolean verboseMode, boolean testingMode) {
      //Search all Authz DB in path and Load them into Hashmap
    }


    protected static void addSpaceAuthz(String spaceToken,
                                      String dbFileName,
                                      boolean autoreload,
                                      int refreshTimeInSec,
                                      boolean verbose) throws AuthzDBReaderException {


        SpaceAuthzInterface spaceAuthz = new SpaceAuthz(dbFileName, autoreload, refreshTimeInSec, verbose);
        /** @todo IMPLEMENT */

        //return authzDBReader;
    }

    public static Log getLogger() {
        return log;
    }


    /**************************************************
     * PUBLIC METHODS
     */

    public static SpaceAuthzInterface getSpaceAuthz(String token) {
        /** @todo IMPLEMENT */
        return null;
    }


  public PathAuthzInterface getPathAuthz(String path) {
    return null;
  }

}
