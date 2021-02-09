/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/*
 * (c)2004 INFN / ICTP-eGrid This file can be distributed and/or modified under
 * the terms of the INFN Software License. For a copy of the licence please
 * visit http://www.cnaf.infn.it/license.html
 */

/**
 * StorageSpaceTO
 */
package it.grid.storm.persistence.model;

import it.grid.storm.common.types.VO;
import it.grid.storm.griduser.AbstractGridUser;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.space.StorageSpaceData;

import java.io.Serializable;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Riccardo Zappi - riccardo.zappi AT cnaf.infn.it
 * @version $Id: StorageSpaceTO.java,v 1.13 2006/06/29 14:46:25 aforti Exp $
 */
public class StorageSpaceTO implements Serializable, Comparable<StorageSpaceTO> {

  private static final long serialVersionUID = -87317982494792808L;

  private static final Logger log = LoggerFactory.getLogger(StorageSpaceTO.class);

  // ----- PRIMARY KEY ----//
  private Long storageSpaceId = null; // Persistence Object IDentifier

  // ----- FIELDS ----//
  private String ownerName = null;
  private String voName = null;
  private String spaceType = null; // `SPACE_TYPE` VARCHAR(10) NOT NULL default
                                   // ''
  private String alias = null;
  private String spaceToken = null;
  private String spaceFile = null; // `SPACE_FILE` VARCHAR(145) NOT NULL default
                                   // ''
  private long lifetime = -1L; // `LIFETIME` bigint(20) default NULL
  private String storageInfo = null;// `STORAGE_INFO` VARCHAR(255) default NULL
  private Date created = new Date();

  private long totalSize = 0L; // `TOTAL_SIZE` bigint(20) NOT NULL default '0'
  private long guaranteedSize = 0L; // `GUAR_SIZE` bigint(20) NOT NULL default
                                    // '0'
  private long freeSize = 0L; // `FREE_SIZE` bigint(20) default NULL

  private long usedSize = -1L; // `USED_SIZE` bigint(20) NOT NULL default '-1'
  private long busySize = -1L; // `BUSY_SIZE` bigint(20) NOT NULL default '-1'
  private long unavailableSize = -1L; // `UNAVAILABLE_SIZE` bigint(20) NOT NULL
                                      // default '-1'
  private long availableSize = -1L; // `AVAILABLE_SIZE` bigint(20) NOT NULL
                                    // default '-1'
  private long reservedSize = -1L; // `RESERVED_SIZE` bigint(20) NOT NULL
                                   // default '-1'
  private Date updateTime = null;

  // ********************** Constructor methods ********************** //

  /**
   * No-arg constructor for JavaBean tools.
   */
  public StorageSpaceTO() {

    super();
  }

  /**
   * Constructor from Domain Object StorageSpaceData
   * 
   * @param spaceData SpaceData
   */
  public StorageSpaceTO(StorageSpaceData spaceData) {

    if (spaceData != null) {
      log.debug("Building StorageSpaceTO with {}", spaceData);
      if (spaceData.getOwner() != null) {
        ownerName = spaceData.getOwner().getDn();
        voName = getVOName(spaceData.getOwner());
      }
      if (spaceData.getSpaceType() != null) {
        spaceType = (spaceData.getSpaceType()).getValue();
      }
      alias = spaceData.getSpaceTokenAlias();
      if (spaceData.getSpaceToken() != null) {
        spaceToken = spaceData.getSpaceToken().getValue();
      }
      spaceFile = spaceData.getSpaceFileNameString();
      if (spaceData.getTotalSpaceSize() != null) {
        totalSize = spaceData.getTotalSpaceSize().value();
      }
      if (spaceData.getTotalGuaranteedSize() != null) {
        guaranteedSize = spaceData.getTotalGuaranteedSize().value();
      }
      if (spaceData.getAvailableSpaceSize() != null) {
        availableSize = spaceData.getAvailableSpaceSize().value();
      }
      if (spaceData.getUsedSpaceSize() != null) {
        usedSize = spaceData.getUsedSpaceSize().value();
      }
      if (spaceData.getFreeSpaceSize() != null) {
        freeSize = spaceData.getFreeSpaceSize().value();
      }
      if (spaceData.getUnavailableSpaceSize() != null) {
        unavailableSize = spaceData.getUnavailableSpaceSize().value();
      }
      if (spaceData.getBusySpaceSize() != null) {
        busySize = spaceData.getBusySpaceSize().value();
      }
      if (spaceData.getReservedSpaceSize() != null) {
        reservedSize = spaceData.getReservedSpaceSize().value();
      }
      if (spaceData.getLifeTime() != null) {
        lifetime = spaceData.getLifeTime().value();
      }
      if (spaceData.getStorageInfo() != null) {
        storageInfo = spaceData.getStorageInfo().getValue();
      }
      if (spaceData.getCreationDate() != null) {
        created = spaceData.getCreationDate();
      }
    }
  }

