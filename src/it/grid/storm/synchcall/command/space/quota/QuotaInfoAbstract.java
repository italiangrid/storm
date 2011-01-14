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

package it.grid.storm.synchcall.command.space.quota;

import it.grid.storm.common.types.*;
import it.grid.storm.namespace.model.*;

public abstract class QuotaInfoAbstract implements QuotaInfoInterface {

  private String filesystemName = null;
  private QuotaType quotaType = null;
  private long blockUsage = -1L;
  private long blockHardLimit = -1L;
  private long blockSoftLimit = -1L;
  private long blockGraceTime = -1L;
  private long iNodeUsage = -1L;
  private long iNodeHardLimit = -1L;
  private long iNodeSoftLimit = -1L;
  private long iNodeGraceTime = -1L;
  private SizeUnit sizeUnit = SizeUnit.KILOBYTES;  //Default values for Blocks
  private TimeUnit iNodeTimeUnit = TimeUnit.DAYS;  //Default values is 7 days = 168 hours
  private TimeUnit sizeTimeUnit = TimeUnit.DAYS;  //Default values is 7 days = 168 hours


  /**
   * getBlockGraceTime
   *
   * @return long
   */
  public long getBlockGraceTime() {
    return this.blockGraceTime;
  }

  /**
   * getBlockHardLimit
   *
   * @return long
   */
  public long getBlockHardLimit() {
    return this.blockHardLimit;
  }

  /**
   * getBlockSoftLimit
   *
   * @return long
   */
  public long getBlockSoftLimit() {
    return this.blockSoftLimit;
  }

  /**
   * getBlockUsage
   *
   * @return long
   */
  public long getBlockUsage() {
    return this.blockUsage;
  }

  /**
   * getFilesystemName
   *
   * @return String
   */
  public String getFilesystemName() {
    return this.filesystemName;
  }

  /**
   * getINodeGraceTime
   *
   * @return long
   */
  public long getINodeGraceTime() {
    return this.iNodeGraceTime;
  }

  /**
   * getINodeHardLimit
   *
   * @return long
   */
  public long getINodeHardLimit() {
    return this.iNodeHardLimit;
  }

  /**
   * getINodeSoftLimit
   *
   * @return long
   */
  public long getINodeSoftLimit() {
    return this.iNodeSoftLimit;
  }

  /**
   * getINodeUsage
   *
   * @return long
   */
  public long getINodeUsage() {
    return this.iNodeUsage;
  }

  /**
   * getQuotaType
   *
   * @return QuotaType
   */
  public QuotaType getQuotaType() {
    return this.quotaType;
  }

  /**
   * getSizeUnit
   *
   * @return SizeUnit
   */
  public SizeUnit getSizeUnit() {
    return this.sizeUnit;
  }


  /**
   * getINodeTimeUnit
   *
   * @return TimeUnit
   */
  public TimeUnit getINodeTimeUnit() {
    return this.iNodeTimeUnit;
  }

  /**
   * getSizeTimeUnit
   *
   * @return TimeUnit
   */
  public TimeUnit getSizeTimeUnit() {
    return this.sizeTimeUnit;
  }

  /**
   * setBlockGraceTime
   *
   * @param blockGraceTime long
   */
  public void setBlockGraceTime(String blockGraceTime) {
    long graceTime = -1;
    try {
      graceTime = Long.parseLong(blockGraceTime);
    } catch (NumberFormatException nfe) {
      graceTime = 0;
      this.sizeTimeUnit = TimeUnit.EMPTY;
    }

    this.blockGraceTime = graceTime;
  }

  /**
   * setBlockHardLimit
   *
   * @param blockHardLimit long
   */
  public void setBlockHardLimit(long blockHardLimit) {
    this.blockHardLimit = blockHardLimit;
  }

  /**
   * setBlockSoftLimit
   *
   * @param blockSoftLimit long
   */
  public void setBlockSoftLimit(long blockSoftLimit) {
    this.blockSoftLimit = blockSoftLimit;
  }

  /**
   * setBlockUsage
   *
   * @param blockUsage long
   */
  public void setBlockUsage(long blockUsage) {
    this.blockUsage = blockUsage;
  }

  /**
   * setFilesystemName
   *
   * @param fileSystemName String
   */
  public void setFilesystemName(String fileSystemName) {
    this.filesystemName = fileSystemName;
  }

  /**
   * setINodeGraceTime
   *
   * @param iNodeGraceTime long
   */
  public void setINodeGraceTime(String iNodeGraceTime) {
    long graceTime = -1;
    try {
      graceTime = Long.parseLong(iNodeGraceTime);
    } catch (NumberFormatException nfe) {
      graceTime = 0;
      this.iNodeTimeUnit = TimeUnit.EMPTY;
    }

    this.iNodeGraceTime = graceTime;
  }

  /**
   * setINodeHardLimit
   *
   * @param iNodeHardLimit long
   */
  public void setINodeHardLimit(long iNodeHardLimit) {
    this.iNodeHardLimit = iNodeHardLimit;
  }


  /**
   * setINodeSoftLimit
   *
   * @param iNodeSoftLimit long
   */
  public void setINodeSoftLimit(long iNodeSoftLimit) {
    this.iNodeSoftLimit = iNodeSoftLimit;
  }

  /**
   * setINodeUsage
   *
   * @param iNodeUsage long
   */
  public void setINodeUsage(long iNodeUsage) {
     this.iNodeUsage = iNodeUsage;
  }

  /**
   * setQuotaType
   *
   * @param quotaType QuotaType
   */
  public void setQuotaType(QuotaType quotaType) {
     this.quotaType = quotaType;
  }

  /**
   * setSizeUnit
   *
   * @param sizeUnit String
   */
  public void setSizeUnit(String sizeUnit) {
    this.sizeUnit = SizeUnit.createSizeUnit(sizeUnit);
  }

  /**
   * setTimeUnit
   *
   * @param timeUnit String
   */
  public void setINodeTimeUnit(String timeUnit) {
    this.iNodeTimeUnit = TimeUnit.createTimeUnit(timeUnit);
  }

  /**
   * setTimeUnit
   *
   * @param timeUnit String
   */
  public void setSizeTimeUnit(String timeUnit) {
    this.sizeTimeUnit = TimeUnit.createTimeUnit(timeUnit);
  }


}
