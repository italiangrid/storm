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

/**
 * 
 */
package it.grid.storm.tape.recalltable.model;

import it.grid.storm.griduser.AbstractGridUser;
import it.grid.storm.griduser.DistinguishedName;
import it.grid.storm.griduser.FQAN;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.tape.recalltable.TapeRecallException;
import it.grid.storm.tape.recalltable.persistence.TapeRecallBuilder;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 * 
 *         This class is the representation of a Recall Task data. Only
 *         user-settable data are writable. The rest data are only read.
 * 
 * 
 *         { "stfn":"<file-name>", "dn":"<DN>", "fqans":["fqan":"<FQAN>",
 *         "fqan":"<FQAN>"], "vo-name":"<vo-name>" }
 * 
 * 
 */
public class TapeRecallData {

    public static String ANONYMOUS_USER = "anonymous";
    
    private static final Logger log = LoggerFactory.getLogger(TapeRecallBuilder.class);
    // Constants
    public static String UNSPECIFIED_FN = "filename N/A";
    public static String UNSPECIFIED_VO = "unspecified-VO";

    // Main Attributes
    private String fileName = null;
    private String userId = null;
    private String voName = null;

    // Sub attributes
    private String userDN = null;
    private String[] fqansString = null;
    private ArrayList<FQAN> fqans = null;

    /**
     * Static Builder
     * 
     * @param inputString
     * @return
     * @throws TapeRecallException
     */
    public static TapeRecallData buildFromString(String inputString) throws TapeRecallException {
            // Check the string as Properties file
        TapeRecallData result = new TapeRecallData();
            // Retrieve from input String the substring encapsuling the keys-values
            // set.
            int beginArray = inputString.indexOf(TapeRecallBuilder.taskStart);
            int endArray = inputString.indexOf(TapeRecallBuilder.taskEnd);
            if ((beginArray >= 0) && (endArray > 0) && (beginArray < endArray)) {
                String parameters = inputString.substring(beginArray + 1, endArray);
                String[] paramArray = parameters.split(TapeRecallBuilder.elementSep);
            for (int i = 0; i < paramArray.length; i++) {
                log.debug("param[" + i + "]=" + paramArray[i]);
                if (paramArray[i].contains(TapeRecallBuilder.equalChar)) {
                    int equalIndex = paramArray[i].indexOf(TapeRecallBuilder.equalChar);
                    String key = paramArray[i].substring(0, equalIndex);
                    String value = paramArray[i].substring(equalIndex + 1, paramArray[i].length());
                    log.debug("KEY:" + key + " VALUE:" + value);
                    //Scan for known keys.
                    if (key.equals(TapeRecallBuilder.fnPrefix)) {
                      result.fileName = value;  
                    } else if (key.equals(TapeRecallBuilder.userIdPrefix)) {
                      result.userId = value;
                    } else if (key.equals(TapeRecallBuilder.voNamePrefix)) {
                      result.voName = value;  
                    } else if (key.equals(TapeRecallBuilder.fqansPrefix)) {
                      result.fqans = result.parseFQANs(value, 0, 0, 0, value.length());  
                    } else {
                        log.warn("Unknown key-value pair (" + key + "," + value + "). StoRM will ignore them.");
                    }
                }
            }
            } else {
                throw new TapeRecallException("Unable to understand :'" + inputString + "'");
            }
            return result;
        }

    
    /**
     * Empty RecallTaskData
     */
    public TapeRecallData() {
        fileName = UNSPECIFIED_FN;
        userId = ANONYMOUS_USER;
        voName = UNSPECIFIED_VO;
    }
    
