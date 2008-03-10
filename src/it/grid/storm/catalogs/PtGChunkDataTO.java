package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.common.types.TransferProtocol;
import it.grid.storm.common.types.TURLPrefix;
import java.util.List;

/**
 * Class that represents a row in the Persistence Layer: this is all raw data
 * referring to the PtGChunkData proper, that is, String and primitive types.
 *
 * Each field is initialized with default values as per SRM 2.2 specification:
 *      protocolList     GSIFTP
 *      dirOption        false
 *      status           SRM_REQUEST_QUEUED
 *
 * All other fields are 0 if int, or a white space if String.
 *
 * @author  EGRID ICTP
 * @version 3.0
 * @date    June 2005
 */
public class PtGChunkDataTO {
    private long primaryKey = -1; //ID primary key of record in DB
    private String requestToken = " ";
    private String fromSURL = " ";
    private int lifetime = 0;
    private boolean dirOption; //initialised in constructor
    private boolean allLevelRecursive; //initialised in constructor
    private int numLevel; //initialised in constructor
    private List protocolList = null; //initialised in constructor
    private long filesize = 0;
    private int status; //initialised in constructor
    private String errString = " ";
    private String turl = " ";
    private boolean empty = true;

    public PtGChunkDataTO() {
        TURLPrefix aux = new TURLPrefix();
        aux.addTransferProtocol(TransferProtocol.GSIFTP);
        this.protocolList = TransferProtocolListConverter.getInstance().toDB(aux);
        this.status = StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_QUEUED);
        this.dirOption = false;
        this.allLevelRecursive = false;
        this.numLevel = 0;
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

    public int lifeTime() {
        return lifetime;
    }

    public void setLifeTime(int n) {
        empty=false;
        lifetime=n;
    }

    public boolean dirOption() {
        return dirOption;
    }

    public void setDirOption(boolean b) {
        empty = false;
        dirOption = b;
    }

    public boolean allLevelRecursive() {
        return allLevelRecursive;
    }

    public void setAllLevelRecursive(boolean b) {
        empty = false;
        allLevelRecursive = b;
    }

    public int numLevel() {
        return numLevel;
    }

    public void setNumLevel(int n) {
        empty = false;
        numLevel = n;
    }

    public List protocolList() {
        return protocolList;
    }

    public void setProtocolList(List l) {
        empty = false;
        if ((l!=null) && (!l.isEmpty())) protocolList = l;
    }

    public long fileSize() {
        return filesize;
    }

    public void setFileSize(long n) {
        empty = false;
        filesize = n;
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

    public String turl() {
        return turl;
    }

    public void setTurl(String s) {
        empty = false;
        turl = s;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(primaryKey); sb.append(" ");
        sb.append(requestToken); sb.append(" ");
        sb.append(fromSURL); sb.append(" ");
        sb.append(lifetime); sb.append(" ");
        sb.append(dirOption); sb.append(" ");
        sb.append(allLevelRecursive); sb.append(" ");
        sb.append(numLevel); sb.append(" ");
        sb.append(protocolList); sb.append(" ");
        sb.append(filesize); sb.append(" ");
        sb.append(status); sb.append(" ");
        sb.append(errString); sb.append(" ");
        sb.append(turl);
        return sb.toString();
    }
}
