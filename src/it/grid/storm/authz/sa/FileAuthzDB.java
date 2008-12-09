package it.grid.storm.authz.sa;

import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;
import java.util.StringTokenizer;
import it.grid.storm.authz.sa.model.SpaceACE;
import it.grid.storm.authz.sa.conf.*;

public class FileAuthzDB implements AuthzDBInterface {

    private AuthzDBReaderInterface authzDBReader;
    private PropertiesConfiguration authzdb;
    private int majorVersion = -1;
    private int minorVersion = -1;
    private String versionDescription;
    private String auhtzDBType;


    public FileAuthzDB() {
    }



    /**
     * getAuthzDBType
     *
     * @return String
     * @todo Implement this it.grid.storm.authz.sa.AuthzDBInterface method
     */
    public String getAuthzDBType() {
        return this.auhtzDBType;
    }

    /**
     * getMajorVersion
     *
     * @return int
     * @todo Implement this it.grid.storm.authz.sa.AuthzDBInterface method
     */
    public int getMajorVersion() {
        return majorVersion;
    }

    /**
     * getMinorVersion
     *
     * @return int
     */
    public int getMinorVersion() {
        return minorVersion;
    }

    /**
     * getMinorVersion
     *
     * @return int
     */
    public String getVersion() {
        return ""+getMajorVersion()+"."+getMinorVersion()+" - "+versionDescription;
    }

    /**
     * getOrderedListOfACE
     *
     * @return List
     * @todo Implement this it.grid.storm.authz.sa.AuthzDBInterface method
     */
    public List<SpaceACE> getOrderedListOfACE() {
        return null;
    }

    public String getVersionDescription() {
        return "";
    }

    public String getHeader() {
        return "";
    }


}
