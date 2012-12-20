/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This class represents the SpaceReservationData associated with the SRM request, that is
 * it contains info about: UserID, spaceType, SizeDesired, SizeGuaranteed,ecc.
 * and must be storef into persistence.
 * 
 * @author Magnoni Luca / Riccardo Zappi
 * @author Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.space;


import it.grid.storm.catalogs.InvalidSpaceDataAttributesException;
import it.grid.storm.common.types.InvalidPFNAttributeException;
import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.common.types.VO;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.persistence.model.StorageSpaceTO;
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


    private GridUserInterface owner = null;
    private TSpaceType spaceType = null;
    private TSpaceToken spaceToken = null;
    private PFN spaceFileName = null;
    private String spaceTokenAlias = null;
    private TLifeTimeInSeconds spaceLifetime = null;
    private TStorageSystemInfo storageInfo = null;
    private Date creationDate = null;

    private TSizeInBytes totalSpaceSize = null; // total size
    private TSizeInBytes totalGuaranteedSize = null; // total guaranteed size

    private TSizeInBytes availableSpaceSize = null; // available size
    private boolean availableSpaceSizeForced = false; // available size = total - busy
    private TSizeInBytes usedSpaceSize = null; // used size
    // For now do not consider the reserved space, a better management is needed
// private TSizeInBytes freeSpaceSize = null; // free size = total - used - reserved
    private TSizeInBytes freeSpaceSize = null; // free size = total - used
    private boolean freeSpaceSizeForced = false;
    private TSizeInBytes unavailableSpaceSize = null;
    private TSizeInBytes reservedSpaceSize = null; // reserved size
    private TSizeInBytes busySpaceSize = null; // busy size = used + reserved + unavailable
    private boolean busySpaceSizeForced = false;

    private static final Logger log = LoggerFactory.getLogger(StorageSpaceData.class);
    
    public StorageSpaceData() {
    }


    /**
     * Used to create a new Storage Space entity.
     * It could be a Storage Area Space (static reservation) or
     * a Space Reservation (dynamic space reservation)
     * 
     * @param guOwner
     * @param spaceType
     * @param spaceTokenAlias
     * @param totalDesiredSize
     * @param totalGuaranteedSize
     * @param spaceLifetime
     * @param storageInfo
     * @param date
     * @param spaceFileName
     * @throws InvalidSpaceDataAttributesException
     */
    public StorageSpaceData(GridUserInterface guOwner, TSpaceType spaceType, String spaceTokenAlias, TSizeInBytes totalDesiredSize,
            TSizeInBytes guaranteedSize, TLifeTimeInSeconds spaceLifetime, TStorageSystemInfo storageInfo, Date date, PFN spaceFileName)
        throws InvalidSpaceDataAttributesException {

        boolean ok = (spaceType != null && ((guOwner != null) || (spaceType == TSpaceType.VOSPACE)) && spaceTokenAlias != null);
        log.debug("Storage Space Data - User identity : " + guOwner);
        log.debug("Storage Space Data - Space Type : " + spaceType);
        log.debug("Storage Space Data - Space Token Alias : " + spaceTokenAlias);

        if (!ok) {
            throw new InvalidSpaceDataAttributesException(guOwner);
        }

        this.owner = guOwner;
        this.spaceType = spaceType;
        try {
            this.spaceToken = TSpaceToken.makeGUID_Token();
        }
        catch (InvalidTSpaceTokenAttributesException ex) {
            log.error("Exception occurred while trying to generate a Token with GUID", ex);
        }
        this.spaceTokenAlias = spaceTokenAlias;
        this.totalSpaceSize = totalDesiredSize;
        this.totalGuaranteedSize = guaranteedSize;

        this.spaceLifetime = spaceLifetime;
        this.storageInfo = storageInfo;
        this.creationDate = date;
        this.spaceFileName = spaceFileName;
        this.usedSpaceSize = TSizeInBytes.makeEmpty();
        this.reservedSpaceSize = TSizeInBytes.makeEmpty();
        this.unavailableSpaceSize = TSizeInBytes.makeEmpty();

// this.availableSpaceSize = totalDesiredSize;
// this.busySpaceSize = TSizeInBytes.makeEmpty();
// //Update Busy Space
// updateBusySpaceSize();
// //Update Free Space
// updateFreeSpaceSize();
    }


    /**
     * Constructor from Persistence Object Model
     * 
     * @param spaceData SpaceData
     */
    public StorageSpaceData(StorageSpaceTO ssTO) throws IllegalArgumentException {
        if (ssTO == null) {
            log.error("Unable to create StorageSpaceData object, provided StorageSpaceTO parameter is null");
            throw new IllegalArgumentException("Received null argument");
        }
        else {
            // Ownership of Storage Space
            if (!(ssTO.getOwnerName() == null || ssTO.getVoName() == null || ssTO.getVoName().equals(VO.NO_VO.getValue()))) {
                try
                {
                    this.owner = GridUserManager.makeVOMSGridUser(ssTO.getOwnerName(), ssTO.getVoName());
                }
                catch (IllegalArgumentException e)
                {
                    log.error("Unexpected error on voms grid user creation. Contact StoRM Support : IllegalArgumentException "
                              + e.getMessage());
                    throw e;
                }
            }
            else {
                this.owner = GridUserManager.makeGridUser(ssTO.getOwnerName());
            }
            if (this.owner != null) {
                log.trace("StorageSpaceData - Owner: " + this.owner.toString());
            }
            else {
                log.trace("StorageSpaceData - Owner: NULL");
            }
            // TYPE of Storage Space
            this.spaceType = TSpaceType.getTSpaceType(ssTO.getSpaceType());
            if (this.spaceType != null) {
                log.trace("StorageSpaceData - spaceType: " + this.spaceType);
            }
            else {
                log.trace("StorageSpaceData - spaceType: NULL");
            }
            // ALIAS for Storage Space (Human readable)
            this.spaceTokenAlias = ssTO.getAlias();
            if (this.spaceTokenAlias != null) {
                log.trace("StorageSpaceData - spaceTokenAlias: " + this.spaceTokenAlias);
            }
            else {
                log.trace("StorageSpaceData - spaceTokenAlias: NULL");
            }
            // TOKEN of Storage Space
            try {
                this.spaceToken = TSpaceToken.make(ssTO.getSpaceToken());
            }
            catch (InvalidTSpaceTokenAttributesException ex) {
                log.error("Error while constructing TSpaceToken", ex);
            }
            if (this.spaceToken != null) {
                log.trace("StorageSpaceData - spaceToken: " + this.spaceToken);
            }

            // Storage Space File Name
            try {
                this.spaceFileName = PFN.make(ssTO.getSpaceFile());
            }
            catch (InvalidPFNAttributeException e) {
                log.error("Error while constructing Storage Space File Name", e);
            }
            if (this.spaceFileName != null) {
                log.trace("StorageSpaceData - spaceFileName: " + this.spaceFileName);
            }

            // Life Time for Storage Space.
            try {
                this.spaceLifetime = TLifeTimeInSeconds.make(ssTO.getLifetime(), TimeUnit.SECONDS);
            }
            catch (InvalidTLifeTimeAttributeException e) {
                log.error("Error while constructing Space Life Time", e);
            }
            if (this.spaceLifetime != null) {
                log.trace("StorageSpaceData - spaceLifetime: " + this.spaceLifetime);
            }
            // Storage System Info.
            // this.storageInfo = new TStorageSystemInfo("", true);
            this.storageInfo = TStorageSystemInfo.make(ssTO.getStorageInfo());
            if (this.storageInfo.getValue() != null) {
                log.trace("StorageSpaceData - storageInfo: " + this.storageInfo);
            }
            else {
                log.trace("StorageSpaceData - storageInfo: NULL");
            }
            // Creation time of Storage Space
            this.creationDate = ssTO.getCreated();
            if (this.creationDate != null) {
                log.trace("StorageSpaceData - date: " + this.creationDate);
            }
            else {
                log.trace("StorageSpaceData - date: NULL");
            }
            // Space TOTAL Desired for Storage Space

            log.debug("StorageSpaceData - TOTAL (Desired) size:" + ssTO.getTotalSize());
            this.totalSpaceSize = TSizeInBytes.makeEmpty();
            if (ssTO.getTotalSize() >= 0) {
                try {
                    this.totalSpaceSize = TSizeInBytes.make(ssTO.getTotalSize(), SizeUnit.BYTES);
                    log.trace("StorageSpaceData - TotalSize (desired): " + this.totalSpaceSize);
                }
                catch (InvalidTSizeAttributesException ex1) {
                    log.error("Error while constructing TotalSize (desired)", ex1);
                }
            }
            else {
                log.trace("StorageSpaceData - TotalSize (desired): EMPTY " + this.totalSpaceSize);
            }
            // Space TOTAL Guaranteed for Storage Space

            log.debug("StorageSpaceData - TOTAL (Guaranteed) size:" + ssTO.getGuaranteedSize());
            this.setTotalGuaranteedSize(TSizeInBytes.makeEmpty());
            if (ssTO.getGuaranteedSize() >= 0) {
                try {
                    this.totalGuaranteedSize = TSizeInBytes.make(ssTO.getGuaranteedSize(), SizeUnit.BYTES);
                    log.trace("StorageSpaceData - TotalSize (guaranteed): " + this.totalGuaranteedSize);
                }
                catch (InvalidTSizeAttributesException ex2) {
                    log.error("Error while constructing SpaceGuaranteed", ex2);
                }
            }
            else {
                // log.debug("StorageSpaceData - TotalSize (guaranteed): EMPTY " + this.reservedSpaceSize);
                log.trace("StorageSpaceData - TotalSize (guaranteed): EMPTY " + this.totalGuaranteedSize);
            }

            // AVAILABLE space
            log.trace("StorageSpaceData - AVAILABLE size:" + ssTO.getAvailableSize());
            // this.availableSpaceSize = TSizeInBytes.makeEmpty();
            this.forceAvailableSpaceSize(TSizeInBytes.makeEmpty());
            if (ssTO.getAvailableSize() >= 0) {
                try {
                    this.forceAvailableSpaceSize(TSizeInBytes.make(ssTO.getAvailableSize(), SizeUnit.BYTES));
                    log.trace("StorageSpaceData - AVAILABLE size : " + this.getAvailableSpaceSize());
                }
                catch (InvalidTSizeAttributesException ex3) {
                    log.error("Error while constructing AvailableSpaceSize", ex3);
                }
            }
            else {
                log.trace("StorageSpaceData - AVAILABLE size : EMPTY " + this.getAvailableSpaceSize());
            }

            // FREE space
            log.trace("StorageSpaceData - FREE (= available + unavailable) size :" + ssTO.getFreeSize());
            this.forceFreeSpaceSize(TSizeInBytes.makeEmpty());
            if (ssTO.getFreeSize() >= 0) {
                try {
                    // TODO GUARANTED
                    // this.availableSpaceSize = TSizeInBytes.make(ssTO.getFreeSize(), SizeUnit.BYTES);
                    this.forceFreeSpaceSize(TSizeInBytes.make(ssTO.getFreeSize(), SizeUnit.BYTES));
                    // log.debug("StorageSpaceData - FREE (= available + unavailable) size : " + this.availableSpaceSize);
                    log.trace("StorageSpaceData - FREE (= available + unavailable) size : " + this.getFreeSpaceSize());
                }
                catch (InvalidTSizeAttributesException ex3) {
                    log.error("Error while constructing FreeSpaceSize", ex3);
                }
            }
            else {
                log.trace("StorageSpaceData - FREE (= available + unavailable) size : EMPTY " + this.getFreeSpaceSize());
            }

            // USED space
            log.debug("StorageSpaceData - USED size:" + ssTO.getUsedSize());
            this.usedSpaceSize = TSizeInBytes.makeEmpty();
            if (ssTO.getUsedSize() >= 0) {
                try {
                    this.usedSpaceSize = TSizeInBytes.make(ssTO.getUsedSize(), SizeUnit.BYTES);
                    log.trace("StorageSpaceData - USED size: " + this.usedSpaceSize);
                }
                catch (InvalidTSizeAttributesException ex3) {
                    log.error("Error while constructing UsedSpaceSize", ex3);
                }
            }
            else {
                log.trace("StorageSpaceData - USED size: EMPTY " + this.usedSpaceSize);
            }

            // BUSY space
            log.debug("StorageSpaceData - BUSY (= used + reserved + unavailable) size:" + ssTO.getBusySize());
            this.forceBusySpaceSize(TSizeInBytes.makeEmpty());
            if (ssTO.getBusySize() >= 0) {
                try {
                    this.forceBusySpaceSize(TSizeInBytes.make(ssTO.getBusySize(), SizeUnit.BYTES));
                    log.trace("StorageSpaceData - BUSY (= used + reserved + unavailable) size:" + this.getBusySpaceSize());
                }
                catch (InvalidTSizeAttributesException ex3) {
                    log.error("Error while constructing BusySpaceSize", ex3);
                }
            }
            else {
                log.trace("StorageSpaceData - BUSY (= used + reserved + unavailable) size: EMPTY " + this.getBusySpaceSize());
            }

            // UNAVAILABLE space

            log.trace("StorageSpaceData - UNAVAILABLE size:" + ssTO.getUnavailableSize());
            this.unavailableSpaceSize = TSizeInBytes.makeEmpty();
            if (ssTO.getUnavailableSize() >= 0) {
                try {
                    this.unavailableSpaceSize = TSizeInBytes.make(ssTO.getUnavailableSize(), SizeUnit.BYTES);
                    log.trace("StorageSpaceData - UNAVAILABLE size: " + this.unavailableSpaceSize);
                }
                catch (InvalidTSizeAttributesException ex3) {
                    log.error("Error while constructing UnavailableSpaceSize", ex3);
                }
            }
            else {
                log.trace("StorageSpaceData - UNAVAILABLE size: EMPTY " + this.unavailableSpaceSize);
            }

            // Space Reserved for Storage Space
            log.trace("StorageSpaceData - TOTAL (Reserved) size:" + ssTO.getReservedSize());
            this.reservedSpaceSize = TSizeInBytes.makeEmpty();
            if (ssTO.getReservedSize() >= 0) {
                try {
                    // TODO GUARANTED
                    // this.reservedSpaceSize = TSizeInBytes.make(ssTO.getGuaranteedSize(), SizeUnit.BYTES);
                    this.reservedSpaceSize = TSizeInBytes.make(ssTO.getReservedSize(), SizeUnit.BYTES);
                    log.trace("StorageSpaceData - TotalSize (reserved): " + this.reservedSpaceSize);
                }
                catch (InvalidTSizeAttributesException ex2) {
                    log.error("Error while constructing SpaceReserved", ex2);
                }
            }
            else {
                log.trace("StorageSpaceData - Reserved : EMPTY " + this.reservedSpaceSize);
            }
        }

    }

    /**
     * @return
     */
    public boolean isInitialized() {
        return !(usedSpaceSize.isEmpty() || unavailableSpaceSize.isEmpty());
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


    public TLifeTimeInSeconds getLifeTime() {
        return spaceLifetime;
    }


    public void getLifeTime(TLifeTimeInSeconds time) {
        this.spaceLifetime = time;
    }


    public TSpaceToken getSpaceToken() {
        return spaceToken;
    }


    public void setSpaceToken(TSpaceToken token) {
        this.spaceToken = token;
    }


    public TUserID getUserID() {
        TUserID userID = null;
        try {
            // Create new UserID with certificate subject + VO String
            if (owner != null) {
                userID = new TUserID(owner.getDn());
            }
            else {
                userID = new TUserID("EMTPY");
            }
        }
        catch (InvalidTUserIDAttributeException e) {
            log.error("StorageSpaceData: Invalid UserID.", e);
        }
        return userID;
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


    public Date getCreationDate() {
        return this.creationDate;
    }


    /**
     * @return the owner
     */
    public final GridUserInterface getOwner() {
        return owner;
    }


    /**
     * @param owner the owner to set
     */
    public final void setOwner(GridUserInterface owner) {
        this.owner = owner;
    }


    /**
     * @return the spaceLifetime
     */
    public final TLifeTimeInSeconds getSpaceLifetime() {
        return spaceLifetime;
    }


    /**
     * @param spaceLifetime the spaceLifetime to set
     */
    public final void setSpaceLifetime(TLifeTimeInSeconds spaceLifetime) {
        this.spaceLifetime = spaceLifetime;
    }


    /**
     * @return the storageInfo
     */
    public final TStorageSystemInfo getStorageInfo() {
        return storageInfo;
    }


    /**
     * @param storageInfo the storageInfo to set
     */
    public final void setStorageInfo(TStorageSystemInfo storageInfo) {
        this.storageInfo = storageInfo;
    }


    /**
     * @return the spaceDesired
     */
    public final TSizeInBytes getTotalSpaceSize() {
        return totalSpaceSize;
    }


    /**
     * @param spaceDesired the spaceDesired to set
     */
    public final void setTotalSpaceSize(TSizeInBytes spaceDesired) {
        this.totalSpaceSize = spaceDesired;
        // let the computed fields built upon this value to be computed from now on
        this.unforceFreeSpaceSize();
        this.unforceAvailableSpaceSize();
    }


    /**
     * @return the spaceGuaranteed
     */
    public final TSizeInBytes getReservedSpaceSize() {
        return reservedSpaceSize;
    }


    /**
     * @param spaceGuaranteed the spaceGuaranteed to set
     */
    public final void setReservedSpaceSize(TSizeInBytes spaceGuaranteed) {
        this.reservedSpaceSize = spaceGuaranteed;
        // let the computed fields built upon this value to be computed from now on
        this.unforceBusySpaceSize();
    }


    /**
     * @return
     */
    public final TSizeInBytes getFreeSpaceSize() {
        if (!freeSpaceSizeForced) {
            // For now do not consider the reserved space, a better management is needed
// long size = this.totalSpaceSize.value() - this.usedSpaceSize.value() - this.reservedSpaceSize.value();
            if (this.totalSpaceSize == null || this.totalSpaceSize.isEmpty() || this.usedSpaceSize == null || this.usedSpaceSize.isEmpty()) {
                this.freeSpaceSize = TSizeInBytes.makeEmpty();
            }
            else {
                long size = this.totalSpaceSize.value() - this.usedSpaceSize.value();
                if (size >= 0) {
                    try {
                        this.freeSpaceSize = TSizeInBytes.make(size, SizeUnit.BYTES);
                    }
                    catch (InvalidTSizeAttributesException e) {
                        log.warn("Unable to create a valid Free Size, used empty one");
                        this.freeSpaceSize = TSizeInBytes.makeEmpty();
                    }
                }
                else {
                    log.warn("Unable to create a valid Free Space Size, computed free space is lower than zero");
                    this.freeSpaceSize = TSizeInBytes.makeEmpty();
                }
            }
        }
        return this.freeSpaceSize;
    }


    /**
     * @param freeSpaceSize
     */
    private final void forceFreeSpaceSize(TSizeInBytes freeSpaceSize) {
        this.freeSpaceSizeForced = true;
        this.freeSpaceSize = freeSpaceSize;
    }


    /**
     * 
     */
    private final void unforceFreeSpaceSize() {
        this.freeSpaceSizeForced = false;
    }


    /**
     * @return the usedSpaceSize
     */
    public final TSizeInBytes getUsedSpaceSize() {
        return usedSpaceSize;
    }


    /**
     * @param usedSpaceSize the usedSpaceSize to set
     */
    public final void setUsedSpaceSize(TSizeInBytes usedSpaceSize) {
        this.usedSpaceSize = usedSpaceSize;
        // let the computed fields built upon this value to be computed from now on
        // RitZ : ????
        this.unforceBusySpaceSize();
        this.unforceFreeSpaceSize();
        updateFreeSize();
        updateBusySize();
        updateAvailableSize();
    }

    private final void updateFreeSize() {
        long freeSizeValue = this.totalSpaceSize.value() - this.getUsedSpaceSize().value();
        if ((freeSizeValue < this.totalSpaceSize.value()) && (freeSizeValue >= 0)) {
            try {
                this.freeSpaceSize = TSizeInBytes.make(freeSizeValue, SizeUnit.BYTES);
            } catch (InvalidTSizeAttributesException e) {
            }
        }       
    }
    
    private final void updateBusySize() {
        long usedSizeValue = getUsedSpaceSize().value();
        long unavailableSizeValue = getUnavailableSpaceSize().value();
        long reservedSizeValue = getReservedSpaceSize().value();
        long busySize = usedSizeValue + reservedSizeValue + unavailableSizeValue;
        if ((busySize < this.totalSpaceSize.value()) && (busySize >= 0)) {
            try {
                this.busySpaceSize = TSizeInBytes.make(busySize, SizeUnit.BYTES);
            } catch (InvalidTSizeAttributesException e) {
            }
        }       
    }
    
    private final void updateAvailableSize() {
        long busySizeValue = getBusySpaceSize().value();
        long availableSizeValue = getTotalSpaceSize().value() - busySizeValue;
        if ((availableSizeValue < this.totalSpaceSize.value()) && (availableSizeValue >= 0)) {
            try {
                this.availableSpaceSize = TSizeInBytes.make(availableSizeValue, SizeUnit.BYTES);
            } catch (InvalidTSizeAttributesException e) {
            }
        }    
        
    }

    /**
     * @return the unavailableSpaceSize
     */
    public final TSizeInBytes getUnavailableSpaceSize() {
        return unavailableSpaceSize;
    }


    /**
     * @param unavailableSpaceSize the unavailableSpaceSize to set
     */
    public final void setUnavailableSpaceSize(TSizeInBytes unavailableSpaceSize) {
        this.unavailableSpaceSize = unavailableSpaceSize;
        // let the computed fields built upon this value to be computed from now on
        this.unforceBusySpaceSize();
    }


    /**
     * @return
     */
    public final TSizeInBytes getBusySpaceSize() {
        if (!this.busySpaceSizeForced) {
            if (this.usedSpaceSize == null || this.usedSpaceSize.isEmpty() || this.unavailableSpaceSize == null
                    || this.unavailableSpaceSize.isEmpty() || this.reservedSpaceSize == null || this.reservedSpaceSize.isEmpty()) {
                this.busySpaceSize = TSizeInBytes.makeEmpty();
            }
            else {
                try {
                    this.busySpaceSize = TSizeInBytes.make(this.usedSpaceSize.value() + this.unavailableSpaceSize.value()
                            + this.reservedSpaceSize.value(), SizeUnit.BYTES);
                }
                catch (InvalidTSizeAttributesException e) {
                    log.warn("Unable to create a valid Busy Size, used empty one");
                    this.busySpaceSize = TSizeInBytes.makeEmpty();
                }
            }
        }
        return this.busySpaceSize;
    }


    /**
     * @return the availableSpaceSize
     */
    public final TSizeInBytes getAvailableSpaceSize() {
        if (!this.availableSpaceSizeForced) {
            if (this.totalSpaceSize == null || this.totalSpaceSize.isEmpty() || this.getBusySpaceSize().isEmpty()) {
                this.availableSpaceSize = TSizeInBytes.makeEmpty();
            }
            else {
                try {
                    this.availableSpaceSize = TSizeInBytes.make(this.totalSpaceSize.value() - this.getBusySpaceSize().value(),
                                                                SizeUnit.BYTES);
                }
                catch (InvalidTSizeAttributesException e) {
                    log.warn("Unable to produce the TSizeInBytes object from \'"
                            + (this.totalSpaceSize.value() - this.getBusySpaceSize().value()) + "\' and \'" + SizeUnit.BYTES + "\'");
                    this.availableSpaceSize = TSizeInBytes.makeEmpty();
                }
            }
        }
        return this.availableSpaceSize;
    }


    /**
     * @param availableSpaceSize
     */
    // TODO This is not public because of private ReserveSpaceCommand.updateReservation calls , hope can be changed soon
    public final void forceAvailableSpaceSize(TSizeInBytes availableSpaceSize) {
        this.availableSpaceSizeForced = true;
        this.availableSpaceSize = availableSpaceSize;
    }


    /**
     * 
     */
    private final void unforceAvailableSpaceSize() {
        this.availableSpaceSizeForced = false;
    }


    /**
     * @param totalGuaranteedSize the totalGuaranteedSize to set
     */
    public void setTotalGuaranteedSize(TSizeInBytes totalGuaranteedSize) {
        this.totalGuaranteedSize = totalGuaranteedSize;
    }


    /**
     * @return the totalGuaranteedSize
     */
    public TSizeInBytes getTotalGuaranteedSize() {
        return totalGuaranteedSize;
    }


    private final void forceBusySpaceSize(TSizeInBytes busySpaceSize) {
        this.busySpaceSizeForced = true;
        this.busySpaceSize = busySpaceSize;
    }


    /**
     * 
     */
    private final void unforceBusySpaceSize() {
        this.busySpaceSizeForced = false;
        // let the computed fields built upon this value to be computed from now on
        this.unforceAvailableSpaceSize();
    }


    /**
     * @param creationDate the creationDate to set
     */
    public final void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }


    /**
     * This method is used to verify if the Space Reservation is expired, so
     * the lifetime is no more valid.
     * 
     * @return true if expired, false otherwise.
     */
    public boolean isExpired() {
        if(this.spaceLifetime != null && this.spaceLifetime.isInfinite())
        {
            return false;
        }
        Date currentTime = new Date();
        // Get the expiration time in millisec getting the creation time plus the lifetime in millisec
        long expirationTimeInMillisec = this.creationDate.getTime() + this.spaceLifetime.value() * 1000;
        Date expirationTime = new Date(expirationTimeInMillisec);
        return expirationTime.before(currentTime);
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n-- StorageSpaceData -- ");
        builder.append("  owner              = ");
        builder.append(owner);
        builder.append("\n");
        builder.append("  spaceType          = ");
        builder.append(spaceType);
        builder.append("\n");
        builder.append("  spaceToken         = ");
        builder.append(spaceToken);
        builder.append("\n");
        builder.append("  spaceFileName      = ");
        builder.append(spaceFileName);
        builder.append("\n");
        builder.append("  spaceTokenAlias    = ");
        builder.append(spaceTokenAlias);
        builder.append("\n");
        builder.append("  spaceLifetime      = ");
        builder.append(spaceLifetime);
        builder.append("\n");
        builder.append("  storageInfo        = ");
        builder.append(storageInfo);
        builder.append("\n");
        builder.append("  creationDate       = ");
        builder.append(creationDate);
        builder.append("\n");
        builder.append("  totalSpaceSize     = ");
        builder.append(totalSpaceSize);
        builder.append("\n");
        builder.append("  totalGuaranteedSize= ");
        builder.append(totalGuaranteedSize);
        builder.append("\n");
        builder.append("  availableSpaceSize = ");
        builder.append(availableSpaceSize);
        builder.append("\n");
        builder.append("  availableSpaceSizeForced=");
        builder.append(availableSpaceSizeForced);
        builder.append("\n");
        builder.append("  usedSpaceSize      = ");
        builder.append(usedSpaceSize);
        builder.append("\n");
        builder.append("  freeSpaceSize      = ");
        builder.append(freeSpaceSize);
        builder.append("\n");
        builder.append("  freeSpaceSizeForced= ");
        builder.append(freeSpaceSizeForced);
        builder.append("\n");
        builder.append("  unavailableSpaceSize= ");
        builder.append(unavailableSpaceSize);
        builder.append("\n");
        builder.append("  reservedSpaceSize   = ");
        builder.append(reservedSpaceSize);
        builder.append("\n");
        builder.append("  busySpaceSize       = ");
        builder.append(busySpaceSize);
        builder.append("\n");
        builder.append("  busySpaceSizeForced = ");
        builder.append(busySpaceSizeForced);
        builder.append("\n");
        builder.append("-- ^^^^^^^^^^^^^^^^ -- ");
        return builder.toString();
    }
}
