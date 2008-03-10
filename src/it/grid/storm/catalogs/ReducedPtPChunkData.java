package it.grid.storm.catalogs;

import org.apache.log4j.Logger;

import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;

/**
 * This class represents a ReducedPrepareToPutChunkData, that is part of a
 * multifile PrepareToPut srm request. It is closely related to PtPChunkData
 * but it is called Reduced because it only contains the toSURL, the current
 * TReturnStatus, the TFileStorageType, the FileLifeTime in case of Volatile,
 * the VomsGridUser limited to the DN, and the primary key of the request.
 *
 * This class is intended to be used by srmPutDone, where only a limited
 * amount of information is needed instead of full blown PtPChunkData. It is also
 * used by the automatic handlnig of non invoked srmPutDone, during transition to
 * SRM_FILE_LIFETIME_EXPIRED.
 *
 * @author  EGRID - ICTP Trieste
 * @date    January, 2007
 * @version 2.0
 */
public class ReducedPtPChunkData {
    private static final Logger log = Logger.getLogger("asynch"); //Logger of error messages! Common to all Asynch package!

    private long primaryKey = -1; //long representing the primary key for the persistence layer!
    private TSURL toSURL;       //SURL that the srm command wants to get
    private TReturnStatus status; //return status for this chunk of request
    private TFileStorageType fileStorageType; //fileStorageType of this shunk of the request
    private TLifeTimeInSeconds fileLifetime;  //requested lifetime for SURL in case of Volatile entry.

    public ReducedPtPChunkData(TSURL toSURL, TReturnStatus status, TFileStorageType fileStorageType, TLifeTimeInSeconds fileLifetime)
        throws InvalidReducedPtPChunkDataAttributesException {
        boolean ok = status!=null &&
            toSURL!=null &&
            fileStorageType!=null &&
            fileLifetime!=null;
        if (!ok) throw new InvalidReducedPtPChunkDataAttributesException(toSURL,status,fileStorageType,fileLifetime);
        this.toSURL = toSURL;
        this.status = status;
        this.fileStorageType = fileStorageType;
        this.fileLifetime = fileLifetime;
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
     * Method that returns the toSURL of the srm request to which this chunk belongs.
     */
    public TSURL toSURL() {return toSURL;}

    /**
     * Method that returns the status for this chunk of the srm request.
     */
    public TReturnStatus status() {return status;}

    /**
     * Method that returns the TFileStorageType of the srm request to which this chunk belongs.
     */
    public TFileStorageType fileStorageType() {return fileStorageType;}

    /**
     * Method that returns the fileLifetime of the srm request to which this chunk belongs.
     */
    public TLifeTimeInSeconds fileLifetime() {return fileLifetime;}







    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ReducedPtPChunkData\n");
        sb.append("primaryKey="); sb.append(primaryKey); sb.append("; ");
        sb.append("toSURL="); sb.append(toSURL); sb.append("; ");
        sb.append("status="); sb.append(status); sb.append(";");
        sb.append("fileStorageType="); sb.append(fileStorageType); sb.append(";");
        sb.append("fileLifetime="); sb.append(fileLifetime); sb.append(".");
        return sb.toString();
    }

    public int hashCode() {
        int hash = 17;
        hash = 37*hash + new Long(primaryKey).hashCode();
        hash = 37*hash + toSURL.hashCode();
        hash = 37*hash + status.hashCode();
        hash = 37*hash + fileStorageType.hashCode();
        hash = 37*hash + fileLifetime.hashCode();
        return hash;
    }

    public boolean equals(Object o) {
        if (o==this) return true;
        if (!(o instanceof ReducedPtPChunkData)) return false;
        ReducedPtPChunkData cd = (ReducedPtPChunkData) o;
        return (primaryKey==cd.primaryKey) &&
            toSURL.equals(cd.toSURL) &&
            status.equals(cd.status) &&
            fileStorageType.equals(cd.fileStorageType) &&
            fileLifetime.equals(cd.fileLifetime);
    }

}
