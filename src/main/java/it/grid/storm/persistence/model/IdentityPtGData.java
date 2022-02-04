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

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.exceptions.InvalidFileTransferDataAttributesException;
import it.grid.storm.persistence.exceptions.InvalidPtGDataAttributesException;
import it.grid.storm.persistence.exceptions.InvalidSurlRequestDataAttributesException;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.synchcall.data.IdentityInputData;

public class IdentityPtGData extends AnonymousPtGData implements IdentityInputData {

  private final GridUserInterface auth;

  /**
   * @param requestToken
   * @param fromSURL
   * @param lifeTime
   * @param dirOption
   * @param desiredProtocols
   * @param fileSize
   * @param status
   * @param transferURL
   * @throws InvalidPtGDataAttributesException
   */
  public IdentityPtGData(GridUserInterface auth, TSURL SURL, TLifeTimeInSeconds lifeTime,
      TDirOption dirOption, TURLPrefix desiredProtocols, TSizeInBytes fileSize,
      TReturnStatus status, TTURL transferURL)
      throws InvalidPtGDataAttributesException, InvalidFileTransferDataAttributesException,
      InvalidSurlRequestDataAttributesException, IllegalArgumentException {

    super(SURL, lifeTime, dirOption, desiredProtocols, fileSize, status, transferURL);
    if (auth == null) {
      throw new IllegalArgumentException(
          "Unable to create the object, invalid arguments: auth=" + auth);
    }
    this.auth = auth;
  }

  @Override
  public GridUserInterface getUser() {

    return auth;
  }

  @Override
  public String getPrincipal() {

    return this.auth.getDn();
  }
}
