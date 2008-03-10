package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.griduser.VomsGridUser;

/**
 * This class represents an Exception thrown when a RequestSummaryData object is
 * created with any invalid attributes: null TRequestType, null TRequestToken,
 * null VomsGridUser.
 * 
 * @author  EGRID - ICTP Trieste
 * @date    March 18th, 2005
 * @version 3.0
 */
public class InvalidRequestSummaryDataAttributesException extends Exception {

    //booleans true if the corresponding variablesare null or negative
    private boolean nullRequestType = true;
    private boolean nullRequestToken = true;
    private boolean nullVomsGridUser = true;

    /**
     * Constructor that requires the attributes that caused the
     * exception to be thrown.
     */
    public InvalidRequestSummaryDataAttributesException(TRequestType requestType, TRequestToken requestToken, VomsGridUser gu) {
        nullRequestType = (requestType==null);
        nullRequestToken = (requestToken==null);
        nullVomsGridUser = (gu==null);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Invalid RequestSummaryData attributes exception: ");
        sb.append("nullRequestType="); sb.append(nullRequestType);
        sb.append("; nullRequestToken="); sb.append(nullRequestToken);
        sb.append("; nullVomsGridUser="); sb.append(nullVomsGridUser);
        return sb.toString();
    }
}