  // ************ HELPER Method *************** //
  private String getVOName(GridUserInterface maker) {

    String voStr = VO.makeNoVo().getValue();
    if (maker instanceof AbstractGridUser) {
      voStr = ((AbstractGridUser) maker).getVO().getValue();
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

  // -------------------------------------

  /**
   * @return the freeSize
   */
  public final long getFreeSize() {

    return freeSize;
  }

  /**
   * @param freeSize the freeSize to set
   */
  public final void setFreeSize(long freeSize) {

    this.freeSize = freeSize;
  }

  /**
   * @return the usedSize
   */
  public final long getUsedSize() {

    return usedSize;
  }

  /**
   * @param usedSize the usedSize to set
   */
  public final void setUsedSize(long usedSize) {

    this.usedSize = usedSize;
  }

  /**
   * @return the busySize
   */
  public final long getBusySize() {

    return busySize;
  }

  /**
   * @param busySize the busySize to set
   */
  public final void setBusySize(long busySize) {

    this.busySize = busySize;
  }

  /**
   * @return the unavailableSize
   */
  public final long getUnavailableSize() {

    return unavailableSize;
  }

  /**
   * @param unavailableSize the unavailableSize to set
   */
  public final void setUnavailableSize(long unavailableSize) {

    this.unavailableSize = unavailableSize;
  }

  /**
   * @return the reservedSize
   */
  public final long getReservedSize() {

    return reservedSize;
  }

  /**
   * @param reservedSize the reservedSize to set
   */
  public final void setReservedSize(long reservedSize) {

    this.reservedSize = reservedSize;
  }

  /**
   * @param availableSize the availableSize to set
   */
  public void setAvailableSize(long availableSize) {

    this.availableSize = availableSize;
  }

  /**
   * @return the availableSize
   */
  public long getAvailableSize() {

    return availableSize;
  }

  // ********************** Common Methods ********************** //

  /**
   * @param updateTime the updateTime to set
   */
  public void setUpdateTime(Date updateTime) {

    this.updateTime = updateTime;
  }

  /**
   * @return the updateTime
   */
  public Date getUpdateTime() {

    return updateTime;
  }

  @Override
  public boolean equals(Object o) {

    if (o == null) {
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
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {

    int hash = 17;
    hash = 37 * hash + spaceToken.hashCode();
    return hash;

  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
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
    sb.append(" FREE SIZE        = " + freeSize);
    sb.append("\n");
    sb.append(" USED SIZE        = " + usedSize);
    sb.append("\n");
    sb.append(" BUSY SIZE        = " + busySize);
    sb.append("\n");
    sb.append(" AVAILABLE      = " + availableSize);
    sb.append("\n");
    sb.append(" RESERVED      = " + reservedSize);
    sb.append("\n");
    sb.append("   UNAVAILABLE    = " + unavailableSize);
    sb.append("\n");
    sb.append(" LIFETIME (sec)   = " + lifetime);
    sb.append("\n");
    sb.append(" STORAGE INFO     = " + storageInfo);
    sb.append("\n");
    sb.append(" UPDATE TIME     = " + updateTime);
    sb.append("\n");
    sb.append(" NR STOR_FILES    = <UNDEF for NOW..>");
    sb.append("\n");
    return sb.toString();
  }

  @Override
  public int compareTo(StorageSpaceTO o) {

    if (o instanceof StorageSpaceTO) {
      return getCreated().compareTo(((StorageSpaceTO) o).getCreated());
    }
    return 0;
  }

  // ********************** Business Methods ********************** //
}
