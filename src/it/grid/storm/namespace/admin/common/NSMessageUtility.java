package it.grid.storm.namespace.admin.common;

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
public class NSMessageUtility {

    public static final char NUL = 0x00; //Null
    public static final char SOH = 0x01; //Start of Heading
    public static final char STX = 0x02; //Start of message
    public static final char ETX = 0x03; //End of message
    public static final char EOT = 0x04; //End of trasmission
    public static final char ACK = 0x06; //Acknoledge
    public static final char NAK = 0x15; //Not acknoledge
    public static final String newline = System.getProperty("line.separator");

    public NSMessageUtility() {
    }
}
