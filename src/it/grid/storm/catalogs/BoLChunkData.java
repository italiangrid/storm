package it.grid.storm.catalogs;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a BringOnLineChunkData, that is part of a multifile BringOnLine srm
 * request. It contains data about: the requestToken, the fromSURL, the requested lifeTime of
 * pinning, the TDirOption which tells whether the requested SURL is a directory and if it must be
 * recursed at all levels, as well as the desired number of levels to recurse, the desired
 * transferProtocols in order of preference, the fileSize, and the transferURL for the supplied
 * SURL.
 * 
 * @author CNAF
 * @version 1.0
 * @date Aug 2009
 */
public class BoLChunkData implements ChunkData {
    private static final Logger log = LoggerFactory.getLogger(BoLChunkData.class);

    private long primaryKey = -1; // long representing the primary key for the persistence layer!
    private TRequestToken requestToken; // This is the requestToken of the multifile srm request to
                                        // which this chunk belongs
    private TSURL fromSURL; // SURL that the srm command wants to get
    private TLifeTimeInSeconds lifeTime; // requested lifetime of TURL: it is the pin time!
    private TDirOption dirOption; // specifies if the request regards a directory and related info
    private TURLPrefix desiredProtocols; // list of desired transport protocols for fromSURL
    private TSizeInBytes fileSize; // size of file
    private TReturnStatus status; // return status for this chunk of request
    private TTURL transferURL; // TURL for picking up the requested file
    private int deferredStartTime = -1;

    public BoLChunkData(TRequestToken requestToken, TSURL fromSURL, TLifeTimeInSeconds lifeTime,
            TDirOption dirOption, TURLPrefix desiredProtocols, TSizeInBytes fileSize, TReturnStatus status,
            TTURL transferURL, int deferredStartTime) throws InvalidBoLChunkDataAttributesException {
        
        boolean ok = requestToken != null && fromSURL != null && lifeTime != null && dirOption != null
                && desiredProtocols != null && fileSize != null && status != null && transferURL != null;

        if (!ok) {
            throw new InvalidBoLChunkDataAttributesException(requestToken,
                                                             fromSURL,
                                                             lifeTime,
                                                             dirOption,
                                                             desiredProtocols,
                                                             fileSize,
                                                             status,
                                                             transferURL);
        }

        this.requestToken = requestToken;
        this.fromSURL = fromSURL;
        this.lifeTime = lifeTime;
        this.dirOption = dirOption;
        this.desiredProtocols = desiredProtocols;
        this.fileSize = fileSize;
        this.status = status;
        this.transferURL = transferURL;
        this.deferredStartTime = deferredStartTime;
    }

