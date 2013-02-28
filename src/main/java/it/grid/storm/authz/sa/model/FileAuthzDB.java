/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.authz.sa.model;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.sa.AuthzDBInterface;
import it.grid.storm.authz.sa.conf.AuthzDBReaderException;
import it.grid.storm.namespace.model.SAAuthzType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;


public class FileAuthzDB implements AuthzDBInterface {

    private final Logger log = AuthzDirector.getLogger();
    private final PropertiesConfiguration authzDB;

    private final String acePrefix = "ace";
    private int majorVersion = -1;
    private int minorVersion = -1;
    private String versionDescription = "Unknown";
    private SAAuthzType authzDBType = SAAuthzType.UNKNOWN;
    private List<SpaceACE> spaceACL = null;


    public FileAuthzDB(PropertiesConfiguration authzDB) throws AuthzDBReaderException {
        this.authzDB = authzDB;
        populateHeader();
        spaceACL = populateACL();
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

    public SAAuthzType getAuthzDBType() {
        return this.authzDBType;
    }

    public String getHeader() {
        return ""+getMajorVersion()+"."+getMinorVersion()+" - "+versionDescription + " ["+authzDBType+"]";
    }

    public List<SpaceACE> getOrderedListOfACE() {
        return spaceACL;
    }


    //*************** PRIVATE METHODS ***************

    private void populateHeader() {
        this.authzDBType = SAAuthzType.getSAType(authzDB.getString("Type"));
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

    private List<SpaceACE> populateACL() {
        spaceACL = new ArrayList<SpaceACE>();
        Iterator<String> scanKeys = authzDB.getKeys(acePrefix);
        while (scanKeys.hasNext()) {
            String key = scanKeys.next();
            String value = authzDB.getString(key);
            log.debug("KEY:"+key + " VALUE:"+value);
            /** @todo IMPLEMENT PARSING OF VALUE */

        }
        /**
         * @todo Add the default ACL
         */
        return spaceACL;
    }



}
