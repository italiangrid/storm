package it.grid.storm.catalogs;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.srm.types.TStatusCode;

import java.util.List;

/**
 * Class that represents a row in the Persistence Layer: this is all raw data referring to the
 * BoLChunkData proper, that is, String and primitive types.
 * 
 * Each field is initialized with default values as per SRM 2.2 specification: protocolList GSIFTP
 * dirOption false status SRM_REQUEST_QUEUED
 * 
 * All other fields are 0 if int, or a white space if String.
 * 
 * @author CNAF
 * @version 1.0
 * @date Aug 2009
 */
public class BoLChunkDataTO {
    private long primaryKey = -1; // ID primary key of record in DB
    private String requestToken = " ";
    private String fromSURL = " ";
    private int lifetime = 0;
    private boolean dirOption; // initialised in constructor
    private boolean allLevelRecursive; // initialised in constructor
    private int numLevel; // initialised in constructor
    private List<String> protocolList = null; // initialised in constructor
    private long filesize = 0;
    private int status; // initialised in constructor
    private String errString = " ";
    private boolean empty = true;
    private int deferredStartTime = -1;

    public BoLChunkDataTO() {
        TURLPrefix protocolPreferences = new TURLPrefix();
        protocolPreferences.addProtocol(Protocol.GSIFTP);
        this.protocolList = TransferProtocolListConverter.toDB(protocolPreferences);
        this.status = StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_QUEUED);
        this.dirOption = false;
        this.allLevelRecursive = false;
        this.numLevel = 0;
    }

    public boolean getAllLevelRecursive() {
        return allLevelRecursive;
    }

    public int getDeferredStartTime() {
        return deferredStartTime;
    }

    public boolean getDirOption() {
        return dirOption;
    }

    public String getErrString() {
        return errString;
    }

    public long getFileSize() {
        return filesize;
    }

    public String getFromSURL() {
        return fromSURL;
    }

    public int getLifeTime() {
        return lifetime;
    }

    public int getNumLevel() {
        return numLevel;
    }

    public long getPrimaryKey() {
        return primaryKey;
    }

    public List<String> getProtocolList() {
        return protocolList;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public int getStatus() {
        return status;
    }

    public void setAllLevelRecursive(boolean b) {
        empty = false;
        allLevelRecursive = b;
    }

    public void setDeferredStartTime(int deferredStartTime) {
        this.deferredStartTime = deferredStartTime;
    }

    public void setDirOption(boolean b) {
        empty = false;
        dirOption = b;
    }

    public void setErrString(String s) {
        empty = false;
        errString = s;
    }

    public void setFileSize(long n) {
        empty = false;
        filesize = n;
    }

    public void setFromSURL(String s) {
        empty = false;
        fromSURL = s;
    }

    public void setLifeTime(int n) {
        empty = false;
        lifetime = n;
    }

    public void setNumLevel(int n) {
        empty = false;
        numLevel = n;
    }

    public void setPrimaryKey(long n) {
        empty = false;
        primaryKey = n;
    }

    public void setProtocolList(List<String> l) {
        empty = false;
        if ((l != null) && (!l.isEmpty())) {
            protocolList = l;
        }
    }

    public void setRequestToken(String s) {
        empty = false;
        requestToken = s;
    }

    public void setStatus(int n) {
        empty = false;
        status = n;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(primaryKey);
        sb.append(" ");
        sb.append(requestToken);
        sb.append(" ");
        sb.append(fromSURL);
        sb.append(" ");
        sb.append(lifetime);
        sb.append(" ");
        sb.append(dirOption);
        sb.append(" ");
        sb.append(allLevelRecursive);
        sb.append(" ");
        sb.append(numLevel);
        sb.append(" ");
        sb.append(protocolList);
        sb.append(" ");
        sb.append(filesize);
        sb.append(" ");
        sb.append(status);
        sb.append(" ");
        sb.append(errString);
        return sb.toString();
    }
}