    /**
     * 
     * 
     * { "filename":"<file-name>"; "dn":"<DN>"; "fqans":["fqan":"<FQAN>",
     * "fqan":"<FQAN>"]; "vo-name":"<vo-name>" }
     * 
     * 
     * @param textFormat
     * @throws TapeRecallException
     */
    public TapeRecallData(String textFormat) throws TapeRecallException {
        //Parsing of the String
        if (textFormat.startsWith(TapeRecallBuilder.taskStart)) {
            
            if (textFormat.contains(TapeRecallBuilder.taskEnd)) {
                int beginIndex = 1;
                int endIndex = textFormat.indexOf(TapeRecallBuilder.taskEnd);
                String taskDataText = textFormat.substring(beginIndex, endIndex);
                int fnPos = taskDataText.indexOf(TapeRecallBuilder.fnPrefix);
                if (fnPos < 0) {
                    throw new TapeRecallException("Unable to find the File Name");
                } else {
                    // Parsing the elements
                    int dnPos = taskDataText.indexOf(TapeRecallBuilder.dnPrefix);
                    if (dnPos < 0) {
                        log.warn("Task Data without User DN");
                    }
                    int fqansPos = taskDataText.indexOf(TapeRecallBuilder.fqansPrefix);
                    if (fqansPos < 1) {
                        log.warn("Task Data without User FQANs");
                    }
                    int voNamePos = taskDataText.indexOf(TapeRecallBuilder.voNamePrefix);
                    if (voNamePos < 1) {
                        log.warn("Task Data without VO Name");
                    }
                    fileName = parseFN(taskDataText, fnPos, dnPos, fqansPos, voNamePos);
                    userDN = parseDN(taskDataText, fnPos, dnPos, fqansPos, voNamePos);
                    fqans = parseFQANs(taskDataText, fnPos, dnPos, fqansPos, voNamePos);
                    voName = parseVOName(taskDataText, fnPos, dnPos, fqansPos, voNamePos);
                    log.debug("filename='" + fileName + "'");
                    log.debug("userDN  ='" + userDN + "'");
                    log.debug("fqans='" + fqans + "'");
                    log.debug("voName='" + voName + "'");
                }
            } else {
                throw new TapeRecallException("Unable to find the File Name");
            }
        }
    }


    /**
     * Constructor with x.509 certificate
     * 
     * @param filename
     * @param user
     */
    public TapeRecallData(String filename, GridUserInterface user) {
        fileName = filename;
        if (user instanceof AbstractGridUser && ((AbstractGridUser)user).hasVoms()) {
            AbstractGridUser gridUser = (AbstractGridUser) user;
            fqans = new ArrayList<FQAN>(Arrays.asList(gridUser.getFQANs()));
            fqansString = gridUser.getFQANsAsString();
            voName = gridUser.getVO().getValue();
        } else {
            voName = UNSPECIFIED_VO;
        }
        userDN = user.getDistinguishedName().getDN();
    }
    
    
    /**
     * 
     * @param filename
     * @param dn
     * @param fqans
     * @param voName
     */
    public TapeRecallData(String filename, String dn, String[] fqans, String voName) {
        fileName = filename;
        DistinguishedName dn500 = (new DistinguishedName(dn));
        userDN = dn500.getX500DN_rfc2253();
        fqansString = fqans;
        this.voName = voName;
    }

    
    /**
     * 
     * @return
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * 
     * @return
     */
    public String getUserID() {
        if (userId.equals(ANONYMOUS_USER)) {
            // Try to build an userId
            if (getUserDN() != null) {
                userId = getUserDN();
                if (getFqansTextFormat() != null) {
                    userId += " - " + getFqansTextFormat();
                }
            }
        }
        return userId; 
    }

    /**
     * 
     * @return
     */
    public String getVoName() {
        return voName;
    }  

    /**
     * 
     */
    public String getRecallTaskData_textFormat() {
        String result = "";
        result += TapeRecallBuilder.taskStart;
        result += getFileNameTextFormat() + TapeRecallBuilder.elementSep;
        result += getUserDNTextFormat() + TapeRecallBuilder.elementSep;
        result += getFqansTextFormat() + TapeRecallBuilder.elementSep;
        result += getVONameTextFormat();
        result += TapeRecallBuilder.taskEnd;
        return result;
    }

