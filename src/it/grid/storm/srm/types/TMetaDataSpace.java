/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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

package it.grid.storm.srm.types;

import it.grid.storm.space.StorageSpaceData;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TMetaDataSpace implements Serializable
{
    private TSpaceType spaceType;
    private TReturnStatus status = null;
    private TSpaceToken spaceToken;
    private TRetentionPolicyInfo retentionPolicyInfo;
    private TUserID owner;
    private TSizeInBytes totalSize;
    private TSizeInBytes guaranteedSize;
    private TSizeInBytes unusedSize;
    private TLifeTimeInSeconds lifetimeAssigned = null;
    private TLifeTimeInSeconds lifetimeLeft = null;

    private static final Logger log = LoggerFactory.getLogger(TMetaDataSpace.class);

    public TMetaDataSpace()
    {
        this.spaceType = TSpaceType.EMPTY;
        this.status = null;
        this.spaceToken = TSpaceToken.makeEmpty();
        this.retentionPolicyInfo = null;
        this.owner = TUserID.makeEmpty();
        this.totalSize = TSizeInBytes.makeEmpty();
        this.guaranteedSize = TSizeInBytes.makeEmpty();
        this.unusedSize = TSizeInBytes.makeEmpty();
        this.lifetimeAssigned = TLifeTimeInSeconds.makeEmpty();
        this.lifetimeLeft = TLifeTimeInSeconds.makeEmpty();
    }

    /**
     * Constructor
     * @param spaceType TSpaceType
     * @param spaceToken TSpaceToken
     * @param status TReturnStatus
     * @param user TUserID
     * @param totalSize TSizeInBytes
     * @param guaranteedSize TSizeInBytes
     * @param unusedSize TSizeInBytes
     * @param lifetimeAssigned TLifeTimeInSeconds
     * @param lifetimeLeft TLifeTimeInSeconds
     * @throws InvalidTMetaDataSpaceAttributeException
     */
    public TMetaDataSpace(TSpaceType spaceType, TSpaceToken spaceToken, TReturnStatus status, TUserID user,
            TSizeInBytes totalSize, TSizeInBytes guaranteedSize, TSizeInBytes unusedSize,
            TLifeTimeInSeconds lifetimeAssigned, TLifeTimeInSeconds lifetimeLeft)
    throws InvalidTMetaDataSpaceAttributeException
    {
        boolean ok = (spaceToken!=null);

        if (!ok) {
            throw new InvalidTMetaDataSpaceAttributeException(spaceToken);
        }

        this.spaceType = spaceType;
        this.spaceToken = spaceToken;
        this.status = status;
        this.owner = user;
        this.totalSize = totalSize;
        this.guaranteedSize = guaranteedSize;
        this.unusedSize = unusedSize;
        this.lifetimeAssigned = lifetimeAssigned;
        this.lifetimeLeft = lifetimeLeft;
    }

    /**
     * Constructor with SpaceData returned by DAO.
     * @param spaceData of type StorageSpaceData
     * @throws InvalidTMetaDataSpaceAttributeException
     * @throws InvalidTSizeAttributesException
     */
    public TMetaDataSpace(StorageSpaceData spaceData) throws InvalidTMetaDataSpaceAttributeException,
    InvalidTSizeAttributesException
    {
        if (spaceData == null) {
            log.warn("TMetaDataSpace built without SPACEDATA detail.");
            this.spaceType = TSpaceType.EMPTY;
            this.spaceToken = TSpaceToken.makeEmpty();
            try {
                this.status = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid space token");
            } catch (InvalidTReturnStatusAttributeException e) {
                this.status = null;
            }
            this.owner = TUserID.makeEmpty();
            this.totalSize = TSizeInBytes.makeEmpty();
            this.guaranteedSize = TSizeInBytes.makeEmpty();
            this.unusedSize = TSizeInBytes.makeEmpty();
            this.lifetimeAssigned = TLifeTimeInSeconds.makeEmpty();
            this.lifetimeLeft = TLifeTimeInSeconds.makeEmpty();
        } else {
            boolean ok = (spaceData.getSpaceToken() != null);
            if (!ok) {
                log.warn("TMetaDataSpace built with SpaceData without Token.. !?");
                throw new InvalidTMetaDataSpaceAttributeException(spaceToken);
            }
            this.spaceType = spaceData.getSpaceType();
            this.spaceToken = spaceData.getSpaceToken();
            this.owner = spaceData.getUserID();
            this.totalSize = spaceData.getTotalSpaceSize();
            this.guaranteedSize = spaceData.getReservedSpaceSize();
            this.unusedSize = spaceData.getFreeSpaceSize();
            this.lifetimeAssigned = spaceData.getLifeTime();
            this.lifetimeLeft = this.lifetimeAssigned.timeLeft(spaceData.getCreationDate());
            try {
                if ((this.lifetimeLeft.value() == 0)&&(this.spaceType!=TSpaceType.VOSPACE)) {
                    this.status = new TReturnStatus(TStatusCode.SRM_SPACE_LIFETIME_EXPIRED, "Expired space lifetime");
                } else {
                    this.status = new TReturnStatus(TStatusCode.SRM_SUCCESS, "Valid space token");
                }
            } catch (InvalidTReturnStatusAttributeException e) {
                this.status = null;
            }
        }
    }

    public static TMetaDataSpace makeEmpty()
    {
        return new TMetaDataSpace();
    }

    /**
     * Method that returns SpaceType
     */
    public TSpaceType getSpaceType()
    {
        return spaceType;
    }

    /**
     * Get TReturnStatus
     */
    public TReturnStatus getStatus()
    {
        return status;
    }

    /**
     * Set TReturnStatus
     */
    public void setStatus(TReturnStatus status)
    {
        this.status = status;
    }

    /**
     * Return Space Token;
     */
    public TSpaceToken getSpaceToken()
    {
        return spaceToken;
    }

    public void setSpaceToken(TSpaceToken token)
    {
        this.spaceToken = token;
    }
    public void setSpaceType(TSpaceType type)
    {
        this.spaceType = type;
    }
    public void setOwner(TUserID uid)
    {
        this.owner = uid;
    }
    public void setTotalSize(TSizeInBytes tsize)
    {
        this.totalSize = tsize;
    }
    public void setGuarSize(TSizeInBytes gsize)
    {
        this.guaranteedSize = gsize;
    }
    public void setUnSize(TSizeInBytes usize)
    {
        this.unusedSize = usize;
    }

    public void setLifeTime(TLifeTimeInSeconds time)
    {
        this.lifetimeAssigned = time;
    }

    public void setLifeTimeLeft(TLifeTimeInSeconds time)
    {
        this.lifetimeLeft = time;
    }

    /**
     * Return retentionPolicyInfo
     */
    public TRetentionPolicyInfo getRetentionPolicyInfo()
    {
        return retentionPolicyInfo;
    }

    public void setRetentionPolicyInfo(TRetentionPolicyInfo retentionPolicyInfo)
    {
        this.retentionPolicyInfo = retentionPolicyInfo;
    }

    /**
     * Return User Identifier;
     */
    public TUserID getUserID()
    {
        return owner;
    }

    /**
     * Return TotalSize;
     */
    public TSizeInBytes getTotalSize()
    {
        return totalSize;
    }

    /**
     * Return Guaranteed Size;
     */
    public TSizeInBytes getGuaranteedSize()
    {
        return guaranteedSize;
    }

    /**
     * Return Unused Size.
     */
    public TSizeInBytes getUnusedSize()
    {
        return unusedSize;
    }

    /**
     * Return Lifetime Assigned.
     */
    public TLifeTimeInSeconds getLifeTimeAssigned()
    {
        return lifetimeAssigned;

    }

    /**
     * Return LifeTime Left
     */
    public TLifeTimeInSeconds getLifeTimeLeft()
    {
        return lifetimeLeft;
    }

    /**
     * Method used to encode value for FE communication.
     */
    public void encode(Map outputParam, String fieldName)
    {
        Map metaDataSpace = new HashMap();

        this.encode(metaDataSpace);
        outputParam.put(fieldName, metaDataSpace);
    }

    /**
     * Method used to encode value for FE communication.
     */
    public void encode(Map metaDataSpace)
    {
        spaceToken.encode(metaDataSpace, TSpaceToken.PNAME_SPACETOKEN);
        if (status != null) {
            status.encode(metaDataSpace, TReturnStatus.PNAME_STATUS);
        }
        if (retentionPolicyInfo != null) {
            retentionPolicyInfo.encode(metaDataSpace, TRetentionPolicyInfo.PNAME_retentionPolicyInfo);
        }
        owner.encode(metaDataSpace, TUserID.PNAME_OWNER);
        totalSize.encode(metaDataSpace, TSizeInBytes.PNAME_TOTALSIZE);
        guaranteedSize.encode(metaDataSpace, TSizeInBytes.PNAME_GUARANTEEDSIZE);
        unusedSize.encode(metaDataSpace, TSizeInBytes.PNAME_UNUSEDSIZE);
        lifetimeAssigned.encode(metaDataSpace, TLifeTimeInSeconds.PNAME_LIFETIMEASSIGNED);
        lifetimeLeft.encode(metaDataSpace, TLifeTimeInSeconds.PNAME_LIFETIMELEFT);
    }
}
