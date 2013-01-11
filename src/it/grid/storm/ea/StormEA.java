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

package it.grid.storm.ea;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.grid.storm.checksum.ChecksumAlgorithm;
import it.grid.storm.jna.ExtendedAttributesImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StormEA {

    private static final Logger log = LoggerFactory.getLogger(StormEA.class);

    private static final String EA_PINNED = "user.storm.pinned";
    private static final String EA_PREMIGRATE = "user.storm.premigrate";
    private static final String EA_CHECKSUM = "user.storm.checksum.";
    private static final String EA_MIGRATED = "user.storm.migrated";

    private static final ExtendedAttributes ea = new ExtendedAttributesImpl();

    public static Map<String,String> getChecksums(String filename) {
        HashMap<String,String> result = new HashMap<String, String>();
        for (ChecksumAlgorithm checksumAlgorithm : ChecksumAlgorithm.values()) {
            String cksm = getChecksum(filename, checksumAlgorithm.toString());
            if (cksm!=null) {
                result.put(checksumAlgorithm.toString(),cksm);
            }
        }
        return result;
    }
    
    
    public static String getChecksum(String fileName, String algorithm) {

        String checksum = null;
        String chkEA = EA_CHECKSUM + algorithm.toLowerCase();

        try {

            byte[] byteArray = ea.getXAttr(fileName, chkEA);

            checksum = new String(byteArray);

        } catch (AttributeNotFoundException e) {

            return null;

        } catch (FileNotFoundException e) {

            log.warn("Cannot retrieve checksum EA because file does not exists: " + fileName);
            return null;

        } catch (NotSupportedException e) {

            log.warn("Cannot retrieve checksum EA (operation not supported) from file: " + fileName);
            return null;

        } catch (ExtendedAttributesException e) {

            log.warn("Cannot retrieve checksum EA from file: " + fileName);
            return null;
        }

        return checksum;
    }
    

    public static boolean getMigrated(String fileName) {

        try {

            ea.getXAttr(fileName, EA_MIGRATED);

        } catch (AttributeNotFoundException e) {

            return false;

        } catch (FileNotFoundException e) {

            log.warn("Cannot retrieve checksum EA because file does not exists: " + fileName);
            return false;

        } catch (NotSupportedException e) {

            log.warn("Cannot retrieve checksum EA (operation not supported) from file: " + fileName);
            return false;

        } catch (ExtendedAttributesException e) {

            log.warn("Cannot retrieve checksum EA from file: " + fileName);
            return false;
        }

        return true;
    }

    public static long getPinned(String fileName) {
        String longString = null;
        try {
            byte[] byteArray = ea.getXAttr(fileName, EA_PINNED);

            if (byteArray == null) {
                return -1;
            }

            longString = new String(byteArray);
            if (longString != null) {
                log.debug("Retrieved PinLifeTime with value: '" + longString + "' (length:"
                        + longString.length() + ")");
            } 
            return Long.decode(longString);

        } catch (FileNotFoundException e) {
            log.warn("Cannot retrieve pinned EA because file does not exists: " + fileName);
        } catch (NotSupportedException e) {
            log.warn("Cannot retrieve pinned EA (operation not supported) to file: " + fileName);
        } catch (AttributeNotFoundException e) {
            log.debug("Cannot retrieve pinned (file is not pinned) EA to file: '" + fileName);
        } catch (NumberFormatException e) {
            log.warn("Value of pinned EA is not a number (found '" + longString + "'), assuming -1. File: "
                    + fileName);
        } catch (ExtendedAttributesException e) {
            log.warn("Cannot retrieve pinned EA (" + e.getMessage() + ")to file: '" + fileName);
        }

        return -1;
    }

    public static void removeChecksum(String fileName) {
        try {

            ea.rmXAttr(fileName, EA_CHECKSUM);

        } catch (FileNotFoundException e) {

            log.warn("Cannot remove '"+EA_CHECKSUM+"' EA because file does not exists: " + fileName);

        } catch (AttributeNotFoundException e) {

            log.info("Cannot remove '"+EA_CHECKSUM+"' EA because unable to find the attribute (Maybe already removed): " + fileName);

        } catch (NotSupportedException e) {

            log.warn("Cannot remove '"+EA_CHECKSUM+"' EA (operation not supported) to file: " + fileName);

        } catch (ExtendedAttributesException e) {
            log.warn("Cannot remove '"+EA_CHECKSUM+"' EA to file: " + fileName);
        }
    }

    public static void removePinned(String fileName) {
        try {

            ea.rmXAttr(fileName, EA_PINNED);

        } catch (FileNotFoundException e) {

            log.warn("Cannot remove '"+EA_PINNED+"' EA because file does not exists: " + fileName);

        } catch (AttributeNotFoundException e) {

            log.info("Cannot remove '"+EA_PINNED+"' EA because unable to find the attribute (Maybe already removed): " + fileName);

        } catch (NotSupportedException e) {

            log.warn("Cannot remove '"+EA_PINNED+"' EA (operation not supported) to file: " + fileName);

        } catch (ExtendedAttributesException e) {
            log.warn("Cannot remove '"+EA_PINNED+"' EA to file: " + fileName);
        }
    }

    public static void setChecksum(String fileName, String checksum, String algorithm)
            throws FileNotFoundException, NotSupportedException, ExtendedAttributesException
    {
        ea.setXAttr(fileName, EA_CHECKSUM + algorithm.toLowerCase(), checksum.getBytes());
    }

    /**
     * Set the Extended Attribute "pinned" ({@value StormEA#EA_PINNED}) to the given value.
     * 
     * @param fileName
     * @param expirationDateInSEC expiration time of the pin expressed as "seconds since the epoch".
     */
    public static void setPinned(String fileName, long expirationDateInSEC) {

        long existingPinValueInSEC = getPinned(fileName);

        Format formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

        if (existingPinValueInSEC >= expirationDateInSEC) {
            log.debug("The file '"
                    + fileName
                    + "' is already Pinned and the pre-existing PinLifeTime is greater than the new one. Nothing is changed in EA. Expiration: "
                    + formatter.format(new Date(existingPinValueInSEC * 1000)));
            return;
        }

        String longString = String.valueOf(expirationDateInSEC);

        try {
            ea.setXAttr(fileName, EA_PINNED, longString.getBytes());

            if (log.isDebugEnabled()) {
                if (existingPinValueInSEC == -1) {
                    log.debug("Added the Pinned EA to '" + fileName + "' with expiration: "
                            + formatter.format(new Date(existingPinValueInSEC * 1000)));
                } else {
                    log.debug("Updated the Pinned EA to '" + fileName + "' with expiration: "
                            + formatter.format(new Date(existingPinValueInSEC * 1000)));
                }
            }

        } catch (FileNotFoundException e) {

            log.warn("Cannot set pinned EA because file does not exists: " + fileName);

        } catch (NotSupportedException e) {

            log.warn("Cannot set pinned EA (operation not supported) to file: " + fileName);

        } catch (ExtendedAttributesException e) {
            log.warn("Cannot set pinned EA to file: " + fileName);
        }
    }

    public static void setPremigrate(String fileName) {

        try {
            ea.setXAttr(fileName, EA_PREMIGRATE, null);

        } catch (FileNotFoundException e) {

            log.warn("Cannot set pre-migrate EA because file does not exists: " + fileName);

        } catch (NotSupportedException e) {

            log.warn("Cannot set pre-migrate EA (operation not supported) to file: " + fileName);

        } catch (ExtendedAttributesException e) {
            log.warn("Cannot set pre-migrate EA to file: " + fileName);
        }
    }

    /**
     * @param absoluteFileName
     * @return boolean: true if the file is pinned, false else.
     */
    public static boolean isPinned(String absoluteFileName) {
        boolean result = false;
        try {
            ea.getXAttr(absoluteFileName, EA_PINNED);
            result = true;
        } catch (FileNotFoundException e) {
            log.warn("Cannot check pinned EA because file does not exists: " + absoluteFileName);
        } catch (NotSupportedException e) {
            log.warn("Cannot check pinned EA (operation not supported) to file: " + absoluteFileName);
        } catch (AttributeNotFoundException e) {
            log.debug("Pinned EA is not attached to file: " + absoluteFileName);
            result = false;
        } catch (ExtendedAttributesException e) {
            log.warn("Cannot check pinned EA (" + e.getMessage() + ")to file: '" + absoluteFileName);
        }
        return result;
    }
}
