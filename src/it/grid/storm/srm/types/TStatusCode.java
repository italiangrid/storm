/**
 * This class represents the TStatusCode of TReturnStatus
 *
 * @author  Magnoni Luca
 * @author  CNAF - INFN  Bologna
 * @date    Avril, 2005
 * @version 1.0
 */

package it.grid.storm.srm.types;

import java.io.Serializable;

public class TStatusCode implements Serializable {

    private static final long serialVersionUID = -1228182137951212768L;
    private String status = null;
    private static java.util.HashMap<String, TStatusCode> _table_ = new java.util.HashMap<String, TStatusCode>();

    public static final TStatusCode EMPTY = new TStatusCode("EMPTY");
    public static final TStatusCode SRM_SUCCESS = new TStatusCode("SRM_SUCCESS");
    public static final TStatusCode SRM_FAILURE = new TStatusCode("SRM_FAILURE");
    public static final TStatusCode SRM_AUTHENTICATION_FAILURE = new TStatusCode("SRM_AUTHENTICATION_FAILURE");
    public static final TStatusCode SRM_AUTHORIZATION_FAILURE = new TStatusCode("SRM_AUTHORIZATION_FAILURE");
    public static final TStatusCode SRM_INVALID_REQUEST = new TStatusCode("SRM_INVALID_REQUEST");
    public static final TStatusCode SRM_INVALID_PATH = new TStatusCode("SRM_INVALID_PATH");
    public static final TStatusCode SRM_FILE_LIFETIME_EXPIRED = new TStatusCode("SRM_FILE_LIFETIME_EXPIRED");
    public static final TStatusCode SRM_SPACE_LIFETIME_EXPIRED = new TStatusCode("SRM_SPACE_LIFETIME_EXPIRED");
    public static final TStatusCode SRM_EXCEED_ALLOCATION = new TStatusCode("SRM_EXCEED_ALLOCATION");
    public static final TStatusCode SRM_NO_USER_SPACE = new TStatusCode("SRM_NO_USER_SPACE");
    public static final TStatusCode SRM_NO_FREE_SPACE = new TStatusCode("SRM_NO_FREE_SPACE");
    public static final TStatusCode SRM_DUPLICATION_ERROR = new TStatusCode("SRM_DUPLICATION_ERROR");
    public static final TStatusCode SRM_NON_EMPTY_DIRECTORY = new TStatusCode("SRM_NON_EMPTY_DIRECTORY");
    public static final TStatusCode SRM_TOO_MANY_RESULTS = new TStatusCode("SRM_TOO_MANY_RESULTS");
    public static final TStatusCode SRM_INTERNAL_ERROR = new TStatusCode("SRM_INTERNAL_ERROR");
    public static final TStatusCode SRM_FATAL_INTERNAL_ERROR = new TStatusCode("SRM_FATAL_INTERNAL_ERROR");
    public static final TStatusCode SRM_NOT_SUPPORTED = new TStatusCode("SRM_NOT_SUPPORTED");
    public static final TStatusCode SRM_REQUEST_QUEUED = new TStatusCode("SRM_REQUEST_QUEUED");
    public static final TStatusCode SRM_REQUEST_INPROGRESS = new TStatusCode("SRM_REQUEST_INPROGRESS");
    public static final TStatusCode SRM_REQUEST_SUSPENDED = new TStatusCode("SRM_REQUEST_SUSPENDED");
    public static final TStatusCode SRM_ABORTED = new TStatusCode("SRM_ABORTED");
    public static final TStatusCode SRM_RELEASED = new TStatusCode("SRM_RELEASED");
    public static final TStatusCode SRM_FILE_PINNED = new TStatusCode("SRM_FILE_PINNED");
    public static final TStatusCode SRM_FILE_IN_CACHE = new TStatusCode("SRM_FILE_IN_CACHE");
    public static final TStatusCode SRM_SPACE_AVAILABLE = new TStatusCode("SRM_SPACE_AVAILABLE");
    public static final TStatusCode SRM_LOWER_SPACE_GRANTED = new TStatusCode("SRM_LOWER_SPACE_GRANTED");
    public static final TStatusCode SRM_DONE = new TStatusCode("SRM_DONE");
    public static final TStatusCode SRM_PARTIAL_SUCCESS = new TStatusCode("SRM_PARTIAL_SUCCESS");
    public static final TStatusCode SRM_REQUEST_TIMED_OUT = new TStatusCode("SRM_REQUEST_TIMED_OUT");
    public static final TStatusCode SRM_LAST_COPY = new TStatusCode("SRM_LAST_COPY");
    public static final TStatusCode SRM_FILE_BUSY = new TStatusCode("SRM_FILE_BUSY");
    public static final TStatusCode SRM_FILE_LOST = new TStatusCode("SRM_FILE_LOST");
    public static final TStatusCode SRM_FILE_UNAVAILABLE = new TStatusCode("SRM_FILE_UNAVAILABLE");
    public static final TStatusCode SRM_CUSTOM_STATUS = new TStatusCode("SRM_CUSTOM_STATUS");

    private TStatusCode(String status) {
        this.status = status;
        _table_.put(status, this);
    }

