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

package it.grid.storm.persistence.model;

import it.grid.storm.persistence.exceptions.InvalidReducedPtGChunkDataAttributesException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a ReducedPrepareToGetChunkData, that is part of a multifile PrepareToGet
 * srm request. It is closely related to PtGChunkData but it is called Reduced because it only
 * contains the fromSURL, the current TReturnStatus, and the primary key of the request.
 * 
 * This class is intended to be used by srmReleaseFiles, where only a limited amunt of information
 * is needed instead of full blown PtGChunkData.
 * 
 * @author EGRID - ICTP Trieste
 * @date November, 2006
 * @version 1.0
 */
public class ReducedPtGChunkData implements ReducedChunkData {

  @SuppressWarnings("unused")
  private static final Logger log = LoggerFactory.getLogger(ReducedPtGChunkData.class);

  private long primaryKey = -1; // long representing the primary key for the
                                // persistence layer!
  private TSURL fromSURL; // SURL that the srm command wants to get
  private TReturnStatus status; // return status for this chunk of request

  public ReducedPtGChunkData(TSURL fromSURL, TReturnStatus status)
      throws InvalidReducedPtGChunkDataAttributesException {

    if (status == null || fromSURL == null) {
      throw new InvalidReducedPtGChunkDataAttributesException(fromSURL, status);
    }
    this.fromSURL = fromSURL;
    this.status = status;
  }

  /**
   * Method that returns the fromSURL of the srm request to which this chunk belongs.
   */
  public TSURL fromSURL() {

    return fromSURL;
  }

  @Override
  public int hashCode() {

    int hash = 17;
    hash = 37 * hash + new Long(primaryKey).hashCode();
    hash = 37 * hash + fromSURL.hashCode();
    hash = 37 * hash + status.hashCode();
    return hash;
  }

  public boolean isPinned() {

    if (status.getStatusCode() == TStatusCode.SRM_FILE_PINNED) {
      return true;
    }
    return false;
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
   * Method that returns the status for this chunk of the srm request.
   */
  public TReturnStatus status() {

    return status;
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append("ReducedPtGChunkData\n");
    sb.append("primaryKey=");
    sb.append(primaryKey);
    sb.append("; ");
    sb.append("fromSURL=");
    sb.append(fromSURL);
    sb.append("; ");
    sb.append("status=");
    sb.append(status);
    sb.append(".");
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {

    if (o == this) {
      return true;
    }
    if (!(o instanceof ReducedPtGChunkData)) {
      return false;
    }
    ReducedPtGChunkData cd = (ReducedPtGChunkData) o;
    return (primaryKey == cd.primaryKey) && fromSURL.equals(cd.fromSURL)
        && status.equals(cd.status);
  }
}