    /**
     * Method that sets the status of this request to SRM_ABORTED; it needs the explanation String
     * which describes the situation in greater detail; if a null is passed, then an empty String is
     * used as explanation.
     */
    public void changeStatusSRM_ABORTED(String explanation) {
        try {
            if (explanation == null) {
                explanation = "";
            }
            status = new TReturnStatus(TStatusCode.SRM_ABORTED, explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_ABORTED! " + e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_AUTHORIZATION_FAILURE; it needs the
     * explanation String which describes the situation in greater detail; if a null is passed, then
     * an empty String is used as explanation.
     */
    public void changeStatusSRM_AUTHORIZATION_FAILURE(String explanation) {
        try {
            if (explanation == null) {
                explanation = "";
            }
            status = new TReturnStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE, explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_AUTHORIZATION_FAILURE! " + e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_FAILURE; it needs the explanation String
     * which describes the situation in greater detail; if a null is passed, then an empty String is
     * used as explanation.
     */
    public void changeStatusSRM_FAILURE(String explanation) {
        try {
            if (explanation == null) {
                explanation = "";
            }
            status = new TReturnStatus(TStatusCode.SRM_FAILURE, explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_FAILURE! " + e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_FILE_BUSY; it needs the explanation String
     * which describes the situation in greater detail; if a null is passed, then an empty String is
     * used as explanation.
     */
    public void changeStatusSRM_FILE_BUSY(String explanation) {
        try {
            if (explanation == null) {
                explanation = "";
            }
            status = new TReturnStatus(TStatusCode.SRM_FILE_BUSY, explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_FILE_BUSY! " + e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_FILE_PINNED; it needs the explanation
     * String which describes the situation in greater detail; if a null is passed, then an empty
     * String is used as explanation.
     */
    public void changeStatusSRM_FILE_PINNED(String explanation) {
        try {
            if (explanation == null) {
                explanation = "";
            }
            status = new TReturnStatus(TStatusCode.SRM_FILE_PINNED, explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_FILE_PINNED! " + e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_INTERNAL_ERROR; it needs the explanation
     * String which describes the situation in greater detail; if a null is passed, then an empty
     * String is used as explanation.
     */
    public void changeStatusSRM_INTERNAL_ERROR(String explanation) {
        try {
            if (explanation == null) {
                explanation = "";
            }
            status = new TReturnStatus(TStatusCode.SRM_INTERNAL_ERROR, explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_INTERNAL_ERROR! " + e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_INVALID_PATH; it needs the explanation
     * String which describes the situation in greater detail; if a null is passed, then an empty
     * String is used as explanation.
     */
    public void changeStatusSRM_INVALID_PATH(String explanation) {
        try {
            if (explanation == null) {
                explanation = "";
            }
            status = new TReturnStatus(TStatusCode.SRM_INVALID_PATH, explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_INVALID_PATH! " + e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_INVALID_REQUEST; it needs the explanation
     * String which describes the situation in greater detail; if a null is passed, then an empty
     * String is used as explanation.
     */
    public void changeStatusSRM_INVALID_REQUEST(String explanation) {
        try {
            if (explanation == null) {
                explanation = "";
            }
            status = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST, explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_INVALID_REQUEST! " + e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_NOT_SUPPORTED; it needs the explanation
     * String which describes the situation in greater detail; if a null is passed, then an empty
     * String is used as explanation.
     */
    public void changeStatusSRM_NOT_SUPPORTED(String explanation) {
        try {
            if (explanation == null) {
                explanation = "";
            }
            status = new TReturnStatus(TStatusCode.SRM_NOT_SUPPORTED, explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_NOT_SUPPORTED! " + e);
        }
    }

    /**
     * Method that sets the status of this request to SRM_REQUEST_INPROGRESS; it needs the
     * explanation String which describes the situation in greater detail; if a null is passed, then
     * an empty String is used as explanation.
     */
    public void changeStatusSRM_REQUEST_INPROGRESS(String explanation) {
        try {
            if (explanation == null) {
                explanation = "";
            }
            status = new TReturnStatus(TStatusCode.SRM_REQUEST_INPROGRESS, explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_REQUEST_INPROGRESS! " + e);
        }
    }
    
    /**
     * Method that sets the status of this request to SRM_REQUEST_QUEUED; it needs the explanation
     * String which describes the situation in greater detail; if a null is passed, then an empty
     * String is used as explanation.
     */
    public void changeStatusSRM_REQUEST_QUEUED(String explanation) {
        try {
            if (explanation == null) {
                explanation = "";
            }
            status = new TReturnStatus(TStatusCode.SRM_REQUEST_QUEUED, explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.debug("UNEXPECTED ERROR! Unable to set SRM request status to SRM_REQUEST_QUEUED! " + e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BoLChunkData)) {
            return false;
        }
        BoLChunkData cd = (BoLChunkData) o;
        return (primaryKey == cd.primaryKey) && requestToken.equals(cd.requestToken)
                && fromSURL.equals(cd.fromSURL) && lifeTime.equals(cd.lifeTime)
                && dirOption.equals(cd.dirOption) && desiredProtocols.equals(cd.desiredProtocols)
                && fileSize.equals(cd.fileSize) && status.equals(cd.status)
                && transferURL.equals(cd.transferURL) && (deferredStartTime == cd.deferredStartTime);
    }

    public int getDeferredStartTime() {
        return deferredStartTime;
    }

    /**
     * Method that returns a TURLPrefix containing the transfer protocols desired for this chunk of
     * the srm request.
     */
    public TURLPrefix getDesiredProtocols() {
        return desiredProtocols;
    }

    /**
     * Method that returns the dirOption specified in the srm request.
     */
    public TDirOption getDirOption() {
        return dirOption;
    }

    /**
     * Method that returns the file size for this chunk of the srm request.
     */
    public TSizeInBytes getFileSize() {
        return fileSize;
    }

    /**
     * Method that returns the fromSURL of the srm request to which this chunk belongs.
     */
    public TSURL getFromSURL() {
        return fromSURL;
    }

    /**
     * Method that returns the requested pin life time for this chunk of the srm request.
     */
    public TLifeTimeInSeconds getLifeTime() {
        return lifeTime;
    }

    /**
     * Method that returns the requestToken of the srm request to which this chunk belongs.
     */
    public TRequestToken getRequestToken() {
        return requestToken;
    }

    /**
     * Method that returns the status for this chunk of the srm request.
     */
    public TReturnStatus getStatus() {
        return status;
    }

    /**
     * Method that returns the TURL for this chunk of the srm request.
     */
    public TTURL getTransferURL() {
        return transferURL;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 37 * hash + new Long(primaryKey).hashCode();
        hash = 37 * hash + requestToken.hashCode();
        hash = 37 * hash + fromSURL.hashCode();
        hash = 37 * hash + lifeTime.hashCode();
        hash = 37 * hash + dirOption.hashCode();
        hash = 37 * hash + desiredProtocols.hashCode();
        hash = 37 * hash + fileSize.hashCode();
        hash = 37 * hash + status.hashCode();
        hash = 37 * hash + transferURL.hashCode();
        hash = 37 * hash + new Integer(deferredStartTime).hashCode();
        return hash;
    }

    /**
     * Method used to get the primary key used in the persistence layer!
     */
    public long primaryKey() {
        return primaryKey;
    }

    public void setDeferredStartTime(int deferredStartTime) {
        this.deferredStartTime = deferredStartTime;
    }

    /**
     * Method used to set the size of the file corresponding to the requested SURL. If the supplied
     * TSizeInByte is null, then nothing gets set!
     */
    public void setFileSize(TSizeInBytes size) {
        if (size != null) {
            fileSize = size;
        }
    }

    /**
     * Method used to set the primary key to be used in the persistence layer!
     */
    public void setPrimaryKey(long l) {
        primaryKey = l;
    }

    /**
     * Method used to set the Status associated to this chunk. If status is null, then nothing gets
     * set!
     */
    public void setStatus(TReturnStatus newstat) {
        if (newstat != null) {
            status = newstat;
        }
    }

    /**
     * Method used to set the transferURL associated to the SURL of this chunk. If TTURL is null,
     * then nothing gets set!
     */
    public void setTransferURL(TTURL turl) {
        if (turl != null) {
            transferURL = turl;
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("BoLChunkData\n");
        sb.append("primaryKey=");
        sb.append(primaryKey);
        sb.append("; ");
        sb.append("RequestToken=");
        sb.append(requestToken);
        sb.append("; ");
        sb.append("fromSURL=");
        sb.append(fromSURL);
        sb.append("; ");
        sb.append("lifeTime=");
        sb.append(lifeTime);
        sb.append("; ");
        sb.append("dirOption=");
        sb.append(dirOption);
        sb.append("; ");
        sb.append("desiredProtocols=");
        sb.append(desiredProtocols);
        sb.append("; ");
        sb.append("fileSize=");
        sb.append(fileSize);
        sb.append("; ");
        sb.append("status=");
        sb.append(status);
        sb.append("; ");
        sb.append("transferURL=");
        sb.append(transferURL);
        return sb.toString();
    }
}
