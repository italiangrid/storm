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

/**
 * This class represents the AbortRequest Output Data associated with the SRM
 * request AbortRequest
 * 
 * @author Magnoni Luca
 * @author CNAF -INFN Bologna
 * @date Dec 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.TReturnStatus;

public class AbortRequestOutputData extends AbortGeneralOutputData {

  private TReturnStatus returnStatus = null;

  public AbortRequestOutputData() {

  }

  public AbortRequestOutputData(TReturnStatus retStatus) {

    this.returnStatus = retStatus;
  }

  public static AbortRequestOutputData make(AbortGeneralOutputData generalOutData) {

    return new AbortRequestOutputData(generalOutData.getReturnStatus());
  }

  /**
   * Returns the returnStatus field
   * 
   * @return TReturnStatus
   */
  public TReturnStatus getReturnStatus() {

    return returnStatus;
  }

  /**
   * Set the returnStatus field
   * 
   * @param returnStatus
   */
  public void setReturnStatus(TReturnStatus returnStatus) {

    this.returnStatus = returnStatus;
  }

  public boolean isSuccess() {

    // TODO Auto-generated method stub
    return false;
  }

}
