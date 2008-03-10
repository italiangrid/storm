/**
 * This class represents the SpaceReservationOutputData associated with the SRM request, that is
 * it contains info about: UserID, spaceType, SizeDesired, SizeGuaranteed,ecc.
 * Number of files progressing, Number of files finished, and whether the request
 * is currently suspended.
 *
 * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.space;

import java.io.Serializable;

import it.grid.storm.srm.types.*;

public class ReserveSpaceOutputData implements Serializable {

    private TSizeInBytes spaceTotal = null;
    private TSizeInBytes spaceGuaranteed = null;
    private TLifeTimeInSeconds spaceLifetime = null;
    private TSpaceToken spaceToken = null;
    private TReturnStatus status = null;
    private TRetentionPolicyInfo retentionPolicyInfo = null;

    public ReserveSpaceOutputData(TReturnStatus status) {
        this.status = status;
    }

    public ReserveSpaceOutputData(TSizeInBytes spaceTotal,
            TSizeInBytes spaceGuaranteed, TLifeTimeInSeconds spaceLifetime,
            TSpaceToken spaceToken, TReturnStatus status)
            throws InvalidReserveSpaceOutputDataAttributesException {
        // boolean ok = spaceGuaranteed!=null && spaceToken!=null &&
        // status!=null;
        boolean ok = status != null;

        if (!ok) {
            throw new InvalidReserveSpaceOutputDataAttributesException(spaceTotal,
                    spaceToken, status);
        }

        this.spaceTotal = spaceTotal;
        this.spaceGuaranteed = spaceGuaranteed;
        this.spaceLifetime = spaceLifetime;
        this.spaceToken = spaceToken;
        this.status = status;
    }

    /**
     * Method that returns the number of files in the SRM request that are
     * currently in progress.
     */
    public TSpaceToken getSpaceToken() {
        return spaceToken;
    }

    /**
     * Method that returns the number of files in the SRM request that are
     * currently finished.
     */
    public TSizeInBytes getGuaranteedSize() {
        return spaceGuaranteed;
    }

    /**
     * Method that tells whether the SRM requst is suspended.
     */
    public TSizeInBytes getTotalSize() {
        return spaceTotal;
    }

    public TLifeTimeInSeconds getLifeTimeInSeconds() {
        return spaceLifetime;
    }


    /**
     * Method that return TReturnStatus status.
     */
    public TReturnStatus getStatus() {
        return status;
    }

    /**
     * Method that returns TRetentionPolicyInfo.
     */
    public TRetentionPolicyInfo getRetentionPolicyInfo() {
        return retentionPolicyInfo;
    }
    
    public void setRetentionPolicyInfo(TRetentionPolicyInfo retentionPolicyInfo) {
        this.retentionPolicyInfo = retentionPolicyInfo;
    }

    /**
     * Method that tells whether the SRM requst is suspended.
     */
    public void setStatus(TStatusCode statusCode)
            throws InvalidTReturnStatusAttributeException {
        this.status.setStatus(statusCode);
    }

    /**
     * Method that tells whether the SRM requst is suspended.
     */
    public void setStatus(TReturnStatus status) {
        this.status = status;
    }

    /**
     * Print
     */
    public void print() {
        System.out.println("****SRM_SR_OutputData******");
        System.out.println("TSizeInBytesTotal: " + spaceTotal);
        System.out.println("TSizeInBytesGuar: " + spaceGuaranteed);
        System.out.println("LifeTimeInSeconds: " + spaceLifetime);
        System.out.println("TSpaceToken: " + spaceToken);
        System.out.println("TReturnStatus: " + status);

    }
}
