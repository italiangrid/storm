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

/*
 * (c)2004 INFN / ICTP-eGrid
 * This file can be distributed and/or modified under the terms of
 * the INFN Software License. For a copy of the licence please visit
 * http://www.cnaf.infn.it/license.html
 *
 */

/**
 * StorageSpaceTO
 */
package it.grid.storm.persistence.model;

import it.grid.storm.common.types.VO;
import it.grid.storm.config.DefaultValue;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Riccardo Zappi - riccardo.zappi AT cnaf.infn.it
 * @version $Id: StorageSpaceTO.java,v 1.13 2006/06/29 14:46:25 aforti Exp $
 */
public class StorageSpaceTO implements Serializable, Comparable {

    /**
     *Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(StorageSpaceTO.class);

    // ----- PRIMARY KEY ----//
    private Long storageSpaceId = null; // Persistence Object IDentifier
    // ----- FIELDS ----//
    private String ownerName = null;
    private String voName = null;
    private String spaceType = null;
    private String alias = null;
    private String spaceToken = null;
    private String spaceFile = null;
    private Date created = new Date();
    private long totalSize = -1L;
    private long guaranteedSize = -1L;
    private long unusedSize = -1L;
    private long lifetime = -1L;
    private String storageInfo = null;

    // ----- ASSOCIATIONS ----//
    // Relationship with StorageFile [one-to-many (not-null = false)]
    private final Set storageFiles = new HashSet();
    // Component splitted into ownerName e voName
    private GridUserInterface owner = null; // The maker

    // ********************** Constructor methods ********************** //

    /**
     * No-arg constructor for JavaBean tools.
     */
    public StorageSpaceTO() {
        super();
    }

    /**
     * Minimal constructor.
     * 
     * @param maker User
     */
    public StorageSpaceTO(GridUserInterface maker) {
        // Always exists a creator!
        owner = maker;
        ownerName = maker.getDn();
        voName = getVOName(maker);

        // No Alias (or Token description) is setted
        alias = null;

        /**
         * The below parameters are filled with DEFAULT values for named user.
         */
        spaceType = DefaultValue.getNamedVO_SpaceType(voName);;
        guaranteedSize = DefaultValue.getNamedVO_GuaranteedSpaceSize(voName);
        totalSize = DefaultValue.getNamedVO_TotalSpaceSize(voName);
        lifetime = DefaultValue.getNamedVO_SpaceLifeTime(voName);

        /**
         * The below parameters are filled with GENERATED values for named user.
         */
        // StoRM generates a space_token
        spaceToken = (new GUID()).toString();
        spaceFile = "sf-" + spaceToken;
    }

    public StorageSpaceTO(GridUserInterface maker, String alias) {
        // Always exists a creator!
        owner = maker;
        ownerName = maker.getDn();
        voName = getVOName(maker);
        // No Alias (or Token description) is setted
        this.alias = alias;

        /**
         * The below parameters are filled with DEFAULT values for named user.
         */
        spaceType = DefaultValue.getNamedVO_SpaceType(voName);;
        guaranteedSize = DefaultValue.getNamedVO_GuaranteedSpaceSize(voName);
        totalSize = DefaultValue.getNamedVO_TotalSpaceSize(voName);
        lifetime = DefaultValue.getNamedVO_SpaceLifeTime(voName);

        /**
         * The below parameters are filled with GENERATED values for named user.
         */
        // StoRM generates a space_token
        spaceToken = (new GUID()).toString();
        spaceFile = "/" + "sf-" + spaceToken;

    }

    /**
     * Full constructor.
     * 
     * @param maker User
     * @param alias String
     * @param spaceToken String
     * @param path PathName
     */
    public StorageSpaceTO(GridUserInterface maker, String alias, String spaceToken, String spaceFile) {
        owner = maker;
        this.alias = alias;
        this.spaceToken = spaceToken;
        this.spaceFile = spaceFile;
    }

    public StorageSpaceTO(GridUserInterface maker, String type, String alias, String spaceToken,
            String spaceFile, long guaranteedSize, long totalSize) {
        owner = maker;
        ownerName = maker.getDn();
        voName = getVOName(maker);
        spaceType = type;
        this.alias = alias;
        this.spaceToken = spaceToken;
        this.spaceFile = spaceFile;
        this.guaranteedSize = guaranteedSize;
        this.totalSize = totalSize;
    }

    /**
     * Constructor from Domain Object
     * 
     * @param spaceData SpaceData
     */
    public StorageSpaceTO(it.grid.storm.space.StorageSpaceData spaceData) {
        if (spaceData != null) {
            StorageSpaceTO.log.debug("Building StorageSpaceTO with " + spaceData);
            owner = spaceData.getUser();
            if (spaceData.getUser() != null) {
                ownerName = (spaceData.getUser()).getDn();
                voName = getVOName(spaceData.getUser());
            }
            if (spaceData.getSpaceType() != null) {
                spaceType = (spaceData.getSpaceType()).getValue();
            }

            alias = spaceData.getSpaceTokenAlias();

            if (spaceData.getSpaceToken() != null) {
                spaceToken = spaceData.getSpaceToken().getValue();
            }

            spaceFile = spaceData.getSpaceFileNameString();

            if (spaceData.getGuaranteedSize() != null) {
                guaranteedSize = spaceData.getGuaranteedSize().value();
            }

            if (spaceData.getDesiredSize() != null) {
                totalSize = spaceData.getDesiredSize().value();
            }

            if (spaceData.getUnusedSizes() != null) {
                unusedSize = spaceData.getUnusedSizes().value();
            }

            if (spaceData.getLifeTime() != null) {
                lifetime = spaceData.getLifeTime().value();
            }

            if (spaceData.getDate() != null) {
                created = spaceData.getDate();
            }
        }
    }

