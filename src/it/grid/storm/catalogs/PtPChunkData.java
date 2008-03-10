package it.grid.storm.catalogs;

import org.apache.log4j.Logger;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStorageSystemInfo;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.common.types.TURLPrefix;

/**
 * This class represents a PrepareToPutChunkData, that is part of a multifile
 * PrepareToPut srm request. It contains data about: the requestToken, the
 * toSURL, the requested lifeTime of pinning, the requested lifetime of
 * volatile, the requested fileStorageType and any available spaceToken, the
 * expectedFileSize, the desired transferProtocols in order of preference,
 * the overwriteOption to be applied in case the file already exists, the
 * transferURL for the supplied SURL.
 *
 * @author  EGRID - ICTP Trieste
 * @date    June, 2005
 * @version 2.0
 */
public class PtPChunkData implements ChunkData {
    private static final Logger log = Logger.getLogger("asynch"); //Logger of error messages! Common to all Asynch package!

    private long primaryKey = -1;             //long representing the primary key for the persistence layer, in the status_Put table!
    private TRequestToken requestToken;       //This is the requestToken of the multifile srm request to which this chunk belongs
    private TLifeTimeInSeconds pinLifetime;   //requested lifetime for TURL.
    private TLifeTimeInSeconds fileLifetime;  //requested lifetime for SURL in case of Volatile entry.
    private TFileStorageType fileStorageType; //TFileStorageType requested for specific toSURL to put
    private TSpaceToken spaceToken;           //SpaceToken to use for toSURL
    private TURLPrefix transferProtocols;     //list of desired transport protocols for toSURL
    private TOverwriteMode overwriteOption;   //specifies the behaviour in case of existing files
    private TSURL toSURL;                     //SURL that the srm command wants to put

    private TSizeInBytes expectedFileSize;    //size of file that will be transferred

    private TReturnStatus status;             //return status for this chunk of request
    private TTURL transferURL;                //TURL for picking up the requested file



