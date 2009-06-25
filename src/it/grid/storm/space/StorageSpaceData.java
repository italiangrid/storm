/**
 * This class represents the SpaceReservationData associated with the SRM request, that is
 * it contains info about: UserID, spaceType, SizeDesired, SizeGuaranteed,ecc.
 * and must be storef into persistence.
 *
 * @author  Magnoni Luca / Riccardo Zappi
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */



package it.grid.storm.space;

import it.grid.storm.catalogs.InvalidSpaceDataAttributesException;
import it.grid.storm.common.types.InvalidPFNAttributeException;
import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.InvalidTLifeTimeAttributeException;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.InvalidTSpaceTokenAttributesException;
import it.grid.storm.srm.types.InvalidTUserIDAttributeException;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TSpaceType;
import it.grid.storm.srm.types.TStorageSystemInfo;
import it.grid.storm.srm.types.TUserID;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageSpaceData {

    private GridUserInterface auth = null;
    private TSpaceType spaceType = null;
    private TSpaceToken spaceToken = null;
    private PFN spaceFileName = null;
    private String spaceTokenAlias = null;
    private TSizeInBytes spaceDesired = null;
    private TSizeInBytes spaceGuaranteed = null;
    private TSizeInBytes actualSpaceSize = null;
    private TSizeInBytes unusedSpaceSize = null;
    private TLifeTimeInSeconds spaceLifetime = null;
    private Date date = null;
    private TStorageSystemInfo storageInfo = null;

    private static final Logger log = LoggerFactory.getLogger(StorageSpaceData.class);

    public StorageSpaceData() {}

    public StorageSpaceData(GridUserInterface auth, TSpaceType spaceType, String spaceTokenAlias,
            TSizeInBytes spaceDesired,
            TSizeInBytes spaceGuaranteed, TLifeTimeInSeconds spaceLifetime,
            TStorageSystemInfo storageInfo,
            Date date, PFN spaceFileName) throws InvalidSpaceDataAttributesException {

        boolean ok = (spaceType != null && ( (auth != null) || (spaceType == TSpaceType.VOSPACE)) && spaceTokenAlias != null);
        log.debug("Storage Space Data - User identity : " + auth);
        log.debug("Storage Space Data - Space Type : " + spaceType);
        log.debug("Storage Space Data - Space Token Alias : " + spaceTokenAlias);

        if (!ok) {
            throw new InvalidSpaceDataAttributesException(auth);
        }

        this.auth = auth;
        this.spaceType = spaceType;
        try {
            this.spaceToken = TSpaceToken.makeGUID_Token();
        }
        catch (InvalidTSpaceTokenAttributesException ex) {
            log.error("Exception occurred while trying to generate a Token with GUID", ex);
        }
        this.spaceTokenAlias = spaceTokenAlias;
        this.spaceDesired = spaceDesired;
        this.spaceGuaranteed = spaceGuaranteed;
        this.unusedSpaceSize = spaceDesired;
        this.spaceLifetime = spaceLifetime;
        this.storageInfo = storageInfo;
        this.date = date;
        this.spaceFileName = spaceFileName;
    }

    /**
     * Constructor from Persistence Object Model
     *
     * @param spaceData SpaceData
     */
    public StorageSpaceData(it.grid.storm.persistence.model.StorageSpaceTO ssTO) {
        //Ownership of Storage Space
        if (ssTO == null) {
            log.debug("StorageSpaceData - ssTO=NULL");
            this.auth = null;
        }
        else {
            this.auth = ssTO.getOwner();
            if (this.auth != null) {
                log.debug("StorageSpaceData - Owner: " + this.auth.toString());
            }
            else {
                log.debug("StorageSpaceData - Owner: NULL");
            }
        }

        //Type of Storage Space
        this.spaceType = TSpaceType.getTSpaceType(ssTO.getSpaceType());
        if (this.spaceType != null) {
            log.debug("StorageSpaceData - spaceType: " + this.spaceType);
        }
        else {
            log.debug("StorageSpaceData - spaceType: NULL");
        }

        //Token of Storage Space
        try {
            this.spaceToken = TSpaceToken.make(ssTO.getSpaceToken());
            if (this.spaceToken != null) {
                log.debug("StorageSpaceData - spaceToken: " + this.spaceToken);
            }
            else {
                log.debug("StorageSpaceData - spaceToken: NULL");
            }
        }
        catch (InvalidTSpaceTokenAttributesException ex) {
            log.error("Error while constructing SpaceType", ex);
        }

        //Alias for Storage Space (Human readable)
        this.spaceTokenAlias = ssTO.getAlias();
        if (this.spaceTokenAlias != null) {
            log.debug("StorageSpaceData - spaceTokenAlias: " + this.spaceTokenAlias);
        }
        else {
            log.debug("StorageSpaceData - spaceTokenAlias: NULL");
        }

        //Space Desidered for Storage Space
        try {
            //CHECK HERE! I've used default Size Unit as BYTE!

            log.debug("StorageSpaceData - size:"+ssTO.getTotalSize());
            this.spaceDesired = TSizeInBytes.make(ssTO.getTotalSize(), SizeUnit.BYTES);
            if (this.spaceDesired != null) {
                log.debug("StorageSpaceData - spaceDesired: " + this.spaceDesired);
            }
            else {
                log.debug("StorageSpaceData - spaceDesired: NULL");
            }
        }
        catch (InvalidTSizeAttributesException ex1) {
            log.error("Error while constructing SpaceDesidered", ex1);
        }

        //Space Guaranted for Storage Space
        try {
            //CHECK HERE! I've used default Size Unit as BYTE!
            this.spaceGuaranteed = TSizeInBytes.make(ssTO.getGuaranteedSize(), SizeUnit.BYTES);
            if (this.spaceGuaranteed != null) {
                log.debug("StorageSpaceData - spaceGuaranteed: " + this.spaceGuaranteed);
            }
            else {
                log.debug("StorageSpaceData - spaceGuaranteed: NULL");
            }
        }
        catch (InvalidTSizeAttributesException ex2) {
            log.error("Error while constructing SpaceGuaranteed", ex2);
        }

        // Unused space
        try {
            //CHECK HERE! I've used default Size Unit as BYTE!
            this.unusedSpaceSize = TSizeInBytes.make(ssTO.getUnusedSize(), SizeUnit.BYTES);
            if (this.unusedSpaceSize != null) {
                log.debug("StorageSpaceData - unusedSpaceSize: " + this.unusedSpaceSize);
            }
            else {
                log.debug("StorageSpaceData - unusedSpaceSize: NULL");
            }
        }
        catch (InvalidTSizeAttributesException ex3) {
            log.error("Error while constructing unusedSpaceSize", ex3);
        }

        //Life Time for Storage Space.
        try {
            //CHECK HERE! I've used default TIME Unit as SECOND!
            this.spaceLifetime = TLifeTimeInSeconds.make(ssTO.getLifetime(), TimeUnit.SECONDS);
            if (this.spaceLifetime != null) {
                log.debug("StorageSpaceData - spaceLifetime: " + this.spaceLifetime);
            }
            else {
                log.debug("StorageSpaceData - spaceLifetime: NULL");
            }
        }
        catch (InvalidTLifeTimeAttributeException ex3) {
            log.error("Error while constructing Space Life Time", ex3);
        }

        //Storage System Info.
        this.storageInfo = new TStorageSystemInfo("", true);

        //Creation time of Storage Space
        this.date = ssTO.getCreated();
        if (this.date != null) {
            log.debug("StorageSpaceData - date: " + this.date);
        }
        else {
            log.debug("StorageSpaceData - date: NULL");
        }

        //Storage Space File Name
        try {
            this.spaceFileName = PFN.make(ssTO.getSpaceFile());
            if (this.spaceFileName != null) {
                log.debug("StorageSpaceData - spaceFileName: " + this.spaceFileName);
            }
            else {
                log.debug("StorageSpaceData - spaceFileName: NULL");
            }
        }
        catch (InvalidPFNAttributeException ex4) {
            log.error("Error while constructing Storage Space File Name", ex4);
        }

    }

    /**
     * Method that returns UserID specify in SRM request.
     */

    public GridUserInterface getUser() {
        return auth;
    }

    public void setUser(GridUserInterface user) {
        this.auth = user;
    }

    /**
     * Method that returns type of space specified in SRM request.
     */

    public TSpaceType getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(TSpaceType spaceType) {
        this.spaceType = spaceType;
    }

    /**
     * Method that returns the number of files in the SRM request that are currently
     * in progress.
     */

    public String getSpaceTokenAlias() {
        return spaceTokenAlias;
    }

    public void setSpaceTokenAlias(String alias) {
        this.spaceTokenAlias = alias;
    }

    public TSizeInBytes getGuaranteedSize() {
        return spaceGuaranteed;
    }

    public void setGuaranteedSize(TSizeInBytes size) {
        this.spaceGuaranteed = size;
    }

    public TSizeInBytes getDesiredSize() {
        return spaceDesired;
    }

    public void setDesiredSize(TSizeInBytes dsize) {
        this.spaceDesired = dsize;
    }

    public TLifeTimeInSeconds getLifeTime() {
        return spaceLifetime;
    }

    public void getLifeTime(TLifeTimeInSeconds time) {
        this.spaceLifetime = time;
    }

    /**
     *
     * @return TSpaceToken
     */
    public TSpaceToken getSpaceToken() {
        return spaceToken;
    }

    public void setSpaceToken(TSpaceToken token) {
        this.spaceToken = token;
    }

    public TUserID getUserID() {
        try {
            //Create new UserID with certificate subject + VO String
            TUserID userID = null;
            if (auth != null) {
                userID = new TUserID(auth.getDn());
            }
            else {
                userID = new TUserID("EMTPY");
            }
            return userID;
        }
        catch (InvalidTUserIDAttributeException e) {
            log.error("StorageSpaceData: Invalid UserID.",e);
        }
        return null;
    }


    public TSizeInBytes getTotalSize() {
        return spaceDesired;
    }

    public void setTotalSize(TSizeInBytes size) {
        this.spaceDesired = size;
    }

    public String getSpaceFileNameString() {
        return spaceFileName.getValue();
    }

    public void setSpaceFileName(PFN spaceFN) {
        this.spaceFileName = spaceFN;
    }

    public PFN getSpaceFileName() {
        return spaceFileName;
    }

    public TSizeInBytes getActualUsedSpace() {
        return actualSpaceSize;
    }

    public void setActualUsedSpace(TSizeInBytes size) {
        this.actualSpaceSize = size;
    }

    public TSizeInBytes getUnusedSizes() {
        return unusedSpaceSize;
    }

    public void setUnusedSize(TSizeInBytes size) {
        this.unusedSpaceSize = size;
    }

    public Date getDate() {
        return this.date;
    }

    /**
     * This method is used to verify if the Space Reservation is expired, so
     * the lifetime is no more valid.
     * @return true if expired, false otherwise.
     */
    public boolean isExpired() {
        Date currentTime = new Date();
        //Get the expiration time in millisec getting the creation time plus the lifetime in millisec
        long expirationTimeInMillisec = this.date.getTime() + this.spaceLifetime.value() * 1000;
        Date expirationTime = new Date(expirationTimeInMillisec);
        return expirationTime.before(currentTime);
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" OWNER GRID USER = " + auth);
        sb.append("\n");
        sb.append(" ALIAS NAME      = " + spaceTokenAlias);
        sb.append("\n");
        sb.append(" SPACE TYPE      = " + spaceType);
        sb.append("\n");
        sb.append(" SPACE TOKEN     = " + spaceToken);
        sb.append("\n");
        sb.append(" SPACE FILE NAME = " + spaceFileName);
        sb.append("\n");
        sb.append(" CREATED         = " + date);
        sb.append("\n");
        sb.append(" DESIDERED SIZE  = " + spaceDesired);
        sb.append("\n");
        sb.append(" GUARANTEED SIZE = " + spaceGuaranteed);
        sb.append("\n");
        sb.append(" LIFETIME (sec)  = " + spaceLifetime);
        sb.append("\n");
        sb.append(" STORAGE INFO    = " + storageInfo);
        sb.append("\n");
        return sb.toString();
    }

}