    /**
     * Facility method to obtain a TStatusCode object from its String representation. An
     * IllegalArgumentExceptin is thrown if the supplied String does not have a TStatusCode counterpart.
     */
    public static TStatusCode fromValue(java.lang.String value) throws java.lang.IllegalArgumentException {
        TStatusCode enumeration = (TStatusCode) _table_.get(value);
        if (enumeration == null)
            throw new java.lang.IllegalArgumentException();
        return enumeration;
    }

    public static TStatusCode fromString(java.lang.String value) throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }

    public String getValue() {
        return status;
    }

    public String toString() {
        return status;
    }

    /*    public static void main(String[] args) {
    //Testing type-safe enum objects, toString, and ceration from String.
    System.out.println("Testing Objects and fromValue...");
    try {
        String s1 = "SRM_SUCCESS"; System.out.println("Should see "+s1+": "+TStatusCode.fromValue(s1));
        String s2 = "SRM_FAILURE"; System.out.println("Should see "+s2+": "+TStatusCode.fromValue(s2));
        String s3 = "SRM_AUTHENTICATION_FAILURE";  System.out.println("Should see "+s3+": "+TStatusCode.fromValue(s3));
        String s4 = "SRM_UNAUATHORIZED_ACCESS";  System.out.println("Should see "+s4+": "+TStatusCode.fromValue(s4));
        String s5 = "SRM_INVALID_REQUEST";  System.out.println("Should see "+s5+": "+TStatusCode.fromValue(s5));
        String s6 = "SRM_INVALID_PATH";  System.out.println("Should see "+s6+": "+TStatusCode.fromValue(s6));
        String s7 = "SRM_FILE_LIFETIME_EXPIRED";  System.out.println("Should see "+s7+": "+TStatusCode.fromValue(s7));
        String s8 = "SRM_SPACE_LIFETIME_EXPIRED";  System.out.println("Should see "+s8+": "+TStatusCode.fromValue(s8));
        String s9 = "SRM_EXCEED_ALLOCATION";  System.out.println("Should see "+s9+": "+TStatusCode.fromValue(s9));
        String s10 = "SRM_NO_USER_SPACE";  System.out.println("Should see "+s10+": "+TStatusCode.fromValue(s10));
        String s11 = "SRM_NO_FREE_SPACE";  System.out.println("Should see "+s11+": "+TStatusCode.fromValue(s11));
        String s12 = "SRM_DUPLICATION_ERROR"; System.out.println("Should see "+s12+": "+TStatusCode.fromValue(s12));
        String s13 = "SRM_NON_EMPTY_DIRECTORY"; System.out.println("Should see "+s13+": "+TStatusCode.fromValue(s13));
        String s14 = "SRM_TOO_MANY_RESULTS"; System.out.println("Should see "+s14+": "+TStatusCode.fromValue(s14));
        String s15 = "SRM_INTERNAL_ERROR"; System.out.println("Should see "+s15+": "+TStatusCode.fromValue(s15));
        String s16 = "SRM_FATAL_INTERNAL_ERROR"; System.out.println("Should see "+s16+": "+TStatusCode.fromValue(s16));
        String s17 = "SRM_NOT_SUPPORTED"; System.out.println("Should see "+s17+": "+TStatusCode.fromValue(s17));
        String s18 = "SRM_REQUEST_QUEUED"; System.out.println("Should see "+s18+": "+TStatusCode.fromValue(s18));
        String s19 = "SRM_REQUEST_INPROGRESS"; System.out.println("Should see "+s19+": "+TStatusCode.fromValue(s19));
        String s20 = "SRM_REQUEST_SUSPENDED"; System.out.println("Should see "+s20+": "+TStatusCode.fromValue(s20));
        String s21 = "SRM_ABORTED"; System.out.println("Should see "+s21+": "+TStatusCode.fromValue(s21));
        String s22 = "SRM_RELEASED"; System.out.println("Should see "+s22+": "+TStatusCode.fromValue(s22));
        String s23 = "SRM_FILE_PINNED"; System.out.println("Should see "+s23+": "+TStatusCode.fromValue(s23));
        String s24 = "SRM_FILE_IN_CACHE"; System.out.println("Should see "+s24+": "+TStatusCode.fromValue(s24));
        String s25 = "SRM_SPACE_AVAILABLE"; System.out.println("Should see "+s25+": "+TStatusCode.fromValue(s25));
        String s26 = "SRM_LOWER_SPACE_GRANTED"; System.out.println("Should see "+s26+": "+TStatusCode.fromValue(s26));
        String s27 = "SRM_DONE"; System.out.println("Should see "+s27+": "+TStatusCode.fromValue(s27));
        String s28 = "SRM_CUSTOM_STATUS"; System.out.println("Should see "+s28+": "+TStatusCode.fromValue(s28));
    } catch (Exception e) {
        System.out.println("Should not see this!");
    }
    //
    //Testing creation from malformed String
    System.out.println("\n\nTesting creation from a wrong String representation...");
    String sa = "wrongwrong"; System.out.print("String: "+sa);
    try {
        TStatusCode.fromValue(sa);
        System.out.println("Should not see this!");
    } catch (IllegalArgumentException e) {
        System.out.println("; OK creation failed as expected - "+e);
    }
    String sb = null; System.out.print("String: "+sb);
    try {
        TStatusCode.fromValue(sb);
        System.out.println("Should not see this!");
    } catch (IllegalArgumentException e) {
        System.out.println("; OK creation failed as expected - "+e);
    }
}*/
}
