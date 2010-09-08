package it.grid.storm.catalogs;

import java.util.List;
import java.util.ArrayList;

import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TStatusCode;

/**
 * Class that represents a row in the Persistence Layer: this is all raw data
 * referring to the CopyChunkData proper, that is, String and primitive types.
 *
 * Each field is initialized with default values as per SRM 2.2 specification:
 *      fileStorageType  VOLATILE
 *      overwriteMode    NEVER
 *      status           SRM_REQUEST_QUEUED
 *
 * All other fields are 0 if int, or a white space if String.
 *
 * @author  EGRID ICTP
 * @version 2.0
 * @date    Semptember 2005
 */
public class CopyChunkDataTO {
    private long primaryKey = -1; //ID primary key of record in DB
    private String requestToken = " ";
    private String fromSURL = " ";
    private String toSURL = " ";
    private int lifetime = 0;
    private String fileStorageType = null; //initialised in constructor 
    private String spaceToken = " ";
    private String overwriteOption = null; //initialised in constructor 
    private int status; //initialised in constructor
    private String errString = " ";
    private boolean empty = true;

    public CopyChunkDataTO() {
        fileStorageType = FileStorageTypeConverter.getInstance().toDB(TFileStorageType.VOLATILE);
        overwriteOption = OverwriteModeConverter.getInstance().toDB(TOverwriteMode.NEVER);
        status = StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_QUEUED);
    }

    public boolean isEmpty() {
        return empty;
    }

    public long primaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(long n) {
        empty = false;
        primaryKey = n;
    }

    public String requestToken() {
        return requestToken;
    }

    public void setRequestToken(String s) {
        empty = false;
        requestToken = s;
    }

    public String fromSURL() {
        return fromSURL;
    }

    public void setFromSURL(String s) {
        empty=false;
        fromSURL=s;
    }

    public String toSURL() {
        return toSURL;
    }

    public void setToSURL(String s) {
        empty=false;
        toSURL=s;
    }

    public int lifeTime() {
        return lifetime;
    }

    public void setLifeTime(int n) {
        empty=false;
        lifetime=n;
    }

    public String fileStorageType() {
        return fileStorageType;
    }

    /**
     * Method used to set the FileStorageType: if s is null nothing gets set;
     * the internal default String is the one relative to Volatile
     * FileStorageType.
     */
    public void setFileStorageType(String s) {
        empty=false;
        if (s!=null) fileStorageType = s;
    }

    public String spaceToken() {
        return spaceToken;
    }

    public void setSpaceToken(String s) {
        empty = false;
        spaceToken = s;
    }

    public String overwriteOption() {
        return overwriteOption;
    }

    /**
     * Method used to set the OverwriteMode: if s is null nothing gets set; the
     * internal default String is the one relative to Never OverwriteMode.
     */
    public void setOverwriteOption(String s) {
        empty = false;
        if (s!=null) overwriteOption = s;
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

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(primaryKey); sb.append(" ");
        sb.append(requestToken); sb.append(" ");
        sb.append(fromSURL); sb.append(" ");
        sb.append(toSURL); sb.append(" ");
        sb.append(lifetime); sb.append(" ");
        sb.append(fileStorageType); sb.append(" ");
        sb.append(spaceToken); sb.append(" ");
        sb.append(overwriteOption); sb.append(" ");
        sb.append(status); sb.append(" ");
        sb.append(errString); sb.append(" ");
        return sb.toString();
    }
}
