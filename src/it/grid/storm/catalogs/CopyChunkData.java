package it.grid.storm.catalogs;

import org.apache.log4j.Logger;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TReturnStatus;

import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;

/**
 * This class represents a CopyChunkData, that is part of a multifile Copy srm
 * request. It contains data about: the requestToken, the fromSURL, the toSURL,
 * the target fileLifeTime, the target fileStorageType and any available target
 * spaceToken, the target overwriteOption to be applied in case the file already
 * exists, the fileSize of the existing file if any, return status of the file
 * together with its error string.
 *
 * @author  EGRID - ICTP Trieste
 * @date    September, 2005
 * @version 2.0
 */
public class CopyChunkData implements ChunkData {
    private static final Logger log = Logger.getLogger("asynch"); //Logger of error messages! Common to all Asynch package!

    private long primaryKey = -1; //long representing the primary key for the persistence layer!
    private TRequestToken requestToken;  //This is the requestToken of the multifile srm request to which this chunk belongs
    private TSURL fromSURL;              //SURL from which the srmCopy will get the file
    private TSURL toSURL;                //SURL to which the srmCopy will put the file
    private TLifeTimeInSeconds lifetime;      //requested lifetime - BEWARE!!! It is the fileLifetime at destination in case of Volatile files!
    private TFileStorageType fileStorageType; //TFileStorageType at destination
    private TSpaceToken spaceToken;           //SpaceToken to use for toSURL
    private TOverwriteMode overwriteOption;   //specifies the behaviour in case of existing files for Put part of the copy (could be local or remote!)
    private TReturnStatus status;             //return status for this chunk of request

    public CopyChunkData(TRequestToken requestToken, TSURL fromSURL, TSURL toSURL,
        TLifeTimeInSeconds lifetime, TFileStorageType fileStorageType,
        TSpaceToken spaceToken, TOverwriteMode overwriteOption, TReturnStatus status)
        throws InvalidCopyChunkDataAttributesException {

        boolean ok = requestToken!=null &&
            fromSURL!=null &&
            toSURL!=null &&
            lifetime!=null &&
            fileStorageType!=null &&
            spaceToken!=null &&
            overwriteOption!=null &&
            status!=null;

        if (!ok) throw new InvalidCopyChunkDataAttributesException(requestToken,
            fromSURL,toSURL,lifetime,fileStorageType,spaceToken,overwriteOption,
            status);

        this.requestToken = requestToken;
        this.fromSURL = fromSURL;
        this.toSURL = toSURL;
        this.lifetime = lifetime;
        this.fileStorageType=fileStorageType;
        this.spaceToken=spaceToken;
        this.overwriteOption=overwriteOption;
        this.status=status;
    }

    /**
     * Method used to get the primary key used in the persistence layer!
     */
    public long primaryKey() {
        return primaryKey;
    }

    /**
     * Method used to set the primary key to be used in the persistence layer!
     */
    public void setPrimaryKey(long l) {
        primaryKey = l;
    }

    /**
     * Method that returns the requestToken of the srm request to which this chunk belongs.
     */
    public TRequestToken requestToken() {return requestToken;}

    /**
     * Method that returns the fromSURL of the srm request to which this chunk belongs.
     */
    public TSURL fromSURL() {return fromSURL;}

    /**
     * Method that returns the toSURL of the srm request to which this chunk belongs.
     */
    public TSURL toSURL() {return toSURL;}

    /**
     * Method that returns the requested pin life time for this chunk of the srm request.
     */
    public TLifeTimeInSeconds lifetime() {return lifetime;}

    /**
     * Method that returns the fileStorageType for this chunk of the srm request.
     */
    public TFileStorageType fileStorageType() {return fileStorageType;}

    /**
     * Method that returns the space token supplied for this chunk of the srm request.
     */
    public TSpaceToken spaceToken() {return spaceToken;}

    /**
     * Method that returns the overwriteOption specified in the srm request.
     */
    public TOverwriteMode overwriteOption() {return overwriteOption;}

    /**
     * Method that returns the status for this chunk of the srm request.
     */
    public TReturnStatus status() {return status;}





