package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TStatusCode;

/**
 * Class that represents some of the fields in a row in the Persistence Layer:
 * this is all raw data referring to the ReducedBoLChunkData proper, that is
 * String and primitive types.
 *
 * @author  EGRID ICTP
 * @version 1.0
 * @date    November, 2006
 */
public class ReducedBoLChunkDataTO {
    private long primaryKey = -1; //ID primary key of record in DB
    private String fromSURL = " ";
    private int status = StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_QUEUED);
    private String errString = " ";
    private boolean empty = true;

    public String errString() {
        return errString;
    }

    public String fromSURL() {
        return fromSURL;
    }

    public long primaryKey() {
        return primaryKey;
    }

    public void setErrString(String s) {
        empty = false;
        errString = s;
    }


    public void setFromSURL(String s) {
        empty=false;
        fromSURL=s;
    }

    public void setPrimaryKey(long n) {
        empty = false;
        primaryKey = n;
    }

    public void setStatus(int n) {
        empty = false;
        status = n;
    }

    public int status() {
        return status;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(primaryKey); sb.append(" ");
        sb.append(fromSURL); sb.append(" ");
        sb.append(status); sb.append(" ");
        sb.append(errString); sb.append(" ");
        return sb.toString();
    }
}
