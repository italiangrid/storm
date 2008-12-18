package it.grid.storm.synchcall.data.space;

import java.io.Serializable;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.ArrayOfTSizeInBytes;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRetentionPolicyInfo;
import it.grid.storm.srm.types.TTransferParameters;
import it.grid.storm.synchcall.data.InputData;

/**
 * This class represents the SpaceReservationData associated with the SRM request, that is
 * it contains info about: UserID, spaceType, SizeDesired, SizeGuaranteed,ecc.
 * Number of files progressing, Number of files finished, and whether the request
 * is currently suspended.
 *
 * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */

public class ReserveSpaceInputData implements Serializable, InputData
{
    private GridUserInterface    auth                     = null;
    private String               spaceTokenAlias          = null;
    private TRetentionPolicyInfo retentionPolicyInfo      = null;
    private TSizeInBytes         spaceDesired             = TSizeInBytes.makeEmpty();
    private TSizeInBytes         spaceGuaranteed          = TSizeInBytes.makeEmpty();
    private TLifeTimeInSeconds   spaceLifetime            = null;
    private ArrayOfTSizeInBytes  arrayOfExpectedFileSizes = null;
    private ArrayOfTExtraInfo    storageSystemInfo        = null;
    private TTransferParameters  transferParameters       = null;

    public ReserveSpaceInputData() {}

    public ReserveSpaceInputData(GridUserInterface auth, String spaceTokenAlias,
            TRetentionPolicyInfo retentionPolicyInfo, TSizeInBytes spaceDesired, TSizeInBytes spaceGuaranteed,
            TLifeTimeInSeconds spaceLifetime, ArrayOfTSizeInBytes arrayOfExpectedFileSizes,
            ArrayOfTExtraInfo storageSystemInfo, TTransferParameters transferParameters)
            throws InvalidReserveSpaceInputDataAttributesException
    {
        boolean ok = (auth != null) && (spaceDesired != null);

        if (!ok)
            throw new InvalidReserveSpaceInputDataAttributesException(auth, spaceDesired, retentionPolicyInfo);

        this.auth = auth;
        this.spaceTokenAlias = spaceTokenAlias;
        this.retentionPolicyInfo = retentionPolicyInfo;
        this.spaceDesired = spaceDesired;
        this.spaceGuaranteed = spaceGuaranteed;
        this.spaceLifetime = spaceLifetime;
        this.arrayOfExpectedFileSizes = arrayOfExpectedFileSizes;
        this.storageSystemInfo = storageSystemInfo;
        this.transferParameters = transferParameters;
    }

    /**
     * Method that returns UserID specify in SRM request.
     */
    public GridUserInterface getUser()
    {
        return auth;
    }

    /**
     * @param user GridUserInterface
     */
    public void setUser(GridUserInterface user)
    {
        this.auth = user;
    }

    /**
     * Method that returns the space token alias
     */
    public String getSpaceTokenAlias()
    {
        return spaceTokenAlias;
    }

    public void setSpaceTokenAlias(String description)
    {
        this.spaceTokenAlias = description;
    }

    /**
     * Returns the retentionPolicyInfo field.
     * @return retentionPolicyInfo of type TRetentionPolicyInfo
     */
    public TRetentionPolicyInfo getRetentionPolicyInfo()
    {
        return retentionPolicyInfo;
    }

    /**
     * Set the retentionPolicyInfo field
     * @param retentionPolicyInfo of type TRetentionPolicyInfo
     */
    public void setRetentionPolicyInfo(TRetentionPolicyInfo retentionPolicyInfo)
    {
        this.retentionPolicyInfo = retentionPolicyInfo;
    }

    /**
     * Returns the desired size of total space to reserve.
     * @return a TSizeInBytes value.
     */
    public TSizeInBytes getDesiredSize()
    {
        return spaceDesired;
    }

    /**
     * Set the desired size of total space field
     * @param size of type TSizeInBytes
     */
    public void setDesiredSize(TSizeInBytes size)
    {
        this.spaceDesired = size;
    }

    /**
     * Method that returns the guaranteed size.
     */
    public TSizeInBytes getGuaranteedSize()
    {
        return spaceGuaranteed;
    }

    public void setGuaranteedSize(TSizeInBytes size)
    {
        this.spaceGuaranteed = size;
    }

    /**
     * Method that return Lifetime
     */
    public TLifeTimeInSeconds getLifetime()
    {
        return spaceLifetime;
    }

    public void setLifetime(TLifeTimeInSeconds lifetime)
    {
        this.spaceLifetime = lifetime;
    }

    /**
     * Method that return arrayOfExpectedFileSizes
     */
    public ArrayOfTSizeInBytes getArrayOfExpectedFileSizes()
    {
        return arrayOfExpectedFileSizes;
    }

    public void setArrayOfExpectedFileSizes(ArrayOfTSizeInBytes arrayOfExpectedFileSizes)
    {
        this.arrayOfExpectedFileSizes = arrayOfExpectedFileSizes;
    }

    /**
     * Method that return storageSystemInfo
     */
    public ArrayOfTExtraInfo getStorageSystemInfo()
    {
        return storageSystemInfo;
    }

    public void setStorageSystemInfo(ArrayOfTExtraInfo storageSystemInfo)
    {
        this.storageSystemInfo = storageSystemInfo;
    }

    /**
     * Method that return transferParameters
     */
    public TTransferParameters getTransferParameters()
    {
        return transferParameters;
    }

    public void setTransferParameters(TTransferParameters transferParameters)
    {
        this.transferParameters = transferParameters;
    }

    public void print()
    {
    //        System.out.println(" GridUserInterface: " + auth);
    //        System.out.println(" TSpaceType:" + spaceType);
    //        System.out.println(" SpaceTokeAlias: " + spaceTokenAlias);
    //        System.out.println(" TSizeInBytes: " + spaceDesired);
    //        System.out.println(" TSizeInBytes: " + spaceGuaranteed);
    //        System.out.println(" LifeTimeInSeconds: " + spaceLifetime);
    //        System.out.println(" TStorageSystemInfo:" + storageInfo);
    }

}
