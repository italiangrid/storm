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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.persistence.exceptions.InvalidBoLDataAttributesException;
import it.grid.storm.persistence.exceptions.InvalidFileTransferDataAttributesException;
import it.grid.storm.persistence.exceptions.InvalidSurlRequestDataAttributesException;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TTURL;

/**
 * This class represents a BringOnLineChunkData, that is part of a multifile BringOnLine srm
 * request. It contains data about: the requestToken, the fromSURL, the requested lifeTime of
 * pinning, the TDirOption which tells whether the requested SURL is a directory and if it must be
 * recursed at all levels, as well as the desired number of levels to recurse, the desired
 * transferProtocols in order of preference, the fileSize, and the transferURL for the supplied
 * SURL.
 * 
 * @author CNAF
 * @version 1.0
 * @date Aug 2009
 */
public class BoLData extends AnonymousFileTransferData {

  private static final Logger log = LoggerFactory.getLogger(BoLData.class);

  /**
   * requested lifetime of TURL: it is the pin time!
   */
  private TLifeTimeInSeconds lifeTime;

  /**
   * specifies if the request regards a directory and related info
   */
  private TDirOption dirOption;

  /**
   * size of file
   */
  private TSizeInBytes fileSize;

  /**
   * how many seconds to wait before to make the lifeTime start consuming
   */
  private int deferredStartTime = 0;

  public BoLData(TSURL fromSURL, TLifeTimeInSeconds lifeTime, TDirOption dirOption,
      TURLPrefix desiredProtocols, TSizeInBytes fileSize, TReturnStatus status, TTURL transferURL,
      int deferredStartTime) throws InvalidFileTransferDataAttributesException,
      InvalidBoLDataAttributesException, InvalidSurlRequestDataAttributesException {

    super(fromSURL, desiredProtocols, status, transferURL);
    if (lifeTime == null || dirOption == null || fileSize == null) {
      throw new InvalidBoLDataAttributesException(fromSURL, lifeTime, dirOption, desiredProtocols,
          fileSize, status, transferURL);
    }
    this.lifeTime = lifeTime;
    this.dirOption = dirOption;
    this.fileSize = fileSize;
    this.deferredStartTime = deferredStartTime;
  }

  public int getDeferredStartTime() {

    return deferredStartTime;
  }

  /**
   * Method that returns the dirOption specified in the srm request.
   */
  public TDirOption getDirOption() {

    return dirOption;
  }

  /**
   * Method that returns the file size for this chunk of the srm request.
   */
  public TSizeInBytes getFileSize() {

    return fileSize;
  }

  /**
   * Method that returns the requested pin life time for this chunk of the srm request.
   */
  public TLifeTimeInSeconds getLifeTime() {

    return lifeTime;
  }

  public void setDeferredStartTime(int deferredStartTime) {

    this.deferredStartTime = deferredStartTime;
  }

  /**
   * Method used to set the size of the file corresponding to the requested SURL. If the supplied
   * TSizeInByte is null, then nothing gets set!
   */
  public void setFileSize(TSizeInBytes size) {

    if (size != null) {
      fileSize = size;
    }
  }

  public void setLifeTime(long lifeTimeInSeconds) {

    TLifeTimeInSeconds lifeTime;
    try {
      lifeTime = TLifeTimeInSeconds.make(lifeTimeInSeconds, TimeUnit.SECONDS);
    } catch (IllegalArgumentException e) {
      log.error(e.getMessage(), e);
      return;
    }

    this.lifeTime = lifeTime;
  }

}