    /**
     * 
     * @return
     */
    private String getUserDN() {
        return userDN;
    }

    /**
     * 
     * @return
     */
    private String getVONameTextFormat() {
        String result = "";
        result += TapeRecallBuilder.voNamePrefix;
        result += getVoName();
        return result;
    }
    
    /**
     * @param taskDataText
     * @param fnPos
     * @param dnPos
     * @param fqansPos
     * @param voNamePos
     * @return
     */
    private String parseDN(String taskDataText, int fnPos, int dnPos, int fqansPos, int voNamePos) {
        String result = null;
        if (dnPos > 0) {
            if (fqansPos > 0) {
                result = taskDataText.substring(dnPos, fqansPos - 1);
                log.debug("dn to parse (dnPos=" + dnPos + " fqansPos=" + fqansPos + ": " + result);
            } else if (voNamePos > 0) {
                result = taskDataText.substring(dnPos, voNamePos - 1);
            } else {
                result = taskDataText.substring(dnPos);
            }
        } else {
            result = "no-DN";
        }
        // Trim out the prefix and the separator (if there)
        int pos = result.indexOf(TapeRecallBuilder.elementSep);
        pos = pos < 0 ? result.length() : pos;
        result = result.substring(TapeRecallBuilder.dnPrefix.length(), pos);
        log.debug("DN to parse ='" + result + "'");
        
        log.warn("## TODO Feature ## : Bug in parsing DN in RFC 2253 format.");
        /**
         * ############### TEMPORARY FIX
         */
        // DistinguishedName dn = new DistinguishedName(result);
        // result = dn.getX500DN_rfc1779();
        if (result.contains("CN=")) {
            int beginIndex = result.indexOf("CN=") + 3;
            result = result.substring(beginIndex);
            int endIndex = result.contains(",") ? result.indexOf(',') : result.length();
            result = result.substring(0, endIndex);
            endIndex = result.contains("/") ? result.indexOf('/') : result.length();
            result = result.substring(0, endIndex);
        }
        /**
         * ################# END TEMPORARY FIX
         */
        
        log.debug("DN parsed ='" + result + "'");
        return result;
    }

    /**
     * @param taskDataText
     * @param fnPos
     * @param dnPos
     * @param fqansPos
     * @param voNamePos
     * @return
     */
    private String parseFN(String taskDataText, int fnPos, int dnPos, int fqansPos, int voNamePos) {
        String result = null;
        if (dnPos > 0) {
            result = taskDataText.substring(fnPos, dnPos);
        } else if (fqansPos > 0) {
            result = taskDataText.substring(fnPos, fqansPos);
        } else if (voNamePos > 0) {
            result = taskDataText.substring(fnPos, voNamePos);
        } else {
            result = taskDataText.substring(fnPos);
        }
        log.debug("FN to parse ='" + result + "'");
        int pos = result.indexOf(TapeRecallBuilder.elementSep);
        pos = pos < 0 ? result.length() : pos;
        result = result.substring(TapeRecallBuilder.fnPrefix.length(), pos);
        log.debug("FN parsed ='" + result + "'");
        return result;
    }

