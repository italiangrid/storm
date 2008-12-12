package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.common.types.TransferProtocol;
import it.grid.storm.common.types.TURLPrefix;

import java.util.List;
import java.util.Arrays;
import it.grid.storm.namespace.model.Protocol;

/**
 * Class that represents a row in the Persistence Layer: this is all raw data
 * referring to the PtPChunkData proper, that is, String and primitive types.
 *
 * Each field is initialized with default values as per SRM 2.2 specification:
 *      protocolList     GSIFTP
 *      fileStorageType  VOLATILE
 *      overwriteMode    NEVER
 *      status           SRM_REQUEST_QUEUED
 *
 * All other fields are 0 if int, or a white space if String.
 *
 * @author  EGRID ICTP
 * @version 2.0
 * @date    June 2005
 */
public class PtPChunkDataTO {
    private long primaryKey = -1; //ID primary key of status_Put record in DB
    private String requestToken = " ";
    private String toSURL = " ";
    private int pinLifetime = -1;
    private int fileLifetime = -1;
    private String fileStorageType = null; //initialised in constructor
    private String spaceToken = " ";
    private long expectedFileSize = 0;
    private List protocolList = null; //initialised in constructor
    private String overwriteOption = null; //initialised in constructor
    private int status; //initialised in constructor
    private String errString = " ";
    private String turl = " ";
    private boolean empty = true;

    /**
     * Constructr that initialises PtPChunkDataTO with correct SRM 2.2 default
     * values.
     *      protocolList     GSIFTP
     *      fileStorageType  VOLATILE
     *      overwriteMode    NEVER
     *      status           SRM_REQUEST_QUEUED
     */
    public  PtPChunkDataTO() {
        this.fileStorageType = FileStorageTypeConverter.getInstance().toDB(TFileStorageType.VOLATILE);
        TURLPrefix protocolPreferences = new TURLPrefix();
        protocolPreferences.addProtocol(Protocol.GSIFTP);
        this.protocolList = TransferProtocolListConverter.toDB(protocolPreferences);
        this.overwriteOption = OverwriteModeConverter.getInstance().toDB(TOverwriteMode.NEVER);
        this.status = StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_QUEUED);
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

    public String toSURL() {
        return toSURL;
    }

    public void setToSURL(String s) {
        empty=false;
        toSURL=s;
    }

    public int pinLifetime() {
        return pinLifetime;
    }

    public void setPinLifetime(int n) {
        empty=false;
        pinLifetime=n;
    }

    public int fileLifetime() {
        return fileLifetime;
    }

    public void setFileLifetime(int n) {
        empty=false;
        fileLifetime=n;
    }

    public String fileStorageType() {
        return fileStorageType;
    }

    /**
     * Method that sets the FileStorageType: if it is null nothing gets set.
     * The deafult value is Permanent.
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

    public long expectedFileSize() {
        return expectedFileSize;
    }

    public void setExpectedFileSize(long l) {
        empty = false;
        expectedFileSize = l;
    }

    public List protocolList() {
        return protocolList;
    }

    public void setProtocolList(List l) {
        empty = false;
        if ((l!=null) && (!l.isEmpty())) protocolList = l;
    }

    public String overwriteOption() {
        return overwriteOption;
    }

    /**
     * Method that sets the OverwriteMode: if it is null nothing gets set.
     * The deafult value is Never.
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

    public String transferURL() {
        return turl;
    }

    public void setTransferURL(String s) {
        empty = false;
        turl = s;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(primaryKey); sb.append(" ");
        sb.append(requestToken); sb.append(" ");
        sb.append(toSURL); sb.append(" ");
        sb.append(pinLifetime); sb.append(" ");
        sb.append(fileLifetime); sb.append(" ");
        sb.append(fileStorageType); sb.append(" ");
        sb.append(spaceToken); sb.append(" ");
        sb.append(expectedFileSize); sb.append(" ");
        sb.append(protocolList); sb.append(" ");
        sb.append(overwriteOption); sb.append(" ");
        sb.append(status); sb.append(" ");
        sb.append(errString); sb.append(" ");
        sb.append(turl);
        return sb.toString();
    }
}