    /**
     * Method that sets the status of this request to SRM_REQUEST_QUEUED;
     * it needs the explanation String which describes the situation in greater detail;
     * if a null is passed, then an empty String is used as explanation.
     */
    public void changeStatusSRM_REQUEST_QUEUED(String explanation) {
        try {
            if (explanation==null) explanation="";
            status = new TReturnStatus(TStatusCode.SRM_REQUEST_QUEUED,explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.error("UNEXPECTED ERROR! Unable to set SRM request status to SRM_REQUEST_QUEUED! "+e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_INVALID_REQUEST;
     * it needs the explanation String which describes the situation in greater detail;
     * if a null is passed, then an empty String is used as explanation.
     */
    public void changeStatusSRM_INVALID_REQUEST(String explanation) {
        try {
            if (explanation==null) explanation="";
            status = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.error("UNEXPECTED ERROR! Unable to set SRM request status to SRM_INVALID_REQUEST! "+e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_AUTHORIZATION_FAILURE;
     * it needs the explanation String which describes the situation in greater detail;
     * if a null is passed, then an empty String is used as explanation.
     */
    public void changeStatusSRM_AUTHORIZATION_FAILURE(String explanation) {
        try {
            if (explanation==null) explanation="";
            status = new TReturnStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE,explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.error("UNEXPECTED ERROR! Unable to set SRM request status to SRM_UNAUTHORISED_ACCESS! "+e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_SUCCESS;
     * it needs the explanation String which describes the situation in greater detail;
     * if a null is passed, then an empty String is used as explanation.
     */
    public void changeStatusSRM_SUCCESS(String explanation) {
        try {
            if (explanation==null) explanation="";
            status = new TReturnStatus(TStatusCode.SRM_SUCCESS,explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.error("UNEXPECTED ERROR! Unable to set SRM request status to SRM_SUCCESS! "+e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_FAILURE;
     * it needs the explanation String which describes the situation in greater detail;
     * if a null is passed, then an empty String is used as explanation.
     */
    public void changeStatusSRM_FAILURE(String explanation) {
        try {
            if (explanation==null) explanation="";
            status = new TReturnStatus(TStatusCode.SRM_FAILURE,explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.error("UNEXPECTED ERROR! Unable to set SRM request status to SRM_FAILURE! "+e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_FAILURE;
     * it needs the explanation String which describes the situation in greater detail;
     * if a null is passed, then an empty String is used as explanation.
     */
    public void changeStatusSRM_NOT_SUPPORTED(String explanation) {
        try {
            if (explanation==null) explanation="";
            status = new TReturnStatus(TStatusCode.SRM_NOT_SUPPORTED,explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.error("UNEXPECTED ERROR! Unable to set SRM request status to SRM_NOT_SUPPORTED! "+e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_DUPLICATION_ERROR;
     * it needs the explanation String which describes the situation in greater detail;
     * if a null is passed, then an empty String is used as explanation.
     */
    public void changeStatusSRM_DUPLICATION_ERROR(String explanation) {
        try {
            if (explanation==null) explanation="";
            status = new TReturnStatus(TStatusCode.SRM_DUPLICATION_ERROR,explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.error("UNEXPECTED ERROR! Unable to set SRM request status to SRM_DUPLICATION_ERROR! "+e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_REQUEST_INPROGRESS;
     * it needs the explanation String which describes the situation in greater detail;
     * if a null is passed, then an empty String is used as explanation.
     */
    public void changeStatusSRM_REQUEST_INPROGRESS(String explanation) {
        try {
            if (explanation==null) explanation="";
            status = new TReturnStatus(TStatusCode.SRM_REQUEST_INPROGRESS,explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.error("UNEXPECTED ERROR! Unable to set SRM request status to SRM_REQUEST_INPROGRESS! "+e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_INTERNAL_ERROR;
     * it needs the explanation String which describes the situation in greater detail;
     * if a null is passed, then an empty String is used as explanation.
     */
    public void changeStatusSRM_INTERNAL_ERROR(String explanation) {
        try {
            if (explanation==null) explanation="";
            status = new TReturnStatus(TStatusCode.SRM_INTERNAL_ERROR,explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.error("UNEXPECTED ERROR! Unable to set SRM request status to SRM_INTERNAL_ERROR! "+e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_FATAL_INTERNAL_ERROR;
     * it needs the explanation String which describes the situation in greater detail;
     * if a null is passed, then an empty String is used as explanation.
     */
    public void changeStatusSRM_FATAL_INTERNAL_ERROR(String explanation) {
        try {
            if (explanation==null) explanation="";
            status = new TReturnStatus(TStatusCode.SRM_FATAL_INTERNAL_ERROR,explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.error("UNEXPECTED ERROR! Unable to set SRM request status to SRM_FATAL_INTERNAL_ERROR! "+e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_INVALID_PATH;
     * it needs the explanation String which describes the situation in greater detail;
     * if a null is passed, then an empty String is used as explanation.
     */
    public void changeStatusSRM_INVALID_PATH(String explanation) {
        try {
            if (explanation==null) explanation="";
            status = new TReturnStatus(TStatusCode.SRM_INVALID_PATH,explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.error("UNEXPECTED ERROR! Unable to set SRM request status to SRM_INVALID_PATH! "+e);
        }
    }



    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CopyChunkData\n");
        sb.append("primaryKey="); sb.append(primaryKey); sb.append("; ");
        sb.append("RequestToken="); sb.append(requestToken); sb.append("; ");
        sb.append("fromSURL="); sb.append(fromSURL); sb.append("; ");
        sb.append("toSURL="); sb.append(toSURL); sb.append("; ");
        sb.append("lifetime="); sb.append(lifetime); sb.append("; ");
        sb.append("fileStorageType="); sb.append(fileStorageType); sb.append("; ");
        sb.append("spaceToken="); sb.append(spaceToken); sb.append("; ");
        sb.append("overwriteOption="); sb.append(overwriteOption); sb.append("; ");
        sb.append("status="); sb.append(status); sb.append("; ");
        return sb.toString();
    }

    public int hashCode() {
        int hash = 17;
        hash = 37*hash + new Long(primaryKey).hashCode();
        hash = 37*hash + requestToken.hashCode();
        hash = 37*hash + fromSURL.hashCode();
        hash = 37*hash + toSURL.hashCode();
        hash = 37*hash + lifetime.hashCode();
        hash = 37*hash + fileStorageType.hashCode();
        hash = 37*hash + spaceToken.hashCode();
        hash = 37*hash + overwriteOption.hashCode();
        hash = 37*hash + status.hashCode();
        return hash;
    }

    public boolean equals(Object o) {
        if (o==this) return true;
        if (!(o instanceof CopyChunkData)) return false;
        CopyChunkData cd = (CopyChunkData) o;
        return (primaryKey==cd.primaryKey) &&
            requestToken.equals(cd.requestToken) &&
            fromSURL.equals(cd.fromSURL) &&
            toSURL.equals(cd.toSURL) &&
            lifetime.equals(cd.lifetime) &&
            fileStorageType.equals(cd.fileStorageType) &&
            spaceToken.equals(cd.spaceToken) &&
            overwriteOption.equals(cd.overwriteOption) &&
            status.equals(cd.status);
    }
}