    // ************ HELPER Method *************** //
    private String getVOName(GridUserInterface maker) {
        String voStr = VO.makeNoVo().getValue();
        if (maker instanceof VomsGridUser) {
            voStr = ((VomsGridUser) maker).getVO().getValue();
        }
        return voStr;
    }

    // ********************** Accessor Methods ********************** //

    public Long getStorageSpaceId() {
        return storageSpaceId;
    }

    public void setStorageSpaceId(Long id) {
        storageSpaceId = id;
    }

    // -------------------------------------

    public GridUserInterface getOwner() {
        return owner;
    }

    public void setOwner(GridUserInterface owner) {
        this.owner = owner;
        // this.ownerName = owner.getDn();
        // this.voName = (owner.getMainVo()).getValue();
    }

    // -------------------------------------

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    // -------------------------------------

    public String getVoName() {
        return voName;
    }

    public void setVoName(String voName) {
        this.voName = voName;
    }

    // -------------------------------------

    public String getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(String spaceType) {
        this.spaceType = spaceType;
    }

    // -------------------------------------

    public long getGuaranteedSize() {
        return guaranteedSize;
    }

    public void setGuaranteedSize(long guaranteedSize) {
        this.guaranteedSize = guaranteedSize;
    }

    // -------------------------------------

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    // -------------------------------------

    public long getUnusedSize() {
        return unusedSize;
    }

    public void setUnusedSize(long unusedSize) {
        this.unusedSize = unusedSize;
    }

    // -------------------------------------

    public void setSpaceToken(String spaceToken) {
        this.spaceToken = spaceToken;
    }

    public String getSpaceToken() {
        return spaceToken;
    }

    // -------------------------------------

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    // -------------------------------------

    public void setSpaceFile(String spaceFile) {
        this.spaceFile = spaceFile;
    }

    public String getSpaceFile() {
        return spaceFile;
    }

    // -------------------------------------

    public long getLifetime() {
        return lifetime;
    }

    public void setLifetime(long lifetime) {
        this.lifetime = lifetime;
    }

    // -------------------------------------

    public String getStorageInfo() {
        return storageInfo;
    }

    public void setStorageInfo(String storageInfo) {
        this.storageInfo = storageInfo;
    }

    // -------------------------------------

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date date) {
        created = date;
    }

    // ********************** Common Methods ********************** //

    @Override
    public boolean equals(Object o) {
    	if (o==null) {
    	  return false;		
    	} 
    	if (o instanceof StorageSpaceTO) {
    		if (this == o) {
                return true;
            }
            final StorageSpaceTO storageSpace = (StorageSpaceTO) o;
            if (!spaceToken.equals(storageSpace.getSpaceToken())) {
                return false;
            }
            if (!spaceFile.equals(storageSpace.getSpaceFile())) {
                return false;
            }
            return true; 
    	}
    	else {
    		return false;
    	}
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 37 * hash + spaceFile.hashCode();
        hash = 37 * hash + spaceToken.hashCode();
        return hash;

    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" ==== STORAGE SPACE (token=" + spaceToken + ") ==== \n");
        sb.append(" STORAGE SPACE ID = " + storageSpaceId);
        sb.append("\n");
        sb.append(" OWNER USER NAME  = " + ownerName);
        sb.append("\n");
        sb.append(" OWNER VO NAME    = " + voName);
        sb.append("\n");
        sb.append(" SPACE ALIAS NAME = " + alias);
        sb.append("\n");
        sb.append(" SPACE TYPE       = " + spaceType);
        sb.append("\n");
        sb.append(" SPACE TOKEN      = " + spaceToken);
        sb.append("\n");
        sb.append(" SPACE FILE       = " + spaceFile);
        sb.append("\n");
        sb.append(" CREATED          = " + created);
        sb.append("\n");
        sb.append(" TOTAL SIZE       = " + totalSize);
        sb.append("\n");
        sb.append(" GUARANTEED SIZE  = " + guaranteedSize);
        sb.append("\n");
        sb.append(" FREE SIZE        = " + unusedSize);
        sb.append("\n");
        sb.append(" LIFETIME (sec)   = " + lifetime);
        sb.append("\n");
        sb.append(" STORAGE INFO     = " + storageInfo);
        sb.append("\n");
        sb.append(" NR STOR_FILES    = <UNDEF for NOW..>");
        sb.append("\n");
        return sb.toString();
    }

    public int compareTo(Object o) {
        if (o instanceof StorageSpaceTO) {
            return getCreated().compareTo(((StorageSpaceTO) o).getCreated());
        }
        return 0;
    }

    // ********************** Business Methods ********************** //
}