    /**
     * @param taskDataText
     * @param fnPos
     * @param dnPos
     * @param fqansPos
     * @param voNamePos
     * @return
     */
    private ArrayList<FQAN> parseFQANs(String taskDataText, int fnPos, int dnPos, int fqansPos, int voNamePos) {
        ArrayList<FQAN> result = new ArrayList<FQAN>();
        String fqansSt = null;
        if (fqansPos > 0) {
            if (voNamePos > 0) {
                fqansSt = taskDataText.substring(fqansPos, voNamePos);
            } else {
                fqansSt = taskDataText.substring(fqansPos);
            }
            
            // Trim out the prefix and the separator (if there)
            int pos = fqansSt.indexOf(TapeRecallBuilder.elementSep);
            pos = pos < 0 ? fqansSt.length() : pos;
            fqansSt = fqansSt.substring(TapeRecallBuilder.fqansPrefix.length(), pos);
            log.debug(fqansSt);
            
            pos = fqansSt.indexOf(TapeRecallBuilder.fqansArrayEnd);
            fqansSt = fqansSt.substring(TapeRecallBuilder.fqansArrayStart.length(), pos);
            log.debug(fqansSt);
            
            String[] fqans = fqansSt.split(TapeRecallBuilder.fqanSep);
            fqansString = new String[fqans.length]; 
            FQAN fqan;
            int count = 0;
            for (String fqanIter : fqans) {
                log.debug("fqanString = '" + fqanIter + "'");
                // Trim out the prefix and the separator (if there)
                pos = fqansSt.indexOf(TapeRecallBuilder.fqanSep);
                pos = pos < 0 ? fqansSt.length() : pos;
                fqanIter = fqanIter.substring(TapeRecallBuilder.fqanPrefix.length(), pos);
                log.debug("fqanString (purged) = '" + fqanIter + "'");
                fqan = new FQAN(fqanIter);
                result.add(fqan);
                fqansString[count] = fqan.toString();
                count++;
            }
        } else {
            log.debug("no FQANs are present into RecallTask Data.");
        }
        return result;
    }

    /**
     * @param taskDataText
     * @param fnPos
     * @param dnPos
     * @param fqansPos
     * @param voNamePos
     * @return
     */
    private String parseVOName(String taskDataText, int fnPos, int dnPos, int fqansPos, int voNamePos) {
        String result = null;
        if (voNamePos > 0) {
            result = taskDataText.substring(voNamePos);
            log.debug("vo-name to parse ='" + result + "'");
            int pos = result.indexOf(TapeRecallBuilder.elementSep);
            pos = pos < 0 ? result.length() : pos;
            result = result.substring(TapeRecallBuilder.voNamePrefix.length(), pos);
        } else {
            if (fqans != null) {
                FQAN fqan = fqans.get(0);
                result = fqan.getVo();
            } else {
                result = "no-voname";
            }
        }
        
        log.debug("vo-name parsed ='" + result + "'");
        return result;
    }

    /**
     * 
     * @return
     */
    private String getFileNameTextFormat() {
        String result = "";
        result += TapeRecallBuilder.fnPrefix;
        result += getFileName();
        return result;
    }

    /**
     * 
     * @return
     */
    private String[] getFqans() {

        if ((fqans != null) && (fqans.size() > 0)) {
            fqansString = new String[fqans.size()];
            int count = 0;
            for (FQAN fqan : fqans) {
                fqansString[count] = fqan.toString();
                count++;
            }
        }
        return fqansString;
    }

    /**
     * 
     * @return
     */
    private String getFqansTextFormat()
    {
        String result = "";
        int fqansNum = (fqansString != null ? fqansString.length : 0);
        result += TapeRecallBuilder.fqansPrefix;
        result += TapeRecallBuilder.fqansArrayStart;
        for (int i = 0; i < fqansNum; i++)
        {
            result += fqansString[i];
            if (i < (fqansNum - 1))
            {
                result += TapeRecallBuilder.fqanSep;
            }
        }
        result += TapeRecallBuilder.fqansArrayEnd;
        return result;
    }

    /**
     * 
     * @return
     */
    private String getUserDNTextFormat() {
        String result = "";
        result += TapeRecallBuilder.dnPrefix;
        result += getUserDN();
        return result;
    }
    
    public String toString() {
        String result = "";
        result += TapeRecallBuilder.fnPrefix + TapeRecallBuilder.equalChar + fileName + "\t";
        result += TapeRecallBuilder.userIdPrefix + TapeRecallBuilder.equalChar + userId + "\t";
        result += TapeRecallBuilder.voNamePrefix + TapeRecallBuilder.equalChar + voName;
        return result;
    }
}