    public PtPChunkData(TRequestToken requestToken, TSURL toSURL,
        TLifeTimeInSeconds pinLifetime, TLifeTimeInSeconds fileLifetime,
        TFileStorageType fileStorageType, TSpaceToken spaceToken,
        TSizeInBytes expectedFileSize, TURLPrefix transferProtocols,
        TOverwriteMode overwriteOption, TReturnStatus status, TTURL transferURL)
        throws InvalidPtPChunkDataAttributesException {
        boolean ok = requestToken!=null &&
            toSURL!=null &&
            pinLifetime!=null &&
            fileLifetime!=null &&
            fileStorageType!=null &&
            spaceToken!=null &&
            expectedFileSize!=null &&
            transferProtocols!=null &&
            overwriteOption!=null &&
            status!=null &&
            transferURL!=null;

        if (!ok) throw new InvalidPtPChunkDataAttributesException(requestToken,toSURL,
            pinLifetime,fileLifetime,fileStorageType,spaceToken,expectedFileSize,
            transferProtocols,overwriteOption,status,transferURL);
        this.requestToken = requestToken;
        this.toSURL = toSURL;
        this.pinLifetime = pinLifetime;
        this.fileLifetime=fileLifetime;
        this.fileStorageType=fileStorageType;
        this.spaceToken=spaceToken;
        this.expectedFileSize = expectedFileSize;
        this.transferProtocols=transferProtocols;
        this.overwriteOption=overwriteOption;
        this.status=status;
        this.transferURL=transferURL;
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
     * Method used to set the Status associated to this chunk.
     * If status is null, then nothing gets set!
     */
    public void setStatus(TReturnStatus newstat) {
        if (newstat!=null) status = newstat;
    }

    /**
     * Method that returns the requestToken of the srm request to which this chunk belongs.
     */
    public TRequestToken requestToken() {return requestToken;}

    /**
     * Method that returns the toSURL of the srm request to which this chunk belongs.
     */
    public TSURL toSURL() {return toSURL;}

    /**
     * Method that returns the requested pin life time for this chunk of the srm request.
     */
    public TLifeTimeInSeconds pinLifetime() {return pinLifetime;}

    /**
     * Method that returns the requested file life time for this chunk of the srm request.
     */
    public TLifeTimeInSeconds fileLifetime() {return fileLifetime;}

    /**
     * Method that returns the fileStorageType for this chunk of the srm request.
     */
    public TFileStorageType fileStorageType() {return fileStorageType;}

    /**
     * Method that returns the space token supplied for this chunk of the srm request.
     */
    public TSpaceToken spaceToken() {return spaceToken;}

    /**
     * Method that returns the knownSizeOfThisFile supplied with this chunk of the srm request.
     */
    public TSizeInBytes expectedFileSize() {return expectedFileSize;}

    /**
     * Method that returns a TURLPrefix containing the transfer protocols desired
     * for this chunk of the srm request.
     */
    public TURLPrefix transferProtocols() {return transferProtocols;}

    /**
     * Method that returns the overwriteOption specified in the srm request.
     */
    public TOverwriteMode overwriteOption() {return overwriteOption;}

    /**
     * Method that returns the status for this chunk of the srm request.
     */
    public TReturnStatus status() {return status;}

    /**
     * Method that returns the TURL for this chunk of the srm request.
     */
    public TTURL transferURL() {return transferURL;}






    /**
     * Method used to set the transferURL associated to the SURL of this chunk.
     * If TTURL is null, then nothing gets set!
     */
	public void setTransferURL(final TTURL turl) {
        if (turl!=null) transferURL = turl;
    }

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
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_REQUEST_QUEUED! "+e);
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
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_REQUEST_INPROGRESS! "+e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_SPACE_AVAILABLE;
     * it needs the explanation String which describes the situation in greater detail;
     * if a null is passed, then an empty String is used as explanation.
     */
    public void changeStatusSRM_SPACE_AVAILABLE(String explanation) {
        try {
            if (explanation==null) explanation="";
            status = new TReturnStatus(TStatusCode.SRM_SPACE_AVAILABLE,explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_SPACE_AVAILABLE! "+e);
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
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_SUCSESS! "+e);
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
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_FAILURE! "+e);
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
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_INTERNAL_ERROR! "+e);
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
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_INVALID_REQUEST! "+e);
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
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_AUTHORIZATION_FAILURE! "+e);
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
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_DUPLICATION_ERROR! "+e);
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
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_INVALID_PATH! "+e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_FILE_BUSY;
     * it needs the explanation String which describes the situation in greater detail;
     * if a null is passed, then an empty String is used as explanation.
     */
    public void changeStatusSRM_FILE_BUSY(String explanation) {
        try {
            if (explanation==null) explanation="";
            status = new TReturnStatus(TStatusCode.SRM_FILE_BUSY,explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_FILE_BUSY! "+e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_ABORTED;
     * it needs the explanation String which describes the situation in greater detail;
     * if a null is passed, then an empty String is used as explanation.
     */
    public void changeStatusSRM_ABORTED(String explanation) {
        try {
            if (explanation==null) explanation="";
            status = new TReturnStatus(TStatusCode.SRM_ABORTED,explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_ABORTED! "+e);
        }
    }





    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("PtPChunkData\n");
        sb.append("primaryKey="); sb.append(primaryKey); sb.append("; ");
        sb.append("RequestToken="); sb.append(requestToken); sb.append("; ");
        sb.append("toSURL="); sb.append(toSURL); sb.append("; ");
        sb.append("pinLifetime="); sb.append(pinLifetime); sb.append("; ");
        sb.append("fileLifetime="); sb.append(fileLifetime); sb.append("; ");
        sb.append("fileStorageType="); sb.append(fileStorageType); sb.append("; ");
        sb.append("spaceToken="); sb.append(spaceToken); sb.append("; ");
        sb.append("expectedFileSize="); sb.append(expectedFileSize); sb.append("; ");
        sb.append("transferProtocols="); sb.append(transferProtocols); sb.append("; ");
        sb.append("overwriteOption="); sb.append(overwriteOption); sb.append("; ");
        sb.append("status="); sb.append(status); sb.append("; ");
        sb.append("transferURL="); sb.append(transferURL); sb.append("; ");
        return sb.toString();
    }

    public int hashCode() {
        int hash = 17;
        hash = 37*hash + new Long(primaryKey).hashCode();
        hash = 37*hash + requestToken.hashCode();
        hash = 37*hash + toSURL.hashCode();
        hash = 37*hash + pinLifetime.hashCode();
        hash = 37*hash + fileLifetime.hashCode();
        hash = 37*hash + fileStorageType.hashCode();
        hash = 37*hash + spaceToken.hashCode();
        hash = 37*hash + expectedFileSize.hashCode();
        hash = 37*hash + transferProtocols.hashCode();
        hash = 37*hash + overwriteOption.hashCode();
        hash = 37*hash + status.hashCode();
        hash = 37*hash + transferURL.hashCode();
        return hash;
    }

    public boolean equals(Object o) {
        if (o==this) return true;
        if (!(o instanceof PtPChunkData)) return false;
        PtPChunkData cd = (PtPChunkData) o;
        return (primaryKey==cd.primaryKey) &&
            requestToken.equals(cd.requestToken) &&
            toSURL.equals(cd.toSURL) &&
            pinLifetime.equals(cd.pinLifetime) &&
            fileLifetime.equals(cd.fileLifetime) &&
            fileStorageType.equals(cd.fileStorageType) &&
            spaceToken.equals(cd.spaceToken) &&
            expectedFileSize.equals(cd.expectedFileSize) &&
            transferProtocols.equals(cd.transferProtocols) &&
            overwriteOption.equals(cd.overwriteOption) &&
            status.equals(cd.status) &&
            transferURL.equals(cd.transferURL);
    }
}
