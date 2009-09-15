package it.grid.storm.ea;

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

    public static boolean getMigrated(String fileName) {
        
        try {
            
            ea.getXAttr(fileName, EA_MIGRATED);
            
        } catch (AttributeNotFoundException e) {

            return false;

        } catch (FileNotFoundException e) {

            log.error("Cannot retrieve checksum EA because file does not exists: " + fileName, e);
            return false;

        } catch (NotSupporterdException e) {

            log.error("Cannot retrieve checksum EA (operation not supported) from file: " + fileName, e);
            return false;

        } catch (ExtendedAttributesException e) {

            log.error("Cannot retrieve checksum EA from file: " + fileName, e);
            return false;
        }
        
        return true;
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

            log.error("Cannot retrieve checksum EA because file does not exists: " + fileName, e);
            return null;

        } catch (NotSupporterdException e) {

            log.error("Cannot retrieve checksum EA (operation not supported) from file: " + fileName, e);
            return null;

        } catch (ExtendedAttributesException e) {

            log.error("Cannot retrieve checksum EA from file: " + fileName, e);
            return null;
        }

        return checksum;
    }

    public static void removePinned(String fileName) {
        try {

            ea.rmXAttr(fileName, EA_PINNED);

        } catch (FileNotFoundException e) {

            log.error("Cannot remove pinned EA because file does not exists: " + fileName, e);

        } catch (AttributeNotFoundException e) {

            // nothing to do

        } catch (NotSupporterdException e) {

            log.error("Cannot remove pinned EA (operation not supported) to file: " + fileName, e);

        } catch (ExtendedAttributesException e) {
            log.error("Cannot remove pinned EA to file: " + fileName, e);
        }
    }

    public static void setChecksum(String fileName, String checksum, String algorithm) {

        if (checksum == null) {
            return;
        }

        String chkEA = EA_CHECKSUM + algorithm.toLowerCase();

        try {
            ea.setXAttr(fileName, chkEA, checksum.getBytes());
        } catch (FileNotFoundException e) {

            log.error("Cannot set checksum EA because file does not exists: " + fileName, e);

        } catch (NotSupporterdException e) {

            log.error("Cannot set checksum EA (operation not supported) to file: " + fileName, e);

        } catch (ExtendedAttributesException e) {
            log.error("Cannot set checksum EA to file: " + fileName, e);
        }
    }

    public static void setPinned(String fileName) {

        try {
            ea.setXAttr(fileName, EA_PINNED, null);

        } catch (FileNotFoundException e) {

            log.error("Cannot set pinned EA because file does not exists: " + fileName, e);

        } catch (NotSupporterdException e) {

            log.error("Cannot set pinned EA (operation not supported) to file: " + fileName, e);

        } catch (ExtendedAttributesException e) {
            log.error("Cannot set pinned EA to file: " + fileName, e);
        }
    }

    public static void setPremigrate(String fileName) {

        try {
            ea.setXAttr(fileName, EA_PREMIGRATE, null);

        } catch (FileNotFoundException e) {

            log.error("Cannot set pre-migrate EA because file does not exists: " + fileName, e);

        } catch (NotSupporterdException e) {

            log.error("Cannot set pre-migrate EA (operation not supported) to file: " + fileName, e);

        } catch (ExtendedAttributesException e) {
            log.error("Cannot set pre-migrate EA to file: " + fileName, e);
        }
    }
}
