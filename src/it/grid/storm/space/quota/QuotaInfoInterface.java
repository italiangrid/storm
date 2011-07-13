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

package it.grid.storm.space.quota;

import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.namespace.model.QuotaType;
import it.grid.storm.common.types.TimeUnit;

public interface QuotaInfoInterface {

  public void setFilesystemName(String fileSystemName) ;

  public String getFilesystemName();

  public void setQuotaType(QuotaType quotaType) ;

  public QuotaType getQuotaType() ;

  public void setBlockUsage(long blockUsage) ;

  public long getBlockUsage();

  public void setBlockHardLimit(long blockHardLimit) ;

  public long getBlockHardLimit();

  public void setBlockSoftLimit(long blockSoftLimit);

  public long getBlockSoftLimit() ;

  public void setBlockGraceTime(String blockGraceTime);

  public long getBlockGraceTime();

  public void setINodeUsage(long iNodeUsage) ;

  public long getINodeUsage() ;

  public void setINodeHardLimit(long iNodeHardLimit) ;

  public long getINodeHardLimit() ;

  public void setINodeSoftLimit(long iNodeSoftLimit);

  public long getINodeSoftLimit() ;

  public void setINodeGraceTime(String iNodeGraceTime) ;

  public long getINodeGraceTime();

  public void setSizeUnit(String sizeUnit) ;

  public SizeUnit getSizeUnit() ;

  public void setINodeTimeUnit(String timeUnit) ;

  public TimeUnit getINodeTimeUnit();

  public void setSizeTimeUnit(String timeUnit) ;

  public TimeUnit getSizeTimeUnit();


  //*** BUSINESS METHOD **//
  public void build(String output) throws QuotaException;


}
