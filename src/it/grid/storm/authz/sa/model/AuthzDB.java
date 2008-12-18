package it.grid.storm.authz.sa.model;

import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.*;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.sa.AuthzDBInterface;


public class AuthzDB implements AuthzDBInterface {

   private final Log log = AuthzDirector.getLogger();
   private PropertiesConfiguration authzDB;

   private final String acePrefix = "ace";
   private int majorVersion = -1;
   private int minorVersion = -1;
   private String versionDescription = "Unknown";
   private String authzDBType = "Unknown";
   private List<SpaceACE> spaceACL = null;


    public AuthzDB(PropertiesConfiguration authzDB) {
        this.authzDB = authzDB;
        populateHeader();
    }

    public int getMajorVersion() {
        return this.majorVersion;
    }

    public int getMinorVersion() {
        return this.minorVersion;
    }

    public String getVersionDescription() {
        return this.versionDescription;
    }

    public String getAuthzDBType() {
        return this.authzDBType;
    }

    public String getHeader() {
        return ""+getMajorVersion()+"."+getMinorVersion()+" - "+versionDescription + " ["+authzDBType+"]";
    }

    public List<SpaceACE> getOrderedListOfACE() {
        return null;
    }



    //*************** PRIVATE METHODS ***************

     private void populateHeader() {
         this.authzDBType = authzDB.getString("Type");
         String[] version = authzDB.getStringArray("Version");
         if (version != null) {
             String versionNr = version[0];
             StringTokenizer versionsNr = new StringTokenizer(versionNr, ".", false);
             if (versionsNr.countTokens() > 0) {
                 this.majorVersion = Integer.parseInt(versionsNr.nextToken());
                 this.minorVersion = Integer.parseInt(versionsNr.nextToken());
             }
             if (version.length > 1) {
                 this.versionDescription = version[1];
             }
         }
     }

     private void populateACL() {
        Iterator scanKeys = authzDB.getKeys(acePrefix);
        while (scanKeys.hasNext()) {
            String key = (String) scanKeys.next();
            String value = (String) authzDB.getString(key);
            log.debug("KEY:"+key + " VALUE:"+value);
            /** @todo IMPLEMENT PARSING OF VALUE */

        }
     }

     private SpaceACE getSpaceACE(String value) {
         SpaceACE spaceACE = new SpaceACE();
         return spaceACE;
     }


}
