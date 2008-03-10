package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TFileStorageType;
import java.util.List;
import java.util.Arrays;

/**
 * Class that represents some of the fields in a row in the Persistence Layer:
 * this is all raw data referring to the ReducedPtPChunkData proper, that is
 * String and primitive types.
 *
 * @author  EGRID ICTP
 * @version 1.0
 * @date    January, 2007
 */
public class ReducedPtPChunkDataTO {
    private long primaryKey = -1; //ID primary key of record in DB
    private String toSURL = " ";
    private int status = StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_QUEUED);
    private String errString = " ";
    private String fileStorageType = FileStorageTypeConverter.getInstance().toDB(TFileStorageType.VOLATILE);
    private int fileLifetime = -1;
    private boolean empty = true;

    public long primaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(long n) {
        empty = false;
        primaryKey = n;
    }

    public String toSURL() {
        return toSURL;
    }

    public void setToSURL(String s) {
        empty=false;
        toSURL=s;
    }


    public int status() {
        return status;
    }

    public void setStatus(int n) {
        empty = false;
        status = n;
    }

    public String errString() {
        return errString;
    }

    public void setErrString(String s) {
        empty = false;
        errString = s;
    }

    public String fileStorageType() {
        return fileStorageType;
    }

    /**
     * Method that sets the FileStorageType: if it is null nothing gets set.
     * The deafult value is Volatile.
     */
    public void setFileStorageType(String s) {
        empty=false;
        if (s!=null) fileStorageType = s;
    }

    public int fileLifetime() {
        return fileLifetime;
    }

    public void setFileLifetime(int n) {
        empty=false;
        fileLifetime=n;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(primaryKey); sb.append(" ");
        sb.append(toSURL); sb.append(" ");
        sb.append(status); sb.append(" ");
        sb.append(errString); sb.append(" ");
        sb.append(fileStorageType); sb.append(" ");
        return sb.toString();
    }
}
